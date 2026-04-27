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

    let calendarErrorTimer = null;

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
    let tempEndHour = endHourInput.value || "23:00";

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
        modalEndHour.value = tempEndHour || "23:00";

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

        if (calendarErrorTimer) {
            clearTimeout(calendarErrorTimer);
        }

        calendarErrorTimer = setTimeout(function () {
            hideCalendarError();
        }, 3500);
    }

    function hideCalendarError() {
        if (!errorBox) {
            return;
        }

        errorBox.textContent = "";
        errorBox.style.display = "none";

        if (calendarErrorTimer) {
            clearTimeout(calendarErrorTimer);
            calendarErrorTimer = null;
        }
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

        const hasFullSelection = !!(tempStartDate && tempStartHour && tempEndDate && tempEndHour);

        bookingButton.disabled = !hasFullSelection;
        bookingButton.classList.toggle("disabled", !hasFullSelection);
    }

    function validateRentalDateTime(startDateValue, startHourValue, endDateValue, endHourValue) {
        if (!startDateValue || !startHourValue || !endDateValue || !endHourValue) {
            return "Vui lòng chọn đầy đủ ngày và giờ nhận/trả xe.";
        }

        const startDateTime = startDateValue + "T" + startHourValue;
        const endDateTime = endDateValue + "T" + endHourValue;

        const startDate = new Date(startDateTime);
        const endDate = new Date(endDateTime);
        const now = new Date();

        if (isNaN(startDate.getTime()) || isNaN(endDate.getTime())) {
            return "Thời gian thuê không hợp lệ. Vui lòng kiểm tra lại.";
        }

        if (startDate < now) {
            return "Thời gian nhận xe không được nhỏ hơn thời điểm hiện tại.";
        }

        if (endDate <= startDate) {
            return "Thời gian trả xe phải sau thời gian nhận xe.";
        }

        if (endDate - startDate < 60 * 60 * 1000) {
            return "Thời gian thuê tối thiểu là 1 giờ.";
        }

        if (hasTimeConflict(startDateTime, endDateTime)) {
            return "Khoảng thời gian bạn chọn xe đã bận. Vui lòng chọn thời gian khác.";
        }

        return "";
    }

    function applySelection() {
        hideCalendarError();

        const errorMessage = validateRentalDateTime(
                tempStartDate,
                tempStartHour,
                tempEndDate,
                tempEndHour
                );

        if (errorMessage) {
            showCalendarError(errorMessage);
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
            const hasFullSelection = !!(
                    startInput.value &&
                    startHourInput.value &&
                    endInput.value &&
                    endHourInput.value
                    );

            if (!hasFullSelection) {
                openModal();
                showCalendarError("Vui lòng chọn đầy đủ ngày và giờ nhận/trả xe.");
                return;
            }

            const errorMessage = validateRentalDateTime(
                    startInput.value,
                    startHourInput.value,
                    endInput.value,
                    endHourInput.value
                    );

            if (errorMessage) {
                tempStartDate = startInput.value || "";
                tempStartHour = startHourInput.value || "00:00";
                tempEndDate = endInput.value || "";
                tempEndHour = endHourInput.value || "23:00";

                openModal();
                showCalendarError(errorMessage);
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
        const blockedRange = getBlockedTimeRange(item);

        const li = document.createElement("li");
        li.textContent = formatDateTimeVN(blockedRange.start)
                + " → "
                + formatDateTimeVN(blockedRange.end);

        listEl.appendChild(li);
    });
}

function getBlockedTimeRange(item) {
    const type = (item.type || item.source || "BOOKING").toUpperCase();

    if (type === "MAINTENANCE") {
        return {
            start: item.start,
            end: item.end
        };
    }

    return {
        start: addHours(item.start, -4),
        end: addHours(item.end, 4)
    };
}

function addHours(dateValue, hours) {
    const date = new Date(dateValue);

    if (isNaN(date.getTime())) {
        return dateValue;
    }

    date.setHours(date.getHours() + hours);

    return toLocalDateTimeValue(date);
}

function toLocalDateTimeValue(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hour = String(date.getHours()).padStart(2, "0");
    const minute = String(date.getMinutes()).padStart(2, "0");

    return year + "-" + month + "-" + day + "T" + hour + ":" + minute;
}

function hasTimeConflict(startValue, endValue) {
    const ranges = parseBusyTimeRanges();

    const start = new Date(startValue);
    const end = new Date(endValue);

    if (isNaN(start.getTime()) || isNaN(end.getTime())) {
        return false;
    }

    return ranges.some(function (item) {
        const blockedRange = getBlockedTimeRange(item);

        const busyStart = new Date(blockedRange.start);
        const busyEnd = new Date(blockedRange.end);

        if (isNaN(busyStart.getTime()) || isNaN(busyEnd.getTime())) {
            return false;
        }

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