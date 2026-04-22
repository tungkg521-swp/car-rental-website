function openCheckFeesModal() {
    const modal = document.getElementById("checkFeesModal");
    const form = document.getElementById("checkFeesForm");
    const noIssuesCheckbox = document.getElementById("noIssuesFound");
    const odometerInput = document.querySelector('input[name="odometerKm"]');

    if (form) {
        form.reset();
    }

    resetReturnCheckCheckboxStates();

    if (noIssuesCheckbox) {
        noIssuesCheckbox.checked = false;
    }

    const savedData = getSavedReturnCheckData();
    const returnMeta = getReturnCheckMeta();

    // Luôn seed lại issue từ pre-check trước
    applyPreDeliverySeedToReturnCheck();

    if (returnMeta.hasReturnCheck) {
        restoreSavedReturnOdometer();

        if (returnMeta.normalReturn) {
            if (noIssuesCheckbox) {
                noIssuesCheckbox.checked = true;
            }
            toggleNoIssuesMode();
        } else {
            restoreSavedReturnCheckData();
            markSavedReturnIssuesAsNotSeeded();
        }
    } else {
        if (odometerInput) {
            odometerInput.value = "";
        }
    }

    if (modal) {
        modal.classList.add("show");
        renderSelectedIssues();

        if (returnMeta.hasReturnCheck && !returnMeta.normalReturn && savedData.issues.length > 0) {
            applySavedFeeValues();
        }
    }
}

function closeCheckFeesModal() {
    const modal = document.getElementById("checkFeesModal");
    const form = document.getElementById("checkFeesForm");
    const noIssuesCheckbox = document.getElementById("noIssuesFound");

    if (form) {
        form.reset();
    }

    resetReturnCheckCheckboxStates();

    if (noIssuesCheckbox) {
        noIssuesCheckbox.checked = false;
    }

    if (modal) {
        modal.classList.remove("show");
    }

    renderSelectedIssues();
}

function escapeHtml(text) {
    if (!text) {
        return "";
    }

    return text
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
}

function buildSafeFieldSuffix(issue) {
    return issue.replace(/\s+/g, "_");
}

function renderSelectedIssues() {
    const checkedBoxes = document.querySelectorAll('input[name="issueTypes"]:checked');
    const preview = document.getElementById("selectedIssuesPreview");
    const feeRowsContainer = document.getElementById("feeRowsContainer");
    const noIssuesCheckbox = document.getElementById("noIssuesFound");

    if (!preview || !feeRowsContainer) {
        return;
    }

    const currentValues = collectCurrentFeeValues();

    preview.innerHTML = "";
    feeRowsContainer.innerHTML = "";

    let hasVisibleTag = false;

    checkedBoxes.forEach((checkbox) => {
        const issue = checkbox.value;
        const safeSuffix = buildSafeFieldSuffix(issue);
        const isSeeded = checkbox.dataset.seeded === "true";

        const tag = document.createElement("span");
        tag.className = "cf-tag";
        tag.textContent = isSeeded ? issue + " (from pre-check)" : issue;
        preview.appendChild(tag);
        hasVisibleTag = true;

        // Issue từ pre-check chỉ hiện ở Selected Issues, không hiện xuống Fees
        if (isSeeded) {
            return;
        }

        // Nếu đang tick No issues found thì không render phí cho issue return-check
        if (noIssuesCheckbox && noIssuesCheckbox.checked) {
            return;
        }

        const savedDescription = currentValues[issue] ? currentValues[issue].description : "";
        const savedAmount = currentValues[issue] ? currentValues[issue].amount : "";

        const safeIssueText = escapeHtml(issue);

        const row = document.createElement("div");
        row.className = "cf-fee-row";
        row.innerHTML =
                '<div class="cf-fee-type">' + safeIssueText + '</div>' +
                '<div>' +
                '<input type="text" name="description_' + safeSuffix + '" placeholder="Enter description" class="cf-input" value="' + escapeHtml(savedDescription) + '">' +
                '</div>' +
                '<div>' +
                '<input type="number" min="0" step="1000" name="amount_' + safeSuffix + '" placeholder="0" class="cf-input" value="' + escapeHtml(savedAmount) + '">' +
                '</div>';

        feeRowsContainer.appendChild(row);
    });

    // Nếu tick No issues found thì vẫn giữ pre-check tags và thêm 1 tag normal return
    if (noIssuesCheckbox && noIssuesCheckbox.checked) {
        const normalTag = document.createElement("span");
        normalTag.className = "cf-tag";
        normalTag.textContent = "No issues found";
        preview.appendChild(normalTag);
        hasVisibleTag = true;
    }

    if (!hasVisibleTag) {
        preview.innerHTML = '<span class="cf-tag">No issues selected</span>';
    }
}

