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
import java.util.Optional;
import java.util.stream.Collectors;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class CSVViewerOutput {

    public static Integer none(String filename, Map<String, Integer> selectData, SyaryoObject syaryo) {
        int n= 0;
        
        try (PrintWriter pw = CSVFileReadWrite.writer(filename)) {
            pw.println(selectData.keySet().stream().collect(Collectors.joining(",")));

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
                    if (i < syaryo.getRow(key, index).size()) {
                        row.add(syaryo.getRow(key, index).get(i));
                    } else {
                        row.add("");
                    }
                }
                pw.println(row.stream().collect(Collectors.joining(",")));
            }
            
            System.out.println(max + "行 csv出力!");
        }
        
        return n;
    }

    public static Integer time(String filename, Map<String, Integer> selectData, SyaryoObject syaryo) {
        int n = 0;
        
        try (PrintWriter pw = CSVFileReadWrite.writer(filename)) {
            pw.println("日付,データ,値");

            List<String> uniqueHeader = selectData.keySet().stream()
                .map(h -> h.split("\\.")[0])
                .distinct()
                .collect(Collectors.toList());

            for (String uheader : uniqueHeader) {
                n++;
                
                if (selectData.get(uheader) != null) {
                    Integer index = selectData.get(uheader);
                    if (uheader.contains("日")) {
                        pw.println(syaryo.getRow(uheader, index).get(0) + "," + uheader + ",");
                    } else {
                        pw.println("NA," + uheader + "," + syaryo.getRow(uheader, index).get(0));
                    }
                } else {
                    List<String> headers = selectData.keySet().stream()
                        .filter(h -> h.contains(uheader))
                        .collect(Collectors.toList());
                    Optional<String> dateHeaderOption = headers.stream().filter(h -> h.contains("日")).findFirst();
                    String dateHeader = "";
                    if (dateHeaderOption.isPresent()) {
                        dateHeader = dateHeaderOption.get();
                    }

                    headers.remove(dateHeader);

                    String dateKey = dateHeader.split("\\.")[0];
                    for (String date : syaryo.getRow(dateKey, selectData.get(dateHeader))) {
                        List<String> row = new ArrayList<>();
                        row.add(date);
                        
                        for (String header : headers) {
                            String key = header.split("\\.")[0];
                            Integer index = selectData.get(header);
                            
                            List<String> list = syaryo.getCol(key, date);
                            if(list == null) continue;
                            
                            row.add(header);
                            row.add(list.get(index));
                        }
                        
                        if(row.size() > 1)
                            pw.println(row.stream().collect(Collectors.joining(",")));
                    }
                }
            }
            System.out.println(n + "行 csv出力!");
        }
        
        return n;
    }
}
