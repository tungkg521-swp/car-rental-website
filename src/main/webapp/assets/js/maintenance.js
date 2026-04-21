
/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


document.addEventListener("DOMContentLoaded", function () {
    const contextPath = window.maintenanceConfig ? window.maintenanceConfig.contextPath : "";
    const carSelect = document.getElementById("carId");
    const startInput = document.getElementById("startDate");
    const endInput = document.getElementById("endDate");
    const maintenanceIdInput = document.querySelector('input[name="maintenanceId"]');

    if (!carSelect || !startInput || !endInput || typeof flatpickr === "undefined") {
        return;
    }

    let startPicker = null;
    let endPicker = null;

    function destroyPickers() {
        if (startPicker) {
            startPicker.destroy();
            startPicker = null;
        }
        if (endPicker) {
            endPicker.destroy();
            endPicker = null;
        }
    }

    function buildPickers(disabledRanges) {
        destroyPickers();

        const isEditMode = maintenanceIdInput && maintenanceIdInput.value && maintenanceIdInput.value.trim() !== "";
        const currentStartValue = startInput.value ? startInput.value.trim() : "";
        const currentEndValue = endInput.value ? endInput.value.trim() : "";

        startPicker = flatpickr(startInput, {
            dateFormat: "Y-m-d",
            minDate: isEditMode ? null : "today",
            disable: disabledRanges,
            defaultDate: currentStartValue || null,
            onChange: function (selectedDates, dateStr) {
                if (endPicker) {
                    endPicker.set("minDate", dateStr || (isEditMode ? null : "today"));

                    if (dateStr && endInput.value && endInput.value < dateStr) {
                        endPicker.setDate(dateStr, true);
                    }
                }
            }
        });

        endPicker = flatpickr(endInput, {
            dateFormat: "Y-m-d",
            minDate: currentStartValue || (isEditMode ? null : "today"),
            disable: disabledRanges,
            defaultDate: currentEndValue || null
        });
    }

    function loadBlockedDates() {
        const carId = carSelect.value;

        if (!carId) {
            buildPickers([]);
            return;
        }

        const maintenanceId = maintenanceIdInput ? maintenanceIdInput.value : "";
        const url = `${contextPath}/staff/maintenance?action=blocked-dates&carId=${encodeURIComponent(carId)}&maintenanceId=${encodeURIComponent(maintenanceId)}`;

        fetch(url, {
            method: "GET",
            headers: {
                "Accept": "application/json"
            }
        })
            .then(function (response) {
                if (!response.ok) {
                    throw new Error("Failed to load blocked dates");
                }
                return response.json();
            })
            .then(function (data) {
                if (!Array.isArray(data)) {
                    buildPickers([]);
                    return;
                }

                const blockedRanges = data
                    .filter(function (item) {
                        return item && item.from && item.to;
                    })
                    .map(function (item) {
                        return {
                            from: item.from,
                            to: item.to
                        };
                    });

                buildPickers(blockedRanges);
            })
            .catch(function (error) {
                console.error("Error loading blocked dates:", error);
                buildPickers([]);
            });
    }

    loadBlockedDates();
    carSelect.addEventListener("change", loadBlockedDates);
});