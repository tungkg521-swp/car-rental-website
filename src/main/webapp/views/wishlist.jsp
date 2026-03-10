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
              <jsp:include page="profile-sidebar.jsp"/>
              
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
