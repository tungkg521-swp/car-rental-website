<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
    <head>

        <title>Car Reviews</title>

        <style>

            body{
                font-family: Arial;
                margin:40px;
            }

            .review-box{
                border:1px solid #ddd;
                padding:15px;
                margin-bottom:15px;
                border-radius:8px;
            }

            .rating{
                color:orange;
                font-weight:bold;
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

        </style>

    </head>

    <body>

        <h2>Customer Reviews</h2>

        <c:forEach var="r" items="${reviews}">

            <div class="review-box">

                <div class="rating">
                    ⭐ ${r.rating}/5
                </div>

                <div>
                    ${r.comment}
                </div>

                <div>
                    ${r.createdAt}
                </div>

            </div>

        </c:forEach>

        <hr>

        <h3>Write Review</h3>

        <form action="review" method="post">

            <input type="hidden" name="carId" value="${param.carId}">
            <input type="hidden" name="bookingId" value="${param.bookingId}">

            <label>Rating</label>

            <select name="rating">

                <option value="5">⭐⭐⭐⭐⭐</option>
                <option value="4">⭐⭐⭐⭐</option>
                <option value="3">⭐⭐⭐</option>
                <option value="2">⭐⭐</option>
                <option value="1">⭐</option>

            </select>

            <br><br>

            <textarea name="comment" placeholder="Write your review"></textarea>

            <br><br>

            <button type="submit">Submit Review</button>

        </form>

    </body>
</html>