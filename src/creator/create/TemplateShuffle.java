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
    }

    private static Map index() {
        return new MapIndexToJSON().reader(INDEXPATH);
    }

    private static Map check(String d) {
        Map cmap = new HashMap();
        System.out.println(d);

        if (d.contains("=")) {
            cmap.put("condition", "=");
            cmap.put("comp1", d.split("=")[0]);
            cmap.put("comp2", d.split("=")[1]);
        } else if (d.contains("<>")) {
            cmap.put("condition", "<>");
            cmap.put("comp1", d.split("<>")[0]);
            cmap.put("comp2", d.split("<>")[1]);
        } else if (d.contains("<")) {
            cmap.put("condition", "<");
            cmap.put("comp1", d.split("<")[0]);
            cmap.put("comp2", d.split("<")[1]);
        } else if (d.contains(">")) {
            cmap.put("condition", ">");
            cmap.put("comp1", d.split(">")[0]);
            cmap.put("comp2", d.split(">")[1]);
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

        if (cmap.get("comp2") != null) {
            String s = cmap.get("comp2").toString();
            if (s.contains("?")) {
                cmap.put("comp2", s.split("\\?")[0]);
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
                        .map(s -> s.split(".")[0])
                        .distinct()
                        .collect(Collectors.toList());
                Map<String, Map<String, SyaryoObject4>> readFile = new HashMap();
                for (String f : readList) {
                    readFile.put(f, zip.read(FILEPATH + "syaryo_mid_" + kisy + "_" + f + "bz2"));
                }

                //車両ごとにデータを抽出
                for (String id : syaryoIndex) {
                    //読み込むファイルにIDが存在しなければ飛ばす
                    Boolean flg = false;
                    for (String f : readFile.keySet()) {
                        if (readFile.get(f).get(id) == null) {
                            flg = true;
                        }
                    }
                    if (flg) {
                        continue;
                    }

                    //Shuffle車両の登録
                    SyaryoObject4 shuffleSyaryo = shuffleMap.get(id);
                    if (shuffleSyaryo == null) {
                        shuffleSyaryo = new SyaryoObject4(id);
                    }

                }
            }
        }
    }

    private static Map format(List<String> formatList, String subKey, List<String> inList, Map<String, List<String>> updateMap) {
        Boolean flg = false;
        List dataList = new ArrayList();
        for (String g : formatList) {
            Map<String, String> getData = check(g);
            if(getData.get("data") != null){
                String data = getData.get("data");
                if(data.contains("#"))
                    data = inList.get(Integer.valueOf(data.split("#")[1]));
                else if(data.contains("arith"))
                    data = calc(getData.get("arith"), getData.get("v1"), getData.get("v2"), inList).toString();
                dataList.add(data);
            }
        }
        
        //除外時の処理
        if(flg)
            return null;
        
        //Mapを更新
        String key;
        if(subKey.contains("#"))
            key = inList.get(Integer.valueOf(subKey.split("#")[1]));
        else
            key = subKey;
        
        updateMap.put(key, dataList);
        
        return updateMap;
    }
    
    private static Object calc(String op, String v1, String v2, List<String> ref){
        v1 = ref.get(Integer.valueOf(v1.split("#")[1]));
        if(v2.contains("#"))
            v2 = ref.get(Integer.valueOf(v2.split("#")[1]));;
        
        if(op.equals("+"))
            return Integer.valueOf(v1) + Integer.valueOf(v2);
        else if(op.equals("-"))
            return Integer.valueOf(v1) - Integer.valueOf(v2);
        else if(op.equals("/"))
            return Integer.valueOf(v1) - Integer.valueOf(v2);
        else if(op.equals("|"))
            return Integer.valueOf(v1) | Integer.valueOf(v2);
        else{
            System.out.println("Arithmatic Error! "+op);
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
