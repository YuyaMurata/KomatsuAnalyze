/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import json.JsonToSyaryoObj;
import json.SyaryoToZip;
import obj.SyaryoElements;
import obj.SyaryoObject2;

/**
 *
 * @author kaeru_yuya
 */
public class LifeOrderPrice {

    private static String kisy = "WA470";
    private static String syaryoName = "WA470-7-10354";
    
    public static void main(String[] args) {
        String filename = "json\\syaryo_obj_" + kisy + "_form";
        Map<String, SyaryoObject2> syaryoMap = new SyaryoToZip().readObject(filename);

        /*String outputname = "life_order_price_" + kisy + ".csv";
        try (PrintWriter csv = CSVFileReadWrite.writer(outputname)) {
            extractMaxOrder(syaryoMap, csv, 1); //0:ALL 1:NEW 2:USED
        }*/
        
        String outputname = "life_order_price_" + syaryoName + ".csv";
        try (PrintWriter csv = CSVFileReadWrite.writer(outputname)) {
            extractSyaryoLifeOrder(syaryoMap.get(syaryoName), csv);
        }
    }

    public static void extractMaxOrder(Map<String, SyaryoObject2> syaryoMap, PrintWriter csv, int newUsedFlg) {
        int cnt = 0;

        csv.println("Company,ID,Kisy,Type,業種コード,回数,合計,平均金額,中央値,会社_区分_作番,最大金額,会社_区分_作番,最小金額");
        for (SyaryoObject2 syaryo : syaryoMap.values()) {
            cnt++;
            syaryo.decompress();

            StringBuilder sb = new StringBuilder();
            String company = ExportTool.extractCompany(syaryo);
            if (company == null || syaryo.getOrder() == null) {
                System.out.println(company + "," + syaryo.name);
                continue;
            }
            
            if(newUsedFlg == 1)
                if(syaryo.getUsed() !=null)
                    continue;
            else if(newUsedFlg == 2)
                if(syaryo.getUsed() ==null)
                    continue;

            sb.append(company);
            sb.append(",");
            sb.append(syaryo.getName());
            sb.append(",");
            sb.append(syaryo.getMachine());
            sb.append(",");
            sb.append(syaryo.getType());
            sb.append(",'");
            for(String customerCode : ExportTool.extractOwner(syaryo)){
                sb.append(customerCode);
                sb.append("_");
            }
            sb.deleteCharAt(sb.lastIndexOf("_"));
            sb.append(",");
            
                
                
            Map orderPrice = initialize();
            int numOrder = 0;
            for (List order : syaryo.getOrder().values()) {
                numOrder++;
                String kbn = (String) order.get(SyaryoElements.Order.FLAG.getNo());
                String comp = (String) order.get(SyaryoElements.Order.Company.getNo());
                String sbn = (String) order.get(SyaryoElements.Order.ID.getNo());
                Integer price = Double.valueOf((String) order.get(SyaryoElements.Order.Invoice.getNo())).intValue();

                if ((int) orderPrice.get("max") < price) {
                    orderPrice.put("max_sbn", comp + "_" + kbn + "_" + sbn);
                    orderPrice.put("max", price);
                }
                if ((int) orderPrice.get("min") > price) {
                    orderPrice.put("min_sbn", comp + "_" + kbn + "_" + sbn);
                    orderPrice.put("min", price);
                }
                ((List) orderPrice.get("list")).add(price);
            }

            //Number of Order
            sb.append(numOrder);
            sb.append(",");

            //Total
            Integer total = ((List<Integer>) orderPrice.get("list")).stream().mapToInt(e -> e).sum();
            sb.append(total);
            sb.append(",");

            //Average
            Double average = ((List<Integer>) orderPrice.get("list")).stream().mapToInt(e -> e).average().getAsDouble();
            sb.append(average);
            sb.append(",");

            //Median
            int medianIndex = ((List) orderPrice.get("list")).size() / 2 - 1;
            if (medianIndex < 0) {
                medianIndex = 0;
            }
            Integer median = ((List<Integer>) orderPrice.get("list")).stream().mapToInt(e -> e)
                .sorted().toArray()[medianIndex];
            sb.append(median);
            sb.append(",");

            //Max
            sb.append(orderPrice.get("max_sbn"));
            sb.append(",");
            sb.append(orderPrice.get("max"));
            sb.append(",");

            //Min
            sb.append(orderPrice.get("min_sbn"));
            sb.append(",");
            sb.append(orderPrice.get("min"));

            csv.println(sb.toString());

            syaryo.compress(true);
            if (cnt % 1000 == 0) {
                System.out.println(cnt + "台");
            }
        }
    }

    private static Map initialize() {
        Map priceMap = new HashMap();

        priceMap.put("max", 0);
        priceMap.put("max_sbn", "");
        priceMap.put("min", Integer.MAX_VALUE);
        priceMap.put("min_sbn", "");
        priceMap.put("list", new ArrayList());

        return priceMap;
    }
    
    public static void extractSyaryoLifeOrder(SyaryoObject2 syaryo, PrintWriter csv) {
        csv.println("日付,会社,区分,作番,金額,概要,,累積コマツ請求,累積社内請求,累積一般請求(単),累積一般請求(修)");
        syaryo.decompress();
        
        Long general_s = 0L,general_p = 0L,komatsu = 0L,company = 0L;
        
        int numOrder = 0;
        for (List order : syaryo.getOrder().values()) {
            StringBuilder sb = new StringBuilder();
            
            numOrder++;
            String date = (String) order.get(SyaryoElements.Order.Date.getNo());
            String kbn = (String) order.get(SyaryoElements.Order.FLAG.getNo());
            String comp = (String) order.get(SyaryoElements.Order.Company.getNo());
            String sbn = (String) order.get(SyaryoElements.Order.ID.getNo());
            Integer price = Double.valueOf((String) order.get(SyaryoElements.Order.Invoice.getNo())).intValue();
            String summary = (String) order.get(SyaryoElements.Order.Summary.getNo());
            
            sb.append(date.substring(0, 4));
            sb.append("/");
            sb.append(date.substring(4, 6));
            sb.append("/");
            sb.append(date.substring(6, 8));
            sb.append(",");
            sb.append(comp);
            sb.append(",UAG_");
            sb.append(kbn);
            sb.append(",");
            sb.append(sbn);
            sb.append(",");
            sb.append(price);
            sb.append(",");
            sb.append(summary);
            
            //請求区分
            if(kbn.equals("1-1-1"))
                general_s += price;
            else if(kbn.equals("1-2-3"))
                general_p += price;
            else if(kbn.equals("1-1-2"))
                komatsu += price;
            else
                company += price;
            
            sb.append(",,");
            sb.append(komatsu);
            sb.append(",");
            sb.append(company);
            sb.append(",");
            sb.append(general_p);
            sb.append(",");
            sb.append(general_s);
            
            csv.println(sb.toString());
        }
        
        System.out.println("車両,"+syaryo.name+" ,受注回数:"+numOrder);
    }
}
