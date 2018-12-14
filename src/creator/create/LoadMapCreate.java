/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class LoadMapCreate {

    private static String INPATH = "C:\\Users\\zz17390\\PycharmProjects\\KMAnalyzePY\\loadmap\\";
    private static String ROOTPATH = KomatsuDataParameter.MIDDLEDATA_PATH;
    private static String OUTPATH;

    private static String[] keys = new String[]{"SMR", "実エンジン回転VSエンジントルク", "作業機操作状況", "ポンプ圧_MAX", "ポンプ圧_F", "ポンプ圧_R", "作業モード選択状況", "エンジン水温VS作動油温", "ST_USERTIME", "ポンプ斜板_F", "ポンプ斜板_R", "可変マッチング"};

    public static void main(String[] args) {
        create("PC200");
    }

    public static void create(String kisy) {
        OUTPATH = ROOTPATH + kisy + "\\shuffle\\";
        Map<String, SyaryoObject4> syaryoMap = new HashMap();

        //Load File
        File[] flist = (new File(INPATH)).listFiles();
        for (File f : flist) {
            if (!f.getName().contains("_output_hour.csv")) {
                continue;
            }

            String name = f.getName().split("_")[0];
            SyaryoObject4 syaryo = new SyaryoObject4(name);
            Map<String, Map<String, List<String>>> temp = new HashMap();

            System.out.println(f);
            try (BufferedReader csv = CSVFileReadWrite.readerSJIS(f.getAbsolutePath())) {
                String line = "";

                int idx = 0;
                String key = "LOADMAP_";
                while ((line = csv.readLine()) != null) {
                    System.out.println(line);
                    if (line.contains("smr")) {
                        temp.put(key + keys[idx], new LinkedHashMap());
                        String[] sarr = line.replace(" ", "").split(",");
                        temp.get(key + keys[idx]).put("SMR", new ArrayList<>(Arrays.asList(sarr).subList(1, sarr.length)));
                        idx++;
                    } else if (line.contains("実エンジン回転VSエンジントルク")) {
                        temp.put(key + keys[idx], new LinkedHashMap());
                        String[] head = csv.readLine().replace(" ", "").split(",");
                        for (int i = 0; i < 12; i++) {
                            line = csv.readLine();
                            String[] sarr = line.replace(" ", "").split(",");
                            for (int j = 1; j < sarr.length; j++) {
                                temp.get(key + keys[idx]).put(sarr[0] + "_" + head[j], Arrays.asList(new String[]{sarr[j]}));
                            }
                        }
                        idx++;
                    } else if (line.contains("作業機操作状況")) {
                        temp.put(key + keys[idx], new LinkedHashMap());
                        String[] head = csv.readLine().replace(" ", "").split(",");
                        line = csv.readLine();
                        String[] sarr = line.replace(" ", "").split(",");
                        for (int j = 1; j < sarr.length; j++) {
                            temp.get(key + keys[idx]).put(head[j], Arrays.asList(new String[]{sarr[j]}));
                        }
                        idx++;
                    } else if (line.contains("ポンプ圧(MAX)") || line.contains("ポンプ圧(F)") || line.contains("ポンプ圧(R)")) {
                        temp.put(key + keys[idx], new LinkedHashMap());
                        String[] head = csv.readLine().replace(" ", "").split(",");
                        line = csv.readLine();
                        String[] sarr = line.replace(" ", "").split(",");
                        for (int j = 1; j < sarr.length; j++) {
                            temp.get(key + keys[idx]).put(head[j], Arrays.asList(new String[]{sarr[j]}));
                        }
                        idx++;
                    } else if (line.contains("作業モード選択状況")) {
                        temp.put(key + keys[idx], new LinkedHashMap());
                        String[] head = csv.readLine().replace(" ", "").split(",");
                        line = csv.readLine();
                        String[] sarr = line.replace(" ", "").split(",");
                        for (int j = 1; j < sarr.length; j++) {
                            temp.get(key + keys[idx]).put(head[j], Arrays.asList(new String[]{sarr[j]}));
                        }
                        idx++;
                    } else if (line.contains("エンジン水温VS作動油温")) {
                        temp.put(key + keys[idx], new LinkedHashMap());
                        String[] head = csv.readLine().replace(" ", "").split(",");
                        for (int i = 0; i < 8; i++) {
                            line = csv.readLine();
                            String[] sarr = line.replace(" ", "").split(",");
                            for (int j = 1; j < sarr.length; j++) {
                                temp.get(key + keys[idx]).put(sarr[0] + "_" + head[j], Arrays.asList(new String[]{sarr[j]}));
                            }
                        }
                        idx++;
                    } else if (line.contains("starterUseTimes")) {
                        temp.put(key + keys[idx], new LinkedHashMap());
                        csv.readLine();
                        line = csv.readLine();
                        String[] sarr = line.replace(" ", "").split(",");
                        temp.get(key + keys[idx]).put("UTIMES", new ArrayList<>(Arrays.asList(sarr).subList(1, sarr.length)));
                        idx++;
                    } else if (line.contains("ポンプ斜板(F)") || line.contains("ポンプ斜板(R)")) {
                        temp.put(key + keys[idx], new LinkedHashMap());
                        String[] head = csv.readLine().replace(" ", "").split(",");
                        for (int i = 0; i < 4; i++) {
                            line = csv.readLine();
                            String[] sarr = line.replace(" ", "").split(",");
                            for (int j = 1; j < sarr.length; j++) {
                                temp.get(key + keys[idx]).put(sarr[0] + "_" + head[j], Arrays.asList(new String[]{sarr[j]}));
                            }
                        }
                        idx++;
                    } else if (line.contains("可変マッチング")) {
                        temp.put(key + keys[idx], new LinkedHashMap());
                        String[] head = csv.readLine().replace(" ", "").split(",");
                        line = csv.readLine();
                        String[] sarr = line.replace(" ", "").split(",");
                        for (int j = 1; j < sarr.length; j++) {
                            temp.get(key + keys[idx]).put(head[j], Arrays.asList(new String[]{sarr[j]}));
                        }
                        idx++;
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            syaryo.putAll(temp);
            syaryoMap.put(syaryo.name, syaryo);

        }

        //Test Print
        syaryoMap.values().forEach(s -> {
            System.out.println(s.name);
            s.getMap().entrySet().stream().forEach(lm -> {
                System.out.println(" -" + lm.getKey());
                lm.getValue().entrySet().stream().map(map -> "  |-" + map.getKey() + ":" + map.getValue()).forEach(System.out::println);
            });
        });

        System.out.println(syaryoMap.size());
        
        //visualデータ生成
        SyaryoObject4 syaryo = syaryoMap.get("Pc200-10-450792");
        syaryo.get("LOADMAP_実エンジン回転VSエンジントルク").entrySet().stream()
                                .map(eng -> eng.getKey().replace("_", ",")+","+eng.getValue().get(0))
                                .forEach(System.out::println);
    }
}