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
    // ===== VALIDATE FORM SUBMIT =====
    const bookingForm = document.querySelector(".hero-booking-form");
    const startDateInput = document.getElementById("startDate");
    const endDateInput = document.getElementById("endDate");
    const startHourInput = document.getElementById("startHour");
    const endHourInput = document.getElementById("endHour");
    const homeDateTimeError = document.getElementById("homeDateTimeError");

    let homeErrorTimer = null;

    function showHomeDateTimeError(message) {
        if (!homeDateTimeError) {
            return;
        }

        homeDateTimeError.textContent = message;
        homeDateTimeError.style.display = "block";

        if (homeErrorTimer) {
            clearTimeout(homeErrorTimer);
        }

        homeErrorTimer = setTimeout(function () {
            hideHomeDateTimeError();
        }, 3500);
    }

    function hideHomeDateTimeError() {
        if (!homeDateTimeError) {
            return;
        }

        homeDateTimeError.textContent = "";
        homeDateTimeError.style.display = "none";

        if (homeErrorTimer) {
            clearTimeout(homeErrorTimer);
            homeErrorTimer = null;
        }
    }

    function buildDateTime(dateValue, hourValue) {
        if (!dateValue || !hourValue) {
            return null;
        }

        const dateTime = new Date(dateValue + "T" + hourValue + ":00");

        if (isNaN(dateTime.getTime())) {
            return null;
        }

        return dateTime;
    }

    function validateHomeDateTime() {
        hideHomeDateTimeError();

        if (!startDateInput || !endDateInput || !startHourInput || !endHourInput) {
            return true;
        }

        const startDateValue = startDateInput.value;
        const endDateValue = endDateInput.value;
        const startHourValue = startHourInput.value;
        const endHourValue = endHourInput.value;

        if (!startDateValue || !endDateValue) {
            showHomeDateTimeError("Vui lòng chọn đầy đủ ngày nhận xe và ngày trả xe.");
            return false;
        }

        if (!startHourValue || !endHourValue) {
            showHomeDateTimeError("Vui lòng chọn đầy đủ giờ nhận xe và giờ trả xe.");
            return false;
        }

        const startDateTime = buildDateTime(startDateValue, startHourValue);
        const endDateTime = buildDateTime(endDateValue, endHourValue);

        if (!startDateTime || !endDateTime) {
            showHomeDateTimeError("Ngày giờ thuê không hợp lệ. Vui lòng kiểm tra lại.");
            return false;
        }

        const now = new Date();

        if (startDateTime < now) {
            showHomeDateTimeError("Thời gian nhận xe không được nhỏ hơn thời điểm hiện tại.");
            return false;
        }

        if (endDateTime <= startDateTime) {
            showHomeDateTimeError("Thời gian trả xe phải sau thời gian nhận xe.");
            return false;
        }

        return true;
    }

    if (bookingForm) {
        bookingForm.addEventListener("submit", function (e) {
            if (!validateHomeDateTime()) {
                e.preventDefault();
            }
        });
    }

    [startDateInput, endDateInput, startHourInput, endHourInput].forEach(function (input) {
        if (input) {
            input.addEventListener("change", hideHomeDateTimeError);
            input.addEventListener("input", hideHomeDateTimeError);
        }
    });
});