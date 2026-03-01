<%-- 
    Document   : wishlist
    Created on : Feb 28, 2026, 12:24:20 PM
    Author     : DatTT
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title> Wish list  </title>
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/style.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/profile.css?v=4">
    </head>
    <body>
        <jsp:include page="includes/header.jsp"/>

        <div class="profile-wrapper">
            <!-- SIDEBAR -->
            <div class="profile-sidebar">
                <h2 class="sidebar-title">Xin chào bạn!</h2>

                <ul class="sidebar-menu">
                    <li class="sidebar-item active">
                        <a href="${pageContext.request.contextPath}/customer/profile">
                            👤 Tài khoản của tôi
                        </a>
                    </li>

                    <li class="sidebar-item active">
                        <a href="${pageContext.request.contextPath}/wishlist?id=${sessionScope.CUSTOMER.customerId}">
                            Xe yêu thích
                        </a>
                    </li>

                    <li class="sidebar-item">
                        <a href="${pageContext.request.contextPath}/">
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
            <div class="profile-content">
                <div class="profile-card">
                    <h1 class="wishlist-title">Xe yêu thích của tôi</h1>

                    <c:forEach var="w" items="${wishlist}">
                        <div class="wishlist-card">

                            <!-- IMAGE -->
                            <div class="wishlist-image">
                                <img src="${w.imageUrl}" alt="${w.modelName}">
                            </div>

                            <!-- INFO -->
                            <div class="wishlist-info">
                                <h4 class="car-title">
                                    ${w.brandName}     ${w.modelName}     ${w.modelYear}
                                </h4>

                                <div class="car-specs">
                                    <span>⚙ ${w.transmission}</span>
                                    <span>⛽ ${w.fuelType}</span>
                                    <span>👥 ${w.seatCount} chỗ</span>
                                    <span>${w.typeName}</span>
                                </div>
                            </div>

                            <!-- ACTION -->
                            <div class="wishlist-action">
                                <div class="price-box">

                                    <div class="new-price">${w.pricePerDay}K/ngày</div>
                                </div>

                                <button class="btn-remove">Bỏ thích</button>
                                <a href="#" class="detail-link">Xem chi tiết</a>
                            </div>

                        </div>
                    </c:forEach>

                </div>

            </div>
        </div>




    </body>
</html>
