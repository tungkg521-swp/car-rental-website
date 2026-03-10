function openModal() {
    const modal = document.getElementById("updateModal");
    if (modal) {
        modal.style.display = "flex";
    }
}

function closeModal() {
    const modal = document.getElementById("updateModal");
    if (modal) {
        modal.style.display = "none";
    }
}

// Auto hide alert
setTimeout(() => {
    const alertBox = document.querySelector('.alert');
    if (alertBox) {
        alertBox.style.display = 'none';
    }
}, 3000);


// ================= ENABLE GPLX EDIT =================
function enableLicenseEdit() {

    const licenseCard = document.querySelector('.license-card');
    if (!licenseCard) return;

    // Enable text + date input (không đụng hidden)
    licenseCard.querySelectorAll('input[type="text"], input[type="date"]')
        .forEach(input => {
            input.removeAttribute('readonly');
        });

    // Enable file input
    licenseCard.querySelectorAll('.file-input')
        .forEach(file => {
            file.removeAttribute('disabled');
        });

    // Show save button
    const saveBtn = licenseCard.querySelector('.save-btn');
    if (saveBtn) {
        saveBtn.style.display = 'inline-block';
    }
}

function showChangePassword() {

    document.getElementById("profileInfoSection").style.display = "none";
    document.getElementById("changePasswordSection").style.display = "block";

}

function showProfile() {

    document.getElementById("profileInfoSection").style.display = "block";
    document.getElementById("changePasswordSection").style.display = "none";

}