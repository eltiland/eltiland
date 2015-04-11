package com.eltiland.test;

import com.eltiland.bl.utils.HashesUtils;
import com.eltiland.model.user.Child;
import com.eltiland.model.user.User;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: nklimenko
 * Date: 31.01.13
 * Time: 11:40
 * To change this template use File | Settings | File Templates.
 */
public class UserFactory {
    private static final String TACHER_NAME = "testTeacherName";
    private static final String PARENT_NAME = "testParentName";
    private static final String PARENT_EMAIL = "testnewparentemail@mailinator.com";
    private static final String SIMPLEUSERNAME = "testSimpleUserName";

    public static Child createChild(/*Parent parent*/) {
        Child child = new Child();
        child.setName("testParentChild" + Calendar.getInstance().getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.getInstance().get(1) - 5, Calendar.JANUARY, 30);
        child.setBirthDate(calendar.getTime());
//        child.setParent(parent);

        return child;
    }

    private static User createSimpleUser(String simpleUserName) {
        User simpleUser = new User();
        simpleUser.setName(simpleUserName);
        simpleUser.setEmail("newSimpleUser@mailinator.com");
        simpleUser.setPassword(HashesUtils.getSHA1inHex(SIMPLEUSERNAME));

        return simpleUser;
    }
}
