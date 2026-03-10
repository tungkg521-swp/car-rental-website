/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
// ===== VERIFY LICENSE MODAL =====

function openVerifyModal() {
    const modal = document.getElementById("verifyModal");
    if (modal) {
        modal.style.display = "flex";
    }
}

function closeVerifyModal() {
    const modal = document.getElementById("verifyModal");
    if (modal) {
        modal.style.display = "none";
    }
}

// đóng khi click ra ngoài
window.addEventListener("click", function (e) {
    const modal = document.getElementById("verifyModal");
    if (e.target === modal) {
        closeVerifyModal();
    }
});

