/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import file.CSVFileReadWrite;
import index.SyaryoObjectElementsIndex;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import file.SyaryoToCompress;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 * 新車年リストをcsv出力
 * @author ZZ17390
 */
public class YearSyaryoList {
    //レイアウト
    private static Map<String, List> dataIndex = SyaryoObjectElementsIndex.getInstance().getIndex();
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static String KISY = "PC200";
    private static Map<String, SyaryoObject4> map;
    
    public static void main(String[] args) {
        //車両の読み込み
        map = new SyaryoToCompress().read(PATH + "syaryo_obj_" + KISY + "_sv_form.bz2");
        
        try(PrintWriter pw = CSVFileReadWrite.writer(KISY+"_sellyearslist.csv")){
            pw.println("SID,日付,データ数");
            
            map.values().stream()
                    .forEach(s -> {
                        s.startHighPerformaceAccess();
                        
                        String str =  s.getName()+","+s.get("新車").keySet().stream().findFirst().get();
                        
                        //データ総量のカウント
                        int total = s.getMap().values().stream().mapToInt(d -> d.size()).sum();
                        
                        str += ","+total;
                        
                        s.startHighPerformaceAccess();
                        pw.println(str);
                    });
        }
    }
}
