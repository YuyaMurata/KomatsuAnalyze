/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import index.SyaryoObjectElementsIndex;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import file.SyaryoToCompress;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class ExportStaffDataCheck {
    //レイアウト
    private static Map<String, List> dataIndex = SyaryoObjectElementsIndex.getInstance().getIndex();
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static String KISY = "PC200";
    private static Map<String, SyaryoObject4> map;
    
    public static void main(String[] args) {
        //車両の読み込み
        map = new SyaryoToCompress().read(PATH + "syaryo_obj_" + KISY + "_form.bz2");
        
        try(PrintWriter pw = CSVFileReadWrite.writer(KISY+"_pointcode_sgcd_check.csv")){
            for(SyaryoObject4 syaryo : map.values()){
                try(SyaryoAnalizer analize = new SyaryoAnalizer(syaryo)){
                    export(pw, analize);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    private static void export(PrintWriter pw, SyaryoAnalizer syaryo){
        int sgcd = dataIndex.get("作業").indexOf("SGYOCD");
        
        int point = dataIndex.get("受注").indexOf("ODR_PNTCD");
        int tanto = dataIndex.get("受注").indexOf("ODR_TNTCD");
        int sgktcd = dataIndex.get("受注").indexOf("SGYO_KTICD");
        
        List mainte = KomatsuDataParameter.PERIOD_MAINTE.get("受注.SGYO_KTICD");
        List mainte2 = KomatsuDataParameter.PERIOD_MAINTE.get("作業.SGYOCD");
        
        for(String sbn : syaryo.get().get("作業").keySet()){
            //定期メンテ排除
            if(mainte.contains(syaryo.get().get("受注").get(sbn.split("#")[0]).get(sgktcd)))
                continue;
            
            String cd = syaryo.get().get("作業").get(sbn).get(sgcd).toString();
            if(mainte2.contains(cd))
                continue;
            
            String d = syaryo.getSBNDate(sbn, Boolean.TRUE);
            String pt = syaryo.get().get("受注").get(sbn.split("#")[0]).get(point).toString();
            String tn = syaryo.get().get("受注").get(sbn.split("#")[0]).get(tanto).toString();
            
            //out
            pw.println(syaryo.get().name+","+d+","+pt+","+tn+","+cd);
        }
    }
}
