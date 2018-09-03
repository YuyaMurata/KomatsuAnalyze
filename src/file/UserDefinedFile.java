/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ZZ17390
 */
public class UserDefinedFile {
    public static List<String> filter(String filename){
        List filterList = new ArrayList();
        try(BufferedReader br = CSVFileReadWrite.reader(filename)){
            String line;
            while((line =br.readLine()) != null){
                filterList.addAll(Arrays.asList(line.split(",")));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        if(filterList.isEmpty())
            return null;
        return filterList;
    }
}
