/*
 * Copyright (C) 2020 Andre Kessler (https://github.com/goblingift)
 * All rights reserved
 */
package gift.goblin.HayRackController.controller.model;

/**
 * Form DTO which contains all bound properties of the load-cell-section on
 * settings-page.
 *
 * @author andre
 */
public class LoadCellSettings {

    /**
     * Decides if the load-cells are basically enabled (true) or disabled
     * (false).
     */
    private boolean enabled;

    /**
     * Number of load-cells.
     */
    private int amount;

    private int loadCellSCK1;
    private int loadCellSCK2;
    private int loadCellSCK3;
    private int loadCellSCK4;

    private int loadCellDAT1;
    private int loadCellDAT2;
    private int loadCellDAT3;
    private int loadCellDAT4;

    private double loadCellMVV1;
    private double loadCellMVV2;
    private double loadCellMVV3;
    private double loadCellMVV4;

    private int loadCellMax1;
    private int loadCellMax2;
    private int loadCellMax3;
    private int loadCellMax4;

    //<editor-fold defaultstate="collapsed" desc="getterSetter">
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getLoadCellSCK1() {
        return loadCellSCK1;
    }

    public void setLoadCellSCK1(int loadCellSCK1) {
        this.loadCellSCK1 = loadCellSCK1;
    }

    public int getLoadCellSCK2() {
        return loadCellSCK2;
    }

    public void setLoadCellSCK2(int loadCellSCK2) {
        this.loadCellSCK2 = loadCellSCK2;
    }

    public int getLoadCellSCK3() {
        return loadCellSCK3;
    }

    public void setLoadCellSCK3(int loadCellSCK3) {
        this.loadCellSCK3 = loadCellSCK3;
    }

    public int getLoadCellSCK4() {
        return loadCellSCK4;
    }

    public void setLoadCellSCK4(int loadCellSCK4) {
        this.loadCellSCK4 = loadCellSCK4;
    }

    public int getLoadCellDAT1() {
        return loadCellDAT1;
    }

    public void setLoadCellDAT1(int loadCellDAT1) {
        this.loadCellDAT1 = loadCellDAT1;
    }

    public int getLoadCellDAT2() {
        return loadCellDAT2;
    }

    public void setLoadCellDAT2(int loadCellDAT2) {
        this.loadCellDAT2 = loadCellDAT2;
    }

    public int getLoadCellDAT3() {
        return loadCellDAT3;
    }

    public void setLoadCellDAT3(int loadCellDAT3) {
        this.loadCellDAT3 = loadCellDAT3;
    }

    public int getLoadCellDAT4() {
        return loadCellDAT4;
    }

    public void setLoadCellDAT4(int loadCellDAT4) {
        this.loadCellDAT4 = loadCellDAT4;
    }

    public double getLoadCellMVV1() {
        return loadCellMVV1;
    }

    public void setLoadCellMVV1(double loadCellMVV1) {
        this.loadCellMVV1 = loadCellMVV1;
    }

    public double getLoadCellMVV2() {
        return loadCellMVV2;
    }

    public void setLoadCellMVV2(double loadCellMVV2) {
        this.loadCellMVV2 = loadCellMVV2;
    }

    public double getLoadCellMVV3() {
        return loadCellMVV3;
    }

    public void setLoadCellMVV3(double loadCellMVV3) {
        this.loadCellMVV3 = loadCellMVV3;
    }

    public double getLoadCellMVV4() {
        return loadCellMVV4;
    }

    public void setLoadCellMVV4(double loadCellMVV4) {
        this.loadCellMVV4 = loadCellMVV4;
    }

    public int getLoadCellMax1() {
        return loadCellMax1;
    }

    public void setLoadCellMax1(int loadCellMax1) {
        this.loadCellMax1 = loadCellMax1;
    }

    public int getLoadCellMax2() {
        return loadCellMax2;
    }

    public void setLoadCellMax2(int loadCellMax2) {
        this.loadCellMax2 = loadCellMax2;
    }

    public int getLoadCellMax3() {
        return loadCellMax3;
    }

    public void setLoadCellMax3(int loadCellMax3) {
        this.loadCellMax3 = loadCellMax3;
    }

    public int getLoadCellMax4() {
        return loadCellMax4;
    }

    public void setLoadCellMax4(int loadCellMax4) {
        this.loadCellMax4 = loadCellMax4;
    }
//</editor-fold>

    @Override
    public String toString() {
        return "LoadCellSettings{" + "enabled=" + enabled + ", amount=" + amount 
                + ", loadCellSCK1=" + loadCellSCK1 + ", loadCellSCK2=" + loadCellSCK2 
                + ", loadCellSCK3=" + loadCellSCK3 + ", loadCellSCK4=" + loadCellSCK4 
                + ", loadCellDAT1=" + loadCellDAT1 + ", loadCellDAT2=" + loadCellDAT2 
                + ", loadCellDAT3=" + loadCellDAT3 + ", loadCellDAT4=" + loadCellDAT4 
                + ", loadCellMVV1=" + loadCellMVV1 + ", loadCellMVV2=" + loadCellMVV2 
                + ", loadCellMVV3=" + loadCellMVV3 + ", loadCellMVV4=" + loadCellMVV4 
                + ", loadCellMax1=" + loadCellMax1 + ", loadCellMax2=" + loadCellMax2 
                + ", loadCellMax3=" + loadCellMax3 + ", loadCellMax4=" + loadCellMax4 + '}';
    }

}
