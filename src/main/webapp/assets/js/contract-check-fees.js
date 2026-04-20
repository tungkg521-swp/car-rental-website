function openCheckFeesModal() {
    const modal = document.getElementById("checkFeesModal");
    const form = document.getElementById("checkFeesForm");

    if (form) {
        form.reset();
    }

    restoreSavedReturnCheckData();

    if (modal) {
        modal.classList.add("show");
        renderSelectedIssues();
        applySavedFeeValues();
    }
}

function closeCheckFeesModal() {
    const modal = document.getElementById("checkFeesModal");
    const form = document.getElementById("checkFeesForm");

    if (form) {
        form.reset();
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

    if (!preview || !feeRowsContainer) {
        return;
    }

    const currentValues = collectCurrentFeeValues();

    preview.innerHTML = "";
    feeRowsContainer.innerHTML = "";

    checkedBoxes.forEach((checkbox) => {
        const issue = checkbox.value;
        const safeIssueText = escapeHtml(issue);
        const safeSuffix = buildSafeFieldSuffix(issue);

        const savedDescription = currentValues[issue] ? currentValues[issue].description : "";
        const savedAmount = currentValues[issue] ? currentValues[issue].amount : "";

        const tag = document.createElement("span");
        tag.className = "cf-tag";
        tag.textContent = issue;
        preview.appendChild(tag);

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
}

function validateCheckFeesForm(event) {
    const checkedBoxes = document.querySelectorAll('input[name="issueTypes"]:checked');

    if (checkedBoxes.length === 0) {
        alert("Please select at least one issue.");
        event.preventDefault();
        return;
    }

    let invalidAmount = false;

    checkedBoxes.forEach((checkbox) => {
        const issue = checkbox.value;
        const safeSuffix = buildSafeFieldSuffix(issue);
        const amountInput = document.querySelector('input[name="amount_' + safeSuffix + '"]');

        if (amountInput && amountInput.value !== "" && Number(amountInput.value) < 0) {
            invalidAmount = true;
        }
    });

    if (invalidAmount) {
        alert("Amount must be greater than or equal to 0.");
        event.preventDefault();
    }
}

function bindCheckFeesEvents() {
    const checkboxes = document.querySelectorAll('input[name="issueTypes"]');
    checkboxes.forEach((checkbox) => {
        checkbox.addEventListener("change", renderSelectedIssues);
    });

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

    if (!savedData.issues || savedData.issues.length === 0) {
        return;
    }

    const checkboxes = document.querySelectorAll('input[name="issueTypes"]');
    checkboxes.forEach((checkbox) => {
        checkbox.checked = savedData.issues.includes(checkbox.value);
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