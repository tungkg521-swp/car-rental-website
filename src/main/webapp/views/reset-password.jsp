<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Reset Password</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css">
</head>
<body>
    <div class="overlay">
        <div class="login-card">
            <h2>Đặt lại mật khẩu</h2>

            <c:choose>
                <c:when test="${not empty message}">
                    <p style="color: green;">${message}</p>
                    <div style="margin-top: 15px; text-align: center;">
                        <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                    </div>
                </c:when>

                <c:otherwise>
                    <c:if test="${not empty email}">
                        <p style="margin-bottom: 15px; color: #555;">
                            Tài khoản: <strong>${email}</strong>
                        </p>
                    </c:if>

                    <c:if test="${not empty error}">
                        <p class="error-message">${error}</p>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/reset-password" method="post">
                        <div class="form-group">
                            <label>Mật khẩu mới</label>
                            <input type="password" name="newPassword" required>
                        </div>

                        <div class="form-group">
                            <label>Xác nhận mật khẩu</label>
                            <input type="password" name="confirmPassword" required>
                        </div>

                        <button type="submit" class="login-btn">Đổi mật khẩu</button>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>