/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import json.SnappyMap;

/**
 *
 * @author ZZ17390
 */
public class SyaryoObject4 implements Serializable {

    private static final long serialVersionUID = 1L;
    public String name;
    private byte[] mapData;

    private transient Map map;
    private transient DecimalFormat dformat = new DecimalFormat("0000");
    private transient Boolean performanceAccess = false;

    public SyaryoObject4(String name) {
        this.name = name;
    }

    public String key_no(Object key) {

        String df = key.toString();

        if (map.get(key) == null) {
            return df;
        } else {
            int i = 1;
            while (map.get(df + "#" + dformat.format(i)) != null) {
                i++;
            }
            return df + "#" + dformat.format(i);
        }
    }

    /**
     * Add Data
     */
    public void add(Map template, int n) {
        decompress();
        for (Object field : template.keySet()) {
            String[] lines = template.get(field).toString().split("\n");

            for (String line : lines) {
                List<String> s = new ArrayList<>();
                s.addAll(Arrays.asList(line.trim().split(",")));
                while (n > s.size()) {
                    s.add(" ");
                }
                map.put(key_no(field), s);
            }
        }
        compress(true);
    }

    /**
     * Read Data
     */
    public String getName() {
        return name;
    }

    //Get Data
    public Map<String, List> get(String key) {
        decompress();
        Map m = (Map) map.get(key);
        compress(true);
        return m;
    }

    //Get Map
    public Map<String, List> getMap() {
        decompress();
        Map m = map;
        compress(true);
        return m;
    }

    /**
     * Update Data
     */
    public void put(Object key, Object obj) {
        decompress();
        map.put(key, obj);
        compress(true);
    }

    public void putAll(Map add) {
        decompress();
        map.putAll(add);
        compress(true);
    }

    /*
    * アクセス性能を向上させる(1車両ごとに呼び出す)
     */
    public void startHighPerformaceAccess() {
        decompress();
        performanceAccess = true;
    }

    public void stopHighPerformaceAccess() {
        performanceAccess = false;
        compress(true);
    }

    /**
     * Delete
     */
    public void remove(Object key) {
        decompress();
        map.remove(key);
        compress(true);
    }

    private void compress(Boolean flg) {
        if (performanceAccess != null) {
            if (performanceAccess) {
                return;
            }
        }
        if (flg) {
            mapData = SnappyMap.toSnappy(map);
        }
        if (map != null) {
            map = null;
        }
    }

    private void decompress() {
        if (performanceAccess != null) {
            if (performanceAccess) {
                return;
            }
        }
        if (mapData != null) {
            map = SnappyMap.toMap(mapData);
            mapData = null;
        }
        if (map == null) {
            map = new LinkedHashMap();
        }
    }

    //Dump
    public String dump() {
        decompress();
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(":");
        sb.append(map);
        /*for (Object key : map.keySet()) {
            sb.append("\n    ");
            sb.append(key);
            sb.append(":");
            Map<String, List> m = (Map<String, List>) map.get(key);
            for(String d : m.keySet()){
                sb.append("\n\t");
                sb.append(d);
                sb.append(m.get(d));
            }
        }*/
        compress(true);

        return sb.toString();
    }
}
