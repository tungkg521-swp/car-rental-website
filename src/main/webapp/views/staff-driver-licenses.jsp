<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <title>Manage Driver Licenses</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/staff.css?v=2">
</head>
<body>

<div class="staff-layout">
    <%@ include file="sidebar.jsp" %>

    <div class="staff-content">
        <h1 class="dashboard-title">Manage Driver Licenses</h1>

        <c:if test="${empty licenses}">
            <p>Không có yêu cầu xác thực GPLX nào (status = REQUESTED).</p>
        </c:if>

        <c:if test="${not empty licenses}">
            <table class="table">
                <thead>
                <tr>
                    <th>License ID</th>
                    <th>Customer ID</th>
                    <th>Số GPLX</th>
                    <th>Họ tên</th>
                    <th>Status</th>
                    <th>Created</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="l" items="${licenses}">
                    <tr>
                        <td>${l.licenseId}</td>
                        <td>${l.customerId}</td>
                        <td>${l.licenseNumber}</td>
                        <td>${l.fullName}</td>
                        <td>${l.status}</td>
                        <td>${l.createdAt}</td>
                        <td>
                            <a class="btn"
                               href="${pageContext.request.contextPath}/staff/licenses?action=detail&licenseId=${l.licenseId}">
                                View
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>
</div>

</body>
</html>