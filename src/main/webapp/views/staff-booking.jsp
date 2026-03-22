<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Manage Bookings</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/staff.css">
    </head>

    <body>
        <div class="staff-layout">

            <%@ include file="sidebar.jsp" %>

            <div class="staff-content">
                <h1 class="dashboard-title">Manage Bookings</h1>

                <div class="dashboard-table">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Customer</th>
                                <th>Car</th>
                                <th>Start</th>
                                <th>End</th>
                                <th>Total</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:choose>
                                <c:when test="${not empty bookingList}">
                                    <c:forEach var="b" items="${bookingList}">
                                        <tr>
                                            <td>${b.bookingId}</td>
                                            <td>${b.customerName}</td>
                                            <td>${b.carName}</td>
                                            <td>${b.startDate}</td>
                                            <td>${b.endDate}</td>
                                            <td>
                                                <fmt:formatNumber value="${b.totalEstimatedPrice}" pattern="#,###" />
                                                VND
                                            </td>
                                            <td>
                                                <span class="status-badge ${fn:toLowerCase(b.status)}">
                                                    ${b.status}
                                                </span>
                                            </td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/staff/bookings?action=detail&id=${b.bookingId}"
                                                   class="btn-view">
                                                    View
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>

                                <c:otherwise>
                                    <tr>
                                        <td colspan="8" style="text-align: center;">No bookings found.</td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </body>
</html>