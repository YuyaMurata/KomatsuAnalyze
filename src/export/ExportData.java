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
    private static String KISY = "PC138US";

    public static void main(String[] args) {
        //ヘッダー設定
        Map headers = new LinkedHashMap();
        headers.put("SID", -1);
        headers.put("日付", -1);
        headers.put("受注.受注金額", dataIndex.get("受注").indexOf("SKKG"));
        headers.put("KOMTRAX_ERROR.エラーコード", dataIndex.get("KOMTRAX_ERROR").indexOf("ERROR_CODE"));
        headers.put("KOMTRAX_ERROR.エラー種類", dataIndex.get("KOMTRAX_ERROR").indexOf("ERROR_KIND"));
        headers.put("KOMTRAX_SMR.SMR", dataIndex.get("KOMTRAX_SMR").indexOf("SMR_VALUE"));
        headers.put("経過", -1);

        //車両の読み込み
        Map<String, SyaryoObject4> map = new SyaryoToZip3().read(PATH + "syaryo_obj_" + KISY + "_form.bz2");

        //単体
        uniExport("test.csv", headers, new SyaryoAnalizer(map.get("PC138US-8-20194")));

        //複数
        //全部
    }

    private static void uniExport(String f, Map<String, Integer> headers, SyaryoAnalizer syaryo) {
        try (PrintWriter pw = CSVFileReadWrite.writer(f)) {
            pw.println(headers.keySet().stream().collect(Collectors.joining(",")));
            int cnt = 0;

            //日付あり
            List<String> dates = dateList(headers, Arrays.asList(syaryo));
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
                                System.out.println(sbns);
                            }
                            for (String sbn : sbns.split(",")) {
                                str = (String) syaryo.get().get(k).get(sbn).get(idx);
                            }
                        } else {
                            str = (String) syaryo.get().get(k).get(date).get(idx);
                        }
                    } catch (NullPointerException e) {
                    }

                    line.append(str).append(",");
                    
                }
                System.out.println(line);
                line.deleteCharAt(line.length() - 1);
                pw.println(line);
            }
        }
    }

    private static List<String> dateList(Map<String, Integer> headers, List<SyaryoAnalizer> syaryos) {
        List<String> date = new ArrayList<>();
        for (String key : headers.keySet()) {
            if (!key.contains(".")) {
                continue;
            }

            String k = key.split("\\.")[0];
            if (k.contains("受注")) {
                for (SyaryoAnalizer syaryo : syaryos) {
                    date.addAll(syaryo.getValue(k, "ODDAY", false));
                }

            } else {
                for (SyaryoAnalizer syaryo : syaryos) {
                    date.addAll(syaryo.get().get(k).keySet());
                }
            }
        }

        date = date.stream().distinct().sorted().collect(Collectors.toList());

        return date;
    }
}
