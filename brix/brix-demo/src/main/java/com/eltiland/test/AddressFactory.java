package com.eltiland.test;

import com.eltiland.model.PostalAddress;

/**
 * Created with IntelliJ IDEA.
 * User: nklimenko
 * Date: 28.01.13
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
public class AddressFactory {
    private static final String COUNTRY_CODE = "RU";


    public static PostalAddress createPostalAddress() {
        return createPostalAddress(COUNTRY_CODE);
    }

    public static PostalAddress createPostalAddress(String countryCode) {
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setCountryCode(countryCode);
        postalAddress.setCity("Moscow");

        postalAddress.setPostalCode("testpostalcode");

        postalAddress.setAddressLine("Осенний бульвар, д. 12, к. 49");

        return postalAddress;
    }
}
