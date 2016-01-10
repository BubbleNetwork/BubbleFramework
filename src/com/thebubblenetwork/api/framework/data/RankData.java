package com.thebubblenetwork.api.framework.data;

import com.thebubblenetwork.api.framework.data.api.DataObject;

import java.util.Map;

/**
 * Created by Jacob on 31/12/2015.
 */
public class RankData extends DataObject {
    public static final String PREFIX = "prefix", SUFFIX = "suffix", INHERITANCE = "inherit";
    private String name;

    public RankData(String name, Map data) {
        super(data);
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
