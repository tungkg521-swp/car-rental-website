<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Verify OTP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css">
</head>
<body>
    <div class="overlay">
        <div class="login-card">
            <h2>Xác minh OTP</h2>

            <c:if test="${not empty email}">
                <p style="margin-bottom: 15px; color: #555;">
                    Mã OTP đã được gửi tới: <strong>${email}</strong>
                </p>
            </c:if>

            <form action="${pageContext.request.contextPath}/verify-otp" method="post">
                <div class="form-group">
                    <label>Mã OTP</label>
                    <input type="text" name="otp" placeholder="Nhập 6 số OTP" maxlength="7" required>
                </div>

                <button type="submit" class="login-btn">Xác minh OTP</button>
            </form>

            <c:if test="${not empty error}">
                <p class="error-message">${error}</p>
            </c:if>

            <c:if test="${not empty message}">
                <p style="color: green; margin-top: 12px;">${message}</p>
            </c:if>

            <div style="margin-top: 15px; text-align: center;">
                <a href="${pageContext.request.contextPath}/forgot-password">Gửi lại OTP</a>
            </div>
        </div>
    </div>
</body>
</html>