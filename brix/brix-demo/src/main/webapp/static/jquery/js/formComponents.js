//TODO Incomplete, add all widgets and formatter


/**
 * Show calendar widget. Originally was in formFieldDate.js.skin
 *
 * @param componentId Tag id attribute of component for binding calendar
 * @param setMaxDateToNow flag. If true then datepicker disables possibility to choose date that in future.
 */
function CalendarWidget(componentId, dateFormat, setMaxDateToNow, onSelectCallback) {
    var maxDate = setMaxDateToNow ? new Date() : null;
    $("#" + componentId).datepicker({
        dateFormat:dateFormat,
        changeMonth:true,
        changeYear:true,
        maxDate:maxDate,
        onSelect:onSelectCallback,
        beforeShow:function (input, inst) {
            // TODO: picker height is not constant.
            var pickerHeight = 200;
            var offset = $(input).offset();
            // if not enough place in the bottom
            if (offset.top + $(input).height() + pickerHeight > $(window).height()) {
                window.setTimeout(function () {
                    inst.dpDiv.css('left', offset.left + $(input).width())
                }, 1);
            }
        }
    });
}

function ImageResizeTool(componentId, props, formParams) {
    function valueOrDefault(property, defaultValue) {
        if (props[property] == null) {
            return defaultValue;
        }
        return props[property]
    }

    $("input[name='" + formParams['x'] + "']").val(valueOrDefault("x1", 0));
    $("input[name='" + formParams['y'] + "']").val(valueOrDefault("y1", 0));
    $("input[name='" + formParams['width'] + "']").val(valueOrDefault("y2", 10) - valueOrDefault("y1", 0));
    $("input[name='" + formParams['height'] + "']").val(valueOrDefault("x2", 10) - valueOrDefault("x1", 0));
    $('img#' + componentId).imgAreaSelect({
        aspectRatio:valueOrDefault("aspectRatio", "4:3"),
        handles:valueOrDefault("handles", true),
        maxHeight:valueOrDefault("maxHeight", 5000),
        minHeight:valueOrDefault("minHeight", 1),
        maxWidth:valueOrDefault("maxWidth", 5000),
        minWidth:valueOrDefault("minWidth", 1),
        movable:valueOrDefault("movable", true),
        resizable:valueOrDefault("resizable", true),
        imageHeight:valueOrDefault("imageHeight", 96),
        imageWidth:valueOrDefault("imageWidth", 96),
        x1:valueOrDefault("x1", 0),
        y1:valueOrDefault("y1", 0),
        x2:valueOrDefault("x2", 10),
        y2:valueOrDefault("y2", 10),
        onSelectEnd:function (img, selection) {
            $("input[name='" + formParams['x'] + "']").val(selection.x1);
            $("input[name='" + formParams['y'] + "']").val(selection.y1);
            $("input[name='" + formParams['width'] + "']").val(selection.width);
            $("input[name='" + formParams['height'] + "']").val(selection.height);
        }
    });
}

