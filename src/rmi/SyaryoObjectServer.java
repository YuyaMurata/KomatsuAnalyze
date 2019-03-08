/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.imageio.ImageIO;
import obj.LoadSyaryoObject;
import param.KomatsuDataParameter;

public class SyaryoObjectServer implements RemoteSyaryoObjectLoader {
	private static final String ICON_IMG = KomatsuDataParameter.ICON_IMG;
    private static final LoadSyaryoObject LOADER = LoadSyaryoObject.getInstance();
    private static TrayIcon icon;
    
    public SyaryoObjectServer() {
    }

    public static void main(String args[]) {
        try {
            SyaryoObjectServer obj = new SyaryoObjectServer();
            RemoteSyaryoObjectLoader stub = (RemoteSyaryoObjectLoader) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            LocateRegistry.createRegistry(1099);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(RemoteSyaryoObjectLoader.className, stub);
            
            System.err.println("Server ready");
            startTray();
            
            //Name
            LOADER.setName("Server Data");
        } catch (Exception e) {
            System.err.println("Server exception:" + e.toString());
            e.printStackTrace();
        }
    }
    
    private static void startTray() throws IOException, AWTException{
        // アイコンイメージの読み込み
        Image image = ImageIO.read(Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(ICON_IMG));
        
        // トレイアイコン生成
        icon = new TrayIcon(image, LOADER.getFilePath());
        
        // イベント登録
        
        // ポップアップメニュー
        PopupMenu menu = new PopupMenu();

        // 終了メニュー
        MenuItem exitItem = new MenuItem("終了");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        // メニューにメニューアイテムを追加
        menu.add(exitItem);
        icon.setPopupMenu(menu);

        SystemTray.getSystemTray().add(icon);

    }

    @Override
    public void setSyaryoObjectLoader(String file) throws RemoteException {
        if(LOADER.getFilePath().contains(file))
            return ;
        
        LOADER.setFile(file);
        
        System.out.println(LOADER.getFilePath());
        icon.setToolTip(LOADER.getFilePath());
    }
    
    @Override
    public void setSyaryoObjectLoader(File file) throws RemoteException {
        if(LOADER.getFilePath().contains(file.getName()))
            return ;
        
        LOADER.setFile(file);
        
        System.out.println(LOADER.getFilePath());
        icon.setToolTip(LOADER.getFilePath());
    }

    @Override
    public LoadSyaryoObject getSyaryoObjectLoader() throws RemoteException {
        if(LOADER._syaryoMap == null)
            System.err.println("Do not set file!");
        
        return LOADER;
    }

}
