<%-- 
    Document   : staff-notification
    Created on : Mar 10, 2026, 12:50:04 AM
    Author     : ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>staff notification</title>
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/staff.css">
    </head>
    <body>

        <div class="staff-layout">
            <%@ include file="sidebar.jsp" %>
            <div class="staff-content">

                <h1 class="dashboard-title">Staff Notifications</h1>
                <!-- TABLE -->
                <div class="dashboard-table">
                    <table>
                        <thead>
                            <tr>   
                                <th>Title</th>
                                <th>Content</th>
                                <th>Created</th>
                            </tr>
                        </thead>

                        <tbody>

                            <c:choose>

                                <c:when test="${empty notifications}">
                                    <tr>
                                        <td colspan="3" style="text-align:center; padding:20px;">
                                            No notifications
                                        </td>
                                    </tr>
                                </c:when>

                                <c:otherwise>
                                    <c:forEach var="n" items="${notifications}">
                                        <tr>
                                            <td>${n.title}</td>
                                            <td>${n.content}</td>
                                            <td>${n.createdAt}</td>
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
