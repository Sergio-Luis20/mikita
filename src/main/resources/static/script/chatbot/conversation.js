function sendMessage() {
    const conversationInput = document.getElementById('conversation-input');
    const text = conversationInput.value;
    if (text) {
        fetch('/chatbot/name', {
            method: 'GET'
        }).then((response) => {
            if (!response.ok) {
                alert('Não foi possível obter o nome: ' + response.status);
                return;
            }
            return response.text();
        }).then((name) => {
            const date = new Date().toISOString();

            const conversationArea = document.getElementById('conversation-area');

            if (conversationArea.value) {
                conversationArea.value += '\n' + formatMessage(name, date, text);
            } else {
                conversationArea.value += formatMessage(name, date, text);
            }
            conversationInput.value = '';

            const url = '/chatbot/predict?timestamp=' + encodeURIComponent(date) + '&message=' + encodeURIComponent(text);

            fetch(url, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then((response) => {
                if (!response.ok) {
                    alert('Erro na requisição: ' + response.status);
                    return;
                }
                return response.json();
            }).then((result) => {
                const name = result.name;
                const timestamp = new Date(result.timestamp).toLocaleString();
                const message = result.message.text;

                conversationArea.value += '\n' + formatMessage(name, timestamp, message);
            });
        });


    }
}

function formatMessage(name, timestamp, message) {
    return '[' + timestamp + '] ' + name + ': ' + message;
}

document.addEventListener('DOMContentLoaded', () => {
    const conversationInput = document.getElementById('conversation-input');

    conversationInput.addEventListener('keydown', (event) => {
        if (event.key == 'Enter') {
            event.preventDefault();
            sendMessage();
        }
    });
});