<html>
<head>
<link rel="stylesheet" href="css/main.css" />	
<link rel="stylesheet" href="css/task.css" />	
<script src="js/common.js"></script>		
<script src="js/getElementTask.js"></script>		
</head>
<body>
<?php
	$tareaId=$_GET['id'];
	$h=150;
	$w=100;
	
	$imageSize="height:$h";
	$link="getElementTaskImage.php?id=$tareaId&w=100%&h=100%";
	
	include_once("connection.php");	
	
	include_once("localization.php");	

	if (isset($_GET['l'])) {
		$lang="en";		
		$lang=$_GET['l'];
		setLanguage($lang);
	}	
		
	if($w==0 && $h==0)
	{
		$imageSize="";
		$link="javascript:window.close();";
	}
	
	if(!$dbconn)
    {    
		echo "";    
	}	

	$sql= "SELECT \"id\",\"nombre\" FROM \"GeoRFID\".\"EstadoTarea\"";
	
	$result = pg_query($dbconn, $sql);
	
	$stateIds = array();
	$stateNames = array();	
	$index=0;
	
	while ($rowState = pg_fetch_row($result)) {
		$stateIds[$index]= $rowState[0];
		$stateNames[$index]= $rowState[1];
		
		$index++;
	}		
	
	$sql= "SELECT tarea.\"id\" as \"tareaId\",\"elementoId\", \"tipoTareaId\", \"descripcion\" as \"descripcionTarea\", \"imagen\" as \"imagenTarea\", \"estadoId\" as \"estadoTareaId\" , tipo.\"nombre\" as \"tipoTareaNombre\" FROM \"GeoRFID\".\"ElementoTarea\" AS tarea INNER JOIN \"GeoRFID\".\"TipoTarea\" as tipo ON tarea.\"tipoTareaId\" = tipo.\"id\" WHERE tarea.\"id\"=" . $tareaId ;	
	
	$result = pg_query($dbconn, $sql);

	if (!$result) {
	  echo "An error occurred.\n";
	  exit;
	}

	while ($rowTask = pg_fetch_row($result)) {
		
		echo "<table class='taskTable'>";
		
		echo "<tr><td class='panelHeader center'>";	
		echo "<img class='incidenceIcon' src='images/warning.png'>&nbsp;";
		echo $rowTask[6];		
		echo "</td></tr>";
		
		echo "<tr><td class='center'>";		
		echo $rowTask[3];
		echo "</td></tr>";		
		
		echo "<tr><td class='center'>";		
		
		if($rowTask[4]=="null" || $rowTask[4]=="")
		{			
			echo "<img style=\"width:$w;\" alt=\"Embedded Image\" src=\"images/no-photo.jpg\"/>";	 								
		}
		else		
		{
			echo "<a target='blank' href='$link'><img style=\"$imageSize\" alt=\"Embedded Image\" src=\"data:image/png;base64,$rowTask[4]\"/></a>";		
		}			
		
		echo "</td></tr>";
				
		if($_SESSION["tipoUsuario"]==1)
		{
			//lista
			echo "<tr><td class='center'>";		
			echo "<select id='selectTaskState' class='fullWidth'>";		
			
			for($k=0;$k<count($stateIds);$k++)
			{
				echo "<option ";
				
				if($stateIds[$k] == $rowTask[5])
				{
					echo "selected";
				}

				echo " value='" . $stateIds[$k] . "'>" . $stateNames[$k] . "</option>" ;
			}
			
			echo "</select>";
			echo "</td></tr>";
			
			echo "<tr><td class='center'>";		
			echo "<input type='button' value=" . locPhp("save") ." onclick='guardarCambioEstadoTarea();'/>&nbsp;<input type='button' value='" . locPhp("close") ."' onclick='cerrar();'/>";
			echo "</td></tr>";
		}
		else
		{
			echo "<tr><td class='center'>";		
			
			for($k=0;$k<count($stateIds);$k++)
			{			
				if($stateIds[$k] == $rowTask[5])
				{
					echo $stateNames[$k]  ;
				}
			}
		
			echo "</td></tr>";				
			echo "<tr><td class='center'>";		
			echo "<input type='button' value='" . locPhp("close") ."' onclick='cerrar();'/>";
			echo "</td></tr>";
		}
		echo "</table>";
	}
	
	pg_close($dbconn);
?>
</body>
</html>