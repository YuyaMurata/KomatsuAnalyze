/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import obj.SyaryoElements;

/**
 *
 * @author zz17390
 */
public class DataTransaction {
    String syaryo;
    String table;
    List header;
    List<List<String>> data = new ArrayList<>();
    
    public DataTransaction(String syaryo, String table, List<String> header) {
        this.syaryo = syaryo;
        this.table = table;
        this.header = header;
    }
    
    public void setData(List row){
        data.add(row);
    }
    
    public String preparedSQL(){
        StringBuilder sql = new StringBuilder();
        sql.append("DROP TABLE IF EXISTS ");
        sql.append(table);
        sql.append(";");
        
        return sql.toString();
    }
    
    public String getCreateTableSQL(){
        StringBuilder sql = new StringBuilder();
        
        sql.append("CREATE TABLE IF NOT EXISTS ");
        sql.append(table);
        sql.append("(");
        sql.append("SID INTEGER, 機種・型・機番 TEXT,");
        sql.append(String.join(" TEXT,", header));
        sql.append(" TEXT");
        sql.append(");");
        
        return sql.toString();
    }
    
    public String getDataSQL(){
        StringBuilder sql = new StringBuilder();
        
        sql.append("INSERT INTO ");
        sql.append(table);
        sql.append("(");
        sql.append("SID,");
        sql.append("機種・型・機番,");
        sql.append(String.join(",", header));
        sql.append(") VALUES ");
        
        List<String> value = new ArrayList<>();
        int n = data.get(0).size();
        for(int i=0; i < n; i++){
            List<String> s = new ArrayList<>();
            
            s.add(syaryo);   
            for(int j=0; j < data.size(); j++)
                if(header.get(j).toString().equals("日付"))
                    s.add(data.get(j).get(i).split("#")[0]);
                else
                    s.add(data.get(j).get(i));
            
            StringBuilder v = new StringBuilder();
            v.append("(");
            v.append(i);
            v.append(",");
            v.append(s.stream().map(d -> "'"+d+"'").collect(Collectors.joining(",")));
            v.append(")");
            value.add(v.toString());
        }
        sql.append(String.join(",", value));
        sql.append(";");
        
        return sql.toString();
    }
    
    public String getRecordCountSQL(){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM ");
        sql.append(table);
        sql.append(";");
        
        return sql.toString();
    }
}
