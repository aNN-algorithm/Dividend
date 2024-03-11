package com.dayone.model.constants;

public enum Month {

    JAN("Jan", 1),
    FEB("Feb", 1),
    MAR("Mar", 1),
    APR("Apr", 1),
    MAY("May", 1),
    JUN("Jun", 1),
    JUL("Jul", 1),
    AUG("Aug", 1),
    SEP("Sep", 1),
    OCT("Oct", 1),
    NOB("Nov", 1),
    DEC("Dec", 1);

    private String s;
    private int number;

    Month(String s, int n) {
        this.s = s;
        this.number = n;
    }

    public static int strToNumber(String s) {
        for (var m : Month.values()) {
            if (m.s.equals(s)) {
                return m.number;
            }
        }

        return -1;
    }
}
