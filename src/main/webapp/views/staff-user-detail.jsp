<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="models.CustomerModel" %>

<%
    CustomerModel customer = (CustomerModel) request.getAttribute("customer");
%>

<!DOCTYPE html>
<html>
<head>
    <title>User Detail</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/staff.css">
</head>
<body class="detail-page">

<div class="detail-container">

    <div class="detail-card">

        <!-- LEFT PROFILE ICON -->
        <div class="detail-image">
            <img src="${pageContext.request.contextPath}/assets/images/user.png"
                 alt="User Avatar">
        </div>

        <!-- RIGHT INFO -->
        <div class="detail-info">

            <h1><%= customer.getFullName() %></h1>

            <span class="status-badge 
                <%= customer.getStatus().toLowerCase() %>">
                <%= customer.getStatus() %>
            </span>

            <div class="specs">
                <div><strong>Email:</strong> <%= customer.getEmail() %></div>
                <div><strong>Phone:</strong> <%= customer.getPhone() %></div>
                <div><strong>Date of Birth:</strong> <%= customer.getDob() %></div>
                <div><strong>Address:</strong> <%= customer.getAddress() %></div>
            </div>

        </div>
    </div>

    <!-- SYSTEM INFO -->
    <div class="detail-description">
        <h2>Account Information</h2>
        <p><strong>Customer ID:</strong> <%= customer.getCustomerId() %></p>
        <p><strong>Account ID:</strong> <%= customer.getAccountId() %></p>
        <p><strong>Created At:</strong> <%= customer.getCreatedAt() %></p>
    </div>

    <div class="detail-actions">
        <a href="${pageContext.request.contextPath}/staff/users"
           class="btn-back">Back</a>

        <a href="${pageContext.request.contextPath}/staff/users?action=edit&id=<%= customer.getCustomerId() %>"
           class="btn-edit">Edit</a>
    </div>

</div>

</body>
</html>
