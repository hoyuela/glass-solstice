function changeViewport(width, height) {
	var viewport = document.querySelector("meta[name=viewport]");
	viewport.setAttribute('content', 'width=' + width + '; height='+ height + '; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;');	
}
