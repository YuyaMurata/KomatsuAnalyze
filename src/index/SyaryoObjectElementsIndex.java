/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import json.MapIndexToJSON;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class SyaryoObjectElementsIndex {
    private static String INDEXPATH = KomatsuDataParameter.SHUFFLE_FORMAT_PATH;
    
    private static SyaryoObjectElementsIndex instance = new SyaryoObjectElementsIndex();
    private Map<String, List> indexMap;
    private SyaryoObjectElementsIndex(){
        initialize();
    }
    
    public static SyaryoObjectElementsIndex getInstance(){
        return instance;
    }
    
    private void initialize(){
        Map<String, Map<String, List<String>>> index = new MapIndexToJSON().reader(INDEXPATH);
        Map<String, List> formIndex = new LinkedHashMap();

        for (String key : index.keySet()) {
            List<String> list = new ArrayList();
            List<String> list2 = new ArrayList();
            int n = 0;
            for (Object id : index.get(key).keySet()) {
                int s = index.get(key).get(id).stream().mapToInt(le -> le.contains("#") ? 1 : 0).sum();
                if (s == index.get(key).get(id).size()) {
                    list = index.get(key).get(id);
                }
                if (n < s) {
                    list2 = index.get(key).get(id);
                    n = s;
                }
            }

            if (list.isEmpty()) {
                list = list2;
            }

            //List内の整形
            list = list.stream().filter(le -> !(le.contains("=") || le.contains("<") || le.contains(">")) || le.contains("?")).collect(Collectors.toList());
            for (int i = 0; i < list.size(); i++) {
                String str = list.get(i);
                if (str.contains("#")) {
                    str = str.split("#")[0].split("\\.")[1];
                }
                list.set(i, str);
            }

            formIndex.put(key, list);
        }
        
        indexMap = formIndex;
        
        show();
    }
    
    public void show(){
        indexMap.entrySet().stream().map(e -> e.getKey() + ":" + e.getValue()).forEach(System.out::println);
    }
    
    public Map<String, List> getIndex(){
        return indexMap;
    }
    
    public List getIndex(String key){
        return indexMap.get(key);
    }
}
