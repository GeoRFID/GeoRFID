<html>
<head>
<script src="js/common.js"></script>		
</head>
<body>
<?php
	$tareaId=$_GET['id'];	
	$link="javascript:window.close();";
	
	include_once("connection.php");	
		
	if(!$dbconn)
    {    
		echo "";    
	}	

	$sql= "SELECT tarea.\"id\" as \"tareaId\",\"elementoId\", \"tipoTareaId\", \"descripcion\" as \"descripcionTarea\", \"imagen\" as \"imagenTarea\", \"estadoId\" as \"estadoTareaId\" , tipo.\"nombre\" as \"tipoTareaNombre\" FROM \"GeoRFID\".\"ElementoTarea\" AS tarea INNER JOIN \"GeoRFID\".\"TipoTarea\" as tipo ON tarea.\"tipoTareaId\" = tipo.\"id\" WHERE tarea.\"id\"=" . $tareaId ;	
	
	$result = pg_query($dbconn, $sql);

	if (!$result) {
	  echo "An error occurred.\n";
	  exit;
	}

	while ($rowTask = pg_fetch_row($result)) {		
		echo "<a href='$link'><img style=\"position: absolute; margin:auto; top:0; left:0; right:0; bottom:0 ;height:100%\" alt=\"Embedded Image\" src=\"data:image/png;base64,$rowTask[4]\"/></a>";							
	}
	
	pg_close($dbconn);
?>
</body>
</html>