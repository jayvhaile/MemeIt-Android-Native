package com.innov8.memeit.CustomClasses;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.pchmn.materialchips.model.ChipInterface;

public class Chip implements ChipInterface {
    String label;
    Object id;
    @Override
    public Object getId() {
        return id;
    }

    @Override
    public Uri getAvatarUri() {
        return null;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return null;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getInfo() {
        return null;
    }

    public Chip setLabel(String label) {
        this.label = label;
        return this;
    }
    public Chip setId(String id) {
        this.id = label;
        return this;
    }
}
