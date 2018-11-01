/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.service;

import java.util.List;
import java.util.Map;
import javafx.concurrent.Task;
import json.SyaryoToZip3;
import viewer.graph.TimeSpreadChart;

/**
 *
 * @author ZZ17390
 */
public class FileLoadTask extends Task<Map>{
    private String filename;
    
    public FileLoadTask(String name) {
        this.filename = name;
    }
    
    @Override
    protected Map call() throws Exception {
        return new SyaryoToZip3().guiRead(filename);
    }
}
