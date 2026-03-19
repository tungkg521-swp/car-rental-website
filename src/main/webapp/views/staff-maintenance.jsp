<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Maintenance Schedule</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/maintenance.css">
</head>
<body>
    <div class="staff-layout">
        <%@ include file="sidebar.jsp" %>

        <div class="staff-content">
            <div class="maintenance-header">
                <h1 class="maintenance-title">Maintenance Schedule</h1>
                <a href="${pageContext.request.contextPath}/staff/maintenance?action=add" 
                   class="btn-add-maintenance">+ Add New Maintenance</a>
            </div>

            <table class="maintenance-table">
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
                            <td>#${m.maintenanceId}</td>
                            <td>${m.modelName} - ${m.licensePlate}</td>
                            <td>${m.maintenanceType}</td>
                            <td>${m.scheduledDate}</td>
                            <td>
                                <span class="status-badge status-${m.status.toLowerCase()}">
                                    ${m.status}
                                </span>
                            </td>
                            <td class="action-cell">
                                <a href="?action=detail&id=${m.maintenanceId}" class="btn-view">View</a>
                                <a href="#" onclick="confirmDelete(${m.maintenanceId})" 
                                   class="btn-delete">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty maintenances}">
                        <tr><td colspan="6" style="text-align:center;padding:50px;">Chưa có lịch bảo dưỡng nào</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <script>
        function confirmDelete(id) {
            if (confirm("XÓA lịch bảo dưỡng #" + id + "?\nXe sẽ tự động về AVAILABLE.")) {
                window.location.href = "${pageContext.request.contextPath}/staff/maintenance?action=delete&id=" + id;
            }
        }
    </script>
</body>
</html>