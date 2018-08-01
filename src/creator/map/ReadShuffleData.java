/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.map;

import creator.index.CustomerIndex;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import json.MapIndexToJSON;
import json.SyaryoToZip3;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class ReadShuffleData {
    private Map<String, List<String>> custMap;
    private static SyaryoToZip3 zip = new SyaryoToZip3();
    private Map<String, Map<String, SyaryoObject4>> readFile;
    private Map<String, List<List>> map;
    private Boolean fileCheck = true;
    private String path;
    private String kisy;
    private String dataKey;
    
    
    public ReadShuffleData(String path, String kisy) {
        this.path = path;
        this.kisy = kisy;
        custMap =setCustomer(KomatsuDataParameter.CUSTOMER_INDEX_PATH);
    }

    private static Map<String, List<String>> setCustomer(String filename) {
        int size = ((List)CustomerIndex.layoutIndex().get("customer")).size()-1;
        Map<String, String> map = new MapIndexToJSON().reader(filename);
        System.out.println("CSUTSIZE="+size);
        Map<String, List<String>> formCustMap = new HashMap<>();
        for(String cid : map.keySet()){
            List<String> list = Arrays.asList(map.get(cid).split(","));
            list = list.stream().map(s -> s.equals("")?"None":s).collect(Collectors.toList());
            while(list.size() < size)
                list.add("None");
            
            formCustMap.put(cid, list);
        }
        return formCustMap;
    }

    public void setDataList(String key, List<String> getDataFormat) {
        this.dataKey = key;
        
        //抽出に必要なテーブルリストを取得
        List<String> readList = getDataFormat.stream()
            .filter(s -> s.contains("."))
            .filter(s -> !s.contains("customer"))
            .map(s -> s.split("\\.")[0])
            .distinct()
            .collect(Collectors.toList());
        
        System.out.println(key+":"+getDataFormat);

        //上記からファイルを読み込む
        readFile = new HashMap();
        for (String f : readList) {
            if (!(new File(path + "syaryo_mid_" + kisy + "_" + f + ".bz2")).exists()) {
                System.out.println("Not Found : " + path + "syaryo_mid_" + kisy + "_" + f + ".bz2");
                continue;
            }
            readFile.put(f, zip.read(path + "syaryo_mid_" + kisy + "_" + f + ".bz2"));
        }

        //例外処理 必要ファイルを読み込めなかったら処理を実行しない
        if (readFile.size() != readList.size()) {
            fileCheck = false;
        }else
            fileCheck=true;
    }
    
    String[] cid = new String[2];
    public String getData(String table, int row, String elm, int col){
        String data = getCustomerData(table, row, elm, col);
        if(data == null)
            data = map.get(table).get(row).get(col).toString();
        return data;
    }
    
    public List<String> getRow(String table, int row){
        return map.get(table).get(row);
    }
    
    private String getCustomerData(String table, int row, String elm, int col){
        if(dataKey.equals("顧客")){
            if(elm.equals("KSYCD")) cid[0] = map.get(table).get(row).get(col).toString();
            else if(elm.contains("KKYKCD") || elm.contains("NNSCD")) cid[1] = map.get(table).get(row).get(col).toString();
        }
        
        if(!table.equals("customer"))
            return null;
        
        String id = cid[0]+"_"+cid[1];
        if(custMap.get(id) == null)
            return "None";
        
        String data = custMap.get(id).get(col);
        if(data.equals(""))
            return "None";
        
        return data;
    }
    
    public String getSubKey(String table, int row, int col){
        return getData(table, row, "", col);
    }

    public Boolean setData(String id) {
        if(idCheck(id))
            return true;
        
        //ファイルから特定IDのデータ郡を抽出
        map = idExtractList(id, readFile);
        System.out.println(map);
        return false;
    }

    //テンプレートファイルからターゲット車両のリストデータを抽出する
    private static Map<String, List<List>> idExtractList(String target, Map<String, Map<String, SyaryoObject4>> readFile) {
        Map extract = new HashMap();
        for (String table : readFile.keySet()) {
            List<List<String>> data = new ArrayList<>();

            SyaryoObject4 targetSyaryo = readFile.get(table).get(target);

            for (Object list : targetSyaryo.getMap().values()) {
                data.add((List<String>) list);
            }

            extract.put(table, data);
        }

        return extract;
    }

    public Boolean check() {
        return !fileCheck;
    }

    private Boolean idCheck(String id) {
        //読み込むファイルにIDが存在しなければ飛ばす
        Boolean flg = false;
        String table = "";
        for (String f : readFile.keySet()) {
            if (readFile.get(f).get(id) == null) {
                table += f;
                flg = true;
            } else {
                table += ",exist " + f + ",";
            }
        }
        
        return flg;
    }
    
    public Integer size(){
        return map.get(map.keySet().stream().findFirst().get()).size();
    }
}
