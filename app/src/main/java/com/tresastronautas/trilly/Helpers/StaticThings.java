package com.tresastronautas.trilly.Helpers;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by juansantiagoacev on 4/29/16!
 */
public class StaticThings {

    public static ParseObject selectedGroup, selectedUser, selectedViaje;
    public static ParseUser currentUser;
    public static List<ParseObject> userGroups;
    public static ParseObject statistics;

    public StaticThings() {

    }

    public static ParseObject getStatistics() {
        return statistics;
    }

    public static void setStatistics(ParseObject statistics) {
        StaticThings.statistics = statistics;
    }

    public static ParseUser getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(ParseUser currentUser) {
        StaticThings.currentUser = currentUser;
    }

    public static ParseObject getSelectedGroup() {
        return selectedGroup;
    }

    public static void setSelectedGroup(ParseObject selectedGroup) {
        StaticThings.selectedGroup = selectedGroup;
    }

    public static List<ParseObject> getUserGroups() {
        return userGroups;
    }

    public static void setUserGroups(List<ParseObject> userGroups) {
        StaticThings.userGroups = userGroups;
    }

    public static ParseObject getSelectedUser() {
        return selectedUser;
    }

    public static void setSelectedUser(ParseObject selectedUser) {
        StaticThings.selectedUser = selectedUser;
    }

    public static ParseObject getSelectedViaje() {
        return selectedViaje;
    }

    public static void setSelectedViaje(ParseObject selectedViaje) {
        StaticThings.selectedViaje = selectedViaje;
    }
}
