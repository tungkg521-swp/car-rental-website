document.addEventListener("DOMContentLoaded", function () {
    initGallery();
    initRentalCalendar();
    initVerifyModalAutoOpen();
    initReviewEditButtons();
    renderBusyTimeRanges();
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
 RENTAL CALENDAR + HOUR SELECT
 ========================= */
function initRentalCalendar() {
    const openButton = document.getElementById("openRentalModal");
    const modal = document.getElementById("rentalTimeModal");
    const closeButton = document.getElementById("closeRentalModal");
    const confirmButton = document.getElementById("confirmRentalSelection");
    const bookingButton = document.getElementById("bookNowBtn");

    const bookingForm = document.getElementById("bookingFromDetailForm");

    const startInput = document.getElementById("startDate");
    const startHourInput = document.getElementById("startHour");
    const endInput = document.getElementById("endDate");
    const endHourInput = document.getElementById("endHour");

    const calendarEl = document.getElementById("rentalCalendar");
    const modalStartHour = document.getElementById("modalStartHour");
    const modalEndHour = document.getElementById("modalEndHour");

    const startDisplay = document.getElementById("displayStartDate");
    const endDisplay = document.getElementById("displayEndDate");
    const selectedPeriodValue = document.getElementById("selectedPeriodValue");
    const errorBox = document.getElementById("calendarDateError");

    if (
            !modal ||
            !calendarEl ||
            !startInput ||
            !startHourInput ||
            !endInput ||
            !endHourInput ||
            !modalStartHour ||
            !modalEndHour
            ) {
        return;
    }

    const busyDates = parseBusyDates(calendarEl.dataset.busyDates);
    const busyDateSet = new Set(busyDates);

    let tempStartDate = startInput.value || "";
    let tempEndDate = endInput.value || "";
    let tempStartHour = startHourInput.value || "00:00";
    let tempEndHour = endHourInput.value || "23:59";

    let fp = null;

    function createCalendar() {
        if (fp) {
            return;
        }

        const defaultDates = [];
        if (tempStartDate) {
            defaultDates.push(tempStartDate);
        }
        if (tempEndDate) {
            defaultDates.push(tempEndDate);
        }

        fp = flatpickr(calendarEl, {
            inline: true,
            mode: "range",
            minDate: "today",
            dateFormat: "Y-m-d",
            defaultDate: defaultDates,
            showMonths: 2,
            monthSelectorType: "static",
            onDayCreate: function (dObj, dStr, instance, dayElem) {
                if (!dayElem || !dayElem.dateObj) {
                    return;
                }

                const dateText = formatLocalDate(dayElem.dateObj);
                if (busyDateSet.has(dateText)) {
                    dayElem.classList.add("busy-day");
                }
            },
            onChange: function (selectedDates) {
                tempStartDate = selectedDates[0] ? formatLocalDate(selectedDates[0]) : "";
                tempEndDate = selectedDates[1] ? formatLocalDate(selectedDates[1]) : "";
                hideCalendarError();
                updateSelectedDisplay();
            }
        });
    }

    function openModal() {
        modal.classList.add("show");
        document.body.classList.add("modal-open");

        modalStartHour.value = tempStartHour || "00:00";
        modalEndHour.value = tempEndHour || "23:59";

        setTimeout(function () {
            createCalendar();
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

    function updateSelectedDisplay() {
        const startText = tempStartDate && tempStartHour ? tempStartDate + "T" + tempStartHour : "";
        const endText = tempEndDate && tempEndHour ? tempEndDate + "T" + tempEndHour : "";

        if (startDisplay) {
            startDisplay.textContent = startText ? formatDateTimeVN(startText) : "Chọn ngày giờ nhận xe";
        }

        if (endDisplay) {
            endDisplay.textContent = endText ? formatDateTimeVN(endText) : "Chọn ngày giờ trả xe";
        }

        if (selectedPeriodValue) {
            if (startText && endText) {
                selectedPeriodValue.textContent = formatDateTimeVN(startText) + " đến " + formatDateTimeVN(endText);
            } else if (startText) {
                selectedPeriodValue.textContent = formatDateTimeVN(startText);
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
        const hasFullSelection = !!(tempStartDate && tempStartHour && tempEndDate && tempEndHour);

        bookingButton.disabled = !(isCarAvailable && hasFullSelection);
        bookingButton.classList.toggle("disabled", !(isCarAvailable && hasFullSelection));
    }

    function applySelection() {
        hideCalendarError();

        if (!tempStartDate || !tempStartHour || !tempEndDate || !tempEndHour) {
            showCalendarError("Vui lòng chọn đầy đủ ngày và giờ nhận/trả xe.");
            return;
        }

        const startDateTime = tempStartDate + "T" + tempStartHour;
        const endDateTime = tempEndDate + "T" + tempEndHour;

        const startDate = new Date(startDateTime);
        const endDate = new Date(endDateTime);
        const now = new Date();

        if (isNaN(startDate.getTime()) || isNaN(endDate.getTime())) {
            showCalendarError("Thời gian thuê không hợp lệ.");
            return;
        }

        if (startDate < now) {
            showCalendarError("Thời gian nhận xe không được ở quá khứ.");
            return;
        }

        if (endDate <= startDate) {
            showCalendarError("Thời gian trả xe phải sau thời gian nhận xe.");
            return;
        }

        if (endDate - startDate < 60 * 60 * 1000) {
            showCalendarError("Thời gian thuê tối thiểu là 1 giờ.");
            return;
        }

        if (hasTimeConflict(startDateTime, endDateTime)) {
            showCalendarError("Khoảng thời gian bạn chọn trùng với lịch đã được đặt. Vui lòng chọn lại.");
            return;
        }

        startInput.value = tempStartDate;
        startHourInput.value = tempStartHour;
        endInput.value = tempEndDate;
        endHourInput.value = tempEndHour;

        updateSelectedDisplay();
        closeModal();
    }

    modalStartHour.addEventListener("change", function () {
        tempStartHour = modalStartHour.value || "";
        hideCalendarError();
        updateSelectedDisplay();
    });

    modalEndHour.addEventListener("change", function () {
        tempEndHour = modalEndHour.value || "";
        hideCalendarError();
        updateSelectedDisplay();
    });

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

    if (bookingButton) {
        bookingButton.addEventListener("click", function () {
            const isCarAvailable = bookingButton.dataset.carAvailable === "true";

            if (!isCarAvailable) {
                return;
            }

            const hasFullSelection = !!(
                    startInput.value &&
                    startHourInput.value &&
                    endInput.value &&
                    endHourInput.value
                    );

            if (!hasFullSelection) {
                openModal();
                return;
            }

            if (bookingForm) {
                bookingForm.submit();
            }
        });
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

function parseBusyTimeRanges() {
    const dataEl = document.getElementById("busyTimeRangesData");
    if (!dataEl) {
        return [];
    }

    try {
        const parsed = JSON.parse(dataEl.textContent || "[]");
        if (Array.isArray(parsed)) {
            return parsed.filter(function (item) {
                return item && item.start && item.end;
            });
        }
    } catch (error) {
        console.log("Busy time ranges parse error:", error);
    }

    return [];
}

function renderBusyTimeRanges() {
    const listEl = document.getElementById("busyTimeList");
    if (!listEl) {
        return;
    }

    const ranges = parseBusyTimeRanges();
    listEl.innerHTML = "";

    if (!Array.isArray(ranges) || ranges.length === 0) {
        listEl.innerHTML = "<li>Hiện chưa có lịch bận.</li>";
        return;
    }

    ranges.forEach(function (item) {
        const li = document.createElement("li");
        li.textContent = formatDateTimeVN(item.start) + " → " + formatDateTimeVN(item.end);
        listEl.appendChild(li);
    });
}

function hasTimeConflict(startValue, endValue) {
    const ranges = parseBusyTimeRanges();

    const start = new Date(startValue);
    const end = new Date(endValue);

    if (isNaN(start.getTime()) || isNaN(end.getTime())) {
        return false;
    }

    return ranges.some(function (item) {
        const busyStart = new Date(item.start);
        const busyEnd = new Date(item.end);

        return busyStart < end && busyEnd > start;
    });
}

function formatLocalDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return year + "-" + month + "-" + day;
}

function formatDateTimeVN(dateStr) {
    if (!dateStr) {
        return "--/--/---- --:--";
    }

    const date = new Date(dateStr);
    if (isNaN(date.getTime())) {
        return "--/--/---- --:--";
    }

    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = date.getFullYear();
    const hour = String(date.getHours()).padStart(2, "0");
    const minute = String(date.getMinutes()).padStart(2, "0");

    return `${day}/${month}/${year} ${hour}:${minute}`;
}