<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/profile.css">
<body>
    <jsp:include page="includes/header.jsp"/>
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