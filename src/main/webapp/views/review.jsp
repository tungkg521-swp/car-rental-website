<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Car Reviews</title>

        <style>

            body{
                font-family: Arial, Helvetica, sans-serif;
                margin:40px;
                background:#f5f5f5;
            }

            .review-box{
                background:white;
                border:1px solid #ddd;
                padding:15px;
                margin-bottom:15px;
                border-radius:8px;
            }

            .customer-name{
                font-weight:bold;
                margin-bottom:5px;
            }

            .rating{
                color:orange;
                font-weight:bold;
                margin-bottom:5px;
            }

            textarea{
                width:100%;
                height:100px;
            }

            button{
                background:#2c7be5;
                color:white;
                border:none;
                padding:10px 15px;
                border-radius:5px;
                cursor:pointer;
            }

            button:hover{
                background:#1a5fd0;
            }

        </style>
    </head>

    <body>

        <h2>Customer Reviews</h2>

        <c:if test="${not empty error}">
            <p style="color:red">${error}</p>
        </c:if>

        <c:forEach var="r" items="${reviews}">
            <div class="review-box">

                <div class="customer-name">
                    👤 ${r.customerName}
                </div>

                <div class="rating">
                    ⭐ ${r.rating}/5
                </div>

                <div>
                    ${r.comment}
                </div>

                <div style="font-size:12px;color:gray">
                    ${r.createdAt}
                </div>

            </div>
        </c:forEach>

        <hr>

        <c:if test="${sessionScope.CUSTOMER != null}">

            <h3>Write Review</h3>

            <form action="${pageContext.request.contextPath}/review" method="post">

                <input type="hidden" name="carId" value="${carId}">

                <label>Rating</label>

                <select name="rating">
                    <option value="5">5 - ⭐⭐⭐⭐⭐</option>
                    <option value="4">4 - ⭐⭐⭐⭐</option>
                    <option value="3">3 - ⭐⭐⭐</option>
                    <option value="2">2 - ⭐⭐</option>
                    <option value="1">1 - ⭐</option>
                </select>

                <br><br>

                <textarea name="comment" placeholder="Write your review"></textarea>

                <br><br>

                <button type="submit">Submit Review</button>

            </form>

        </c:if>

        <c:if test="${sessionScope.CUSTOMER == null}">
            <p>Please <a href="login.jsp">login</a> to write a review.</p>
        </c:if>

    </body>
</html>