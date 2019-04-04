/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.index;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import index.SyaryoObjectElementsIndex;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import file.SyaryoToCompress;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class WorkIDPartsIDIndex {

    private static String OUTPATH = "";
    
    //レイアウト
    private static Map<String, List> dataIndex = SyaryoObjectElementsIndex.getInstance().getIndex();
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static String KISY = "PC200";
    private static Map<String, SyaryoObject> map;

    public static void main(String[] args) {
        String filename = OUTPATH + KISY+"_wid_pid_index.bz2";
        Map<String, SyaryoObject> index = new HashMap();

        //車両の読み込み
        map = new SyaryoToCompress().read(PATH + "syaryo_obj_" + KISY + "_form.bz2");
        int odrkbnIdx = dataIndex.get("受注").indexOf("ODR_KBN");
        int sgksycdIdx = dataIndex.get("作業").indexOf("KSYCD");
        int sgcdIdx = dataIndex.get("作業").indexOf("SGYOCD");
        int mainsgIdx = dataIndex.get("作業").indexOf("0"); //DIHY_SGYO_FLG
        int sgnmIdx = dataIndex.get("作業").indexOf("SGYO_NM");
        int ksycdIdx = dataIndex.get("部品").indexOf("KSYCD");
        int pmakerIdx = dataIndex.get("部品").indexOf("None"); //BHN_MAKR_KBN
        int hnbnIdx = dataIndex.get("部品").indexOf("HNBN");
        int bhnnmIdx = dataIndex.get("部品").indexOf("BHN_NM");
        int quantIdx = dataIndex.get("部品").indexOf("JISI_SU");
        int bhnpriceIdx = dataIndex.get("部品").indexOf("SKKG");
        
        map.values().stream()
            .filter(s -> s.get("作業") != null)
            .filter(s -> s.get("部品") != null)
            .forEach(s -> {
                SyaryoAnalizer analize = new SyaryoAnalizer(s, true);
                analize.getValue("受注", new Integer[]{odrkbnIdx}).entrySet().stream()
                    .filter(o -> o.getValue().get(0).equals("2"))
                    .filter(o -> analize.getSBNWork(o.getKey()) != null)
                    .filter(o -> analize.getSBNParts(o.getKey()) != null)
                    .map(o -> o.getKey())
                    .forEach(sbn ->{
                        analize.getSBNWork(sbn).entrySet().stream()
                            .filter(w -> w.getValue().get(mainsgIdx).equals("1"))
                            .forEach(w ->{
                            String sgcd = w.getValue().get(sgcdIdx);
                            
                            SyaryoObject sg = index.get(sgcd);
                            if(sg == null){
                                sg = new SyaryoObject(sgcd);
                                sg.put("作業", new HashMap());
                                sg.put("部品", new HashMap());
                            }
                            sg.startHighPerformaceAccess();
                            
                            sg.get("作業").put(w.getKey(), Arrays.asList(new String[]{w.getValue().get(sgksycdIdx), w.getValue().get(sgnmIdx)}));
                            
                            sg.get("部品").putAll(
                                analize.getSBNParts(sbn).entrySet().stream()
                                    .filter(p -> !p.getValue().get(quantIdx).equals("None"))
                                    .filter(p -> !p.getValue().get(bhnpriceIdx).equals("None"))
                                    .collect(Collectors.toMap(p->p.getKey(), p -> 
                                                    Arrays.asList(new String[]{
                                                        p.getValue().get(ksycdIdx),
                                                        p.getValue().get(pmakerIdx),
                                                        p.getValue().get(hnbnIdx),
                                                        p.getValue().get(bhnnmIdx), 
                                                        String.valueOf(Integer.valueOf(p.getValue().get(bhnpriceIdx))/Integer.valueOf(p.getValue().get(quantIdx)))
                                                    })
                                            )
                                    )
                            );
                            
                            sg.stopHighPerformaceAccess();
                            index.put(sgcd, sg);
                        });
                    });
            });
        
        //価格降順
        index.values().stream().forEach(s -> {
            s.startHighPerformaceAccess();
            
            Map temp = new LinkedHashMap();
            Map<String, Integer> sortMap = s.get("部品").entrySet().stream()
                            .collect(Collectors.toMap(p -> p.getKey(), p -> Integer.valueOf(p.getValue().get(4).toString())));
            List sortList = sortMap.entrySet().stream()
                                .sorted(Map.Entry.comparingByValue())
                                .map(p -> p.getKey()).collect(Collectors.toList());
            sortList.stream().forEach(k ->{
                temp.put(k, s.get("部品").get(k));
            });
            s.put("部品", temp);
            
            s.stopHighPerformaceAccess();
        });
        
        new SyaryoToCompress().write(filename, index);

    }
}
