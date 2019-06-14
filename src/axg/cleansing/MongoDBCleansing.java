/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axg.cleansing;

import axg.mongodb.MongoDBData;
import axg.obj.MSyaryoObject;
import file.MapToJSON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author ZZ17807
 */
public class MongoDBCleansing {

    //Index
    static Map<String, Map<String, List<String>>> index = new MapToJSON().toMap("axg\\mongoobj_syaryo_src.json");
    static List<String> target = Arrays.asList("売上", "");

    public static void main(String[] args) {
        /*MongoDBData mongo = MongoDBData.create();
        mongo.set("json", "komatsuDB_PC200");
        mongo.copyTo("komatsuDB_PC200_Temp");
        mongo.close();
         */

        MongoDBData mongo2 = MongoDBData.create();
        mongo2.set("json", "komatsuDB_PC200_Temp");

        List<String> sids = mongo2.getKeyList();

        sids.stream().forEach(s -> {
            MSyaryoObject m = mongo2.get(s);
            System.out.print(s);
            cleanOne(m);
        });
        //System.out.println(mongo2.get("name", "PC200-8-N1-351483", "顧客_S").toJson());
        mongo2.close();
    }

    public static MSyaryoObject cleanOne(MSyaryoObject obj) {
        //売上 table
        Map rule = sellRule();

        String key = "売上";
        List<String> removeKey = removeData(key, obj.get(key), rule);
        System.out.println(String.join(",", removeKey));

        /*obj.removeAll(key, removeKey);
        obj.recalc();
        obj.print();
        System.out.println(obj.get(key));
        */
        return null;
    }

    private static Map sellRule() {
        return new HashMap() {
            {
                put("売上.受注売上区分", Arrays.asList("2"));
                put("売上.見込実績区分", Arrays.asList("2"));
                put("売上.折半元折半先区分", Arrays.asList("1"));
                put("売上.本体赤黒区分", Arrays.asList("1"));
                put("売上.受注売上実績最新フラグ", Arrays.asList("1"));
                put("売上.論理削除フラグ", Arrays.asList("0"));
                put("売上.本体案件取引進捗区分", Arrays.asList("151", "153", "155"));
            }
        };
    }

    private static Map serviceRule() {
        return new HashMap() {
            {
                put("サービス経歴.発生区分", "1");
                put("サービス経歴.赤黒区分", "0");
            }
        };
    }

    private static Map orderRule() {
        return new HashMap() {
            {
                put("受注.作番クローズ理由コード", "1");
            }
        };
    }

    private static Map syaryoRule() {
        return new HashMap() {
            {
                put("KOMPAS車両.論理削除フラグ", "0");
            }
        };
    }

    private static Map workRule() {
        return new HashMap() {
            {
                put("作業.論理削除フラグ", "0");
            }
        };
    }

    private static Map partsRule() {
        return new HashMap() {
            {
                put("部品.論理削除フラグ", "0");
            }
        };
    }

    private static List<String> removeData(String key, Map<String, List<String>> data, Map<String, List<String>> rule) {
        if(data == null){
            System.out.print(",,,,");
            return new ArrayList<>();
        }
        List<String> removeSubKey = data.entrySet().stream()
                .filter(d -> !rule.entrySet().stream()
                .filter(r -> !r.getValue().contains(d.getValue().get(index.get(key).get(key + ".subKey").indexOf(r.getKey()))))
                .findFirst().isPresent()
                )
                .map(s -> s.getKey())
                .collect(Collectors.toList());

        System.out.print(","+(data.size()-removeSubKey.size())+","+data.size()+"," + removeSubKey.size()+",");

        return removeSubKey;
    }
}
