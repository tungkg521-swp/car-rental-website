<%-- 
    Document   : register
    Created on : Feb 28, 2026, 11:44:50 AM
    Author     : HP
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Register</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css">
</head>
<body>

<div class="overlay">
    <div class="login-card">

        <h2>Đăng ký tài khoản</h2>

        <form action="${pageContext.request.contextPath}/register" method="post">

            <div class="form-group">
                <label>Họ và tên</label>
               <input type="text" name="fullName" value="${fullName}" required maxlength="100">
            </div>

            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" value="${email}" required maxlength="100">
            </div>

            <div class="form-group">
                <label>Số điện thoại</label>
               <input type="text" name="phone" value="${phone}" required pattern="[0-9]{10}">
            </div>

            <div class="form-group">
                <label>Địa chỉ</label>
              <input type="text" name="address" value="${address}" required maxlength="200">
            </div>

            <div class="form-group">
                <label>Ngày sinh</label>
              <input type="date" name="dob" value="${dob}" required>
            </div>

            <div class="form-group">
                <label>Mật khẩu</label>
                <input type="password" name="password" placeholder="Nhập mật khẩu" required>
            </div>

            <div class="form-group">
                <label>Xác nhận mật khẩu</label>
                <input type="password" name="confirmPassword" placeholder="Nhập lại mật khẩu" required>
            </div>

            <button type="submit" class="login-btn">Đăng ký</button>

        </form>

        <div class="switch-link">
            Đã có tài khoản?
            <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
        </div>

        <c:if test="${not empty error}">
            <p class="error-message">${error}</p>
        </c:if>

    </div>
</div>

</body>
</html>