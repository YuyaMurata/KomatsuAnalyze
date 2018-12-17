/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ZZ17390
 */
public class ListToCSV {
    public static List<String> toList(String csv){
        List<String> list = new ArrayList<>();
        try(BufferedReader br = CSVFileReadWrite.reader(csv)){
            String line;
            while((line = br.readLine()) != null)
                list.add(line);
            
            return list;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static void toCSV(String csv){
        List<String> list = new ArrayList<>();
        try(PrintWriter pw = CSVFileReadWrite.writer(csv)){
            list.stream().forEach(pw::println);   
        }
    }
}
