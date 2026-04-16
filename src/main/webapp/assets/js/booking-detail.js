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

/* GALLERY */
let galleryImages = [];
let currentImageIndex = 0;

function setMainImage(element, index) {
    const mainImage = document.getElementById("mainCarImage");
    const thumbs = document.querySelectorAll(".thumb");

    if (!mainImage || !element) {
        return;
    }

    mainImage.src = element.src;
    currentImageIndex = index;

    thumbs.forEach(function (thumb) {
        thumb.classList.remove("active-thumb");
    });

    element.classList.add("active-thumb");
}

function changeImage(step) {
    const mainImage = document.getElementById("mainCarImage");
    const thumbs = document.querySelectorAll(".thumb");

    if (!mainImage || galleryImages.length === 0) {
        return;
    }

    currentImageIndex += step;

    if (currentImageIndex < 0) {
        currentImageIndex = galleryImages.length - 1;
    }

    if (currentImageIndex >= galleryImages.length) {
        currentImageIndex = 0;
    }

    mainImage.src = galleryImages[currentImageIndex];

    thumbs.forEach(function (thumb) {
        thumb.classList.remove("active-thumb");
    });

    if (thumbs[currentImageIndex]) {
        thumbs[currentImageIndex].classList.add("active-thumb");
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const bookingPage = document.querySelector(".booking-detail-page");

    if (!bookingPage) {
        return;
    }

    /* POPUP CANCEL */
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

    /* GALLERY INIT */
    const thumbs = document.querySelectorAll(".thumb");
    const mainImage = document.getElementById("mainCarImage");

    if (thumbs.length > 0 && mainImage) {
        galleryImages = [];

        thumbs.forEach(function (thumb, index) {
            galleryImages.push(thumb.src);

            thumb.addEventListener("click", function () {
                setMainImage(thumb, index);
            });
        });

        const activeThumb = document.querySelector(".thumb.active-thumb");

        if (activeThumb) {
            currentImageIndex = Array.from(thumbs).indexOf(activeThumb);
            mainImage.src = activeThumb.src;
        } else {
            currentImageIndex = 0;
            mainImage.src = thumbs[0].src;
            thumbs[0].classList.add("active-thumb");
        }
    }
});
