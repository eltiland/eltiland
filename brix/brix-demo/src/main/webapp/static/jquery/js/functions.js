function Elti() {
}

Elti.prototype = {
    addLoadingFogHandlers:function (baseElement) {
        if (!baseElement) {
            baseElement = $('body');
        }

        // add click handler to all links to fade in loading
        addLoadingFogAndNavigationBehavior(":not(div#wicketDebugLink) :not(div.formFieldHTML) a[target!='_blank']:not('.popup'):not('.ajaxLink'):not('[href^='mailto:']')" +
            ":not(.deleteButton):not(.ui-dialog-titlebar-close):not(.noFog)");

        //remove attached event 'click' from element with implemented onclick action (support ajax link)
        $('a[onclick]').unbind('click');

        baseElement.find(":not(div.formFieldHTML) a[target='_blank']:not('.popup')")
            .click(function (event) {
                event.stopPropagation();
            });

        // add submit handler to all forms to fade in loading
        baseElement.find('form.elti_save').submit(function (event) {
            event.preventDefault();

            var form = $(this);
            ELTI.showLoadingFog(function () {
                var data = 'elti=elti';
                $('form.elti_save').each(function () {
                    data += '&' + $(this).serialize();
                });

                var href = form.attr('action');

                jQuery.post(href, data, function (data) {
                    document.open();
                    document.write(data);
                    document.close();

                    // get url of the loaded page
                    var url = $('address').html();
                    // check if the page is not the same page (i.e. no redirect in the
                    // ajax call)
                    if (window.location.pathname != url) {
                        // redirect
                        ELTI.showLoadingFog();
                        window.location.href = window.location.protocol + '//' +
                            window.location.host + url;
                    }
                });
            });
        });

        baseElement.find('form').not('.elti_save').submit(function (event) {
            event.preventDefault();
            //#387 elti_static_submit - css class using for submitting NON ajax forms
            //TODO: add elti_static_submit css class foreach non-ajax form
            if ($(this).hasClass('elti_static_submit')) {
                var form = this;
                ELTI.showLoadingFog(function () {
                    form.submit();
                });
            }
        });
    },

    addFormHandlers:function (baseElement) {
        if (!baseElement) {
            baseElement = $('body');
        }

        baseElement.find('form button[name][value]').click(function (event) {
            event.preventDefault();
            event.stopPropagation();

            var input = $(this).find('button').size() > 0 ?
                $(this).find('button').first() : $(this).closest('button').first();
            var form = $('form').has(this).first();

            if (input.attr('name')) {
                $('form').has(this).find("*[name='" + input.attr('name') + "']")
                    .not(input).attr('name', '');
                $('form').has(this).append("<input type='hidden' name='" +
                    input.attr('name') + "' value='" + input.attr('value') + "' />");
            }

            $('form').has(this).submit();
        });

        baseElement.find("input[type='text'].defaultValue").change(function () {
            var input = $(this);
            input.removeClass('defaultValue');

            ELTI.hideTooltip();
            var span = input.closest('span');
            span.unbind('mouseenter');
            span.unbind('mouseleave');
            span.attr('title', '');
        });
    },

    setFocus:function (baseElement) {
        if (!baseElement) {
            baseElement = $('body');
        }

        var firstElement;
        baseElement.find("form input:not([type='hidden']):not(.readonly):not(.date), select:not(.readonly), textarea:not(.readonly)")
            .each(function (index, item) {
                if (!firstElement &&
                    $(item).closest("div.table:not('.tableContent')").size() == 0) {
                    firstElement = $(item);
                }
            });

        if (firstElement) {
            firstElement.focus();
        }
    },

    showLoadingFog:function (readyHandler) {
        $('body > div.ui-dialog').css('zIndex', 98);

        $('#loading').fadeTo(0, 0);
        $('#loading').show();
        $('#loading').fadeTo(ELTI.fadeInTime, 0.75, readyHandler);
    },

    hideLoadingFog:function (readyHandler) {
        $('#loading').fadeTo(ELTI.fadeOutTime, 0, function () {
            $('#loading').hide();

            // check if notification icons are present
            if ($('#loading img').size() > 1) {
                // remove all notification icons
                $('#loading img').not($('#loading img').last()).detach();
            }

            if (readyHandler instanceof Function) {
                readyHandler();
            }
        });
    },

    showFog:function (readyHandler) {
        $('body > div.ui-dialog').css('zIndex', 98);

        var fog = $('div#fog');
        fog.fadeTo(ELTI.fadeInTime, 0.5, function () {
            fog.data('opacity', 0.5);

            if (readyHandler instanceof Function) {
                readyHandler();
            }
        });
    },

    hideFog:function (readyHandler) {
        var fog = $('div#fog');
        fog.fadeTo(ELTI.fadeOutTime, 0, function () {
            fog.hide();
            fog.data('opacity', 0);

            if (readyHandler instanceof Function) {
                readyHandler();
            }
        });
    },
    /**
     * Collapse all collapsable blocks on the page.
     */
    collapse:function () {
        $('.jsCollapsable').each(function () {
            var hidden = $(this);
            hidden.hide();
            //TODO: localize string
            var maximize = $('<a class="elti-linkbutton">развернуть</a>')
                .click(function (e) {
                    hidden.show();
                    minimize.show();
                    maximize.hide();
                });
            //TODO: localize string
            var minimize = $('<a class="elti-linkbutton">свернуть</a>')
                .click(function (e) {
                    hidden.hide();
                    maximize.show();
                    minimize.hide();
                }).hide();
            $(this).after(maximize);
            $(this).after(minimize);
        });
    },
    init:function (baseElement) {
        if (!baseElement) {
            baseElement = $('body');
        }
        $(document).ready(function () {
            $(window).resize(fixHeader);
            ELTI.collapse();
        });

        // can not be set in HTML on the body tag as some browsers evaluate the
        // onunload handler before all external JS files are loaded and might thus
        // throw errors
        window.onunload = function () {
            ELTI.hideLoadingFog();
        };
        // for some reason Firefox is too fast :-)
        // TODO: check if this is true for all platforms and on processors with
        // different clock speed
        if ($.browser.mozilla) {
            this.fadeInTime = this.fadeOutTime = 250;
        }
        this.addLoadingFogHandlers(baseElement);
        this.addFormHandlers(baseElement);
        this.hideLoadingFog(baseElement);
        this.setFocus(baseElement);
    }
};

