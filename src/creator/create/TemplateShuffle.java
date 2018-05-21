/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import creator.template.SyaryoTemplate;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.JsonToSyaryoTemplate;
import json.MapIndexToJSON;
import json.SyaryoTemplateToJson;
import json.SyaryoToZip0;
import json.SyaryoToZip2;
import json.SyaryoToZip3;
import obj.SyaryoObject4;

/**
 *
 * @author ZZ17390
 */
public class TemplateShuffle {

    private static String KISY = "PC138US";
    private static String INDEXPATH = "index\\shuffle_format.json";
    private static String SINDEXPATH = "template\\" + KISY + "\\syaryo_" + KISY + "_index.json";
    private static String FILEPATH = "middle\\" + KISY + "\\obj\\";
    private static String OUTPATH = "middle\\";

    public static void main(String[] args) {
        //shuffle(KISY);
        create(KISY);
    }

    private static Map index() {
        return new MapIndexToJSON().reader(INDEXPATH);
    }

    private static Map check(String d) {
        Map cmap = new HashMap();
        //System.out.println(d);

        if (d.contains("=")) {
            cmap.put("condition", "=");
            cmap.put("c1", d.split("=")[0]);
            cmap.put("c2", d.split("=")[1]);
        } else if (d.contains("<>")) {
            cmap.put("condition", "<>");
            cmap.put("c1", d.split("<>")[0]);
            cmap.put("c2", d.split("<>")[1]);
        } else if (d.contains("<")) {
            cmap.put("condition", "<");
            cmap.put("c1", d.split("<")[0]);
            cmap.put("c2", d.split("<")[1]);
        } else if (d.contains(">")) {
            cmap.put("condition", ">");
            cmap.put("c1", d.split(">")[0]);
            cmap.put("c2", d.split(">")[1]);
        } else if (d.contains("|")) {
            cmap.put("arith", "|");
            cmap.put("v1", d.split("\\|")[0]);
            cmap.put("v2", d.split("\\|")[1]);
        } else if (d.contains("+")) {
            cmap.put("arith", "+");
            cmap.put("v1", d.split("\\+")[0]);
            cmap.put("v2", d.split("\\+")[1]);
        } else if (d.contains("-")) {
            cmap.put("arith", "-");
            cmap.put("v1", d.split("-")[0]);
            cmap.put("v2", d.split("-")[1]);
        } else if (d.contains("/")) {
            cmap.put("arith", "/");
            cmap.put("v1", d.split("/")[0]);
            cmap.put("v2", d.split("/")[1]);
        } else {
            cmap.put("data", d);
        }

        if (cmap.get("c2") != null) {
            String s = cmap.get("c2").toString();
            if (s.contains("?")) {
                cmap.put("c2", s.split("\\?")[0]);
                cmap.put("data", s.split("\\?")[1]);
            }
        }
        if (cmap.get("arith") != null) {
            cmap.put("data", "arith");
        }

        System.out.println(cmap);

        return cmap;
    }

    private static void create(String kisy) {
        Map<String, Map<String, List>> map = index();
        SyaryoToZip3 zip = new SyaryoToZip3();
        List<String> syaryoIndex = new ArrayList(new SyaryoTemplateToJson().reader(SINDEXPATH).keySet());

        for (String key : map.keySet()) {
            Map<String, List> dataMap = map.get(key);
            Map<String, SyaryoObject4> shuffleMap = new HashMap();

            for (String subKey : dataMap.keySet()) {
                List<String> getDatas = dataMap.get(subKey);

                //Read SyaryoObject File
                List<String> readList = getDatas.stream()
                    .filter(s -> s.contains("."))
                    .map(s -> s.split("\\.")[0])
                    .distinct()
                    .collect(Collectors.toList());
                Map<String, Map<String, SyaryoObject4>> readFile = new HashMap();
                for (String f : readList) {
                    readFile.put(f, zip.read(FILEPATH + "syaryo_mid_" + kisy + "_" + f + ".bz2"));
                }

                //車両ごとにデータを抽出
                for (String id : syaryoIndex) {
                    //読み込むファイルにIDが存在しなければ飛ばす
                    Boolean flg = false;
                    String table = "";
                    for (String f : readFile.keySet()) {
                        if (readFile.get(f).get(id) == null) {
                            table += f;
                            flg = true;
                        } else {
                            table += ",exist " + f + ",";
                        }
                    }
                    if (flg) {
                        System.out.println("Do not exist ID." + id + " : " + table);
                        continue;
                    }

                    //Read File Extract Syaryo ID
                    Map readMap = idExtractList(id, readFile);
                    System.out.println(id + ":" + readMap);

                    //Shuffle車両の登録
                    SyaryoObject4 shuffleSyaryo = shuffleMap.get(id);
                    if (shuffleSyaryo == null) {
                        shuffleSyaryo = new SyaryoObject4(id);
                    }

                    Map updateMap = shuffleSyaryo.get(key);
                    if (updateMap == null) {
                        updateMap = new HashMap();
                    }

                    format(getDatas, subKey, readMap, updateMap);
                    System.out.println(updateMap);
                    System.exit(0);
                }
            }
        }
    }

