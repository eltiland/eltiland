/**
 * This function does stop jquery event from eveyrthing: stop propagation, prevent default, etc.
 */
function stopEvent(event) {
    if (!event) {
        return;
    }
    event.stopPropagation();
    event.preventDefault();
}

/**
 * This function may be called on wicket ajax failure. It should be registered
 * in the BasePoseidonPage to catch all the instances of the server non-reachable
 * or whatever.
 */
function tryRegisterWicketAjaxOnFailure(message) {
    if (!Wicket || !Wicket.Ajax) {
        return;
    }
    Wicket.Ajax.registerFailureHandler(function () {
        alertError(message, 24 * 60 * 60 * 1000, 0, 'center', true);
    });
}

function bindConfirmationDialog(selector, text, confirmation, hiddenField) {
    var link = $(selector);

    var oldHandlers = new Array();
    if (link.data('events') && link.data('events').click) {
        $.each(link.data('events').click, function (key, value) {
            oldHandlers.push(value);
        });
    }
    var oldOnClick = link.attr('onclick');
    link.attr('onclick', null);
    link.unbind('click');

    // we need the confirmation always be first.
    link.bind('click', {
        askQuestion:true
    }, function (event) {
        if (event.data.askQuestion) {
            event.preventDefault();
            event.stopPropagation();
            event.stopImmediatePropagation();

            var link = $(this);
            var zIndex = link.zIndex();
            var dialog = $('#dialogue');
            if (dialog.size() == 0) {
                dialog = $('<div id="dialogue"></div>')
            } else {
                dialog.dialog('destroy');
            }

            dialog.html(text).dialog({
                resizable:false,
                zIndex:zIndex,
                modal:true,
                title:confirmation,
                buttons:{
                    'Да':function () {
                        $(this).dialog('close');
                        // workaround to make IE8 working. Cheredinchenko, Litvinenko
                        link.attr('onclick', oldOnClick);
                        if (hiddenField) {
                            $(hiddenField).val(true);
                        }
                        event.data.askQuestion = false;
                        link.click();
                        //soft unbind to prepare call submit dialog again (fail validation)
                        event.data.askQuestion = true;
                        link.attr('onclick', null);
                    },
                    'Нет':function () {
                        $(this).dialog('close');
                        if (hiddenField) {
                            link.attr('onclick', oldOnClick);
                            $(hiddenField).val(false);
                            event.data.askQuestion = false;
                            link.click();
                            //soft unbind to prepare call submit dialog again (fail validation)
                            event.data.askQuestion = true;
                            link.attr('onclick', null);
                        }
                    }
                }
            });

            if (confirmation == null) {
                dialog.closest(".ui-dialog").find(".ui-dialog-titlebar:first").hide();
            }
        }
    });

    for (var i in oldHandlers) {
        var handler = oldHandlers[i];
        handler.guid = null;
        link.bind('click', handler);
    }
}
/**
 * Shows loading indicator near link.
 * @param selector link selector
 * @param indicatorClass loading ajax indicator class
 */
function showLoading(selector, indicatorClass) {
    var e = $(selector);

    var tagName = e[0].tagName.toLowerCase();
    if (tagName == 'button' || tagName == 'input') {
        e.prop('disabled', true);
    }

    var indicator = '<div class="jsIndicator ' + indicatorClass + '"></div>';

    if (tagName == 'button') {
        e.append(indicator);
    } else {
        e.after(indicator);
    }

    e.data('ORIGINAL_ONCLICK', e.attr('onclick'));
    e.data('ORIGINAL_CLASSES', e.attr('class'));
    e.attr('onclick', 'stopEvent');
    e.attr('class', '');
    e.addClass('disabled');
    e.closest('form').bind('submit.stopEvent', stopEvent); // stop normal form submit
}
/**
 * Hides loading indicator near selector.
 * @param selector link selector with showed indicator
 */
function hideLoading(selector) {
    var e = $(selector);

    if (e.size() == 0) {
        // nothing to hide
        return;
    }

    var tagName = e[0].tagName.toLowerCase();
    if (tagName == 'button') {
        e.children('.jsIndicator').remove();
    } else {
        e.siblings('.jsIndicator').remove();
    }
    e.prop('disabled', false);
    e.attr('onclick', e.data('ORIGINAL_ONCLICK'));
    e.attr('class', e.data('ORIGINAL_CLASSES'));
    e.data('ORIGINAL_ONCLICK', null);
    e.data('ORIGINAL_CLASSES', null);
    e.removeClass('disabled');
    e.closest('form').unbind('submit.stopEvent', stopEvent); // resume normal form submit
}

/**
 *  Apply ajax loading indicator, put mask div over content for block user action.
 *
 *  @param elementId DOM element id
 *  @param isShowLoadingIndicatorImage Flag show spin indicator image
 *  @param isStartIndicating Flag show/hide whole loading indicator on the page
 */
