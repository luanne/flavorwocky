<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:javascript library="jquery" />
	</head>
	<body>
        <p>
            <button id="addPairing">Add Pairing</button>
            <div id="success"></div>
        </p>

        <p>
            Find foods that pair well with <input id="food">
        </p>



    <div id="pairing-dialog-form" title="Add a pairing">
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
        </fieldset>
        </form>
    </div>

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
            				    alert(ui.item.value );
            				    alert(ui.item.id );
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
                                            'category1': $('#category1').val(), 'category2': $('#category2').val()}
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
        </script>
	</body>
</html>