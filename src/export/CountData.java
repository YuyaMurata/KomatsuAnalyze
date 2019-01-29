/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import index.SyaryoObjectElementsIndex;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import file.SyaryoToCompress;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author kaeru_yuya
 */
public class CountData {
    private static String KISY = "PC200";
    private static String OBJPATH = KomatsuDataParameter.OBJECT_PATH;
    private static Map<String, SyaryoObject> syaryoMap;
    private static Map<String, List> dataIndex = SyaryoObjectElementsIndex.getInstance().getIndex();
    
    public static void main(String[] args) {
        SyaryoToCompress zip3 = new SyaryoToCompress();
        String filename = OBJPATH + "syaryo_obj_" + KISY + "_sv_form_old.bz2";
        syaryoMap = zip3.read(filename);
        
        Map<Integer, List> filter = new HashMap<>();
        filter.put(dataIndex.get("受注").indexOf("SGYO_KTICD"), KomatsuDataParameter.MAINTE_DEFINE.get("受注.SGYO_KTICD"));
        
        //PC200
        System.out.println(filter);
        System.out.println(count("受注", filter));
        System.out.println(syaryoMap.size());
    }
    
    private static Integer count(String key, Map<Integer, List> filter){
        int cnt = 0;
        int emp = 0;
        
        for(SyaryoObject s : syaryoMap.values()){
            s.startHighPerformaceAccess();
            
            if(s.get(key) != null){
                if(filter == null)
                    cnt += s.get(key).size();
                else{
                    for(List l : s.get(key).values()){
                        Boolean f = true;
                        for(Integer idx : filter.keySet()){
                            f = f && filter.get(idx).contains(l.get(idx).toString());
                        }
                        if(f)
                            cnt++;
                    }
                }
            }else
                emp++;
            
            s.stopHighPerformaceAccess();
        }
        
        System.out.println("サービスなし台数:"+emp);
        
        return cnt;
    }
}