ELTI = new Elti();
ELTI.fadeInTime = 50;
ELTI.fadeOutTime = 50;
ELTI.init();

/**
 * Processes title attributes starting from the root element. Titles are then cut and put into the 'title' data of the
 * jquery object.
 *
 * @param elementQuery element query to start search from
 */
function processTitles(elementQuery) {
    $(elementQuery).each(function () {
        var jThis = $(this);
        jThis.data('tooltip', jThis.attr('title'));
        jThis.removeAttr('title');
    });
}

function showTooltip(event, className, id, offset) {
    if (!offset) {
        var offset = {
            top:-10,
            left:-10
        };
    }

    // get tooltip element
    var tooltip = $('#' + id);
    // check if tooltip element exists
    if (tooltip.size() == 0) {
        // create tooltip element
        $('body').append($('<div id="' + id + '"></div>'));
        // get tooltip element
        tooltip = $('#' + id);

        // attach mouseleave handler for closing tooltip
        tooltip.mouseleave(function () {
            hideTooltip.call(this, $(this).attr('id'));
        });
    }

    // caching
    var jThis = $(this);

    // set tooltip content
    tooltip.html(jThis.data('tooltip'));

    // check if a class shall be added
    if (className) {
        // add class
        tooltip.addClass(className);
    }

    // move tooltip slightly under the current mouse position
    // we first try to get it from event
    var left, top;
    if (event.pageX && event.pageY) {
        left = event.pageX;
        top = event.pageY;
    } else if (event.offset) {
        left = event.offset().left;
        top = event.offset().top;
    } else {
        alert('Could not determine popup position. Please fix me. functions.js')
    }

    tooltip.css('top', (top + offset.top) + 'px');
    tooltip.css('left', (left + offset.left) + 'px');

    // finally show the tooltip
    tooltip.show();

    // move tooltip (if needed) so that it doesn't overlap the screen boundaries
    // caching
    var position = tooltip.position();
    var overlapX = position.left + tooltip.outerWidth() - $('body').innerWidth();
    if (overlapX > 0) {
        tooltip.css('left', (position.left - overlapX) + 'px');
    }

    var overlapY = position.top + tooltip.outerHeight() - $('body').innerHeight();
    if (overlapY > 0) {
        tooltip.css('top', (position.top - overlapY) + 'px');
    }
}

function hideTooltip(id) {
    // get tooltip element
    var tooltip = $('#' + id);
    // check if tooltip element exists
    if (tooltip.size() == 1) {
        // hide tooltip
        tooltip.hide();

        // reset tooltip element
        tooltip.html('');
        tooltip.removeClass();
    }
}

function addLoadingFogAndNavigationBehavior(selector) {
    $(selector).click(function (event) {
        event.stopPropagation();
        event.preventDefault();

        var href = this.href;
        ELTI.showLoadingFog(function () {
            window.location.href = href;
        });
    });
}

function fixOrderImages() {
    if ($.browser.mozilla) {
        var div = $('.noScroll .wicket_orderUp div, .noScroll  .wicket_orderDown div');
        var diff = (div.find('a').width() + div.find('img').width()) - div.width();
        if (diff > 0) {
            var paddingRight = parseInt(div.css('padding-right').replace("px", ""));
            if (diff <= paddingRight) {
                div.css('padding-right', '-=' + diff);
            }
            else {
                // if right padding space is not enough use right padding
                div.css('padding-right', 0);
                div.css('padding-left', '-=' + (diff - paddingRight + 1));
            }
        }
    }
}

