var DEFAULT_ZOOM = 12;
var MIN_ZOOM = 10;
var MAX_ZOOM = 16;
var DOT_SIZE = 40;
var markers = new Array();
markers[1] = new Array();
markers[2] = new Array();


var maps = new Array();

var dayMap1 = 1;
var dayMap2 = 2;
var timeStart = new Array();
var timeEnd = new Array();
timeStart[1] = 9 * 60;
timeEnd[1] = 17 * 60;
timeStart[2] = 9 * 60;
timeEnd[2] = 17 * 60;

function initializeMap(div_id, day_id){
	
	var southWest = L.latLng(47.212893,-122.821655),
	 	northEast = L.latLng(47.789027,-121.773834),
	 	bounds = L.latLngBounds(southWest, northEast);

	maps[div_id] = L.map('map'.concat(div_id), { maxBounds: bounds}).setView([47.606209, -122.332071], DEFAULT_ZOOM),
	 	 cloudmadeUrl = 'http://{s}.tile.cloudmade.com/af71292a98e943fe976cf463d4cbd82e/{styleId}@2x/256/{z}/{x}/{y}.png',
	 	 cloudmadeAttribution = 'Map data &copy; 2011 OpenStreetMap contributors, Imagery &copy; 2011 CloudMade';
	 	 
	 	
	 var day   = L.tileLayer(cloudmadeUrl, {styleId: 123649, attribution: cloudmadeAttribution, minZoom: MIN_ZOOM, maxZoom: MAX_ZOOM}),
	 	 night  = L.tileLayer(cloudmadeUrl, {styleId: 33332,   attribution: cloudmadeAttribution, minZoom: MIN_ZOOM, maxZoom: MAX_ZOOM}),
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
	
	drawDots(maps[div_id], div_id, day_id);
	
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
		removeDots(maps[div_id], div_id);
		drawDots(maps[div_id], div_id, day_id);
	});
}

function drawDots(map, div_id, day_id){
	var outlineColor = 'red';
	var fillColor = '#f03';
	var opacity = 1;
	var count = 0;
	for (var i = 0; i<data[day_id][0].length; i++){
		if ((data[day_id][2][i][0] * 60 + data[day_id][2][i][1]) < timeStart[div_id] || (data[day_id][2][i][0] * 60 + data[day_id][2][i][1]) > timeEnd[div_id]) continue;

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

function removeDots(map, div_id){
	for (var i = 0; i < markers[div_id].length; i++) {
		map.removeLayer(markers[div_id][i]);
	}
	markers[div_id] = [];
}

function changeDay(div_id, day_id){
	removeDots(maps[div_id], div_id);
	drawDots(maps[div_id], div_id, day_id);
	if (div_id == 1)
		dayMap1 = day_id;
	else
		dayMap2 = day_id;
}

function filterByTime(time_start_hr, time_start_min, time_end_hr, time_end_min, div_id){
	timeStart[div_id] = time_start_hr * 60 + time_start_min;
	timeEnd[div_id] = time_end_hr * 60 + time_end_min;
	
	removeDots(maps[div_id], div_id);
	var day_id;
	if (div_id == 1) {
		day_id = dayMap1;
	}
	else {
		day_id = dayMap2;
	}
	drawDots(maps[div_id], div_id, day_id);

}
