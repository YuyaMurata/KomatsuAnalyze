/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axg.obj;

import java.util.List;
import org.bson.types.ObjectId;

/**
 *
 * @author ZZ17807
 */
public class MHeaderObject {
    private ObjectId id;
    private List<String> header;
    private Boolean isCompleted;
    
    public MHeaderObject(){
    }
    
    public MHeaderObject(List<String> header) {
        this.header = header;
        this.isCompleted = true;
    }
    
    public ObjectId getId() {
        return id;
    }

    public void setId(final ObjectId id) {
        this.id = id;
    }
    
    public List<String> getHeader(){
        return header;
    }
    
    public void setHeader(final List header){
        this.header = header;
    }
    
    public Boolean getIsCompleted(){
        return isCompleted;
    }
    
    public void setIsCompleted(final Boolean isCompleted){
        this.isCompleted = isCompleted;
    }
}
