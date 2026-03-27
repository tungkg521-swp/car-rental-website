document.addEventListener("DOMContentLoaded", function () {
    initGallery();
    initRentalCalendar();
    initVerifyModalAutoOpen();
    initReviewEditButtons();
});

/* =========================
 GALLERY
 ========================= */
function initGallery() {
    const mainImage = document.getElementById("mainCarImage");
    if (!mainImage) {
        return;
    }

    const thumbnails = Array.from(document.querySelectorAll(".thumbs img"));
    if (thumbnails.length === 0) {
        return;
    }

    let currentIndex = 0;

    function updateMainImage(index) {
        if (index < 0 || index >= thumbnails.length) {
            return;
        }

        currentIndex = index;
        mainImage.src = thumbnails[index].src;

        thumbnails.forEach(function (thumb, i) {
            thumb.classList.toggle("active", i === index);
        });
    }

    thumbnails.forEach(function (thumb, index) {
        thumb.addEventListener("click", function () {
            updateMainImage(index);
        });
    });

    const prevButton = document.querySelector(".main-image .prev");
    const nextButton = document.querySelector(".main-image .next");

    if (prevButton) {
        prevButton.addEventListener("click", function () {
            updateMainImage((currentIndex - 1 + thumbnails.length) % thumbnails.length);
        });
    }

    if (nextButton) {
        nextButton.addEventListener("click", function () {
            updateMainImage((currentIndex + 1) % thumbnails.length);
        });
    }

    updateMainImage(0);
}

/* =========================
 RENTAL CALENDAR
 ========================= */
