/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import index.SyaryoObjectElementsIndex;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import json.MapIndexToJSON;
import json.SyaryoToZip3;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class ExportData2 {

    //レイアウト
    private static Map<String, List> dataIndex = SyaryoObjectElementsIndex.getInstance().getIndex();
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static String KISY = "PC200";
    private static Map<String, SyaryoObject4> map;
    
    private static String syaryofilename = PATH + "syaryo_obj_" + KISY + "_sv_form.bz2";
    private static String filename = "ExportData_"+KISY;

    public static void main(String[] args) {
        //取得データ
        Map headers = new LinkedHashMap();
        headers.put("受注", new Integer[]{dataIndex.get("受注").indexOf("SGYO_KTICD"), dataIndex.get("受注").indexOf("ODR_KBN"), dataIndex.get("受注").indexOf("SKKG")});
        //headers.put("作業", new Integer[]{dataIndex.get("作業").indexOf("KSYCD"), dataIndex.get("作業").indexOf("SGYOCD"), dataIndex.get("作業").indexOf("0")});
        headers.put("部品", new Integer[]{dataIndex.get("部品").indexOf("KSYCD"), dataIndex.get("部品").indexOf("HNBN"), dataIndex.get("部品").indexOf("None"), dataIndex.get("部品").indexOf("BHN_NM"), dataIndex.get("部品").indexOf("JISI_SU"), dataIndex.get("部品").indexOf("SKKG")});
        //headers.put("KOMTRAX_ERROR", new Integer[]{dataIndex.get("KOMTRAX_ERROR").indexOf("ERROR_CODE"), dataIndex.get("KOMTRAX_ERROR").indexOf("COUNT")});
        //headers.put("新車", new Integer[]{-1, dataIndex.get("新車").indexOf("sell")});
        //headers.put("SMR", new Integer[]{-1, dataIndex.get("SMR").indexOf("SVC_MTR")});
        //headers.put("KOMTRAX_SMR", new Integer[]{-1, dataIndex.get("KOMTRAX_SMR").indexOf("SMR_VALUE")});
        //headers.put("KOMTRAX_FUEL_CONSUME", new Integer[]{-1, dataIndex.get("KOMTRAX_FUEL_CONSUME").indexOf("CONSUME_COUNT")});

        //Time
        long start = System.currentTimeMillis();
        
        //テスト
        //export("test.json", headers, new SyaryoAnalizer(map.get("PC200-8N1-313582")), null);

        //ヘッダーの登録
        Map recmap = regHeader(headers);
        
        //車両の読み込み
        map = new SyaryoToZip3().read(syaryofilename);
        
        //単体
        //String name = "PC200-8N1-313582";
        //uniExport("ExportData_" + name + ".json", headers, name, filter);
        //複数
        //String[] names = new String[]{"PC200-8N1-310531", "PC200-8N1-315586", "PC200-8N1-313998", "PC200-8N1-312914", "PC200-8N1-316882"};
        //multiExport(recmap, "ExportData_Multi_"+names.length+".json", headers, names);
        //全部
        allExport(recmap, filename+"_ALL.json", headers);
        
        long stop = System.currentTimeMillis();
        System.out.println("車両数:"+map.size()+" "+"抽出対象車両数:"+recmap.size());
        System.out.println("Time:"+(stop-start)+"ms");
    }
    
    public static Map regHeader(Map<String, Integer[]> headers){
        Map recmap = new HashMap();
        Map headMap = new HashMap();
        
        for(String header : headers.keySet()){
            Map inMap = new HashMap();
            inMap.put(header, Arrays.asList(headers.get(header)).stream().map(i -> i<0?(header+"key"):dataIndex.get(header).get(i)).collect(Collectors.toList()));
            headMap.put(header, inMap);
        }
       
        recmap.put("_headers", headMap);
        
        return recmap;
    }
    
    public static void allExport(Map recmap, String f, Map<String, Integer[]> headers) {
        f = KomatsuDataParameter.EXPORT_PATH+f;
        
        for (String name : map.keySet()) {
            System.out.println(name);
            try (SyaryoAnalizer syaryo = new SyaryoAnalizer(map.get(name))) {
                export(recmap, headers, syaryo);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        new MapIndexToJSON().write(f, recmap);
    }

    public static void multiExport(Map recmap, String f, Map<String, Integer[]> headers, String[] names) {
        f = KomatsuDataParameter.EXPORT_PATH+f;
        
        for (String name : names) {
            System.out.println(name);
            SyaryoAnalizer syaryo = new SyaryoAnalizer(map.get(name));
            export(recmap, headers, syaryo);
        }
        new MapIndexToJSON().write(f, recmap);
    }

    public static void uniExport(Map recmap, String f, Map<String, Integer[]> headers, String name, Map filter) {
        f = KomatsuDataParameter.EXPORT_PATH+f;
        
        System.out.println(name);
        SyaryoAnalizer syaryo = new SyaryoAnalizer(map.get(name));
        
        export(recmap, headers, syaryo);
        new MapIndexToJSON().write(f, recmap);
    }

    private static void export(Map recmap, Map<String, Integer[]> headers, SyaryoAnalizer syaryo) {
        Map<String, Map> expMap = syaryo.export(headers);
        Optional s = headers.keySet().stream().filter(h -> expMap.get(h) == null).findFirst();
        
        if(!s.isPresent())
            recmap.put(syaryo.get().name, expMap);
    }
}
