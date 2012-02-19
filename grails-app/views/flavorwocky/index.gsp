<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:javascript library="jquery" />
		<script type="text/javascript" src="${resource(dir: 'js', file: 'd3.js')}"></script>
        <script type="text/javascript" src="${resource(dir: 'js', file: 'd3.layout.js')}"></script>
        <style type="text/css">

                .node circle {
                cursor: pointer;
                fill: #fff;
                stroke: steelblue;
                stroke-width: 1.5px;
                }

                .node text {
                font: 10px sans-serif;
                }

                path.link {
                fill: none;
                stroke: #ccc;
                stroke-width: 2.0px;
                }

            </style>
	</head>
	<body>
        <p>
            <button id="addPairing">Add Pairing</button>
            <div id="success"></div>
        </p>

        <div>
            <div class="singleline">Find foods that pair well with <input id="food" /></div>
            <div id="searchFeedback" class="ui-state-error"></div>
        </div>



    <div id="pairing-dialog-form" title="Add a pairing" style="clear:both">
        <p class="validateTips"></p>
        <form>
        <fieldset>
            <div class="dialog-left">
                <label for="ingredient1">Ingredient 1</label>
                <input type="text" name="ingredient1" id="ingredient1" class="text ui-widget-content ui-corner-all" />
                <g:select name="category1" from="${categories}" optionKey="key" optionValue="value"/>
            </div>
            <div class="dialog-right">
                <label for="ingredient2">Ingredient 2</label>
                <input type="text" name="ingredient2" id="ingredient2" class="text ui-widget-content ui-corner-all" />
                <g:select name="category2" from="${categories}"  optionKey="key" optionValue="value"/>
            </div>
            <div class="dialog-somewhere">
                <label for="affinity">Affinity</label>
                <g:select name="affinity" from="${affinity}" optionKey="key" optionValue="value"/>
            </div>
        </fieldset>
        </form>
    </div>
    <div id="chart"></div>
        <script type="text/javascript">
            function updateTips( t ) {
                var tips = $( ".validateTips" );
                tips
                    .text( t )
                    .addClass( "ui-state-highlight" );
               setTimeout(function() {
                    tips.removeClass( "ui-state-highlight", 1500 );
                }, 500 );
        	}

            var ingredient1 = $('#ingredient1')
            var ingredient2 = $('#ingredient2')
        	var allFields = $( [] ).add(ingredient1).add(ingredient2);

            $(function() {
                $( "#food" ).autocomplete({
            			source: "autosearch",
            			minLength: 2,
            			select: function(event, ui ) {
            				if (ui.item) {
            				    flavorTreeSearch(ui.item.id);
            				    $('#searchFeedback').html('');
            				}
            			}
            		});
                $("#food").bind('keypress', function(e) {
                        if (e.which == 13) {
                                jQuery.ajax("${createLink(action:'autosearch')}", {
                                    success: function(data, textStatus, jqXHR) {
                                        console.log (data);
                                        if (data.length<=0) {
                                            $('#searchFeedback').html('No such ingredient found');
                                        } else if (data.length>=2) {
                                            $('#searchFeedback').html('Too many ingredients found. Please narrow your search');
                                        } else if (data.length==1) {
                                            //make search
                                            console.log(data[0].id);
                                            flavorTreeSearch(data[0].id);
                                        }
                                    },
                                    error: function(data, textStatus, jqXHR) {
                                        $('#searchFeedback').html('Oops! Probably a 5xx error');
                                    },
                                    data: {'term':$("#food").val()}
                                });
                                //flavorTreeSearch(null, $("#food").val());
                        }
                });

                $( "#ingredient1").autocomplete({
                        source: "autosearch",
                        minLength: 2,
                        select: function(event, ui ) {
                            if (ui.item) {
                                //alert(ui.item.value );
                                //alert(ui.item.id );
                            }
                        }
                    });

                $( "#ingredient2").autocomplete({
                        source: "autosearch",
                        minLength: 2,
                        select: function(event, ui ) {
                            if (ui.item) {
                                //alert(ui.item.value );
                                //alert(ui.item.id );
                            }
                        }
                    });


                $("#addPairing").button({
                    icons: { primary: "ui-icon-newwin" }
                });

                $("#addPairing").click(function() {
                    $( ".validateTips" ).text('Select two ingredients that go together');
                    $("#pairing-dialog-form").dialog( "open" );
                });

                $( "#pairing-dialog-form" ).dialog({
                    autoOpen: false,
                    height: 300,
                    width: 700,
                    modal: true,
                    buttons: {
                        "Create Pairing": function() {
                            var bValid = true;
                            allFields.removeClass( "ui-state-error" );

                            bValid = bValid && checkNotBlank( ingredient1, "first");
                            bValid = bValid && checkNotBlank( ingredient2, "second");

                            if ( bValid ) {
                                jQuery.ajax("${createLink(action:'create')}", {
                                    success: function() { },
                                    data: {'ingredient1': $('#ingredient1').val(), 'ingredient2': $('#ingredient2').val(),
                                            'category1': $('#category1').val(), 'category2': $('#category2').val(),
                                            'affinity': $('#affinity').val()}
                                });
                                $( this ).dialog( "close" );
                            }
                        },
                        Cancel: function() {
                            $( this ).dialog( "close" );
                        }
                    },
                    close: function() {
                        allFields.val( "" ).removeClass( "ui-state-error" );
                    }
                });

            });

             var m = [20, 120, 20, 120],
                w = 900 - m[1] - m[3],
                h = 400 - m[0] - m[2],
                i = 0,
                duration = 500,
                root;

                var tree = d3.layout.tree()
                .size([h, w]);

                var diagonal = d3.svg.diagonal()
                .projection(function(d) { return [d.y, d.x]; });

                var vis = d3.select("#chart").append("svg")
                .attr("width", w + m[1] + m[3])
                .attr("height", h + m[0] + m[2])
                .append("g")
                .attr("transform", "translate(" + m[3] + "," + m[0] + ")");

                function flavorTreeSearch(nodeId) {
                    d3.json("flavorTree?nodeId="+nodeId, function(json) {
                        root = json;
                        root.x0 = h / 2;
                        root.y0 = 0;

                        function collapse(d) {
                            if (d.children) {
                                d._children = d.children;
                                d._children.forEach(collapse);
                                d.children = null;
                            }
                        }

                        root.children.forEach(collapse);
                        update(root);
                    });
                }

                function update(source) {

                // Compute the new tree layout.
                var nodes = tree.nodes(root).reverse();

                // Normalize for fixed-depth.
                nodes.forEach(function(d) { d.y = d.depth * 250 * d.wt; });

                // Update the nodes…
                var node = vis.selectAll("g.node")
                .data(nodes, function(d) { return d.id || (d.id = ++i); });

                // Enter any new nodes at the parent's previous position.
                var nodeEnter = node.enter().append("g")
                .attr("class", "node")
                .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
                .on("click", click);

                nodeEnter.append("circle")
                .attr("r", 1e-6)
                .style("stroke", function(d) {return d.catColor})
                .style("fill", function(d) { return d.catColor});

                nodeEnter.append("text")
                .attr("x", function(d) { return d.children || d._children ? -10 : 10; })
                .attr("dy", ".35em")
                .attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
                .text(function(d) { return d.name; })
                .style("fill-opacity", 1e-6);

                // Transition nodes to their new position.
                var nodeUpdate = node.transition()
                .duration(duration)
                .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

                nodeUpdate.select("circle")
                .attr("r", 4.5)
                 .style("fill", function(d) { return d.catColor});

                nodeUpdate.select("text")
                .style("fill-opacity", 1);

                // Transition exiting nodes to the parent's new position.
                var nodeExit = node.exit().transition()
                .duration(duration)
                .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
                .remove();

                nodeExit.select("circle")
                .attr("r", 1e-6);

                nodeExit.select("text")
                .style("fill-opacity", 1e-6);

                // Update the links…
                var link = vis.selectAll("path.link")
                .data(tree.links(nodes), function(d) { return d.target.id; });

                // Enter any new links at the parent's previous position.
                //.style("stroke",function(d) { return d.target.catColor})
                link.enter().insert("path", "g")
                .attr("class", "link")
                .attr("d", function(d) {
                var o = {x: source.x0, y: source.y0};
                return diagonal({source: o, target: o});
                })
                .transition()
                .duration(duration)
                .attr("d", diagonal);

                // Transition links to their new position.
                link.transition()
                .duration(duration)
                .attr("d", diagonal);

                // Transition exiting nodes to the parent's new position.
                link.exit().transition()
                .duration(duration)
                .attr("d", function(d) {
                var o = {x: source.x, y: source.y};
                return diagonal({source: o, target: o});
                })
                .remove();

                // Stash the old positions for transition.
                nodes.forEach(function(d) {
                d.x0 = d.x;
                d.y0 = d.y;
                });
                }

                // Toggle children on click.
                function click(d) {
                if (d.children) {
                d._children = d.children;
                d.children = null;
                } else {
                d.children = d._children;
                d._children = null;
                }
                update(d);
                }
        </script>
	</body>
</html>