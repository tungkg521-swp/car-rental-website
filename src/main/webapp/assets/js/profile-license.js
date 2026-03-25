function enableLicenseEdit() {
    document.querySelectorAll('.license-card input').forEach(function (input) {
        if (input.type === 'file') {
            input.disabled = false;
        } else {
            input.removeAttribute('readonly');
        }
    });

    const saveBtn = document.querySelector('.license-card .save-btn');
    if (saveBtn) {
        saveBtn.style.display = 'inline-block';
    }
}

function openModal() {
    const modal = document.getElementById('updateModal');
    if (modal) {
        modal.style.display = 'flex';
    }
}

function closeModal() {
    const modal = document.getElementById('updateModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

function previewImage(input) {
    const file = input.files[0];
    if (!file) return;
    if (!file.type.startsWith('image/')) return;

    const imageBox = input.closest('.image-box');
    if (!imageBox) return;

    const oldPreview = imageBox.querySelector('.preview-img');
    const oldEmpty = imageBox.querySelector('.empty-img');

    const reader = new FileReader();

    reader.onload = function (e) {
        if (oldPreview) {
            oldPreview.src = e.target.result;
            return;
        }

        if (oldEmpty) {
            const newImg = document.createElement('img');
            newImg.src = e.target.result;
            newImg.alt = 'Preview';
            newImg.className = 'preview-img';
            oldEmpty.parentNode.replaceChild(newImg, oldEmpty);
        }
    };

    reader.readAsDataURL(file);
}

window.addEventListener('DOMContentLoaded', function () {
    const enableEditValue = document.getElementById('enableLicenseEditValue');
    if (enableEditValue && enableEditValue.value === 'true') {
        enableLicenseEdit();
    }
});