package com.cityzen.cityzen.Utils.MapUtils.Search.nominatimparser;

public class BoundingBox {

    private double[] boundingbox;

    public BoundingBox(){
        this.boundingbox = new double[4];
    }
    public double[] getBoundingBox(){return boundingbox;}

    public void setBound(int i, double l){
        boundingbox[i]= l;
    }
}
