<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="models.CustomerModel" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <title>User Detail</title>
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/staff.css">

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/user-detail.css">
    </head>
    <body >

        <div class="staff-layout">

            <%@ include file="sidebar.jsp" %>


            <div class="staff-content">
                <div class="user-detail-page">

                    <div class="page-header">
                        <div>
                            <h1 class="page-title">User Detail</h1>
                            <p class="page-subtitle">View customer profile and account information</p>
                        </div>
                    </div>

                    <div class="user-profile-card">
                        <div class="profile-left">
                            <div class="avatar-wrapper">
                                <img src="${pageContext.request.contextPath}/assets/images/user.png"
                                     alt="User Avatar"
                                     class="profile-avatar">
                            </div>
                        </div>

                        <div class="profile-right">
                            <div class="profile-top">
                                <div>
                                    <h2 class="user-name">${customer.fullName}</h2>
                                    <p class="user-role">Customer Profile</p>
                                </div>

                                <span class="status-badge ${customer.status.toLowerCase()}">
                                    ${customer.status}
                                </span>
                            </div>

                            <div class="info-grid">
                                <div class="info-item">
                                    <span class="info-label">Email</span>
                                    <span class="info-value">${customer.email}</span>
                                </div>

                                <div class="info-item">
                                    <span class="info-label">Phone</span>
                                    <span class="info-value">${customer.phone}</span>
                                </div>

                                <div class="info-item">
                                    <span class="info-label">Date of Birth</span>
                                    <span class="info-value">${customer.dob}</span>
                                </div>

                                <div class="info-item">
                                    <span class="info-label">Address</span>
                                    <span class="info-value">${customer.address}</span>
                                </div>

                                <div class="info-item">
                                    <span class="info-label">Customer ID</span>
                                    <span class="info-value">${customer.customerId}</span>
                                </div>

                                <div class="info-item">
                                    <span class="info-label">Customer Status</span>
                                    <span class="info-value">
                                        <span class="status-badge ${customer.status.toLowerCase()}">
                                            ${customer.status}
                                        </span>
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="account-card">
                        <div class="account-card-header">
                            <div>
                                <h3>Account Information</h3>
                                <p>System account details and status management</p>
                            </div>

                            <c:if test="${sessionScope.ACCOUNT.roleId == 3}">
                                <button class="edit-status-btn" onclick="openModal()">Edit Status</button>
                            </c:if>
                        </div>

                        <c:if test="${param.msg == 'success'}">
                            <div class="alert success">
                                Updated successfully!
                            </div>
                        </c:if>

                        <c:if test="${param.msg == 'error'}">
                            <div class="alert error">
                                Update failed!
                            </div>
                        </c:if>

                        <div class="info-grid">
                            <div class="info-item">
                                <span class="info-label">Account ID</span>
                                <span class="info-value">${customer.accountId}</span>
                            </div>

                            <div class="info-item">
                                <span class="info-label">Account Status</span>
                                <span class="info-value">
                                    <span class="status-badge ${customer.statusAccount.toLowerCase()}">
                                        ${customer.statusAccount}
                                    </span>
                                </span>
                            </div>

                            <div class="info-item">
                                <span class="info-label">Created At</span>
                                <span class="info-value">${customer.createdAt.toLocalDate()}</span>
                            </div>

                            <div class="info-item">
                                <span class="info-label">Linked Customer ID</span>
                                <span class="info-value">${customer.customerId}</span>
                            </div>
                        </div>
                    </div>

                    <div class="page-actions">
                        <a href="${pageContext.request.contextPath}/staff/users" class="btn-back-detail">
                            ← Back to User List
                        </a>
                    </div>

                </div>
            </div>
        </div>



        <div id="updateModal" class="modal-overlay">

            <div class="modal-box">

                <button class="modal-close" onclick="closeModal()">✖</button>


                <form action="${pageContext.request.contextPath}/staff/users?action=edit&idCus=${customer.customerId}&idAcc=${customer.accountId}" method="POST">
                    <div class="form-group">
                        <label>Status</label>
                        <select id="statusSelect" name="status">
                            <option value="ACTIVE"
                                    ${customer.statusAccount == 'ACTIVE' ? 'selected disabled' : ''}>
                                Active
                            </option>

                            <option value="BLOCKED"
                                    ${customer.statusAccount == 'BLOCKED' ? 'selected disabled' : ''}>
                                BLocked
                            </option>
                        </select>
                    </div>

                    <button class="modal-submit" >Update Status</button>
                </form>
            </div>

        </div>       

        <script src="${pageContext.request.contextPath}/assets/js/staff.js"></script>
    </body>
</html>
