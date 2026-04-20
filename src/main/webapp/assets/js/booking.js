// ================= GLOBAL FUNCTION =================
function validateBooking() {
    const start = getStartDateTimeValue();
    const end = getEndDateTimeValue();

    if (!start || !end) {
        alert("Vui lòng chọn đầy đủ thời gian thuê");
        return false;
    }

    const startDate = new Date(start);
    const endDate = new Date(end);
    const now = new Date();

    if (isNaN(startDate.getTime()) || isNaN(endDate.getTime())) {
        alert("Thời gian thuê không hợp lệ");
        return false;
    }

    if (startDate < now) {
        alert("Thời gian nhận xe không được nhỏ hơn thời điểm hiện tại");
        return false;
    }

    if (endDate <= startDate) {
        alert("Thời gian trả xe phải sau thời gian nhận xe");
        return false;
    }

    const minMillis = 60 * 60 * 1000;
    if (endDate - startDate < minMillis) {
        alert("Thời gian thuê tối thiểu là 1 giờ");
        return false;
    }

    const totalEstimatedPriceInput = document.getElementById("totalEstimatedPrice");
    if (totalEstimatedPriceInput && Number(totalEstimatedPriceInput.value || 0) <= 0) {
        alert("Tổng tiền không hợp lệ");
        return false;
    }

    return true;
}

// ================= HELPER =================
function formatMoney(number) {
    return Number(number || 0).toLocaleString("vi-VN");
}

function getStartDateTimeValue() {
    const startDate = document.getElementById("startDate")?.value || "";
    const startHour = document.getElementById("startHour")?.value || "";

    if (!startDate || !startHour) {
        return "";
    }

    return startDate + "T" + startHour;
}

