package com.baba.homeaidadmin.Modals;

public class WorkerDetails {
    private String wName, wPhone, wType, wAddress, wId, isApproved;

    public WorkerDetails() {
    }

    public WorkerDetails(String wName, String wPhone, String wType, String wAdress, String wId, String isApproved) {
        this.wName = wName;
        this.wPhone = wPhone;
        this.wType = wType;
        this.wAddress = wAdress;
        this.wId = wId;
        this.isApproved = isApproved;
    }

    public String getwName() {
        return wName;
    }

    public void setwName(String wName) {
        this.wName = wName;
    }

    public String getwPhone() {
        return wPhone;
    }

    public void setwPhone(String wPhone) {
        this.wPhone = wPhone;
    }

    public String getwType() {
        return wType;
    }

    public void setwType(String wType) {
        this.wType = wType;
    }

    public String getwAddress() {
        return wAddress;
    }

    public void setwAddress(String wAddress) {
        this.wAddress = wAddress;
    }

    public String getwId() {
        return wId;
    }

    public void setwId(String wId) {
        this.wId = wId;
    }

    public String getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(String isApproved) {
        this.isApproved = isApproved;
    }
}
