/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.easy;

import command.py.PythonCommand;
import creator.create.KomatsuDataParameter;
import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import json.SyaryoToZip3;
//import obj.SyaryoObject3;
import obj.SyaryoObject4;

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

    private Map<String, SyaryoObject4> syaryoMap;
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
    @FXML
    private ComboBox<String> datafilter;
    @FXML
    private TitledPane kmact;
    @FXML
    private TextArea viewarea_kmact;
    @FXML
    private TitledPane kmfuel;
    @FXML
    private TextArea viewarea_kmfuel;
    @FXML
    private MenuItem rightClick_copy;
    @FXML
    private Label number_syaryo_label;
    @FXML
    private ToggleButton filter_button;
    @FXML
    private ToggleButton order_button;
    @FXML
    private Label index_number_label;
    @FXML
    private Button graph_button;
    @FXML
    private ComboBox<String> graph_menu;
    
    private SyaryoObject4 currentSyaryo;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        syaryoMap = new HashMap();
        machineListInitialize();
        dataFilterSettings(KomatsuDataParameter.SETTING_DATAFILETER_PATH);
        graphMenuSettings();
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
        if(index < 0)
            return;
        String name = keylist.getItems().get(index).toString();
        currentSyaryo = (SyaryoObject4) syaryoMap.get(name);
        index_number_label.setText(String.valueOf(index+1));
        id_label.setText(name);

        //データの設定
        settingData(currentSyaryo);
    }

    //アコーディオンの設定
    private void settingData(SyaryoObject4 syaryo) {
        syaryo.decompress();

        String[] str;
        str = textdump(syaryo.get(spec.getText().split(" ")[0].replace("×", "")));
        viewarea_spec.setText(str[1]);
        spec.setText(check(spec.getText(), str));

        str = textdump(syaryo.get(detail.getText().split(" ")[0].replace("×", "")));
        viewarea_detail.setText(str[1]);
        detail.setText(check(detail.getText(), str));

        str = textdump(syaryo.get(product.getText().split(" ")[0].replace("×", "")));
        viewarea_product.setText(str[1]);
        product.setText(check(product.getText(), str));

        str = textdump(syaryo.get(deploy.getText().split(" ")[0].replace("×", "")));
        viewarea_deploy.setText(str[1]);
        deploy.setText(check(deploy.getText(), str));

        str = textdump(syaryo.get(dead.getText().split(" ")[0].replace("×", "")));
        viewarea_dead.setText(str[1]);
        dead.setText(check(dead.getText(), str));

        str = textdump(syaryo.get(news.getText().split(" ")[0].replace("×", "")));
        viewarea_news.setText(str[1]);
        news.setText(check(news.getText(), str));

        str = textdump(syaryo.get(used.getText().split(" ")[0].replace("×", "")));
        viewarea_used.setText(str[1]);
        used.setText(check(used.getText(), str));

        str = textdump(syaryo.get(owner.getText().split(" ")[0].replace("×", "")));
        viewarea_owner.setText(str[1]);
        owner.setText(check(owner.getText(), str));

        String[] str2 = textdump(syaryo.get("コマツケア前受け金"));
        str = textdump(syaryo.get(care.getText().split(" ")[0].replace("×", "")));
        viewarea_care.setText(str2[1] + "\n\n" + str[1]);
        care.setText(check(care.getText(), str));
        
        str = textdump(syaryo.get(support.getText().split(" ")[0].replace("×", "")));
        viewarea_support.setText(str[1]);
        support.setText(check(support.getText(), str));

        str = textdump(syaryo.get(order.getText().split(" ")[0].replace("×", "")));
        viewarea_order.setText(str[1]);
        order.setText(check(order.getText(), str));

        str = textdump(syaryo.get(work.getText().split(" ")[0].replace("×", "")));
        viewarea_work.setText(str[1]);
        work.setText(check(work.getText(), str));

        str = textdump(syaryo.get(parts.getText().split(" ")[0].replace("×", "")));
        viewarea_parts.setText(str[1]);
        parts.setText(check(parts.getText(), str));

        str = textdump(syaryo.get(smr.getText().split(" ")[0].replace("×", "")));
        viewarea_smr.setText(str[1]);
        smr.setText(check(smr.getText(), str));

        str = textdump(syaryo.get(kmsmr.getText().split(" ")[0].replace("×", "")));
        viewarea_kmsmr.setText(str[1]);
        kmsmr.setText(check(kmsmr.getText(), str));

        str = textdump(syaryo.get(kmgps.getText().split(" ")[0].replace("×", "")));
        viewarea_kmgps.setText(str[1]);
        kmgps.setText(check(kmgps.getText(), str));

        str = textdump(syaryo.get(kmerror.getText().split(" ")[0].replace("×", "")));
        viewarea_kmerror.setText(str[1]);
        kmerror.setText(check(kmerror.getText(), str));

        str = textdump(syaryo.get(kmact.getText().split(" ")[0].replace("×", "")));
        viewarea_kmact.setText(str[1]);
        kmact.setText(check(kmact.getText(), str));

        str = textdump(syaryo.get(kmfuel.getText().split(" ")[0].replace("×", "")));
        viewarea_kmfuel.setText(str[1]);
        kmfuel.setText(check(kmfuel.getText(), str));

        syaryo.compress(true);
    }

    private String check(String title, String[] datastr) {
        if (datastr[1] == null) {
            return "×" + title.split(" ")[0].replace("×", "");
        } else {
            return title.split(" ")[0].replace("×", "")+" "+datastr[0];
        }
    }

    private String[] textdump(Map<String, List> m) {
        if (m == null) {
            return new String[]{"", null};
        }
        
        StringBuilder sb = new StringBuilder();
        for (String d : m.keySet()) {
            sb.append(d);
            sb.append(m.get(d));
            sb.append("\n");
        }

        return new String[]{String.valueOf(m.size()), sb.toString()};
    }

    @FXML
    private void loadJSONFile(ActionEvent event) {
        FileChooser filechooser = new FileChooser();
        filechooser.setInitialDirectory(new File(KomatsuDataParameter.SYARYOOBJECT_FDPATH));
        File file = filechooser.showOpenDialog(menu.getScene().getWindow());
        if (file != null) {
            id_label.setText(file.getName());
            syaryoMap = loadSyaryoMap(file);
            updateKeyList(new ArrayList(new TreeSet(syaryoMap.keySet())));
        }
    }

    private Map loadSyaryoMap(File file) {
        System.out.println(file.getName());
        return new SyaryoToZip3().read(file.getAbsolutePath());
    }

    private void updateKeyList(List keys) {
        ObservableList list = FXCollections.observableArrayList(keys);
        keylist.setItems(list);
        number_syaryo_label.setText(keylist.getItems().size()+" / "+syaryoMap.size());
    }

    private static Map currentFilterMap;
    @FXML
    private void selectFilter(ActionEvent event) {
        System.out.println("Selection in the listView is : " + datafilter.getValue());
        String filter = datafilter.getValue();
        
        final Map<String, Object> datafilterList = new ConcurrentHashMap();
        if(filter.equals("ALL")){
            datafilterList.putAll(syaryoMap);
        }
        else if(filter_button.isSelected()){
            syaryoMap.values().parallelStream()
                .forEach(s ->{
                    s.decompress();
                    if(s.get(filter) != null)
                        datafilterList.put(s.getName(), s.get(filter).size());
                    s.compress(true);
                });
            filter_button.setText("EF");
        }else{
            syaryoMap.values().parallelStream()
                .forEach(s ->{
                    s.decompress();
                    if(s.get(filter) == null)
                        datafilterList.put(s.getName(), 0);
                    s.compress(true);
                });
            filter_button.setText("DF");
        }
        
        //更新処理
        if(keylist.getItems() != datafilterList)
            updateKeyList((new ArrayList(new TreeSet(datafilterList.keySet()))));
        
        currentFilterMap = datafilterList;
    }
    
    private void selectOrder(ActionEvent event) {
        List list;
        if(datafilter.getValue().equals("ALL") || currentFilterMap == null)
            return ;
        else if(order_button.isSelected()){
            list = (List) currentFilterMap.entrySet().stream()
                                    .sorted(Map.Entry.comparingByValue().reversed())
                                    .map(s -> ((Map.Entry)s).getKey().toString())
                                    .collect(Collectors.toList());
            order_button.setText("D");
        }else{
            list = (List) currentFilterMap.entrySet().stream()
                                    .sorted(Map.Entry.comparingByValue())
                                    .map(s -> ((Map.Entry)s).getKey().toString())
                                    .collect(Collectors.toList());
            order_button.setText("A");
        }
        //更新処理
        updateKeyList(list);
    }

    @FXML
    private void rightClickCopy(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(id_label.getText());
        clipboard.setContent(content);
    }
    
    private void dataFilterSettings(String file){
        List<String> list = new ArrayList<>();
        try(BufferedReader br = CSVFileReadWrite.reader(file)){
            //header
            String line = br.readLine();
            
            while((line = br.readLine()) != null){
                if(line.contains("ALL"))
                    list.add(line);
                else
                    list.add(line);
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        datafilter.getItems().addAll(list);
    }
    
    private void graphMenuSettings(){
        
        graph_menu.getItems().add("SMR");
    }

    @FXML
    private void rightClickCopyList(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        StringBuilder sb = new StringBuilder();
        for(Object id : keylist.getItems()){
            sb.append(id);
            sb.append("\n");
        }
        content.putString(sb.toString());
        clipboard.setContent(content);
    }

    @FXML
    private void onFilter(ActionEvent event) {
        selectFilter(event);
    }

    @FXML
    private void onOrder(ActionEvent event) {
        selectOrder(event);
    }

    @FXML
    private void graphAction(ActionEvent event) {
        String v = graph_menu.getValue();
        if(v == null)
            return ;
        
        System.out.println(v);
        if(currentSyaryo == null)
            return ;
        currentSyaryo.decompress();
        Map<String, List> map = currentSyaryo.get(v);
        currentSyaryo.compress(true);
        if(map == null)
            return ;
                
        //CSVFile 生成
        try(PrintWriter csv = CSVFileReadWrite.writer(KomatsuDataParameter.GRAPH_TEMP_FILE)){
            csv.println("Date,SMR");
            for(String date : map.keySet()){
                csv.println(date+","+map.get(date).get(2));
            }
        }
        
        //Graph Python 実行
        PythonCommand.py(KomatsuDataParameter.GRAPH_PY, KomatsuDataParameter.GRAPH_TEMP_FILE);
    }
}
