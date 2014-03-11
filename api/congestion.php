<?php

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: X-Requested-With");
   
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

$q = "SELECT lat, lon, AVG(deviation) avg, COUNT(deviation) ct FROM deviation ".
	 "WHERE oba_day IN (\"". implode("\",\"",$days) ."\") GROUP BY lat, lon ORDER BY RAND() LIMIT 500";

// Extract result set and loop rows
$result = $mysqli->query($q);

$list = array();

while ($row = $result->fetch_assoc()) {
   $list[] = array("type" => "Feature",
      "properties" => array("avg_deviation" => $row["avg"], "data_points" => $row["ct"]),
      "geometry" => array("coordinations" => array($row["lat"], $row["lon"]), "type" => "Point"));
}

echo json_encode($list);
$mysqli->close();
?>