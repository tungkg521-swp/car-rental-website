window.addEventListener('DOMContentLoaded', function () {
    const notificationData = document.getElementById('notificationData');

    if (notificationData) {
        const successMessage = notificationData.dataset.success;
        const errorMessage = notificationData.dataset.error;
        const redirectUrl = notificationData.dataset.redirect;

        if (successMessage) {
            Swal.fire({
                icon: 'success',
                title: successMessage,
                showConfirmButton: false,
                timer: 1500
            }).then(() => {
                if (redirectUrl) {
                    window.location.href = redirectUrl;
                }
            });
        } else if (errorMessage) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: errorMessage
            });
        }
    }
});

function toggleCustomer(show) {
    const box = document.getElementById('customerSelectBox');
    if (!box) return;

    if (show) {
        box.style.display = 'block';
    } else {
        box.style.display = 'none';
    }
}