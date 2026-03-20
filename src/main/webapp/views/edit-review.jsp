<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Edit Review</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style-base.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/car-detail.css">
    </head>
    <body>

        <jsp:include page="includes/header.jsp"/>

        <section class="car-detail-page">
            <div class="container">

                <div class="review-form" style="max-width:700px; margin:0 auto;">
                    <h2>Edit Review</h2>

                    <c:if test="${not empty sessionScope.error}">
                        <div class="alert error">${sessionScope.error}</div>
                        <c:remove var="error" scope="session"/>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/review" method="post">
                        <input type="hidden" name="action" value="update"/>
                        <input type="hidden" name="reviewId" value="${review.reviewId}"/>
                        <input type="hidden" name="carId" value="${carId}"/>

                        <div class="form-group">
                            <label>Car</label>
                            <input type="text" value="${car.modelName}" readonly
                                   style="width:100%; padding:10px; border:1px solid #ccc; border-radius:6px;">
                        </div>

                        <div class="form-group">
                            <label>Rating</label>
                            <select name="rating" class="rating-select">
                                <option value="5" <c:if test="${review.rating == 5}">selected</c:if>>⭐⭐⭐⭐⭐</option>
                                <option value="4" <c:if test="${review.rating == 4}">selected</c:if>>⭐⭐⭐⭐</option>
                                <option value="3" <c:if test="${review.rating == 3}">selected</c:if>>⭐⭐⭐</option>
                                <option value="2" <c:if test="${review.rating == 2}">selected</c:if>>⭐⭐</option>
                                <option value="1" <c:if test="${review.rating == 1}">selected</c:if>>⭐</option>
                                </select>
                            </div>

                            <div class="form-group">
                                <label>Comment</label>
                                <textarea name="comment" rows="6">${review.comment}</textarea>
                        </div>

                        <div style="display:flex; gap:12px;">
                            <button type="submit" class="review-btn">Save</button>

                            <a href="${pageContext.request.contextPath}/cars?action=detail&carId=${carId}"
                               class="review-btn"
                               style="text-decoration:none; display:inline-flex; align-items:center; justify-content:center; background:#6b7280;">
                                Cancel
                            </a>
                        </div>
                    </form>
                </div>

            </div>
        </section>

    </body>
</html>