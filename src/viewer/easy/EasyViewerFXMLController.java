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
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import json.SyaryoToZip3;
import obj.SyaryoObject3;

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
    private Accordion viewarea;

    private Map syaryoMap;
    @FXML
    private TitledPane spec;
    @FXML
    private TextArea viewarea_spec;
    @FXML
    private TitledPane detail;
    @FXML
    private TextArea viewarea_detail;
    @FXML
    private TitledPane product;
    @FXML
    private TextArea viewarea_product;
    @FXML
    private TitledPane deploy;
    @FXML
    private TextArea viewarea_deploy;
    @FXML
    private TitledPane dead;
    @FXML
    private TextArea viewarea_dead;
    @FXML
    private TitledPane news;
    @FXML
    private TextArea viewarea_news;
    @FXML
    private TitledPane used;
    @FXML
    private TextArea viewarea_used;
    @FXML
    private TitledPane owner;
    @FXML
    private TextArea viewarea_owner;
    @FXML
    private TitledPane care;
    @FXML
    private TextArea viewarea_care;
    @FXML
    private TitledPane support;
    @FXML
    private TextArea viewarea_support;
    @FXML
    private TitledPane order;
    @FXML
    private TextArea viewarea_order;
    @FXML
    private TitledPane work;
    @FXML
    private TextArea viewarea_work;
    @FXML
    private TitledPane parts;
    @FXML
    private TextArea viewarea_parts;
    @FXML
    private TitledPane smr;
    @FXML
    private TextArea viewarea_smr;
    @FXML
    private TitledPane kmsmr;
    @FXML
    private TextArea viewarea_kmsmr;
    @FXML
    private TitledPane kmgps;
    @FXML
    private TextArea viewarea_kmgps;
    @FXML
    private TitledPane kmerror;
    @FXML
    private TextArea viewarea_kmerror;
    @FXML
    private Label id_label;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        syaryoMap = new HashMap();
        machineListInitialize();
    }
    
    public void machineListInitialize() {
        //Event
        keylist.getSelectionModel().selectedIndexProperty().addListener(
            (ov, old, current) -> {
                // リスト・ビュー内の選択項目を出力
                System.out.println(ov);
                machineHistorySelected(current.intValue());
            }
        );
    }
    
    //Selected Machine
    public void machineHistorySelected(Integer index) {
        System.out.println("Selection in the listView is : " + index);
        String name = keylist.getItems().get(index).toString();
        SyaryoObject3 syaryo = (SyaryoObject3) syaryoMap.get(name);
        id_label.setText(name);
        
        //データの設定
        settingData(syaryo);
    }

    private void settingData(SyaryoObject3 syaryo){
        syaryo.decompress();
        
        String str;
        viewarea_spec.setText(str = textdump(syaryo.get(spec.getText().replace("×", ""))));
        spec.setText(check(spec.getText(), str));
        
        viewarea_detail.setText(str = textdump(syaryo.get(detail.getText().replace("×", ""))));
        detail.setText(check(detail.getText(), str));
        
        viewarea_product.setText(str = textdump(syaryo.get(product.getText().replace("×", ""))));
        product.setText(check(product.getText(), str));
        
        viewarea_deploy.setText(str = textdump(syaryo.get(deploy.getText().replace("×", ""))));
        deploy.setText(check(deploy.getText(), str));
        
        viewarea_dead.setText(str = textdump(syaryo.get(dead.getText().replace("×", ""))));
        dead.setText(check(dead.getText(), str));
        
        viewarea_news.setText(str = textdump(syaryo.get(news.getText().replace("×", ""))));
        news.setText(check(news.getText(), str));
        
        viewarea_used.setText(str = textdump(syaryo.get(used.getText().replace("×", ""))));
        used.setText(check(used.getText(), str));
        
        viewarea_owner.setText(str = textdump(syaryo.get(owner.getText().replace("×", ""))));
        owner.setText(check(owner.getText(), str));
        
        viewarea_care.setText(str = textdump(syaryo.get("コマツケア前受け金")));
        viewarea_care.setText(str = viewarea_care.getText()+"\n\n"+textdump(syaryo.get(care.getText().replace("×", ""))));
        care.setText(check(care.getText(), str));
        
        viewarea_support.setText(str = textdump(syaryo.get(support.getText().replace("×", ""))));
        support.setText(check(support.getText(), str));
        
        viewarea_order.setText(str = textdump(syaryo.get(order.getText().replace("×", ""))));
        order.setText(check(order.getText(), str));
        
        viewarea_work.setText(str = textdump(syaryo.get(work.getText().replace("×", ""))));
        work.setText(check(work.getText(), str));
        
        viewarea_parts.setText(str = textdump(syaryo.get(parts.getText().replace("×", ""))));
        parts.setText(check(parts.getText(), str));
        
        viewarea_smr.setText(str = textdump(syaryo.get(smr.getText().replace("×", ""))));
        smr.setText(check(smr.getText(), str));
        
        viewarea_kmsmr.setText(str = textdump(syaryo.get("KMSMR")));
        kmsmr.setText(check(kmsmr.getText(), str));
        
        viewarea_kmgps.setText(str = textdump(syaryo.get("KMGPS")));
        kmgps.setText(check(kmgps.getText(), str));
        
        viewarea_kmerror.setText(str = textdump(syaryo.get("KMERROR")));
        kmerror.setText(check(kmerror.getText(), str));
        
        syaryo.compress(true);
    }
    
    private String check(String title, String datastr){
        if(datastr.contains("None"))
            return "×"+title.replace("×", "");
        else
            return title.replace("×", "");
    }
    
    private String textdump(Map<String, List> m){
        if(m == null)
            return "None";
        StringBuilder sb = new StringBuilder();
        sb.append("データ数:");
        sb.append(m.size());
        sb.append("\n\n");
        
        for(String d : m.keySet()){
            sb.append(d);
            sb.append(m.get(d));
            sb.append("\n");
        }
        

        return sb.toString();
    }

    @FXML
    private void loadJSONFile(ActionEvent event) {
        FileChooser filechooser = new FileChooser();
        filechooser.setInitialDirectory(new File("json\\"));
        File file = filechooser.showOpenDialog(menu.getScene().getWindow());
        if (file != null) {
            id_label.setText(file.getName());
            syaryoMap = loadSyaryoMap(file);
            updateKeyList((List) syaryoMap.keySet().stream().collect(Collectors.toList()));
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
