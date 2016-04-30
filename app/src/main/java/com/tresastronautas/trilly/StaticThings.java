package com.tresastronautas.trilly;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by juansantiagoacev on 4/29/16.
 */
public class StaticThings {

    public static ParseObject selectedGroup;
    public static ParseUser currentUser;
    public static List<ParseObject> userGroups;

    public StaticThings() {

    }

    public static void setCurrentUser(ParseUser currentUser) {
        StaticThings.currentUser = currentUser;
    }

    public static void setSelectedGroup(ParseObject selectedGroup) {
        StaticThings.selectedGroup = selectedGroup;
    }

    public static void setUserGroups(List<ParseObject> userGroups) {
        StaticThings.userGroups = userGroups;
    }

    public static ParseUser getCurrentUser() {
        return currentUser;
    }

    public static ParseObject getSelectedGroup() {
        return selectedGroup;
    }

    public static List<ParseObject> getUserGroups() {
        return userGroups;
    }
}
