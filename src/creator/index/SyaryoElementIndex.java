/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.index;

import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ZZ17390
 */
public class SyaryoElementIndex {
    private static String OUTFILE="index\\syaryo_data_index.csv";
    public static void main(String[] args) {
        
        File[] flist = (new File("resource\\layout")).listFiles();
        for(File file : flist)
            createIndex(file);
    }
    
    private static void createIndex(File file){
        try(BufferedReader br = CSVFileReadWrite.readerSJIS(file.getAbsolutePath());
            PrintWriter pw = CSVFileReadWrite.addwriter(OUTFILE)){
            System.out.println(file.getName());
            
            String line = br.readLine().replace(" ", "");
            List<String> header = Arrays.asList(line.split(","));
            
            pw.println("テーブル名,"+file.getName().replace("Layout_", "").replace(".csv", ""));
            StringBuilder sb1 = new StringBuilder("No.,");
            StringBuilder sb2 = new StringBuilder("名称,");
            StringBuilder sb3 = new StringBuilder("コード,");
            while((line=br.readLine()) != null){
                String[] s = line.split(",");
                sb1.append(s[header.indexOf("no")]).append(",");
                sb2.append(s[header.indexOf("name")]).append(",");
                sb3.append(s[header.indexOf("code")]).append(",");
            }
            pw.println(sb1.substring(0, sb1.length()-1));
            pw.println(sb2.substring(0, sb2.length()-1));
            pw.println(sb3.substring(0, sb3.length()-1));
            pw.println("SELECT");
            pw.println("JOIN(FROM)");
            pw.println("JOIN(TO)");
            pw.println("");
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
