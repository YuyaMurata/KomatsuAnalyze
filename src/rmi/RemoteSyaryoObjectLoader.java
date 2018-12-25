/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import obj.LoadSyaryoObject;

public interface RemoteSyaryoObjectLoader extends Remote {
    public static String className = new Object(){}.getClass().getEnclosingClass().getName();

    LoadSyaryoObject getSyaryoObjectLoader() throws RemoteException;
    
    //簡易ファイル名で読む
    void setSyaryoObjectLoader(String file) throws RemoteException;
    
    //絶対パスで読む
    void setSyaryoObjectLoader(File file) throws RemoteException;
}
