/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author ZZ17390
 */
public class CSVFileReadWrite {
    public static PrintWriter writer(String filename){
        try {
            return  new PrintWriter(new OutputStreamWriter(new FileOutputStream(filename), "SJIS"));
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        
        return null;
    }
}
