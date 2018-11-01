/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.service;

import java.util.Map;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author ZZ17390
 */
public class FileLoadService extends Service<Map>{
    public static String filename;
    
    public void setInfo(String name){
        filename = name;
    }
    
    @Override
    protected Task<Map> createTask() {
        return new FileLoadTask(filename);
    }
}
