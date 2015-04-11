package com.eltiland.bl.magazine;

import com.eltiland.exceptions.EltilandManagerException;
import com.eltiland.model.magazine.Client;

import java.util.List;

/**
 * Manager for Client entity.
 *
 * @author Aleksey Plotnikov
 */
public interface ClientManager {
    /**
     * Creates and persists new Client.
     *
     * @param client client to create.
     * @return new created client.
     */
    Client createClient(Client client) throws EltilandManagerException;

    /**
     * Updates client entity.
     *
     * @param client client to update.
     * @return updated client.
     */
    Client updateClient(Client client) throws EltilandManagerException;

    /**
     * Pay's client magazine invoice.
     *
     * @param client client to pay.
     */
    void payClientMagazines(Client client) throws EltilandManagerException;

    /**
     * @return client info by it's code.
     */
    Client getClientByCode(String code);

    /**
     * @return list of the applyed clients.
     */
    int getAppliedClientsCount();

    /**
     * Get formatted list of clients.
     *
     * @param index     first index.
     * @param count     count of results.
     * @param sProperty sorting property.
     * @param isAsc     sorting direction.
     * @return list of clients.
     */
    List<Client> getAppliedClients(int index, int count, String sProperty, boolean isAsc);
}
