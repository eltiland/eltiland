package com.eltiland.ui.course.control.data.panel.item;

import com.eltiland.exceptions.CourseException;
import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.model.course2.content.ELTCourseItem;
import com.eltiland.model.course2.content.google.ELTContentCourseItem;
import com.eltiland.model.course2.content.google.ELTDocumentCourseItem;
import com.eltiland.model.course2.content.google.ELTPresentationCourseItem;
import com.eltiland.model.course2.content.group.ELTGroupCourseItem;
import com.eltiland.model.course2.content.test.ELTTestCourseItem;
import com.eltiland.model.course2.content.video.ELTVideoCourseItem;
import com.eltiland.model.course2.content.webinar.ELTWebinarCourseItem;
import com.eltiland.ui.common.components.dialog.ELTAlerts;
import com.eltiland.ui.common.components.dialog.ELTDialogPanel;
import com.eltiland.ui.common.components.dialog.callback.IDialogNewCallback;
import com.eltiland.ui.common.model.GenericDBModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel for selecting type of the new course element.
 *
 * @author Aleksey Plotnikov.
 */
public class ItemTypeSelector extends ELTDialogPanel implements IDialogNewCallback<ELTCourseItem> {

    private IDialogActionProcessor<ELTCourseItem> newCallback;

    private TypeItem document;
    private TypeItem content;
    private TypeItem presentation;
    private TypeItem test;
    private TypeItem video;
    private TypeItem webinar;
    private TypeItem group;

    private Class<? extends ELTCourseItem> clazz = null;

    private IModel<ELTCourseBlock> blockModel = new GenericDBModel<>(ELTCourseBlock.class);

    public ItemTypeSelector(String id) {
        super(id);

        document = new TypeItem("document", ELTDocumentCourseItem.class) {
            @Override
            protected void onClick(AjaxRequestTarget target, boolean newValue) {
                if (newValue) {
                    clazz = ELTDocumentCourseItem.class;
                    content.reset(target);
                    presentation.reset(target);
                    test.reset(target);
                    video.reset(target);
                    webinar.reset(target);
                    group.reset(target);
                } else {
                    clazz = null;
                }
            }

            @Override
            protected String getImage() {
                return "document";
            }
        };

        content = new TypeItem("editor_doc", ELTContentCourseItem.class) {
            @Override
            protected void onClick(AjaxRequestTarget target, boolean newValue) {
                if (newValue) {
                    clazz = ELTContentCourseItem.class;
                    document.reset(target);
                    presentation.reset(target);
                    test.reset(target);
                    video.reset(target);
                    webinar.reset(target);
                    group.reset(target);
                } else {
                    clazz = null;
                }
            }

            @Override
            protected String getImage() {
                return "document";
            }
        };

        presentation = new TypeItem("presentation", ELTPresentationCourseItem.class) {
            @Override
            protected void onClick(AjaxRequestTarget target, boolean newValue) {
                if (newValue) {
                    clazz = ELTPresentationCourseItem.class;
                    document.reset(target);
                    content.reset(target);
                    test.reset(target);
                    video.reset(target);
                    webinar.reset(target);
                    group.reset(target);
                } else {
                    clazz = null;
                }
            }

            @Override
            protected String getImage() {
                return "presentation";
            }
        };

        test = new TypeItem("test", ELTTestCourseItem.class) {
            @Override
            protected void onClick(AjaxRequestTarget target, boolean newValue) {
                if (newValue) {
                    clazz = ELTTestCourseItem.class;
                    document.reset(target);
                    content.reset(target);
                    presentation.reset(target);
                    video.reset(target);
                    webinar.reset(target);
                    group.reset(target);
                } else {
                    clazz = null;
                }
            }

            @Override
            protected String getImage() {
                return "test";
            }
        };

        video = new TypeItem("video", ELTVideoCourseItem.class) {
            @Override
            protected void onClick(AjaxRequestTarget target, boolean newValue) {
                if (newValue) {
                    clazz = ELTVideoCourseItem.class;
                    document.reset(target);
                    content.reset(target);
                    test.reset(target);
                    presentation.reset(target);
                    webinar.reset(target);
                    group.reset(target);
                } else {
                    clazz = null;
                }
            }

            @Override
            protected String getImage() {
                return "video";
            }
        };

        webinar = new TypeItem("webinar", ELTWebinarCourseItem.class) {
            @Override
            protected void onClick(AjaxRequestTarget target, boolean newValue) {
                if (newValue) {
                    clazz = ELTWebinarCourseItem.class;
                    document.reset(target);
                    content.reset(target);
                    test.reset(target);
                    video.reset(target);
                    presentation.reset(target);
                    group.reset(target);
                } else {
                    clazz = null;
                }
            }

            @Override
            protected String getImage() {
                return "webinar";
            }
        };

        group = new TypeItem("group", ELTGroupCourseItem.class) {
            @Override
            protected void onClick(AjaxRequestTarget target, boolean newValue) {
                if (newValue) {
                    clazz = ELTGroupCourseItem.class;
                    document.reset(target);
                    content.reset(target);
                    test.reset(target);
                    video.reset(target);
                    webinar.reset(target);
                    presentation.reset(target);
                } else {
                    clazz = null;
                }
            }

            @Override
            protected String getImage() {
                return "group";
            }

            @Override
            public boolean isVisible() {
                return isGroupVisible();
            }
        };


        form.add(document);
        form.add(content);
        form.add(presentation);
        form.add(test);
        form.add(video);
        form.add(webinar);
        form.add(group);
        form.setMultiPart(true);
    }

    @Override
    protected String getHeader() {
        return getString("selector.header");
    }

    @Override
    protected List<EVENT> getActionList() {
        return new ArrayList<>(Arrays.asList(EVENT.Select));
    }

    public void initData(IModel<ELTCourseBlock> blockModel) {
        this.blockModel = blockModel;
    }

    @Override
    protected void eventHandler(EVENT event, AjaxRequestTarget target) {
        if (event.equals(EVENT.Select)) {
            if (clazz == null) {
                ELTAlerts.renderErrorPopup(getString("error.notselected"), target);
            } else {
                try {
                    ELTCourseItem item = clazz.newInstance();
                    item.setBlock(blockModel.getObject());
                    newCallback.process(new GenericDBModel<>(ELTCourseItem.class, item), target);
                } catch (InstantiationException | IllegalAccessException e) {
                    ELTAlerts.renderErrorPopup(CourseException.ERROR_ITEM_CREATE, target);
                }
            }
        }
    }

    @Override
    public String getVariation() {
        return "styled";
    }

    @Override
    public void setNewCallback(IDialogActionProcessor<ELTCourseItem> callback) {
        newCallback = callback;
    }

    protected boolean isGroupVisible() {
        return true;
    }
}
