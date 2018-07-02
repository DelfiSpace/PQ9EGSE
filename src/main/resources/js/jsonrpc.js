function jsonRPC(address)
{
    // reconnect timeout in ms
    this.autoReconnectInterval = 5*1000;
    // server address: stored to allow automatic reconnection
    this.endpoint = address;
    // disable error and close messages during reconnection
    this.reconnectInProgress = false;

    jsonRPC.prototype.open = function()
    {
        this.myWebSocket = new WebSocket(this.endpoint);

        var that = this;
        this.myWebSocket.onopen = function(evt) 
        {
            that.reconnectInProgress = false;
            that.openHandler(evt);
        };

        this.myWebSocket.onerror = function(evt) 
        {
            that.internalErrorHandler(evt);        
        };

        this.myWebSocket.onclose = function(evt) 
        {
            that.internalCloseHandler(evt);        
        };

        this.myWebSocket.onmessage = function(evt) 
        {
            try 
            {
                var jsonObj = JSON.parse(evt.data);

                if ((typeof jsonObj["command"] !== 'undefined') && (typeof jsonObj["data"] !== 'undefined') )
                {
                    that.messageHandler(jsonObj["command"], jsonObj["data"]);
                }
            } catch (e) 
            {
                that.errorHandler("Invalid message: " + evt.data);
            }            
        };
    }

    jsonRPC.prototype.onopen =  function (f)
    {
        jsonRPC.prototype.openHandler = f;
    }

    jsonRPC.prototype.onmessage =  function (f)
    {
        jsonRPC.prototype.messageHandler = f;
    }

    jsonRPC.prototype.onclose =  function (f)
    {
        jsonRPC.prototype.closeHandler = f;
    }

    jsonRPC.prototype.onerror =  function (f)
    {
        jsonRPC.prototype.errorHandler = f;
    }

    // default open handler
    jsonRPC.prototype.openHandler = function (evt)
    {
        console.log("jsonRPC.onopen.");
    }

    // default message handler
    jsonRPC.prototype.messageHandler = function (data)
    {
        console.log("jsonRPC.messageHandler: " + data);
    }

    // close handler managing reconnection
    jsonRPC.prototype.internalCloseHandler = function (evt)
    {      
        if (this.reconnectInProgress == false)
        {
            // call the close handler 
            jsonRPC.prototype.closeHandler(evt);
        }
        switch (evt.code)
        {
            case 1000:	// CLOSE_NORMAL
                break;
            default:	// Abnormal closure
                // try to reconnect
                this.reconnect(evt);
                break;
        }        
    }

    jsonRPC.prototype.closeHandler = function (evt)
    {
        console.log("jsonRPC.closeHandler: " + evt);
    }

    jsonRPC.prototype.errorHandler = function (err)
    {
        console.log("jsonRPC.errorHandler: " + err);
    }

    jsonRPC.prototype.internalErrorHandler = function (evt)
    {
        switch (evt.code)
        {
            case 'ECONNREFUSED':
                this.reconnect(evt);
                break;
            default:
                if (this.reconnectInProgress == false)
                {
                    if (evt.code == undefined)
                    {
                        this.errorHandler("undefined");
                    }
                    else
                    {
                        this.errorHandler(evt.code);
                    }
                    break;
                }
        }        
    }

    jsonRPC.prototype.reconnect = function(evt)
    {
        this.reconnectInProgress = true;
        var that = this;
        setTimeout(function()
        {            
            that.open();
        }, this.autoReconnectInterval);
    }

    jsonRPC.prototype.send = function (command, data)
    {
        if(this.myWebSocket.readyState === this.myWebSocket.OPEN)
        {
            var obj = {}
            obj["command"] = command;
            obj["data"] = data;
            var s = JSON.stringify(obj);
            this.myWebSocket.send(s);
        }
        else
        {
            this.errorHandler("Send failure, connection lost...");
        }
    }
}
   