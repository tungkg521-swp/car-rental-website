<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đặt xe thành công</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
</head>
<body>

<div class="container mt-5">

    <div class="card p-4 shadow-sm text-center">
        <h3 class="text-success mb-3">🎉 Đặt xe thành công</h3>

        <p><strong>Khách hàng:</strong> ${customer.fullName}</p>
        <p><strong>Xe:</strong> ${car.modelName}</p>
        <p><strong>Thời gian thuê:</strong>
            ${booking.startDate} → ${booking.endDate}
        </p>
        <p><strong>Tổng tiền:</strong>
            ${booking.totalEstimatedPrice} VND
        </p>

        <div class="mt-4">
            <a href="${pageContext.request.contextPath}/home"
               class="btn btn-primary">
                Về trang chủ
            </a>
        </div>
    </div>

</div>

</body>
</html>
