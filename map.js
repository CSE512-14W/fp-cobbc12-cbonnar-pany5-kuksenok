var DEFAULT_ZOOM = 11;
var MAX_ZOOM = 11;
var count = 0;
var testRoute = new Array();

testRoute[0] = [[47.679114,-122.321777],
    			[47.654143,-122.320404],
				[47.621756,-122.325897],
				[47.597685,-122.33139],
				[47.571749,-122.33963],
				[47.513346,-122.33139]];
				
testRoute[1] = [-360, 60, -6000, 0, 720, -45];
    
    
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
	day.addTo(map);
	L.control.layers(baseMaps, overlayMaps).addTo(map);

	//Reference for animated markers: https://github.com/openplans/Leaflet.AnimatedMarker
	var line = L.polyline(testRoute[0]),
    animatedMarker = L.animatedMarker(line.getLatLngs(), {distance: 300, interval: 2000,});
    var latLngs = new Array();
    
    console.log(testRoute[0][0][0]);
    for (var i = 0; i<testRoute[0].length; i++){
    	
	    latLngs[i] = new L.latLng(testRoute[0][i][0], testRoute[0][i][1]);
    }
	var path = new L.Polyline(latLngs, {color: "purple", opacity: 1.0});
	map.addLayer(animatedMarker);
	animatedMarker.start();
	map.addLayer(path);
	//To convert Unix time to Date
	//new Date(1324123199*1000).toString()
	
	//Setting up a timer to draw parts of the route dynamically
	/*
	var id = setInterval(
	function (){
		var route = [];
		var lineOpacity = .5;
		var pointA = new L.latLng(testRoute[count][0], testRoute[count][1]);
		var pointB = new L.latLng(testRoute[count+1][0], testRoute[count+1][1]);
		var newLine = [pointA, pointB];
		var line = new L.Polyline(newLine, {color: "purple", opacity: lineOpacity});
		route.push(line);
		var point = new L.Circle(pointB, 100, {color: "red", opacity: lineOpacity});
		
		
		map.addLayer(line);
		map.addLayer(point);
		count = count + 1;
		if (count == testRoute.length)
			clearInterval(id);
	}
	, 1000);
	
*/
}


