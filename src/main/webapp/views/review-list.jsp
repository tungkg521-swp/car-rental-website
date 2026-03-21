<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Manage Reviews</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
    </head>
    <body>

        <div class="staff-layout">

            <%@ include file="sidebar.jsp" %>

            <div class="staff-content">
                <h1 class="dashboard-title">Manage Reviews</h1>

                <c:if test="${not empty sessionScope.success}">
                    <div class="alert success">${sessionScope.success}</div>
                    <c:remove var="success" scope="session"/>
                </c:if>

                <c:if test="${not empty sessionScope.error}">
                    <div class="alert error">${sessionScope.error}</div>
                    <c:remove var="error" scope="session"/>
                </c:if>

                <div class="dashboard-table">
                    <table>
                        <thead>
                            <tr>
                                <th>Review ID</th>
                                <th>Customer</th>
                                <th>Car ID</th>
                                <th>Booking ID</th>
                                <th>Rating</th>
                                <th>Comment</th>
                                <th>Created At</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="r" items="${reviews}">
                                <tr>
                                    <td>${r.reviewId}</td>
                                    <td>${r.customerName}</td>
                                    <td>${r.carId}</td>
                                    <td>${r.bookingId}</td>
                                    <td>${r.rating}</td>
                                    <td style="max-width: 320px; white-space: normal;">${r.comment}</td>
                                    <td>
                                        <fmt:formatDate value="${r.createdAt}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/admin/review" method="post"
                                              onsubmit="return confirm('Are you sure you want to delete this review?');">
                                            <input type="hidden" name="action" value="delete"/>
                                            <input type="hidden" name="reviewId" value="${r.reviewId}"/>
                                            <button type="submit" class="btn-delete">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty reviews}">
                                <tr>
                                    <td colspan="8">No reviews found.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

    </body>
</html>