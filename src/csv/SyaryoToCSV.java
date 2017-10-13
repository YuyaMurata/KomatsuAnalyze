/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import json.JsonToSyaryoObj;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class SyaryoToCSV {
    public static void main(String[] args) {
        String path = "分析結果\\";
        String filename = path+"WA470_typ_order";
        Map<String, SyaryoObject> syaryoMap = new JsonToSyaryoObj().reader("syaryo_obj_WA470_form.json");
        
        
        PrintWriter pw;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename+".csv"))));
            for(SyaryoObject syaryo : syaryoMap.values()){
                if(syaryo.getOrder()!= null)
                for(String date : syaryo.getOrder().keySet()){
                    List order = syaryo.getOrder(date);
                    //System.out.println(order);
                    pw.println(syaryo.getName()+","+syaryo.getType()+","+order.stream().collect(Collectors.joining(",")));
                }
            }
            
            pw.close();
        } catch (IOException ex) {
        }
    }
}
