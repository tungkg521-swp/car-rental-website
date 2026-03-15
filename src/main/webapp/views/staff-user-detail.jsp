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
              href="${pageContext.request.contextPath}/assets/css/profile.css">
    </head>
    <body >

        <div class="staff-layout">

            <%@ include file="sidebar.jsp" %>

            <div class="staff-content">
                <div class="detail-container">

                    <div class="detail-card">

                        <!-- LEFT PROFILE ICON -->
                        <div class="detail-image">
                            <img src="${pageContext.request.contextPath}/assets/images/user.png"
                                 alt="User Avatar">
                        </div>

                        <!-- RIGHT INFO -->
                        <div class="detail-info">

                            <h1>${customer.fullName}</h1>




                            <div class="specs">
                                <div><strong>Customer status:</strong> <span class="status-badge ${customer.status.toLowerCase()}">
                                        ${customer.status}                               
                                    </span></div>
                                <div><strong>Email:</strong> ${customer.email}</div>
                                <div><strong>Phone:</strong> ${customer.phone}</div>
                                <div><strong>Date of Birth:</strong> ${customer.dob}</div>
                                <div><strong>Address:</strong> ${customer.address}</div>
                            </div>

                        </div>
                    </div>

                    <!-- SYSTEM INFO -->
                    <div class="detail-description">
                        <c:if test="${param.msg == 'success'}">
                            <div class="alert success">
                                Cập nhật thành công!
                            </div>
                        </c:if>

                        <c:if test="${param.msg == 'error'}">
                            <div class="alert error">
                                Cập nhật thất bại!
                            </div>
                        </c:if>
                        <h2>Account Information</h2>
                        <p><strong>Customer ID:</strong> ${customer.customerId}</p>
                        <p><strong>Account status:</strong> <span class="status-badge ${customer.statusAccount.toLowerCase()}">
                                ${customer.statusAccount}                               
                                <button class="edit-main-btn" onclick="openModal()">Edit</button> </span></p>

                        <p><strong>Account ID:</strong> ${customer.accountId}</p>
                        <p><strong>Created At:</strong> ${customer.createdAt.toLocalDate()}</p>
                    </div>

                    <div class="detail-actions">
                        <a href="${pageContext.request.contextPath}/staff/users"
                           class="btn-back">Back</a>


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

                            <option value="LOCKED"
                                    ${customer.statusAccount == 'LOCKED' ? 'selected disabled' : ''}>
                                Locked
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
