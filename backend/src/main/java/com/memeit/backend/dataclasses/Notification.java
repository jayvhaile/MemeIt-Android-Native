package com.memeit.backend.dataclasses;

import com.google.gson.annotations.SerializedName;

public class Notification {
    public enum NotificationType{
        FOLLOWED,
        REACTED,
        COMMENTED,
        AWARDED,
        OTHER;
    }
    @SerializedName("mesg")
    private String message;
    @SerializedName("seen")
    private boolean isSeen;
    @SerializedName("type")
    private int type;

    public Notification(String message, boolean isSeen, NotificationType type) {
        this.message = message;
        this.isSeen = isSeen;
        this.type = type.ordinal();
    }

    public String getMessage() {
        return message;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public NotificationType getType() {
        return NotificationType.values()[type];
    }
}
