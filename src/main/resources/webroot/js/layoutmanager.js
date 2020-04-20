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
          id: 'log'
        }
    ]}
]};

var newItemConfig = {
    type: 'component',
    componentName: 'Downlink GUI',
    id: "downlinkgui"
};

var loadOnce = true;

// check if the current layout configuration 
// version corresponds to the stored one
configVersion = localStorage.getItem( 'configVersion' );
hash = hashCode(JSON.stringify(config));
if ((configVersion !== null) && (configVersion !== hash))
{
    // version mismatch, to prevent issues clean the current configuration
    localStorage.removeItem('layoutSavedState');
}
// update the stored configuration vrsion
localStorage.setItem( 'configVersion', hash );

// load the stored layout configuration
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
  container.getElement().html("<div id=\"downlinkgui\"></div>");
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
    if ((component.config.id === "header") || 
        (component.config.id === "downlinkTitle") || 
        (component.config.id === "uplinkTitle"))
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
    if (loadOnce === true)
    {
        rpc.send("uplink", "");
        rpc.send("downlinkgui", "");
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

        case "downlink":
            dataToGUI(data);
            break;

        case "downlinkgui":
            setDiv("downlinkgui", data);
            //myLayout.root.getItemsById( 'downlink' )[0].addChild(newItemConfig );
            //myLayout.root.getItemsById( 'downlinkgui' )[0].html("<div id=\"downlinkgui\"></div>");
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
    obj['_send_'] = id.split(":")[1];
    for(var i = 0; i < elm.length; i++)
    {   
        obj[elm[i].split(":")[2]] = document.getElementById(elm[i]).value;
    } 
    var s = JSON.stringify(obj);
    rpc.send('SendCommand', s);
}

function dataToGUI(data)
{  
    var elements = JSON.parse(data);
    var packet = elements['_received_'];

    // find the last received message and mark it as outdated
    changeClass("updatedFrame", "outdatedFrame");
 
    // find invalid values and change color properties
    changeClass("invalidValue", "valuesColumn");

    for (var elm in elements)
    {
        if (elm.substring(0, 1) !== "_") 
        {
            values = JSON.parse(elements[elm]);
            if (values['valid'] === "false")
            {
                document.getElementById('Downlink:' + packet + ":" + elm).className = "invalidValue";
                document.getElementById('Downlink:' + packet + ":" + elm).innerHTML = 
                        "<strike title=\"Value outside the valid range\">" + values['value'] + "</strike>";
            }
            else
            {
                document.getElementById('Downlink:' + packet + ":" + elm).innerHTML = values['value'];
            }            
        }
    }
    var timestamp =  new Date(Date.parse(elements['_timestamp_']));    
    var formatted_date = timestamp.getHours() + ":" + timestamp.getMinutes() + ":" 
            + timestamp.getSeconds() + "." + timestamp.getMilliseconds();    
    document.getElementById('Downlink:' + packet + ":_timestamp_").innerHTML = formatted_date;
    document.getElementById('Downlink:' + packet).className = "updatedFrame";
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

    if (delta === delta1)
    {
        document.getElementById(id).scrollIntoView(false);
    }
	
	if (id == "log"){
		var divHeight = document.getElementById(id).offsetHeight;
		var lineHeight = document.getElementById(id).style.lineHeight;
		if (lineHeight == ""){
			lineHeight = 36;
		}
		var linecount = divHeight/lineHeight
		
		while (linecount > 50){
			var str = document.getElementById(id).innerHTML;
			var n = str.search("<br>\n");
			str = str.substring(n+10);
			document.getElementById(id).innerHTML = str;
			
			var divHeight = document.getElementById(id).offsetHeight;
			var lineHeight = document.getElementById(id).style.lineHeight;
			if (lineHeight == ""){
				lineHeight = 36;
			}
			var linecount = divHeight/lineHeight
		
		}
		//console.log(linecount);
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

function setMode()
{
    rpc.send("setMode", document.getElementById('EGSEMode').value);
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

function hashCode(s) 
{
    var h = 0, l = s.length, i = 0;
    if ( l > 0 )
    {
        while (i < l)
        {
            h = (h << 5) - h + s.charCodeAt(i++) | 0;
        }
    }
    return String(h);
}

function changeClass(oldclass, newclass)
{
    var updatedValues = document.querySelectorAll("." + oldclass);
    updatedValues.forEach(function(userItem) 
    {
        userItem.className = newclass;
    });
}
