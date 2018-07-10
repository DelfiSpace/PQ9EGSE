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
          type: "row",
      content: [
        {
          type: "component",
          componentName: "Downlink",
          componentState: { label: "A" }
        },
        {
          type: "component",
          componentName: "Uplink",
          componentState: { label: "A" }
        }]},
        {
          type: "component",
          componentName: "EventLog",
          componentState: { label: "C" }
        }
      ]
    }
  ]
};

savedState = localStorage.getItem( 'layoutSavedState' );

if( savedState !== null ) 
{
    myLayout = new GoldenLayout( JSON.parse( savedState ) );
} else 
{
    myLayout = new GoldenLayout( config );
}


myLayout.registerComponent("Downlink", function(container, componentState) 
{
  container.getElement().html("<div id=\"downlink\"></div>");
});
myLayout.registerComponent("Uplink", function(container, componentState) 
{
    var html = [];
    html.push(
        "<div id=\"uplink\">",
        "<button type=\"button\" id=\"send1\" onclick=\"handleSend(this.id)\">Send1</button>",
        "<br/>",
        "<button type=\"button\" id=\"send2\" onclick=\"handleSend(this.id)\">Send2</button>",
        "<br/>",
        "<button type=\"button\" id=\"send3\" onclick=\"handleSend(this.id)\">Send3</button>",
        "<br/>",
        "</div>"
    );

  container.getElement().html(html.join(""));
});
myLayout.registerComponent("EventLog", function(container, componentState) 
{
  container.getElement().html("<div id=\"log\"></div>");
});
myLayout.init();

var rpc = new jsonRPC("ws://" + location.host + "/wss");

rpc.onopen( function(evt)
{
    updateDiv("log", myGetTime() + " Connection established.");
});

rpc.onclose( function(evt)
{
    updateDiv("log", myGetTime() + " Connection lost, trying to reconnect...");
});

rpc.onerror(function(error)
{
    updateDiv("log", myGetTime() + " Communication error: " + error);
});

rpc.onmessage( function(command, data)
{
    switch(command) 
    {
        case "log":
            updateDiv("log", myGetTime() + data);
            break;

        case "downlink":
            updateDiv("downlink", myGetTime() + data);
            break;

        case "uplink":
            document.getElementById("uplink").innerHTML = data;
            break;

        default:
            updateDiv("log", myGetTime() + "Invalid command \"" + command + "\": " + data);
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
    for(var i = 0; i < elm.length; i++)
    {   
        obj[elm[i]] = document.getElementById(elm[i]).value;
    } 
    var s = JSON.stringify(obj);
    rpc.send(id, s);
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

function updateDiv(id, data)
{
    var objDiv = document.getElementById(id).parentNode;
    var delta = objDiv.offsetHeight + objDiv.scrollTop;
    var delta1 = objDiv.scrollHeight;
       
    document.getElementById(id).innerHTML += data + "<br/>\n";

    if (delta == delta1)
    {
        document.getElementById(id).scrollIntoView(false);
    }
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