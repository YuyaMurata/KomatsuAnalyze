/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import obj.LoadSyaryoObject;

public class SyaryoObjectClient {
    private static SyaryoObjectClient instance = new SyaryoObjectClient();
    private static RemoteSyaryoObjectLoader stub;
    public Boolean isRunnable = false;
    
    private SyaryoObjectClient() {
        String host = null;
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            stub = (RemoteSyaryoObjectLoader) registry.lookup(RemoteSyaryoObjectLoader.className);
            isRunnable = true;
        } catch (Exception e) {
            System.err.println("Not Launched Server!");
            //e.printStackTrace();
            isRunnable = false;
        }
    }
    
    public static SyaryoObjectClient getInstance(){
        return instance;
    }
    
    public Boolean setLoadFile(String file){
        if(file.equals("Default File"))
            return false;
        
        try {
            stub.setSyaryoObjectLoader(file);
            return true;
        } catch (RemoteException ex) {
            //ex.printStackTrace();
            return false;
        }
    }
    
    public Boolean setLoadFile(File file){
        if(file.toString().equals("Default File"))
            return false;
        
        try {
            stub.setSyaryoObjectLoader(file);
            return true;
        } catch (RemoteException ex) {
            //ex.printStackTrace();
            return false;
        }
    }
    
    public LoadSyaryoObject getLoader(){
        try {
            return stub.getSyaryoObjectLoader();
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public static void main(String[] args) {
        /*String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            RemoteSyaryoObjectLoader stub = (RemoteSyaryoObjectLoader) registry.lookup(RemoteSyaryoObjectLoader.className);
            stub.setSyaryoObjectLoader("PC200_sv_form");
            LoadSyaryoObject response = stub.getSyaryoObjectLoader();

            System.out.println("response:" + response._syaryoMap);
        } catch (Exception e) {
            System.err.println("Client exception:" + e.toString());
            e.printStackTrace();
        }*/
        
        SyaryoObjectClient client = getInstance();
        client.setLoadFile("PC200_sv_form");
        System.out.println(client.getLoader()._syaryoMap);
    }
}
