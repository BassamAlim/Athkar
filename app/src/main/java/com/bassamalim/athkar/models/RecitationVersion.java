package com.bassamalim.athkar.models;

import android.view.View;

import java.io.Serializable;

public class RecitationVersion implements Serializable {
    private final int index;
    private final String server;
    private final String rewaya;
    private final String count;
    private final String suras;
    private final View.OnClickListener listener;

    public RecitationVersion(int index, String server, String rewaya, String count,
                             String suras, View.OnClickListener listener) {
        this.index = index;
        this.server = server;
        this.rewaya = rewaya;
        this.count = count;
        this.suras = suras;
        this.listener = listener;
    }

    public int getIndex() {
        return index;
    }

    public String getServer() {
        return server;
    }

    public String getRewaya() {
        return rewaya;
    }

    public Integer getCount() {
        return Integer.valueOf(count);
    }

    public String getSuras() {
        return suras;
    }

    public View.OnClickListener getListener() {
        return listener;
    }
}