function AjaxLoadingIndicator(elementId, isShowLoadingIndicatorImage, isStartIndicating) {
    var div = document.createElement('div');
    div.setAttribute('id', 'loadingAjax');

    var innerDiv = document.createElement('div');
    div.appendChild(innerDiv);

    if (isShowLoadingIndicatorImage) {
        loadingGifSource = document.getElementById('loadingAjaxGif').src;

        var text = document.createTextNode('Updating ...');
        innerDiv.appendChild(text);
        innerDiv.appendChild(document.createElement('br'));

        var image = document.createElement('img');
        image.setAttribute('alt', '');
        image.setAttribute('src', loadingGifSource);
        innerDiv.appendChild(image);
    }

    if (isStartIndicating) {
        $('#' + elementId).append(div);

        $('#loadingAjax').css("height", $('#' + elementId).height());
        $('#loadingAjax').css("width", $('#' + elementId).width());
    } else {
        $('#' + elementId + " > #loadingAjax").remove();
    }
}


var dialogTimerId;
function alertError(message, showTime, fadeoutTime, position, modal) {
    if (!position) {
        position = 'top';
    }
    if (!modal) {
        modal = false;
    }
    alertGeneric(message, 'alertError', showTime, fadeoutTime, position, modal)
}

function alertInfo(message, showTime, fadeoutTime, position, modal) {
    if (!position) {
        position = 'top';
    }
    if (!modal) {
        modal = false;
    }
    alertGeneric(message, 'alertOK', showTime, fadeoutTime, position, modal)
}

function alertWarning(message, showTime, fadeoutTime, position) {
    if (!position) {
        position = 'top';
    }
    alertGeneric(message, 'alertWarning', showTime, fadeoutTime, position, false)
}

function alertGeneric(message, dialogClass, showTime, fadeoutTime, position, modal) {
    clearTimeout(dialogTimerId);

    var alertBox = $('#eltiland_alertbox');
    if (alertBox.size() == 0) {
        alertBox = $('body').prepend('<div id="eltiland_alertbox"></div>').find('#eltiland_alertbox');
    }
    alertBox.html(
        '<div class="poseidon_boxes_lightgray" >'
            + '<div class="' + dialogClass + '"></div><div style="min-height:48px;">' + message +
            "</div></div>");
    alertBox.dialog('destroy').dialog({
        open:function (event, ui) {
            var crossToClose = $(this).parent().find('div.ui-dialog-titlebar > a.ui-dialog-titlebar-close');
            if (crossToClose.length > 0) {
                $(this).find('div.poseidon_boxes_lightgray').prepend(crossToClose);
                crossToClose.css('display', 'block');
                crossToClose.css('position', 'static');
                crossToClose.css('margin', '0px');
                crossToClose.css('padding', '2px');
                crossToClose.css('float', 'right');
                crossToClose.css('right', '40px');
            }
        },
        minWidth:600,
        width:600,
        minHeight:68,
        modal:modal,
        position:position,
        resizable:false,
        dialogClass:'alertBox',
        zIndex:40000 /*see div.wicket-modal css value for z-index*/
    });
    alertBox.click(function () {
        alertBox.stop();
        alertBox.fadeTo(0, 100);
        clearTimeout(dialogTimerId);
    });
    dialogTimerId = setTimeout(function () {
            alertBox.fadeOut(fadeoutTime, function () {
                alertBox.dialog('close');
            });
        },
        showTime);
}

$(window).load(function () {
    function replaceUsernameToReal() {
        $('#usernameReplaceTextBox').hide();
        var t = $('#usernameTextBox');
        t.show();
        t.focus();
    }

    function replacePasswordToReal() {
        $('#passwordReplaceTextBox').hide();
        var t = $('#passwordTextBox');
        t.show();
        t.focus();
    }

    var u = $('#usernameReplaceTextBox');
    u.click(replaceUsernameToReal);
    u.focus(replaceUsernameToReal);

    var p = $('#passwordReplaceTextBox');
    p.click(replacePasswordToReal);
    p.focus(replacePasswordToReal);
});
/**
 * This function may be called on wicket ajax failure. It should be registered
 * in the BasePage to catch all the instances of the server non-reachable
 * or whatever.
 */
function tryRegisterWicketAjaxOnFailure(message) {
    if (!Wicket || !Wicket.Ajax) {
        return;
    }
    Wicket.Ajax.registerFailureHandler(function () {
        alertError(message, 24 * 60 * 60 * 1000, 0, 'center', true);
    });
}
/**
 * Sets item active.
 * @param selector  selector
 */
function setItemActive(selector) {
    var activeItem = $(selector);
    var activeClass = "active";
    activeItem.siblings(".elti-item").removeClass(activeClass);
    activeItem.addClass(activeClass);
}