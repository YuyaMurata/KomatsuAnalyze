/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import file.CSVFileReadWrite;
import filter.DataFilter;
import filter.MaintenanceFilter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class ExportTestFilter {
    private static String KISY = "PC200";
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    
    public static void main(String[] args) {
        LOADER.setFile(KISY+"_km_form");
        Map<String, SyaryoObject> syaryoMap = LOADER.getSyaryoMap();
        
        //ライフサイクル評価
        SyaryoObject s1 = syaryoMap.get("PC200-8N1-354503");
        SyaryoObject s2 = syaryoMap.get("PC200-8N1-316482");
        SyaryoObject s3 = syaryoMap.get("PC200-8N1-313689");
        
        //s1ライフサイクル
        lifeout(s1);
        
    }
    
    private static void lifeout(SyaryoObject s){
        try(PrintWriter pw = CSVFileReadWrite.writer(s.name+"_life.csv")){
            pw.println("会社CD,作番,日付,メンテナンス累積,サービス累積,累積金額");
            Map<String, List> odr = s.get("受注");
            
            int idx = LOADER.index("受注", "SKKG");
            int idx_date = LOADER.index("受注", "ODDAY");
            int idx_comp = LOADER.index("受注", "会社CD");
            
            
            DataFilter df = new MaintenanceFilter();
            
            Integer mamc = 0;
            Integer samc = 0;
            Integer allamc = 0;
            for(String sbn : odr.keySet()){
                String date = odr.get(sbn).get(idx_date).toString();
                String comp = odr.get(sbn).get(idx_comp).toString();
                
                Integer price = Integer.valueOf(odr.get(sbn).get(idx).toString());
                List data = df.filter("受注", odr.get(sbn));
                
                if(data != null)
                    mamc += price;
                else
                    samc += price;
                
                allamc += price;
                pw.println(comp+","+sbn+","+date+","+mamc+","+samc+","+allamc);
            }
        }
    }
}
