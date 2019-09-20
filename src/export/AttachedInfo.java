/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import data.code.PartsCodeConv;
import file.CSVFileReadWrite;
import file.ListToCSV;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class AttachedInfo {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();

    public static void main(String[] args) {
        //workname(file);
        LOADER.setFile("PC200_form");
        //analizeLCC("PC200_mainte_eval.csv");
        //serviceinfo("list\\PC200_部品分類_190829更新.csv", false, false);
        serviceDate("list\\PC200_部品分類_190829更新.csv");
        //workinfo(file);
        //analizeLCC("PC200_culster_use.csv");
        //partsinfo("PC200_エンジン.csv");
    }

    /**
     * 車両IDベースの紐付け
     */
    private static void analizeLCC(String file) {
        Map<String, SyaryoObject> map = LOADER.getSyaryoMap();

        try (PrintWriter csv = CSVFileReadWrite.writer(file + "_attched_lcc.csv")) {
            try (BufferedReader br = CSVFileReadWrite.readerSJIS(file)) {
                String line = br.readLine();
                csv.println(line + ",y,SMR,LCC,ACC,LCC-ACCIDENT");
                while ((line = br.readLine()) != null) {
                    String name = line.split(",")[0];
                    try (SyaryoAnalizer analize = new SyaryoAnalizer(map.get(name), true)) {
                        if (analize.acmLCC >= 0) {
                            csv.println(line + "," + (analize.currentAge_day / 365d) + "," + analize.maxSMR[3] + "," + analize.acmLCC + "," + analize.acmAccidentPrice + "," + (analize.acmLCC - analize.acmAccidentPrice));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 作番に日付情報とSMRを負荷
     */
    private static void serviceDate(String file) {
        Map<String, SyaryoObject> map = LOADER.getSyaryoMap();
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(file + "_attached_info.csv")) {
            List<String> csv = ListToCSV.toList(file);
            List<String> h = Arrays.asList(csv.get(0).split(","));
            csv.remove(0);

            System.out.println(h);

            //header
            int odrdate = LOADER.index("受注", "ODDAY");
            int fdate = LOADER.index("受注", "SGYO_KRDAY");
            pw.println("SID,作番," + String.join(",", h) + ",受注日,作業完了日");

            csv.stream()
                    .map(l -> l.split(","))
                    .forEach(s -> {
                        String key = s[h.indexOf("SID")];
                        String value = s[h.indexOf("部品.作番")].split("#")[0];

                        SyaryoObject syaryo = map.get(key);
                        //System.out.println(syaryo.get("受注"));
                        List<String> odr = syaryo.get("受注").get(value.split("#")[0]);
                        pw.println(String.join(",", s) + "," +odr.get(odrdate)+","+odr.get(fdate));
                    });

        }
    }

    /**
     * サービス情報の付加 車両ID＋会社ID+作番からサービスを紐付ける
     *
     * @param file
     */
    private static void serviceinfo(String file, Boolean w, Boolean p) {
        Map<String, SyaryoObject> map = LOADER.getSyaryoMap();
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(file + "_attached_info.csv")) {
            //header
            pw.println("SID,受注情報,作番," + String.join(",", LOADER.indexes("受注")));
            if (w) {
                pw.println("SID,作業明細,作番," + String.join(",", LOADER.indexes("作業")));
            }
            if (p) {
                pw.println("SID,部品明細,作番," + String.join(",", LOADER.indexes("部品")));
            }

            List<String> csv = ListToCSV.toList(file);
            List<String> h = Arrays.asList(csv.get(0).split(","));
            csv.remove(0);

            Map<String, Map<String, String>> source = new HashMap<>();
            csv.stream()
                    .map(l -> l.split(","))
                    .forEach(s -> {
                        String key = s[h.indexOf("SID")];
                        String value = s[h.indexOf("作番")].split("#")[0];
                        if (source.get(key) == null) {
                            source.put(key, new HashMap<>());
                        }

                        source.get(key).put(value, "");
                    });

            source.entrySet().stream().forEach(e -> {
                try (SyaryoAnalizer s = new SyaryoAnalizer(map.get(e.getKey()), true)) {
                    if (s.numAccident == 0) {
                        e.getValue().keySet().stream().forEach(key -> {
                            //受注情報の追加
                            List<String> odr = s.get("受注").get(key);
                            pw.println(s.name + ",受注情報," + key + "," + String.join(",", odr));

                            //作業明細の追加
                            if (w && s.getSBNWork(key) != null) {
                                s.getSBNWork(key).entrySet().stream()
                                        .map(m -> s.name + ",作業明細," + key + "," + String.join(",", m.getValue()))
                                        .forEach(pw::println);
                            }

                            //部品明細の追加
                            if (p && s.getSBNParts(key) != null) {
                                s.getSBNParts(key).entrySet().stream()
                                        .map(m -> s.name + ",部品明細," + key + "," + String.join(",", m.getValue()))
                                        .forEach(pw::println);
                            }
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

        }
    }

    /**
     * 作業明細を追加 車両ID＋作番をベースに作業明細情報をぶら下げる サービス情報　レコード#1 作業明細情報　レコード#1 作業明細情報
     * レコード#2
     *
     * @param file
     */
    private static void workinfo(String file) {
        Map<String, SyaryoObject> map = LOADER.getSyaryoMap();
        try (PrintWriter csv = CSVFileReadWrite.writer(file + "_attached_info.csv")) {
            try (BufferedReader br = CSVFileReadWrite.reader(file)) {
                String line = br.readLine();
                List<String> l = Arrays.asList(line.split(","));
                //l.set(12, "作業明細");
                csv.println(String.join(",", l));

                while ((line = br.readLine()) != null) {
                    l = Arrays.asList(line.split(","));
                    String name = l.get(0); //車両ID
                    String sbn = l.get(2);  //作番

                    try (SyaryoAnalizer s = new SyaryoAnalizer(map.get(name), false)) {
                        csv.println(String.join(",", l));

                        Map<String, List<String>> work = s.getSBNWork(sbn);
                        if (work != null) {
                            work.entrySet().stream().forEach(w -> {
                                csv.println(",作業明細," + w.getKey() + "," + String.join(",", w.getValue()));
                            });
                        } else {
                            csv.println(",作業明細,なし");
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void partsinfo(String file) {

        try (PrintWriter csv = CSVFileReadWrite.writerSJIS(file + "_attached_info.csv")) {
            try (BufferedReader br = CSVFileReadWrite.reader(file)) {
                String line = br.readLine();
                csv.println(line);

                while ((line = br.readLine()) != null) {
                    String pid = line.split(",")[3];
                    String pnm = line.split(",")[4];
                    String price = line.split(",")[6];
                    String cd = PartsCodeConv.conv(pid, pnm, price);

                    String l = line.replace(pid + "," + pnm, cd + "," + pid + "," + pnm);
                    csv.println(l);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
