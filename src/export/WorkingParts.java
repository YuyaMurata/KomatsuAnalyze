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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import file.SyaryoToCompress;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class WorkingParts {

    //レイアウト
    private static Map<String, List> dataIndex = SyaryoObjectElementsIndex.getInstance().getIndex();
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static String KISY = "PC200";
    private static Map<String, SyaryoObject4> map;

    public static void main(String[] args) {
        //車両の読み込み
        map = new SyaryoToCompress().read(PATH + "syaryo_obj_" + KISY + "_sv_form.bz2");
        int sgcdIdx = dataIndex.get("作業").indexOf("SGYOCD");
        int sgmainIdx = dataIndex.get("作業").indexOf("0");
        int ksycdIdx = dataIndex.get("部品").indexOf("KSYCD");
        int pmakerIdx = dataIndex.get("部品").indexOf("None"); //BHN_MAKR_KBN
        int hnbnIdx = dataIndex.get("部品").indexOf("HNBN");
        int bhnnmIdx = dataIndex.get("部品").indexOf("BHN_NM");
        int bhnquantIdx = dataIndex.get("部品").indexOf("JISI_SU");
        int bhnpriceIdx = dataIndex.get("部品").indexOf("SKKG");

        try (PrintWriter pw = CSVFileReadWrite.writer(KISY + "_workingparts.csv")) {
            //Header
            pw.println("SID,会社コード,作番(作業が存在するもの),部品メーカ,品番,品名");

            map.values().stream()
                .filter(s -> s.get("作業") != null)
                .filter(s -> s.get("部品") != null)
                .forEach(s -> {
                    SyaryoAnalizer analize = new SyaryoAnalizer(s);
                    List<String> worksbn = analize.getValue("作業", new Integer[]{0}).keySet().stream()
                        .map(w -> w.split("#")[0])
                        .distinct()
                        .collect(Collectors.toList());

                    worksbn.stream().forEach(ws -> {
                        Optional<String> sgcheck = analize.getSBNWork(ws).values().stream()
                            .filter(w -> w.get(sgmainIdx).equals("1"))
                            .map(w -> w.get(sgcdIdx))
                            .findFirst();
                        
                        if(!sgcheck.isPresent()){
                            System.out.println(ws+":代表作業なし");
                            return;
                        }
                        
                        String sgcd = sgcheck.get();
                        if(analize.getSBNParts(ws) == null){
                            System.out.println(ws+":部品なし");
                            return;
                        }
                        
                        Map<String, Integer> sortMap = analize.getSBNParts(ws).entrySet().stream()
                                                .filter(e -> e.getValue().get(pmakerIdx).equals("10"))
                                                .collect(Collectors.toMap(e -> e.getKey(), e -> Integer.valueOf(e.getValue().get(bhnpriceIdx)) / Integer.valueOf(e.getValue().get(bhnquantIdx))));
                        
                        if(sortMap.isEmpty()){
                            System.out.println(sgcd+":コマツ部品なし");
                            return;
                        }
                        
                        String maxSbn = Collections.max(sortMap.entrySet(), Map.Entry.comparingByValue()).getKey();
                        List<String> parts = analize.get().get("部品").get(maxSbn);
                        
                        //金額の高い部品
                        pw.println(s.name+","+ws+","+sgcd+","+String.join(",", parts));
                        
                        //全部品
                        /*sortMap.keySet().stream().map(pa -> analize.get().get("部品").get(pa)).forEach(pa ->{
                            pw.println(s.name+","+ws+","+sgcd+","+String.join(",", pa));
                        });*/
                    });
                });
        }
    }
}
