var DEFAULT_ZOOM = 12;
var MIN_ZOOM = 10;
var MAX_ZOOM = 16;
var DOT_SIZE = 10;

var geoLayer;
var maps = new Array();


var timeStart = new Array();
var timeEnd = new Array();
timeStart[1] = 9 * 60;
timeEnd[1] = 17 * 60;


var dataSource = dataSource = "http://anachrobot.us/bus/api/congestion.php?days=2011-12-21";


function initializeMap(div_id, day_id){

		
	var southWest = L.latLng(47.212893,-122.821655),
	 	northEast = L.latLng(47.789027,-121.773834),
	 	bounds = L.latLngBounds(southWest, northEast);

	maps[div_id] = L.map('map'.concat(div_id)).setView([47.64513397,-122.1076889], DEFAULT_ZOOM),
	 	 cloudmadeUrl = 'http://{s}.tile.cloudmade.com/af71292a98e943fe976cf463d4cbd82e/{styleId}@2x/256/{z}/{x}/{y}.png',
	 	 cloudmadeAttribution = 'Map data &copy; 2011 OpenStreetMap contributors, Imagery &copy; 2011 CloudMade';
	 	 
	 	
	 var day = L.tileLayer(cloudmadeUrl, {styleId: 123649, attribution: cloudmadeAttribution, minZoom: MIN_ZOOM, maxZoom: MAX_ZOOM}),
	 	 night = L.tileLayer(cloudmadeUrl, {styleId: 33332,   attribution: cloudmadeAttribution, minZoom: MIN_ZOOM, maxZoom: MAX_ZOOM}),
	 	 minimal = L.tileLayer(cloudmadeUrl, {styleId: 123647, attribution: cloudmadeAttribution, minZoom: MIN_ZOOM, maxZoom: MAX_ZOOM});


	 var baseMaps = {
		 "Day": day,
		 "Night": night
	};

	var overlayMaps = {
    	"Minimal": minimal
	};
	night.addTo(maps[div_id]);
	L.control.layers(baseMaps, overlayMaps).addTo(maps[div_id]);
	
	plotPoints(maps[div_id], div_id);
	
	
	/*
	maps[div_id].on('zoomstart', function() {
		if (maps[div_id].getZoom() <= 12){
			DOT_SIZE = 40;
		}
		else if (maps[div_id].getZoom() <= 14) {
			DOT_SIZE = 20;
		}
		else{
			DOT_SIZE = 10;
		}
		*/
	
}

function changeDay(div_id, day_id) {
	if (day_id == 0)
		dataSource = "http://anachrobot.us/bus/api/congestion.php?days=2011-12-18";
	else if (day_id == 1)	
		dataSource = "http://anachrobot.us/bus/api/congestion.php?days=2011-12-21";
	else if (day_id == 2)	
		dataSource = "http://anachrobot.us/bus/api/congestion.php?days=2012-03-02";
	else if (day_id == 3)	
		dataSource = "http://anachrobot.us/bus/api/congestion.php?days=2012-03-03";
	else if (day_id == 4)	
		dataSource = "http://anachrobot.us/bus/api/congestion.php?days=2012-03-04";
	else
		dataSource = "http://anachrobot.us/bus/api/congestion.php?days=2012-03-07";
	removeDots(maps[div_id]);
	plotPoints(maps[div_id], div_id);
}

function plotPoints(map, div_id) {
	var outlineColor = 'red';
	var fillColor = '#f03';
	var opacity = 1;
	
	var geoData = [];
	$.getJSON(dataSource, function (data) {
		geoData = data;
   	 	}).complete(function () {
	   	 	geoLayer = L.geoJson(geoData, {
		   	 	pointToLayer: function (feature, latlng) {
		   	 		if (parseInt(feature.properties.avg_deviation)>120){
			   	 		outlineColor = 'red';
			   	 		fillColor = '#f03';
		   	 		} else if (parseInt(feature.properties.avg_deviation)>0) {
			   	 		outlineColor = 'yellow';
			   	 		fillColor = '#FFFF33';
		   	 		} else {
			   	 		outlineColor = 'green';
			   	 		fillColor = '#33FF00';
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
							s = "0s delay";
						else
							s = dev + "s delay";
					
						popupContent = "<b>Avg. Deviation: </b>" + s + "<br>" +
						"<b>Data Points: </b>" + feature.properties.data_points;
					}

					layer.bindPopup(popupContent);
				}
			});
			geoLayer.addTo(maps[div_id]);
	   	 });

}

function removeDots(map){
	map.removeLayer(geoLayer);
	geoLayer = [];
}

function filterByTime(time_start_hr, time_start_min, time_end_hr, time_end_min, div_id){
	timeStart[div_id] = time_start_hr * 60 + time_start_min;
	timeEnd[div_id] = time_end_hr * 60 + time_end_min;
	
	removeDots(maps[div_id]);
	plotPoints(maps[div_id], div_id);
}

//---------------------------------------
// NOTE: ALL FUNCTIONS BELOW ARE OLD, DO
// NOT USE THEM
//---------------------------------------
/*
function drawDots(map, div_id, day_id){
	var outlineColor = 'red';
	var fillColor = '#f03';
	var opacity = 1;
	var count = 0;
	for (var i = 0; i<data[day_id][0].length; i++){
		if ((data[day_id][2][i][0] * 60 + data[day_id][2][i][1]) < timeStart[div_id] || (data[day_id][2][i][0] * 60 + 		data[day_id][2][i][1]) > timeEnd[div_id]) continue;

		if (data[day_id][1][i] < -120) {
			outlineColor = 'red';
			fillColor = '#f03';
		} else if(data[day_id][1][i] <=0) {
			outlineColor = 'yellow';
			fillColor = '#FFFF33';
		} else {
			outlineColor = 'green';
			fillColor = '#33FF00';
		} 
		markers[div_id][count] = L.circle(data[day_id][0][i], DOT_SIZE, {
    		color: outlineColor,
			fillColor: fillColor,
			fillOpacity: opacity});
		var s = "<b>Route: </b>" + data[day_id][3][i].toString() + "<br>"
		+ "<b>Deviation: </b>" + data[day_id][1][i].toString() + "s<br>"
		+ "<b>Time: </b>" + data[day_id][2][i][0] + ":" + data[day_id][2][i][1];
		markers[div_id][count].bindPopup(s);
		map.addLayer(markers[div_id][count]);
		count+=1;
	}
}



function changeDay2(div_id, day_id){
	removeDots(maps[div_id], div_id);
	drawDots(maps[div_id], div_id, day_id);
	if (div_id == 1)
		dayMap1 = day_id;
	else
		dayMap2 = day_id;
}
*/


