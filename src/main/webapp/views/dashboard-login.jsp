<%-- 
    Document   : dashboard-login
    Created on : Mar 18, 2026, 2:36:25 PM
    Author     : HP
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css">
</head>
<body>

<div class="overlay">
    <div class="login-card">

        <h2>Dashboard Login</h2>
        <p style="text-align:center; margin-bottom: 18px; color:#666;">
            Dành cho Staff / Admin
        </p>

        <form action="${pageContext.request.contextPath}/dashboard" method="post">

            <div class="form-group">
                <label>Email</label>
                <input type="text" name="email" placeholder="Nhập email" required>
            </div>

            <div class="form-group">
                <label>Mật khẩu</label>
                <input type="password" name="password" placeholder="Nhập mật khẩu" required>
            </div>

            <button type="submit" class="login-btn">Đăng nhập Dashboard</button>

        </form>

        <c:if test="${not empty error}">
            <p class="error-message">${error}</p>
        </c:if>

        <div style="margin-top: 15px; text-align: center; font-size: 14px;">
            <a href="${pageContext.request.contextPath}/login"
               style="color: #0d6efd; text-decoration: none;">
                Quay lại đăng nhập khách hàng
            </a>
        </div>

    </div>
</div>

</body>
</html>