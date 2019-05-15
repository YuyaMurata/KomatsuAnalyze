/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import file.SyaryoToCompress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class SyaryoObjectReject {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String KISY = "PC200_form";

    public static void main(String[] args) {
        LOADER.setFile(KISY);
        Map<String, SyaryoObject> syaryoMap = LOADER.getSyaryoMap();

        type(syaryoMap, Arrays.asList(new String[]{"8", "8N1", "10"}));
        komtrax(syaryoMap, "KOMTRAX_ACT_DATA");
        service(syaryoMap, "20170501");
        empty(syaryoMap, "受注");
        syaryo(syaryoMap);

        LOADER.close();
        new SyaryoToCompress().write(LOADER.getFilePath(), syaryoMap);

    }

    //型による車両排除
    private static void type(Map<String, SyaryoObject> map, List<String> t) {
        System.out.println(t + "型 車両の処理");
        System.out.println("Before Number of Syaryo : " + map.size());

        Map<String, SyaryoObject> m = new TreeMap<>();
        Map<String, Integer> detail = new HashMap<>();
        map.keySet().stream().forEach(k -> {
            String typ = k.split("-")[1];

            if (t.contains(typ)) {
                m.put(k, map.get(k));
            } else {
                if (detail.get(typ) == null) {
                    detail.put(typ, 0);
                }
                detail.put(typ, detail.get(typ) + 1);
            }
        });

        map.clear();
        map.putAll(m);

        System.out.println("Number of Syaryo (型) : " + map.size());
        System.out.println("          Detail : " + detail);
    }

    //車両マスタの仕様情報から車両を削除
    private static void syaryo(Map<String, SyaryoObject> map) {
        System.out.println("仕様情報から車両の削除処理 data=[サブディーラ担当ポイントコード]");
        System.out.println("Before Number of Syaryo : " + map.size());

        List<String> DEALER_LIST = KomatsuDataParameter.DEALER_REJECT_LIST;
        System.out.println("リスト_" + DEALER_LIST);

        String specNo = "0";
        final int comp_idx = 0;
        final int dealer_idx = 7;

        Map<String, SyaryoObject> m = new TreeMap<>();
        Map<String, Integer> rej = new TreeMap<>();
        map.values().parallelStream().forEach(s -> {
            String comp = s.get("仕様").get(specNo).get(comp_idx);
            String dealer = s.get("仕様").get(specNo).get(dealer_idx);

            if (DEALER_LIST.contains(comp + "," + dealer)) {
                if (rej.get(comp + "," + dealer) == null) {
                    rej.put(comp + "," + dealer, 0);
                }
                rej.put(comp + "," + dealer, rej.get(comp + "," + dealer) + 1);
            } else {
                m.put(s.name, s);
            }
        });

        map.clear();
        map.putAll(m);

        System.out.println("Number of Syaryo (SPEC) : " + map.size());
        System.out.println("          Detail : " + rej);
    }

    //KOMTRAX対応していない車両の削除
    private static void komtrax(Map<String, SyaryoObject> map, String baseKey) {
        System.out.println("KOMTRAX非対応車両の処理 data=[" + baseKey + "]");
        System.out.println("Before Number of Syaryo : " + map.size());

        Map<String, SyaryoObject> kmmap = map.entrySet().parallelStream()
                .filter(e -> e.getValue().get(baseKey) != null)
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        
        map.clear();
        map.putAll(kmmap);
        
        System.out.println("Number of Syaryo (KOMTRAX) : " + map.size());
    }
    
    //KOMTRAX対応していない車両の削除
    private static void empty(Map<String, SyaryoObject> map, String baseKey) {
        System.out.println(baseKey+"が空の車両を処理 data=[" + baseKey + "]");
        System.out.println("Before Number of Syaryo : " + map.size());

        Map<String, SyaryoObject> emap = map.entrySet().parallelStream()
                .filter(e -> e.getValue().get(baseKey) != null)
                .filter(e -> !e.getValue().get(baseKey).isEmpty())
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        
        map.clear();
        map.putAll(emap);
        
        System.out.println("Number of Syaryo ("+baseKey+") : " + map.size());
    }

    //サービス情報から削除
    private static void service(Map<String, SyaryoObject> map, String date) {
        System.out.println("サービス情報による車両の処理 data=[カンパニ UR, GC] 新車納入 : " + date + " 以降の車両の削除");
        System.out.println("Before Number of Syaryo : " + map.size());

        //カンパニ車両除去
        Map companyMap = map.entrySet().parallelStream()
                .map(e -> e.getValue())
                .filter(s -> {
                    s.startHighPerformaceAccess();
                    Boolean c1 = true, c2 = true;
                    if(s.get("受注") != null)
                        c1 = !s.get("受注").values().stream()
                                        .filter(c -> c.get(0).equals("UR") || c.get(0).equals("GC"))
                                        .findFirst()
                                        .isPresent();
                    if(s.get("顧客") != null)
                        c2 = !s.get("顧客").values().stream()
                                        .filter(c -> c.get(0).equals("UR") || c.get(0).equals("GC"))
                                        .findFirst()
                                        .isPresent();
                    s.stopHighPerformaceAccess();
                    return c1 && c2;
                })
                .collect(Collectors.toMap(s -> s.name, s -> s));
        
        map.clear();
        map.putAll(companyMap);
        System.out.println("Number of Syaryo (会社) : " + map.size());

        ///新車除去
        Map newMap = map.entrySet().parallelStream().filter(e -> !(e.getValue().get("新車") == null
                ? e.getValue().get("生産").keySet().stream().filter(d -> Integer.valueOf(date) <= Integer.valueOf(d.split("#")[0])).findFirst().isPresent()
                : e.getValue().get("新車").keySet().stream().filter(d -> Integer.valueOf(date) <= Integer.valueOf(d.split("#")[0])).findFirst().isPresent())
        ).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        map.clear();
        map.putAll(newMap);
        System.out.println("Number of Syaryo (新車) : " + map.size());
    }
}
