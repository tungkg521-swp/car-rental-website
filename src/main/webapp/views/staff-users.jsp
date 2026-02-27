
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Manage Users</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/staff.css">
    </head>
    <body>

        <div class="staff-layout">

            <%@ include file="sidebar.jsp" %>

            <div class="staff-content">

                <h1 class="dashboard-title">Manage Users</h1>

                <!-- FILTER -->
                <div class="car-filter">
                    <input type="text" placeholder="Search by full name...">
                    <select>
                        <option>All Status</option>
                        <option>ACTIVE</option>
                        <option>BLOCKED</option>
                    </select>
                    <button>Search</button>
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
                                <th>Status</th>
                                <th>Created</th>
                                <th>Action</th>
                            </tr>
                        </thead>

                        <tbody>

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
                                    <td>${u.createdAt}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/staff/users?action=detail&id=${u.customerId}"
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
