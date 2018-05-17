/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package google.map;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapShape;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.util.Duration;
import javafx.util.StringConverter;
import json.JsonToSyaryoObj;
import obj.SyaryoObject1;

/**
 * FXML Controller class
 *
 * @author ZZ17390
 */
public class GoogleMapFXMLController implements Initializable {

	@FXML
	private GoogleMapView mapView;

	private GoogleMap map;

	private DecimalFormat formatter = new DecimalFormat("###.00000");
	private DecimalFormat dateFormatter = new DecimalFormat("00");

	@FXML
	private Slider dateSlider;
	@FXML
	private Button runBtn;
	@FXML
	private Label dateLabel;

	/**
	 * Initializes the controller class.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO
		mapView.addMapInializedListener(() -> configureMap());
		dateSlider.valueProperty().addListener((ObservableValue<? extends Number> observ, Number oldVal, Number newVal) -> {
			dateScroll(oldVal.doubleValue(), newVal.doubleValue());
		});
	}

	protected void configureMap() {
		MapOptions mapOptions = new MapOptions();

		//Create Map
		mapOptions.center(new LatLong(35.670889, 139.742127))
			.mapType(MapTypeIdEnum.ROADMAP)
			.zoom(12);
		map = mapView.createMap(mapOptions, false);

		//Get Syaryo Data
		Map<String, SyaryoObject1> syaryoMap = new JsonToSyaryoObj().reader("syaryo_obj_WA470_form.json");

		String rule = "WA470-7-10180";
		List<SyaryoObject1> syaryoList = syaryoMap.values().stream()
			.filter(s -> s.getName().contains(rule))
			.filter(s -> s.getGPS() != null)
			.collect(Collectors.toList());
		System.out.println("GPS車両 : " + syaryoList.size());

		//allPoint(syaryoList);
		timePoint(syaryoMap.get(rule));

		sliderInitialize();
	}

	public void sliderInitialize() {
		//Slider Initialize
		dateSlider.setMin(data.first());
		dateSlider.setMax(data.last());
		dateSlider.setMajorTickUnit(1);
		dateSlider.setBlockIncrement(1);
		//dateSlider.setShowTickLabels(true);
		dateSlider.setShowTickMarks(true);
		dateSlider.setSnapToTicks(true);
		dateSlider.setLabelFormatter(new StringConverter<Double>() {
			@Override
			public String toString(Double value) {
				return String.valueOf(value.intValue());
			}

			@Override
			public Double fromString(String string) {
				return null;
			}
		});
	}

	MapPathData data;

	public void timePoint(SyaryoObject1 syaryo) {
		System.out.println(syaryo.getName() + ":" + syaryo.getGPS().size());
		data = new MapPathData(syaryo.getName());

		for (String gpsDate : syaryo.getGPS().keySet()) {
			if (gpsDate.contains("#")) {
				continue;
			}
			String ymd = gpsDate.split(" ")[0].replace("/", "").substring(0, 8);

			data.addData(Integer.valueOf(ymd), (String) syaryo.getGPS().get(gpsDate).get(0));
		}

	}

	protected void dateScroll(Double oldv, Double newv) {
		String d = String.valueOf(newv.intValue());
		dateLabel.setText(d.substring(0, 4) + "/" + d.substring(4, 6) + "/" + d.substring(6, 8));

		//System.out.println("Scroll!" + newv);
		//String oy = sliderMap.floorEntry(oldv).getValue();
		if (oldMapShape != null) {
			for (MapShape old : oldMapShape) {
				//System.out.println(old);
				map.removeMapShape(old);

				if (oldMarker.size() > 3) {
					map.removeMarker(oldMarker.poll());
					oldInfoWin.poll().close();
				}
			}
			oldMapShape.clear();
		}

		marker("車両 No.1", data.getPath(newv.intValue()).get(data.getPath(newv.intValue()).size() - 1), dateLabel.getText());
		gpsPath(data.getPath(newv.intValue()), 3, data.color);
	}

	//Map process
	public void marker(String name, LatLong latLong, String content) {
		//Create GPS Marker
		MarkerOptions mopt = new MarkerOptions();
		InfoWindowOptions infoWindowOptions = new InfoWindowOptions();

		//Marker
		Marker marker = new Marker(mopt);
		marker.setPosition(latLong);
		oldMarker.add(marker);

		InfoWindow info = new InfoWindow(infoWindowOptions);
		info.setContent(name + ":" + content);
		info.open(map, marker);
		oldInfoWin.add(info);

		map.setCenter(latLong);
		map.addMarker(marker);
	}

	private List<MapShape> oldMapShape = new ArrayList<>();
	private Queue<Marker> oldMarker = new ArrayDeque<>();
	private Queue<InfoWindow> oldInfoWin = new ArrayDeque<>();
	private Timeline timer;

	public void gpsPath(List<LatLong> path, int size, String color) {
		PolylineOptions line_opt;
		Polyline line;

		line_opt = new PolylineOptions();
		line_opt.path(new MVCArray(path.toArray(new LatLong[path.size()])))
			.clickable(false)
			.draggable(false)
			.editable(false)
			.strokeColor(color)
			.strokeWeight(size)
			.visible(true);

		line = new Polyline(line_opt);
		map.addMapShape(line);
		oldMapShape.add(line);
	}

	public void gpsPoint(List<LatLong> path, int size, String color, Boolean info) {
		for (LatLong latlong : path) {
			List point = new ArrayList();
			point.add(latlong);

			gpsPath(point, size, color);
		}

		if (info) {
			marker("", path.get(0), color);
		}
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private Calendar calendar = Calendar.getInstance();

	@FXML
	private void runTime(ActionEvent event) {
		//dateSlider.setValue(dateSlider.getMin());

		//Date
		Date date = null;
		try {
			date = sdf.parse(String.valueOf(Double.valueOf(dateSlider.getValue()).intValue()));
		} catch (ParseException ex) {
		}
		calendar.setTime(date);

		timer = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String d = String.valueOf(calendar.get(Calendar.YEAR))
					+ dateFormatter.format(calendar.get(Calendar.MONTH) + 1)
					+ dateFormatter.format(calendar.get(Calendar.DAY_OF_MONTH));

				System.out.println(d);

				dateSlider.setValue(Double.valueOf(d));

				calendar.add(Calendar.DAY_OF_MONTH, 1);

				if (dateSlider.getMax() < dateSlider.getValue()) {
					timer.stop();
				}
			}
		}));
		timer.setCycleCount(Timeline.INDEFINITE);
		timer.play();
	}
}
