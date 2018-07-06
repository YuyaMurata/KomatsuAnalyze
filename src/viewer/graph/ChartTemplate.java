/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.graph;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javafx.concurrent.Task;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;
import program.py.PythonCommand;

/**
 *
 * @author ZZ17390
 */
public abstract class ChartTemplate{
    public String graphProgramPath;
    public String headers;
    public ChartTemplate() {
    }
    
    public void chart(String name, Map<String, List> map){
        //CSVFile 生成
        try(PrintWriter csv = CSVFileReadWrite.writer(KomatsuDataParameter.GRAPH_TEMP_FILE)){
            csv.println("Syaryo,"+name);
            csv.println(headers);
            int i=0;
            for(String date : map.keySet()){
                List values = map.get(date);
                StringBuilder sb = new StringBuilder(date);
                for(Object v : values){
                    sb.append(",");
                    sb.append(v);
                }
                csv.println(sb.toString());
            }
        }
        
        //Graph Python 実行
        PythonCommand.py(KomatsuDataParameter.GRAPH_PY, KomatsuDataParameter.GRAPH_TEMP_FILE);
    }
    
    public void setGraphPath(String path){
        this.graphProgramPath = path;
    }
    
    public abstract Map<String, List> graphFile(String select, String headers, SyaryoObject4 syaryo);
    
    public void graph(String select, String headers, SyaryoObject4 syaryo){
        this.headers = headers;
        Map<String, List> data = graphFile(select, headers, syaryo);
        if(data != null)
            chart(syaryo.getName(), data);
    }
}