/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbimport;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ZZ17390
 */
public class TableContent {
    public String name;
    public TableContent(String name) {
        this.name = name;
    }
    
    private List fields = new ArrayList();
    private List fnames = new ArrayList();
    public void add(String field, String fname){
        fields.add(field);
        fnames.add(fname);
    }
    
    public List getFields(){
        return fields;
    }
    
    public List getNames(){
        return fnames;
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(fields.toString());
        sb.append("/");
        sb.append(fnames.toString());
        return sb.toString();
    }
}
