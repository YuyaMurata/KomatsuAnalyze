/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import static export.DeliverDate.extractDate;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import json.JsonToSyaryoObj;
import obj.SyaryoElements;
import obj.SyaryoObject2;

/**
 *
 * @author kaeru_yuya
 */
public class LifeOrderPrice {

    private static String kisy = "PC200";
    private static String[] kbn = new String[]{"123", "111", "112"};

    public static void main(String[] args) {
        String filename = "json\\syaryo_obj_" + kisy + "_form.json";
        Map<String, SyaryoObject2> syaryoMap = new JsonToSyaryoObj().reader3(filename);

        String outputname = "life_order_price_" + kisy + ".csv";
        try (PrintWriter csv = CSVFileReadWrite.writer(outputname)) {
            extractMaxOrder(syaryoMap, csv);
        }
    }

    public static void extractMaxOrder(Map<String, SyaryoObject2> syaryoMap, PrintWriter csv) {
        int cnt = 0;

        csv.println("ID,Kisy,Type,作番1123,,売上1123,,作番2111,,売上2111,,作番2112,,売上2112,");
        for (SyaryoObject2 syaryo : syaryoMap.values()) {
            cnt++;

            StringBuilder sb = new StringBuilder();
            sb.append(syaryo.getName());
            sb.append(",");
            sb.append(syaryo.getMachine());
            sb.append(",");
            sb.append(syaryo.getType());
            sb.append(",");

            Map<String, Integer> maxOrder = new HashMap();
            Map<String, Integer> maxKOrder = new HashMap();
            Map<String, String> maxOrderSBN = new HashMap();
            Map<String, String> maxKOrderSBN = new HashMap();
            if (syaryo.getOrder() != null) {
                for (List order : syaryo.getOrder().values()) {
                    String kbn = (String) order.get(SyaryoElements.Order.FLAG.getNo());

                    if (maxOrder.get(kbn) == null) {
                        maxOrder.put(kbn, 0);
                    }

                    if (maxKOrder.get(kbn) == null) {
                        maxKOrder.put(kbn, 0);
                    }

                    Integer price = Double.valueOf((String) order.get(SyaryoElements.Order.Invoice.getNo())).intValue();
                    String sbn = (String) order.get(SyaryoElements.Order.ID.getNo());
                    Integer kprice = Double.valueOf((String) order.get(SyaryoElements.Order.KInvoice.getNo())).intValue();
                    String ksbn = (String) order.get(SyaryoElements.Order.ID.getNo());

                    if (maxOrder.get(kbn) <= price) {
                        maxOrderSBN.put(kbn, sbn);
                        maxOrder.put(kbn, price);
                    }

                    if (maxKOrder.get(kbn) <= kprice) {
                        maxKOrderSBN.put(kbn, ksbn);
                        maxKOrder.put(kbn, kprice);
                    }
                }

                for (String kbn : maxOrder.keySet()) {
                    try {
                        sb.append(nullCheck(maxOrderSBN.get(kbn)));
                        sb.append(",");
                        sb.append(nullCheck(maxKOrderSBN.get(kbn)));
                        sb.append(",");
                        sb.append(nullCheck(maxOrder.get(kbn)));
                        sb.append(",");
                        sb.append(nullCheck(maxKOrder.get(kbn)));
                        sb.append(",");
                    } catch (NullPointerException e) {
                        System.out.println(syaryo.getName());
                        System.exit(0);
                    }
                }
            }

            sb.deleteCharAt(sb.lastIndexOf(","));

            csv.println(sb.toString());

            if (cnt % 1000 == 0) {
                System.out.println(cnt + "台");
            }
        }
    }
    
    private static String nullCheck(Object value){
        if(value == null){
            return "";
        }else
            return value.toString();
    }
}
