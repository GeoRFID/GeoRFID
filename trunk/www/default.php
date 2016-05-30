<html>
<head>
	<title>GeoRFID</title>
	<meta charset="utf-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0">	
	<link rel="stylesheet" href="leaflet/v0.7.7/leaflet.css" />	
	<link rel="stylesheet" href="css/font-awesome-4.6.1/css/font-awesome.min.css">
	<link href="css/bootstrap-3.3.6-dist/css/bootstrap.css" rel="stylesheet">
	<script src="leaflet/v0.7.7/leaflet.js"></script>		
	<script src="js/measure.js"></script>		
	<script src="js/AnimatedMarker.js"></script>		
	<script src="js/MovingMarker.js"></script>		
	<script src="js/KML.js"></script>		
	<script src="js/indoor.js"></script>		
	<script src="js/jquery.min.js"></script>		
	<script src="js/common.js"></script>		
	<script src="js/routes.js"></script>		
	<link rel="stylesheet" href="css/main.css" />	
	<link rel="stylesheet" href="css/measure.css" />		
	<link rel="icon" type="image/png" href="images/georfid.ico">
	<script type="text/javascript">
	<?php 
		if (session_status() == PHP_SESSION_NONE) {
			session_start();
		}

		include_once("localization.php");
		
		if (isset($_GET['l'])) {
			$lang="en";		
			$lang=$_GET['l'];
			setLanguage($lang);
		}
		
		echo "var lang='" . $lang . "';";
		
		echo "var tipoUsuario=" . $_SESSION["tipoUsuario"] . ";";
		
		echo "var streetsLoc='" . locPhp('streets') . "';";
		echo "var grayScaleLoc='" . locPhp('grayScale') . "';";
		echo "var satelliteLoc='" . locPhp('satellite') . "';";
		echo "var typeLoc='" . locPhp('type') . "';";
		echo "var modelLoc='" . locPhp('model') . "';";
		echo "var fieldsLoc='" . locPhp('fields') . "';";
		echo "var moveLoc='" . locPhp('move') . "';";
		echo "var imageLoc='" . locPhp('image') . "';";
		echo "var saveLoc='" . locPhp('save') . "';";
		echo "var deleteLoc='" . locPhp('delete') . "';";
		echo "var deleteConfirmLoc='" . locPhp('deleteConfirm') . "';";
		echo "var viewIncidentLoc='" . locPhp('viewIncident') . "';";
		
	?>			
	</script>
	
