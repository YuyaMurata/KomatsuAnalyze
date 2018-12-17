/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class ButtonService extends Service<Void>{
    public static SyaryoObject syaryo;
    public static String smenu;
    
    public void setInfo(String menu, SyaryoObject current){
        smenu = menu;
        syaryo = current;
    }
    
    @Override
    protected Task<Void> createTask() {
        return new ButtonTask();
    }
}
