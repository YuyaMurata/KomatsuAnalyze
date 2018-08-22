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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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

    public static void main(String[] args) {
        //ヘッダー設定
        Map headers = new LinkedHashMap();
        headers.put("SID", -1);
        headers.put("日付", -1);
        headers.put("受注.作業形態", dataIndex.get("受注").indexOf("SGYO_KTICD"));
        headers.put("受注.受注金額", dataIndex.get("受注").indexOf("SKKG"));
        headers.put("作業.作業コード", dataIndex.get("作業").indexOf("SGYOCD"));
        headers.put("KOMTRAX_ERROR.エラーコード", dataIndex.get("KOMTRAX_ERROR").indexOf("ERROR_CODE"));
        headers.put("KOMTRAX_ERROR.エラー種類", dataIndex.get("KOMTRAX_ERROR").indexOf("ERROR_KIND"));
        headers.put("KOMTRAX_SMR.SMR", dataIndex.get("KOMTRAX_SMR").indexOf("SMR_VALUE"));
        headers.put("経過", -1);

        Map filter = KomatsuDataParameter.PERIOD_MAINTE;

        //車両の読み込み
        map = new SyaryoToZip3().read(PATH + "syaryo_obj_" + KISY + "_form.bz2");
        
        //テスト
        export(null, headers, new SyaryoAnalizer(map.get("PC200-8N1-313582")), null);
        
        //単体
        //String name = "PC200-8N1-313582";
        //uniExport("ExportData_" + name + ".csv", headers, name, filter);

        //複数
        //String[] names = new String[]{"PC200-8N1-310531", "PC200-8N1-315586", "PC200-8N1-313998", "PC200-8N1-312914", "PC200-8N1-316882"};
        //multiExport("ExportData_Multi_"+names.length+".csv", headers, names, filter);
        
        //全部
        //allExport("ExportData_"+KISY+"_ALL.csv", headers, filter);
    }

    private static void allExport(String f, Map<String, Integer> headers, Map filter) {
        try (PrintWriter pw = CSVFileReadWrite.writer(f)) {
            pw.println(headers.keySet().stream().collect(Collectors.joining(",")));
            for (String name : map.keySet()) {
                System.out.println(name);
                try (SyaryoAnalizer syaryo = new SyaryoAnalizer(map.get(name))) {
                    export(pw, headers, syaryo, filter);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static void multiExport(String f, Map<String, Integer> headers, String[] names, Map filter) {
        try (PrintWriter pw = CSVFileReadWrite.writer(f)) {
            pw.println(headers.keySet().stream().collect(Collectors.joining(",")));
            for (String name : names) {
                System.out.println(name);
                SyaryoAnalizer syaryo = new SyaryoAnalizer(map.get(name));
                export(pw, headers, syaryo, filter);
            }
        }
    }

    private static void uniExport(String f, Map<String, Integer> headers, String name, Map filter) {
        try (PrintWriter pw = CSVFileReadWrite.writer(f)) {
            pw.println(headers.keySet().stream().collect(Collectors.joining(",")));
            System.out.println(name);
            SyaryoAnalizer syaryo = new SyaryoAnalizer(map.get(name));
            export(pw, headers, syaryo, filter);
        }
    }

    private static void export(PrintWriter pw, Map<String, Integer> headers, SyaryoAnalizer syaryo, Map filter) {
        int cnt = 0;
        
        String name = syaryo.get().name;
        System.out.println(name);
        
        Map<String, Map<String, List<String>>> expMap = new HashMap();
        //親Keyの取得
        List<String> data = headers.keySet().stream()
                                    .filter(s -> s.contains("."))
                                    .map(s -> s.split("\\.")[0])
                                    .distinct()
                                    .collect(Collectors.toList());
        
        for(String d : data){
            //子要素の取得
            List<Integer> elements = headers.entrySet().stream()
                                                    .filter(s -> s.getKey().contains(d))
                                                    .map(s -> s.getValue())
                                                    .collect(Collectors.toList());
            
            expMap.put(d, syaryo.getValue(d, elements));
        }
        
        if(headers.get("日付") != null){
            //全日付の取得
            List<String> dates = new ArrayList<>();
            for(String subKey : expMap.keySet()){
                if(KomatsuDataParameter.TRANS_DATE.contains(subKey))
                    dates.addAll(expMap.get(subKey).keySet().stream().map(s -> syaryo.getSBNDate(s, true)).collect(Collectors.toList()));
                else
                    dates.addAll(expMap.get(subKey).keySet());
            }
            
            //データ書き込み
            List<String> header = headers.keySet().stream().map(s -> s.split("\\.")[0]).distinct().collect(Collectors.toList());
            for(String date : dates){
                List<String> line = new ArrayList<>();
                for(String head : header){
                    String subKey = date;
                    if(KomatsuDataParameter.TRANS_DATE.contains(head))
                        subKey = syaryo.getSBNDate(date, true);
                    if(expMap.get(head).get(subKey) != null)
                        line.addAll(expMap.get(head).get(subKey));
                    else
                        line.addAll();
                }
            }
        }
        
        System.out.println(expMap);
    }
}
