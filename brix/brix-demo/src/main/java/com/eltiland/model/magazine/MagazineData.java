package com.eltiland.model.magazine;

import java.math.BigDecimal;

/**
 * Magazine data for output in letter to admin.
 * @author Aleksey Plotnikov.
 */
public class MagazineData {
    private String name;
    private BigDecimal price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
