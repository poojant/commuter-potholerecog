package com.example.lenovo.commutersafety;

public class Zone {
    String zoneID , zoneTitle , zoneData , zoneSolution , zoneLat , zoneLong , zoneStatus , zoneImage;

    public Zone() {
    }

    public Zone(String zoneID, String zoneTitle, String zoneData, String zoneSolution, String zoneLat, String zoneLong, String zoneStatus, String zoneImage) {
        this.zoneID = zoneID;
        this.zoneTitle = zoneTitle;
        this.zoneData = zoneData;
        this.zoneSolution = zoneSolution;
        this.zoneLat = zoneLat;
        this.zoneLong = zoneLong;
        this.zoneStatus = zoneStatus;
        this.zoneImage = zoneImage;
    }

    public String getZoneID() {
        return zoneID;
    }

    public void setZoneID(String zoneID) {
        this.zoneID = zoneID;
    }

    public String getZoneTitle() {
        return zoneTitle;
    }

    public void setZoneTitle(String zoneTitle) {
        this.zoneTitle = zoneTitle;
    }

    public String getZoneData() {
        return zoneData;
    }

    public void setZoneData(String zoneData) {
        this.zoneData = zoneData;
    }

    public String getZoneSolution() {
        return zoneSolution;
    }

    public void setZoneSolution(String zoneSolution) {
        this.zoneSolution = zoneSolution;
    }

    public String getZoneLat() {
        return zoneLat;
    }

    public void setZoneLat(String zoneLat) {
        this.zoneLat = zoneLat;
    }

    public String getZoneLong() {
        return zoneLong;
    }

    public void setZoneLong(String zoneLong) {
        this.zoneLong = zoneLong;
    }

    public String getZoneStatus() {
        return zoneStatus;
    }

    public void setZoneStatus(String zoneStatus) {
        this.zoneStatus = zoneStatus;
    }

    public String getZoneImage() {
        return zoneImage;
    }

    public void setZoneImage(String zoneImage) {
        this.zoneImage = zoneImage;
    }
}
