package com.cityzen.cityzen.Utils.MapUtils.OpeningHours;

/**
 * Created by Valdio Veliu on 05/05/2017.
 */

public enum DAY {
    Mo(1),
    Tu(2),
    We(3),
    Th(4),
    Fr(5),
    Sa(6),
    Su(7);

    private int dayID;

    DAY(int dayID) {
        this.dayID = dayID;
    }

    public int getDayID() {
        return dayID;
    }

    public static DAY getDAY(int dayID) {
        switch (dayID) {
            case 1:
                return Mo;
            case 2:
                return Tu;
            case 3:
                return We;
            case 4:
                return Th;
            case 5:
                return Fr;
            case 6:
                return Sa;
            case 7:
                return Su;
            default:
                return Mo;
        }
    }
}