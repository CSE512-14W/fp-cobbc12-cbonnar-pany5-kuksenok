<?php

$before = isset($_GET['before'])?$_GET['before']:"23:59";
$after = isset($_GET['after'])?$_GET['after']:"00:00";
$days = isset($_GET['days'])?$_GET['days']:null;
if ($days==null && isset($_GET['cut'])){
	$days = $_GET['cut']=="pre"?"2011-12-17,2011-12-21,2011-12-18,2011-12-16":"2012-03-02,2012-03-03,2012-03-04,2012-03-07";
}

$valid = explode(",", "2011-12-17,2011-12-21,2011-12-18,2011-12-16,2012-03-02,2012-03-03,2012-03-04,2012-03-07");

$days = explode(",", $days);

foreach ($days as $key => $value) {
	if(!in_array($value, $valid)){
		unset($days[$key]);
	}
}

if (empty($days)){
	$days = $valid;
}

$mysqli = new mysqli("localhost","root","","bus2");

$q = "SELECT lat, lon, AVG(deviation), COUNT(deviation) FROM deviation ".
	 "WHERE oba_day IN (\"". implode("\",\"",$days) ."\") GROUP BY lat, lon";

// Extract result set and loop rows
$result = $mysqli->query($q);
while ($row = $result->fetch_assoc()) {
   print_r($row);
}

$mysqli->close();
?>

var stops = [{
   "type": "Feature",
   "properties": {"avg_deviation": "-1200", "stop_name": "40th and Stone Way N"},
   "geometry": {
       "type": "Point",
       "coordinates":  [-104.05, 48.99],
   }
}, {
   "type": "Feature",
   "properties": {"avg_deviation": "0", "stop_name": "Stevens and Benton"},
   "geometry": {
       "type": "Point",
       "coordinates": [-109.05, 41.00],
   }
}];