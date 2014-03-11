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

$before = explode(":", $before);
$after = explode(":", $after);

if (!is_numeric($before[0]) || !is_numeric($before[1])) {
$before = array(23, 59);
}
if (!is_numeric($after[0]) || !is_numeric($after[1])) {
$after = array(0, 0);
}

$time_cond = "";
$time_cond .= "hh<=" . $before[0] . " AND ";
$time_cond .= "(mm<=" . $before[1] . " OR hh<" . $before[0] . ") AND ";
$time_cond .= "hh>=" . $after[0] . " AND ";
$time_cond .= "(mm>=" . $after[1] . " OR hh>" . $after[0] . ") AND ";

$q = "SELECT lat, lon, AVG(deviation) avg FROM deviation ".
    "WHERE " . $time_cond. " oba_day IN (\"". implode("\",\"",$days) ."\") GROUP BY lat, lon ORDER BY RAND() LIMIT 2000";

// Extract result set and loop rows
$result = $mysqli->query($q);

$list = array();

while ($row = $result->fetch_assoc()) {
   $list[] = array("type" => "Feature",
      "properties" => array("avg_deviation" => $row["avg"], "data_points" => $row["ct"]),
      "geometry" => array("coordinates" => array((double)$row["lat"], (double)$row["lon"]), "type" => "Point"));
}

echo json_encode($list);
$mysqli->close();
?>