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
                <div class="maintenance-page-shell">
                    <div class="maintenance-header">
                        <div>
                            <h1 class="maintenance-title">Maintenance Schedule</h1>
                        </div>

                        <a href="${pageContext.request.contextPath}/staff/maintenance?action=add" class="btn-add-maintenance">
                            + Add New Maintenance
                        </a>
                    </div>

                    <c:if test="${not empty sessionScope.message}">
                        <div class="maintenance-alert success-alert">
                            ${sessionScope.message}
                        </div>
                        <c:remove var="message" scope="session"/>
                    </c:if>

                    <div class="maintenance-table-card">
                        <table class="maintenance-table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Car</th>
                                    <th>Type</th>
                                    <th>Start Date</th>
                                    <th>End Date</th>
                                    <th>Status</th>
                                    <th class="actions-col">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="m" items="${maintenanceList}">
                                    <tr>
                                        <td class="maintenance-id">#${m.maintenanceId}</td>
                                        <td>
                                            <div class="maintenance-car-cell">
                                                <div class="maintenance-car-name">${m.modelName}</div>
                                                <div class="maintenance-car-plate">${m.licensePlate}</div>
                                            </div>
                                        </td>
                                        <td>${m.maintenanceType}</td>
                                        <td>${m.startDate}</td>
                                        <td>${m.endDate}</td>
                                        <td class="status-cell">
                                            <span class="status-badge status-${m.status.toLowerCase().replace('_','-')}">
                                                ${m.status}
                                            </span>
                                        </td>
                                        <td class="action">
                                            <div class="maintenance-action-group">
                                                <a href="${pageContext.request.contextPath}/staff/maintenance?action=detail&id=${m.maintenanceId}" class="btn btn-view">
                                                    View
                                                </a>

                                                <form action="${pageContext.request.contextPath}/staff/maintenance" method="post" class="delete-inline-form"
                                                      onsubmit="return confirm('Bạn có chắc muốn xóa lịch bảo dưỡng này không?');">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="maintenanceId" value="${m.maintenanceId}">
                                                    <button type="submit" class="btn btn-delete">Delete</button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>

                                <c:if test="${empty maintenanceList}">
                                    <tr>
                                        <td colspan="7" class="empty-maintenance-cell">Chưa có lịch bảo dưỡng nào</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>