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

    if (!startDateInput || !endDateInput || !pricePerDayRaw) {
        console.log("Missing elements");
        return;
    }

        // khóa ngày quá khứ
    const today = new Date().toISOString().split("T")[0];
    startDateInput.setAttribute("min", today);
    endDateInput.setAttribute("min", today);

    const pricePerDay = parseFloat(pricePerDayRaw.value);

    function calculate() {

    const startValue = startDateInput.value;
    const endValue = endDateInput.value;

    if (!startValue || !endValue) {
        resetSummary();
        return;
    }

    // tách yyyy-mm-dd
    const [sy, sm, sd] = startValue.split("-").map(Number);
    const [ey, em, ed] = endValue.split("-").map(Number);

    const startDate = new Date(sy, sm - 1, sd);
    const endDate = new Date(ey, em - 1, ed);

    const today = new Date();
    today.setHours(0,0,0,0);

    if (startDate < today) {
    resetSummary();
    return;
}

if (endDate <= startDate) {
    resetSummary();
    return;
}

    const diff = endDate - startDate;

    const days = Math.floor(diff / (1000 * 60 * 60 * 24));

    const total = days * pricePerDay;

    daysText.innerText = days + " ngày";
    priceText.innerText = formatMoney(pricePerDay) + " VND";
    totalText.innerText = formatMoney(total) + " VND";
}

    function resetSummary() {
        daysText.innerText = "0 ngày";
        priceText.innerText = "0 VND";
        totalText.innerText = "0 VND";
    }

    function formatMoney(n) {
        return n.toLocaleString("vi-VN");
    }

    startDateInput.addEventListener("change", calculate);
    endDateInput.addEventListener("change", calculate);

    
    function validateBooking() {

    const start = document.getElementById("startDate").value;
    const end = document.getElementById("endDate").value;

    if (!start || !end) {
        alert("Vui lòng chọn đầy đủ ngày thuê");
        return false;
    }

    const startDate = new Date(start);
    const endDate = new Date(end);

    if (endDate <= startDate) {
        alert("Ngày trả phải sau ngày thuê");
        return false;
    }

    return true;
}

});