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
          href="${pageContext.request.contextPath}/assets/css/wishlist.css?v=13">
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

                                <div class="wishlist-info">
                                    <h4 class="car-name-row">
                                        <span class="car-model-name">${w.modelName}</span>
                                        <span class="car-name-dot">•</span>
                                        <span class="car-plate-inline">${w.plateNumber}</span>
                                    </h4>

                                    <p class="car-sub-meta">
                                        ${w.brandName} • ${w.typeName}
                                    </p>

                                    <div class="car-spec-line">
                                        <span class="car-spec-item">🚗 ${w.seatCount} chỗ</span>
                                        <span class="car-spec-separator">||</span>
                                        <span class="car-spec-item">⚙ ${w.transmission}</span>
                                        <span class="car-spec-separator">||</span>
                                        <span class="car-spec-item fuel-item">⛽ ${w.fuelType}</span>
                                    </div>
                                </div>

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

