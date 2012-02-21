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
                        $('#ingredientNodeId').val(ui.item.id);
                        $('#searchFeedback').html('');
                    }
                }
            });
        $("#food").bind('keypress', function(e) {
                if (e.which == 13) {
                        jQuery.ajax(autosearchLink, {
                            success: function(data, textStatus, jqXHR) {
                                console.log (data);
                                if (data.length<=0) {
                                    $('#searchFeedback').html('No such ingredient found');
                                } else if (data.length>=2) {
                                    $('#searchFeedback').html('Too many ingredients found. Please narrow your search');
                                } else if (data.length==1) {
                                    //make search
                                    flavorTreeSearch(data[0].id);
                                    $('#ingredientNodeId').val(data[0].id);
                                }
                            },
                            error: function(data, textStatus, jqXHR) {
                                $('#searchFeedback').html('Oops! Probably a 5xx error');
                            },
                            data: {'term':$("#food").val()}
                        });
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
            $( ".validateTips" ).text('Add two ingredients that pair well');
            $("#pairing-dialog-form").dialog( "open" );
        });

        $( "#pairing-dialog-form" ).dialog({
            autoOpen: false,
            height: 250,
            width: 550,
            modal: true,
            buttons: {
                "Create Pairing": function() {
                    var bValid = true;
                    allFields.removeClass( "ui-state-error" );

                    bValid = bValid && checkNotBlank( ingredient1, "first");
                    bValid = bValid && checkNotBlank( ingredient2, "second");

                    if ( bValid ) {
                        jQuery.ajax(createLink, {
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


        //show a random ingredient
        var showIngr = [14, 11];
        $('#ingredientNodeId').val(showIngr[Math.floor(Math.random()*(showIngr.length))])
        console.log ($('#ingredientNodeId').val());
        flavorTreeSearch($('#ingredientNodeId').val());

        $('#viewInteraction').button().bind('click', function(){
            console.log('view interaction');
            console.log($('#ingredientNodeId').val());
            d3.select("#chart").selectAll('g.node').remove();
            d3.select("#chart").selectAll('path').remove();
            flavorNetworkSearch($('#ingredientNodeId').val());
        });
        $('#viewExploration').button().bind('click', function(){
            d3.select("#chart").selectAll('g.node').remove();
            d3.select("#chart").selectAll('path').remove();
            flavorTreeSearch($('#ingredientNodeId').val());
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
        .attr("dy", ".60em")
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


        function flavorNetworkSearch(nodeId) {
            d3.json("flavorNetwork?nodeId="+nodeId, function(json) {
            var force = self.force = d3.layout.force()
            .nodes(json.nodes)
            .links(json.links)
            .gravity(.05)
            .distance(function(d) { return 250 * d.dist; })
            .charge(-100)
            .size([w, h])
            .start();

            var link = vis.selectAll("line.link")
            .data(json.links)
            .enter().append("svg:line")
            .attr("class", "link")
            .style("stroke-width", "1.5px")
            .style("stroke","#ccc")
            .attr("x1", function(d) { return d.source.x; })
            .attr("y1", function(d) { return d.source.y; })
            .attr("x2", function(d) { return d.target.x; })
            .attr("y2", function(d) { return d.target.y; });

            var node = vis.selectAll("g.node")
            .data(json.nodes)
            .enter().append("svg:g")
            .attr("class", "node")
            .call(force.drag);

            // .attr("xlink:href", "https://d3nwyuy0nl342s.cloudfront.net/images/icons/public.png")
            node.append("svg:circle")
            .attr("class", "circle")
            .attr("r",5)
            .style("fill",function(d) { return d.catColor})
            .style("stroke",function(d) { return d.catColor})
            .attr("x", "-8px")
            .attr("y", "-8px")
            .attr("width", "16px")
            .attr("height", "16px")
            .call(force.drag);

            node.append("svg:text")
            .attr("class", "nodetext")
            .attr("dx", 12)
            .attr("dy", ".35em")
            .text(function(d) { return d.name });

            force.on("tick", function() {
            link.attr("x1", function(d) { return d.source.x; })
            .attr("y1", function(d) { return d.source.y; })
            .attr("x2", function(d) { return d.target.x; })
            .attr("y2", function(d) { return d.target.y; });

            node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
            });
            });

        }