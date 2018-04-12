/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author ZZ17390
 */
public class LayoutAddEnable {

    public static void main(String[] args) {
        File[] flist = (new File("resource\\layout")).listFiles();

        for (File f : flist) {
            try (BufferedReader layout = CSVFileReadWrite.readerSJIS(f.getAbsolutePath())) {
                String l = layout.readLine();
                if (l.contains("enable")) {
                    continue;
                }
                try (PrintWriter pw = CSVFileReadWrite.writer(f.getName())) {
                    pw.println(l+",enable");
                    while ((l = layout.readLine()) != null) {
                        pw.println(l+",");
                    }
                }
            } catch (IOException ex) {
            }
        }
    }
}
