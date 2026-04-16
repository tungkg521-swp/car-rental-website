<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Forgot Password</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css">
</head>
<body>
    <div class="overlay">
        <div class="login-card">
            <h2>Quên mật khẩu</h2>

            <form action="${pageContext.request.contextPath}/forgot-password" method="post">
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" value="${email}" placeholder="Nhập email" required>
                </div>

                <button type="submit" class="login-btn">Gửi mã OTP</button>
            </form>

            <c:if test="${not empty error}">
                <p class="error-message">${error}</p>
            </c:if>

            <c:if test="${not empty message}">
                <p style="color: green; margin-top: 12px;">${message}</p>
            </c:if>

            <div style="margin-top: 15px; text-align: center;">
                <a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a>
            </div>
        </div>
    </div>
</body>
</html>