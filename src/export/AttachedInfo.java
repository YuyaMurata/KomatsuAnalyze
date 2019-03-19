/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import data.code.PartsCodeConv;
import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
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
        //LOADER.setFile("PC200_form");
        //serviceinfo(file);
        //workinfo(file);
        //analizeLCC("PC200_culster_use.csv");
        partsinfo("ExportData_PC200_ALL.csv");
    }

    /**
     * 車両IDベースの紐付け
     */
    private static void analizeLCC(String file) {
        Map<String, SyaryoObject> map = LOADER.getSyaryoMap();

        try (PrintWriter csv = CSVFileReadWrite.writer(file + "_attched_lcc.csv")) {
            try (BufferedReader br = CSVFileReadWrite.reader(file)) {
                String line = br.readLine();
                csv.println(line + ",y,SMR,LCC,ACC,LCC-ACCIDENT");
                while ((line = br.readLine()) != null) {
                    String name = line.split(",")[0];
                    try (SyaryoAnalizer analize = new SyaryoAnalizer(map.get(name), true)) {
                        if(analize.acmLCC >= 0)
                            csv.println(line+","+(analize.currentAge_day/365d)+","+analize.maxSMR[3]+","+analize.acmLCC+","+analize.acmAccidentPrice+","+(analize.acmLCC-analize.acmAccidentPrice));
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
     * サービス情報の付加 車両ID＋作番から作業形態を紐付ける
     *
     * @param file
     */
    private static void serviceinfo(String file) {
        Map<String, SyaryoObject> map = LOADER.getSyaryoMap();
        try (PrintWriter csv = CSVFileReadWrite.writer(file + "_attached_info.csv")) {
            try (BufferedReader br = CSVFileReadWrite.reader(file)) {
                String line = br.readLine();
                List<String> l = Arrays.asList(line.split(","));
                l.set(3, "作業形態");
                csv.println(String.join(",", l));

                while ((line = br.readLine()) != null) {
                    l = Arrays.asList(line.split(","));
                    String name = l.get(0);
                    String sbn = l.get(2);

                    SyaryoObject s = map.get(name);
                    List<String> sv = s.get("受注").get(sbn);
                    l.set(3, sv.get(LOADER.index("受注", "SGYO_KTICD")));

                    csv.println(String.join(",", l));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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
    
    private static void partsinfo(String file){
        
        try (PrintWriter csv = CSVFileReadWrite.writerSJIS(file + "_attached_info.csv")) {
            try (BufferedReader br = CSVFileReadWrite.reader(file)) {
                String line = br.readLine();
                csv.println(line);

                while ((line = br.readLine()) != null) {
                    String pid = line.split(",")[3];
                    String pnm = line.split(",")[4];
                    String price = line.split(",")[6];
                    String cd = PartsCodeConv.conv(pid, pnm, price);
                    
                    String l = line.replace(pid+","+pnm, cd+","+pid+","+pnm);
                    csv.println(l);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
