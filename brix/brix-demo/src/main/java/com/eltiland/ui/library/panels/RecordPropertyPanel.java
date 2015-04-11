package com.eltiland.ui.library.panels;

import com.eltiland.model.library.*;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.model.GenericDBModel;
import com.eltiland.ui.library.components.selector.SelectTypeField;
import com.eltiland.ui.library.panels.type.ArchivePropertyPanel;
import com.eltiland.ui.library.panels.type.DocumentPropertyPanel;
import com.eltiland.ui.library.panels.type.ImagePropertyPanel;
import com.eltiland.ui.library.panels.type.VideoPropertyPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;

/**
 * Panel for creating and editing library single entity.
 *
 * @author Aleksey Plotnikov.
 */
public abstract class RecordPropertyPanel extends BaseEltilandPanel<LibraryRecord> {

    private WebMarkupContainer panelContainer = new WebMarkupContainer("panelContainer");
    private boolean createMode = true;

    private static final String CSS = "static/css/library/record_property.css";

    /**
     * Ctor for creating new entity.
     *
     * @param id markup id.
     */
    public RecordPropertyPanel(String id) {
        super(id);
        this.createMode = true;
        addComponents();
    }

    /**
     * Ctor for editing new entity.
     *
     * @param id markup id.
     */
    protected RecordPropertyPanel(String id, IModel<LibraryRecord> libraryRecordIModel) {
        super(id, libraryRecordIModel);

        this.createMode = false;
        addComponents();
    }

    private void addComponents() {
        add(new Label("header", getString(createMode ? "title" : "editTitle")));

        add(panelContainer.setOutputMarkupId(true));
        add(new SelectTypeField("selectType") {
            @Override
            protected void onSelect(AjaxRequestTarget target, Class<? extends LibraryRecord> clazz) {
                if (clazz.equals(LibraryDocumentRecord.class) || clazz.equals(LibraryPresentationRecord.class)) {
                    panelContainer.replace(new DocumentPropertyPanel(
                            "dataPanel", clazz.equals(LibraryDocumentRecord.class)) {
                        @Override
                        protected void onCreateRecord(AjaxRequestTarget target) {
                            RecordPropertyPanel.this.onCreateRecord(target);
                        }
                    });
                } else if (clazz.equals(LibraryVideoRecord.class)) {
                    panelContainer.replace(new VideoPropertyPanel("dataPanel") {
                        @Override
                        protected void onCreateRecord(AjaxRequestTarget target) {
                            RecordPropertyPanel.this.onCreateRecord(target);
                        }
                    });
                } else if (clazz.equals(LibraryImageRecord.class)) {
                    panelContainer.replace(new ImagePropertyPanel("dataPanel") {
                        @Override
                        protected void onCreateRecord(AjaxRequestTarget target) {
                            RecordPropertyPanel.this.onCreateRecord(target);
                        }
                    });
                } else if (clazz.equals(LibraryArchiveRecord.class)) {
                    panelContainer.replace(new ArchivePropertyPanel("dataPanel") {
                        @Override
                        protected void onCreateRecord(AjaxRequestTarget target) {
                            RecordPropertyPanel.this.onCreateRecord(target);
                        }
                    });
                }
                target.add(panelContainer);
            }

            @Override
            public boolean isVisible() {
                return createMode;
            }
        });

        if (createMode) {
            panelContainer.add(new EmptyPanel("dataPanel"));
        } else {
            Class<? extends LibraryRecord> clazz = RecordPropertyPanel.this.getModelObject().getClass();
            if (clazz.equals(LibraryDocumentRecord.class) || clazz.equals(LibraryPresentationRecord.class)) {
                panelContainer.add(new DocumentPropertyPanel("dataPanel",
                        new GenericDBModel<>(LibraryRecord.class, getModelObject()),
                        clazz.equals(LibraryDocumentRecord.class)) {
                    @Override
                    protected void onSaveRecord(AjaxRequestTarget target) {
                        RecordPropertyPanel.this.onSaveRecord(target);
                    }
                });
            } else if (clazz.equals(LibraryVideoRecord.class)) {
                panelContainer.add(new VideoPropertyPanel("dataPanel",
                        new GenericDBModel<>(LibraryVideoRecord.class, (LibraryVideoRecord) getModelObject())) {
                    @Override
                    protected void onSaveRecord(AjaxRequestTarget target) {
                        RecordPropertyPanel.this.onSaveRecord(target);
                    }
                });
            } else if (clazz.equals(LibraryImageRecord.class)) {
                panelContainer.add(new ImagePropertyPanel("dataPanel",
                        new GenericDBModel<>(LibraryImageRecord.class, (LibraryImageRecord) getModelObject())) {
                    @Override
                    protected void onSaveRecord(AjaxRequestTarget target) {
                        RecordPropertyPanel.this.onSaveRecord(target);
                    }
                });
            } else if (clazz.equals(LibraryArchiveRecord.class)) {
                panelContainer.add(new ArchivePropertyPanel("dataPanel",
                        new GenericDBModel<>(LibraryArchiveRecord.class, (LibraryArchiveRecord) getModelObject())) {
                    @Override
                    protected void onSaveRecord(AjaxRequestTarget target) {
                        RecordPropertyPanel.this.onSaveRecord(target);
                    }
                });
            }
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(CSS);
    }

    protected void onCreateRecord(AjaxRequestTarget target) {

    }

    protected void onSaveRecord(AjaxRequestTarget target) {

    }
}
