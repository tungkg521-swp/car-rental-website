<%-- 
    Document   : access-denied
    Created on : Mar 18, 2026, 2:38:34 PM
    Author     : HP
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Access Denied</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css">
</head>
<body>

<div class="overlay">
    <div class="login-card">
        <h2>Access Denied</h2>

        <p style="text-align:center; color:#dc3545; margin-top:15px;">
            ${message}
        </p>

        <div style="margin-top: 20px; text-align:center;">
            <a href="${pageContext.request.contextPath}/home"
               style="color:#0d6efd; text-decoration:none; font-weight:600;">
                Quay về trang chủ
            </a>
        </div>
    </div>
</div>

</body>
</html>