


const inputField = document.getElementById("chat-input");
const outputArea = document.getElementById("chat-area");

const socket =
  new WebSocket(document.getElementById("ws-route").value.replace("http","ws"));

const myID = document.getElementById("myID").innerHTML
socket.onopen = (event) => socket.send("New User Connected.");

function addMessage() {
	var j = document.getElementById('chat-input').value;
	socket.send(j + (" " + myID));
	document.getElementById('chat-input').value = '';
}

socket.onmessage = (event) => {

  if (event.data === "New User Connected.") {
    outputArea.innerHTML += '<p>' + event.data + '</p>';
  }
  // } else if (myID === ) {
  // 	outputArea.innerHTML += '<p>' + event.data + '</p>';
  // }
}
