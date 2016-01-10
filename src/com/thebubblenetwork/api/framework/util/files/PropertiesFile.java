package com.thebubblenetwork.api.framework.util.files;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright Statement
 * ----------------------
 * Copyright (C) The Bubble Network, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Wrote by Jacob Evans <jacobevansminor@gmail.com>, 01 2016
 *
 *
 * Class information
 * ---------------------
 * Package: com.thebubblenetwork.api.framework.util.files
 * Date-created: 10/01/2016 20:08
 * Project: BubbleFramework
 */
public class PropertiesFile {
    private Map<String,String> data = new HashMap<>();
    public PropertiesFile(File config) throws Exception{
        if(!config.exists()){
            throw new IOException("File does not exist");
        }
        FileReader reader;
        try {
            reader = new FileReader(config);
        } catch (FileNotFoundException e) {
            //Automatic Catch Statement
            throw new IOException("File does not exist");
        }
        BufferedReader bufferedReader = new BufferedReader(reader);
        String nextline;
        try {
            while ((nextline = bufferedReader.readLine()) != null){
                if(!nextline.startsWith("#")){
                    String[] properties = nextline.split(":");
                    if(properties.length != 2)throw new Exception("Invalid configuration \'" + nextline + "\'");
                    data.put(properties[0],properties[1]);
                }
            }
        }
        catch (IOException ex){
            throw new IOException("An internal error has occurred",ex);
        }
    }

    public String getString(String s){
        return data.get(s);
    }

    public Number getNumber(String s)throws ParseException{
        return NumberFormat.getInstance().parse(s);
    }
}
