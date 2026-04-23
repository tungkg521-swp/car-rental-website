document.addEventListener("DOMContentLoaded", function () {
    const openAboutBtn = document.getElementById("openAboutModal");
    const aboutModal = document.getElementById("aboutModal");
    const closeAboutBtn = document.getElementById("closeAboutModal");
    const aboutOverlay = aboutModal ? aboutModal.querySelector(".promo-modal-overlay") : null;

    if (openAboutBtn && aboutModal) {
        openAboutBtn.addEventListener("click", function (e) {
            e.preventDefault();
            aboutModal.classList.add("show");
            document.body.classList.add("modal-open");
        });
    }

    if (closeAboutBtn && aboutModal) {
        closeAboutBtn.addEventListener("click", function () {
            aboutModal.classList.remove("show");
            document.body.classList.remove("modal-open");
        });
    }

    if (aboutOverlay && aboutModal) {
        aboutOverlay.addEventListener("click", function () {
            aboutModal.classList.remove("show");
            document.body.classList.remove("modal-open");
        });
    }

    document.addEventListener("keydown", function (e) {
        if (e.key === "Escape" && aboutModal) {
            aboutModal.classList.remove("show");
            document.body.classList.remove("modal-open");
        }
    });
});