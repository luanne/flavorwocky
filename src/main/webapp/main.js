//
//  main.js
//
//  A project template for using arbor.js
//

(function($){

  var Renderer = function(canvas){
    var canvas = $(canvas).get(0)
    var ctx = canvas.getContext("2d");
	var gfx = arbor.Graphics(canvas);
	
    var particleSystem

    var that = {
      init:function(system){
        //
        // the particle system will call the init function once, right before the
        // first frame is to be drawn. it's a good place to set up the canvas and
        // to pass the canvas size to the particle system
        //
        // save a reference to the particle system for use in the .redraw() loop
        particleSystem = system

        // inform the system of the screen dimensions so it can map coords for us.
        // if the canvas is ever resized, screenSize should be called again with
        // the new dimensions
        particleSystem.screenSize(canvas.width, canvas.height) 
        particleSystem.screenPadding(80) // leave an extra 80px of whitespace per side
        
        // set up some event handlers to allow for node-dragging
        that.initMouseHandling()
      },
      
      redraw:function(){
        // 
        // redraw will be called repeatedly during the run whenever the node positions
        // change. the new positions for the nodes can be accessed by looking at the
        // .p attribute of a given node. however the p.x & p.y values are in the coordinates
        // of the particle system rather than the screen. you can either map them to
        // the screen yourself, or use the convenience iterators .eachNode (and .eachEdge)
        // which allow you to step through the actual node objects but also pass an
        // x,y point in the screen's coordinate system
        // 
        ctx.fillStyle = "green"
        ctx.fillRect(0,0, canvas.width, canvas.height)
        
        particleSystem.eachEdge(function(edge, pt1, pt2){
          // edge: {source:Node, target:Node, length:#, data:{}}
          // pt1:  {x:#, y:#}  source position in screen coords
          // pt2:  {x:#, y:#}  target position in screen coords

          // draw a line from pt1 to pt2
          ctx.strokeStyle = "rgba(0,0,0, .333)"
          ctx.lineWidth = 1
          ctx.beginPath()
          ctx.moveTo(pt1.x, pt1.y)
          ctx.lineTo(pt2.x, pt2.y)
          ctx.stroke()
        })

        particleSystem.eachNode(function(node, pt){
          // node: {mass:#, p:{x,y}, name:"", data:{}}
          // pt:   {x:#, y:#}  node position in screen coords

var nodeName = node.name
var w = Math.max(20, 20+gfx.textWidth(nodeName) )
if (node.data.alpha===0) return
  if (node.data.shape=='dot'){
      gfx.oval(pt.x-w/2, pt.y-w/2, w, w, {fill:node.data.color, alpha:node.data.alpha})
      gfx.text(nodeName, pt.x, pt.y+7, {color:"white", align:"center", font:"Arial", size:12})
      gfx.text(nodeName, pt.x, pt.y+7, {color:"white", align:"center", font:"Arial", size:12})
  }else{
      gfx.rect(pt.x-w/2, pt.y-8, w, 20, 4, {fill:node.data.color, alpha:node.data.alpha})
      gfx.text(nodeName, pt.x, pt.y+9, {color:"white", align:"center", font:"Arial", size:12})
      gfx.text(nodeName, pt.x, pt.y+9, {color:"white", align:"center", font:"Arial", size:12})
	  }

		  })    			
      },
      
      initMouseHandling:function(){
        // no-nonsense drag and drop (thanks springy.js)
        var dragged = null;

        // set up a handler object that will initially listen for mousedowns then
        // for moves and mouseups while dragging
        var handler = {
          clicked:function(e){
            var pos = $(canvas).offset();
            _mouseP = arbor.Point(e.pageX-pos.left, e.pageY-pos.top)
            dragged = particleSystem.nearest(_mouseP);

            if (dragged && dragged.node !== null){
              // while we're dragging, don't let physics move the node
              dragged.node.fixed = true
            }

            $(canvas).bind('mousemove', handler.dragged)
            $(window).bind('mouseup', handler.dropped)

            return false
          },
          dragged:function(e){
            var pos = $(canvas).offset();
            var s = arbor.Point(e.pageX-pos.left, e.pageY-pos.top)

            if (dragged && dragged.node !== null){
              var p = particleSystem.fromScreen(s)
              dragged.node.p = p
            }

            return false
          },

          dropped:function(e){
            if (dragged===null || dragged.node===undefined) return
            if (dragged.node !== null) dragged.node.fixed = false
            dragged.node.tempMass = 1000
            dragged = null
            $(canvas).unbind('mousemove', handler.dragged)
            $(window).unbind('mouseup', handler.dropped)
            _mouseP = null
            return false
          }
        }
        
        // start listening
        $(canvas).mousedown(handler.clicked);

      },
      
    }
    return that
  }    

  var sys
  $(document).ready(function(){
    sys = arbor.ParticleSystem(500, 600, 0.5) // create the system with sensible repulsion/stiffness/friction
    sys.parameters({gravity:true}) // use center-gravity to make the graph settle nicely (ymmv)
    sys.renderer = Renderer("#viewport") // our newly created renderer will have its .init() method called shortly by sys...

    // add some nodes to the graph and watch it go...
    sys.addEdge('a','b')
    sys.addEdge('a','c')
    sys.addEdge('a','d')
    sys.addEdge('a','e')
    sys.addNode('f', {alone:true, mass:.25})

    // or, equivalently:
    //
    // sys.graft({
    //   nodes:{
    //     f:{alone:true, mass:.25}
    //   }, 
    //   edges:{
    //     a:{ b:{},
    //         c:{},
    //         d:{},
    //         e:{}
    //     }
    //   }
    // })
    
	$.ajax({
		accepts: "application/json",
		contentType: "application/json"
	});
	
	$('#init').click(init);
	$('#addNode').click(addNode);
	$('#runQuery').click(runQuery);
  })

	function handleError(jqXHR, textStatus, errorThrown) {
		alert('jqXHR = '+jqXHR);
		alert('textStatus = '+textStatus);
		alert('errorThrown = '+errorThrown);
	}
	
	function handleSuccessInit(data, textStatus, jqXHR) {
		alert('data = '+data.neo4j_version);
		$('#info ul').append('<li><b>version</b>: '+data.neo4j_version+'</li>');
	}
	
	function init() {
			$.ajax('http://5afd95982.hosted.neo4j.org:7024/db/data/', {error: handleError, success: handleSuccessInit});
	}
	
	function createNode() {
			$.ajax('http://localhost:7474/db/data/node', {type: 'POST', error: handleError, success: handleSuccessCreate});
	}

	function handleSuccessCreate(data, textStatus, jqXHR) {
		alert('Createed node');
	}

	function handleSuccessQuery(data, textStatus, jqXHR) {
			
			console.log(data);
			
			$.each (data.data, function(index, value){
				console.log(value);
				//alert(value[0].data.name);
				sys.addNode(value[0].data.name)
			});
	}
	
	function runQuery(query) {
		var qry = $('#qry').val();
		if (qry) {
			$.ajax('http://localhost:7474/db/data/cypher', {error: handleError, 
								success: handleSuccessQuery,
								dataType: 'json',
								data: {query: qry},
								type: 'POST'});
		} else {
			alert('Enter a Cypher query');
		}		
	}

	function addNode() {
		sys.addNode('GG', {alone:true, mass:.25})
	}

})(this.jQuery)