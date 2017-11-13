/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.csv;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class CSVViewerOutput {

    public static Integer none(String filename, Map<String, Integer> selectData, List<SyaryoObject> syaryos) {
        int n = 0;

        try (PrintWriter pw = CSVFileReadWrite.writer(filename)) {
            pw.println(selectData.keySet().stream().collect(Collectors.joining(",")));

            for (SyaryoObject syaryo : syaryos) {
                int max = 0;
                for (String header : selectData.keySet()) {
                    String key = header.split("\\.")[0];
                    if (max < syaryo.getSize(key)) {
                        max = syaryo.getSize(key);
                    }
                }

                for (int i = 0; i < max; i++) {
                    n++;
                    List<String> row = new ArrayList();
                    for (String header : selectData.keySet()) {
                        String key = header.split("\\.")[0];
                        Integer index = selectData.get(header);
                        //System.out.println(key + " ," + index);
                        if (i < syaryo.getCol(key, index).size()) {
                            row.add(syaryo.getCol(key, index).get(i));
                        } else {
                            row.add(syaryo.getCol(key, index).get(syaryo.getCol(key, index).size() - 1));
                        }
                    }
                    pw.println(row.stream().map(s -> s.split("#")[0]).collect(Collectors.joining(",")));
                }

                System.out.println(syaryo.getName() + ":" + max + "行 csv出力!");
            }
        }

        return n;
    }

    public static Integer time(String filename, Map<String, Integer> selectData, List<SyaryoObject> syaryos) {
        int n = 0;

        //日付
        List<String> dateHeaders = selectData.entrySet().stream()
            .filter(s -> (s.getKey().contains("日") && (s.getValue() == -1)))
            .map(s -> s.getKey())
            .collect(Collectors.toList());

        try (PrintWriter pw = CSVFileReadWrite.writer(filename)) {
            pw.println("日付,車両,データ,値");

            for (SyaryoObject syaryo : syaryos) {
                int n1 = 0;
                //日付 + データ
                for (String header : dateHeaders) {
                    String key = header.split("\\.")[0];
                    List<String> dates = syaryo.getCol(key, selectData.get(header));
                    for (String date : dates) {
                        List<String> hList = selectData.keySet().stream()
                            .filter(s -> (!s.equals(header) && s.contains(key)))
                            .collect(Collectors.toList());
                        for (String h : hList) {
                            n++;
                            n1++;
                            String hkey = h.split("\\.")[0];
                            if (h.contains("経過日")) {
                                pw.println(date.split("#")[0] + "," + syaryo.getName() + "," + h + "," + syaryo.getRow("経過日", date).get(0));
                            } else {
                                pw.println(date.split("#")[0] + "," + syaryo.getName() + "," + h + "," + syaryo.getRow(hkey, date).get(selectData.get(h)));
                            }
                        }
                    }
                }

                //日付 = データ
                dateHeaders = selectData.entrySet().stream()
                    .filter(s -> s.getValue() == -2)
                    .map(s -> s.getKey())
                    .collect(Collectors.toList());

                for (String header : dateHeaders) {
                    n++;
                    n1++;
                    String key = header.split("\\.")[0];
                    List<String> dates = syaryo.getCol(key, selectData.get(header));
                    String date = syaryo.getRow(key, "").get(0);
                    pw.println(date + "," + syaryo.getName() + "," + header + "," + date);
                }

                System.out.println(syaryo.getName() +":" + n1 + "行 csv出力!");
            }
        }

        return n;
    }
}
