/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.graph;

import file.CSVFileReadWrite;
import file.ListToCSV;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
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
    private static String PY_CSV_FILE = KomatsuDataParameter.GRAPH_TEMP_FILE;
    
    public ChartTemplate() {
    }
    
    public void exec(String name, String script){
        //Graph Python 実行
        System.out.println(name+"-Python 実行");
        PythonCommand.py(script, KomatsuDataParameter.GRAPH_TEMP_FILE);
    }
    
    public abstract List<String> graphFile(String select, int idx, SyaryoObject syaryo);
    
    public void graph(String select, int idx, SyaryoObject syaryo){
        List<String> data = graphFile(select, idx, syaryo);
        if(data != null){
            String script = PY_PATH+PY_SCRIPT.get(select);
            System.out.println(syaryo.name+"-CSV 生成 scripts:"+PY_SCRIPT.get(select));
            
            //CSV生成
            ListToCSV.toCSV(PY_CSV_FILE, data);
            
            //Python 実行
            exec(syaryo.getName(), script);
        }
    }
}
