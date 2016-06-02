<?php
	$currentLanguageCode = "en";
	$currentLanguage;
	
	if (session_status() == PHP_SESSION_NONE) {
			session_start();
		}
	
	function setLanguage($lang)
	{	
		global $currentLanguage;
		global $currentLanguageCode;	
		
		if(!isset($_SESSION['lang']))
		{
			$_SESSION['lang'] = $currentLanguageCode ;
		}
				
		$currentLanguageCode=$lang;	
		$_SESSION["lang"] = $currentLanguageCode;
		
		switch($currentLanguageCode)
		{
			case "es":
			{
				$currentLanguage=[
					"user" => "Usuario",
					"password" => "Contraseña",
					"login" => "Iniciar sesión",
					"userNoValid" => "El usuario y contraseña no son válidos",
					"streets" => "Calles",
					"grayScale" => "Escala de grises",
					"satellite" => "Satelite",
					"type" => "Tipo",
					"model" => "Modelo",
					"fields" => "Campos",
					"move" => "Mover",
					"image" => "Imagen",
					"save" => "Guardar",
					"delete" => "Eliminar",
					"deleteConfirm" => "¿Seguro que desea eliminar este elemento?",
					"centerElements" => "Centrar todos los elementos",
					"update" => "Actualizar",
					"filterSearch" => "Filtrar/Buscar",
					"listElements" => "Lista de elementos",
					"newElement" => "Nuevo elemento",
					"link" => "Enlazar a mapas",
					"showRoutes" => "Mostar rutas",
					"textFilter" => "Establecer texto para filtrar",
					"filter" => "Filtrar",
					"cancel" => "Cancelar",
					"close" => "Cerrar",
					"selectType" => "Seleccione tipo de elemento",
					"create" => "Crear",
					"selectMapService" => "Seleccionar servicio de mapas",
					"selectImage" => "Seleccione imagen para actualizar",
					"updateImage" => "Actualizar imagen",
					"viewIncident" => "Ver incidencia",
					"heatMap" => "Mapa de calor",
				];		
				break;				
			}
			case "eu":
			{
				$currentLanguage=[
					"user" => "Erabiltzaile",
					"password" => "Pasahitza",
					"login" => "Saioa hasi",
					"userNoValid" => "Erabiltzaile eta pasahiza ez dira baliozkoak",
					"streets" => "Kaleak",
					"grayScale" => "Gris-eskala",
					"satellite" => "Satelite",
					"type" => "Mota",
					"model" => "Modelo",
					"fields" => "Eremuak",
					"move" => "Mugitu",
					"image" => "Irudia",
					"save" => "Gorde",
					"delete" => "Ezabatu",
					"deleteConfirm" => "Ziur zaude elementu hau ezabatu nahi duzula?",
					"centerElements" => "Elementu guztiak erdiratu",
					"update" => "Eguneratu",
					"filterSearch" => "Hautatu/Bilatu",
					"listElements" => "Elementu zerrenda",
					"newElement" => "Elementu berria",
					"link" => "Mapak estekatu",
					"showRoutes" => "Ibilbideak erakutsi",
					"textFilter" => "Bilatuko textua jarri",
					"filter" => "Hautatu",
					"cancel" => "Utzi",
					"close" => "Itxi",
					"selectType" => "Elementu mota aukeratu",
					"create" => "Sortu",
					"selectMapService" => "Mapa zerbitzu aukeratu",
					"selectImage" => "Irudi berria aukeratu",
					"updateImage" => "Irudia eguneratu",
					"viewIncident" => "Intzidenzia ikusi",
					"heatMap" => "Bero mapa",
				];		
				break;				
			}
			default:
			{
				$currentLanguage=[
					"user" => "User",
					"password" => "Password",
					"login" => "Login",
					"userNoValid" => "User and password are not valid",
					"streets" => "Streets",
					"grayScale" => "Grey scale",
					"satellite" => "Satellite",
					"type" => "Type",
					"model" => "Model",
					"fields" => "Fields",
					"move" => "Move",
					"image" => "Image",
					"save" => "Save",
					"delete" => "Delete",
					"deleteConfirm" => "Are you sure you want to delete this element?",
					"centerElements" => "Center all elements",
					"update" => "Update",
					"filterSearch" => "Filter/Search",
					"listElements" => "List elements",
					"newElement" => "New element",
					"link" => "Link to maps",
					"showRoutes" => "Show routes",
					"textFilter" => "Set filter text",
					"filter" => "Filter",
					"cancel" => "Cancel",
					"close" => "Close",
					"selectType" => "Select element type",
					"create" => "Create",
					"selectMapService" => "Select mapping service",
					"selectImage" => "Select image for update",
					"updateImage" => "Update image",
					"viewIncident" => "View incident",
					"heatMap" => "Heat map",
				];		
				break;
			}			
		}		
	}	
	function loc($key)
	{		
		echo locPhp($key);
	}
	
	function locPhp($key)
	{			
		global $currentLanguage;
		global $currentLanguageCode;
		if($currentLanguage==null)
		{
			setLanguage($currentLanguageCode);
		}
			
		return $currentLanguage[$key];
	}
?>