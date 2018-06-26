var myWebSocket;

function connectToWS(endpoint) 
{
console.log("connect to " + endpoint);
    if (myWebSocket !== undefined) {
        myWebSocket.close()
    }

    myWebSocket = new WebSocket(endpoint);

    myWebSocket.onmessage = function(event) {
        var leng;
        if (event.data.size === undefined) {
            leng = event.data.length
        } else {
            leng = event.data.size
        }

        var objDiv = document.getElementById("log").parentNode;
        var delta = objDiv.offsetHeight + objDiv.scrollTop;
        var delta1 = objDiv.scrollHeight;

        console.log("onmessage. size: " + leng + ", content: " + event.data);
        document.getElementById("log").innerHTML += event.data + "<br/>\n";

        if (delta == delta1)
        {
            document.getElementById("log").scrollIntoView(false);
        }
    }

function getScrollXY()
{
    var x = 0, y = 0;
    if( typeof( window.pageYOffset ) == 'number' ) {
        // Netscape
        x = window.pageXOffset;
        y = window.pageYOffset;
    } else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) {
        // DOM
        x = document.body.scrollLeft;
        y = document.body.scrollTop;
    } else if( document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) {
        // IE6 standards compliant mode
        x = document.documentElement.scrollLeft;
        y = document.documentElement.scrollTop;
    }
    
   /*In Safari 7.0.1 above functions will not work to get vertical scroll top. So if its unable to get vertical scroll top then try to get    vertical scroll in jquery $(window).scrollTop() */
   if(y == 0){
    y = $(window).scrollTop();
   }
   //console('Vertical scroll position: ' + y);    
return y;
}

    myWebSocket.onopen = function(evt) {
        console.log("onopen.");
    };

    myWebSocket.onclose = function(evt) {
        console.log("onclose.");
    };

    myWebSocket.onerror = function(evt) {
        console.log("Error!");
    };
}

function sendMsg() {
    var message = document.getElementById("myMessage").value;
    myWebSocket.send(message);
}

function closeConn() {
    myWebSocket.close();
}

