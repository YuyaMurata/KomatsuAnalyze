/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class SimpleExporter {
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String KISY = "PC200";
    private static Map<String, SyaryoObject> map;
    private static String simplefilter = "";

    public static void main(String[] args) {
        LOADER.setFile(KISY+"_form");
        map = LOADER.getSyaryoMap();
        
        //ヘッダー設定
        Map<String, Integer> headers = new LinkedHashMap();
        headers.put("受注.会社", LOADER.index("受注", "会社CD"));
        headers.put("受注.作番", LOADER.index("受注", "KEY"));
        headers.put("受注.日付", LOADER.index("受注", "ODDAY"));
        headers.put("受注.作業形態", LOADER.index("受注", "SGYO_KTICD"));
        headers.put("受注.作業形態名", LOADER.index("受注", "SGKT_NM"));
        headers.put("受注.概要", LOADER.index("受注", "GAIYO_1"));
        //headers.put("部品.会社", LOADER.index("部品", "会社CD"));
        //headers.put("部品.作番", LOADER.index("部品", "KEY"));
        //headers.put("部品.品番", LOADER.index("部品", "HNBN"));
        //headers.put("部品.数量", LOADER.index("部品", "JISI_SU"));
        //headers.put("部品.金額", LOADER.index("部品", "SKKG"));
        //headers.put("部品.品名", LOADER.index("部品", "BHN_NM"));
        //headers.put("作業.会社", LOADER.index("作業", "会社CD"));
        //headers.put("作業.作番", LOADER.index("作業", "KEY"));
        //headers.put("作業.作業コード", LOADER.index("作業", "SGYOCD"));
        //headers.put("作業.作業名", LOADER.index("作業", "SGYO_NM"));
        //headers.put("顧客.顧客CD", dataIndex.get("顧客").indexOf("NNSCD"));
        //headers.put("顧客.区分", dataIndex.get("顧客").indexOf("KKYK_KBN"));
        //headers.put("顧客.業種", dataIndex.get("顧客").indexOf("GYSCD"));
        
        //フィルタ設定　定期メンテナンス
        simplefilter = " AT,";
        
        System.out.println(headers);

        //単体
        //String name = "PC200-8N1-313582";
        //uniExport("ExportData_" + name + ".csv", headers, name, filter);

        //複数
        //String[] names = new String[]{"PC200-8N1-310531", "PC200-8N1-315586", "PC200-8N1-313998", "PC200-8N1-312914", "PC200-8N1-316882"};
        //multiExport("ExportData_Multi_"+names.length+".csv", headers, names, filter);
        
        //全部
        allExport("ExportData_"+KISY+"_ALL_km4.csv", headers);
    }

    private static void allExport(String f, Map<String, Integer> headers) {
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(f)) {
            pw.println("SID,"+headers.keySet().stream().collect(Collectors.joining(",")));
            for (String name : map.keySet()) {
                System.out.println(name);
                try (SyaryoAnalizer syaryo = new SyaryoAnalizer(map.get(name), false)) {
                    export(pw, headers, syaryo);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static void multiExport(String f, Map<String, Integer> headers, String[] names) {
        try (PrintWriter pw = CSVFileReadWrite.writer(f)) {
            pw.println(headers.keySet().stream().collect(Collectors.joining(",")));
            for (String name : names) {
                System.out.println(name);
                SyaryoAnalizer syaryo = new SyaryoAnalizer(map.get(name), false);
                export(pw, headers, syaryo);
            }
        }
    }

    private static void uniExport(String f, Map<String, Integer> headers, String name) {
        try (PrintWriter pw = CSVFileReadWrite.writer(f)) {
            pw.println(headers.keySet().stream().collect(Collectors.joining(",")));
            System.out.println(name);
            SyaryoAnalizer syaryo = new SyaryoAnalizer(map.get(name), false);
            export(pw, headers, syaryo);
        }
    }

    private static void export(PrintWriter pw, Map<String, Integer> headers, SyaryoAnalizer syaryo) {
        List<Integer> index = new ArrayList(headers.values());
        String key = headers.keySet().stream().findFirst().get().split("\\.")[0];
        syaryo.getValue(key, index.toArray(new Integer[index.size()])).values().stream()
                            .filter(s -> s.toString().contains(simplefilter))
                            .map(s -> syaryo.get().name+","+String.join(",", s))
                            .forEach(pw::println);
    }
}
