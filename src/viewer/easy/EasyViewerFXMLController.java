/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.easy;

import creator.template.SimpleTemplate;
import param.KomatsuDataParameter;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import json.JsonToSyaryoTemplate;
import json.SyaryoTemplateToJson;
import json.SyaryoToZip3;
//import obj.SyaryoObject3;
import obj.SyaryoObject4;
import viewer.service.ButtonService;

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
    private Label id_label;
    @FXML
    private ComboBox<String> datafilter;
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
    private String currentFile;

    @FXML
    private MenuItem smr_plot;
    @FXML
    private MenuItem smr_hist;
    @FXML
    private Accordion dataAccordion;
    @FXML
    private TextField searchBox;
    @FXML
    private Button searchBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        syaryoMap = new HashMap();
        machineListInitialize();
        dataFilterSettings();
        graphMenuSettings();
        initializeAccordion();
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
        if (index < 0) {
            return;
        }
        String name = keylist.getItems().get(index).toString();

        currentSyaryo = syaryoMap.get(name);
        index_number_label.setText(String.valueOf(index + 1));
        id_label.setText(currentSyaryo.getName());

        //データの設定
        settingData(currentSyaryo);
    }

    private void initializeAccordion() {
        TitledPane[] tps = new TitledPane[KomatsuDataParameter.DATA_ORDER.size()];
        TextArea[] tes = new TextArea[KomatsuDataParameter.DATA_ORDER.size()];

        int i = 0;
        for (String data : KomatsuDataParameter.DATA_ORDER) {
            tes[i] = new TextArea();
            tps[i] = new TitledPane(data, tes[i]);
            i++;
        }

        dataAccordion.getPanes().clear();
        dataAccordion.getPanes().addAll(tps);
    }

    //アコーディオンの設定
    private void settingData(SyaryoObject4 syaryo) {
        int i = 0;
        for (String data : KomatsuDataParameter.DATA_ORDER) {
            TitledPane title = dataAccordion.getPanes().get(i);
            String[] str;
            str = textdump(syaryo.get(title.getText().split(" ")[0].replace("×", "")));
            ((TextArea) title.getContent()).setText(str[1]);
            title.setText(check(title.getText(), str));
            i++;
        }
    }

    private String check(String title, String[] datastr) {
        if (datastr[1] == null) {
            return "×" + title.split(" ")[0].replace("×", "");
        } else {
            return title.split(" ")[0].replace("×", "") + " " + datastr[0];
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

        //Filter Initialize
        initializeFilter(true);
    }

    private Map loadSyaryoMap(File file) {
        System.out.println(file.getName());
        currentFile = file.getName();

        if (file.getName().contains(".bz2")) {
            return new SyaryoToZip3().read(file.getAbsolutePath());
        } else {
            return new SyaryoToZip3().readJSON(file.getAbsolutePath());
        }
    }

    private void updateKeyList(List keys) {
        ObservableList list = FXCollections.observableArrayList(keys);
        keylist.setItems(list);
        number_syaryo_label.setText(keylist.getItems().size() + " / " + syaryoMap.size());
    }

    private void initializeFilter(Boolean f) {
        //Select Filter
        if (f) {
            datafilter.getSelectionModel().select(0);
        }

        //Order Initialize
        order_button.setSelected(false);
        order_button.setText("N");

        //Filter Button Initialize
        filter_button.setSelected(false);
        filter_button.setText("NF");
    }

    private static Map currentFilterMap;

    @FXML
    private void selectFilter(ActionEvent event) {
        System.out.println("Selection in the listView is : " + datafilter.getValue());
        currentFilterMap = null;

        //Filter Button Initialize
        initializeFilter(false);

        //Filter Button
        filter_button.setSelected(true);
        onFilter(event);
    }

    private void selectOrder(ActionEvent event) {
        List list;
        if (datafilter.getValue().equals("ALL") || currentFilterMap == null) {
            return;
        } else if (order_button.isSelected()) {
            list = (List) currentFilterMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue().reversed())
                .map(s -> ((Map.Entry) s).getKey().toString())
                .collect(Collectors.toList());
            order_button.setText("D");
        } else {
            list = (List) currentFilterMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(s -> ((Map.Entry) s).getKey().toString())
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

    private void dataFilterSettings() {
        datafilter.getItems().add("ALL");
        datafilter.getItems().addAll(KomatsuDataParameter.DATA_ORDER);
    }

    private void graphMenuSettings() {
        graph_menu.getItems().add("SMR");
        graph_menu.getItems().add("KOMTRAX_SMR");
        graph_menu.getItems().add("KOMTRAX_ERROR");
        graph_menu.getItems().add("KOMTRAX_FUEL_CONSUME");
        graph_menu.getItems().add("KOMTRAX_GPS");
    }

    @FXML
    private void rightClickCopyList(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        StringBuilder sb = new StringBuilder();
        for (Object id : keylist.getItems()) {
            sb.append(id);
            sb.append("\n");
        }
        content.putString(sb.toString());
        clipboard.setContent(content);
    }

    @FXML
    private void onFilter(ActionEvent event) {
        filtering(datafilter.getValue(), filter_button.isSelected());
    }

    //Curent Filter
    private Map enableFilterMap;
    private Map disableFilterMap;

    private void filtering(String filter, Boolean btnEnDis) {
        if (filter.equals("ALL")) {
            updateKeyList((new ArrayList(new TreeSet(syaryoMap.keySet()))));
            return;
        }

        if (currentFilterMap == null) {
            enableFilterMap = new ConcurrentHashMap();
            disableFilterMap = new ConcurrentHashMap();
            syaryoMap.values().parallelStream()
                .forEach(s -> {
                    Map data = s.get(filter);
                    if (data != null) {
                        enableFilterMap.put(s.getName(), data.size());
                    } else {
                        disableFilterMap.put(s.getName(), 0);
                    }
                });
        }

        if (btnEnDis) {
            currentFilterMap = enableFilterMap;
            filter_button.setText("EN");
        } else {
            currentFilterMap = disableFilterMap;
            filter_button.setText("DS");
        }

        //更新処理
        updateKeyList((new ArrayList(new TreeSet(currentFilterMap.keySet()))));
    }

    @FXML
    private void onOrder(ActionEvent event) {
        selectOrder(event);
    }

    ButtonService service = new ButtonService();

    @FXML
    private void graphAction(ActionEvent event) {
        String v = graph_menu.getValue();
        if (v == null) {
            return;
        }

        System.out.println(v);
        service.setInfo(v, currentSyaryo);
        service.restart();
    }

    @FXML
    private void plotSMR(ActionEvent event) {
        keylist.getItems();
    }

    @FXML
    private void histSMR(ActionEvent event) {
        keylist.getItems();
    }

    @FXML
    private void clickSearch(ActionEvent event) {
        if (searchBox.getText().equals("")) {
            return;
        }
        
        List<String> targets;
        String[] searchWord = searchBox.getText().split(",");
        
        List<String> searchList;
        Map<String, Integer> result = new HashMap<>();
        
        searchList = Arrays.asList(searchWord).stream().filter(s -> syaryoMap.keySet().contains(s)).collect(Collectors.toList());
        if(!searchList.isEmpty()){
            updateKeyList(searchList);
            return;
        }
        
        if (datafilter.getValue().equals("ALL")) {
            targets = KomatsuDataParameter.DATA_ORDER;
            searchList = new ArrayList<>(syaryoMap.keySet());
        } else {
            targets = Arrays.asList(new String[]{datafilter.getValue()});
            searchList = new ArrayList<>(currentFilterMap.keySet());
        }

        searchList.parallelStream().forEach(s -> {
            SyaryoObject4 syaryo = syaryoMap.get(s);
            
            for (String target : targets) {
                if (syaryo.get(target) == null) {
                    continue;
                }

                syaryo.startHighPerformaceAccess();

                Map<String, Boolean> check = new HashMap<>();
                for (String w : searchWord) {
                    check.put(w, false);
                }

                //System.out.println(s);
                for (String sbn : syaryo.get(target).keySet()) {
                    List data = syaryo.get(target).get(sbn);
                    for (String w : searchWord) {
                        Boolean and = false;

                        if (sbn.split("#")[0].equals(w)) {
                            and = true;
                        } else if (data.contains(w)) {
                            and = true;
                        }

                        if (and) {
                            check.put(w, true);
                        }
                    }
                }

                if (!check.values().stream().filter(c -> !c).findFirst().isPresent()) {
                    result.put(s, 1);
                }

                syaryo.stopHighPerformaceAccess();
            }
        });

        updateKeyList(new ArrayList(new TreeSet(result.keySet())));
    }
}
