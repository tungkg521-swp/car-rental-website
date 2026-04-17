<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

<header class="site-header">
    <div class="container header-inner">

        <!-- LEFT: LOGO -->
        <div class="brand brand-left">
            <a href="${pageContext.request.contextPath}/home"
               style="text-decoration:none;color:inherit">
                <span class="top">AUTOMOBILI</span>
                <span class="bottom">Rental Car</span>
            </a>
        </div>

        <!-- CENTER: MENU -->
        <nav class="nav-main nav-center">
            <a href="${pageContext.request.contextPath}/cars">Thuê xe</a>
            <a href="#">Khuyến mãi</a>
            <a href="#">Giới thiệu</a>

        </nav>

        <!-- RIGHT: AUTH -->
        <div class="auth auth-right">
            <c:if test="${empty sessionScope.ACCOUNT}">
                <a href="${pageContext.request.contextPath}/login" class="login">Login</a>
                <a href="${pageContext.request.contextPath}/register" class="register">Register</a>
            </c:if>

            <c:if test="${not empty sessionScope.ACCOUNT}">

                <div class="customer-header-actions">
                    <a href="${pageContext.request.contextPath}/customer/bookings?action=list"
                       class="customer-action-btn booking-btn"
                       title="My Bookings">
                        <i class="fa-solid fa-clipboard-list"></i>
                        <span>Đơn thuê</span>
                    </a>

                    <button type="button"
                            class="customer-action-btn notify-btn"
                            onclick="toggleNotification()"
                            title="Notifications">
                        <i class="fa-regular fa-bell"></i>
                    </button>

                    <a href="${pageContext.request.contextPath}/customer/profile" class="profile-link">
                        <div class="header-avatar">
                            ${sessionScope.CUSTOMER.fullName.substring(0,1)}
                        </div>
                        <div class="profile-meta">
                            <span class="profile-greeting">Xin chào</span>
                            <span class="user-name">${sessionScope.CUSTOMER.fullName}</span>
                        </div>
                        <i class="fa-solid fa-chevron-down profile-arrow"></i>
                    </a>

                    <a href="${pageContext.request.contextPath}/logout" class="logout">Logout</a>
                </div>

            </c:if>
        </div>
    </div>
</header>