function AvatarCreatorComponentWorkaround(componentId) {
    var currentComponent = $('#' + componentId);

    var pluginOccupiedComponent = currentComponent.find('img.jsUsedImageToResize');

    var activityButtonChange = currentComponent.find('.jsUsedChangeImageLink');
    var activityButtonCut = currentComponent.find('.jsUsedImageAreaSelectorSubmit');
    var activityButtonClose = currentComponent.find('.jsUsedCloseCreateAvatarPanel');

    function getOldHandlers(link) {
        var oldHandlers = new Array();
        if (link.data('events') && link.data('events').click) {
            $.each(link.data('events').click, function (key, value) {
                oldHandlers.push(value);
            });
        }
        return oldHandlers
    }

    function getOldOnClickHandler(link) {
        var oldOnClickHandler = link.attr('onclick');
        link.attr('onclick', null);
        link.unbind('click');
        return oldOnClickHandler;
    }

    function bindNewHandler(activityButton, handler) {
        activityButton.bind('click', {
            askQuestion:true
        }, function (event) {
            if (event.data.askQuestion) {
                event.preventDefault();
                event.stopPropagation();
                event.stopImmediatePropagation();
                pluginOccupiedComponent.imgAreaSelect({ hide:true });
                var link = $(this);
                link.attr('onclick', handler);
                event.data.askQuestion = false;
                link.click();
            }
        });
    }

    function appendOldHandlers(activityButton, oldHandlers) {
        for (i in oldHandlers) {
            var handler = oldHandlers[i];
            handler.guid = null;
            activityButton.bind('click', handler);
        }

    }

    //save all change image button event handlers
    var oldChangeHandlers = getOldHandlers(activityButtonChange);
    //save all submit button event handlers
    var oldCutHandlers = getOldHandlers(activityButtonCut);
    //save all submit button event handlers
    var oldCloseHandlers = getOldHandlers(activityButtonClose);

    //remove old onClick handler
    var oldChangeOnClickHandler = getOldOnClickHandler(activityButtonChange);
    var oldCutOnClickHandler = getOldOnClickHandler(activityButtonCut);
    var oldCloseOnClickHandler = getOldOnClickHandler(activityButtonClose);

    //bind new onclick handler with buf fix, and fire old onclick
    bindNewHandler(activityButtonChange, oldChangeOnClickHandler);
    bindNewHandler(activityButtonCut, oldCutOnClickHandler);
    bindNewHandler(activityButtonClose, oldCloseOnClickHandler);

    //bind all another event handlers
    appendOldHandlers(activityButtonChange, oldChangeHandlers);
    appendOldHandlers(activityButtonCut, oldCutHandlers);
    appendOldHandlers(activityButtonClose, oldCloseHandlers);
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

/**
 * Formatter for Integer. Originally was in formFieldInteger.js.skin
 *
 * @param componentId Tag id attribute of component for binding integer formatter
 */

function IntegerFormatter(componentId) {
    var options = {format:'#,###', locale:'us', nanForceZero:false};
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
 * TinyMCE plugin.
 * @param componentId Tag id attribute of component for binding integer formatter
 * @param width dialog window height
 * @param height dialog window width
 */

function RichTextFormatter(componentId, width, height, ctrlEnterCallback, widthOffset, heightOffset) {
    setTimeout(function () {
        var options = {
            setup:function (ed) {
                ed.onKeyDown.add(function (ed, e) {
                    if (!ctrlEnterCallback) {
                        return;
                    }
                    key = e.keyCode || e.which;
                    if (e.ctrlKey && (key == 13)) {
                        ctrlEnterCallback();
                    }
                });
                ed.onKeyDown.add(function (ed, e) {
                    key = e.keyCode || e.which;
                    if (key == 27) {
                        var press = jQuery.Event("keydown");
                        press.ctrlKey = false;
                        press.keyCode = 27;
                        press.which = 27;
                        $(document).triggerHandler(press);

                        return false;
                    }
                    return true;
                })
            },
            theme:"advanced",
            theme_advanced_toolbar_location:'top',
            theme_advanced_toolbar_align:'left',
            width:width,
            height:height,

            mode:"exact",
            auto_focus:componentId,
            elements:componentId,

            plugins:'contextmenu,fullpage,autolink,lists,table,advhr,advlink,inlinepopups,preview,searchreplace,print,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,wordcount,advlist',

            theme_advanced_buttons1:'bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,formatselect,fontselect,fontsizeselect,|,cite,abbr,acronym,del,ins,attribs,|,visualchars,nonbreaking,blockquote',
            theme_advanced_buttons2:'cut,copy,paste,pastetext,pasteword,|,search,replace,|,bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,cleanup,code,|,preview,|,forecolor,backcolor',
            theme_advanced_buttons3:'tablecontrols,|,hr,removeformat,visualaid,|,sub,sup,|,charmap,advhr,|,print,|,ltr,rtl,|,fullscreen, image',

            autosave_ask_before_unload:false,
            add_form_submit_trigger:0,
            submit_patch:0
        };
        // workaround for IE 9 (read bug #357)
        if ($.browser.msie && $.browser.version == '9.0') {
            options.plugins = options.plugins.replace('contextmenu,', '');
        }
        tinyMCE.init(options);
    }, 0);

}

function highlightSubmitButton(inputId) {
    $('#' + inputId).closest('form').find('button:has(img[src*="floppy_disk"])').addClass('poseidon_modified');
}
function highlightInfoPanelFieldWithButton(panelFieldId) {
    $('#' + panelFieldId + ' input[readonly]').addClass('poseidon_modified');
    highlightSubmitButton(panelFieldId);
}
function addMultiselectBehavior(internalSelectorId, choiceContainerId, addDropdownId, selectDropdownText, isDisabled, isRequired, defaultLabelValue) {
    //this will used for construct default label value if component is not required and nothing selected
    function processLabelDefaultValue(choiceContainer, isRequired, defaultLabelValue) {
        if (choiceContainer.is(":empty") && !isRequired) {
            createSpanElement(defaultLabelValue, choiceContainer, null)
        }
    }

    var internalSelector = $('#' + internalSelectorId);
    var choiceContainer = $('#' + choiceContainerId);
    var addDropdown = $('#' + addDropdownId);
    var selectDropDown = selectDropdownText;

    choiceContainer.append("<div class=\"clear\"></div>");

    if (isDisabled) {
        choiceContainer.empty();
        choiceContainer.append("<div class=\"clear\"></div>");
        addDropdown.empty();
        internalSelector.find(':selected').each(
            function (index, val) {
                createSpanElement(val.text, choiceContainer, null)
            }
        );
        processLabelDefaultValue(choiceContainer, isRequired, defaultLabelValue);
        return;
    }


    addDropdown.change(function () {
        $(this).find("option:selected").each(function (index, element) {
            internalSelector.find("[value='" + $(element).val() + "']").attr("selected", "selected");
            $(element).remove();
        });
        internalSelector.change();
    });

    internalSelector.change(function (element) {
        choiceContainer.empty();
        choiceContainer.append("<div class=\"clear\"></div>");
        addDropdown.empty();
        internalSelector.find(':selected').each(
            function (index, val) {
                createSpanElement(val.text, choiceContainer, function () {
                    val.selected = false;
                    internalSelector.change();
                })
            }
        );
        processLabelDefaultValue(choiceContainer, isRequired, defaultLabelValue);

        addDropdown.append($('<option selected="selected">' + selectDropDown + '</option>'));

        internalSelector.find(':not(:selected)').each(
            function (index, val) {
                var newItem = $(val).clone(false);
                newItem.removeAttr('selected');
                addDropdown.append(newItem);
            }
        );

    });
    addDropdown.change();
}

function showDropBoxComponent(clickedId, targetDropDownId) {
    var clicked = $('#' + clickedId);
    var targetDropDown = $('#' + targetDropDownId);
    clicked.click(
        function () {
            targetDropDown.show();
            targetDropDown.focus();
        })
}

function hideDropBoxComponent(targetDropDownId) {
    var targetDropDown = $('#' + targetDropDownId);
    targetDropDown.blur(
        function () {
            targetDropDown.hide();
        })
}

/**
 * Append JQuery behavior to AbstractEditableLabel to client side component processing.
 *
 * @param buttonId button edit selector
 * @param titleId label representation of editor selector
 * @param editorId editor component selector
 * @param isPassword if password component flag
 * @param autocompleteCSSName CSS class name specific for autocomplete  suggester component
 * @param isRequired required flag
 * @param defaultLabelValue default value for label if component is not required
 *
 */
function abstractEditableLabelProcessor(buttonId, titleId, editorId, isPassword, autocompleteCSSName, isRequired, defaultLabelValue) {
    //this will used for construct default label value if component is not required and nothing selected
    function processSelectorLabelDefaultValue(editor, label, isRequired, defaultLabelValue) {
        var selectedItemText = editor.find(":selected").text();
        if (selectedItemText == "" && !isRequired) {
            selectedItemText = defaultLabelValue;
        }
        label.text(selectedItemText);
    }


    //prepossess default label value if is not required
    var label = $('#' + titleId);
    var editor = $('#' + editorId);
    if (editor.is("select")) {
        processSelectorLabelDefaultValue(editor, label, isRequired, defaultLabelValue);
    }

    // edit button click behavior
    $('#' + buttonId).click(function () {
        $('#' + titleId).hide();
        var editor = $('#' + editorId);
        var editorParent = editor.parent();
        editorParent.show();
        editor.select();
        editor.focus();
    });
    // label click/double click behavior
    var clickHandler = function () {
        $('#' + titleId).hide();
        var editor = $('#' + editorId);
        var editorParent = editor.parent();
        editorParent.show();
        editor.select();
        editor.focus();
    };

    $('#' + titleId).dblclick(clickHandler);
    $('#' + titleId).click(clickHandler);

    // on blur behavior
    $('#' + editorId).blur(function () {
        //if focus was moved to autocomplete component, just noop
        if ($('.' + autocompleteCSSName + ':visible').size() > 0) {
            return;
        }

        var editor = $(this);
        var editorParent = editor.parent();
        var label = $('#' + titleId);
        editorParent.hide();

        if (editor.is("select")) {
            processSelectorLabelDefaultValue(editor, label, isRequired, defaultLabelValue);
        } else if (isPassword) {
            label.text('******');
        } else {
            label.text(editor.val());
        }
        label.show();
    });
}

/**
 * Append JQuery behavior to TagEditableMultiValueLabel to client side component processing.
 * @param editorId autocomplete text field jquery selector
 * @param containerId selected displayed values container jquery selector
 * @param storeId hidden input field with store selected values in coma separated format jquery selector
 * @param titleId label representation of autocomplete text field
 * @param autocompleteCSSName CSS class name specific for autocomplete  suggester component
 * @param valueDelimiter multi value delimiter
 */
function suggestEditableMultiValueLabelProcessor(editorId, containerId, storeId, titleId, autocompleteCSSName, valueDelimiter, isDisabled) {
    var groupDiv = $("<div></div>");
    $('#' + containerId).append(groupDiv);
    groupDiv.toggleClass($('#' + titleId).attr("class"));

    $('#' + titleId).hide();

    groupDiv.append("<div class=\"clear\"></div>");
    //initialise span elements
    if ($('#' + storeId).val() != '') {
        var existedValueArray = $('#' + storeId).val().split(valueDelimiter);
        for (var i = 0; i < existedValueArray.length; i++) {
            if (isDisabled) {
                createSpanElement(existedValueArray[i], groupDiv, null);
            } else {
                createSpanElement(existedValueArray[i], groupDiv, function () {
                    //recalculate store value
                    constructMultiValueStore(storeId, containerId, valueDelimiter);
                });
            }
        }
    }

    if (isDisabled) {
        return;
    }
    $('#' + editorId).val('');

    //attach onBlur action
    $('#' + editorId).blur(function (event) {
        //if focus was moved to autocomplete component, just noop
        if ($('.' + autocompleteCSSName + ':visible').size() > 0) {
            return;
        }

        //add value from input
        var container = $('#' + containerId);
        var editorText = $('#' + editorId).val();

        //skip if editor is empty
        if (editorText == '') {
            $('#' + titleId).hide();
            return;
        }
        //check if already added
        var existedValueArray = $('#' + storeId).val().split(valueDelimiter);
        for (var i = 0; i < existedValueArray.length; i++) {
            if (existedValueArray[i].toLowerCase() == editorText.toLowerCase()) {
                //hide input representation
                $('#' + editorId).val('');
                $('#' + titleId).hide();
                return;
            }
        }
        createSpanElement(editorText, groupDiv, function () {
            //recalculate store value
            constructMultiValueStore(storeId, containerId, valueDelimiter);
        });

        //hide input representation
        $('#' + editorId).val('');
        $('#' + titleId).hide();

        //recalculate store value
        constructMultiValueStore(storeId, containerId, valueDelimiter);
    })
}

function constructMultiValueStore(storeId, containerId, delimiter) {
    var store = $('#' + storeId);
    store.val('');
    $('#' + containerId).find('.choice-item > .elti-inline-editor-title-label').each(function (index) {
        var dl = delimiter;
        if (store.val() == '') {
            dl = '';
        }
        store.val(store.val() + dl + $(this).get(0).title)
    });
}

/**
 * Internal implementation for "create span element from text" mechanism.
 *
 * @param textVal source text value
 * @param container placeholder for newly created element
 * @param onCloseCallback custom on close/delete element callback
 */
function createSpanElement(textVal, container, onCloseCallback) {
    var shownText;
    if (textVal.length > 7) {
        var i = 7;
        while ((textVal[i] == " ") && (i > 0)) {
            i--;
        }
        shownText = textVal.substring(0, i) + "&#8230;";
    } else {
        shownText = textVal;
    }
    var span = $("<div class='choice-item'></div>");
    $(container).children(".clear").before(span);

    var titleLabel = $("<div class='elti-inline-editor-title-label'>" + shownText + "</div>");
    titleLabel.attr("title", textVal);

    span.append(titleLabel);

    if (onCloseCallback != null) {
        var link = $("<a href='javascript:' class='elti-linkbutton'>x</a>");
        link.click(function () {
            titleLabel.remove();
            $(this).remove();
            onCloseCallback();
        });
        titleLabel.after(link);
    }
}