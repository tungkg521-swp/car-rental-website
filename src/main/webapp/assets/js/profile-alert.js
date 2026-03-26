window.addEventListener('DOMContentLoaded', function () {
    const alertData = document.getElementById('profileAlertData');
    if (!alertData) return;

    const msg = alertData.dataset.msg;
    const redirectUrl = alertData.dataset.redirect;

    if (!msg) return;

    let config = null;

    switch (msg) {
        case 'success':
            config = {
                icon: 'success',
                title: 'Thành công',
                text: 'Cập nhật thành công!'
            };
            break;

        case 'error':
            config = {
                icon: 'error',
                title: 'Thất bại',
                text: 'Cập nhật thất bại!'
            };
            break;

        case 'verify_success':
            config = {
                icon: 'success',
                title: 'Gửi thành công',
                text: 'Đã gửi yêu cầu xác thực thành công!'
            };
            break;

        case 'license_not_found':
            config = {
                icon: 'warning',
                title: 'Chưa có GPLX',
                text: 'Bạn chưa khai báo thông tin GPLX.'
            };
            break;

        case 'missing_info':
            config = {
                icon: 'warning',
                title: 'Thiếu thông tin',
                text: 'Vui lòng nhập đầy đủ thông tin và tải đủ 5 ảnh xác minh trước khi gửi duyệt.'
            };
            break;

        case 'already_requested':
            config = {
                icon: 'info',
                title: 'Đang chờ duyệt',
                text: 'Yêu cầu xác thực của bạn đang chờ duyệt.'
            };
            break;

        case 'already_approved':
            config = {
                icon: 'success',
                title: 'Đã xác thực',
                text: 'GPLX của bạn đã được xác thực trước đó.'
            };
            break;
    }

    if (config) {
        Swal.fire({
            ...config,
            showConfirmButton: false,
            timer: 1500,
            timerProgressBar: true
        }).then(() => {
            if (redirectUrl) {
                window.location.href = redirectUrl;
            }
        });
    }
});

window.addEventListener('DOMContentLoaded', function () {
    const alertData = document.getElementById('profileAlertData');
    if (!alertData) return;

    const successMessage = alertData.dataset.success;
    const redirectUrl = alertData.dataset.redirect;

    if (!successMessage) return;

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
});