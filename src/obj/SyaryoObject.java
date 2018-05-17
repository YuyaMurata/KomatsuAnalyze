/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import json.SnappyMap;

/**
 *
 * @author ZZ17390
 */
public class SyaryoObject implements Serializable{
    private static final long serialVersionUID = 1L;
    public String name;
    public byte[] mapData;
    
    
    public transient Map map = new LinkedHashMap();
    private transient DecimalFormat dformat = new DecimalFormat("0000");

    public SyaryoObject(String name) {
        this.name = name;
    }

    public String key_no(Object key) {
        Map oldMap = (Map) map.get(key);

        String df = key.toString();

        if (oldMap == null) {
            return df;
        } else {
            if (oldMap.get(df) == null) {
                return df;
            } else {
                int i = 1;
                while (oldMap.get(df + "#" + dformat.format(i)) != null) {
                    i++;
                }
                return df + "#" + dformat.format(i);
            }
        }
    }

    /**
     * Add Data
     */
    public void add(Map template, int n) {
        for (Object field : template.keySet()) {
            String[] lines = template.get(field).toString().split("\n");

            for (String line : lines) {
                List<String> s = Arrays.asList(line.trim().split(","));
                if(n > s.size())
                    s.add(" ");
                map.put(key_no(field), s);
            }
        }
    }

    /**
     * Get Data
     */
    public String getName() {
        return name;
    }

    //Get
    public Map<String, List> get(String key) {
        return (Map) map.get(key);
    }

    public void compress(Boolean flg) {
        if (flg) {
            mapData = SnappyMap.toSnappy(map);
        }
        if (map != null) {
            map = null;
        }
    }

    public void decompress() {
        map = SnappyMap.toMap(mapData);
        mapData = null;
        if(map == null)
            map = new LinkedHashMap();
    }

    //Dump
    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(":");
        for (Object key : map.keySet()) {
            sb.append("\n    ");
            sb.append(key);
            sb.append(":");
            Map<String, List> m = (Map<String, List>) map.get(key);
            for(String d : m.keySet()){
                sb.append("\n\t");
                sb.append(d);
                sb.append(m.get(d));
            }
        }

        return sb.toString();
    }
}
