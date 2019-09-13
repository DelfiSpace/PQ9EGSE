// save the layout changes for next session
var persistentLayout = true;

var config = {
  settings:{
      hasHeaders: true,        
      showCloseIcon: false,
      showPopoutIcon: false,
      showMaximiseIcon: false
    },
  content: [
    {
      type: "column",
      content: [
        {   
          type: "component",
          componentName: "Header",
          isClosable: false,
          height: 5,
          id: 'header'
        },
        {
          type: "row",
      content: [
        {
        type: "column",
            content: [
              {   
                type: "component",
                componentName: "DownlinkTitle",
                isClosable: false,
                id: 'downlinkTitle',
                height: 5
              },
              {
          type: "component",
          componentName: "Downlink",
          isClosable: false,
          height: 75,
          id: 'downlink'
        }]},
        {
        type: "column",
            content: [
              {   
                type: "component",
                componentName: "UplinkTitle",
                isClosable: false,
                height: 5,
                id: 'uplinkTitle'
              },
              {
          type: "component",
          componentName: "Uplink",
          isClosable: false,
          height: 75,
          id: 'uplink'
        }]}]},
        {
          type: "component",
          componentName: "EventLog",
          isClosable: false,
          height: 15,
          id: 'ddd',
        }
  ]
}]};
 var loadOnce = true;

savedState = localStorage.getItem( 'layoutSavedState' );

if ((persistentLayout) && ( savedState !== null ))
{
    myLayout = new GoldenLayout( JSON.parse( savedState ) );
} else 
{
    myLayout = new GoldenLayout( config );
}

myLayout.registerComponent("Header", function(container) 
{
  container.getElement().html("<div id=\"header\"></div>");
});
myLayout.registerComponent("DownlinkTitle", function(container) 
{
  container.getElement().html("<div><center>Downlink</center></div>");
});
myLayout.registerComponent("Downlink", function(container) 
{
  container.getElement().html("<div id=\"datalog\"></div>");
});
myLayout.registerComponent("UplinkTitle", function(container) 
{
  container.getElement().html("<div><center>Uplink</center></div>");
});
myLayout.registerComponent("Uplink", function(container) 
{
  container.getElement().html("<div id=\"uplink\"></div>");
});
myLayout.registerComponent("EventLog", function(container) 
{
  container.getElement().html("<div id=\"log\"></div>");
});

// remove the header from certain tabs
myLayout.on( 'componentCreated', function( component )
{
    console.log(component.config.componentName);
    if ((component.config.id == "header") || 
        (component.config.id == "downlinkTitle") || 
        (component.config.id == "uplinkTitle"))
    {
        component.tab.header.position(false);
    }
});

myLayout.init();

var rpc = new jsonRPC("ws://" + location.host + "/wss");

rpc.onopen( function(evt)
{
    // generate the uplink only the first time:
    // if the connection is lost and re-attained, do not
    // update the uplink panel
    if (loadOnce == true)
    {
        rpc.send("uplink", "");
        loadOnce = false;
    }
    appendToDiv("log", myGetTime() + " Connection established.");
});

rpc.onclose( function(evt)
{
    appendToDiv("log", myGetTime() + " Connection lost, trying to reconnect...");
});

rpc.onerror(function(error)
{
    appendToDiv("log", myGetTime() + " Communication error: " + error);
});

rpc.onmessage( function(command, data)
{
    switch(command) 
    {
        case "log":
            appendToDiv("log", data);
            break;

        case "datalog":
            appendToDiv("datalog", data);
            break;

        case "uplink":
            setDiv("uplink", data);
            break;

        case "header":
            setDiv("header", data);
            break;

        default:
            appendToDiv("log", myGetTime() + "Invalid command \"" + command + "\": " + data);
    }
});

rpc.open();

function handleSend(buttonId) 
{
    rpc.send("send", "message to be sent from " + buttonId);
};

function fetchData(id, elm)
{
    var obj = {};  
    obj['_send_'] = id;
    for(var i = 0; i < elm.length; i++)
    {   
        var e = elm[i].split(":");
        obj[elm[i].split(":")[1]] = document.getElementById(elm[i]).value;
    } 
    var s = JSON.stringify(obj);
    rpc.send('SendCommand', s);
}

function myGetTime()
{
    var html = [];
    var d = new Date();
    html.push(        
        ("0" + d.getHours()).slice(-2),
        ":",
        ("0" + d.getMinutes()).slice(-2),
        ":",
        ("0" + d.getSeconds()).slice(-2),
        ".",
        ("0" + d.getMilliseconds()).slice(-3),
        " "
    );
    return html.join("");        
}

function appendToDiv(id, data)
{
    // make sure the graphical elements have been created correctly, 
    // otherwise erase the stored configuration and reload the page
    if (document.getElementById(id) === null)
    {
        resetLayout();
    }
    var objDiv = document.getElementById(id).parentNode;    
    var delta = objDiv.offsetHeight + objDiv.scrollTop;
    var delta1 = objDiv.scrollHeight;
       
    document.getElementById(id).innerHTML += data + "<br/>\n";

    if (delta == delta1)
    {
        document.getElementById(id).scrollIntoView(false);
    }
}

function setDiv(id, data)
{
    // make sure the graphical elements have been created correctly, 
    // otherwise erase the stored configuration and reload the page
    if (document.getElementById(id) === null)
    {
        resetLayout();
    }
    document.getElementById(id).innerHTML = data;
}

myLayout.on( 'stateChanged', function()
{
    var state = JSON.stringify( myLayout.toConfig() );
    localStorage.setItem( 'layoutSavedState', state );
});

function resetLayout() 
{
    // clear the saved layout state
    localStorage.removeItem('layoutSavedState');
    location.reload(true);
}

function setSerialPort()
{
    rpc.send("setSerialPort", document.getElementById('serialPort').value);
}

function reloadXTCEFile()
{
    rpc.send("reloadXTCEFile", "");
}

function reloadSerialPorts()
{
    rpc.send("reloadSerialPorts", "");
}
