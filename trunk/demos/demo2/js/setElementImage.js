var id=getQueryString("id");
var form= document.getElementById("setElementImageUploadForm");
form.action="setElementImageUpload.php?id=" + id;

function cerrar()
{	
	parent.ocultarDiv("divActualizarImagen");
}