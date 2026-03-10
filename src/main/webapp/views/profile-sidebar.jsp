<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/style.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/profile.css?v=8">


<div class="profile-sidebar">

    <h2 class="sidebar-title">Xin chào bạn!</h2>

    <ul class="sidebar-menu">

        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/profile">
                👤 Tài khoản của tôi
            </a>
        </li>

         <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/wishlist">
                 Danh sách yêu thích
            </a>
        </li>
        
        <li class="sidebar-item">
            <a href="${pageContext.request.contextPath}/customer/profile?action=changePassword">
                🔒 Đổi mật khẩu
            </a>
        </li>

        <li class="sidebar-item logout">
            <a href="${pageContext.request.contextPath}/logout">
                ↩ Đăng xuất
            </a>
        </li>

    </ul>

</div>