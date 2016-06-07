package com.snap.thirdear.db;

/**
 * Created by hrajal on 5/24/2016.
 */
public class Trigger {
    private long _id;
    private long groupsId;
    private String type;
    private String triggerText;
// not for DB column
    private String matchingWord;

    public static enum TYPE {WORDS,SOUND, NOISE, SENSOR}

    public Trigger() {

    }

    public Trigger(long groupsId, TYPE type, String trigger_text) {
        this.groupsId = groupsId;
        this.type = type.name();
        this.triggerText = trigger_text;
    }

    public Trigger(TYPE type, String trigger_text) {
        this.type = type.name();
        this.triggerText = trigger_text;
    }


    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long getGroupsId() {
        return groupsId;
    }

    public void setGroupsId(long groupsId) {
        this.groupsId = groupsId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTriggerText() {
        return triggerText;
    }

    public void setTriggerText(String triggerText) {
        this.triggerText = triggerText;
    }


    public String getMatchingWord() {
        return matchingWord;
    }

    public void setMatchingWord(String matchingWord) {
        this.matchingWord = matchingWord;
    }

    @Override
    public String toString() {
        return "Trigger{" +
                "_id=" + _id +
                ", groupsId=" + groupsId +
                ", type='" + type + '\'' +
                ", triggerText='" + triggerText + '\'' +
                ", matchingWord='" + matchingWord + '\'' +
                '}';
    }
}
