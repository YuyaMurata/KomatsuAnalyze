/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class AttachedInfo {

    private static String csvFile = "PC200_事故車両推定_金額_不明.csv";
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();

    public static void main(String[] args) {
        //workname(csvFile);
        LOADER.setFile("PC200_sv_form");
        //serviceinfo(csvFile);
        workinfo(csvFile);
    }

    private static void workname(String file) {
        Map devName = KomatsuDataParameter.WORK_DEVID_DEFNAME;
        try (PrintWriter csv = CSVFileReadWrite.writer(csvFile + "_work_id_name.csv")) {
            try (BufferedReader br = CSVFileReadWrite.reader(csvFile)) {
                String line = br.readLine();
                csv.println(line + ",devID,devName");
                while ((line = br.readLine()) != null) {
                    if (line.split(",")[3].length() > 3) {
                        String devID = line.split(",")[3].substring(0, 4);
                        line = line + "," + devID + "," + devName.get(devID);
                    } else {
                        line = line + ",None,None";
                    }
                    csv.println(line);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void serviceinfo(String file) {
        Map<String, SyaryoObject> map = LOADER.getSyaryoMap();
        try (PrintWriter csv = CSVFileReadWrite.writer(csvFile + "_attached_info.csv")) {
            try (BufferedReader br = CSVFileReadWrite.reader(csvFile)) {
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

    private static void workinfo(String file) {
        Map<String, SyaryoObject> map = LOADER.getSyaryoMap();
        try (PrintWriter csv = CSVFileReadWrite.writer(csvFile + "_attached_info.csv")) {
            try (BufferedReader br = CSVFileReadWrite.reader(csvFile)) {
                String line = br.readLine();
                List<String> l = Arrays.asList(line.split(","));
                //l.set(12, "作業明細");
                csv.println(String.join(",", l));

                while ((line = br.readLine()) != null) {
                    l = Arrays.asList(line.split(","));
                    String name = l.get(0);
                    String sbn = l.get(2);

                    try (SyaryoAnalizer s = new SyaryoAnalizer(map.get(name))) {
                        csv.println(String.join(",", l));
                        
                        Map<String, List<String>> work = s.getSBNWork(sbn);
                        if(work != null)
                            work.entrySet().stream().forEach(w ->{
                                csv.println(",作業明細,"+w.getKey()+","+String.join(",", w.getValue()));
                            });
                        else
                            csv.println(",作業明細,なし");
                        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
