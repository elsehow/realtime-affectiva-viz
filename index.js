var chunky = require('chunky-webcam')
  , _ = require('lodash')
  , getUserMedia = require('getusermedia')
  , attachMediaStream = require('attachmediastream')
  , el = id => document.getElementById(id)
  // html elements
  , video_el = el('webcam')
  , expressions_chart_el = el('expressions-chart')
  , expressions_legend_el = el('expressions-legend')
  , emotions_chart_el = el('emotions-chart')
  , emotions_legend_el = el('emotions-legend')
  , error_el = el('error')


// config
var resolution = 3500
  , aff_api_endpoint = 'https://verdigris.ischool.berkeley.edu:3333'
  , video_err_message =  "I can't see your face! :c try moving to a better-lit place, and make sure your face fits in the frame. dont blame us!! this SDK is pretty picky."

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

function mutate_store (key, max, new_data, i) {
  //var d = new Date(new_data['date and time'])
  _.forEach(store[key], (arr, exp) => {
    var y = new_data[key][exp]
    var l = store[key][exp]
    l.push({
      x: i,
      y: y,
    })
    if (l.length > max)
      l.shift()
  }) 
}

var make_graph = require('./make_graph')

var emotions_graph = make_graph(
  emotions_chart_el, 
  emotions_legend_el,
  store.emotions
)

var expressions_graph = make_graph(
  expressions_chart_el, 
  expressions_legend_el,
  store.expressions
)


function handle (data) {
  // clear error message, if any
  error_el.innerHTML = ""
  // functions for mutating app state
  var m_em = _.partial(
    mutate_store, 
    'emotions', 
    data.length
  )
  var m_ex = _.partial(
    mutate_store, 
    'expressions', 
    data.length
  )
  //mutate app state + update graphs
  data.forEach(m_em)
  emotions_graph.update()
  data.forEach(m_ex)
  expressions_graph.update()
  return
}


function handle_video_error (err) {
  error_el.innerHTML = video_err_message
}



// app entrypoint
getUserMedia({ video: true, audio: false}, (err, stream) => {

  if (err)
    document.write('failed getting your webcam!', err)
  
  attachMediaStream(stream,video_el, {
    muted: true,
    mirror: true,
  })
  
  chunk = chunky(stream, resolution, aff_api_endpoint)

  chunk.socket.on('data', handle)
  chunk.socket.on('video-error', handle_video_error) 

})

