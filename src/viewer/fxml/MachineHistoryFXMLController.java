package viewer.fxml;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import json.JsonToSyaryoObj;
import obj.Element;
import obj.SyaryoElements;
import obj.SyaryoObject;

/**
 *
 * @author kaeru
 */
public class MachineHistoryFXMLController implements Initializable {

	@FXML
	private VBox root_vbox;
	@FXML
	private MenuBar fileMenu;
	@FXML
	private SplitPane history_splitpane;
	@FXML
	private ListView<?> machineList;

	private Map<String, SyaryoObject> syaryoMap = new HashMap();
	@FXML
	private Label history_label;
	@FXML
	private ListView<Item> pList;
	@FXML
	private ListView<Item> cList;
	private Map<String, ObservableList<Item>> cMap;
	@FXML
	private TableView<TableColumn> sampleView;
	@FXML
	private Button applyButton;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO
		//File Read
		String fileName = "json\\syaryo_obj_WA470_form.json";
		syaryoMap = new JsonToSyaryoObj().reader(fileName);

		//ListViewSettings
		cMap = new HashMap<>();
		for (String prefName : SyaryoElements.list) {
			//parent
			Item pref = new Item(0, prefName, false);
			/*pref.onProperty().addListener((ov, old_val, new_val) -> {
				elementChecked(pref.getName(), new_val);
			});*/
			pList.getItems().add(pref);
			
			//child
			ListView<Item> list = new ListView() ;
			for(Element e : SyaryoElements.map.get(prefName)){
				if(e.getText().contains("format")) continue;
				
				Item pref2 = new Item(e.getNo(), e.getText(), false);
				/*pref2.onProperty().addListener((ov, old_val, new_val) -> {
					System.out.println(pref2.getIndex()+":"+pref2.getName());
				});*/
				list.getItems().add(pref2);
			}
			cMap.put(prefName, list.getItems());
		}
		pList.setCellFactory(CheckBoxListCell.forListView((Item item) -> item.onProperty()));
		
		//Add ListView
		ObservableList machines = machineList.getItems();

		int i = 0;
		for (Object name : syaryoMap.keySet()) {
			machines.add((++i) + " : " + name);
		}

		//Event
		machineList.getSelectionModel().selectedIndexProperty().addListener(
			(ov, old, current) -> {
				// リスト・ビュー内の選択項目を出力
				machineHistorySelected();
			}
		);
		
		pList.getSelectionModel().selectedIndexProperty().addListener(
			(ov, old, current) -> {
				// リスト・ビュー内の選択項目を出力
				elementSelected();
			}
		);
	}

	//Selected Machine
	public void machineHistorySelected() {
		int index = machineList.getSelectionModel().getSelectedIndex();
		System.out.println("Selection in the listView is : " + index);
		String name = machineList.getItems().get(index).toString().split(" : ")[1];
		history_label.setText(syaryoMap.get(name).dump());

	}
	
	//Selected SyaryoElements
	public void elementSelected() {
		int index = pList.getSelectionModel().getSelectedIndex();
		String name = pList.getItems().get(index).toString();
		
		cList.setItems(cMap.get(name));
		cList.setCellFactory(CheckBoxListCell.forListView((Item item) -> item.onProperty()));
	}

	@FXML
	private void applyAction(ActionEvent event) {
		System.out.println("Apply!");
		
		sampleView.getColumns().clear();
		sampleView.getColumns().add(new TableColumn("機種・型・機番"));
		
		for(Item item : pList.getItems()){
			System.out.println(item.getName());
			for(Item item2 : cMap.get(item.getName())){
				//System.out.print("("+item2.getIndex()+","+item2.name+","+item2.isOn()+"),");
				if(item2.isOn())
					sampleView.getColumns().add(new TableColumn(item.getName()+"."+item2.getName()));
			}
			System.out.println("");
		}
	}

	static class Item {

		private final StringProperty name = new SimpleStringProperty();
		private final BooleanProperty on = new SimpleBooleanProperty();
		private final Integer index;

		public Item(Integer index, String name, boolean on) {
			this.index = index;
			setName(name);
			setOn(on);
		}

		public final StringProperty nameProperty() {
			return this.name;
		}

		public final Integer getIndex() {
			return this.index;
		}
		
		public final String getName() {
			return this.nameProperty().get();
		}

		public final void setName(final String name) {
			this.nameProperty().set(name);
		}

		public final BooleanProperty onProperty() {
			return this.on;
		}

		public final boolean isOn() {
			return this.onProperty().get();
		}

		public final void setOn(final boolean on) {
			this.onProperty().set(on);
		}

		@Override
		public String toString() {
			return getName();
		}

	}
}
