/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.easy;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import json.SyaryoToZip3;

/**
 * FXML Controller class
 *
 * @author kaeru_yuya
 */
public class EasyViewerFXMLController implements Initializable {

    @FXML
    private MenuBar menu;
    @FXML
    private MenuItem import_json;
    @FXML
    private ListView<?> keylist;
    @FXML
    private Label viewarea;

    private Map syaryoMap;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        syaryoMap = new HashMap();
    }

    @FXML
    private void loadJSONFile(ActionEvent event) {
        FileChooser filechooser = new FileChooser();
        filechooser.setInitialDirectory(new File("json\\"));
        File file = filechooser.showOpenDialog(menu.getScene().getWindow());
        if (file != null) {
            viewarea.setText(file.getName());

            syaryoMap = loadSyaryoMap(file);
            updateKeyList((List) syaryoMap.keySet());
        }
    }

    private Map loadSyaryoMap(File file) {
        System.out.println(file.getName());
        return new SyaryoToZip3().read(file.getAbsolutePath());
    }

    private void updateKeyList(List keyList) {
        ObservableList list = FXCollections.observableArrayList(keyList);
        keylist.setItems(list);
    }
}
