<?php

$before = isset($_GET['before'])?$_GET['before']:null;
$after = isset($_GET['after'])?$_GET['after']:null;
$days = isset($_GET['days'])?$_GET['days']:null;
if ($days==null && isset($_GET['cut'])){
	$days = $_GET['cut']=="pre"?"2011-12-17,2011-12-21,2011-12-18,2011-12-16":"2012-03-02,2012-03-03,2012-03-04,2012-03-07";
}

$valid = explode(",", "2011-12-17,2011-12-21,2011-12-18,2011-12-16,2012-03-02,2012-03-03,2012-03-04,2012-03-07");

$days = explode(",", $days);

foreach ($days as $key => $value) {
	if(!in_array($value, $valid)){
		unset($filter[$key]);
	}
}

$con = mysqli_connect("localhost","root","","bus2");

if (mysqli_connect_errno($con))
  {
  echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }

$q = "SELECT lat, lon, count(*) FROM schedule, stops, route, trips ".
	 "WHERE schedule.stop_id = stops.stop_id AND trips.trip_id = schedule.trip_id AND route.route_short_name IN (\"".
	 	implode("\",\"",$filter) ."\") AND trips.trip_id=route.route_id GROUP BY stops.stop_id";

echo $q;

print_r(mysqli_query($con, $q));

mysqli_close($con);
?>

var geojsonFeature = {
    "type": "Feature",
    "properties": {
        "name": "Coors Field",
        "amenity": "Baseball Stadium",
        "popupContent": "This is where the Rockies play!"
    },
    "geometry": {
        "type": "Point",
        "coordinates": [-104.99404, 39.75621]
    }
};