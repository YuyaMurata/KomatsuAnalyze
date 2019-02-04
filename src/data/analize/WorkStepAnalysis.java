/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ZZ17390
 */
public class WorkStepAnalysis {
    private static String file = "作業工数分析_20190204.csv";
    public static void main(String[] args) {
        //抽出車両
        String name = "PC200-8N1-315586";
        
        try(BufferedReader br = CSVFileReadWrite.reader(file)){
            List<String> header = Arrays.asList(br.readLine().split(","));
            int kisy_idx = header.indexOf("機種");
            int typ_idx = header.indexOf("型式");
            int syhk_idx = header.indexOf("小変形");
            int kiban_idx = header.indexOf("機番");
            
            String line = "";
            int total = 0;
            try(PrintWriter pw = CSVFileReadWrite.writer(name+".csv")){
                while((line = br.readLine()) != null){
                    String[] data = line.split(",");
                    
                    String n = data[kisy_idx]+"-"+data[typ_idx]+data[syhk_idx]+"-"+data[kiban_idx];
                    if(n.equals(name)){
                        total++;
                        pw.println(line);
                    }
                }
            }
            
            System.out.println(name+":"+total);
            
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