function validateCheckFeesForm(event) {
    const odometerInput = document.querySelector('input[name="odometerKm"]');
    const preCheckOdometerInput = document.getElementById("preCheckOdometerValue");
    const noIssuesCheckbox = document.getElementById("noIssuesFound");

    if (!odometerInput || odometerInput.value.trim() === "") {
        alert("Please enter return odometer.");
        event.preventDefault();
        return;
    }

    const endKm = Number(odometerInput.value);

    if (isNaN(endKm) || endKm < 0) {
        alert("Return odometer must be greater than or equal to 0.");
        event.preventDefault();
        return;
    }

    if (preCheckOdometerInput) {
        const startKm = Number(preCheckOdometerInput.value || 0);
        if (endKm < startKm) {
            alert("Return odometer cannot be smaller than pre-check odometer.");
            event.preventDefault();
            return;
        }
    }

    // Nếu tick No issues found thì chỉ cần km, không kiểm tra phí
    if (noIssuesCheckbox && noIssuesCheckbox.checked) {
        return;
    }

    const checkedBoxes = document.querySelectorAll('input[name="issueTypes"]:checked');
    let invalidAmount = false;
    let missingAmount = false;

    checkedBoxes.forEach((checkbox) => {
        const isSeeded = checkbox.dataset.seeded === "true";

        // Issue từ pre-check không tính phí
        if (isSeeded) {
            return;
        }

        const issue = checkbox.value;
        const safeSuffix = buildSafeFieldSuffix(issue);
        const amountInput = document.querySelector('input[name="amount_' + safeSuffix + '"]');

        if (!amountInput || amountInput.value.trim() === "") {
            missingAmount = true;
            return;
        }

        const amountValue = Number(amountInput.value);
        if (isNaN(amountValue) || amountValue <= 0) {
            invalidAmount = true;
        }
    });

    if (missingAmount) {
        alert("Please enter an amount for each selected return issue.");
        event.preventDefault();
        return;
    }

    if (invalidAmount) {
        alert("Amount must be greater than 0 for each selected return issue.");
        event.preventDefault();
    }
}

function bindCheckFeesEvents() {
    const checkboxes = document.querySelectorAll('input[name="issueTypes"]');
    checkboxes.forEach((checkbox) => {
        checkbox.addEventListener("change", function () {
            checkbox.dataset.seeded = "false";
            renderSelectedIssues();
        });
    });

    const noIssuesCheckbox = document.getElementById("noIssuesFound");
    if (noIssuesCheckbox) {
        noIssuesCheckbox.addEventListener("change", toggleNoIssuesMode);
    }

    const form = document.getElementById("checkFeesForm");
    if (form) {
        form.addEventListener("submit", validateCheckFeesForm);
    }

    window.addEventListener("click", function (event) {
        const returnModal = document.getElementById("checkFeesModal");
        if (returnModal && event.target === returnModal) {
            closeCheckFeesModal();
        }

        const beforeModal = document.getElementById("beforeCheckModal");
        if (beforeModal && event.target === beforeModal) {
            closeBeforeCheckModal();
        }
    });
}

document.addEventListener("DOMContentLoaded", function () {
    bindCheckFeesEvents();
});

function showReturnCheckRequiredMessage() {
    alert("Please inspect the vehicle first by using Check - Fees before completing the return.");
}

function handleCompleteReturn(hasReturnCheck) {
    if (!hasReturnCheck) {
        showReturnCheckRequiredMessage();
        return;
    }

    const confirmed = confirm("Complete this return now?");
    if (!confirmed) {
        return;
    }

    const form = document.getElementById("completeReturnForm");
    if (form) {
        form.submit();
    }
}

function restoreSavedReturnCheckData() {
    const savedData = getSavedReturnCheckData();
    const seedIssues = getPreDeliverySeedIssues();

    if (!savedData.issues || savedData.issues.length === 0) {
        return;
    }

    const checkboxes = document.querySelectorAll('input[name="issueTypes"]');
    checkboxes.forEach((checkbox) => {
        // Không đụng vào issue cố định từ pre-check
        if (!seedIssues.includes(checkbox.value) && savedData.issues.includes(checkbox.value)) {
            checkbox.checked = true;
            checkbox.dataset.seeded = "false";
            checkbox.disabled = false;
        }
    });
}

