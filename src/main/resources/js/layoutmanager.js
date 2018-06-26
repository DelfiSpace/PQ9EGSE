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

if( savedState !== null ) {
    myLayout = new GoldenLayout( JSON.parse( savedState ) );
} else {
    myLayout = new GoldenLayout( config );
}


myLayout.registerComponent("Downlink", function(container, componentState) 
{
  container.getElement().html("<h2>" + componentState.label + "</h2>");
});
myLayout.registerComponent("Uplink", function(container, componentState) 
{
  container.getElement().html("<h2>" + componentState.label + "</h2>");
});
myLayout.registerComponent("EventLog", function(container, componentState) 
{
  container.getElement().html("<h2> Log Window </h2>");
});
myLayout.init();

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