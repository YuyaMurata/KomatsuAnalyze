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
    public ChartTemplate() {
    }
    
    public void chart(String name, Map<String, List> map){
        //CSVFile 生成
        System.out.println(name+"-CSV 生成");
        try(PrintWriter csv = CSVFileReadWrite.writer(KomatsuDataParameter.GRAPH_TEMP_FILE)){
            csv.println("Syaryo,"+name);
            csv.println("Date,"+String.join(",", map.get("header")));
            map.remove("header");
            int i=0;
            
            for(String date : map.keySet()){
                List values = map.get(date);
                String d = date.split("#")[0].split(" ")[0].replace("/", "");
                if(d.length() > 8)
                    d = d.substring(0, 8);
                
                StringBuilder sb = new StringBuilder(d);
                i++;
                for(Object v : values){
                    sb.append(",");
                    if(v.equals("None"))
                        sb.append("NaN");
                    else
                        sb.append(v);
                }
                csv.println(sb.toString());
                
                if(i%1000 == 0)
                    System.out.println(i+"件処理");
                    
            }
        }
        
        //Graph Python 実行
        System.out.println(name+"-Python 実行");
        PythonCommand.py(KomatsuDataParameter.GRAPH_PY, KomatsuDataParameter.GRAPH_TEMP_FILE);
    }
    
    public void setGraphPath(String path){
        this.graphProgramPath = path;
    }
    
    public abstract Map<String, List> graphFile(String select, SyaryoObject4 syaryo);  
    public void graph(String select, SyaryoObject4 syaryo){
        Map<String, List> data = graphFile(select, syaryo);
        if(data != null)
            chart(syaryo.getName(), data);
    }
}