function applySavedFeeValues() {
    const savedData = getSavedReturnCheckData();

    if (!savedData.issues || savedData.issues.length === 0) {
        return;
    }

    savedData.issues.forEach((issue, index) => {
        const safeSuffix = buildSafeFieldSuffix(issue);

        const descriptionInput = document.querySelector('input[name="description_' + safeSuffix + '"]');
        const amountInput = document.querySelector('input[name="amount_' + safeSuffix + '"]');

        if (descriptionInput && savedData.descriptions[index] !== undefined) {
            descriptionInput.value = savedData.descriptions[index];
        }

        if (amountInput && savedData.amounts[index] !== undefined) {
            amountInput.value = savedData.amounts[index];
        }
    });
}

function getReturnCheckMeta() {
    const meta = document.getElementById("returnCheckMetaData");

    return {
        hasReturnCheck: meta ? meta.dataset.hasReturnCheck === "true" : false,
        normalReturn: meta ? meta.dataset.normalReturn === "true" : false
    };
}

function getSavedReturnCheckData() {
    const items = document.querySelectorAll(".saved-return-item");

    const data = {
        issues: [],
        descriptions: [],
        amounts: []
    };

    items.forEach((item) => {
        data.issues.push(item.dataset.issue || "");
        data.descriptions.push(item.dataset.description || "");
        data.amounts.push(item.dataset.amount || "0");
    });

    return data;
}

function collectCurrentFeeValues() {
    const values = {};
    const checkedBoxes = document.querySelectorAll('input[name="issueTypes"]:checked');

    checkedBoxes.forEach((checkbox) => {
        const issue = checkbox.value;
        const safeSuffix = buildSafeFieldSuffix(issue);

        const descriptionInput = document.querySelector('input[name="description_' + safeSuffix + '"]');
        const amountInput = document.querySelector('input[name="amount_' + safeSuffix + '"]');

        values[issue] = {
            description: descriptionInput ? descriptionInput.value : "",
            amount: amountInput ? amountInput.value : ""
        };
    });

    return values;
}

function parseIssueText(issueText) {
    if (!issueText) {
        return [];
    }

    return issueText
            .split("|")
            .map(function (item) {
                return item.trim();
            })
            .filter(function (item) {
                return item.length > 0;
            });
}

function resetBeforeCheckForm() {
    const form = document.getElementById("beforeCheckForm");
    if (!form) {
        return;
    }

    const exteriorBoxes = form.querySelectorAll('input[name="exteriorIssues"]');
    const interiorBoxes = form.querySelectorAll('input[name="interiorIssues"]');
    const statusRadios = form.querySelectorAll('input[name="physicalStatus"]');
    const fuelSelect = form.querySelector('select[name="fuelLevel"]');
    const noteTextarea = form.querySelector('textarea[name="note"]');

    exteriorBoxes.forEach((checkbox) => {
        checkbox.checked = false;
    });

    interiorBoxes.forEach((checkbox) => {
        checkbox.checked = false;
    });

    statusRadios.forEach((radio) => {
        radio.checked = false;
    });

    const okRadio = form.querySelector('input[name="physicalStatus"][value="OK"]');
    if (okRadio) {
        okRadio.checked = true;
    }

    if (fuelSelect) {
        fuelSelect.value = "EMPTY";
    }

    if (noteTextarea) {
        noteTextarea.value = "";
    }
}

function fillBeforeCheckFormFromSavedData() {
    const savedData = document.getElementById("savedBeforeCheckData");
    const form = document.getElementById("beforeCheckForm");

    if (!savedData || !form) {
        return;
    }

    const exteriorNote = savedData.dataset.exteriorNote || "";
    const interiorNote = savedData.dataset.interiorNote || "";
    const fuelLevel = savedData.dataset.fuelLevel || "";
    const checkResult = savedData.dataset.checkResult || "";
    const note = savedData.dataset.note || "";

    const exteriorIssues = parseIssueText(exteriorNote);
    const interiorIssues = parseIssueText(interiorNote);

    const exteriorBoxes = form.querySelectorAll('input[name="exteriorIssues"]');
    const interiorBoxes = form.querySelectorAll('input[name="interiorIssues"]');

    exteriorBoxes.forEach((checkbox) => {
        checkbox.checked = exteriorIssues.includes(checkbox.value);
    });

    interiorBoxes.forEach((checkbox) => {
        checkbox.checked = interiorIssues.includes(checkbox.value);
    });

    const fuelSelect = form.querySelector('select[name="fuelLevel"]');
    if (fuelSelect && fuelLevel) {
        fuelSelect.value = fuelLevel;
    }

    const statusRadio = form.querySelector('input[name="physicalStatus"][value="' + checkResult + '"]');
    if (statusRadio) {
        statusRadio.checked = true;
    }

    const noteTextarea = form.querySelector('textarea[name="note"]');
    if (noteTextarea) {
        noteTextarea.value = note;
    }
}

