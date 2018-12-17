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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import file.SyaryoToCompress;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class ExportIrregularData {

    private static String KISY = "PC200";
    private static String OBJPATH = KomatsuDataParameter.OBJECT_PATH;

    public static void main(String[] args) {
        SyaryoToCompress zip3 = new SyaryoToCompress();
        String filename = OBJPATH + "syaryo_obj_" + KISY + "_km_form.bz2";
        Map<String, SyaryoObject> syaryoMap = zip3.read(filename);

        Map<String, List> dataIndex = SyaryoObjectElementsIndex.getInstance().getIndex();

        errSMR(syaryoMap, dataIndex.get("SMR"), dataIndex.get("KOMTRAX_SMR"));

        //errFUEL(syaryoMap, dataIndex.get("FUEL_CONSUME"));
        //errSBN(syaryoMap, "受注");
        //errSGCD(syaryoMap, "作業");
    }

    private static void errSMR(Map<String, SyaryoObject> syaryoMap, List smrList, List kmsmrList) {
        int smrIdx = smrList.indexOf("SVC_MTR");
        int kmsmrIdx = kmsmrList.indexOf("SMR_VALUE");
        try (PrintWriter csv = CSVFileReadWrite.writer("errdata_SMR_" + KISY + ".csv")) {
            //Header
            csv.println("SID,SMR_N,KOMTRAX_SMR_N,最終更新日,MaxSMR,KM最終更新日,KMMaxSMR,稼働時間が下がった(SMR),稼働時間が24Hオーバー(SMR),稼働時間が下がった(KOMTRAX_SMR),稼働時間が24Hオーバー(KOMTRAX_SMR),サービス_KOMTRAXの乖離(標準偏差)");

            for (String sid : syaryoMap.keySet()) {
                try (SyaryoAnalizer syaryo = new SyaryoAnalizer(syaryoMap.get(sid))) {
                    System.out.print(sid + ":");
                    String line = "";

                    //SMR 評価
                    Map<String, List> smr = syaryo.get().get("SMR");
                    Integer smrSize = 0;
                    String temp = "";
                    Integer tempSMR = 0;
                    
                    //評価基準
                    Integer dayover = 0;
                    Integer down = 0;
                    if (smr != null) {
                        smrSize = smr.size();
                        for (String date : smr.keySet()) {
                            Integer value = Integer.valueOf(smr.get(date).get(smrIdx).toString());
                            if (temp.equals("")) {
                                temp = date;
                                tempSMR = value;
                                continue;
                            }

                            int d = SyaryoAnalizer.time(temp, date);
                            //稼働時間が下がった
                            if (tempSMR > value) {
                                down++;
                            } else if (((value - tempSMR) / d) > 24) {
                                //24Hオーバー
                                dayover++;
                            }

                            temp = date;
                            tempSMR = value;
                        }
                    }
                    line = line + "," + down + "," + dayover;

                    //KOMTRAX SMR 評価
                    Map<String, List> kmsmr = syaryo.get().get("KOMTRAX_SMR");
                    Integer kmsmrSize = 0;
                    temp = "";
                    tempSMR = 0;
                    //評価基準
                    dayover = 0;
                    down = 0;
                    if (kmsmr != null) {
                        kmsmrSize = kmsmr.size();
                        for (String date : kmsmr.keySet()) {
                            Integer value = Integer.valueOf(kmsmr.get(date).get(kmsmrIdx).toString());
                            if (temp.equals("")) {
                                temp = date;
                                tempSMR = value;
                                continue;
                            }

                            int d = SyaryoAnalizer.time(temp, date);
                            //稼働時間が下がった
                            if (tempSMR > value) {
                                down++;
                            } else if (((value - tempSMR) / d) > 24) {
                                //24Hオーバー
                                dayover++;
                            }

                            temp = date;
                            tempSMR = value;
                        }
                    }
                    
                    line = sid + "," + smrSize + ","+ kmsmrSize + "," + syaryo.maxSMR[0] + "," + syaryo.maxSMR[1] + "," + syaryo.maxSMR[2] + "," + syaryo.maxSMR[3] + line + "," + down + "," + dayover;

                    //サービス - KOMTRAXの乖離を評価
                    List<Double> dg = new ArrayList<>();
                    int n = 0;
                    if (smr != null && kmsmr != null) {
                        if (smr.size() >= 10 && kmsmr.size() >= 10) {
                            for (String date : smr.keySet()) {
                                if (kmsmr.get(date) != null) {
                                    Double value = Double.valueOf(smr.get(date).get(smrIdx).toString());
                                    Double kmvalue = Double.valueOf(kmsmr.get(date).get(kmsmrIdx).toString());
                                    dg.add(Math.abs(value - kmvalue));
                                    n++;
                                }
                            }
                        }
                    }

                    Double sd = -1d;
                    if (n > 1) {
                        //標準偏差
                        Double avg = dg.stream().mapToDouble(d -> d).average().getAsDouble();
                        Double v = dg.stream().mapToDouble(d -> Math.pow(d - avg, 2)).sum() / (n - 1);
                        sd = Math.sqrt(v);
                    }
                    System.out.println(sd);

                    line = line + "," + sd;

                    csv.println(line);
                } catch (Exception ex) {
                }
            }
        }
    }

    private static void errSBN(Map<String, SyaryoObject> syaryoMap, String data) {
        try (PrintWriter csv = CSVFileReadWrite.writer("errdata_SBN_" + KISY + ".csv")) {
            for (SyaryoObject syaryo : syaryoMap.values()) {
                if (syaryo.get(data) == null) {
                    continue;
                }

                Map<String, List<String>> sbncheck = new HashMap();
                for (String sbn : syaryo.get(data).keySet()) {
                    if (sbn.length() > 6) {
                        continue;
                    }

                    sbncheck.put(sbn, new ArrayList<>());
                }

                for (String sbn : syaryo.get(data).keySet()) {
                    if (sbncheck.get(sbn) != null) {
                        continue;
                    }

                    Optional<String> syuseisbn = sbncheck.keySet().stream().filter(s -> sbn.contains(s)).findFirst();
                    if (syuseisbn.isPresent()) {
                        sbncheck.get(syuseisbn.get()).add(sbn);
                    }
                }

                for (String sbn : sbncheck.keySet()) {
                    csv.println(syaryo.name + "," + syaryo.get(data).get(sbn).get(0) + "," + syaryo.get(data).get(sbn).get(3) + "," + sbn + "," + String.join(" ", sbncheck.get(sbn)));
                }
            }
        }
    }

    private static void errSGCD(Map<String, SyaryoObject> syaryoMap, String data) {
        try (PrintWriter csv = CSVFileReadWrite.writer("errdata_SGCD_" + KISY + ".csv")) {
            for (SyaryoObject syaryo : syaryoMap.values()) {
                syaryo.startHighPerformaceAccess();

                if (syaryo.get(data) == null) {
                    continue;
                }

                Map<String, List<String>> sbncheck = new HashMap();
                for (String sbn : syaryo.get(data).keySet()) {
                    if (syaryo.get(data).get(sbn).get(3).toString().length() > 4) {
                        continue;
                    }

                    sbncheck.put(sbn, new ArrayList<>());
                }

                for (String sbn : sbncheck.keySet()) {
                    csv.println(syaryo.name + "," + syaryo.get("受注").get(sbn.split("#")[0]).get(0) + "," + sbn.split("#")[0] + "," + syaryo.get(data).get(sbn).get(3));
                }

                syaryo.stopHighPerformaceAccess();
            }
        }
    }

    private static void errFUEL(Map<String, SyaryoObject> syaryoMap, List fuelList) {
        try (PrintWriter csv = CSVFileReadWrite.writer("errdata_FUEL_" + KISY + ".csv")) {
            SyaryoObject header = syaryoMap.get("_headers");

            for (SyaryoObject syaryo : syaryoMap.values()) {
                syaryo.startHighPerformaceAccess();

                if (syaryo.get("KOMTRAX_FUEL_CONSUME") == null) {
                    continue;
                }

                syaryo.stopHighPerformaceAccess();
            }
        }
    }
}