</head>
<body onresize="establecerAlturaMapa()">
	<div class="fullWidth fullHeight" >
		<div class="header">
			<img class='headerLogo' src='images/GeoRFIDlogo.png' />
			<div class="right fullWidth logout">							
			<a href="javascript:setLanguage('en')">en</a>
			<a href="javascript:setLanguage('es')">es</a>
			<a href="javascript:setLanguage('eu')">eu</a>
			&nbsp;&nbsp;
				<?php 
					if (session_status() == PHP_SESSION_NONE) {
						session_start();
					}
					echo $_SESSION["nombre"];
				?>			
				<a class="btn btn-primary" href="logout.php">
					<i class="fa fa-sign-out" aria-hidden="true"></i>					
				</a>			
			</div>
		</div>
		<div class="tools">
			<a class="btn btn-default" href="javascript:recargarElementos();" title='<?php loc('centerElements'); ?>'>
				<i class="fa fa-globe" aria-hidden="true"></i>					
			</a>			
			<a class="btn btn-default" href="javascript:actualizarElementos();" title='<?php loc('update'); ?>'>
				<i class="fa fa-refresh" aria-hidden="true"></i>					
			</a>
			<a class="btn btn-default" href="javascript:mostrarFiltrado();" title='<?php loc('filterSearch'); ?>'>
				<i class="fa fa-filter" aria-hidden="true"></i>					
			</a>								
			<a class="btn btn-default" href="javascript:mostrarListado();" title='<?php loc('listElements'); ?>'>
				<i class="fa fa-list" aria-hidden="true"></i>					
			</a>						
			<a class="btn btn-default" id="btnNuevoElemento" href="javascript:nuevoElemento();" title='<?php loc('newElement'); ?>'>
				<i class="fa fa-plus" aria-hidden="true"></i>					
			</a>
			<a class="btn btn-default" href="javascript:enlazar();" title='<?php loc('link'); ?>'>
				<i class="fa fa-external-link" aria-hidden="true"></i>					
			</a>					
		</div>
		<div id="divFiltro" class="filtro hidden"><?php loc('textFilter'); ?>
			<br/>
			<br/>
			<input type="text" id="txtFiltro" class="fullWidth"/>
			<br/>
			<br/>
			<div class="center">
				<input type="button" class="halfWidth" value="<?php loc('filter'); ?>" onclick="javascript:filtrar()"/>
				&nbsp;
				<input type="button" class="halfWidth" value="<?php loc('cancel'); ?>" onclick="javascript:cancelarFiltro()"/>				
			</div>
		</div>
		<div id="map" class="fullWidth fullHeight" style="margin:0 0 0 0" >
		</div>
		<div id="divListado" class="listado hidden">	
			<div class="fullWidth right">
				<input type="button" value="<?php loc('close'); ?>" onclick="javascript:cancelar()"/>
			</div>
			<br>
			<div id="divListadoContenido"></div>					
		</div>
		<div id="divNuevoElemento" class="listado hidden centerDiv nuevoElemento">
		<div class="panelHeader fullWidth"><?php loc('newElement'); ?></div>
			<br/>
			<?php loc('selectType'); ?>
			<br/>
			<br/>
			<select id="selectNewElementType" class="fullWidth"></select>	
			<br/>
			<br/>
			<div class="center">
			<input type="button" class="halfWidth" value="<?php loc('create'); ?>" onclick="javascript:nuevoElementoPaso2()"/>
			&nbsp;
			<input type="button" class="halfWidth" value="<?php loc('cancel'); ?>" onclick="javascript:cancelar()"/>
			</div>
		</div>
		<div id="divEnlacesMapas" class="enlacesMapas centerDiv hidden">	
		<div class="fullWidth center"><?php loc('selectMapService'); ?>
			<table>
			<tr>
				<td class="center"><img class='bigIcon' src='images/GoogleMaps.png' onclick="javaspcript:iniciarCapturaEnlace(1);" /><br/>Google Maps</td>
				<td class="center"><img class='bigIcon' src='images/StreetView.png' onclick="javaspcript:iniciarCapturaEnlace(2);"/><br/>Street View</td>
			</tr>
			<tr>
				<td class="center"><img class='bigIcon' src='images/bing.png' onclick="javaspcript:iniciarCapturaEnlace(3);"/><br/>Bing Maps</td>
				<td class="center"><img class='bigIcon' src='images/here.png' onclick="javaspcript:iniciarCapturaEnlace(4);"/><br/>Here Maps</td>
			</tr>
			</table>
		</div>
			<div class="fullWidth right">
				<input type="button" value="<?php loc('close'); ?>" onclick="javascript:cancelar()"/>
			</div>
			
		</div>
		<div id="divActualizarImagen" class="listado subirImagen hidden centerDiv">
			<iframe id="setElementImageFrame" frameborder=0 width='345' height='120' src=""></iframe>
		</div>
		<div id="divTarea" class="listado tarea hidden centerDiv">
			<iframe id="tareaFrame" frameborder=0 width='260' height='280' src=""></iframe>
		</div>
		<div id="divSplash" class="splash">
			<div id="divSplashContent" class="centerDiv splashContent fullWidth fullHeight">
				<img class='splashLogo centerDiv' src='images/GeoRFIDlogo.png' />		
				<div id="divLoading" class="loading"><img src='images/loader.gif' /></div>				
			</div>
		</div>
		<div id="divOpencartis" class="opencartis">
			<a target="blank" href='http://www.opencartis.com'><img class='opencartis' src='images/opencartis.png' /></a>
		</div>
		<!--<div id="divRoutes" class="routes">
			<div class="red routesPadding" title="Pittsburg/Bay Point - SFIA/Millbrae">
				<input type="checkbox"  onclick="javascript:changeRouteState(1)"/> PITT-SFIA
			</div>
			<div class="blue routesPadding" title="Fremont - Richmond">
				<input type="checkbox" class="test" onclick="javascript:changeRouteState(2)"/> FRMT-RICH
			</div>
			<div class="grey routesPadding" title="Fremont - Daly City">
				<input type="checkbox" onclick="javascript:changeRouteState(3)"/> FRMT-DALY
			</div>
			<div class="green routesPadding" title="Richmond - Daly City/Millbrae">
				<input type="checkbox" onclick="javascript:changeRouteState(4)"/> RICH-MLBR
			</div>
			<div class="orange routesPadding" title="Dublin/Pleasanton - Daly City">
				<input type="checkbox" onclick="javascript:changeRouteState(5)"/> DUBL-DALY
			</div>
			<div class="yellow routesPadding" title="Coliseum - Oakland Airport">
				<input type="checkbox" onclick="javascript:changeRouteState(6)"/> OAKL-COLS
			</div>
		</div>-->		
	</div>	
</body>

<script src="js/default.js"></script>

</html>
