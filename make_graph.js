var Rickshaw = require('rickshaw')

// function to setup a new graph
// returns a rickshaw graph
module.exports = function (chart_el, legend_el, series_data) { 
  // color pallette
  var palette = new Rickshaw.Color.Palette( { scheme: 'munin' } )
  // graph
  var graph = new Rickshaw.Graph({
    element: chart_el,
    width: 600,
    height: 150,
    renderer: "line",
    //preserve: true,
    //stroke: true,
    series: _.map(series_data, (arr, name) => {
      return {
        color: palette.color(),
        data: arr,
        name: name
      }
    })
  })
  // legend
  var legend = new Rickshaw.Graph.Legend( {
    graph: graph,
    element: legend_el,
  
  })
  // hover detail
  var hoverDetail = new Rickshaw.Graph.HoverDetail( {
    graph: graph,
    xFormatter: x => x
  })
  // render graph
  graph.render()
  
  // x axis
  new Rickshaw.Graph.Axis.Time( {
    graph: graph,
    ticksTreatment: 'glow',
    timeFixture: new Rickshaw.Fixtures.Time
  }).render()
  
  // y axis
  new Rickshaw.Graph.Axis.Y( {
    graph: graph,
    tickFormat: Rickshaw.Fixtures.Number.formatKMBT,
    ticksTreatment: 'glow',
  }).render()
  
  // highlighter
  new Rickshaw.Graph.Behavior.Series.Highlight( {
    graph: graph,
    legend: legend
  })
  return graph
}
