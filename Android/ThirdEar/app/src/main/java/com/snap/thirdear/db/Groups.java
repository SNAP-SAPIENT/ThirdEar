package com.snap.thirdear.db;

/**
 * Created by hrajal on 5/24/2016.
 */
public class Groups {

    private long _id;
    private String iconUrl;
    private int enabled;
    private int phoneVibrate;
    private int phoneLight;
    private int phoneAudio;
    private int light;
    private int btReceiver;
    private int wearableDevice;
    private String alertText;
    private String name;

    public Groups(){

    }

    public Groups(String icon_url, int enabled, int phoneVibrate, int phoneLight, int phoneAudio, int light, int btReceiver, int wearableDevice, String alertText, String name) {
        this.iconUrl = icon_url;
        this.enabled = enabled;
        this.phoneVibrate = phoneVibrate;
        this.phoneLight = phoneLight;
        this.phoneAudio = phoneAudio;
        this.light = light;
        this.btReceiver = btReceiver;
        this.wearableDevice = wearableDevice;
        this.alertText = alertText;
        this.name = name;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public int getPhoneVibrate() {
        return phoneVibrate;
    }

    public void setPhoneVibrate(int phoneVibrate) {
        this.phoneVibrate = phoneVibrate;
    }

    public int getPhoneLight() {
        return phoneLight;
    }

    public void setPhoneLight(int phoneLight) {
        this.phoneLight = phoneLight;
    }

    public int getPhoneAudio() {
        return phoneAudio;
    }

    public void setPhoneAudio(int phoneAudio) {
        this.phoneAudio = phoneAudio;
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public int getBtReceiver() {
        return btReceiver;
    }

    public void setBtReceiver(int btReceiver) {
        this.btReceiver = btReceiver;
    }

    public int getWearableDevice() {
        return wearableDevice;
    }

    public void setWearableDevice(int wearableDevice) {
        this.wearableDevice = wearableDevice;
    }

    public String getAlertText() {
        return alertText;
    }

    public void setAlertText(String alertText) {
        this.alertText = alertText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Groups{" +
                "_id=" + _id +
                ", iconUrl='" + iconUrl + '\'' +
                ", enabled=" + enabled +
                ", phoneVibrate=" + phoneVibrate +
                ", phoneLight=" + phoneLight +
                ", phoneAudio=" + phoneAudio +
                ", light=" + light +
                ", btReceiver=" + btReceiver +
                ", wearableDevice=" + wearableDevice +
                ", alertText='" + alertText + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
