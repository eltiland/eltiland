package com.eltiland.ui.course.control.data.panel.block;

import com.eltiland.model.course2.content.ELTCourseBlock;
import com.eltiland.ui.common.BaseEltilandPanel;
import com.eltiland.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * block additional information panel.
 *
 * @author Aleksey Plotnikov.
 */
public class BlockInfoPanel extends BaseEltilandPanel<ELTCourseBlock> {
    /**
     * panel ctor.
     *
     * @param id                   markup id.
     * @param eltCourseBlockIModel block model.
     */
    public BlockInfoPanel(String id, IModel<ELTCourseBlock> eltCourseBlockIModel) {
        super(id, eltCourseBlockIModel);

        boolean opened = getModelObject().getDefaultAccess();
        Label access = new Label("access", getString(opened ? "open" : "closed"));
        access.add(new AttributeModifier("class", opened ? "active_item center_item" : "disactive_item center_item"));
        add(access);

        String dateString = StringUtils.EMPTY;
        if (getModelObject().getStartDate() != null && getModelObject().getEndDate() != null) {
            dateString = String.format(getString("date.limit"),
                    DateUtils.formatDate(getModelObject().getStartDate()),
                    DateUtils.formatDate(getModelObject().getEndDate()));
        }
        add(new Label("dateLimit", dateString));
    }
}
