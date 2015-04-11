package com.eltiland.test;

import com.eltiland.model.Pei;

/**
 * Created with IntelliJ IDEA.
 * User: nklimenko
 * Date: 28.01.13
 * Time: 16:48
 * To change this template use File | Settings | File Templates.
 */
public class PeiFactory {
    private static final String PEI_NAME = "testpei";

    public static Pei createPei() {
        return createPei(PEI_NAME);
    }

    private static Pei createPei(String peiName) {
        Pei pei = new Pei();
        pei.setEmail("testpei@mailinator.com");
        pei.setName(peiName);
        pei.setManager("testmanager");
        pei.setPhone("22-22-99");

        return pei;
    }
}
