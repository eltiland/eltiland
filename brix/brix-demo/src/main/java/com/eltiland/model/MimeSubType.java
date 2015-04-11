package com.eltiland.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author knorr
 * @version 1.0
 * @since 8/17/12
 */
@Entity
@Table(schema = "public", name = "mime")
public class MimeSubType implements Serializable {
    String type;
    String icon;
    String resourceKey;

    @Id
    @Column(name = "mime_type", unique = true, nullable = false, length = 255)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "icon_name", unique = true, nullable = false, length = 255)
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Column(name = "resource_key", unique = true, nullable = false, length = 255)
    public String getResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MimeSubType)) return false;

        MimeSubType that = (MimeSubType) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }
}
