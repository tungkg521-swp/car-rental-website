<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<body>
    <jsp:include page="includes/header.jsp"/>
    <style>
        .profile-card .save-btn{
            background:#0d6efd;
            color:white;
            border:none;
            padding:12px 28px;
            border-radius:25px;
            font-size:15px;
            font-weight:600;
            cursor:pointer;
            transition:0.25s;
        }

        .profile-card .save-btn:hover{
            background:#0b5ed7;
        }

        .error-message{
            color:#e53935;
            background:#fdecea;
            padding:10px;
            border-radius:6px;
            margin-top:12px;
            font-size:14px;
        }
    </style>
    <div class="profile-wrapper">

        <jsp:include page="profile-sidebar.jsp"/>

        <div class="profile-content">

            <div class="profile-card">

                <h2>Thay đổi mật khẩu</h2>

                <form action="${pageContext.request.contextPath}/customer/profile"
                      method="post">

                    <input type="hidden" name="action" value="changePassword">

                    <div class="form-group">
                        <label>Mật khẩu hiện tại</label>
                        <input type="password" name="oldPassword" required>
                    </div>

                    <div class="form-group">
                        <label>Mật khẩu mới</label>
                        <input type="password" name="newPassword" required>
                    </div>

                    <div class="form-group">
                        <label>Xác nhận mật khẩu</label>
                        <input type="password" name="confirmPassword" required>
                    </div>

                    <button type="submit" class="save-btn" >
                        Đổi mật khẩu
                    </button>

                    <c:if test="${not empty error}">
                        <p class="error-message">${error}</p>
                    </c:if>

                </form>

            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <c:if test="${not empty sessionScope.success}">
        <script>
            Swal.fire({
                icon: 'success',
                title: '${sessionScope.success}',
                showConfirmButton: false,
                timer: 1500
            }).then(() => {
                window.location.href = '${pageContext.request.contextPath}/customer/profile?action=changePassword';
            });
        </script>
        <c:remove var="success" scope="session"/>
    </c:if>
</body>