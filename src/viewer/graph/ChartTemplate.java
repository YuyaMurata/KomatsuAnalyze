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
import obj.SyaryoObject;
import param.KomatsuDataParameter;
import program.py.PythonCommand;

/**
 *
 * @author ZZ17390
 */
public abstract class ChartTemplate{
    private static String PY_PATH = KomatsuDataParameter.PYTHONE_PATH;
    private static Map<String, String> PY_SCRIPT = KomatsuDataParameter.GRAPH_PY;
    
    public ChartTemplate() {
    }
    
    public void chart(String name, Map<String, String> map){
        //CSVFile 生成
        System.out.println(name+"-CSV 生成");
        try(PrintWriter csv = CSVFileReadWrite.writer(KomatsuDataParameter.GRAPH_TEMP_FILE)){
            csv.println("Syaryo,"+name);
            csv.println(map.get("header"));
            map.remove("header");
            
            map.entrySet().stream()
                        .map(e -> e.getKey().substring(0,8)+","+e.getValue().replace("None", "NaN"))
                        .forEach(csv::println);
            
            /*for(String date : map.keySet()){
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
            }*/
        }
    }
    
    public void exec(String name, String script){
        //Graph Python 実行
        System.out.println(name+"-Python 実行");
        PythonCommand.py(script, KomatsuDataParameter.GRAPH_TEMP_FILE);
    }
    
    public abstract Map<String, String> graphFile(String select, SyaryoObject syaryo);
    
    public void graph(String select, SyaryoObject syaryo){
        Map<String, String> data = graphFile(select, syaryo);
        if(data != null){
            String script = PY_PATH+PY_SCRIPT.get(select);
            chart(syaryo.getName(), data);
            exec(syaryo.getName(), script);
        }
    }
}
