// save the layout changes for next session


// Create A Layout using GoldenLayout  https://golden-layout.com/tutorials/getting-started.html
var config = {
  settings:{
	  hasHeaders: true,        
	  showCloseIcon: false,
	  showPopoutIcon: true,
	  showMaximiseIcon: true
	},
  content: [
    {
      type: "column",
      content: [
        {
			type: "component",
			componentName: "LogTable",
			isClosable: false,
			id: 'log'
        },
        {
			type: "component",
			componentName: "EventLog",
			isClosable: false,
			height: 15,
			id: 'log'
        }
    ]}
]};
myLayout = new GoldenLayout( config );
myLayout.registerComponent("EventLog", function(container, componentState) 
{
  container.getElement().html("<div id=\"log\"></div>");
});
myLayout.registerComponent("LogTable", function(container, componentState) 
{
  container.getElement().html("<div><table id=\"logTable\" class=\"cell-border compact display\" width=\"100%\"></table></div>");
});

myLayout.init();


const rpc = new jsonRPC("ws://" + location.host + "/wss");
rpc.onopen( function(evt)
{
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
            appendToDiv("log", "LOGGING:"+data);
            break;

        case "downlink":
            appendToDiv("log", "DOWNLINK:"+data);
			cmdToTable(data)
            break;
			
		default:
            appendToDiv("log", myGetTime() + "Invalid command \"" + command + "\": " + data);
			break;
    }
});
rpc.open();

function appendToDiv(id, data)
{
    // make sure the graphical elements have been created correctly, 
    // otherwise erase the stored configuration and reload the page
    if (document.getElementById(id) === null)
    {
        resetLayout();
    }
    var objDiv = document.getElementById(id).parentNode;    
	var objDiv2 = document.getElementById(id)
    var delta = objDiv.offsetHeight + objDiv.scrollTop;
    var delta1 = objDiv.scrollHeight;
       
    document.getElementById(id).innerHTML += data + "<br>\n" + "<br>\n";

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

function cmdToTable(data)
{
	var t = $('#logTable').DataTable();
			
	if (JSON.parse(data)['_received_'] != null){
		var rowNode = t.row.add([
			JSON.parse(data)['_timestamp_'],
			JSON.parse(JSON.parse(data)['Source']).value,
			JSON.parse(JSON.parse(data)['Destination']).value,
			JSON.parse(data)['_received_'],
			JSON.parse(JSON.parse(data)['Request']).value,
			JSON.parse(data)['_raw_']
			]).draw().node();
			 
		if (JSON.parse(JSON.parse(data)['Request']).value == "Request") {
		$( rowNode )
		.addClass( "requestRow" )
		}else if (JSON.parse(JSON.parse(data)['Request']).value == "Reply") {
		$( rowNode )
		.addClass( "replyRow" )
		}else if (JSON.parse(JSON.parse(data)['Request']).value == "Error") {
		$( rowNode )
		.addClass( "errorRow" )
		}
	}else{
		let rawFrame = JSON.parse(JSON.parse(data)['_raw_']);
		var rowNode = t.row.add([
			JSON.parse(data)['_timestamp_'],
			rawFrame[2],
			rawFrame[0],
			"UNKNOWN",
			"",
			JSON.parse(data)['_raw_']
			]).draw().node();
		$( rowNode )
		.addClass( "unknownRow" )
	}
	
	var objDiv = document.getElementById('logTable').parentNode.parentNode.parentNode;   
	objDiv.scrollTop = objDiv.scrollHeight;
	t.page( 'last' ).draw( 'page' );
}
 function resetLayout() 
{
    // clear the saved layout state
    localStorage.removeItem('layoutSavedState');
    location.reload(true);
}

$(document).ready(function() {
	console.log("Hello world!");
	//initTable()
    $('#logTable').DataTable( {
		"autoWidth": true,
		paging: true,
		pageLength: 20,
		ordering: false,
		searching: false,
		info: false,
        columns: [
            { title: "TimeStamp" },
            { title: "Src." },
            { title: "Dest." },
            { title: "Type" },
            { title: "Request" },
            { title: "RAW" }
        ],
		"columnDefs": [
            {
                "targets": [ 5 ],
                "visible": false,
                "searchable": false
            }
        ]
    } );
} );
