/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.index;

import db.HiveDB;
import db.field.Customer;
import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.MapIndexToJSON;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class CustomerIndex {
    private static String INDEX_PATH = KomatsuDataParameter.SETTING_GECUSTTDATA_PATH;
    private static String OUTPATH = KomatsuDataParameter.CUSTOMER_INDEX_PATH;
    private static List<String> codelist = new ArrayList<>();
    
    public static void main(String[] args) {
        //Layout Index
        Map<String, List> index = index();
        
        TreeMap<String, String> customerIndex = new TreeMap();
        
        try {
            Statement stmt = HiveDB.getConnection().createStatement();

            //Customer
            String sql = createSQL(index);
            System.out.println("Running: " + sql);
            
            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            while (res.next()) {
                n++;
                List<String> content = new ArrayList();
                for(String c : codelist)
                    content.add(res.getString(c));
                
                String company = res.getString(Customer._Customer.KSYCD.get());
                String customer = res.getString(Customer._Customer.KKYKCD.get());
                
                //Customer Index
                customerIndex.put(company+"_"+customer, String.join(",", content));

                if (n % 10000 == 0) {
                    //System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Created CutomerIndex = " + n);
            
            new MapIndexToJSON().write(OUTPATH, customerIndex);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
    }
    
    private static String createSQL(Map<String, List> index){
        StringBuilder sql = new StringBuilder("select ");
        //common
        List<String> list = index.get("customer_common");
        String[] j = list.get(list.size()-1).split(",");
        list = list.subList(0, list.size()-1);
        for(String c : list)
            sql.append("a.").append(c).append(",");
        codelist.addAll(list);
        
        //customer
        list = index.get("customer");
        list = list.subList(0, list.size()-1);
        list.remove("KSYCD");
        list.remove("KKYKCD");
        for(String c : list)
            sql.append("b.").append(c).append(",");
        codelist.addAll(list);
        
        sql.deleteCharAt(sql.length()-1);
        sql.append(" from ").append("customer_common a");
        sql.append(" join ").append("customer b").append(" on (");
        
        for(int i=2; i < j.length; i++)
            sql.append("a.").append(j[i]).append("=").append("b.").append(j[i]).append(" and ");
        sql.delete(sql.length() - 5, sql.length()).append(")");
        
        return sql.toString();
    }
    
    //Set Layout Index
    public static Map index() {
        try (BufferedReader br = CSVFileReadWrite.readerSJIS(INDEX_PATH)) {
            String line;
            Map index = new HashMap();
            while ((line = br.readLine()) != null) {
                String table = line;
                String number = br.readLine();
                String name = br.readLine();
                String[] code = br.readLine().split(",");
                String[] select = br.readLine().split(",");
                String[] joinTo = br.readLine().split(",");
                br.readLine();

                List layout = new ArrayList();
                for (int i = 1; i < select.length; i++) {
                    if (select[i].equals("1")) {
                        layout.add(code[i]);
                    }
                }

                if (layout.isEmpty()) {
                    continue;
                }

                if (joinTo.length > 1) {
                    layout.add(Arrays.asList(joinTo).stream().filter(s -> s.length() > 0).collect(Collectors.joining(",")));
                } else {
                    layout.add("None");
                }

                System.out.println(table+":"+layout);

                index.put(table.split(",")[1], layout);

            }

            return index;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    //Set ShuffleLayout
    public static Map layoutIndex(){
        Map<String, List> layoutIndex = index();
        Map<String, List> index = new HashMap<>();
        
        List<String> cust_com = layoutIndex.get("customer_common");
        List<String> cust = layoutIndex.get("customer");
        cust.remove("KSYCD");
        cust.remove("KKYKCD");
        cust_com = cust_com.stream().filter(s -> !s.contains("JOIN(TO)")).collect(Collectors.toList());
        
        cust_com.addAll(cust);
        index.put("customer", cust_com);
        
        return index;
    }
}
