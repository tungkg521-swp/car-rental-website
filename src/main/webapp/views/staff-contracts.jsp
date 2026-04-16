
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Manage Contracts</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/staff.css?v=3">
</head>
<body>

<div class="staff-layout">

    <%@ include file="sidebar.jsp" %>

    <div class="staff-content">
        <h1 class="dashboard-title">Manage Contracts</h1>

        <div class="dashboard-table">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Booking</th>
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
                <c:forEach var="c" items="${contractList}">
                    <tr>
                        <td>${c.contractId}</td>
                        <td>#${c.bookingId}</td>
                        <td>${c.customerName}</td>
                        <td>${c.carName}</td>
                        <td><fmt:formatDate value="${c.contractStartDate}" pattern="dd/MM/yyyy"/></td>
                        <td><fmt:formatDate value="${c.contractEndDate}" pattern="dd/MM/yyyy"/></td>
                        <td><fmt:formatNumber value="${c.totalAmount}" pattern="#,###"/> VND</td>
                        <td>
                            <span class="status-badge ${c.contractStatus.toLowerCase()}">
                                ${c.contractStatus}
                            </span>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/staff/contracts?action=detail&id=${c.contractId}"
                               class="btn-view">
                                View
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

</body>
</html>