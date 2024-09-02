function enterConversation() {
    const nameInput = document.getElementById('name-input');
    const text = nameInput.value;
    if (text) {
        window.location.href = '/chatbot/conversation?name=' + encodeURIComponent(text);
    }
}