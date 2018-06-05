/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import creator.create.KomatsuDataParameter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zz17390
 */
public class DataRejectRule {
    List kuec = new ArrayList();
    public List getKUEC(){
        return kuec;
    }
    
    List kuec_ids = KomatsuDataParameter.KUEC_LIST;
    public Boolean addKUEC(String id, String date){
        if(kuec_ids.contains(id)){
            kuec.add(date);
            return true;
        }
        return false;
    }
    
    List work = new ArrayList();
    public List getWORKID(){
        return work;
    }
    
    List parts = new ArrayList();
    public List getPARTSID(){
        return parts;
    }
}
