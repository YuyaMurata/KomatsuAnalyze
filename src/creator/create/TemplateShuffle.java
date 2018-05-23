/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import creator.template.SyaryoTemplate;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
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
    private static String INDEXPATH = KomatsuDataParameter.SHUFFLE_FORMAT_PATH;
    private static String[] kisyList = KomatsuDataParameter.KISY_LIST;
    private static String ROOTPATH = KomatsuDataParameter.MIDDLEDATA_PATH;
    private static String ROOTPATH_SIDX = KomatsuDataParameter.TEMPLATE_PATH;

    public static void main(String[] args) {
        //shuffle(KISY);
        for(String kisy : kisyList)
            create(kisy);
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

        //System.out.println(cmap);
        return cmap;
    }

    private static void create(String kisy) {
        String SINDEXPATH = ROOTPATH_SIDX + kisy + "\\syaryo_" + kisy + "_index.json";
        String FILEPATH = ROOTPATH + kisy + "\\obj\\";
        String OUTPATH = ROOTPATH + kisy + "\\shuffle\\";
        String FILENAME = "syaryo_mid_" + kisy + "_";
        
        //機種を取得しているかチェック
        if (!(new File(SINDEXPATH)).exists()) {
            System.out.println("Do not get kisy=" + kisy + "!");
            return;
        }
        
        //Create Folder
        if (!(new File(OUTPATH)).exists()) {
            (new File(OUTPATH)).mkdir();
        }

        Map<String, Map<String, List>> map = index();
        SyaryoToZip3 zip = new SyaryoToZip3();
        List<String> syaryoIndex = new ArrayList(new SyaryoTemplateToJson().reader(SINDEXPATH).keySet());

        //抽出する分類名を設定　e.g. 顧客
        for (String key : map.keySet()) {
            //File check
            String filename = OUTPATH + FILENAME + key;
            if ((new File(filename + ".bz2")).exists()) {
                System.out.println("exists file " + FILENAME + key);
                continue;
            }

            System.out.println("Shuffle = " + key);
            Map<String, List> dataFormatMap = map.get(key);
            Map<String, SyaryoObject4> shuffleMap = new TreeMap();

            List notRegisterList = new ArrayList();

            //抽出するFormatIndexGroupを設定 e.g. as_keiyaku テーブル
            for (String subKey : dataFormatMap.keySet()) {
                List<String> getDataFormat = dataFormatMap.get(subKey);
                if(getDataFormat.contains(null)){
                    System.err.println("Error Shuffle Format contained NULL!");
                    System.exit(0);
                }
                    
                
                //抽出に必要なテーブルリストを取得
                List<String> readList = getDataFormat.stream()
                    .filter(s -> s.contains("."))
                    .map(s -> s.split("\\.")[0])
                    .distinct()
                    .collect(Collectors.toList());
                //上記からファイルを読み込む
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
                        notRegisterList.add(id + " : " + table);
                        continue;
                    }

                    //ファイルから特定IDのデータ郡を抽出
                    // readMap = {table : [[TargetID Data1], [TargetID Data2] ...]}
                    Map<String, List<List>> readMap = idExtractList(id, readFile);
                    //System.out.println(id + ":" + readMap);

                    //Shuffle車両の登録
                    SyaryoObject4 shuffleSyaryo = shuffleMap.get(id);
                    if (shuffleSyaryo == null) {
                        shuffleSyaryo = new SyaryoObject4(id);
                    }
                    shuffleSyaryo.decompress();

                    //Shuffle車両が分類名を持たないときの処理
                    if (shuffleSyaryo.get(key) == null) {
                        shuffleSyaryo.map.put(key, new TreeMap());
                    }

                    //データリストへの整形処理
                    for (int i = 0; i < readMap.get(readMap.keySet().stream().findFirst().get()).size(); i++) {
                        //Subkeyの設定
                        String recID = subKey;
                        if (subKey.contains("#")) {
                            String recIDTable = subKey.split("\\.")[0];
                            Integer recIDIDX = Integer.valueOf(subKey.split("#")[1]);
                            recID = readMap.get(recIDTable).get(i).get(recIDIDX).toString();
                        }

                        //車両情報の整形
                        List update = format(getDataFormat, i, readMap);

                        //車両情報を更新
                        if (update != null) {
                            //System.out.println(recID);
                            if (shuffleSyaryo.get(key).get(recID) != null) {
                                recID = dup(recID, shuffleSyaryo.get(key));
                            }
                            shuffleSyaryo.get(key).put(recID, update);
                        } else {
                            String recIDTable = subKey.split("\\.")[0];
                            System.out.println("ID=" + shuffleSyaryo.getName() + " 除外データ("+recIDTable+"):" + readMap.get(recIDTable).get(i));
                        }
                    }

                    //オブジェクトファイルへ車両を登録
                    System.out.println(shuffleSyaryo.dump());
                    if (!shuffleSyaryo.get(key).isEmpty()) {
                        shuffleMap.put(shuffleSyaryo.getName(), shuffleSyaryo);
                    } else {
                        notRegisterList.add(id + " : " + table + "(empty data)");
                    }
                    shuffleSyaryo.compress(true);
                }
            }

            //Check
            SyaryoObject4 syaryo = shuffleMap.values().stream().findFirst().get();
            syaryo.decompress();
            System.out.println(syaryo.dump());
            syaryo.compress(true);
            System.out.println("Not Registerd = " + notRegisterList);

            //Output
            zip.write(filename, shuffleMap);

            //System.exit(0);
        }
    }

    //テンプレートファイルからターゲット車両のリストデータを抽出する
    private static Map<String, List<List>> idExtractList(String target, Map<String, Map<String, SyaryoObject4>> readFile) {
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

    private static List format(List<String> formatData, int idx, Map<String, List<List>> readMap) {
        List<Map<String, String>> formatList = new ArrayList<>();

        for (String g : formatData) {
            Map<String, String> getData = check(g);
            formatList.add(getData);
        }

        //データ列を進める処理
        Boolean flg = false;
        List dataList = new ArrayList();
        for (Map<String, String> f : formatList) {
            if (f.get("condition") != null) {
                flg = comp(f, idx, readMap, dataList);
            } else if (f.get("arith") != null) {
                flg = arith(f, idx, readMap, dataList);
            } else if (f.get("data") != null) {
                flg = data(f, idx, readMap, dataList);
            }

            if (flg) {
                return null;
            }
        }

        return dataList;
    }

    private static DecimalFormat df = new DecimalFormat("0000");

    private static String dup(String key, Map map) {
        int cnt = 0;
        String k = key;
        while (map.get(k) != null) {
            k = key + "#" + df.format(++cnt);
        }
        return k;
    }

    //Adding Simple Formtat : key="data"
    private static Boolean data(Map<String, String> f, int row, Map<String, List<List>> readMap, List dataList) {
        //データ抽出
        String table = null;
        Integer col = null;
        if (f.get("data").contains("#")) {
            table = f.get("data").split("\\.")[0];
            col = Integer.valueOf(f.get("data").split("#")[1]);
        }

        //抽出データを車両情報に追加
        if (col != null) {
            //System.out.println(f);
            //System.out.println(readMap.get(table).get(row));
            dataList.add(readMap.get(table).get(row).get(col));
        } else {
            dataList.add(f.get("data"));
        }

        return false;
    }

    //Adding Arithmatic Data : key="arith"
    private static Boolean arith(Map<String, String> f, int row, Map<String, List<List>> readMap, List dataList) {
        //key="arith"
        String table = null;
        if (f.get("v1").contains("#")) {
            table = f.get("v1").split("\\.")[0];
        }

        //計算結果を車両情報に追加
        dataList.add(calc(f.get("arith"), f.get("v1"), f.get("v2"), readMap.get(table).get(row)).toString());

        return false;
    }

    private static Boolean comp(Map<String, String> f, int row, Map<String, List<List>> readMap, List dataList) {
        String table = null;
        Integer col = null;
        if (f.get("c1").contains("#")) {
            table = f.get("c1").split("\\.")[0];
            col = Integer.valueOf(f.get("c1").split("#")[1]);
        }

        if (!compare(f.get("condition"), f.get("c1"), f.get("c2"), readMap.get(table).get(row))) {
            //データ除外
            if (f.get("data") == null) {
                return true;
            }
        } else {
            if (f.get("data") != null) {
                table = f.get("data").split("\\.")[0];
                col = Integer.valueOf(f.get("data").split("#")[1]);
            }
        }
        if (f.get("data") != null) {
            dataList.add(readMap.get(table).get(row).get(col));
        }

        return false;
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
            if(v1.equals("Y") || v2.equals("Y"))
                return 1;
            else
                return 0;
        } else {
            System.out.println("Arithmatic Error! " + op);
            return null;
        }
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
