<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Manage Users</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/staff.css">
             <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/profile.css">
    </head>
    <body>

        <div class="staff-layout">

            <%@ include file="sidebar.jsp" %>


            <div class="staff-content">

                <h1 class="dashboard-title">Manage Users</h1>

                <!-- FILTER -->
                <div class="car-filter">
                    <form action="${pageContext.request.contextPath}/staff/users?action=search" method="POST">

                        <input type="text" 
                               name="namesearch" 
                               placeholder="Search by full name..."
                               value="${param.namesearch}">

                        <select name="status">
                            <option value="ALL">All Status</option>
                            <option value="ACTIVE" ${param.status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                            <option value="INACTIVE" ${param.status == 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
                        </select>

                        <button type="submit">Search</button>
                    </form>
                </div>

                <!-- TABLE -->
                <div class="dashboard-table">

                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Full Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>Customer Status</th>
                                <th>Account Status</th>
                                <th>Created</th>
                                <th>Action</th>
                            </tr>
                        </thead>

                        <tbody>
                               <c:choose>

                            <c:when test="${empty customerList}">
                                <tr>
                                    <td colspan="7" style="text-align:center; padding:20px;">
                                        Không có tài khoản nào
                                    </td>
                                </tr>
                            </c:when>

                                
                                <c:otherwise>
                            <c:forEach var="u" items="${customerList}">
                                <tr>
                                    <td>${u.customerId}</td>
                                    <td>${u.fullName}</td>
                                    <td>${u.email}</td>
                                    <td>${u.phone}</td>
                                    <td>
                                        <span class="status-badge ${u.status.toLowerCase()}">
                                            ${u.status}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="status-badge ${u.statusAccount.toLowerCase()}">
                                            ${u.statusAccount}
                                        </span>
                                    </td>
                                    <td>${u.createdAt.toLocalDate()}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/staff/users?action=detail&id=${u.customerId}"
                                           class="btn-view">
                                            View
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                                </c:otherwise>
   </c:choose>
                        </tbody>

                    </table>

                </div>

            </div>
        </div>

    </body>
</html>
