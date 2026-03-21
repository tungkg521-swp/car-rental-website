// ================= GLOBAL FUNCTION =================
function validateBooking() {
    const start = document.getElementById("startDate").value;
    const end = document.getElementById("endDate").value;

    if (!start || !end) {
        alert("Vui lòng chọn đầy đủ ngày thuê");
        return false;
    }

    const [sy, sm, sd] = start.split("-").map(Number);
    const [ey, em, ed] = end.split("-").map(Number);

    const startDate = new Date(sy, sm - 1, sd);
    const endDate = new Date(ey, em - 1, ed);

    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (startDate < today) {
        alert("Ngày thuê không được nhỏ hơn ngày hiện tại");
        return false;
    }

    if (endDate <= startDate) {
        alert("Ngày trả phải sau ngày thuê");
        return false;
    }

    return true;
}

function formatMoney(n) {
    return Number(n || 0).toLocaleString("vi-VN");
}

function formatDateVN(dateStr) {
    if (!dateStr)
        return "--/--/----";
    const [y, m, d] = dateStr.split("-");
    return `${d}/${m}/${y}`;
}

function calculateBookingData() {
    const startDateInput = document.getElementById("startDate");
    const endDateInput = document.getElementById("endDate");
    const pricePerDayRaw = document.getElementById("pricePerDayRaw");

    if (!startDateInput || !endDateInput || !pricePerDayRaw) {
        return null;
    }

    const startValue = startDateInput.value;
    const endValue = endDateInput.value;
    const pricePerDay = parseFloat(pricePerDayRaw.value) || 0;

    if (!startValue || !endValue) {
        return null;
    }

    const [sy, sm, sd] = startValue.split("-").map(Number);
    const [ey, em, ed] = endValue.split("-").map(Number);

    const startDate = new Date(sy, sm - 1, sd);
    const endDate = new Date(ey, em - 1, ed);

    const diff = endDate - startDate;
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));

    if (days <= 0) {
        return null;
    }

    const total = days * pricePerDay;

    return {
        startValue,
        endValue,
        days,
        pricePerDay,
        total
    };
}


// ================= MAIN =================
document.addEventListener("DOMContentLoaded", function () {

    // Auto hide error message
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
    const pricePerDayRaw = document.getElementById("pricePerDayRaw");

    const priceText = document.getElementById("priceText");
    const daysText = document.getElementById("daysText");
    const totalText = document.getElementById("totalText");

    const bookingForm = document.getElementById("bookingForm");
    const openConfirmBtn = document.getElementById("openConfirmBtn");
    const finalSubmitBtn = document.getElementById("finalSubmitBtn");
    const agreePolicy = document.getElementById("agreePolicy");

    if (!startDateInput || !endDateInput || !pricePerDayRaw) {
        console.log("Missing elements");
        return;
    }

    // khóa ngày quá khứ
    const todayStr = new Date().toISOString().split("T")[0];
    startDateInput.setAttribute("min", todayStr);
    endDateInput.setAttribute("min", todayStr);

    const pricePerDay = parseFloat(pricePerDayRaw.value) || 0;

    function calculate() {
        const data = calculateBookingData();

        if (!data) {
            resetSummary();
            return;
        }

        daysText.innerText = data.days + " ngày";
        priceText.innerText = formatMoney(pricePerDay) + " VND";
        totalText.innerText = formatMoney(data.total) + " VND";
    }

    function resetSummary() {
        daysText.innerText = "0 ngày";
        priceText.innerText = "0 VND";
        totalText.innerText = "0 VND";
    }

    function fillConfirmModal() {
        const data = calculateBookingData();
        const noteValue = document.getElementById("bookingNote")?.value?.trim() || "";

        if (!data)
            return;

        document.getElementById("confirmStartDate").innerText = formatDateVN(data.startValue);
        document.getElementById("confirmEndDate").innerText = formatDateVN(data.endValue);
        document.getElementById("confirmPricePerDay").innerText = formatMoney(data.pricePerDay) + " VND";
        document.getElementById("confirmDays").innerText = data.days + " ngày";
        document.getElementById("confirmTotal").innerText = formatMoney(data.total) + " VND";

        document.getElementById("confirmPricePerDay2").innerText = formatMoney(data.pricePerDay) + " VND";
        document.getElementById("confirmDays2").innerText = data.days + " ngày";
        document.getElementById("confirmTotal2").innerText = formatMoney(data.total) + " VND";

        document.getElementById("confirmNoteBox").innerText = noteValue || "Không có ghi chú";
    }

    if (startDateInput) {
        startDateInput.addEventListener("change", function () {
            endDateInput.setAttribute("min", startDateInput.value || todayStr);
            calculate();
        });
    }

    if (endDateInput) {
        endDateInput.addEventListener("change", calculate);
    }

    if (openConfirmBtn) {
        openConfirmBtn.addEventListener("click", function () {
            if (!validateBooking())
                return;

            fillConfirmModal();
            openConfirmModal();
        });
    }

    if (finalSubmitBtn) {
        finalSubmitBtn.addEventListener("click", function () {
            if (!agreePolicy.checked) {
                alert("Vui lòng đồng ý chính sách hủy chuyến trước khi đặt xe");
                return;
            }

            bookingForm.submit();
        });
    }

    calculate();
});


// ================= PRICE MODAL =================
function openModal() {
    document.getElementById("priceModal").style.display = "flex";
}

function closeModal() {
    document.getElementById("priceModal").style.display = "none";
}


// ================= CONFIRM MODAL =================
function openConfirmModal() {
    document.getElementById("confirmBookingModal").style.display = "flex";
}

function closeConfirmModal() {
    document.getElementById("confirmBookingModal").style.display = "none";
}