package com.tresastronautas.trilly;

/**
 * Created by JuanSantiagoAcev on 25/02/16!
 */
public class ParseConstants {

    public enum User {
        NAME("_User"),
        STATS("stats"),
        FIRST("nombre"),
        LAST("apellido"),
        FBID("facebookID"),
        GROUPS("grupos"),
        PIC("picture"),
        KG("peso"),
        EDAD("edad"),
        ALTURA("altura");

        private String value;

        User(String v) {
            value = v;
        }

        public String val() {
            return value;
        }
    }

    public enum Estadistica {
        NAME("Estadistica"),
        KM("kmRecorridos"),
        TIME("tiempo"),
        USER("user"),
        CURRENT_TREE("currentTree"),
        SAVED_TREES("savedTrees"),
        GAS("gas"),
        MONEY("money"),
        CO2("kgCO2"),
        CAL("cal");

        private String value;

        Estadistica(String v) {
            value = v;
        }

        public String val() {
            return value;
        }
    }

    public enum Ruta {
        NAME("Ruta"),
        PATH("path"),
        KM("kmRecorridos"),
        ORIGIN("origen"),
        TIME("tiempo"),
        USER("usuario"),
        VEL("velPromedio"),
        CAL("cal");

        private String value;

        Ruta(String v) {
            value = v;
        }

        public String val() {
            return value;
        }
    }

    public enum Path {
        NAME("Path"),
        DATA("data");

        private String value;

        Path(String v) {
            value = v;
        }

        public String val() {
            return value;
        }
    }

    public enum Grupo {
        NAME("Grupo"),
        GROUP_NAME("nombre"),
        ABOUT("descripcion"),
        TYPE("tipo"),
        KM("kmRecorridos"),
        CO2("kgCO2"),
        USERS("users"),
        FBUSERS("fbUsers"),
        SAVED_TREES("savedTrees"),
        USER_COUNT("userCount");

        private String value;

        Grupo(String v) {
            value = v;
        }

        public String val() {
            return value;
        }
    }
}
