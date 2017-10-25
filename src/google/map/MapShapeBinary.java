/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package google.map;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ZZ17390
 */
public class MapShapeBinary {

    public void write(TreeMap<String, Map<String, Map<String, List>>> timeMap) {
        try {
            ObjectOutputStream objOutStream
                    = new ObjectOutputStream(
                            new FileOutputStream("test.dat"));

            objOutStream.writeObject(timeMap);
            
            objOutStream.close();
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }
    
    public TreeMap<String, Map<String, Map<String, List>>> read(String filename) {
        try {
            ObjectInputStream objinStream
                    = new ObjectInputStream(
                            new FileInputStream(filename));
            
            TreeMap map = (TreeMap)objinStream.readObject();
            System.out.println(map);
            
            return map;
            
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } catch (ClassNotFoundException ex) {
        }
        return null;
    }
}
