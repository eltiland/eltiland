package com.eltiland.bl;

import com.eltiland.model.PostalAddress;

import java.util.List;

/**
 * Address manager
 */
public interface AddressManager {

    /**
     * FOR SUGGESTBOX: get the city which resembles the search query.
     *
     * @param query search query.
     * @return list of the city names
     */
    List<String> getCitiesSuggestions(String query);

    /**
     * Save new address
     * @param address address to save
     */
    void createAddress(PostalAddress address);

    /**
     * Updates address to DB
     * @param address changes
     */
    void updateAddress(PostalAddress address);
}
