(function () {
    // Load plugin specific language pack
    tinymce.PluginManager.requireLangPack('collapsible');

    tinymce.create('tinymce.plugins.CollapsiblePlugin', {
        /**
         * Initializes the plugin, this will be executed after the plugin has been created.
         * This call is done before the editor instance has finished it's initialization so use the onInit event
         * of the editor instance to intercept that event.
         *
         * @param {tinymce.Editor} ed Editor instance that the plugin is initialized in.
         * @param {string} url Absolute URL to where the plugin is located.
         */
        init:function (ed, url) {


            // Register button
            ed.addButton('collapsible', {
                title:'collapsible.desc',
                onclick:function () {
                    var parentSelectionNode = ed.selection.getNode();
                    var selectionContent = ed.selection.getContent();

                    //special behaviour for composite node - lists
                    if (parentSelectionNode.tagName.toLowerCase() == "ol"
                        || parentSelectionNode.tagName.toLowerCase() == "ul") {

                        $(parentSelectionNode).wrap('<div class="jsCollapsable-temp"/>');
                        selectionContent = null;
                    }

                    //special behaviour for composite node - table
                    if (parentSelectionNode.tagName.toLowerCase() == "tr"
                        || parentSelectionNode.tagName.toLowerCase() == "tbody"
                        || parentSelectionNode.tagName.toLowerCase() == "thead"
                        || parentSelectionNode.tagName.toLowerCase() == "tfoot") {

                        parentSelectionNode = $(parentSelectionNode).closest("table");
                        $(parentSelectionNode).wrap('<div class="jsCollapsable-temp"/>');
                        selectionContent = null;
                    }

                    //default node processing
                    if (selectionContent != null) {
                        ed.selection.setContent("<div class='jsCollapsable-temp'>" + selectionContent + "</div>");
                    }

                    $(parentSelectionNode).find(".jsCollapsable").each(function () {
                        var node = $(this).html();
                        $(this).after(node);
                        $(this).remove();
                    });
                    $(parentSelectionNode).closest(".jsCollapsable").each(function () {
                        var node = $(this).html();
                        $(this).after(node);
                        $(this).remove();
                    });
                    $(ed.dom.getRoot()).find(".jsCollapsable-temp")
                        .removeClass("jsCollapsable-temp").addClass("jsCollapsable");

                    ed.save();
                },
                image:url + '/img/collapse_icon.png'
            });
        },

        /**
         * Returns information about the plugin as a name/value array.
         * The current keys are longname, author, authorurl, infourl and version.
         *
         * @return {Object} Name/value array containing information about the plugin.
         */
        getInfo:function () {
            return {
                longname:'Collapsible plugin',
                author:'Igor Cherednichenko',
                authorurl:'http://logicify.com',
                infourl:'http://logicify.com',
                version:"1.0"
            };
        }
    })
    ;

// Register plugin
    tinymce.PluginManager.add('collapsible', tinymce.plugins.CollapsiblePlugin);
})();