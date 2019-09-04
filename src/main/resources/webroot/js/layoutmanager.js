var config = {
  settings:{
      hasHeaders: true,        
      showCloseIcon: false
    },
  content: [
    {
      type: "column",
      content: [
        {   
          type: "component",
          componentName: "Header",
          componentState: { label: "H" },
          isClosable: false,
          height: 15
        },
        {
          type: "row",
      content: [
        {
          type: "component",
          componentName: "DataLog",
          componentState: { label: "A" },
          isClosable: false,
          height: 60
        },
        {
          type: "component",
          componentName: "Uplink",
          componentState: { label: "A" },
          isClosable: false,
          height: 60
        }]},
        {
          type: "component",
          componentName: "EventLog",
          componentState: { label: "C" },
          isClosable: false,
          height: 25
        }
      ]
    }
  ]
};
 var loadOnce = true;

savedState = localStorage.getItem( 'layoutSavedState' );

if( savedState !== null ) 
{
    myLayout = new GoldenLayout( JSON.parse( savedState ) );
} else 
{
    myLayout = new GoldenLayout( config );
}

myLayout.registerComponent("Header", function(container, componentState) 
{
  container.getElement().html("<div id=\"header\"></div>");
});
myLayout.registerComponent("DataLog", function(container, componentState) 
{
  container.getElement().html("<div id=\"datalog\"></div>");
});
myLayout.registerComponent("Uplink", function(container, componentState) 
{
  container.getElement().html("<div id=\"uplink\"></div>");
});
myLayout.registerComponent("EventLog", function(container, componentState) 
{
  container.getElement().html("<div id=\"log\"></div>");
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
