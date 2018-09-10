/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import index.SyaryoObjectElementsIndex;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import json.SyaryoToZip3;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class CreateAssociationMatrix {
    //レイアウト
    private static Map<String, List> dataIndex = SyaryoObjectElementsIndex.getInstance().getIndex();
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static String KISY = "PC200";
    private static Map<String, SyaryoObject4> map;

    public static void main(String[] args) {
        //車両の読み込み
        map = new SyaryoToZip3().read(PATH + "syaryo_obj_" + KISY + "_sv_form.bz2");
        int sgcdIdx = dataIndex.get("作業").indexOf("SGYOCD");
        int mainflgIdx = dataIndex.get("作業").indexOf("0");
        int hnbnIdx = dataIndex.get("部品").indexOf("HNBN");
        
        try(PrintWriter pw = CSVFileReadWrite.writer(KISY+"_workingparts.csv")){
            //Header
            pw.println("SID,作業コード,作番,代表フラグ,品番_装置");
            map.values().stream()
                        .filter(s -> s.get("作業") != null)
                        .filter(s -> s.get("部品") != null)
                        .forEach(s ->{
                            SyaryoAnalizer analize = new SyaryoAnalizer(s);
                            List<String> worksbn = analize.getValue("作業", new Integer[]{0}).keySet().stream()
                                                                .map(w -> w.split("#")[0])
                                                                .distinct()
                                                                .collect(Collectors.toList());
                            
                            worksbn.stream().map(w -> analize.getSBNParts(w))
                                            .filter(p -> p != null)
                                            .forEach(p -> {
                                                p.entrySet().stream()
                                                    .map(pd -> s.getName()+","+pd.getValue().get(ksycdIdx)+","+pd.getKey()+","+pd.getValue().get(pmakerIdx)+",'"+pd.getValue().get(hnbnIdx)+","+pd.getValue().get(bhnnmIdx))
                                                    .forEach(pw::println);
                                            });
                        });
        }
    }
}
