<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <title>${car.modelName}</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style-base.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/car-detail.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/wishlist.css">
    </head>

    <body>

        <jsp:include page="includes/header.jsp"/>

        <section class="car-detail-page">
            <div class="container">

                <!-- TOP SECTION -->
                <div class="car-top">

                    <!-- LEFT IMAGE -->
                    <div class="car-gallery">

                        <div class="main-image">
                            <button class="nav prev" onclick="prevImage()">‹</button>

                            <img id="mainCarImage"
                                 src="${pageContext.request.contextPath}/assets/images/cars/${car.imageFolder}/${car.imageFolder}_1.jpg"
                                 alt="${car.modelName}">

                            <button class="nav next" onclick="nextImage()">›</button>
                        </div>

                        <div class="thumbs">
                            <c:forEach var="i" begin="1" end="5">
                                <img src="${pageContext.request.contextPath}/assets/images/cars/${car.imageFolder}/${car.imageFolder}_${i}.jpg"
                                     onclick="changeImage(${i})">
                            </c:forEach>
                        </div>

                    </div>

                    <!-- RIGHT INFO -->
                    <div class="car-summary">

                        <h1>
                            ${car.modelName}
                            <span class="status">${car.status}</span>
                        </h1>

                        <div class="price">
                            <fmt:formatNumber value="${car.pricePerDay}" pattern="#,###"/> VND / day
                        </div>

                        <div class="badge">
                            Miễn phí sạc tới 31/12/2027
                        </div>

                        <ul class="specs">
                            <li>🚗 ${car.seatCount} chỗ</li>
                            <li>⚙️ ${car.transmission}</li>
                            <li>⛽ ${car.fuelType}</li>
                            <li>📅 Năm ${car.modelYear}</li>
                            <li>🚘 ${car.typeName}</li>
                            <li>🏷️ ${car.brandName}</li>
                        </ul>

                        <a href="${pageContext.request.contextPath}/booking?action=create&carId=${car.carId}"
                           class="btn ${car.status ne 'AVAILABLE' ? 'disabled' : ''}">
                            Đặt xe
                        </a>


                        <form class="wishlist-form"
                              action="${pageContext.request.contextPath}/wishlist?action=add"
                              method="POST">

                            <input type="hidden" name="carId" value="${car.carId}">

                            <button type="submit" class="wishlist-btn">
                                ❤️ Thêm vào yêu thích
                            </button>

                            <c:if test="${not empty SUCCESS}">
                                <div class="alert success">${SUCCESS}</div>
                            </c:if>

                            <c:if test="${not empty ERROR}">
                                <div class="alert error">${ERROR}</div>
                            </c:if>
                        </form>


                        <a href="#" class="consult">Nhận thông tin tư vấn</a>

                    </div>
                </div>


                <!-- DESCRIPTION -->
                <div class="section">
                    <h2>Mô tả xe</h2>
                    <p>${car.description}</p>
                </div>


                <!-- CONDITIONS -->
                <div class="bottom-grid">

                    <div class="conditions">

                        <h2>Điều kiện thuê xe</h2>

                        <div class="box">
                            <h4>Thông tin cần có khi nhận xe</h4>
                            <ul>
                                <li>CCCD hoặc Hộ chiếu còn thời hạn</li>
                                <li>Bằng lái hợp lệ</li>
                            </ul>
                        </div>

                        <div class="box">
                            <h4>Hình thức thanh toán</h4>
                            <ul>
                                <li>Trả trước</li>
                                <li>Thanh toán 100% khi nhận xe</li>
                            </ul>
                        </div>

                        <div class="box">
                            <h4>Chính sách đặt cọc</h4>
                            <p>Đặt cọc theo quy định của chủ xe</p>
                        </div>

                    </div>


                    <div class="similar">
                        <h2>Xe tương tự</h2>
                        <p>Đang cập nhật...</p>
                    </div>

                </div>


                <!-- REVIEWS -->
                <div class="section review-section">

                    <h2>Customer Reviews</h2>
                    <p>Xem đánh giá từ khách hàng đã thuê xe này.</p>

                    <c:if test="${empty reviews}">
                        <div class="no-review">
                            No reviews yet. Be the first to review this car.
                        </div>
                    </c:if>

                    <c:forEach var="r" items="${reviews}">

                        <div class="review-card">

                            <div class="review-header">

                                <div class="review-user">
                                    👤 ${r.customerName}
                                </div>
                                <c:if test="${not empty sessionScope.CUSTOMER and sessionScope.CUSTOMER.customerId == r.customerId}">
    <a class="review-edit"
       href="${pageContext.request.contextPath}/review?action=edit&reviewId=${r.reviewId}&carId=${car.carId}">
        ✏
    </a>
</c:if>

                                <div class="review-stars">
                                    <c:forEach begin="1" end="${r.rating}">
                                        ⭐
                                    </c:forEach>
                                </div>

                                <div class="review-date">
                                    <fmt:formatDate value="${r.createdAt}" pattern="dd/MM/yyyy"/>
                                </div>

                            </div>

                            <div class="review-comment">
                                ${r.comment}
                            </div>

                        </div>

                    </c:forEach>

                </div>

            </div>
        </section>
        <c:if test="${LICENSE_REQUIRED}">
            <div id="verifyModal" class="verify-modal">
                <div class="verify-box">
                    <div class="verify-icon">!</div>

                    <p>Bạn cần xác thực <b>GPLX</b> mới có thể đặt xe.</p>

                    <div class="verify-actions">
                        <a href="${pageContext.request.contextPath}/customer/profile"
                           class="verify-btn primary">
                            Đi xác thực ngay
                        </a>

                        <button type="button"
                                onclick="closeVerifyModal()"
                                class="verify-btn secondary">
                            Đóng
                        </button>
                    </div>
                </div>
            </div>

            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    openVerifyModal();
                });
            </script>
        </c:if>

        <script src="${pageContext.request.contextPath}/assets/js/car-detail.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/verify-license.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/wishlist.js"></script>

    </body>

</html>

