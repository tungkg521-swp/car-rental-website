<%-- 
    Document   : wishlist
    Created on : Feb 28, 2026, 12:24:20 PM
    Author     : DatTT
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title> Wish list  </title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/wishlist.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/car-detail.css">
</head>
<body>
    <jsp:include page="includes/header.jsp"/>

    <div class="profile-wrapper">
        <jsp:include page="profile-sidebar.jsp"/>

        <div class="profile-content">
            <div class="profile-card">
                <c:if test="${not empty success}">
                    <div class="alert  success">${success}</div>
                </c:if>

                <c:if test="${not empty error}">
                    <div class="alert  error">${success}</div>
                </c:if>
                <h1 class="wishlist-title">Xe yêu thích của tôi</h1>



                <c:choose>

                    <c:when test="${empty wishlist}">
                        <div class="empty-wishlist">
                            🚗 Bạn chưa có xe nào trong danh sách yêu thích.
                        </div>
                    </c:when>

                    <c:otherwise>
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
                                    <div class="price">
                                        <fmt:formatNumber value="${w.pricePerDay}" pattern="#,###"/> VND / day
                                    </div>

                                    <form action="${pageContext.request.contextPath}/wishlist?action=delete" method="POST">
                                        <input type="hidden" name="carId" value="${w.carId}">
                                        <button type="submit" class="btn-remove">
                                            Bỏ thích
                                        </button>
                                    </form>
                                    <a href="${pageContext.request.contextPath}/cars?action=detail&carId=${w.carId}" class="detail-link">Xem chi tiết</a>
                                </div>

                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </div>



    <script src="${pageContext.request.contextPath}/assets/js/wishlist.js"></script>
</body>

