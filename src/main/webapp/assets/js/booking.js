// ================= GLOBAL FUNCTION =================
function validateBooking() {
    const start = document.getElementById("startDate")?.value;
    const end = document.getElementById("endDate")?.value;

    if (!start || !end) {
        alert("Vui lòng chọn đầy đủ ngày thuê");
        return false;
    }

    const [sy, sm, sd] = start.split("-").map(Number);
    const [ey, em, ed] = end.split("-").map(Number);

    const startDate = new Date(sy, sm - 1, sd);
    const endDate = new Date(ey, em - 1, ed);

    if (endDate < startDate) {
        alert("Ngày trả không được nhỏ hơn ngày thuê");
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

function calculateRentalDays(startValue, endValue) {
    if (!startValue || !endValue) {
        return 0;
    }

    const [sy, sm, sd] = startValue.split("-").map(Number);
    const [ey, em, ed] = endValue.split("-").map(Number);

    const startDate = new Date(sy, sm - 1, sd);
    const endDate = new Date(ey, em - 1, ed);

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (startDate < today || endDate < startDate) {
        return 0;
    }

    const diff = endDate - startDate;
    const days = Math.floor(diff / (1000 * 60 * 60 * 24)) + 1;

    return days;
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
        daysText.innerText = "0 ngày";
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
    const endDateInput = document.getElementById("endDate");
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

    if (!startDateInput || !endDateInput || !pricePerDayRaw) {
        return;
    }

    const startValue = startDateInput.value;
    const endValue = endDateInput.value;
    const pricePerDay = parseFloat(pricePerDayRaw.value) || 0;

    const days = calculateRentalDays(startValue, endValue);

    if (days <= 0) {
        resetSummary();
        return;
    }

    const subtotal = days * pricePerDay;

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
        daysText.innerText = days + " ngày";
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
    const endDateInput = document.getElementById("endDate");
    const voucherSelect = document.getElementById("voucherSelect");

    if (!startDateInput || !endDateInput) {
        return;
    }

    const today = new Date().toISOString().split("T")[0];
    startDateInput.setAttribute("min", today);
    endDateInput.setAttribute("min", today);

    startDateInput.addEventListener("change", function () {
        endDateInput.setAttribute("min", startDateInput.value || today);

        if (endDateInput.value && endDateInput.value < startDateInput.value) {
            endDateInput.value = "";
        }

        calculateBookingSummary();
    });

    endDateInput.addEventListener("change", calculateBookingSummary);

    if (voucherSelect) {
        voucherSelect.addEventListener("change", calculateBookingSummary);
    }

    calculateBookingSummary();
});

// ================= MODAL =================
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