function openBeforeCheckModal() {
    const modal = document.getElementById("beforeCheckModal");
    if (!modal) {
        return;
    }

    resetBeforeCheckForm();
    fillBeforeCheckFormFromSavedData();

    modal.classList.add("show");
}

function closeBeforeCheckModal() {
    const modal = document.getElementById("beforeCheckModal");
    if (modal) {
        modal.classList.remove("show");
    }
}

function validateSendToCustomer(isOkCheck) {
    if (!isOkCheck) {
        alert("You can only send to customer after saving a valid pre-delivery check with result OK.");
        return false;
    }

    return confirm("Send this handover check to customer?");
}

function normalizeIssueName(issue) {
    if (!issue) {
        return "";
    }

    const trimmed = issue.trim();

    if (trimmed === "Scratch") {
        return "Exterior scratch";
    }

    if (trimmed === "Seat damage" || trimmed === "Dashboard issue") {
        return "Interior damage";
    }

    return trimmed;
}

function getPreDeliverySeedIssues() {
    const seed = document.getElementById("preDeliveryIssueSeedData");

    if (!seed) {
        return [];
    }

    const exteriorIssues = parseIssueText(seed.dataset.exteriorNote || "");
    const interiorIssues = parseIssueText(seed.dataset.interiorNote || "");

    const allIssues = exteriorIssues
            .concat(interiorIssues)
            .map(normalizeIssueName);

    return allIssues.filter(function (issue, index) {
        return allIssues.indexOf(issue) === index;
    });
}



function applyPreDeliverySeedToReturnCheck() {
    const seedIssues = getPreDeliverySeedIssues();

    if (!seedIssues || seedIssues.length === 0) {
        return;
    }

    const checkboxes = document.querySelectorAll('input[name="issueTypes"]');

    checkboxes.forEach(function (checkbox) {
        if (seedIssues.includes(checkbox.value)) {
            checkbox.checked = true;
            checkbox.dataset.seeded = "true";
            checkbox.disabled = true;
        }
    });
}

function clearReturnSeedFlags() {
    const checkboxes = document.querySelectorAll('input[name="issueTypes"]');
    checkboxes.forEach(function (checkbox) {
        checkbox.dataset.seeded = "false";
    });
}

function markSavedReturnIssuesAsNotSeeded() {
    const seedIssues = getPreDeliverySeedIssues();
    const checkboxes = document.querySelectorAll('input[name="issueTypes"]');

    checkboxes.forEach(function (checkbox) {
        // Chỉ bỏ seeded cho các issue của return-check đã lưu,
        // không đụng vào issue cố định từ pre-check
        if (checkbox.checked && !seedIssues.includes(checkbox.value)) {
            checkbox.dataset.seeded = "false";
            checkbox.disabled = false;
        }
    });
}

function resetReturnCheckCheckboxStates() {
    const checkboxes = document.querySelectorAll('input[name="issueTypes"]');
    checkboxes.forEach(function (checkbox) {
        checkbox.disabled = false;
        checkbox.dataset.seeded = "false";
    });
}

function toggleNoIssuesMode() {
    const noIssuesCheckbox = document.getElementById("noIssuesFound");
    const issueCheckboxes = document.querySelectorAll('input[name="issueTypes"]');
    const feeRowsContainer = document.getElementById("feeRowsContainer");

    if (!noIssuesCheckbox) {
        return;
    }

    if (noIssuesCheckbox.checked) {
        issueCheckboxes.forEach(function (checkbox) {
            const isSeeded = checkbox.dataset.seeded === "true";

            // Pre-check issue là cố định -> giữ nguyên
            if (isSeeded) {
                checkbox.checked = true;
                checkbox.disabled = true;
            } else {
                // Chỉ clear issue phát sinh ở return-check
                checkbox.checked = false;
                checkbox.disabled = true;
            }
        });

        if (feeRowsContainer) {
            feeRowsContainer.innerHTML = "";
        }

        renderSelectedIssues();
    } else {
        issueCheckboxes.forEach(function (checkbox) {
            const isSeeded = checkbox.dataset.seeded === "true";

            if (isSeeded) {
                checkbox.checked = true;
                checkbox.disabled = true;
            } else {
                checkbox.disabled = false;
            }
        });

        renderSelectedIssues();
    }
}

function restoreSavedReturnOdometer() {
    const odometerInput = document.querySelector('input[name="odometerKm"]');
    const holder = document.getElementById("returnCheckSavedOdometer");

    if (!odometerInput || !holder) {
        return;
    }

    odometerInput.value = holder.dataset.odometer || "";
}

