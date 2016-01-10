package com.thebubblenetwork.api.framework.data.api;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jacob on 31/12/2015.
 */
public class DataObject {

    private Map data;


    public DataObject(Map data) {
        this.data = data;
    }

    protected static Map loadData(ResultSet set) throws SQLException {
        Map datamap = new HashMap();
        while (set.next()) {
            datamap.put(set.getObject("key"), set.getObject("value"));
        }
        set.close();
        return datamap;
    }

    public Map getRaw() {
        return data;
    }

    public Boolean getBoolean(String indentifier) throws InvalidBaseException {
        String s = getString(indentifier);
        return Boolean.parseBoolean(s);
    }

    public Number getNumber(String indentifier) throws InvalidBaseException {
        String s = getString(indentifier);
        try {
            return NumberFormat.getInstance().parse(s);
        } catch (NumberFormatException ex) {
            throw new InvalidBaseException(ex);
        } catch (ParseException ex) {
            throw new InvalidBaseException(ex);
        }
    }

    public String getString(String indentifier) throws InvalidBaseException {
        check(indentifier, String.class);
        return (String) getRaw().get(indentifier);
    }


    protected void check(String indentifier, Class cast) throws InvalidBaseException {
        if (!getRaw().containsKey(indentifier))
            throw new InvalidBaseException("Could not find raw data: " + indentifier);
        if (!cast.isInstance(getRaw().get(indentifier)))
            throw new InvalidBaseException("Data could not be cast to: " + cast.getName());
    }
}
