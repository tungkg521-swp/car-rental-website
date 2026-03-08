<%-- 
    Document   : change-password
    Created on : Mar 1, 2026, 1:57:24 PM
    Author     : ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="${pageContext.request.contextPath}/assets/css/login.css" rel="stylesheet" type="text/css"/>
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <title>Change Password</title>
    </head>

    <body>
        <div class="overlay">
            <div class="login-card">

                <h2>Thay đổi mật khẩu</h2>

                <form action="${pageContext.request.contextPath}/changepassword" method="post">                 

                    <div class="form-group">
                        <label>Mật khẩu hiện tại</label>
                        <input type="password" name="oldPassword" placeholder="Nhập mật khẩu hiện tại" required>
                    </div>



                    <div class="form-group">
                        <label>Mật khẩu mới</label>
                        <input type="password" name="newPassword" placeholder="Nhập mật khẩu mới" required>
                    </div>

                    <div class="form-group">
                        <label>Xác nhận mật khẩu</label>
                        <input type="password" name="confirmPassword" placeholder="Xác nhận mật khẩu mới" required>
                    </div>

                    <button type="submit" class="login-btn">Xác nhận</button>
                    <!-- Hiển thị lỗi nếu có -->
                    <c:if test="${not empty error}">
                        <p class="error-message">${error}</p>
                    </c:if>
                </form>

            </div>
        </div>

        <c:if test="${not empty success}">
            <script>
                Swal.fire({
                    icon: 'success',
                    title: '${success}',
                    showConfirmButton: false,
                    timer: 1500
                }).then(() => {
                    window.location.href = '${pageContext.request.contextPath}/login';
                });
            </script>
        </c:if>
    </body>
</html>
