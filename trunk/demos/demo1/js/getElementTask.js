function cerrar()
{	
	var frame=parent.document.getElementById("tareaFrame");
	frame.src="";
	parent.ocultarDiv("divTarea");
}

function guardarCambioEstadoTarea()
{
	parent.eliminarCapas();
		
	var id = getQueryString("id");
	var selectTaskState= document.getElementById("selectTaskState");	
		
	var oReq = new XMLHttpRequest();
	oReq.open("get", "saveTask.php?id=" + id + "&stateid=" + selectTaskState.value , false);    	
	oReq.send();
	
	parent.actualizarElementos();
	cerrar();
}

