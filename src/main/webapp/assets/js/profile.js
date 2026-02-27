function openModal() {
    const modal = document.getElementById("updateModal");
    if (modal) {
        modal.style.display = "flex";
    }
}

function closeModal() {
    const modal = document.getElementById("updateModal");
    if (modal) {
        modal.style.display = "none";
    }
}

setTimeout(() => {
    const alertBox = document.querySelector('.alert');
    if (alertBox) {
        alertBox.style.display = 'none';
    }
}, 3000);