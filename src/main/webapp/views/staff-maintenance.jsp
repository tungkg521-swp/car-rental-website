<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Maintenance Schedule</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
</head>
<body>
    <div class="staff-layout">
        <%@ include file="sidebar.jsp" %>

        <div class="staff-content">
            <h1>Maintenance Schedule</h1>

            <a href="${pageContext.request.contextPath}/staff/maintenance?action=add" class="btn-add">+ Add New Maintenance</a>

            <table class="table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Car</th>
                        <th>Type</th>
                        <th>Scheduled Date</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="m" items="${maintenances}">
                        <tr>
                            <td>${m.maintenanceId}</td>
                            <td>${m.modelName}</td>
                            <td>${m.maintenanceType}</td>
                            <td>${m.scheduledDate}</td>
                            <td class="status-${m.status.toLowerCase()}">${m.status}</td>
                            <td>
                                <a href="?action=detail&id=${m.maintenanceId}">Detail</a> |
                                <a href="?action=edit&id=${m.maintenanceId}">Edit</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>