<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Đặt xe thành công</title>

<link rel="stylesheet"
href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">

<link rel="stylesheet"
href="${pageContext.request.contextPath}/assets/css/booking-success.css">

</head>

<body>

<div class="success-card">

<h2 class="success-title">🎉 Đặt xe thành công</h2>

<p class="mt-3 text-muted">
Yêu cầu đặt xe của bạn đã được gửi thành công.
<br>
Vui lòng chờ nhân viên xác nhận.
</p>

<a href="${pageContext.request.contextPath}/home"
class="btn btn-primary mt-4">
Về trang chủ
</a>

<a href="${pageContext.request.contextPath}/booking?action=list"
class="btn btn-outline-secondary mt-4 ms-2">
Xem đơn đặt xe
</a>

</div>

</body>
</html>