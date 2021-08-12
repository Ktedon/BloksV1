

function changeImage() {
	
	var s = document.getElementById("seed").value;
	var t = document.getElementById("type").value;

	if(typeof s !== 'undefined' && s !== null) {
    	if(typeof t !== 'undefined' && t !== null) {
    		var ref = "https://avatars.dicebear.com/api/".concat(t, "/", s, ".svg")
    		document.getElementById("img").src=ref
		} else {
		}
	} else {
	}
}


function randomImage() {
	
	var s = Math.random().toString(36);
	var t = document.getElementById("type").value;

	document.getElementById("seed").value = s; 

	if(typeof s !== 'undefined' && s !== null) {
    	if(typeof t !== 'undefined' && t !== null) {
    		var ref = "https://avatars.dicebear.com/api/".concat(t, "/", s, ".svg")
    		document.getElementById("img").src=ref
		} else {
		}
	} else {
	}
}