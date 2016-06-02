var currentId;
var elements;
var filtro="";
var layerControl;		
var moviendoElemento=false;
var creandoElemento=false;
var capturandoEnlace=false;
var indiceEnlace=-1;
var zoomExtension=true;
var nuevoTipo=1;
var layerGroups = new Array();
var listaValores = new Array();
var tipos = new Array();
var map = undefined;			
var kmlLayer;
var selectNewElementType;
var dataPoints;
var heatMapVisible=false;
var heat;

function cargarListasValores()
{
    var oReq = new XMLHttpRequest();
	
    oReq.onload = function() {
        		
		var filas= this.responseText.split("\n");
				
		for(var i=0;i<filas.length;i++)
		{						
			if(filas[i]!="")
			{													
				var parts = filas[i].split(";");
				var indice=parts[0];
				var valor=parts[1];
				if(listaValores[indice] == undefined)
				{
					listaValores[indice] = new Array();
				}
				
				listaValores[indice].push(valor);
			}
		}
		
		actualizarElementos();
    };   
	
	oReq.open("get", "getElementPropertyList.php", true);    
	
    oReq.send();
}

function actualizarElementos()
{				
	if(map==undefined)
	{	
		var mbAttr = 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
				'<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
				'Imagery © <a href="http://mapbox.com">Mapbox</a>',
			mbUrl = 'https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibGFkZCIsImEiOiJjaWcwcXhoaGUwMDFxdnBsdzhxYm5lbmoxIn0.IPgwICFYonFtFXSN4r8EPg';

		var grayscale   = L.tileLayer(mbUrl, {id: 'mapbox.light', attribution: mbAttr, maxZoom:24, maxNativeZoom: 21}),
			streets  = L.tileLayer(mbUrl, {id: 'mapbox.streets',   attribution: mbAttr, maxZoom:24, maxNativeZoom: 21});
			satellite  = L.tileLayer(mbUrl, {id: 'mapbox.satellite',   attribution: mbAttr, maxZoom:24, maxNativeZoom: 19});
			osm= L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors', maxZoom:24, maxNativeZoom: 21});

		var Esri_WorldImagery = L.tileLayer('http://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {attribution: 'Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-EGP, and the GIS User Community', maxZoom:24, maxNativeZoom: 19});

		map = L.map('map', {
			center: [43.320533,-1.9854482],
			zoom: 18,
			layers: [streets],
			fullscreenControl: true,
			fullscreenControlOptions: {
			position: 'topleft'
			}
		});
		
		var baseLayers= new Array(); 
		baseLayers[streetsLoc]=streets;
		baseLayers[grayScaleLoc]=grayscale;
		baseLayers[satelliteLoc]=satellite;
		baseLayers["OSM"]=osm;
		baseLayers["Esri"]=Esri_WorldImagery;
			
			

		layerControl=L.control.layers(baseLayers);
		layerControl.addTo(map);

		map.on('click', 
			function(e) 
				{ 
					ocultarTodosDiv();
					
					var lat=e.latlng.lat;
					var lon=e.latlng.lng;			
					
					if(moviendoElemento)
					{						
						moverElementoPaso2(lat, lon);
					}
					if(creandoElemento)
					{
						nuevoElementoPaso3(lat, lon);
					}
					if(capturandoEnlace)
					{
						enlaceCapturado(lat, lon);
					}
				}
			);
	
		kmlLayer = new L.KML("kml/localizado.kml", {async: true});
                                                              
		kmlLayer.on("loaded", function(e) { 
		   map.fitBounds(e.target.getBounds());
		});
                                                
        map.addLayer(kmlLayer);
		 		 
		var measureControl = new L.Control.Measure(
			{ 
				primaryLengthUnit: 'meters',
				secondaryLengthUnit: undefined,
				primaryAreaUnit: 'sqmeters', 
				secondaryAreaUnit: undefined,
				activeColor: '#FF0000',
				completedColor: '#ff4e4e',
				localization: lang 				
			});
		measureControl.addTo(map);
						
		//loadIndoor();
	}
	
	function loadIndoor()
	{
		    $.getJSON("json/data.json", function(geoJSON) {

            var indoorLayer = new L.Indoor(geoJSON, {
                getLevel: function(feature) { 
                    if (feature.properties.relations.length === 0)
                        return null;

                    return feature.properties.relations[0].reltags.level;
                },
                onEachFeature: function(feature, layer) {
                    layer.bindPopup(JSON.stringify(feature.properties, null, 4));
                },
                style: function(feature) {
                    var fill = 'white';

                    if (feature.properties.tags.buildingpart === 'habitaculo') {
                        fill = '#169EC6';
                    } else if (feature.properties.tags.buildingpart === 'verticalpassage') {
                        fill = '#0A485B';
                    }

                    return {
                        fillColor: fill,
                        weight: 1,
                        color: '#666',
                        fillOpacity: 1
                    };
                }
            });

            indoorLayer.setLevel("0");

            indoorLayer.addTo(map);

            var levelControl = new L.Control.Level({
                level: "0",
                levels: indoorLayer.getLevels()
            });

            // Connect the level control to the indoor layer
            levelControl.addEventListener("levelchange", indoorLayer.setLevel, indoorLayer);
            levelControl.addTo(map);
        });      
	}

	eliminarCapas();
	
    var oReq = new XMLHttpRequest();
	
    oReq.onload = function() {
        		
		elements= this.responseText.split("\n");
		var minLat=0;
		var maxLat=0;
		var minLon=0;
		var maxLon=0;
		var tableListadoHtml="<table class='tablaListado'><tr><td class='tablaListado center panelHeader'>" + typeLoc + "</td><td class='tablaListado center panelHeader'>" + fieldsLoc + "</td></tr>";
		
		tipos = new Array();
		dataPoints = new Array();
		
		for(var i=0;i<elements.length;i++)
		{	
			if(layerControl == undefined)
			{
				layerControl = L.control.layers().addTo(map);										
			}
			
			if(elements[i]!="")
			{												
				if(elements[i].toLowerCase().indexOf(filtro.toLowerCase())==-1)
				{
					continue;
				}
		
				parts = elements[i].split("|");
		
				values= parts[0].split(";");				
			
				var id= values[0];
				var tag= values[1];
				var lat=parseFloat(values[2]);
				var lon=parseFloat(values[3]);
				var tipoId=values[4];
				var tipo=values[5];
				var tareaId=values[6];							
				
				var eachPoint= new Array();
				eachPoint.push(lat);
				eachPoint.push(lon);
				eachPoint.push(50);
				dataPoints.push(eachPoint);
				
				if(tipos[tipoId]==undefined)
				{
					tipos[tipoId]= tipo;					
				}
				
				var iconFile='images/' + tipo + '.png';
				
				tableListadoHtml+="<tr><td class='tablaListado'>" + tipo + "</td>"
				
				if(minLat==0)	
				{	
					minLat=lat;
					maxLat=lat;
					minLon=lon;
					maxLon=lon;
				}
				else
				{					
					if(lat<minLat)
					{
						minLat=lat;
					}
					if(lon<minLon)
					{
						minLon=lon;
					}
					if(lat>maxLat)
					{
						maxLat=lat;
					}
					if(lon>maxLon)
					{
						maxLon=lon;
					}				
				}							
				
				var currentGroup= layerGroups[tipo];
				if(currentGroup==undefined)
				{
					layerGroups[tipo]=L.layerGroup();
					currentGroup= layerGroups[tipo];
					currentGroup.addTo(map);
					layerControl.addOverlay(currentGroup, "<img src='images/" + tipo + ".png'/>" + tipo );						
				}
				
				var tooltip= "<table disabled='true' class='tooltipTable'><tr><td colspan='2' class='panelHeader center'>" + tipo +"</td></tr>" ;
				
				var textoResumen="";
				
				for(var j=1;j<parts.length;j++)
				{
					if(parts[j]!="")
					{
						var propertyParts=parts[j].split(";");
						
						var nombre = propertyParts[2];
						var tipoCampoId = parseInt(propertyParts[3]);
						var valor = propertyParts[5];
						var listaValoresId = propertyParts[4];							
						
						tooltip += "<tr ";
						if(j%2!=0)
						{
							tooltip += "class='rowAlternate'";		
						}
						tooltip += " >";
										
						tooltip += "<td>" + nombre + "</td><td class='center'>";
						
						var campoId = id + "_campo_" + nombre;
						
						if(tipoUsuario==0)
						{
							switch(tipoCampoId)
							{
								case 3:
								{
									//boolean
									if(valor=="1")
									{
										tooltip+="<input disabled id='" + campoId + "'  type='checkbox' checked/>";	
									}
									else
									{
										tooltip+="<input disabled id='" + campoId + "'  type='checkbox' />";
									}
									break;
								}									
								default:
								{
									tooltip +="<span id='" + campoId + "' >"+ valor +"</span>" ;
									
									textoResumen = AnadirTextoResumen(textoResumen, valor) ;
									break;
								}
							}
						}
						else
						{							
							switch(tipoCampoId)
							{
								case 2:
								{
									//lista
									tooltip +="<select id='" + campoId + "' class='fullWidth'>";

									var valores= listaValores[listaValoresId];
									
									for(var k=0;k<valores.length;k++)
									{
										valorLista = valores[k];
										tooltip +="<option ";
										
										if(valorLista == valor)
										{
											tooltip +="selected";									
																	
											textoResumen = AnadirTextoResumen(textoResumen, valorLista);
										}

										tooltip += " value='"+ valorLista+"'>" + valorLista + "</option>" ;										
									}
									
									tooltip +="</select>";
									break;
								}
								case 3:
								{
									//boolean
									if(valor=="1")
									{
										tooltip+="<input id='" + campoId + "'  type='checkbox' checked/>";	
									}
									else
									{
										tooltip+="<input id='" + campoId + "'  type='checkbox' />";
									}
									break;
								}
								case 4:
								{
									//entero
									tooltip +="<input id='" + campoId + "'  type='number' value='"+ valor +"'/>" ;
									break;
								}
								case 5:
								{
									//decimal
									tooltip +="<input id='" + campoId + "'  type='number' step='0.1' value='"+ parseFloat(valor) +"'/>" ;
									break;
								}
								default:
								{
									tooltip +="<input type='textbox' id='" + campoId + "' value='"+ valor +"'/>" ;
									
									textoResumen = AnadirTextoResumen(textoResumen, valor) ;
									break;
								}
							}
						}

						tooltip += "</td></tr>";
					}
				}
				
				tooltip+= "<tr><td colspan='2' class='center tag'><iframe frameborder=0 width='220' height='130' src='getElementImage.php?id=" + id + "&w=100%&h=100%&c=0' ></iframe>";
				if(tag!=undefined && tag!="null")
				{
					tooltip+="<br/>" + tag;
				}
				tooltip+= "</td></tr>";
		
				if(tipoUsuario==1)		
				{
					tooltip+= "<tr><td class='center' colspan='2'><input type='button' value='"+ moveLoc +"' onclick='moverElementoPaso1(" + id + ");'/>&nbsp;"				
					tooltip+= "<input type='button' value='" + imageLoc + "' onclick='actualizarImagenElemento(" + id + ")'/>&nbsp;"
					tooltip+= "<input type='button' value='" + saveLoc + "' onclick='guardarElemento(" + id + ")'/>&nbsp;"
					tooltip+= "<input type='button' value='" + deleteLoc + "' onclick='eliminarElemento(" + id + ");'/></td></tr>"
				}
				
				tooltip+= "</table>";
				//tooltip+= "<div class='fullWidth center'>";
				//tooltip+= "<a target='blank' href='" + generarEnlace(1,lat,lon) + "'><img class='miniIcon' src='images/GoogleMaps.png' title='Google Maps' /></a> ";
				//tooltip+= "<a target='blank' href='" + generarEnlace(2,lat,lon) + "'><img class='miniIcon' src='images/StreetView.png' title='Street View' /></a> ";
				//tooltip+= "<a target='blank' href='" + generarEnlace(3,lat,lon) + "'><img class='miniIcon' src='images/bing.png' title='Bing Maps' /></a>";
				//tooltip+= "<a target='blank' href='" + generarEnlace(4,lat,lon) + "'><img class='miniIcon' src='images/here.png' title='Here Maps' /></a>";											
				//tooltip+= "</div>";						
				
				if(tareaId!="")
				{
					tooltip+= "<br/><div class='fullWidth center incidence' onclick='javascript:mostrarTarea(" + tareaId + ")'>";
					tooltip+= "<img class='incidenceIcon' src='images/warning.png'> " + viewIncidentLoc;					
					tooltip+= "</div>";	
					
					iconFile='images/' + tipo + '_i.png';
				}
											
				
				var icon= L.icon({
					iconUrl: iconFile,
					iconSize:     [24, 24], // size of the icon
					iconAnchor:   [12, 12], // point of the icon which will correspond to marker's location
					popupAnchor:  [0, 0] // point from which the popup should open relative to the iconAnchor
				});
				
				var customOptions =
				{
					'className' : 'tooltipBackground'
				}
				
				L.marker([lat,lon], {icon:icon}).bindPopup(tooltip, customOptions).addTo(currentGroup);	
				
				tableListadoHtml+="</td><td class='tablaListado'>" + textoResumen + "</td><td class='tablaListado'><a href='javascript:ZoomPunto(" + lat + "," + lon + ");'><i class='fa fa-search' aria-hidden='true'></i></a></td>"
				tableListadoHtml += "</tr>"
			}					
		}
		
		layerControl.addOverlay(kmlLayer, "<img style='width:28px' src='images/house.png'/> Building" );	
		
		tableListadoHtml+="</table>";
		var divListadoContenido = document.getElementById("divListadoContenido");
		divListadoContenido.innerHTML= tableListadoHtml;
		
		
		if(zoomExtension && maxLon!=null)
		{
			var margin= 0.001;
			var southWest= L.latLng(minLat-margin,minLon-margin);
			var northEast= L.latLng(maxLat+margin,maxLon+margin);
			var latlngbounds = L.LatLngBounds();
			//map.fitBounds([southWest,northEast]);
			
			zoomExtension=false;
		}		
		
		ocultarDiv("divSplash");
		
		establecerAlturaMapa();
    };
    
	oReq.open("get", "getElements.php", true);    
	
    oReq.send();
}

function mostrarMapaCalor()
{
	var count=0;	

	if(heatMapVisible)
	{		 	
		actualizarElementos();	
		map.removeLayer(heat);
		heatMapVisible=false;
	}
	else
	{
		eliminarCapas();	
		heat = L.heatLayer(dataPoints, {radius: 25}).addTo(map);	
		heatMapVisible=true;
	}
}

function eliminarElemento(id)
{
	if(confirm(deleteConfirmLoc))
	{
		eliminarCapas();
		
		var oReq = new XMLHttpRequest();
		oReq.open("get", "deleteElement.php?id=" + id, false);    	
		oReq.send();
		
		actualizarElementos();
	}
}

function AnadirTextoResumen(texto, dato)
{
	if(dato!="")
	{
		if(texto!="")
		{
			texto+="; "
		}
			
		texto += dato;	
	}
	
	return texto;
}


function ZoomPunto(lat,lon)
{
	map.setView([lat, lon], 25);	
}

function moverElementoPaso1(id)
{
	hidePopup();
	moviendoElemento=true;
	currentId=id;
	cursorClick();
}

function hidePopup()
{
	map.closePopup();
}

function moverElementoPaso2(lat, lon)
{
	resetearCursor();
	
	moviendoElemento=false;
	
	eliminarCapas();
		
	var oReq = new XMLHttpRequest();
	oReq.open("get", "moveElement.php?id=" + currentId + "&lat=" + lat + "&lon=" + lon , false);    	
	oReq.send();
	
	actualizarElementos();
}

function guardarElemento(idGuardar)
{
	var parameters="";
	
		for(var i=0;i<elements.length;i++)
		{	
			if(layerControl == undefined)
			{
				layerControl = L.control.layers().addTo(map);				
			}
			
			if(elements[i]!="")
			{													
				parts = elements[i].split("|");
		
				values= parts[0].split(";");
			
				var id= values[0];

				if(idGuardar==id)
				{					
					var tag= values[1];
					var lat=parseFloat(values[2]);
					var lon=parseFloat(values[3]);
					var tipo= values[5];

					parameters = "id=" + id;
					
					for(var j=1;j<parts.length;j++)
					{
						if(parts[j]!="")
						{
							var propertyParts=parts[j].split(";");
							
							var campoId = propertyParts[0];
							var nombre = propertyParts[2];
							var tipoCampoId = parseInt(propertyParts[3]);
							var valor ="";
							
							var campoDom=document.getElementById(id + "_campo_" + nombre);
							
							parameters += "&" + campoId + "=";
							
							switch(tipoCampoId)
							{
								case 2:
								{
									//lista
									valor = campoDom.options[campoDom.selectedIndex].value;
									
									break;
								}
								case 3:
								{
									//boolean
									if(campoDom.checked)
									{
										valor=1;
									}
									else
									{
										valor=0;
									}									
									break;
								}
								case 4: //entero									
								case 5: //decimal																
								default:
								{
									valor = campoDom.value.trim();
									break;
								}
							}							
							
							parameters += valor;
						}										
					}
					
					eliminarCapas();
		
					var oReq = new XMLHttpRequest();
					oReq.open("get", "saveElement.php?" + parameters , false);    	
					oReq.send();
	
					actualizarElementos();					
				}
			}
		}	
}

function eliminarCapas()
{	
	for(var key in layerGroups) {
		let value = layerGroups[key];
		value.clearLayers();
	};
}

function recargarElementos()
{
	zoomExtension=true;
	actualizarElementos();
}

function mostrarFiltrado()
{
	mostrarDiv("divFiltro");	
}

function mostrarListado()
{
	mostrarDiv("divListado");
}

function filtrar()
{
	filtro = document.getElementById("txtFiltro").value;
	actualizarElementos();
}

function nuevoElemento()
{
	var keys=Object.keys(tipos);		
	
	if(selectNewElementType==undefined)
	{
		selectNewElementType = document.getElementById("selectNewElementType");

		for(var i=0;i<keys.length;i++)
		{
			var opt = document.createElement('option');
			var key=keys[i];
			
			opt.value = key;
			opt.innerHTML = tipos[key];
			selectNewElementType.appendChild(opt);		
		}
	}
	
	mostrarDiv("divNuevoElemento");
}

function nuevoElementoPaso2()
{
	var selectNewElementType = document.getElementById("selectNewElementType");
	nuevoTipo = selectNewElementType.options[selectNewElementType.selectedIndex].value;

	ocultarDiv("divNuevoElemento");
	creandoElemento=true;
	cursorClick();
}

function nuevoElementoPaso3(lat, lon)
{
	resetearCursor();
	creandoElemento=false;
		
	eliminarCapas();
		
	var oReq = new XMLHttpRequest();
	oReq.open("get", "newElement.php?tipo=" + nuevoTipo + "&lat=" + lat + "&lon=" + lon , false);    	
	oReq.send();
	
	actualizarElementos();	
}

function actualizarImagenElemento(id)
{
	hidePopup();
	var frame=document.getElementById("setElementImageFrame");
	frame.src="setElementImage.php?id=" + id;
	mostrarDiv("divActualizarImagen");
}

function ocultarDiv(nombreDiv)
{
	var div = document.getElementById(nombreDiv);	
	div.className = div.className + " hidden";
}

function mostrarDiv(nombreDiv)
{	
	var div = document.getElementById(nombreDiv);
	
	ocultarTodosDiv();
	
	while(div.className.indexOf("hidden")>0)
	{
		div.className = div.className.replace("hidden","");
	}
}

function cancelar()
{
	resetearCursor()
	ocultarTodosDiv();
	moviendoElemento=false;
	creandoElemento=false;	
}

function cancelarFiltro()
{
	filtro = "";
	actualizarElementos();
	cancelar();
}

function ocultarTodosDiv()
{
	ocultarDiv("divNuevoElemento");
	ocultarDiv("divListado");
	ocultarDiv("divFiltro");
	ocultarDiv("divActualizarImagen");
	ocultarDiv("divEnlacesMapas");
	ocultarDiv("divTarea");	
}

function imagenActualizada()
{	
	ocultarTodosDiv();
}

function sleepFor( sleepDuration ){
    var now = new Date().getTime();
    while(new Date().getTime() < now + sleepDuration){ /* do nothing */ } 
}

function generarEnlace(indice, lat, lon)
{
	var enlace="";
	
	switch(indice)
	{
		case 1:
		{
			enlace="https://maps.google.com/?q=" + lat + "," + lon;
			break;
		}
		case 2:
		{
			enlace="http://maps.google.com/maps?q=&layer=c&cbll=" + lat + "," + lon;
			break;
		}
		case 3:
		{
			enlace="http://www.bing.com/mapspreview?&ty=18&q=" + lat + "%2c" + lon;
			break;
		}
		case 4:
		{
			//enlace="http://b5m.gipuzkoa.eus/b5map/r1/es/mapa/permalink/capa-frente/meu2015/capa-fondo/osm/lat/" + lat + "/lon/" + lon +"/zoom/19/";
			enlace="https://maps.here.com/?map=" + lat + "," + lon +",19,normal";
			break;
		}				
	}
	
	return enlace;
}

function iniciarCapturaEnlace(indice)
{
	cursorClick();
	ocultarTodosDiv();
	indiceEnlace=indice;
	capturandoEnlace=true;
}

function enlaceCapturado(lat, lon)
{
	resetearCursor();
	capturandoEnlace=false;
	window.open(generarEnlace(indiceEnlace,lat,lon));
}

function enlazar()
{
	ocultarTodosDiv();
	mostrarDiv("divEnlacesMapas");
}

function cursorClick()
{
	document.getElementById('map').style.cursor = 'crosshair';
}

function resetearCursor()
{
	document.getElementById('map').style.cursor = '';
}

function establecerAlturaMapa()
{
	var mapDiv = document.getElementById('map');
	mapDiv.style.height=document.body.clientHeight-50;
}

function habilitarOpcionesEdicion()
{
	if(tipoUsuario==0)
	{
		ocultarDiv("btnNuevoElemento");
	}	
}

function mostrarTarea(tareaId)
{
	hidePopup();
	var frame=document.getElementById("tareaFrame");
	frame.src="getElementTask.php?id=" + tareaId +"&l=" + getQueryString("l");
	mostrarDiv("divTarea");
	
}

cargarListasValores();

habilitarOpcionesEdicion();