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
import file.MapToJSON;
import file.SyaryoToCompress;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 * FXML Controller class
 *
 * @author ZZ17390
 */
public class GoogleMapFXMLController implements Initializable {

	@FXML
	private GoogleMapView mapView;

	private GoogleMap map;

    private String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private String KISY = "PC138US";
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
        Map map = new MapToJSON().reader(KomatsuDataParameter.AUTH_PATH);
        System.out.println(map);

        //Proxy
        System.setProperty("https.proxyHost", ((List) map.get("proxy")).get(0).toString());
        System.setProperty("https.proxyPort", ((List) map.get("proxy")).get(1).toString());
        //mapView = new GoogleMapView("en-US", (String) map.get("apikey"));
        
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
		/*Map<String, SyaryoObject4> syaryoMap = new SyaryoToZip3().read(PATH+"syaryo_obj_"+KISY+"_form.bz2");
        
        SyaryoObject4 syaryo = syaryoMap.get("PC138US-10-40651");
 
        Map<String, List> gps = syaryo.get("KOMTRAX_GPS");
		System.out.println("GPS車両 : " + gps.size());
        //syaryo.compress(Boolean.FALSE);
        
		timePoint(syaryo.name, gps);
        */
        
		sliderInitialize();
        
        System.out.println("start");
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
	public void timePoint(String name, Map<String, List> gps) {
		System.out.println(name + ":" + gps.size());
		data = new MapPathData(name);

		for (String gpsDate : gps.keySet()) {
			if (gpsDate.contains("#")) {
				continue;
			}
			//String ymd = gpsDate.split(" ")[0].replace("/", "").substring(0, 8);
            System.out.println(gpsDate+"-"+gps.get(gpsDate));
			data.addData(Integer.valueOf(gpsDate), gps.get(gpsDate));
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
