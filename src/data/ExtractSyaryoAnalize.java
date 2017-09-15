/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import json.JsonToSyaryoObj;
import obj.SyaryoObject;

/**
 *
 * @author zz17390
 */
public class ExtractSyaryoAnalize {

    public static void main(String[] args) throws IOException {
        String fileName = "syaryo_history_pc200.json";
        Map<String, SyaryoObject> syaryoMap = new JsonToSyaryoObj().reader(fileName);

        String csv = "PC200_data_info.csv";
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(csv)));
        pw.println("機種・型・機番,業種,新車価格,中古車価格,差額,新車年数,切替時SMR,切替時サービス数");

        Map<String, Integer> price = new HashMap();
        Map<String, Integer> uprice = new HashMap();
        for (SyaryoObject syaryo : syaryoMap.values()) {
            if (!syaryo.getType().equals("8") || syaryo.getUsed().isEmpty()) {
                continue;
            }

            TreeMap<String, String> map = new TreeMap(syaryo.getUsed());
            if (map.firstEntry().getValue().equals("")) {
                continue;
            }
            if (syaryo.getPrice() == null) {
                continue;
            }
            if (syaryo.getPrice().equals("")) {
                continue;
            }
            //if(syaryo.getSMR() == null)
            //    continue;
            //

            //year
            Integer year = Integer.valueOf(map.firstKey().substring(0, 4)) - Integer.valueOf(syaryo.getNew().substring(0, 4));

            //Price
            if (price.get(syaryo.getPrice()) == null) {
                price.put(syaryo.getPrice(), 0);
            }
            price.put(syaryo.getPrice(), price.get(syaryo.getPrice()) + 1);

            if (uprice.get(map.firstEntry().getValue()) == null) {
                uprice.put(map.firstEntry().getValue(), 0);
            }
            uprice.put(map.firstEntry().getValue(), uprice.get(map.firstEntry().getValue()) + 1);

            String date = map.firstKey();

            //SMR
            String smrv = "";
            if (syaryo.getSMR() != null) {
                TreeMap<String, String> smr = new TreeMap(syaryo.getSMR());
                for (String smrdate : smr.keySet()) {
                    if(!smrdate.equals(""))
                    if (Integer.valueOf(smrdate) <= Integer.valueOf(date)) {
                        smrv = smr.get(smrdate);
                    } else {
                        break;
                    }
                }
            }

            //History
            TreeMap<String, String> history = new TreeMap(syaryo.getHistory());
            int cnt = 0;
            for (String hisdate : history.keySet()) {
                if (Integer.valueOf(hisdate) <= Integer.valueOf(date)) {
                    cnt++;
                } else {
                    break;
                }
            }

            //customer
            Map customer = (Map) new TreeMap(syaryo.getOwner()).firstEntry().getValue();
            String gyosyu = customer.keySet().stream().findFirst().get().toString().split("-")[3];
            System.out.println(syaryo.getName());
            pw.println(syaryo.getName() + "," + gyosyu + "," + syaryo.getPrice() + "," + map.firstEntry().getValue() + ","
                    + "" + "," + year + "," + smrv + "," + cnt);
        }
        pw.close();

        String csv2 = "PC200_price_info.csv";
        PrintWriter pw2 = new PrintWriter(new BufferedWriter(new FileWriter(csv2)));
        pw2.println("価格,台数");

        for (String newPrice : price.keySet()) {
            pw2.println(newPrice + "," + price.get(newPrice));
        }

        pw2.println("価格,台数");
        for (String usedPrice : uprice.keySet()) {
            pw2.println(usedPrice + "," + uprice.get(usedPrice));
        }

        pw2.close();
    }
}
