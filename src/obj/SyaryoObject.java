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

/**
 * 車両オブジェクトの定義クラス
 * @author ZZ17390
 */
public class SyaryoObject implements Serializable {
    //車両オブジェクトファイルに保存される変数
    private static final long serialVersionUID = 1L;
    public String name; //車両名
    private byte[] mapData; //圧縮後のバイナリデータ

    //車両オブジェクトファイルから削除される変数
    private transient Map<String, Map<String, List<String>>> map; //圧縮前の車両データ
    private transient DecimalFormat dformat = new DecimalFormat("0000"); //キー重複番号の正規化
    private transient Boolean performanceAccess = false; //アクセス性能を管理するフラグ

    public SyaryoObject(String name) {
        this.name = name;
    }
    
    /**
     * キー重複時にIDの重複数を計算
     * @param data : 重複を調べたいMap
     * @param key : 重複を調べるキー
     * @return 重複回数
     */
    private String key_no(Map data, Object key) {

        String df = key.toString();

        if (data.get(key) == null) {
            return df;
        } else {
            int i = 1;
            while (data.get(df + "#" + dformat.format(i)) != null) {
                i++;
            }
            return df + "#" + dformat.format(i);
        }
    }

    /**
     * Add Data
     * オブジェクト生成時に利用
     * 文字型からオブジェクト型へ変換
     */
    public void add(Map template, int n) {
        decompress();
        for (Object field : template.keySet()) {
            String[] lines = template.get(field).toString().split("\n");
            
            Map temp = new HashMap();
            for (String line : lines) {
                List<String> s = new ArrayList<>();
                s.addAll(Arrays.asList(line.trim().split(",")));
                while (n > s.size()) {
                    s.add(" ");
                }
                temp.put(key_no(temp, field), s);
            }
            map.put(field.toString(), temp);
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
    public Map<String, List<String>> get(String key) {
        decompress();
        Map m = (Map) map.get(key);
        compress(true);
        return m;
    }

    //Get Map
    public Map<String, Map<String, List<String>>> getMap() {
        decompress();
        Map m = map;
        compress(true);
        return m;
    }

    /**
     * Update Data
     * データのリプレイスで利用
     * データ要素の更新は不可
     */
    public void put(String key, Map data) {
        decompress();
        map.put(key, data);
        compress(true);
    }

    /**
    * Insert Data
    * データの追加で利用
    * データ要素の追加は不可
    */
    public void putAll(Map data) {
        decompress();
        map.putAll(data);
        compress(true);
    }

    /*
    * アクセス性能を向上させる(1車両ごとにプログラムの最初で呼び出す)
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
     * データ削除
     * データ要素の削除は不可
     */
    public void remove(String key) {
        decompress();
        map.remove(key);
        compress(true);
    }

    /**
     * データ圧縮
     * @param flg : 繰り返し車両データを呼び出すときのフラグ 
     * true=データを残す false=データを破棄 
     */
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
    
    /**
     * データ解凍
     */
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
    
    public Map toMap(Boolean head){
        decompress();
        Map dump = new HashMap();
        if(head){
            for(Map v : map.values())
                dump.putAll(v);
        }else
            dump.putAll(map);
        compress(true);
        
        return dump;
    }
    
    //Dump
    public String dump() {
        decompress();
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(":");
        sb.append(map);
        
        compress(true);

        return sb.toString();
    }
}
