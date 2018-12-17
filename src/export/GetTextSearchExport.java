/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import file.CSVFileReadWrite;
import index.SyaryoObjectElementsIndex;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import file.SyaryoToCompress;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class GetTextSearchExport {

    //レイアウト
    private static Map<String, List> dataIndex = SyaryoObjectElementsIndex.getInstance().getIndex();
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static String KISY = "PC200";
    private static Map<String, SyaryoObject4> map;

    private static String syaryofilename = PATH + "syaryo_obj_" + KISY + "_sv_form.bz2";

    public static void main(String[] args) {
        map = new SyaryoToCompress().read(syaryofilename);
        
        List header = dataIndex.get("部品");
        List<Integer> indexs = Arrays.asList(new Integer[]{header.indexOf("KSYCD"), header.indexOf("HNBN")});
        textSearch("部品", "エンジンオイル", header.indexOf("BHN_NM"), indexs);
    }

    public static void textSearch(String data, String text, int search, List<Integer> export) {
        try (PrintWriter pw = CSVFileReadWrite.writer(KISY + "_" + data + "_" + text + ".csv")) {
            for (SyaryoObject4 syaryo : map.values()) {
                
                if (syaryo.get(data) == null) {
                    continue;
                }

                System.out.println(syaryo.name);
                
                syaryo.startHighPerformaceAccess();
                
                for (List l : syaryo.get(data).values()) {
                    if (!l.get(search).toString().contains(text)) {
                        continue;
                    }
                    
                    
                    List d = new ArrayList();
                    d.add(syaryo.name);

                    for (int idx : export) {
                        d.add(l.get(idx));
                    }
                    
                    pw.println(String.join(",", d));
                }
                
                syaryo.stopHighPerformaceAccess();
            }
        }
    }
}
