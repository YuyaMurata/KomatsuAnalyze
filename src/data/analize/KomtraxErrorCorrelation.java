/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import file.ListToCSV;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import param.KomatsuUserParameter;

/**
 *
 * @author ZZ17807
 */
public class KomtraxErrorCorrelation {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String corrFile = KomatsuUserParameter.AZ_PATH + "user\\PC200_KOMTRAX_ERROR_summary_day.csv";

    public static void main(String[] args) {
        LOADER.setFile("PC200_form");
        //summary(LOADER.getSyaryoMap(), false);
        correlation(corrFile);
    }

    private static List<String> summary(Map<String, SyaryoObject> map, Boolean flg) {
        int idx1 = LOADER.index("KOMTRAX_ERROR", "ERROR_CODE");
        int idx2 = LOADER.index("KOMTRAX_ERROR", "VALUE");

        List<String> ec = map.values().parallelStream()
                .map(e -> e.get("KOMTRAX_ERROR"))
                .filter(e -> e != null)
                .flatMap(ke -> ke.values().parallelStream().map(kev -> kev.get(idx1)).distinct())
                .distinct().sorted().collect(Collectors.toList());

        if (!flg) {
            return ec;
        }

        Map<String, List<Map.Entry>> resultsb = new ConcurrentHashMap<>();

        map.values().parallelStream().forEach(s -> {
            s.startHighPerformaceAccess();

            if (s.get("KOMTRAX_ERROR") != null) {
                System.out.println(s.name);
                List result = ec.stream().map(code -> errorDayCount(s, code, idx1, idx2)).collect(Collectors.toList());
                resultsb.put(s.name, result);
            }

            s.stopHighPerformaceAccess();
        });

        Map<String, List<Map.Entry>> results = new TreeMap();
        results.putAll(resultsb);

        try (PrintWriter pw1 = CSVFileReadWrite.writerSJIS("KOMTRAX_ERROR_summary_count.csv");
                PrintWriter pw2 = CSVFileReadWrite.writerSJIS("KOMTRAX_ERROR_summary_day.csv")) {
            //header
            pw1.println("SID," + String.join(",", ec));
            pw2.println("SID," + String.join(",", ec));

            //data
            results.entrySet().forEach(r -> {
                pw1.println(r.getKey() + "," + r.getValue().stream().map(rs -> rs.getKey().toString()).collect(Collectors.joining(",")));
                pw2.println(r.getKey() + "," + r.getValue().stream().map(rs -> rs.getValue().toString()).collect(Collectors.joining(",")));
            });
        }

        return ec;
    }

    private static Map.Entry<Long, Long> errorDayCount(SyaryoObject s, String code, int idx1, int idx2) {
        AbstractMap.SimpleEntry set;

        OptionalInt v = s.get("KOMTRAX_ERROR").values().parallelStream()
                .filter(e -> e.get(idx1).equals(code))
                .map(e -> e.get(idx2))
                .mapToInt(e -> Integer.valueOf(e))
                .max();

        Long d = s.get("KOMTRAX_ERROR").values().parallelStream()
                .filter(e -> e.get(idx1).equals(code))
                .count();

        if (v.isPresent()) {
            set = new AbstractMap.SimpleEntry(v.getAsInt(), d.intValue());
        } else {
            set = new AbstractMap.SimpleEntry(0, 0);
        }

        return set;
    }

    private static void correlation(String file) {
        PearsonsCorrelation pearson = new PearsonsCorrelation();

        List<String> line = ListToCSV.toList(file);
        List<String> header = Arrays.asList(line.get(0).split(","));
        header = header.subList(1, header.size());
        List<String> sids = new ArrayList<>();
        line.remove(0);

        double[][] mat = new double[header.size()][line.size()];
        line.stream().map(s -> s.split(",")).forEach(s -> {
            sids.add(s[0]);
            for (int i = 1; i < s.length; i++) {
                mat[i - 1][sids.indexOf(s[0])] = Double.valueOf(s[i]);
            }
        });
        System.out.println("Matrix[" + mat.length + "][" + mat[0].length + "]");
        //matrixPrint("test.csv", sids,header, mat);

        //相関解析
        Double[][] corrmat = new Double[header.size()][header.size()];
        for (int i = 0; i < header.size(); i++) {
            for (int j = 0; j <= i; j++) {
                if (i == j) {
                    corrmat[i][j] = 1d;
                } else {
                    corrmat[i][j] = pearson.correlation(mat[i], mat[j]);
                }
            }
        }
        System.out.println("CorrMatrix[" + corrmat.length + "][" + corrmat[0].length + "]");
        matrixPrint("PC200_KOMTRAX_ERROR_相関解析.csv", header, header, corrmat);

        //台数解析
        Long[][] nummat = new Long[header.size()][header.size()];
        Long[][] nummator = new Long[header.size()][header.size()];
        for (int i = 0; i < header.size(); i++) {
            for (int j = 0; j <= i; j++) {
                if (i == j) {
                    nummat[i][j] = null;
                } else {
                    double[] mati = mat[i];
                    double[] matj = mat[j];
                    nummat[i][j] = IntStream.range(0, mat[0].length).filter(s -> mati[s] > 0d && matj[s] > 0d).count();
                    nummator[i][j] = IntStream.range(0, mat[0].length).filter(s -> mati[s] > 0d || matj[s] > 0d).count();
                }
            }
        }
        System.out.println("NumMatrix[" + nummat.length + "][" + nummat[0].length + "]");
        matrixPrint("PC200_KOMTRAX_ERROR_台数AND解析.csv", header, header, nummat);
        matrixPrint("PC200_KOMTRAX_ERROR_台数OR解析.csv", header, header, nummator);

        //複合解析
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS("PC200_complex.csv")) {
            //header
            pw.println("エラーコード1,エラーコード2,相関係数,エラー1発報台数,エラー2発報台数,2エラー発報台数(AND),2エラー発報台数(OR)");
            
            for (int i = 0; i < header.size(); i++) {
                for (int j = 0; j < i; j++) {
                    if (corrmat[i][j] > 0.7 || corrmat[i][j] < -0.7) {
                        double[] mati = mat[i];
                        double[] matj = mat[j];
                        Long sumi = IntStream.range(0, mat[0].length).filter(mi -> mati[mi] > 0d).count();
                        Long sumj = IntStream.range(0, mat[0].length).filter(mj -> matj[mj] > 0d).count();
                        pw.println(header.get(i)+","+header.get(j)+","+corrmat[i][j]+","+sumi+","+sumj+","+nummat[i][j]+","+nummator[i][j]);
                    }
                }
            }
        }
    }

    private static void matrixPrint(String f, List<String> colh, List<String> rowh, Object[][] data) {
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(f)) {
            //header
            pw.println("," + String.join(",", colh));
            for (int i = 0; i < data.length; i++) {
                pw.println(rowh.get(i) + "," + Arrays.asList(data[i]).stream().map(d -> d == null ? "" : d.toString()).collect(Collectors.joining(",")));
            }
        }
    }
}
