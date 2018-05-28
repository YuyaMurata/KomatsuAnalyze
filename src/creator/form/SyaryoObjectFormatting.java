/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import creator.create.KomatsuDataParameter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.MapIndexToJSON;
import json.SyaryoToZip3;
import obj.SyaryoElements;
import obj.SyaryoObject4;

/**
 *
 * @author zz17390
 */
public class SyaryoObjectFormatting {
    private static String KISY = "PC138US";
    private static String OBJPATH = KomatsuDataParameter.OBJECT_PATH;
    private static String INDEX_PATH = KomatsuDataParameter.CUSTOMER_INDEX_PATH;
    
    public static void main(String[] args) {
        form(KISY);
    }
    
    private static void form(String kisy){
        SyaryoToZip3 zip3 = new SyaryoToZip3();
        String filename = OBJPATH+"syaryo_obj_"+kisy+".bz2";
        Map<String, SyaryoObject4> syaryoMap = zip3.read(filename);
        //本社コード
        Map<String, String> index = new MapIndexToJSON().reader(INDEX_PATH);
        
        int n = 0;
        for(String key : syaryoMap.keySet()){
            SyaryoObject4 syaryo = syaryoMap.get(key);
            syaryo.decompress();
            
            Map newMap = formQwner(syaryo.get("顧客"), index);
            syaryo.map.put("顧客", newMap);
            
            syaryo.compress(true);
            n++;
            
            if(n%1000==0){
                System.out.println(n+"台処理");
            }
        }
        
        String outfile = OBJPATH+"syaryo_obj_"+kisy+"_form.bz2";
        zip3.write(outfile, syaryoMap);
    }
    
    private static Map formQwner(Map<String, List> owner, Map<String, String> index){
        if(owner == null){
            System.out.println("Do no find owner!");
        }
        
        Integer ownerID = 2;
        Integer ownerName = 3;
        
        //ID重複排除 ##排除
        List owners = owner.values().stream()
                                .map(l -> index.get(l.get(ownerID))==null?l.get(ownerID):index.get(l.get(ownerID)))
                                .filter(id -> !id.toString().contains("##"))
                                .distinct()
                                .collect(Collectors.toList());
        
        Map<String, List> map = new TreeMap();
        int i = 0;
        for(String date : owner.keySet()){
            if(date.replace("/", "").length() >= 8){
                String id = owner.get(date).get(ownerID).toString();
                if(index.get(id) != null)
                    id = index.get(id);
                    
                if(id.equals(owners.get(i))){
                    map.put(date.replace("/", ""), owner.get(date));
                    i++;
                    if(owners.size() <= i)
                        break;
                }
            }
        }
        
        //名称重複排除
        owners = map.values().stream()
                        .map(l -> l.get(ownerName))
                        .distinct()
                        .collect(Collectors.toList());
        Map<String, List> map2 = new TreeMap();
        i = 0;
        for(String date : map.keySet()){
            String name = map.get(date).get(ownerName).toString();
            if(name.equals(owners.get(i))){
                map2.put(date, map.get(date));
                i++;
                if(owners.size() <= i)
                    break;
            }
        }
        
        return map2;
    }
}
