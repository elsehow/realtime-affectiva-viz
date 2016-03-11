var chunky = require('chunky-webcam')
  , _ = require('lodash')
  , getUserMedia = require('getusermedia')
  , attachMediaStream = require('attachmediastream')
  // element in which we show video
  , video_el = document.getElementById('webcam')
  // element in which we show server messages
  , expressions_chart_el = document.getElementById('expressions-chart')
  , emotions_chart_el = document.getElementById('emotions-chart')
  , expressions_legend_div = document.getElementById('expressions-legend')
  , Rickshaw = require('rickshaw')


// config
var resolution = 3000
var aff_api_endpoint = 'http://verdigris.ischool.berkeley.edu:3334'

// mutable app store (for rickshaw)
var store = {
  expressions: {
    attention: []
    , browFurrow: []
    , chinRaise: []
    , eyeClosure: []
    , innerBrowRaise: []
    , lipCornerDepressor: []
    , lipPress: []
    , lipPucker: []
    , lipSuck: []
    , mouthOpen: []
    , noseWrinkle: []
    , smile: []
    , smirk: []
    , upperLipRaise: []
  },
  emotions: {
    anger: []
    , contempt: []
    , disgust: []
    , engagement: []
    , fear: []
    , joy: []
    , sadness: []
    , surprise: []
    , valence: []
  }
}

// setup rickshaw graph
var palette = new Rickshaw.Color.Palette( { scheme: 'spectrum2000' } )
var graph = new Rickshaw.Graph({
  element: expressions_chart_el,
  width: 600,
  height: 150,
  renderer: "line",
  //preserve: true,
  //stroke: true,
  series: _.map(store.expressions, (arr, name) => {
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
  element: expressions_legend_div,

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
  timeFixture: new Rickshaw.Fixtures.Time.Local()
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
} );


function handle (data) {
  if (data.error) 
    console.log('ERR!', data.error)
  else
    data.forEach(mutate_store)
    //console.log(graph.series)
    graph.update()
  return
}

function mutate_store (new_data, i) {
  _.forEach(store.expressions, (arr, exp) => {
    var y = new_data.expressions[exp]
    var l = store.expressions[exp]
    l.push({x: i, y: y})
    if (l.length > 100)
      l.shift()
  }) 
}



// get user webcam
getUserMedia({ video: true, audio: false}, (err, stream) => {

    // if the browser doesn't support user media 
    // or the user says "no" the error gets passed 
    // as the first argument. 
    if (err)
        document.write('failed getting your webcam!', err);

    //else
    //    console.log('got stream!', stream)

    // put the webcam stream in a video element
    attachMediaStream(stream,video_el, {
        muted: true,
        mirror: true,
    })

    // setup chunky-webcam, with our stream
    // it will record 1000ms chunks,
    // and send them with a 'video' event to 'localhost:9999'
    // where our server runs
    chunk = chunky(stream, resolution, aff_api_endpoint)

    // our server will send 'data' events
    chunk.socket.on('data', handle)
})

