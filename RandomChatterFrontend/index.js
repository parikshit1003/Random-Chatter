
let token = '';

let syncState = 0;

let intervalId;

window.addEventListener('load', (event) => {
  terminateChat(window.sessionStorage.getItem('token'));
  window.sessionStorage.clear();
});

function initializeChat() {
  fetch("http://localhost:8080/init", {
      method: "GET"
    })
    .then((response) => response.text())
    .then((text) => {
      window.sessionStorage.setItem('token', text);
    });
}

document.getElementById('initialize-chat').onclick = function() {

  document.getElementById('initialize-chat').disabled = true;
  document.getElementById('terminate-chat').disabled = false;

  initializeChat();

  intervalId = setInterval(receiveMessage, 5000);
}

document.getElementById('refresh-chat').onclick = function() {
  receiveMessage();
}

document.getElementById('terminate-chat').onclick = function() {

  document.getElementById('terminate-chat').disabled = true;
  document.getElementById('initialize-chat').disabled = false;

  terminateChat(window.sessionStorage.getItem('token'));
  window.sessionStorage.clear();
}

document.getElementById('send-message').onclick = function() {

  let message = document.getElementById('message-input').value;

  if (message == '') {
    return;
  }

  const message_box = document.createElement('p');
  message_box.innerHTML = message;
  message_box.classList.add('message-box-black');
  document.getElementById('chat-box').appendChild(message_box);
  document.getElementById('message-input').value = '';

  window.scrollTo({ top : document.body.scrollHeight, behavior: 'smooth' });

  token = window.sessionStorage.getItem('token');

  fetch("http://localhost:8080/messages/send/" + message, {
    method: 'POST',
    headers: {
      "SessionToken": token
    }
  }).then(response => {
    if (!response.ok) {
      throw new Error(`HTTP Error! Status: ${response.status}`);
    }
    return response.arrayBuffer();
  })
  .catch(error => {
    alert('Error Sending Message!', error);
  });
}

function receiveMessage() {

  token = window.sessionStorage.getItem('token');

  console.log(token);

  fetch("http://localhost:8080/messages/receive/" + syncState, {
    method: 'GET',
    headers: {
      "SessionToken": token
    }
  })
  .then((response) => response.json())
  .then((json) => handleReceivedMessages(json));
}

function terminateChat(sessionToken) {

  if (sessionToken == null) {
    return;
  }

  if (intervalId != null) {
    clearInterval(intervalId);
    intervalId = null;
  }

  syncState = 0;

  document.getElementById('chat-box').replaceChildren();
  const spacing = document.createElement('div');
  spacing.classList.add('blank-spacing');
  document.getElementById('chat-box').appendChild(spacing);

  fetch("http://localhost:8080/terminate", {
    method: 'DELETE',
    headers: {
      "SessionToken": sessionToken
    }
  })
  .then((response) => {
    if (!response.ok) {
      throw new Error(`HTTP Error! Status: ${response.status}`);
    }
    return response.arrayBuffer();
  })
  .catch(error => {
    alert('Error Terminating Session!', error);
  });
}

function handleReceivedMessages(json) {

  if (json == null) {
    return;
  }

  syncState = json["syncState"];

  if(json["messages"] == null) {
    return;
  }

  for (let i = 0; i < json["messages"].length; i++) {
    const message_box = document.createElement('p');
    message_box.innerHTML = json["messages"][i];
    message_box.classList.add('message-box-white');
    document.getElementById('chat-box').appendChild(message_box);
  }

  window.scrollTo({ top : document.body.scrollHeight, behavior: 'smooth' });
}
