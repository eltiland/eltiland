package com.eltiland.model.subscribe;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: igor
 * Date: 8/8/12
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubscribeRequestData {
    public final static String REQUEST_GROUP_SUBSCRIBE_LETTER_TO_ADMIN = "templates/requestGroupSubscribeMessageToAdmin.fo";
    public final static String REQUEST_SUBSCRIBE_LETTER_TO_ADMIN = "templates/requestSubscribeMessageToAdmin.fo";
    public final static String REQUEST_GROUP_SUBSCRIBE_LETTER_TO_SUBSCRIBER = "templates/requestGroupSubscribeMessageToSubscriber.fo";
    public final static String REQUEST_SUBSCRIBE_LETTER_TO_SUBSCRIBER = "templates/requestSubscribeMessageToSubscriber.fo";
    public final static String SUBSCRIBE_PEI_NAME = "subscribe_pei_name";
    public final static String SUBSCRIBE_PEI_ADDRESS = "subscribe_pei_address";
    public final static String SUBSCRIBE_PEI_EMAIL = "subscribe_pei_email";
    public final static String SUBSCRIBE_PEI_PRINCIPAL = "subscribe_pei_principal";
    public final static String SUBSCRIBE_PEI_TELEPHONE = "subscribe_pei_telephone";
    public final static String SUBSCRIBE_MAGAZINE_COUNT = "subscribe_magazine_count";
    public final static String SUBSCRIBE_SUBSCRIBE_PERIOD = "subscribe_subscribe_period";
    public final static String SUBSCRIBE_ORDER_NUMBER = "subscribe_order_number";
    public final static String SUBSCRIBE_GROUP_TYPE = "subscribe_group_type";

    private String peiName;
    private String peiAddress;
    private String peiEmail;
    private String peiPrincipal;
    private String peiTelephone;
    private Integer magazineCount;
    private String subscribePeriod;
    private String orderNumber;
    private String groupType;

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getPeiName() {
        return peiName;
    }

    public void setPeiName(String peiName) {
        this.peiName = peiName;
    }

    public String getPeiAddress() {
        return peiAddress;
    }

    public void setPeiAddress(String peiAddress) {
        this.peiAddress = peiAddress;
    }

    public String getPeiEmail() {
        return peiEmail;
    }

    public void setPeiEmail(String peiEmail) {
        this.peiEmail = peiEmail;
    }

    public String getPeiPrincipal() {
        return peiPrincipal;
    }

    public void setPeiPrincipal(String peiPrincipal) {
        this.peiPrincipal = peiPrincipal;
    }

    public String getPeiTelephone() {
        return peiTelephone;
    }

    public void setPeiTelephone(String peiTelephone) {
        this.peiTelephone = peiTelephone;
    }

    public Integer getMagazineCount() {
        return magazineCount;
    }

    public void setMagazineCount(Integer magazineCount) {
        this.magazineCount = magazineCount;
    }

    public String getSubscribePeriod() {
        return subscribePeriod;
    }

    public void setSubscribePeriod(String subscribePeriod) {
        this.subscribePeriod = subscribePeriod;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Map<String, Object> getTemplateSubstitutionMap() {
        Map<String, Object> map = new HashMap<String, Object>(0);
        map.put(SUBSCRIBE_PEI_NAME, getPeiName());
        map.put(SUBSCRIBE_PEI_ADDRESS, getPeiAddress());
        map.put(SUBSCRIBE_PEI_EMAIL, getPeiEmail());
        map.put(SUBSCRIBE_PEI_PRINCIPAL, getPeiPrincipal());
        map.put(SUBSCRIBE_PEI_TELEPHONE, getPeiTelephone());
        map.put(SUBSCRIBE_MAGAZINE_COUNT, getMagazineCount());
        map.put(SUBSCRIBE_SUBSCRIBE_PERIOD, getSubscribePeriod());
        map.put(SUBSCRIBE_ORDER_NUMBER,getOrderNumber());
        map.put(SUBSCRIBE_GROUP_TYPE,getGroupType());
        return map;
    }
}
