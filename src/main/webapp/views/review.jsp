<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Car Reviews</title>

        <style>
            body{
                font-family: 'Segoe UI', Tahoma, sans-serif;
                background:#f4f6f9;
                margin:0;
                padding:40px;
            }
            h2{
                margin-bottom:20px;
            }
            .container{
                max-width:800px;
                margin:auto;
            }
            .review-box{
                background:white;
                border-radius:10px;
                padding:15px 20px;
                margin-bottom:15px;
                box-shadow:0 2px 8px rgba(0,0,0,0.08);
            }
            .customer-name{
                font-weight:bold;
                font-size:15px;
                margin-bottom:5px;
            }
            .rating{
                color:#f5a623;
                font-size:14px;
                margin-bottom:5px;
            }
            textarea{
                width:100%;
                height:100px;
                border-radius:8px;
                border:1px solid #ccc;
                padding:10px;
                resize:none;
                outline:none;
            }
            textarea:focus{
                border-color:#2c7be5;
            }
            select{
                padding:5px;
                border-radius:5px;
            }
            button{
                background:#2c7be5;
                color:white;
                border:none;
                padding:10px 18px;
                border-radius:6px;
                cursor:pointer;
                transition:0.2s;
            }
            button:hover{
                background:#1a5fd0;
            }
            .form-box, .car-info{
                background:white;
                padding:20px;
                border-radius:10px;
                box-shadow:0 2px 8px rgba(0,0,0,0.08);
            }

            .car-detail p, h3{
                margin:4px 0;
            }
        </style>
    </head>

    <body>
        <div class="container">

            <h2>Customer Feedback ⭐</h2>

            <!-- CAR INFO -->
            <c:if test="${not empty car}">
                <div class="car-info" style="display:flex; gap:15px; align-items:flex-start;">
                    <!-- Ảnh xe 70% -->
                    <div style="flex:7;">
                        <img src="${pageContext.request.contextPath}/${car.imageUrl}" 
                             alt="${car.modelName}" style="width:100%; border-radius:8px;">
                    </div>
                    <!-- Thông tin xe 30% -->
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