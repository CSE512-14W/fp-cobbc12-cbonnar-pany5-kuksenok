var DEFAULT_ZOOM = 10;
var MIN_ZOOM = 8;
var MAX_ZOOM = 16;
var DOT_SIZE = 4;

var geoLayer = new Array();
geoLayer[1] = new Array();
geoLayer[2] = new Array();

var maps = new Array();

var dataSource = "http://anachrobot.us/bus/api/congestion.php";

var dayArgs = new Array();
dayArgs[1] = "?days=2011-12-16";
dayArgs[2] = "?days=2011-03-02";

var timeArgs = new Array();
timeArgs[1] = "&before=9:00&after:17:00";
timeArgs[2] = "&before=9:00&after:17:00";

function initializeMap(div_id, day_id){

		
	var southWest = L.latLng(46.736691,-123.261395),
	 	northEast = L.latLng(47.789027,-121.773834),
	 	bounds = L.latLngBounds(southWest, northEast);

	maps[div_id] = L.map('map'.concat(div_id), {maxBounds: bounds}).setView([47.390328,-122.279348], DEFAULT_ZOOM),
	 	 cloudmadeUrl = 'http://{s}.tile.cloudmade.com/af71292a98e943fe976cf463d4cbd82e/{styleId}@2x/256/{z}/{x}/{y}.png',
	 	 cloudmadeAttribution = 'Map data &copy; 2011 OpenStreetMap contributors, Imagery &copy; 2011 CloudMade';
	 	 
	 	
	 var light = L.tileLayer(cloudmadeUrl, {styleId: 123649, attribution: cloudmadeAttribution, minZoom: MIN_ZOOM, maxZoom: MAX_ZOOM}),
	 	 dark = L.tileLayer(cloudmadeUrl, {styleId: 33332,   attribution: cloudmadeAttribution, minZoom: MIN_ZOOM, maxZoom: MAX_ZOOM});
	 	 
	 var baseMaps = {
		 "Light": light,
		 "Dark": dark
	};

	light.addTo(maps[div_id]);
	L.control.layers(baseMaps).addTo(maps[div_id]);
	
	plotPoints(maps[div_id], div_id);
}

function changeDay(div_id, day_id) {
	if (day_id == 0)
		dayArgs[div_id] = "?days=2011-12-16";
	else if (day_id == 1)	
		dayArgs[div_id] = "?days=2011-12-17";
	else if (day_id == 2)	
		dayArgs[div_id] = "?days=2012-12-18";
	else if (day_id == 3)	
		dayArgs[div_id] = "?days=2012-03-02";
	else if (day_id == 4)	
		dayArgs[div_id] = "?days=2012-03-03";
	else
		dayArgs[div_id] = "?days=2012-03-04";
	
	removeDots(maps[div_id], div_id);
	plotPoints(maps[div_id], div_id);
}

function plotPoints(map, div_id) {
	var outlineColor = 'red';
	var fillColor = '#f03';
	var opacity = .8;
	
	var geoData = [];
	var url = dataSource + dayArgs[div_id] + timeArgs[div_id];
	console.log(url);
	map.spin(true);
	$.getJSON(url, function (data) {
		geoData = data;
   	 	}).done(function () {
   	 		map.spin(false);
	   	 	geoLayer[div_id] = L.geoJson(geoData, {
		   	 	pointToLayer: function (feature, latlng) {
		   	 		if (parseInt(feature.properties.avg_deviation)>300){
			   	 		outlineColor = '#990000';
			   	 		fillColor = '#CC0000';
			   	 	} else if (parseInt(feature.properties.avg_deviation)>60){
			   	 		outlineColor = '#FF00003';
			   	 		fillColor = 'red';
		   	 		} else if (parseInt(feature.properties.avg_deviation)==0) {
			   	 		outlineColor = 'black';
			   	 		fillColor = '#B0B0B0';
		   	 		} else if (parseInt(feature.properties.avg_deviation)>=-60){
			   	 		outlineColor = 'green';
			   	 		fillColor = '#33FF00';
		   	 		} else {
			   	 		outlineColor = 'blue';
			   	 		fillColor = "#3399FF";
		   	 		}
			   	 	return L.circleMarker(feature.geometry.coordinates,
			   	 							{radius: DOT_SIZE,
			   	 							color: outlineColor,
				   	 						fillColor: fillColor,
				   	 						fillOpacity: opacity});
				},
			   	onEachFeature: function (feature, layer) {
				   	var popupContent;
					if (feature.properties) {
						var dev = parseInt(feature.properties.avg_deviation);
						var s;
						if (dev < 0) 
							s = Math.abs(dev) + "s early";
						else if( dev == 0) 
							s = "unknown";
						else
							s = dev + "s delay";
					
						popupContent = "<b>Avg. Deviation: </b>" + s;
						}
					layer.bindPopup(popupContent);

					layer.on('mouseover', function(e) {
						layer.openPopup();
					});
				}
			});
			geoLayer[div_id].addTo(maps[div_id]);
	   	 });
}

function removeDots(map, div_id){
	map.removeLayer(geoLayer[div_id]);
	geoLayer[div_id] = [];
}

function filterByTime(time_start_hr, time_start_min, time_end_hr, time_end_min, div_id){
	if (time_start_min < 10)
		time_start_min = '0'.concat(time_start_min.toString());
	if (time_end_min < 10)
		time_end_min = '0'.concat(time_end_min.toString());
		
	timeArgs[div_id] = "&before=" + time_end_hr + ":" + time_end_min + "&after=" + time_start_hr + ":" + time_start_min;

	removeDots(maps[div_id], div_id);
	plotPoints(maps[div_id], div_id);
}



