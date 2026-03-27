<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>

    <head>
        <title>${car.modelName}</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style-base.css?v=6">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/car-detail.css?v=6">

        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/wishlist.css?v=6">

    </head>



    <body>

    <body data-context-path="${pageContext.request.contextPath}">


        <jsp:include page="includes/header.jsp"/>

        <section class="car-detail-page">
            <div class="container">

                <!-- TOP SECTION -->
                <div class="car-top">

                    <!-- LEFT IMAGE -->
                    <div class="car-gallery">

                        <div class="main-image">
                            <button class="nav prev" type="button">‹</button>

                            <img id="mainCarImage"
                                 src="${pageContext.request.contextPath}/assets/images/cars/${car.imageFolder}/${car.imageFolder}_1.jpg"
                                 alt="${car.modelName}">

                            <button class="nav next" type="button">›</button>
                        </div>

                        <div class="thumbs">
                            <c:forEach var="i" begin="1" end="5">
                                <img src="${pageContext.request.contextPath}/assets/images/cars/${car.imageFolder}/${car.imageFolder}_${i}.jpg"
                                     alt="${car.modelName} image ${i}">
                            </c:forEach>
                        </div>

                    </div>

                    <!-- RIGHT INFO -->
                    <div class="car-summary">

                        <c:if test="${not empty BOOKING_ERROR}">
                            <div class="alert alert-danger">${BOOKING_ERROR}</div>
                        </c:if>

                        <h1>
                            ${car.modelName}
                           
                        </h1>

                        <div class="price">
                            <fmt:formatNumber value="${car.pricePerDay}" pattern="#,###"/> VND / day
                        </div>

                        <c:if test="${not empty car.description}">
                            <div class="badge">
                                ${car.description}
                            </div>
                        </c:if>

                        <div class="rental-calendar-box">

                            <h3>Chọn thời gian thuê</h3>

                            <c:if test="${not empty BOOKING_ERROR}">
                                <div class="date-error-box">${BOOKING_ERROR}</div>
                            </c:if>

                            <div id="calendarDateError" class="date-error-box" style="display:none;"></div>

                            <form id="bookingFromDetailForm"
                                  action="${pageContext.request.contextPath}/booking"
                                  method="get">
                                <input type="hidden" name="action" value="create">
                                <input type="hidden" name="carId" value="${car.carId}">
                                <input type="hidden" id="startDate" name="startDate" value="${startDate}">
                                <input type="hidden" id="endDate" name="endDate" value="${endDate}">

                                <div class="rental-summary-trigger" id="openRentalModal" role="button" tabindex="0">

                                    <div class="rental-summary-grid">
                                        <div class="rental-summary-col">
                                            <div class="rental-summary-label">Nhận xe</div>
                                            <div class="rental-summary-value" id="displayStartDate">
                                                <c:choose>
                                                    <c:when test="${not empty startDate}">
                                                        ${startDate}
                                                    </c:when>
                                                    <c:otherwise>
                                                        Chọn ngày nhận xe
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>

                                        <div class="rental-summary-col">
                                            <div class="rental-summary-label">Trả xe</div>
                                            <div class="rental-summary-value" id="displayEndDate">
                                                <c:choose>
                                                    <c:when test="${not empty endDate}">
                                                        ${endDate}
                                                    </c:when>
                                                    <c:otherwise>
                                                        Chọn ngày trả xe
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>
                                </div>


                                <button type="submit"
                                        id="bookNowBtn"
                                        data-car-available="${car.status eq 'AVAILABLE'}"
                                        class="btn ${car.status ne 'AVAILABLE' ? 'disabled' : ''}"
                                        ${car.status ne 'AVAILABLE' ? 'disabled' : ''}>
                                    Đặt xe
                                </button>
                            </form>

                            <div class="specs">
                                <div>🚗 ${car.seatCount} chỗ</div>
                                <div>⚙ ${car.transmission}</div>
                                <div>⛽ ${car.fuelType}</div>
                                <div>📅 Năm ${car.modelYear}</div>
                                <c:if test="${not empty car.typeName}">
                                    <div>🚘 ${car.typeName}</div>
                                </c:if>
                                <c:if test="${not empty car.brandName}">
                                    <div>🏷 ${car.brandName}</div>
                                </c:if>
                            </div>

                            <c:if test="${sessionScope.ACCOUNT != null and sessionScope.ACCOUNT.roleId == 1}">
                                <button type="button"
                                        class="wishlist-btn"
                                        data-car-id="${car.carId}">
                                    ❤ Thêm vào yêu thích
                                </button>
                            </c:if>

                            <a href="#" class="consult">Nhận thông tin tư vấn</a>


                        </div>

                    </div>
                </div>

                <!-- DESCRIPTION -->
                <div class="section">
                    <h2>Mô tả xe</h2>
                    <p>${car.description}</p>
                </div>

                <!-- REVIEWS -->
                <div class="section review-section">
                    <h2>Đánh giá</h2>

                    <c:choose>
                        <c:when test="${not empty reviews}">
                            <c:forEach var="review" items="${reviews}">
                                <div class="review-card">
                                    <div class="review-header">
                                        <span>${review.customerName}</span>
                                        <span class="review-stars">${review.rating} ★</span>
                                    </div>

                                    <div class="review-date">
                                        ${review.createdAt}
                                    </div>

                                    <div class="review-comment">
                                        ${review.comment}
                                    </div>

                                    <c:if test="${sessionScope.ACCOUNT != null and review.customerId == sessionScope.CUSTOMER.customerId}">
                                        <button type="button"
                                                class="review-edit"
                                                data-review-id="${review.reviewId}"
                                                data-rating="${review.rating}"
                                                data-comment="<c:out value='${review.comment}'/>">
                                            ✏
                                        </button>
                                    </c:if>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <p>Chưa có đánh giá nào.</p>
                        </c:otherwise>
                    </c:choose>
                </div>

            </div>
        </section>

        <!-- EDIT REVIEW MODAL -->
        <div id="editReviewModal" class="edit-modal" style="display:none;">
            <div class="edit-modal-content">
                <div class="edit-modal-header">
                    <h3>Edit Review</h3>
                    <button type="button" class="edit-close" onclick="closeEditReviewModal()">×</button>
                </div>

                <form id="editReviewForm"
                      action="${pageContext.request.contextPath}/review"
                      method="post">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="reviewId" id="editReviewId">

                    <div class="edit-form-group">
                        <label for="editRating" class="edit-label">Rating</label>
                        <select name="rating" id="editRating" class="edit-select" required>
                            <option value="5">⭐⭐⭐⭐⭐ (5)</option>
                            <option value="4">⭐⭐⭐⭐ (4)</option>
                            <option value="3">⭐⭐⭐ (3)</option>
                            <option value="2">⭐⭐ (2)</option>
                            <option value="1">⭐ (1)</option>
                        </select>
                    </div>

                    <div class="edit-form-group">
                        <label for="editComment" class="edit-label">Write Feedback</label>
                        <textarea name="comment"
                                  id="editComment"
                                  rows="6"
                                  class="edit-textarea"
                                  placeholder="Write your feedback..."
                                  required></textarea>
                    </div>

                    <div class="edit-actions">
                        <button type="submit" class="edit-btn save-btn">Save</button>
                        <button type="button" class="edit-btn cancel-btn" onclick="closeEditReviewModal()">Cancel</button>
                    </div>
                </form>
            </div>
        </div>

        <div id="rentalTimeModal" class="rental-time-modal">
            <div class="rental-time-modal-content">
                <button type="button" class="rental-time-close" id="closeRentalModal">×</button>

                <h3 class="rental-time-title">Thời gian</h3>

                <div id="rentalCalendar"
                     data-busy-dates='${empty busyDatesJson ? "[]" : busyDatesJson}'></div>

                <div class="rental-modal-actions">
                    <button type="button" class="rental-confirm-btn" id="confirmRentalSelection">
                        Tiếp tục
                    </button>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
        <script src="${pageContext.request.contextPath}/assets/js/car-detail.js?v=9"></script>
        <script src="${pageContext.request.contextPath}/assets/js/verify-license.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/wishlist.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/edit-review.js"></script>
    </body>
</html>