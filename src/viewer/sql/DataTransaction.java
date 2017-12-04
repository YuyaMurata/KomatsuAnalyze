/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author zz17390
 */
public class DataTransaction {
    String table;
    List header;
    List<List<String>> data = new ArrayList<>();
    
    public DataTransaction(String table, List<String> header) {
        this.table = table;
        this.header = header;
    }
    
    public void setData(List row){
        data.add(row);
    }
    
    public String getCreateTableSQL(){
        StringBuilder sql = new StringBuilder();
        
        sql.append("CREATE TABLE IF NOT EXISTS ");
        sql.append(table);
        sql.append("(");
        sql.append(String.join(" TEXT,", header));
        sql.append(" TEXT");
        sql.append(");");
        
        return sql.toString();
    }
    
    public String getDatSQL(){
        StringBuilder sql = new StringBuilder();
        
        sql.append("INSERT INTO ");
        sql.append(table);
        sql.append("(");
        sql.append(String.join(",", header));
        sql.append(")");
        
        List<String> value = new ArrayList<>();
        int n = data.get(0).size();
        for(int i=0; i < n; i++){
            List<String> s = new ArrayList<>();
            for(int j=0; j < data.size(); j++)
                s.add(data.get(j).get(i));
            
            StringBuilder v = new StringBuilder();
            v.append(" VALUES(");
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