function initRentalCalendar() {
    const openButton = document.getElementById("openRentalModal");
    const modal = document.getElementById("rentalTimeModal");
    const closeButton = document.getElementById("closeRentalModal");
    const confirmButton = document.getElementById("confirmRentalSelection");
    const calendarEl = document.getElementById("rentalCalendar");

    const startInput = document.getElementById("startDate");
    const endInput = document.getElementById("endDate");

    const startDisplay = document.getElementById("displayStartDate");
    const endDisplay = document.getElementById("displayEndDate");
    const selectedPeriodValue = document.getElementById("selectedPeriodValue");
    const bookingButton = document.getElementById("bookNowBtn");
    const errorBox = document.getElementById("calendarDateError");

    if (!modal || !calendarEl || !startInput || !endInput) {
        return;
    }

    const busyDates = parseBusyDates(calendarEl.dataset.busyDates);
    const busyDateSet = new Set(busyDates);

    let tempStartDate = startInput.value || "";
    let tempEndDate = endInput.value || "";

    let fp = null;

    function createCalendar() {
        if (fp) {
            return;
        }

        const initialDates = [];
        if (tempStartDate) {
            initialDates.push(tempStartDate);
        }
        if (tempEndDate) {
            initialDates.push(tempEndDate);
        }

        fp = flatpickr(calendarEl, {
            inline: true,
            mode: "range",
            minDate: "today",
            dateFormat: "Y-m-d",
            defaultDate: initialDates,
            showMonths: 2,
            monthSelectorType: "static",
            disable: busyDates,
            onReady: function (selectedDates, dateStr, instance) {
                syncSelectedDates(selectedDates);
                markBusyDays(instance);
                updateSelectedDisplay();
            },
            onMonthChange: function (selectedDates, dateStr, instance) {
                markBusyDays(instance);
            },
            onYearChange: function (selectedDates, dateStr, instance) {
                markBusyDays(instance);
            },
            onChange: function (selectedDates, dateStr, instance) {
                syncSelectedDates(selectedDates);
                markBusyDays(instance);
                updateSelectedDisplay();
            }
        });
    }

    function openModal() {
        modal.classList.add("show");
        document.body.classList.add("modal-open");

        setTimeout(function () {
            createCalendar();
            fp.redraw();
            markBusyDays(fp);
        }, 100);
    }

    function closeModal() {
        modal.classList.remove("show");
        document.body.classList.remove("modal-open");
        hideCalendarError();
    }

    function showCalendarError(message) {
        if (!errorBox) {
            return;
        }
        errorBox.textContent = message;
        errorBox.style.display = "block";
    }

    function hideCalendarError() {
        if (!errorBox) {
            return;
        }
        errorBox.textContent = "";
        errorBox.style.display = "none";
    }

    function syncSelectedDates(selectedDates) {
        hideCalendarError();

        if (!selectedDates || selectedDates.length === 0) {
            tempStartDate = "";
            tempEndDate = "";
            return;
        }

        tempStartDate = selectedDates[0] ? formatLocalDate(selectedDates[0]) : "";
        tempEndDate = selectedDates[1] ? formatLocalDate(selectedDates[1]) : "";
    }

    function updateSelectedDisplay() {
        if (startDisplay) {
            startDisplay.textContent = tempStartDate || "Chọn ngày nhận xe";
        }

        if (endDisplay) {
            endDisplay.textContent = tempEndDate || "Chọn ngày trả xe";
        }

        if (selectedPeriodValue) {
            if (tempStartDate && tempEndDate) {
                selectedPeriodValue.textContent = tempStartDate + " đến " + tempEndDate;
            } else if (tempStartDate) {
                selectedPeriodValue.textContent = tempStartDate;
            } else {
                selectedPeriodValue.textContent = "Chưa chọn thời gian thuê";
            }
        }

        updateBookingButtonState();
    }

    function updateBookingButtonState() {
        if (!bookingButton) {
            return;
        }

        const isCarAvailable = bookingButton.dataset.carAvailable === "true";
        const hasFullDate = tempStartDate !== "" && tempEndDate !== "";
        const canBook = isCarAvailable && hasFullDate;

        bookingButton.disabled = !canBook;
        bookingButton.classList.toggle("disabled", !canBook);
    }

    function hasBusyDateInRange(startDateText, endDateText) {
        if (!startDateText || !endDateText) {
            return false;
        }

        const startDate = new Date(startDateText + "T00:00:00");
        const endDate = new Date(endDateText + "T00:00:00");
        const checkingDate = new Date(startDate);

        while (checkingDate <= endDate) {
            const dateText = formatLocalDate(checkingDate);
            if (busyDateSet.has(dateText)) {
                return true;
            }
            checkingDate.setDate(checkingDate.getDate() + 1);
        }

        return false;
    }

    function applySelection() {
        hideCalendarError();

        if (!tempStartDate || !tempEndDate) {
            showCalendarError("Vui lòng chọn đầy đủ ngày nhận xe và ngày trả xe.");
            return;
        }

        if (hasBusyDateInRange(tempStartDate, tempEndDate)) {
            showCalendarError("Khoảng thời gian bạn chọn có ngày xe bận. Vui lòng chọn lại.");
            return;
        }

        startInput.value = tempStartDate;
        endInput.value = tempEndDate;

        updateSelectedDisplay();
        closeModal();
    }

    function markBusyDays(instance) {
        if (!instance || !instance.daysContainer) {
            return;
        }

        const dayElements = instance.daysContainer.querySelectorAll(".flatpickr-day");

        dayElements.forEach(function (dayEl) {
            dayEl.classList.remove("busy-day");

            const dateObj = dayEl.dateObj;
            if (!dateObj) {
                return;
            }

            const dateText = formatLocalDate(dateObj);
            if (busyDateSet.has(dateText)) {
                dayEl.classList.add("busy-day");
            }
        });
    }

    if (openButton) {
        openButton.addEventListener("click", function (event) {
            event.preventDefault();
            openModal();
        });

        openButton.addEventListener("keydown", function (event) {
            if (event.key === "Enter" || event.key === " ") {
                event.preventDefault();
                openModal();
            }
        });
    }

    if (closeButton) {
        closeButton.addEventListener("click", closeModal);
    }

    if (confirmButton) {
        confirmButton.addEventListener("click", applySelection);
    }

    modal.addEventListener("click", function (event) {
        if (event.target === modal) {
            closeModal();
        }
    });

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape" && modal.classList.contains("show")) {
            closeModal();
        }
    });

    updateSelectedDisplay();
}

/* =========================
 REVIEW EDIT
 ========================= */
function initReviewEditButtons() {
    document.querySelectorAll(".review-edit").forEach(function (button) {
        button.addEventListener("click", function () {
            openEditReviewModal(
                    button.dataset.reviewId,
                    button.dataset.rating,
                    button.dataset.comment
                    );
        });
    });
}

/* =========================
 VERIFY MODAL AUTO OPEN
 ========================= */
function initVerifyModalAutoOpen() {
    const verifyModal = document.getElementById("licenseVerifyModal");
    if (!verifyModal) {
        return;
    }

    const shouldOpen = verifyModal.dataset.open === "true";
    if (shouldOpen) {
        verifyModal.style.display = "flex";
    }

    const closeButtons = verifyModal.querySelectorAll("[data-close-verify]");
    closeButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            verifyModal.style.display = "none";
        });
    });

    verifyModal.addEventListener("click", function (event) {
        if (event.target === verifyModal) {
            verifyModal.style.display = "none";
        }
    });
}

/* =========================
 HELPERS
 ========================= */
function parseBusyDates(rawValue) {
    if (!rawValue) {
        return [];
    }

    try {
        const parsed = JSON.parse(rawValue);
        if (Array.isArray(parsed)) {
            return parsed.filter(function (item) {
                return typeof item === "string" && item.trim() !== "";
            });
        }
    } catch (error) {
        console.log("Busy dates parse error:", error);
    }

    return [];
}

function formatLocalDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return year + "-" + month + "-" + day;
}