function fixHeader() {
    $('.eltiTable').each(function () {
        fixSingleHeader(this);
    });
}

function fixHeaderById(dataTableId) {
    fixSingleHeader($("#" + dataTableId));
}
function fixSingleHeader(dataTable) {
    var newTheadTr = $(dataTable).find(".newTheadTr");
    var oldHeaders = $(dataTable).find("div.table tr.headers th");
    var tableContent = $(dataTable).find("div.tableContent > table");
    newTheadTr.html(oldHeaders.parent().html());
    oldHeaders.each(function (i) {
        $(newTheadTr.children().get(i)).width($(this).width());
    });

    $(dataTable).find("div.noScroll > table").width(tableContent.width());
}

function centerCurrentDialog() {
    var w = Wicket.Window.get();
    if (w != null) {
        w.center();
    }
}

function refreshModalWindowsDimensions() {
    var win = Wicket.Window.get();
    while (win != null) {
        refreshModalWindowDimension(win);
        win = win.oldWindow;
    }
}

function refreshModalWindowDimension(win) {
    win.window.style.width = win.settings.width + "px";
    win.content.style.height = win.settings.height != null ? win.settings.height + "px" : "";
}

function addTooltipBehavior(query) {
    targetElement = $(query);
    if (!targetElement.data('tooltip')) {
        return;
    }
    $(query).mouseenter(function (event) {
        showTooltip.call(this, event, 'error', 'tooltip');
    });
}

function addTextTooltipBehavior(query, cssClass) {
    var element = $(query);
    if (!element.data('tooltip')) {
        return;
    }
    element.mouseenter(
        function (event) {
            showTooltip.call(this, event, cssClass, 'tooltip', {
                top:10,
                left:10
            });
        }).mouseleave(function () {
            hideTooltip.call(this, 'tooltip');
        });
}

/**
 * Load YouTube api (async), construct iframe and initialise it by calling onYouTubeIframeAPIReady()
 * after iframe created
 */
function createYouTubePlayers() {
    //This function creates an <iframe> (and YouTube player)
    //after the API code downloads.
    //  function onYouTubeIframeAPIReady() {
    var players = document.getElementsByClassName('youtubePlayer');
    for (var i = 0; i < players.length; i++) {
        new YT.Player(players[i].getAttribute('id'), {
            videoId:players[i].getAttribute('title'),
            playerVars:{ 'rel':0 }
        });
    }
    // }

    //window['onYouTubeIframeAPIReady'] = onYouTubeIframeAPIReady;

    //This code loads the IFrame Player API code asynchronously.
    var tag = document.createElement('script');
    tag.id = "youtube_iframe_api";
    tag.src = "//www.youtube.com/iframe_api";
    var firstScriptTag = document.getElementsByTagName('script')[0];
    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
}


/**
 * Formatter for Float. Originally was in formFieldFloat.js.skin
 *
 * @param componentId Tag id attribute of component for binding float formatter
 */
function FloatFormatter(componentId) {
    var options = {format:'#,###.00', locale:'us', nanForceZero:false};
    var component = $("#" + componentId);
    component.change(
        function () {
            var input = $(this);
            if (input.val()) {
                formatComponent(input, options);
            }
        });

    if (component.val()) {
        formatComponent(component, options);
    }
}

/**
 * Helper method for FloatFormatter
 * @param component
 * @param options
 */
function formatComponent(component, options) {
    var string = component.val();
    var formattedString = $.formatNumber(string, options);
    if (formattedString != null) {
        if (formattedString.indexOf("e") == -1) {
            component.val(formattedString);
        } else {
            component.val(string);
        }
    }
}

/*
 acherednichenko: removed, now FeedbackLabel has its own control over tooltips (processes and adds behavior).
 As we need more tooltips, we may make this behavior a separate class.
 $().ready(function() {
 processTitles('*[title!=""]');

 $('div.errorColor:data("tooltip")').add('span.errorColor:data("tooltip")')
 .mouseenter(function(event) {
 showTooltip.call(this, event, 'error', 'tooltip');
 });

 $('div:not(".errorColor"):not(".tooltipBox"):data("tooltip")')
 .add('span:not(".errorColor"):data("tooltip")').mouseenter(
 function() {
 showTooltip.call(this, event, null, 'tooltip', {
 top: 10,
 left: 10
 });
 }).mouseleave(function() {
 hideTooltip.call(this, 'tooltip');
 });

 $('.tooltipBox').click(function(event) {
 showTooltip.call(this, event, null, 'tooltipBox');
 });

 });
 */