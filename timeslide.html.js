<html>
	<head>
		<title>jQuery Time Slider Demo Part 1</title>
		<link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/dot-luv/jquery-ui.css" />
		<style>
			#slider-range{
				width:400px;
			}
		</style>
	</head>
	<body>
		<div id="slider-range"></div>
		<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script> 
		<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js" type="text/javascript"></script>
		<script type="text/javascript">
			$("#slider-range").slider({
				range: true,
				min: 0,
				max: 2879,
				values: [540, 1020],
				step:5
			});
		</script>
	</body>
</html>