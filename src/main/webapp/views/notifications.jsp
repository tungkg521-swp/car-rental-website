<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link href="${pageContext.request.contextPath}/assets/css/notification.css" rel="stylesheet">

<div class="notification-popup">

    <div class="notification-header">
        Thông báo
    </div>

    <div class="notification-section">
        Mới
    </div>

    <div class="notification-list">   <!-- thêm div này -->

        <c:if test="${empty notifications}">
            <div class="notification-empty">
                Không có thông báo
            </div>
        </c:if>

        <c:forEach var="n" items="${notifications}">
            <c:choose>

                <c:when test="${n.referenceType == 'REVIEW'}">
                    <div class="notification-item"
                         onclick="handleNotificationClick(${n.referenceId})"
                         style="cursor: pointer; background: #f0f8ff;">
                    </c:when>

                    <c:otherwise>
                        <div class="notification-item">
                        </c:otherwise>

                    </c:choose>

                    <div class="notification-icon">🔔</div>

                    <div class="notification-content">
                        <div class="notification-title">${n.title}</div>
                        <div class="notification-text">${n.content}</div>
                        <div class="notification-time">${n.createdAt}</div>
                    </div>

                    <div class="notification-dot"></div>
                </div>
            </c:forEach>

        </div>

    </div>
    <script>
        function handleNotificationClick(contractId) {
            window.location.href = 'review?contractId=' + contractId;
        }
    </script>