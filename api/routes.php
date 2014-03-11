<?php

$filter = isset($_GET['filter'])?explode(",", $_GET['filter']):array(8,16,43,48,2,10);

foreach ($filter as $key => $value) {
	if(!is_numeric($value)){
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

var myLines = [{
    "type": "LineString",
    "coordinates": [[-100, 40], [-105, 45], [-110, 55]]
}, {
    "type": "LineString",
    "coordinates": [[-105, 40], [-110, 45], [-115, 55]]
}];