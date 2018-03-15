/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.add;

import java.util.TreeMap;
import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ZZ17390
 */
public class AddAllSupport {
    public static void main(String[] args) {
        TreeMap<String, String> asData = new TreeMap();
        String filename = "E:\\vmshare\\allsupport\\utf8\\201802AS契約.csv";
        
        try(BufferedReader csv = CSVFileReadWrite.reader(filename)) {
            String line;
            while((line = csv.readLine()) != null){
                String[] f = line.split(",");
                String kisy = f[10];
                String typ = f[11];
                String syhk = f[12];
                String kiban = f[13];
                
                String cid = f[3];
                
                String start = f[73];
                String stop = f[74];
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
