package com.eltiland.ui.worktop.simple.panel.files;

import com.eltiland.bl.GenericManager;
import com.eltiland.bl.impl.integration.FileUtility;
import com.eltiland.bl.user.UserFileManager;
import com.eltiland.model.file.UserFile;
import com.eltiland.model.user.User;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.ui.common.components.grid.ELTTable;
import com.eltiland.ui.common.components.grid.GridAction;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Panel with files, available for user.
 *
 * @author Aleksey Plotnikov.
 */
public class ProfileAvailablePanel extends BaseEltilandPanel<User> {

    @SpringBean
    private UserFileManager userFileManager;
    @SpringBean
    private GenericManager genericManager;
    @SpringBean
    private FileUtility fileUtility;

    private ELTTable<UserFile> table;

    public ProfileAvailablePanel(String id, IModel<User> userIModel) {
        super(id, userIModel);

        table = new ELTTable<UserFile>("table", 30) {
            @Override
            protected List<IColumn<UserFile>> getColumns() {
                List<IColumn<UserFile>> columns = new ArrayList<>();
                columns.add(new PropertyColumn<UserFile>(new ResourceModel("name.column"), "file.name", "file.name"));
                columns.add(new PropertyColumn<UserFile>(new ResourceModel("date.column"), "uploadDate", "uploadDate"));
                columns.add(new PropertyColumn<UserFile>(new ResourceModel("user.column"), "owner.name", "owner.name"));
                return columns;
            }

            @Override
            protected Iterator getIterator(int first, int count) {
                return userFileManager.getAvailableFileSearchList(ProfileAvailablePanel.this.getModelObject(),
                        first, count, getSearchString(), getSort().getProperty(), getSort().isAscending()).iterator();
            }

            @Override
            protected int getSize() {
                return userFileManager.getAvailableFileSearchCount(
                        ProfileAvailablePanel.this.getModelObject(), getSearchString());
            }

            @Override
            protected List<GridAction> getGridActions(IModel<UserFile> rowModel) {
                return new ArrayList<>(Arrays.asList(GridAction.DOWNLOAD));
            }

            @Override
            protected String getActionTooltip(GridAction action) {
                if (action.equals(GridAction.DOWNLOAD)) {
                    return ProfileAvailablePanel.this.getString("download.tooltip");
                } else {
                    return StringUtils.EMPTY;
                }
            }

            @Override
            protected void onClick(IModel<UserFile> rowModel, GridAction action, AjaxRequestTarget target) {
            }

            @Override
            protected String getNotFoundedMessage() {
                return ProfileAvailablePanel.this.getString("no.files");
            }

            @Override
            protected String getFileName(IModel<UserFile> rowModel) {
                genericManager.initialize(rowModel.getObject(), rowModel.getObject().getFile());
                return rowModel.getObject().getFile().getName();
            }

            @Override
            protected InputStream getInputStream(IModel<UserFile> rowModel) throws ResourceStreamNotFoundException {
                genericManager.initialize(rowModel.getObject(), rowModel.getObject().getFile());
                genericManager.initialize(rowModel.getObject().getFile(), rowModel.getObject().getFile().getBody());
                IResourceStream resourceStream = fileUtility.getFileResource(
                        rowModel.getObject().getFile().getBody().getHash());
                return resourceStream.getInputStream();
            }
        };
        add(table);
    }
}
