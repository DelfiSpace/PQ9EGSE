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
  container.getElement().html("<div id=\"uplink\"></div>");
});
myLayout.registerComponent("EventLog", function(container, componentState) 
{
  container.getElement().html("<div id=\"log\"></div>");
});
myLayout.init();

console.log("test");
connectToWS("ws://" + location.host + "/wss");


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