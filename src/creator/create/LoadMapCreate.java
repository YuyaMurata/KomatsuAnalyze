/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import creator.template.SimpleTemplate;
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
import file.SyaryoTemplateToJSON;
import file.SyaryoToCompress;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class LoadMapCreate {

    private static String INPATH = "C:\\Users\\zz17390\\PycharmProjects\\KMAnalyzePY\\loadmap\\";
    private static String INDEX_PATH = KomatsuDataParameter.TEMPLATE_PATH;
    private static String OUTPATH = KomatsuDataParameter.MIDDLEDATA_PATH;

    private static String[] keys = new String[]{"SMR", "実エンジン回転VSエンジントルク", "作業機操作状況", "ポンプ圧_MAX", "ポンプ圧_F", "ポンプ圧_R", "作業モード選択状況", "エンジン水温VS作動油温", "ST_USERTIME", "ポンプ斜板_F", "ポンプ斜板_R", "可変マッチング"};

    public static void main(String[] args) {
        //add data
        Arrays.asList(keys).stream().map(s -> "\"LOADMAP_"+s+"\",").forEach(System.out::println);

        create("PC200");
    }

    public static void create(String kisy) {
        OUTPATH = OUTPATH + kisy + "\\shuffle\\";

        //File check
        String FILENAME = "syaryo_mid_" + kisy + "_";
        String filename = OUTPATH + FILENAME + "LOADMAP";
        if ((new File(filename + ".bz2")).exists()) {
            System.out.println("exists file " + filename);
            return ;
        }
        
        //車両Indexの作成 SimpleTemplateにcheck機能が付加される
        File file = new File(INDEX_PATH + kisy +"\\syaryo_" + kisy + "_index.json");
        if (file.exists()) {
            new SyaryoTemplateToJSON().ToTemplate(file.getAbsolutePath());
        }

        Map<String, SyaryoObject> syaryoMap = new HashMap();

        //Load File
        File[] flist = (new File(INPATH)).listFiles();
        for (File f : flist) {
            if (!f.getName().contains("_output_hour.csv")) {
                continue;
            }

            String name = f.getName().split("_")[0];
            if(!kisy.equals(name.split("-")[0]))
                continue;
            
            if(SimpleTemplate.check(name.split("-")[0], name.split("-")[2]) == null){
                System.out.println("reject : "+name);
                continue;
            }
            
            SyaryoObject syaryo = new SyaryoObject(name);
            Map<String, Map<String, List<String>>> temp = new HashMap();

            //System.out.println(f);
            try (BufferedReader csv = CSVFileReadWrite.readerSJIS(f.getAbsolutePath())) {
                String line = "";

                int idx = 0;
                String key = "LOADMAP_";
                while ((line = csv.readLine()) != null) {
                    //System.out.println(line);
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
                                temp.get(key + keys[idx]).put(fkey(sarr[0],head[j]), Arrays.asList(new String[]{sarr[j]}));
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
                            temp.get(key + keys[idx]).put(label(head[j]), Arrays.asList(new String[]{sarr[j]}));
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
                                temp.get(key + keys[idx]).put(fkey(sarr[0],head[j]), Arrays.asList(new String[]{sarr[j]}));
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
                                temp.get(key + keys[idx]).put(fkey(sarr[0],head[j]), Arrays.asList(new String[]{sarr[j]}));
                            }
                        }
                        idx++;
                    } else if (line.contains("可変マッチング")) {
                        temp.put(key + keys[idx], new LinkedHashMap());
                        String[] head = csv.readLine().replace(" ", "").split(",");
                        line = csv.readLine();
                        String[] sarr = line.replace(" ", "").split(",");
                        for (int j = 1; j < sarr.length; j++) {
                            temp.get(key + keys[idx]).put(label(head[j]), Arrays.asList(new String[]{sarr[j]}));
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
        /*syaryoMap.values().forEach(s -> {
            System.out.println(s.name);
            s.getMap().entrySet().stream().forEach(lm -> {
                System.out.println(" -" + lm.getKey());
                lm.getValue().entrySet().stream().map(map -> "  |-" + map.getKey() + ":" + map.getValue()).forEach(System.out::println);
            });
        });*/
        
        //visualデータ生成
        //SyaryoObject4 syaryo = syaryoMap.get("PC200-10-451146");
        //syaryo.get("LOADMAP_実エンジン回転VSエンジントルク").entrySet().stream()
        //                        .map(eng -> eng.getKey().replace("_", ",")+","+eng.getValue().get(0))
        //                        .forEach(System.out::println);
        new SyaryoToCompress().write(filename, syaryoMap);
        System.out.println(syaryoMap.size());
    }
    
    private static String fkey(String x, String y){
        String lx = label(x);
        String ly = label(y);
        
        return lx+"_"+ly;
    }
    
    private static String label(String x){
        String lx = "";
        
        if(x.startsWith("〜"))
            lx = x.replace("〜", "");
        else
            if(x.contains(".")){
                lx = String.valueOf(Double.valueOf(x.replace("〜", ""))+0.1);
            }else
                lx = String.valueOf(Integer.valueOf(x.replace("〜", ""))+1);
        
        return lx;
    }
}
