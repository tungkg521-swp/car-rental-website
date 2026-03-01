<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css">
</head>
<body>

<div class="overlay">
    <div class="login-card">

        <h2>Đăng nhập</h2>

        <form action="${pageContext.request.contextPath}/login" method="post">

            <div class="form-group">
                <label>Email</label>
                <input type="text" name="email" placeholder="Nhập email" required>
            </div>

            <div class="form-group">
                <label>Mật khẩu</label>
                <input type="password" name="password" placeholder="Nhập mật khẩu" required>
            </div>

            <button type="submit" class="login-btn">Đăng nhập</button>

        </form>

        <!-- Hiển thị lỗi nếu có -->
        <c:if test="${not empty error}">
            <p class="error-message">${error}</p>
        </c:if>

        <!-- Phần bổ sung thêm -->
        <div style="margin-top: 15px; text-align: center; font-size: 14px;">

            <div style="margin-bottom: 8px;">
                <a href="#" style="color: #0d6efd; text-decoration: none;">
                    Quên mật khẩu?
                </a>
            </div>

            <div>
                Chưa có tài khoản?
                <a href="${pageContext.request.contextPath}/register"
                   style="color: #198754; font-weight: 600; text-decoration: none;">
                    Đăng ký
                </a>
            </div>

        </div>

    </div>
</div>

</body>
</html>