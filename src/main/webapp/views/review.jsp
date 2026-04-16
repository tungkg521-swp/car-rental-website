<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Car Reviews</title>
        <link href="${pageContext.request.contextPath}/assets/css/review.css" rel="stylesheet" type="text/css"/>
    </head>

    <body>
        <div class="container">

            <h2>Customer Feedback ⭐</h2>

            <!-- CAR INFO -->
            <c:if test="${not empty car}">
                <div class="car-info" style="display:flex; gap:15px; align-items:flex-start;">
                    <div style="flex:7;">
                        <img src="${pageContext.request.contextPath}/${car.imageUrl}" 
                             alt="${car.modelName}" style="width:100%; border-radius:8px; height: 220px">
                    </div>
                    <div class="car-detail" style="flex:3;">
                        <h3>${car.modelName} (${car.modelYear})</h3>
                        <p>Brand: ${car.brandName}</p>
                        <p>Type: ${car.typeName}</p>
                        <p>Seats: ${car.seatCount}</p>
                        <p>Fuel: ${car.fuelType}</p>
                        <p>Transmission: ${car.transmission}</p>
                        <p><strong>Total Price: $${car.pricePerDay}</strong></p>
                    </div>
                </div>
            </c:if>

            <hr style="margin:30px 0;">

            <!-- FORM REVIEW -->
            <c:if test="${sessionScope.CUSTOMER != null}">
                <div class="form-box">
                    <form action="${pageContext.request.contextPath}/review" method="post">
                        <input type="hidden" name="action" value="add"/>
                        <input type="hidden" name="carId" value="${carId}">
                        <label>Rating</label> <br>
                        <select name="rating">
                            <option value="5">⭐⭐⭐⭐⭐ (5)</option>
                            <option value="4">⭐⭐⭐⭐ (4)</option>
                            <option value="3">⭐⭐⭐ (3)</option>
                            <option value="2">⭐⭐ (2)</option>
                            <option value="1">⭐ (1)</option>
                        </select>

                        <br><br>
                        <label>Write Feedback</label>
                        <textarea name="comment" placeholder="Write your feedback..."></textarea>
                        <br><br>
                        <button type="submit">Submit</button>
                    </form>
                </div>
            </c:if>

        </div>
    </body>
</html>