/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

function showPopup(type, title, message) {
    const overlay = document.getElementById("popupOverlay");
    const box = document.getElementById("popupBox");
    const icon = document.getElementById("popupIcon");
    const popupTitle = document.getElementById("popupTitle");
    const popupMessage = document.getElementById("popupMessage");

    if (!overlay || !box || !icon || !popupTitle || !popupMessage) {
        return;
    }

    popupTitle.textContent = title;
    popupMessage.textContent = message;

    box.classList.remove("success", "error", "warning");

    if (type === "success") {
        box.classList.add("success");
        icon.textContent = "✓";
    } else if (type === "error") {
        box.classList.add("error");
        icon.textContent = "✕";
    } else {
        box.classList.add("warning");
        icon.textContent = "!";
    }

    overlay.classList.add("show");
}

function closePopup() {
    const overlay = document.getElementById("popupOverlay");
    if (overlay) {
        overlay.classList.remove("show");
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const bookingPage = document.querySelector(".booking-detail-page");

    if (!bookingPage) {
        return;
    }

    const cancelStatus = bookingPage.dataset.cancelStatus;

    if (cancelStatus === "success") {
        showPopup(
            "success",
            "Cancel Successful",
            "Your booking has been cancelled successfully."
        );
    } else if (cancelStatus === "fail") {
        showPopup(
            "error",
            "Cancel Failed",
            "Unable to cancel this booking."
        );
    } else if (cancelStatus === "error") {
        showPopup(
            "error",
            "System Error",
            "Something went wrong while cancelling the booking."
        );
    }

    const overlay = document.getElementById("popupOverlay");
    if (overlay) {
        overlay.addEventListener("click", function (event) {
            if (event.target === overlay) {
                closePopup();
            }
        });
    }
});
