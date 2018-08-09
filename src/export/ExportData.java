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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
public class ExportData {

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

        //単体
        //String name = "PC200-8N1-313582";
        //uniExport("ExportData_" + name + ".csv", headers, name, filter);

        //複数
        //String[] names = new String[]{"PC200-8N1-310531", "PC200-8N1-315586", "PC200-8N1-313998", "PC200-8N1-312914", "PC200-8N1-316882"};
        //multiExport("ExportData_Multi_"+names.length+".csv", headers, names, filter);
        //全部
        allExport("ExportData_"+KISY+"_ALL.csv", headers, filter);
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

        //日付あり
        List<String> dates = dateList(headers, syaryo);
        System.out.println(dates);
        for (String date : dates) {
            StringBuilder line = new StringBuilder();
            for (String key : headers.keySet()) {
                String k = key.split("\\.")[0];
                int idx = headers.get(key);
                String str = "";
                try {
                    if (key.equals("SID")) {
                        str = syaryo.syaryo.name;
                    } else if (key.equals("日付")) {
                        str = date.split("#")[0];
                    } else if (key.equals("経過")) {
                        str = syaryo.age(date).toString();
                    } else if (key.contains("受注")) {
                        String sbns = syaryo.getSBNDate(date, false);
                        if (sbns.contains(",")) {
                            //System.out.println(sbns);
                        }
                        for (String sbn : sbns.split(",")) {
                            if (dataFilter(syaryo, k, sbn, syaryo.get().get(k).get(sbn), filter)) {
                                str = (String) syaryo.get().get(k).get(sbn).get(idx);
                            }
                        }
                    } else if (key.contains("作業")) {
                        String sbns = syaryo.getSBNDate(date, false);
                        for (String sbn : sbns.split(",")) {
                            str = (String) syaryo.getSBNWork(sbn).values().stream().map(s -> s.get(idx)).collect(Collectors.joining("_"));
                        }
                    } else if (key.contains("部品")) {
                        String sbns = syaryo.getSBNDate(date, false);
                        for (String sbn : sbns.split(",")) {
                            str = (String) syaryo.getSBNParts(sbn).values().stream().map(s -> s.get(idx)).collect(Collectors.joining("_"));
                        }
                    } else {
                        str = (String) syaryo.get().get(k).get(date).get(idx);
                    }
                } catch (NullPointerException e) {
                }

                line.append(str).append(",");

            }
            //System.out.println(line);
            line.deleteCharAt(line.length() - 1);
            pw.println(line);
        }
    }

    private static Boolean dataFilter(SyaryoAnalizer syaryo, String key, String sbn, List<String> data, Map filter) {
        if (!key.equals("受注")) {
            return true;
        }

        Boolean flg = true;
        //作業形態チェック
        String fkey = "受注.SGYO_KTICD";
        List<String> filterData = Arrays.asList((String[]) filter.get(fkey));
        flg = filterData.contains(data.get(dataIndex.get(fkey.split("\\.")[0]).indexOf(fkey.split("\\.")[1])));

        if (syaryo.get().get("作業") != null) {
            String fkey2 = "作業.SGYOCD";
            List<String> filterData2 = Arrays.asList((String[]) filter.get(fkey));
            flg = flg || syaryo.getSBNWork(sbn).values().stream()
                .map(s -> s.get(dataIndex.get(fkey2.split("\\.")[0]).indexOf(fkey2.split("\\.")[1])))
                .allMatch(wid -> filterData2.contains(wid));
        }
        
        System.out.println(data.get(dataIndex.get(fkey.split("\\.")[0]).indexOf(fkey.split("\\.")[1]))+":"+(((Boolean) filter.get("FLG")) != flg));
        
        return ((Boolean) filter.get("FLG")) != flg;
    }

    private static List<String> dateList(Map<String, Integer> headers, SyaryoAnalizer syaryo) {
        List<String> date = new ArrayList<>();
        for (String key : headers.keySet()) {
            String k = key.split("\\.")[0];
            if (k.equals("作業") || k.equals("部品")) {
                continue;
            }

            if (!key.contains(".")) {
                continue;
            } else if (key.contains("SMR") || key.contains("GPS")) {
                continue;
            } else if (syaryo.get().get(k) == null) {
                continue;
            }

            if (k.contains("受注")) {
                date.addAll(syaryo.getValue(k, "ODDAY", false));
            } else {
                date.addAll(syaryo.get().get(k).keySet());
            }
        }

        date = date.stream().distinct().sorted().collect(Collectors.toList());

        return date;
    }
}
