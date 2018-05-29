/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import creator.create.KomatsuDataParameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.MapIndexToJSON;
import json.SyaryoToZip3;
import obj.SyaryoObject4;

/**
 *
 * @author zz17390
 */
public class SyaryoObjectFormatting {
    private static String KISY = "PC138US";
    private static String INDEXPATH = KomatsuDataParameter.SHUFFLE_FORMAT_PATH;
    private static String OBJPATH = KomatsuDataParameter.OBJECT_PATH;
    private static String INDEX_PATH = KomatsuDataParameter.CUSTOMER_INDEX_PATH;
    private static Map<String, List> dataIndex;
    
    public static void main(String[] args) {
        //Data Index
        dataIndex = index();
        
        form(KISY);
    }
    
    private static void form(String kisy){
        SyaryoToZip3 zip3 = new SyaryoToZip3();
        String filename = OBJPATH+"syaryo_obj_"+kisy+".bz2";
        Map<String, SyaryoObject4> syaryoMap = zip3.read(filename);
        
        //本社コード
        Map<String, String> honsyIndex = new MapIndexToJSON().reader(INDEX_PATH);
        
        int n = 0;
        for(String key : syaryoMap.keySet()){
            SyaryoObject4 syaryo = syaryoMap.get(key);
            syaryo.decompress();
            
            Map newMap = formQwner(syaryo.get("顧客"), dataIndex.get("顧客"), honsyIndex);
            syaryo.map.put("顧客", newMap);
            
            newMap = formNew(syaryo.get("新車"), syaryo.get("出荷"), dataIndex.get("新車"));
            syaryo.map.put("新車", newMap);
            
            syaryo.compress(true);
            n++;
            
            if(n%1000==0){
                System.out.println(n+"台処理");
            }
        }
        
        String outfile = OBJPATH+"syaryo_obj_"+kisy+"_form.bz2";
        zip3.write(outfile, syaryoMap);
    }
    
    private static Map formQwner(Map<String, List> owner, List indexList, Map<String, String> honsyIndex){
        if(owner == null){
            System.out.println("Do no find owner!");
        }
        
        Integer ownerID = indexList.indexOf("KKYKCD");
        Integer ownerName = indexList.indexOf("KKYK_KANA");
        
        //ID重複排除 ##排除
        List owners = owner.values().stream()
                                .map(l -> honsyIndex.get(l.get(ownerID))==null?l.get(ownerID):honsyIndex.get(l.get(ownerID)))
                                .filter(id -> !id.toString().contains("##"))
                                .distinct()
                                .collect(Collectors.toList());
        
        Map<String, List> map = new TreeMap();
        int i = 0;
        for(String date : owner.keySet()){
            if(date.replace("/", "").length() >= 8){
                String id = owner.get(date).get(ownerID).toString();
                if(honsyIndex.get(id) != null)
                    id = honsyIndex.get(id);
                    
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
    
    private static Map formNew(Map<String, List> news, Map<String, List> deploy, List indexList){
        Map<String, List> map = new TreeMap();
        if(news == null){
            //出荷情報を取得する処理
            String date = deploy.keySet().stream().findFirst().get();
            List list = new ArrayList();
            for(Object s : indexList)
                list.add("None");
            map.put(date, list);
            return map;
        }
        
        //List price index
        int hyomen = indexList.indexOf("HM_URI_KN");
        int jitsu = indexList.indexOf("RL_URI_KN");
        int hyojun = indexList.indexOf("STD_SY_KKU");
        
        //修正しない
        if(news.size() == 1){
            List<String> list = news.values().stream().findFirst().get();
            if(list.get(hyomen).contains("+"))
                for(int i = hyomen; i < list.size(); i++)
                    list.set(i, String.valueOf(Double.valueOf(list.get(i)).intValue()));
            return news;
        }
        
        //複数存在するときの処理
        List list = new ArrayList();
        String key = "";
        String[] price = new String[3];
        Boolean flg = true;
        for(String date : news.keySet()){
            list = news.get(date);
            if(flg){
                key = date.split("#")[0];
                price[0] = list.get(hyomen).toString();
                price[1] = list.get(jitsu).toString();
                price[2] = list.get(hyojun).toString();
                flg = false;
            }else{
                if(list.get(hyomen) != "None")
                    price[0] = list.get(hyomen).toString();
                if(list.get(jitsu) != "None")
                    price[1] = list.get(jitsu).toString();
                if(list.get(hyojun) != "None")
                    price[2] = list.get(hyojun).toString();
            }
        }
        
        //List整形
        int i = 0;
        for(String s : price){
            if(!s.equals("None"))
                list.set(hyomen+i, String.valueOf(Double.valueOf(s).intValue()));
        }
        
        map.put(key, list);
        
        return map;
    }
    
    private static Map<String, List> index() {
        Map<String, Map<String, List<String>>> index = new MapIndexToJSON().reader(INDEXPATH);
        Map<String, List> formIndex = new HashMap();
        
        for(String key : index.keySet()){
            List<String> list = new ArrayList();
            List<String> list2 = new ArrayList();
            int n = 0;
            for(Object id : index.get(key).keySet()){
                int s = index.get(key).get(id).stream().mapToInt(le -> le.contains("#")?1:0).sum();
                if(s == index.get(key).get(id).size()){
                    list = index.get(key).get(id);
                }
                if(n < s){
                    list2 = index.get(key).get(id);
                    n = s;
                }
            }
            
            if(list.isEmpty())
                list = list2;
            
            //List内の整形
            list = list.stream().filter(le -> !(le.contains("=")||le.contains("<")||le.contains(">"))).collect(Collectors.toList());
            for(int i=0; i < list.size(); i++){
                String str = list.get(i);
                if(str.contains("#"))
                    str = str.split("#")[0].split("\\.")[1];
                list.set(i, str);
            }
            
            formIndex.put(key, list);
        }
        formIndex.entrySet().stream().map(e -> e.getKey()+":"+e.getValue()).forEach(System.out::println);
        return formIndex;
    }
}
