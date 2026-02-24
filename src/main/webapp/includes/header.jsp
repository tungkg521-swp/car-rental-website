<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Font Awesome (nếu project đã có thì có thể bỏ) -->
<link rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

<header class="site-header">
    <div class="container header-inner">

        <!-- LEFT -->
        <nav class="nav-left">
            <a href="#">The Hypercar</a>
            <a href="#">News</a>
        </nav>

        <!-- CENTER LOGO -->
        <div class="brand">
            <a href="${pageContext.request.contextPath}/home"
               style="text-decoration:none;color:inherit">
                <span class="top">AUTOMOBILI</span>
                <span class="bottom">Rental Car</span>
            </a>
        </div>

        <!-- RIGHT -->
        <div class="header-right">
            <nav class="nav-main">
                <a href="${pageContext.request.contextPath}/cars">Thuê xe</a>
                <a href="#">Khuyến mãi</a>
                <a href="#">Giới thiệu</a>
            </nav>

            <!-- AUTH -->
            <div class="auth">

                <!-- ===== CHƯA LOGIN ===== -->
                <c:if test="${empty sessionScope.ACCOUNT}">
                    <a href="${pageContext.request.contextPath}/login" class="login">Login</a>
                    <a href="${pageContext.request.contextPath}/register" class="register">Register</a>
                </c:if>

                <!-- ===== ĐÃ LOGIN ===== -->
                <c:if test="${not empty sessionScope.ACCOUNT}">

                    <!-- ICON VIEW BOOKING -->
                    <a href="${pageContext.request.contextPath}/customer/bookings?action=list"
                       class="icon-btn booking-btn"
                       title="My Bookings">
                        <i class="fa-solid fa-clipboard-list"></i>
                    </a>

                    <!-- PROFILE LINK -->
                    <a href="${pageContext.request.contextPath}/customer/profile"
                       class="profile-link">

                        <div class="header-avatar">
                            ${sessionScope.CUSTOMER.fullName.substring(0,1)}
                        </div>

                        <span class="user-name">
                            ${sessionScope.CUSTOMER.fullName}
                        </span>
                    </a>

                    <a href="${pageContext.request.contextPath}/logout" class="logout">
                        Logout
                    </a>

                </c:if>


            </div>
        </div>

    </div>
</header>
