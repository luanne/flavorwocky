if (typeof jQuery !== 'undefined') {
	(function($) {
		$('#spinner').ajaxStart(function() {
			$(this).fadeIn();
		}).ajaxStop(function() {
			$(this).fadeOut();
		});

        $('.ping-server').click(function() {
            ping();
        });

        ping();
	})(jQuery);
}

function ping() {
    $.ajax({
        url:'ping',
        success: function (data) {
                if (data == 'true') {
                    $('#ping-ok').show();
                    $('#ping-fail').hide();
                 }
                else {
                    $('#ping-ok').hide();
                    $('#ping-fail').show();
                }
            },
        error: function (data) {
                $('#ping-ok').hide();
                $('#ping-fail').fail();
            }
    });
}

function checkNotBlank( o, n) {
    if ( o.val().length < 1) {
        o.addClass( "ui-state-error" );
        updateTips( "Select the " + n +" ingredient" );
        return false;
    } else {
        return true;
    }
}