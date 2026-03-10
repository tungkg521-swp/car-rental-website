/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

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


// Ẩn alert sau 3s
setTimeout(() => {
    const alertBox = document.querySelector('.alert');
    if (alertBox) {
        alertBox.style.display = 'none';
    }
}, 3000);
