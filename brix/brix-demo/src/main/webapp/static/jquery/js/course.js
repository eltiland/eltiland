/**
 * Created with IntelliJ IDEA.
 * User: Aleks
 * Date: 22.10.13
 * Time: 20:04
 * To change this template use File | Settings | File Templates.
 */

function formatTreeItem() {
    var rows = document.getElementsByClassName("row");
    var selected_rows = document.getElementsByClassName("row-selected");
    formatTreeString(rows);
    formatTreeString(selected_rows);
}

function formatTreeString(rows) {
    for (var i = 0; i < rows.length; i++) {
        var a = rows[i].getElementsByClassName("a_")[0];
        var nodelink = a.getElementsByClassName("nodelink")[0];
        var text = nodelink.getElementsByClassName("text")[0].innerHTML;

        var height = 30 + (Math.floor(text.length / 25) * 20);
        rows[i].style.height = height + "px";

        var link = a.getElementsByClassName("link")[0];
        if (link != null) {
            var junction = link.getElementsByClassName("junction")[0];
            if (junction != null) {
                junction.style.height = height + "px";
            }
        }

        var indent_line = a.getElementsByClassName("indent-line")[0];
        if (indent_line != null) {
            indent_line.style.height = height + "px";
        }
    }
}

function deSelectItems(id) {
    var elements = document.getElementsByClassName("selected");
    for (var i = 0; i < elements.length; i++) {
        var element = elements[i];
        var classname = elements[i].className = "infoPanel";
    }
    hideControls();
    if (id != null) {
        var selected_element = document.getElementById(id);
        selected_element.className = "infoPanel selected";
        var controlElement = selected_element.getElementsByClassName('elementControl');
        if (controlElement != null) {
            $(controlElement).show('slow');
        }
    }
}

function hideControls() {
    var elements = document.getElementsByClassName("elementControl");
    for (var i = 0; i < elements.length; i++) {
        elements[i].style.display = 'none';
    }
}

function filterGoogleStyles() {
    var elements = $("style");
    var toDelete = false;
    for (var i = 0; i < elements.length; i++) {
        if (elements[i].id == "null") {
            toDelete = true;
            break;
        }
    }
    if (toDelete) {
        var lastId = elements[elements.length - 1].id;
        if (lastId == "null") {
            elements[elements.length - 2].remove();
        } else {
            elements[elements.length - 1].remove();
        }
    }
}

function printCourseItemContent() {

    var showPopup = false;
    var w = window.open();

    w.document.write($(".documentContent").html());

    var h = w.document.getElementsByTagName('head')[0];
    var s = w.document.createElement('style');

    s.appendChild(document.createTextNode($("style")[1].innerHTML));
    h.appendChild(s);


    if (navigator.userAgent.toLowerCase().indexOf('chrome') > -1) {
        w.onbeforeunload = function () {
            if (!showPopup) {
                showPopup = true;
                return 'Для того, чтоб отменить печать, останьтесь на данной странице и используйте кнопку Отмена".\n';
            }
        };
    }

    w.print();
    w.close();
}