function getEndDateTimeValue() {
    const endDate = document.getElementById("endDate")?.value || "";
    const endHour = document.getElementById("endHour")?.value || "";

    if (!endDate || !endHour) {
        return "";
    }

    return endDate + "T" + endHour;
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

function calculateDepositAmount(total) {
    return Number(total || 0) * 0.3;
}

function calculateRemainingAmount(total) {
    return Number(total || 0) - calculateDepositAmount(total);
}

function calculateBookingPrice(startValue, endValue, pricePerDay) {
    if (!startValue || !endValue) {
        return {
            total: 0,
            subtotal: 0,
            billableText: "0"
        };
    }

    const startDate = new Date(startValue);
    const endDate = new Date(endValue);
    const now = new Date();

    if (isNaN(startDate.getTime()) || isNaN(endDate.getTime())) {
        return {
            total: 0,
            subtotal: 0,
            billableText: "0"
        };
    }

    if (startDate < now || endDate <= startDate) {
        return {
            total: 0,
            subtotal: 0,
            billableText: "0"
        };
    }

    const diffMinutes = Math.floor((endDate - startDate) / (1000 * 60));
    if (diffMinutes < 60) {
        return {
            total: 0,
            subtotal: 0,
            billableText: "0"
        };
    }

    const pricePerHour = pricePerDay / 24;
    const halfDayPrice = pricePerDay / 2;

    const fullDays = Math.floor(diffMinutes / 1440);
    const remainMinutes = diffMinutes % 1440;

    let total = fullDays * pricePerDay;
    let billableText = fullDays > 0 ? `${fullDays} ngày` : "";

    if (remainMinutes > 0) {
        if (remainMinutes <= 360) {
            const remainHours = remainMinutes / 60;
            total += remainHours * pricePerHour;

            const hourText = Number.isInteger(remainHours)
                ? `${remainHours} giờ`
                : `${remainHours.toFixed(2)} giờ`;

            billableText = billableText
                ? `${billableText} + ${hourText}`
                : hourText;
        } else if (remainMinutes <= 720) {
            total += halfDayPrice;
            billableText = billableText
                ? `${billableText} + nửa ngày`
                : "nửa ngày";
        } else {
            total += pricePerDay;
            billableText = billableText
                ? `${billableText} + 1 ngày`
                : "1 ngày";
        }
    }

    return {
        total: total,
        subtotal: total,
        billableText: billableText || "0"
    };
}

function buildVoucherOptionText(option) {
    const code = option.getAttribute("data-code") || "";
    const discountType = option.getAttribute("data-type") || "";
    const discountValue = parseFloat(option.getAttribute("data-value")) || 0;
    const minBookingAmount = parseFloat(option.getAttribute("data-min")) || 0;

    let text = code + " - ";

    if (discountType === "PERCENT") {
        text += "Giảm " + discountValue + "%";
    } else {
        text += "Giảm " + formatMoney(discountValue) + " VND";
    }

    if (minBookingAmount > 0) {
        text += " (Tối thiểu " + formatMoney(minBookingAmount) + " VND)";
    }

    return text;
}

function resetVoucherOptionsToDefault() {
    const voucherSelect = document.getElementById("voucherSelect");
    if (!voucherSelect) {
        return;
    }

    voucherSelect.value = "";
    voucherSelect.disabled = true;

    for (let i = 1; i < voucherSelect.options.length; i++) {
        const option = voucherSelect.options[i];
        option.disabled = false;
        option.textContent = buildVoucherOptionText(option);
    }
}

function updateVoucherOptions(subtotal) {
    const voucherSelect = document.getElementById("voucherSelect");
    const voucherHint = document.getElementById("voucherHint");

    if (!voucherSelect) {
        return;
    }

    if (subtotal <= 0) {
        resetVoucherOptionsToDefault();

        if (voucherHint) {
            voucherHint.innerText = "Vui lòng chọn thời gian thuê trước để kiểm tra voucher khả dụng.";
            voucherHint.className = "text-muted d-block mt-2";
        }
        return;
    }

    voucherSelect.disabled = false;

    let hasAvailableVoucher = false;

    for (let i = 1; i < voucherSelect.options.length; i++) {
        const option = voucherSelect.options[i];

        const maxUses = parseInt(option.getAttribute("data-maxuses")) || 0;
        const usedCount = parseInt(option.getAttribute("data-usedcount")) || 0;
        const minBookingAmount = parseFloat(option.getAttribute("data-min")) || 0;

        let reason = "";
        let canUse = true;

        if (maxUses > 0 && usedCount >= maxUses) {
            canUse = false;
            reason = " - Hết lượt dùng";
        } else if (subtotal < minBookingAmount) {
            canUse = false;
            reason = " - Chưa đủ điều kiện";
        }

        option.disabled = !canUse;
        option.textContent = buildVoucherOptionText(option) + reason;

        if (canUse) {
            hasAvailableVoucher = true;
        }
    }

    if (voucherSelect.value) {
        const selectedOption = voucherSelect.options[voucherSelect.selectedIndex];
        if (selectedOption && selectedOption.disabled) {
            voucherSelect.value = "";
        }
    }

    if (voucherHint) {
        if (hasAvailableVoucher) {
            voucherHint.innerText = "Chọn voucher phù hợp để được giảm giá.";
            voucherHint.className = "text-muted d-block mt-2";
        } else {
            voucherHint.innerText = "Hiện tại không có voucher nào đủ điều kiện áp dụng.";
            voucherHint.className = "text-warning d-block mt-2";
        }
    }
}

function resetSummary() {
    const priceText = document.getElementById("priceText");
    const daysText = document.getElementById("daysText");
    const subtotalText = document.getElementById("subtotalText");
    const discountText = document.getElementById("discountText");
    const voucherCodeText = document.getElementById("voucherCodeText");
    const totalText = document.getElementById("totalText");
    const totalEstimatedPrice = document.getElementById("totalEstimatedPrice");
    const voucherHint = document.getElementById("voucherHint");

    if (priceText) {
        priceText.innerText = "0 VND";
    }
    if (daysText) {
        daysText.innerText = "0";
    }
    if (subtotalText) {
        subtotalText.innerText = "0 VND";
    }
    if (discountText) {
        discountText.innerText = "-0 VND";
    }
    if (voucherCodeText) {
        voucherCodeText.innerText = "Không có";
    }
    if (totalText) {
        totalText.innerText = "0 VND";
    }
    if (totalEstimatedPrice) {
        totalEstimatedPrice.value = "0";
    }
    if (voucherHint) {
        voucherHint.innerText = "Vui lòng chọn thời gian thuê trước để kiểm tra voucher khả dụng.";
        voucherHint.className = "text-muted d-block mt-2";
    }

    resetVoucherOptionsToDefault();
}

function calculateBookingSummary() {
    const startDateInput = document.getElementById("startDate");
    const startHourInput = document.getElementById("startHour");
    const endDateInput = document.getElementById("endDate");
    const endHourInput = document.getElementById("endHour");
    const pricePerDayRaw = document.getElementById("pricePerDayRaw");
    const voucherSelect = document.getElementById("voucherSelect");

    const priceText = document.getElementById("priceText");
    const daysText = document.getElementById("daysText");
    const subtotalText = document.getElementById("subtotalText");
    const discountText = document.getElementById("discountText");
    const voucherCodeText = document.getElementById("voucherCodeText");
    const totalText = document.getElementById("totalText");
    const totalEstimatedPrice = document.getElementById("totalEstimatedPrice");
    const voucherHint = document.getElementById("voucherHint");

    if (!startDateInput || !startHourInput || !endDateInput || !endHourInput || !pricePerDayRaw) {
        return;
    }

    const startValue = getStartDateTimeValue();
    const endValue = getEndDateTimeValue();
    const pricePerDay = parseFloat(pricePerDayRaw.value) || 0;

    const pricing = calculateBookingPrice(startValue, endValue, pricePerDay);

    if (pricing.total <= 0) {
        resetSummary();
        return;
    }

    const subtotal = pricing.subtotal;

    updateVoucherOptions(subtotal);

    let discount = 0;
    let appliedVoucherCode = "Không có";

    if (voucherSelect && voucherSelect.value !== "") {
        const selectedOption = voucherSelect.options[voucherSelect.selectedIndex];

        if (selectedOption && !selectedOption.disabled) {
            const voucherCode = selectedOption.getAttribute("data-code") || "";
            const discountType = selectedOption.getAttribute("data-type") || "";
            const discountValue = parseFloat(selectedOption.getAttribute("data-value")) || 0;
            const minBookingAmount = parseFloat(selectedOption.getAttribute("data-min")) || 0;
            const maxUses = parseInt(selectedOption.getAttribute("data-maxuses")) || 0;
            const usedCount = parseInt(selectedOption.getAttribute("data-usedcount")) || 0;

            let canApply = true;

            if (maxUses > 0 && usedCount >= maxUses) {
                canApply = false;
            }

            if (subtotal < minBookingAmount) {
                canApply = false;
            }

            if (canApply) {
                if (discountType === "PERCENT") {
                    discount = subtotal * discountValue / 100;
                } else if (discountType === "FIXED") {
                    discount = discountValue;
                }

                if (discount > subtotal) {
                    discount = subtotal;
                }

                appliedVoucherCode = voucherCode;

                if (voucherHint) {
                    voucherHint.innerText = "Voucher đã được áp dụng.";
                    voucherHint.className = "text-success d-block mt-2";
                }
            } else {
                voucherSelect.value = "";
                appliedVoucherCode = "Không có";

                if (voucherHint) {
                    voucherHint.innerText = "Voucher này hiện không đủ điều kiện áp dụng.";
                    voucherHint.className = "text-danger d-block mt-2";
                }
            }
        }
    }

    const total = subtotal - discount;

    if (priceText) {
        priceText.innerText = formatMoney(pricePerDay) + " VND";
    }
    if (daysText) {
        daysText.innerText = pricing.billableText;
    }
    if (subtotalText) {
        subtotalText.innerText = formatMoney(subtotal) + " VND";
    }
    if (discountText) {
        discountText.innerText = "-" + formatMoney(discount) + " VND";
    }
    if (voucherCodeText) {
        voucherCodeText.innerText = appliedVoucherCode;
    }
    if (totalText) {
        totalText.innerText = formatMoney(total) + " VND";
    }
    if (totalEstimatedPrice) {
        totalEstimatedPrice.value = total;
    }
}

function fillConfirmModal() {
    const startValue = getStartDateTimeValue();
    const endValue = getEndDateTimeValue();
    const noteValue = document.getElementById("bookingNote")?.value?.trim() || "";

    const pricePerDay = parseFloat(document.getElementById("pricePerDayRaw")?.value) || 0;
    const pricing = calculateBookingPrice(startValue, endValue, pricePerDay);
    const total = parseFloat(document.getElementById("totalEstimatedPrice")?.value) || 0;

    const subtotalText = document.getElementById("subtotalText")?.innerText || "0 VND";
    const discountText = document.getElementById("discountText")?.innerText || "-0 VND";
    const voucherCodeText = document.getElementById("voucherCodeText")?.innerText || "Không có";

    if (!startValue || !endValue || pricing.total <= 0) {
        return;
    }

    const deposit = calculateDepositAmount(total);
    const remaining = calculateRemainingAmount(total);

    const confirmStartDate = document.getElementById("confirmStartDate");
    const confirmEndDate = document.getElementById("confirmEndDate");
    const confirmPricePerDay = document.getElementById("confirmPricePerDay");
    const confirmDays = document.getElementById("confirmDays");
    const confirmTotal = document.getElementById("confirmTotal");

    const confirmPricePerDay2 = document.getElementById("confirmPricePerDay2");
    const confirmDays2 = document.getElementById("confirmDays2");
    const confirmSubtotal = document.getElementById("confirmSubtotal");
    const confirmDiscount = document.getElementById("confirmDiscount");
    const confirmVoucherCode = document.getElementById("confirmVoucherCode");
    const confirmTotal2 = document.getElementById("confirmTotal2");
    const confirmDeposit = document.getElementById("confirmDeposit");
    const confirmRemaining = document.getElementById("confirmRemaining");
    const confirmDiscountCorner = document.getElementById("confirmDiscountCorner");
    const confirmNoteBox = document.getElementById("confirmNoteBox");

    if (confirmStartDate) {
        confirmStartDate.innerText = formatDateTimeVN(startValue);
    }
    if (confirmEndDate) {
        confirmEndDate.innerText = formatDateTimeVN(endValue);
    }

    if (confirmPricePerDay) {
        confirmPricePerDay.innerText = formatMoney(pricePerDay) + " VND";
    }
    if (confirmDays) {
        confirmDays.innerText = pricing.billableText;
    }
    if (confirmTotal) {
        confirmTotal.innerText = formatMoney(total) + " VND";
    }

    if (confirmPricePerDay2) {
        confirmPricePerDay2.innerText = formatMoney(pricePerDay) + " VND";
    }
    if (confirmDays2) {
        confirmDays2.innerText = pricing.billableText;
    }
    if (confirmSubtotal) {
        confirmSubtotal.innerText = subtotalText;
    }
    if (confirmDiscount) {
        confirmDiscount.innerText = discountText;
    }
    if (confirmVoucherCode) {
        confirmVoucherCode.innerText = voucherCodeText;
    }
    if (confirmTotal2) {
        confirmTotal2.innerText = formatMoney(total) + " VND";
    }
    if (confirmDeposit) {
        confirmDeposit.innerText = formatMoney(deposit) + " VND";
    }
    if (confirmRemaining) {
        confirmRemaining.innerText = formatMoney(remaining) + " VND";
    }
    if (confirmDiscountCorner) {
        confirmDiscountCorner.innerText = discountText;
    }
    if (confirmNoteBox) {
        confirmNoteBox.innerText = noteValue || "Không có ghi chú";
    }
}

// ================= MAIN =================
document.addEventListener("DOMContentLoaded", function () {
    setTimeout(function () {
        const alertBox = document.querySelector(".alert-danger");
        if (alertBox) {
            alertBox.style.transition = "opacity 0.5s";
            alertBox.style.opacity = "0";

            setTimeout(function () {
                alertBox.remove();
            }, 500);
        }
    }, 3000);

    const startDateInput = document.getElementById("startDate");
    const startHourInput = document.getElementById("startHour");
    const endDateInput = document.getElementById("endDate");
    const endHourInput = document.getElementById("endHour");
    const voucherSelect = document.getElementById("voucherSelect");
    const bookingForm = document.getElementById("bookingForm");
    const openConfirmBtn = document.getElementById("openConfirmBtn");
    const finalSubmitBtn = document.getElementById("finalSubmitBtn");
    const agreePolicy = document.getElementById("agreePolicy");

    if (!startDateInput || !startHourInput || !endDateInput || !endHourInput) {
        return;
    }

    const today = new Date().toISOString().slice(0, 10);
    startDateInput.setAttribute("min", today);
    endDateInput.setAttribute("min", today);

    startDateInput.addEventListener("change", calculateBookingSummary);
    startHourInput.addEventListener("change", calculateBookingSummary);
    endDateInput.addEventListener("change", calculateBookingSummary);
    endHourInput.addEventListener("change", calculateBookingSummary);

    if (voucherSelect) {
        voucherSelect.addEventListener("change", calculateBookingSummary);
    }

    if (openConfirmBtn) {
        openConfirmBtn.addEventListener("click", function () {
            calculateBookingSummary();

            if (!validateBooking()) {
                return;
            }

            fillConfirmModal();
            openConfirmModal();
        });
    }

    if (finalSubmitBtn) {
        finalSubmitBtn.addEventListener("click", function () {
            if (agreePolicy && !agreePolicy.checked) {
                alert("Vui lòng đồng ý chính sách hủy chuyến trước khi đặt xe");
                return;
            }

            if (bookingForm) {
                bookingForm.submit();
            }
        });
    }

    calculateBookingSummary();
});

// ================= PRICE MODAL =================
function openModal() {
    const modal = document.getElementById("priceModal");
    if (modal) {
        modal.style.display = "flex";
    }
}

function closeModal() {
    const modal = document.getElementById("priceModal");
    if (modal) {
        modal.style.display = "none";
    }
}

window.addEventListener("click", function (event) {
    const modal = document.getElementById("priceModal");
    if (event.target === modal) {
        closeModal();
    }
});

// ================= CONFIRM MODAL =================
function openConfirmModal() {
    const modal = document.getElementById("confirmBookingModal");
    if (modal) {
        modal.style.display = "flex";
    }
}

function closeConfirmModal() {
    const modal = document.getElementById("confirmBookingModal");
    if (modal) {
        modal.style.display = "none";
    }
}