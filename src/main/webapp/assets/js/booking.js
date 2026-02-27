document.addEventListener("DOMContentLoaded", function () {

    const startDateInput = document.getElementById("startDate");
    const endDateInput = document.getElementById("endDate");

    const pricePerDayEl = document.getElementById("pricePerDay");
    const priceText = document.getElementById("priceText");
    const daysText = document.getElementById("daysText");
    const totalText = document.getElementById("totalText");

    if (!startDateInput || !endDateInput || !pricePerDayEl) {
        return;
    }

    const pricePerDay = parseInt(pricePerDayEl.innerText);

    function calculate() {
    const startDateValue = startDateInput.value;
    const endDateValue = endDateInput.value;

    if (!startDateValue || !endDateValue) {
        resetSummary();
        return;
    }

    // Parse date KHÔNG bị lệch timezone
    const [sy, sm, sd] = startDateValue.split("-").map(Number);
    const [ey, em, ed] = endDateValue.split("-").map(Number);

    const startDate = new Date(sy, sm - 1, sd);
    const endDate = new Date(ey, em - 1, ed);

    if (endDate < startDate) {
        resetSummary();
        return;
    }

    const timeDiff = endDate.getTime() - startDate.getTime();
    const days = Math.floor(timeDiff / (1000 * 60 * 60 * 24)) + 1;

    const totalPrice = days * pricePerDay;

    daysText.innerText = days + " ngày";
    priceText.innerText = formatMoney(pricePerDay) + " VND";
    totalText.innerText = formatMoney(totalPrice) + " VND";
}


    function resetSummary() {
        daysText.innerText = "0 ngày";
        priceText.innerText = "0 VND";
        totalText.innerText = "0 VND";
    }

    function formatMoney(amount) {
        return amount.toLocaleString("vi-VN");
    }

    // Lắng nghe thay đổi ngày
    startDateInput.addEventListener("change", calculate);
    endDateInput.addEventListener("change", calculate);
});