    //テンプレートファイルからターゲット車両のリストデータを抽出する
    private static Map<String, List<List<String>>> idExtractList(String target, Map<String, Map<String, SyaryoObject4>> readFile) {
        Map extract = new HashMap();
        for (String table : readFile.keySet()) {
            List<List<String>> data = new ArrayList<>();

            SyaryoObject4 targetSyaryo = readFile.get(table).get(target);
            targetSyaryo.decompress();

            for (Object list : targetSyaryo.map.values()) {
                data.add((List<String>) list);
            }

            targetSyaryo.compress(false);
            extract.put(table, data);
        }

        return extract;
    }

    private static Map format(List<String> formatData, String subKey, Map<String, List<List<String>>> readMap, Map<String, List<String>> updateMap) {
        List<Map<String, String>> formatList = new ArrayList<>();

        for (String g : formatData) {
            Map<String, String> getData = check(g);
            formatList.add(getData);
        }

        //SubKey settings
        String subKeyTable = null;
        Integer subKeyIDX = null;
        if (subKey.contains("#")) {
            subKeyTable = subKey.split("\\.")[0];
            subKeyIDX = Integer.valueOf(subKey.split("#")[1]);
        }

        //Data settings
        for (Map<String, String> f : formatList) {
            String key = subKey;
            if (f.get("condition") != null) {
                comp(f, readMap, subKeyTable, subKeyIDX, key, updateMap);
            } else if (f.get("arith") != null) {
                arith(f, readMap, subKeyTable, subKeyIDX, key, updateMap);
            } else if (f.get("data") != null) {
                data(f, readMap, subKeyTable, subKeyIDX, key, updateMap);
            }
        }

        return updateMap;
    }

    private static void data(Map<String, String> f, Map<String, List<List<String>>> readMap, String subKeyTable, Integer subKeyIDX, String key, Map<String, List<String>> updateMap) {
        //Simple exist key="data"
        String table = null;
        Integer idx = null;
        if (f.get("data").contains("#")) {
            table = f.get("data").split("\\.")[0];
            idx = Integer.valueOf(f.get("data").split("#")[1]);
        }
        for (int i = 0; i < readMap.get(table).size(); i++) {
            if (subKeyIDX != null) {
                key = readMap.get(subKeyTable).get(i).get(subKeyIDX);
            }
            if (updateMap.get(key) == null) {
                updateMap.put(key, new ArrayList());
            }

            if (idx != null) {
                updateMap.get(key).add(readMap.get(table).get(i).get(idx));
            } else {
                updateMap.get(key).add(f.get("data"));
            }
        }
    }

    private static void arith(Map<String, String> f, Map<String, List<List<String>>> readMap, String subKeyTable, Integer subKeyIDX, String key, Map<String, List<String>> updateMap) {
        //key="arith"
        String table = null;
        Integer idx = null;
        if (f.get("v1").contains("#")) {
            table = f.get("v1").split("\\.")[0];
            idx = Integer.valueOf(f.get("v1").split("#")[1]);
        }
        for (int i = 0; i < readMap.get(table).size(); i++) {
            if (subKeyIDX != null) {
                key = readMap.get(subKeyTable).get(i).get(subKeyIDX);
            }
            if (updateMap.get(key) == null) {
                updateMap.put(key, new ArrayList());
            }

            if (idx != null) {
                updateMap.get(key).add(calc(f.get("arith"), f.get("v1"), f.get("v2"), readMap.get(table).get(i)).toString());
            }
        }
    }

    private static Object calc(String op, String v1, String v2, List<String> ref) {
        v1 = ref.get(Integer.valueOf(v1.split("#")[1]));
        if (v2.contains("#")) {
            v2 = ref.get(Integer.valueOf(v2.split("#")[1]));
        }

        if (op.equals("+")) {
            return Integer.valueOf(v1) + Integer.valueOf(v2);
        } else if (op.equals("-")) {
            return Integer.valueOf(v1) - Integer.valueOf(v2);
        } else if (op.equals("/")) {
            return Integer.valueOf(v1) - Integer.valueOf(v2);
        } else if (op.equals("|")) {
            return Integer.valueOf(v1) | Integer.valueOf(v2);
        } else {
            System.out.println("Arithmatic Error! " + op);
            return null;
        }
    }

