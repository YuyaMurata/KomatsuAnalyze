/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import creator.map.ReadShuffleData;
import param.KomatsuDataParameter;
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
import json.SyaryoToZip3;
import obj.SyaryoObject4;

/**
 * 車両オブジェクトのシャッフリング
 * シャッフルの定義ファイル(shuffle_format.json)に従ってデータを並び替える
 * @author ZZ17390
 */
public class TemplateShuffle {

    private static String INDEXPATH = KomatsuDataParameter.SHUFFLE_FORMAT_PATH;
    private static String[] kisyList = KomatsuDataParameter.KISY_LIST;
    private static String ROOTPATH = KomatsuDataParameter.MIDDLEDATA_PATH;
    private static String ROOTPATH_SIDX = KomatsuDataParameter.TEMPLATE_PATH;

    public static void main(String[] args) {
        String kisy = "PC138US";
        create(kisy);
    }

    public static void create(String kisy) {
        String SINDEXPATH = ROOTPATH_SIDX + kisy + "\\syaryo_" + kisy + "_index.json";
        String FILEPATH = ROOTPATH + kisy + "\\obj\\";
        String OUTPATH = ROOTPATH + kisy + "\\shuffle\\";
        String FILENAME = "syaryo_mid_" + kisy + "_";

        //機種をオブジェクト化しているか確認
        if (!(new File(FILEPATH)).exists()) {
            System.out.println("Do not find object! kisy=" + kisy + "!");
            return;
        }

        //Create Folder
        if (!(new File(OUTPATH)).exists()) {
            (new File(OUTPATH)).mkdir();
        }

        Map<String, Map<String, List>> map = index();
        SyaryoToZip3 zip = new SyaryoToZip3();
        List<String> syaryoIndex = new ArrayList(new SyaryoTemplateToJson().reader(SINDEXPATH).keySet());
        
        ReadShuffleData shuffle = new ReadShuffleData(FILEPATH, kisy);
        
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
                if (getDataFormat.contains(null)) {
                    System.err.println("Error shuffle format contained NULL!");
                    System.exit(0);
                }
                
                //Set DataList subKeyはセットしない
                shuffle.setDataList(key, getDataFormat);
                
                //例外処理 必要ファイルを読み込めなかったら処理を実行しない
                if (shuffle.check()){
                    continue;
                }
                
                //車両ごとにデータを抽出
                for (String id : syaryoIndex) {
                    if(shuffle.setData(id))
                        continue;

                    //Shuffle車両の登録
                    SyaryoObject4 shuffleSyaryo = shuffleMap.get(id);
                    if (shuffleSyaryo == null) {
                        shuffleSyaryo = new SyaryoObject4(id);
                    }
                    
                    //高速アクセス スタート
                    shuffleSyaryo.startHighPerformaceAccess();

                    //Shuffle車両が分類名を持たないときの処理
                    if (shuffleSyaryo.get(key) == null) {
                        shuffleSyaryo.put(key, new TreeMap());
                    }

                    //データリストへの整形処理
                    for (int i = 0; i < shuffle.size(); i++) {
                        //Subkeyの設定
                        String recID = subKey;
                        if (subKey.contains("#")) {
                            String recIDTable = subKey.split("\\.")[0];
                            Integer recIDIDX = Integer.valueOf(subKey.split("#")[1]);
                            recID = shuffle.getSubKey(recIDTable, i, recIDIDX);
                        }

                        //車両情報の整形
                        List update = format(getDataFormat, i, shuffle);
                        
                        //車両情報を更新
                        if (update != null) {
                            //System.out.println(key+":"+recID+":"+shuffleSyaryo.getMap());
                            if (shuffleSyaryo.get(key).get(recID) != null) {
                                recID = dup(recID, shuffleSyaryo.get(key));
                            }
                            shuffleSyaryo.get(key).put(recID, update);
                        } else {
                            if (subKey.contains("#")) {
                                String recIDTable = subKey.split("\\.")[0];
                                System.out.println("ID=" + shuffleSyaryo.getName() + " 除外データ(" + recIDTable + "):" + shuffle.getRow(recIDTable,i));
                            } else {
                                System.out.println("ID=" + shuffleSyaryo.getName() + " 除外データ(" + key + "):" + getDataFormat);
                            }
                        }
                    }

                    //オブジェクトファイルへ車両を登録
                    //System.out.println(shuffleSyaryo.dump());
                    //高速アクセス ストップ (登録データの保存)
                    shuffleSyaryo.stopHighPerformaceAccess();
                    
                    if (!shuffleSyaryo.get(key).isEmpty()) {
                        shuffleMap.put(shuffleSyaryo.getName(), shuffleSyaryo);
                    } else {
                        notRegisterList.add(id + " : " + subKey.split("\\.")[0] + "(empty data)");
                    }
                }
            }

