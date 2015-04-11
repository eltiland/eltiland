package com.eltiland.model;

import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.util.Set;

/**
 * Postal address of the organization or a person.
 */
@Entity
@Table(name = "postal_address", schema = "public")
@NamedQueries({
        @NamedQuery(name = "address.cities.suggest", query = "select distinct p.city from PostalAddress p where p.city like :input")
})
@Indexed
public class PostalAddress extends AbstractIdentifiable {
    private String countryCode;
    private String city;
    private String addressLine;
    private String postalCode;
    private Set<Pei> peis;

    @OneToMany(mappedBy = "address")
    @ContainedIn
    public Set<Pei> getPeis() {
        return peis;
    }

    public void setPeis(Set<Pei> peis) {
        this.peis = peis;
    }

    @Column(name = "country_code", nullable = false)
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Column(name = "city", nullable = false)
    @Field
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "address_line", nullable = false)
    @Field
    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    @Column(name = "postal_code", nullable = true)
    @Field
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        if (postalCode != null) {
            return String.format("%s, %s, %s", addressLine, city, postalCode);
        } else {
            return String.format("%s, %s", addressLine, city);
        }
    }
}
