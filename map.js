var DEFAULT_ZOOM = 13;
var MAX_ZOOM = 16;
var DOT_SIZE = 10;
var markers = new Array();
var first = 0;

function initializeMap(){


	var southWest = L.latLng(47.212893,-122.821655),
	 	northEast = L.latLng(47.789027,-121.773834),
	 	bounds = L.latLngBounds(southWest, northEast);
	 	
	 var map = L.map('map', { maxBounds: bounds}).setView([47.606209, -122.332071], DEFAULT_ZOOM),
	 	 cloudmadeUrl = 'http://{s}.tile.cloudmade.com/af71292a98e943fe976cf463d4cbd82e/{styleId}@2x/256/{z}/{x}/{y}.png',
	 	 cloudmadeAttribution = 'Map data &copy; 2011 OpenStreetMap contributors, Imagery &copy; 2011 CloudMade';
	 	 
	 	
	 var day   = L.tileLayer(cloudmadeUrl, {styleId: 123649, attribution: cloudmadeAttribution, minZoom: DEFAULT_ZOOM, maxZoom: MAX_ZOOM}),
	 	 night  = L.tileLayer(cloudmadeUrl, {styleId: 33332,   attribution: cloudmadeAttribution, minZoom: DEFAULT_ZOOM, maxZoom: MAX_ZOOM}),
	 	 minimal = L.tileLayer(cloudmadeUrl, {styleId: 123647, attribution: cloudmadeAttribution, minZoom: DEFAULT_ZOOM, maxZoom: MAX_ZOOM});


	 var baseMaps = {
		 "Day": day,
		 "Night": night
	};

	var overlayMaps = {
    	"Minimal": minimal
	};
	night.addTo(map);
	L.control.layers(baseMaps, overlayMaps).addTo(map);
	
	/*map.on('zoomend', function() {
		if (map.getZoom() <= 14){
			dot_size = 60;
		}
		else if (map.getZoom() == 15) {
			dot_size = 40;
		}
		else{
			dot_size = 20;
		}
		drawDots(map);
	});*/
	
	drawDots(map);

/*
		//To convert Unix time to Date
	//new Date(1324123199*1000).toString()
	
*/
}

function drawDots(map){
	var outlineColor = 'red';
	var fillColor = '#f03';
	var opacity = 1;
	for (var i = 0; i<testRoute[0].length; i++){
		if (testRoute[1][i] < -60) {
			outlineColor = 'red';
			fillColor = '#f03';
		} else {
			outlineColor = 'green';
			fillColor = '#33FF00';
		} 
		var circle = L.circle(testRoute[0][i], DOT_SIZE, {
    		color: outlineColor,
			fillColor: fillColor,
			fillOpacity: opacity});
		circle.bindPopup("<b>Hello world!</b><br />I am a popup.");
		markers.push(circle);
	}
	var currentMarks = L.layerGroup(markers);
	currentMarks.addTo(map);
	first = 1;
}