            if (!shuffleMap.isEmpty()) {
                //Check
                SyaryoObject4 syaryo = shuffleMap.values().stream().findFirst().get();
                System.out.println(syaryo.dump());
                System.out.println("Not Registerd = " + notRegisterList);
                
                //Output
                zip.write(filename, shuffleMap);
            }else
                System.out.println(key+"情報が存在しません.");
            
            
            //System.exit(0);
        }
    }

    private static Map index() {
        return new MapIndexToJSON().reader(INDEXPATH);
    }

    private static List format(List<String> formatData, int idx, ReadShuffleData shuffle) {
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
                flg = comp(f, idx, shuffle, dataList);
            } else if (f.get("arith") != null) {
                flg = arith(f, idx, shuffle, dataList);
            } else if (f.get("data") != null) {
                flg = data(f, idx, shuffle, dataList);
            }

            if (flg) {
                return null;
            }
        }
        
        //空情報のみ格納されていたときの処理
        if(!dataList.stream().filter(d -> !d.equals("")).findFirst().isPresent())
            return null;
        
        return dataList;
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
                cmap.put("data", s.split("\\?")[1].split(":")[0]);
                if(s.contains(":"))
                    cmap.put("dataelse", s.split("\\?")[1].split(":")[1]);
            }
        }
        if (cmap.get("arith") != null) {
            cmap.put("data", "arith");
        }

        //System.out.println(cmap);
        return cmap;
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
    private static Boolean data(Map<String, String> f, int row, ReadShuffleData shuffle, List dataList) {
        //データ抽出
        String table = null;
        String elm = null;
        Integer col = null;
        if (f.get("data").contains("#")) {
            table = f.get("data").split("\\.")[0];
            elm = f.get("data").split("\\.")[1].split("#")[0];
            col = Integer.valueOf(f.get("data").split("#")[1]);
        }

        //抽出データを車両情報に追加
        if (col != null) {
            //System.out.println(f);
            //System.out.println(readMap.get(table).get(row));
            dataList.add(shuffle.getData(table, row, elm, col));
        } else {
            dataList.add(f.get("data"));
        }

        return false;
    }

    //Adding Arithmatic Data : key="arith"
    private static Boolean arith(Map<String, String> f, int row, ReadShuffleData shuffle, List dataList) {
        //key="arith"
        String table = null;
        if (f.get("v1").contains("#")) {
            table = f.get("v1").split("\\.")[0];
        }

        //計算結果を車両情報に追加
        dataList.add(calc(f.get("arith"), f.get("v1"), f.get("v2"), shuffle.getRow(table, row)).toString());

        return false;
    }

    private static Boolean comp(Map<String, String> f, int row, ReadShuffleData shuffle, List dataList) {
        String table = null;
        String elm = null;
        Integer col = null;
        if (f.get("c1").contains("#")) {
            table = f.get("c1").split("\\.")[0];
            elm = f.get("c1").split("\\.")[1].split("#")[0];
            col = Integer.valueOf(f.get("c1").split("#")[1]);
        }

        if (!compare(f.get("condition"), f.get("c1"), f.get("c2"), shuffle.getRow(table, row))) {
            //データ除外
            if (f.get("data") == null) {
                return true;
            }else if(f.get("dataelse") != null){
                f.put("data", f.get("dataelse"));
                table = f.get("data").split("\\.")[0];
                elm = f.get("data").split("\\.")[1].split("#")[0];
                col = Integer.valueOf(f.get("data").split("#")[1]);
            }
        } else {
            if (f.get("data") != null) {
                table = f.get("data").split("\\.")[0];
                elm = f.get("c1").split("\\.")[1].split("#")[0];
                col = Integer.valueOf(f.get("data").split("#")[1]);
            }
        }
        if (f.get("data") != null) {
            dataList.add(shuffle.getData(table, row, elm, col));
        }

        return false;
    }

    private static Object calc(String op, String v1, String v2, List<String> ref) {
        v1 = ref.get(Integer.valueOf(v1.split("#")[1]));
        if (v2.contains("#")) {
            v2 = ref.get(Integer.valueOf(v2.split("#")[1]));
        }

        try{
        switch (op) {
            case "+":
                return Integer.valueOf(v1) + Integer.valueOf(v2);
            case "-":
                return Integer.valueOf(v1) - Integer.valueOf(v2);
            case "/":
                return Double.valueOf(v1) / Double.valueOf(v2);
            case "|":
                if (v1.equals("Y") || v2.equals("Y")) {
                    return 1;
                } else {
                    return 0;
                }
            default:
                System.out.println("Arithmatic Error! " + op);
                return null;
        }}catch(Exception e){
            e.printStackTrace();
            System.out.println(v1+" "+op+" "+v2 + ":" +ref);
            System.exit(0);
            return null;
        }
    }

    private static Boolean compare(String cond, String c1, String c2, List<String> ref) {
        c1 = ref.get(Integer.valueOf(c1.split("#")[1]));
        if (c2.contains("#")) {
            c2 = ref.get(Integer.valueOf(c2.split("#")[1]));
        }

        c2 = c2.replace("'", "");
        switch (cond) {
            case "=":
                return c1.equals(c2);
            case "<>":
                return !c1.equals(c2);
            case "<":
                return Integer.valueOf(c1) < Integer.valueOf(c2);
            case ">":
                return Integer.valueOf(c1) > Integer.valueOf(c2);
            default:
                return null;
        }
    }
}
