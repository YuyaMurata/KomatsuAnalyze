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
        String kisy = "PC200";
        Map<String, SyaryoObject> syaryoMap = new JsonToSyaryoObj().reader("syaryo_obj_"+kisy+"_form.json");
        
        //order(path+kisy, syaryoMap);
        
        komtrax(path+kisy, syaryoMap);
        
    }
    
    public static void order(String filename, Map<String, SyaryoObject> syaryoMap){
        PrintWriter pw;
        
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename+"_order.csv"))));
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
    
    public static void komtrax(String filename, Map<String, SyaryoObject> syaryoMap){
        PrintWriter pw1,pw2;
        
        try {
            pw1 = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename+"_komtrax_syaryoDB.csv"))));
            pw2 = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename+"_komtrax_komtraxDB.csv"))));
            
            List<SyaryoObject> syaryos = syaryoMap.values().stream()
                                                .filter(s -> s.getType().equals("8"))
                                                .collect(Collectors.toList());
            
            for(SyaryoObject syaryo : syaryos){
                String[] name = syaryo.getName().split("-");
                if(syaryo.getKomtrax()) pw1.println(name[0]+","+name[1]+","+name[2]+","+syaryo.getKomtrax());
                if(syaryo.getSMR() != null)
                    for(String date : syaryo.getSMR().keySet())
                        if(syaryo.getSMR().get(date).get(1).toString().contains("komtrax")){
                            pw2.println(name[0]+","+name[1]+","+name[2]+","+syaryo.getKomtrax());
                            break;
                        }
            }
            
            pw1.close();
            pw2.close();
        } catch (IOException ex) {
        }
    }
}
