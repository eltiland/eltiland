// Loads a plugin from an external URL
//tinymce.PluginManager.load('collapsible', '/static/jquery/plugins/collapsible/editor_plugin.js');

function newWysiwyg(textAreaId) {
    tinyMCE.init({

        // General options
        height:500,
        mode:"exact",
        elements:textAreaId,
        theme:"advanced",
        skin:"o2k7",
        plugins:"table,brix,safari,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,inlinepopups, youtube, collapsible",
        relative_urls:"false",

        // Theme options
        theme_advanced_buttons1:"removeformat,bold,italic,underline,strikethrough,|,forecolor,|,justifyleft,justifycenter,justifyright,justifyfull,|,styleselect,formatselect,fontselect,fontsizeselect",
        theme_advanced_buttons2:"hr,|,pastetext,pasteword,|,search,|,bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,image,youtube,code,|,table,tablecontrols, |, collapsible",
        theme_advanced_buttons3:"",
        theme_advanced_buttons4:"",
        theme_advanced_toolbar_location:"top",
        theme_advanced_toolbar_align:"left",
        theme_advanced_statusbar_location:"bottom",
        theme_advanced_resizing:true,
        theme_advanced_font_sizes:"10px,12px,13px,14px,16px,18px,19px,20px",
        font_size_style_values:"10px,12px,13px,14px,16px,18px,19px,20px",
        theme_advanced_fonts:"Andale Mono=andale mono,times;Arial=arial,helvetica,sans-serif;Arial Black=arial black,avant garde;Book Antiqua=book antiqua,palatino;Comic Sans MS=comic sans ms,sans-serif;Courier New=courier new,courier;Georgia=georgia,palatino;Helvetica=helvetica;Impact=impact,chicago;Symbol=symbol;Tahoma=tahoma,arial,helvetica,sans-serif;Terminal=terminal,monaco;Times New Roman=times new roman,times;Trebuchet MS=trebuchet ms,geneva;Verdana=verdana,geneva;Webdings=webdings;Wingdings=wingdings,zapf dingbats",

        /*
         This settings was removed from version 3.3
         http://www.tinymce.com/wiki.php/Configuration:removeformat_selector

         Since 3.3 used this format (example)
         http://www.tinymce.com/wiki.php/Configuration:formats
         formats : {
         removeformat : [
         {selector : 'b,strong,em,i,font,u,strike', remove : 'all', split : true, expand : false, block_expand : true, deep : true},
         {selector : 'span', attributes : ['style', 'class'], remove : 'empty', split : true, expand : false, deep : true},
         {selector : '*', attributes : ['style', 'class'], split : false, expand : false, deep : true}
         ]
         },
         */
        removeformat_selector:'*',

        // Example content CSS (should be your site CSS)
        content_css:"css/content.css,static/css/static_content.css",
        file_browser_callback:"eltilandFileUploader",
        onchange_callback:function (inst) {
            if (inst.isDirty()) {
                inst.save();
            }
            return true; // Continue handling
        },
        handle_event_callback:function (e) {
            if (this.isDirty()) {
                this.save();
            }
            return true; // Continue handling
        },
        // Drop lists for link/image/media/template dialogs
        template_external_list_url:"lists/template_list.js",
        external_link_list_url:"lists/link_list.js",
        external_image_list_url:"lists/image_list.js",
        media_external_list_url:"lists/media_list.js",

        // Replace values for the template plugin
        template_replace_values:{
            username:"Some User",
            staffid:"991234"
        },

        setup: function(ed) {
            ed.onInit.add(function(ed) {
                var window = Wicket.Window.get();
                if( window != null ) {
                    window.center();
                }
            });
        }
    });
}

function eltilandFileUploader(field_name, url, type, win) {

    // alert("Field_Name: " + field_name + "nURL: " + url + "nType: " + type + "nWin: " + win); // debug/testing

    /* If you work with sessions in PHP and your client doesn't accept cookies you might need to carry
     the session name and session ID in the request string (can look like this: "?PHPSESSID=88p0n70s9dsknra96qhuk6etm5").
     These lines of code extract the necessary parameters and add them back to the filebrowser URL again. */

    var cmsURL = tinyMCEPreInit.imageUploadPagePath;  // script URL - use an absolute path!
    if (cmsURL.indexOf("?") < 0) {
        //add the type as the only query parameter
        cmsURL = cmsURL + "?type=" + type;
    }
    else {
        //add the type as an additional query parameter
        // (PHP session ID is now included if there is one at all)
        cmsURL = cmsURL + "&type=" + type;
    }

    tinyMCE.activeEditor.windowManager.open({
        file:cmsURL,
        title:'My File Browser',
        width:600, // Your dimensions may differ - toy around with them!
        height:200,
        resizable:"yes",
        inline:"yes", // This parameter only has an effect if you use the inlinepopups plugin!
        close_previous:"no"
    }, {
        window:win,
        input:field_name
    });
    return false;
}
