/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filter;

import java.util.List;
import java.util.Map;
import obj.SyaryoLoader;
import param.KomatsuDataParameter;

/**
 * メンテナンスサービスのフィルタリング
 * @author ZZ17390
 */
public class MaintenanceFilter extends DataFilter{
    private Map<String, List> filterMap = KomatsuDataParameter.MAINTE_DEFINE;
    private SyaryoLoader LOADER = SyaryoLoader.getInstance();
    
    @Override
    public Boolean flogic(String key, List<String> data) {
        Boolean flg = false;
        
        for(String f : filterMap.keySet()){
            if(!f.contains(key))
                continue;
            
            int idx = LOADER.index(key, f.split("\\.")[1]);
            List<String> flist = filterMap.get(f);
            
            for(String df : flist){
                if(flg)
                    break;
                if(df.contains("*")){
                    flg = data.get(idx).contains(df.replace("*", ""));
                }else{
                    flg = data.get(idx).equals(df);
                }
            }
        }
        
        return flg;
    }
}