    private static List<String> comp(Map<String, String> f, Map<String, List<List<String>>> readMap, String subKeyTable, Integer subKeyIDX, String key, Map<String, List<String>> updateMap) {
        String table = null;
        Integer idx = null;
        List reject = new ArrayList();
        if (f.get("c1").contains("#")) {
            table = f.get("c1").split("\\.")[0];
            idx = Integer.valueOf(f.get("comp1").split("#")[1]);
        }
        for (int i = 0; i < readMap.get(table).size(); i++) {
            if (subKeyIDX != null) {
                key = readMap.get(subKeyTable).get(i).get(subKeyIDX);
            }
            if (!compare(f.get("condition"), f.get("c1"), f.get("c2"), readMap.get(table).get(i))) {
                //データ除外
                reject.add(key);
            } else {
                if (f.get("data") != null) {
                    if (updateMap.get(key) == null) {
                        updateMap.put(key, new ArrayList());
                    }
                    
                    updateMap.get(key).add(readMap.get(table).get(i).get(idx));
                }
            }
        }
        return reject;
    }

    private static Boolean compare(String cond, String c1, String c2, List<String> ref) {
        c1 = ref.get(Integer.valueOf(c1.split("#")[1]));
        if (c2.contains("#")) {
            c2 = ref.get(Integer.valueOf(c2.split("#")[1]));
        }

        c2 = c2.replace("'", "");
        if (cond.equals("=")) {
            return c1.equals(c2);
        } else if (cond.equals("<>")) {
            return !c1.equals(c2);
        } else if (cond.equals("<")) {
            return Integer.valueOf(c1) < Integer.valueOf(c2);
        } else if (cond.equals(">")) {
            return Integer.valueOf(c1) > Integer.valueOf(c2);
        } else {
            return null;
        }
    }

    //旧バージョン
    public static void shuffle(String kisy) {
        String path = "..\\KomatsuData\\車両テンプレート\\" + kisy + "\\gz\\";
        String outpath = "..\\KomatsuData\\中間データ\\" + kisy + "\\";
        //String path = "template\\"+kisy+"\\gz\\";
        //String outpath = "middle\\"+kisy+"\\";

        String FILENAME = outpath + "syaryo_mid_" + kisy + "_";
        File[] flist = (new File(path)).listFiles();

        //フォルダ作成実行
        File fd = new File(outpath);
        if (!fd.exists()) {
            fd.mkdir();
        }

        //ベースファイル
        File basefile = new File(path + "syaryo_" + kisy + "_template.gz");

        for (File f : flist) {
            System.out.println(f.getName());

            if (f.getName().equals(basefile.getName())) {
                continue;
            }

            Map<String, SyaryoTemplate> syaryoTemplates = new SyaryoToZip2().readTemplate(f);
            if (syaryoTemplates == null) {
                System.out.println("SyaryoTemplate is NULL!");
                continue;
            }
            if (syaryoTemplates.isEmpty()) {
                System.out.println("SyaryoTemplate is Empty!");
                continue;
            }

            //マップキーの最大数を取得
            String id = null;
            int max = 0;
            for (SyaryoTemplate template : syaryoTemplates.values()) {
                template.decompress();
                if (max < template.map.size()) {
                    max = template.map.size();
                    id = template.getName();
                }
                template.compress(false);
            }

            //出力用のMapを作成
            SyaryoTemplate field = syaryoTemplates.get(id);
            field.decompress();
            Map<Object, Map<String, SyaryoTemplate>> fieldMap = new HashMap();
            for (Object key : field.map.keySet()) {
                File midfile = new File(FILENAME + key + ".gz");
                if (midfile.exists()) {
                    fieldMap.put(key, new SyaryoToZip2().readTemplate(midfile));
                } else {
                    fieldMap.put(key, new SyaryoToZip2().readTemplate(basefile));
                }

                for (SyaryoTemplate template : syaryoTemplates.values()) {
                    int index = 0;
                    Boolean header = true;
                    template.decompress();
                    SyaryoTemplate fieldSyaryo = fieldMap.get(key).get(template.getName());
                    fieldSyaryo.decompress();
                    String errstr = null;
                    try {

                        for (String str : template.get(key.toString()).split("\n")) {
                            if (header) {
                                index = str.split(",").length;
                                header = false;
                                continue;
                            }
                            errstr = str;
                            if (str.replace(" ", "").split(",").length < index) {
                                str += "?";
                            }
                            fieldSyaryo.add(key.toString(), str.trim().split(","));
                        }
                    } catch (Exception e) {
                        System.out.println(key);
                        System.out.println(template.getName());
                        System.out.println(errstr);
                    }

                    fieldSyaryo.compress(true);
                    template.compress(false);
                }
            }

            //Mapを出力
            for (Object key : fieldMap.keySet()) {
                new SyaryoToZip0().write(FILENAME + key, fieldMap.get(key));
            }
        }
    }
}
