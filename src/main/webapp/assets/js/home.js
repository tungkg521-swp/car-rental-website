document.addEventListener("DOMContentLoaded", function () {
    // ===== PROMO MODAL =====
    const promoCards = document.querySelectorAll(".promo-card");
    const promoModal = document.getElementById("promoModal");
    const closePromoModal = document.getElementById("closePromoModal");

    const promoModalTitle = document.getElementById("promoModalTitle");
    const promoModalSubtitle = document.getElementById("promoModalSubtitle");
    const promoModalContent = document.getElementById("promoModalContent");

    if (promoCards.length > 0 && promoModal && closePromoModal
            && promoModalTitle && promoModalSubtitle && promoModalContent) {

        promoCards.forEach(function (card) {
            card.addEventListener("click", function () {
                promoModalTitle.textContent = this.dataset.title || "";
                promoModalSubtitle.textContent = this.dataset.subtitle || "";
                promoModalContent.textContent = this.dataset.content || "";

                promoModal.classList.add("show");
                document.body.classList.add("modal-open");
            });
        });

        function closeModal() {
            promoModal.classList.remove("show");
            document.body.classList.remove("modal-open");
        }

        closePromoModal.addEventListener("click", closeModal);

        const overlay = promoModal.querySelector(".promo-modal-overlay");
        if (overlay) {
            overlay.addEventListener("click", closeModal);
        }

        document.addEventListener("keydown", function (e) {
            if (e.key === "Escape") {
                closeModal();
            }
        });
    }

    // ===== TIME DROPDOWN =====
    const dropdowns = document.querySelectorAll(".time-dropdown");

    dropdowns.forEach(function (dropdown) {
        const toggle = dropdown.querySelector(".time-dropdown-toggle");
        const options = dropdown.querySelectorAll(".time-option");
        const targetId = dropdown.dataset.target;
        const hiddenInput = document.getElementById(targetId);

        if (!toggle || !hiddenInput) {
            return;
        }

        toggle.addEventListener("click", function (e) {
            e.stopPropagation();

            dropdowns.forEach(function (otherDropdown) {
                if (otherDropdown !== dropdown) {
                    otherDropdown.classList.remove("open");
                }
            });

            dropdown.classList.toggle("open");
        });

        options.forEach(function (option) {
            option.addEventListener("click", function () {
                const value = option.dataset.value || "";

                toggle.textContent = value;
                hiddenInput.value = value;
                dropdown.classList.remove("open");
            });
        });

        if (hiddenInput.value) {
            toggle.textContent = hiddenInput.value;
        }
    });

    document.addEventListener("click", function () {
        dropdowns.forEach(function (dropdown) {
            dropdown.classList.remove("open");
        });
    });

    // ===== VALIDATE FORM SUBMIT =====
    const bookingForm = document.querySelector(".hero-booking-form");
    const startHourInput = document.getElementById("startHour");
    const endHourInput = document.getElementById("endHour");

    if (bookingForm && startHourInput && endHourInput) {
        bookingForm.addEventListener("submit", function (e) {
            if (!startHourInput.value || !endHourInput.value) {
                e.preventDefault();
                alert("Vui lòng chọn giờ nhận xe và giờ trả xe.");
            }
        });
    }
});