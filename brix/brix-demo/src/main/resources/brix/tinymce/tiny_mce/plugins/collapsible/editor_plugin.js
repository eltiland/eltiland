(function () {
    tinymce.PluginManager.requireLangPack('collapsible');
    tinymce.create('tinymce.plugins.CollapsiblePlugin', {init:function (ed, url) {
        ed.addButton('collapsible', {title:'collapsible.desc', onclick:function () {
            var parentSelectionNode = ed.selection.getNode();
            var selectionContent = ed.selection.getContent();
            if (parentSelectionNode.tagName.toLowerCase() == "ol" || parentSelectionNode.tagName.toLowerCase() == "ul") {
                $(parentSelectionNode).wrap('<div class="jsCollapsable-temp"/>');
                selectionContent = null
            }
            if (parentSelectionNode.tagName.toLowerCase() == "tr" || parentSelectionNode.tagName.toLowerCase() == "tbody" || parentSelectionNode.tagName.toLowerCase() == "thead" || parentSelectionNode.tagName.toLowerCase() == "tfoot") {
                parentSelectionNode = $(parentSelectionNode).closest("table");
                $(parentSelectionNode).wrap('<div class="jsCollapsable-temp"/>');
                selectionContent = null
            }
            if (selectionContent != null) {
                ed.selection.setContent("<div class='jsCollapsable-temp'>" + selectionContent + "</div>")
            }
            $(parentSelectionNode).find(".jsCollapsable").each(function () {
                var node = $(this).html();
                $(this).after(node);
                $(this).remove()
            });
            $(parentSelectionNode).closest(".jsCollapsable").each(function () {
                var node = $(this).html();
                $(this).after(node);
                $(this).remove()
            });
            $(ed.dom.getRoot()).find(".jsCollapsable-temp").removeClass("jsCollapsable-temp").addClass("jsCollapsable");
            ed.save()
        }, image:url + '/img/collapse_icon.png'})
    }, getInfo:function () {
        return{longname:'Collapsible plugin', author:'Igor Cherednichenko', authorurl:'http://logicify.com', infourl:'http://logicify.com', version:"1.0"}
    }});
    tinymce.PluginManager.add('collapsible', tinymce.plugins.CollapsiblePlugin)
})();