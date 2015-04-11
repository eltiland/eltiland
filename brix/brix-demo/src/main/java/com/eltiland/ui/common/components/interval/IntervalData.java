package com.eltiland.ui.common.components.interval;

import java.io.Serializable;
import java.util.Date;

/**
 * Class for representing date interval.
 *
 * @author Aleksey Plotnikov
 */
class IntervalData implements Serializable {
    private Date beginDate;
    private Date endDate;

    public IntervalData(Date beginDate, Date endDate) {
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}
