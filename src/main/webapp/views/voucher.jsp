<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="isAdmin" value="${sessionScope.ACCOUNT != null and sessionScope.ACCOUNT.roleId == 3}" />

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Voucher Management</title>
        <link rel="stylesheet" 
              href="${pageContext.request.contextPath}/assets/css/staff.css">
        <link rel="stylesheet" 
              href="${pageContext.request.contextPath}/assets/css/voucher.css">
    </head>
    <body>
        <div class="staff-layout">
            <jsp:include page="/views/sidebar.jsp" />

            <div class="staff-content">
                <div class="container">
                    <h1>Voucher Management</h1>

                    <!-- Messages -->
                    <c:if test="${not empty error}">
                        <div class="error-message">${error}</div>
                    </c:if>
                    <c:if test="${not empty sessionScope.message}">
                        <div class="success-message">${sessionScope.message}</div>
                        <c:remove var="message" scope="session"/>
                    </c:if>

                    <!-- ================= LIST VIEW ================= -->
                    <c:if test="${param.action == null || param.action == 'list'}">
                        <!-- Nút Create chỉ hiện cho admin -->
                        <c:if test="${isAdmin}">
                            <div style="margin-bottom: 20px;">
                                <a href="${pageContext.request.contextPath}/staff/vouchers?action=create"
                                   class="btn btn-success">+ Create Voucher</a>
                            </div>
                        </c:if>

                        <c:if test="${empty vouchers}">
                            <div class="empty-state">
                                <p>No vouchers found.</p>
                                <c:if test="${isAdmin}">
                                    <a href="${pageContext.request.contextPath}/staff/vouchers?action=create"
                                       class="btn btn-primary">Create your first voucher</a>
                                </c:if>
                            </div>
                        </c:if>

                        <c:if test="${not empty vouchers}">
                            <table class="dashboard-table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Code</th>
                                        <th>Discount</th>
                                        <th>Type</th>
                                        <th>Expire Date</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="v" items="${vouchers}">
                                        <tr>
                                            <td>${v.voucherId}</td>
                                            <td><strong>${v.code}</strong></td>
                                            <td>
                                                <fmt:formatNumber value="${v.discount}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                                                ${v.type == 'PERCENT' ? '%' : '$'}
                                            </td>
                                            <td>
                                                <span class="status-badge" style="background-color: #e2e3e5; color: #383d41;">
                                                    ${v.type}
                                                </span>
                                            </td>
                                            <td><fmt:formatDate value="${v.expireDate}" pattern="dd/MM/yyyy"/></td>
                                            <td>
                                                <span class="status-badge ${v.status ? 'active' : 'inactive'}">
                                                    ${v.status ? 'Active' : 'Inactive'}
                                                </span>
                                            </td>
                                            <td class="action-buttons">
                                                <a href="${pageContext.request.contextPath}/staff/vouchers?action=detail&voucherId=${v.voucherId}"
                                                   class="btn-view">View</a>

                                                <c:if test="${isAdmin}">
                                                    <form method="POST" action="${pageContext.request.contextPath}/staff/vouchers" 
                                                          style="display: inline;" 
                                                          onsubmit="return confirm('Are you sure you want to delete this voucher?')">
                                                        <input type="hidden" name="action" value="delete">
                                                        <input type="hidden" name="voucherId" value="${v.voucherId}">
                                                        <button type="submit" class="btn-delete">Delete</button>
                                                    </form>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:if>
                    </c:if>

                    <!-- ================= CREATE VIEW ================= -->
                    <c:if test="${param.action == 'create'}">
                        <h2 style="margin-bottom: 20px;">Create New Voucher</h2>

                        <form method="POST" action="${pageContext.request.contextPath}/staff/vouchers?action=create">
                            <div class="form-group">
                                <label>Code *</label>
                                <input type="text" name="code" required 
                                       placeholder="Enter voucher code (e.g., SUMMER2024)">
                            </div>

                            <div class="form-row">
                                <div class="form-group">
                                    <label>Discount Type *</label>
                                    <select name="type" required>
                                        <option value="PERCENT">Percentage (%)</option>
                                        <option value="AMOUNT">Fixed Amount ($)</option>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label>Discount Value *</label>
                                    <input type="number" name="discount" step="0.01" required
                                           placeholder="Enter discount value" min="0.01">
                                </div>
                            </div>

                            <div class="form-group">
                                <label>Expire Date *</label>
                                <input type="date" name="expireDate" required 
                                       min="${java.time.LocalDate.now()}">
                            </div>

                            <div class="form-row">
                                <div class="form-group">
                                    <label>Max Uses *</label>
                                    <input type="number" name="maxUses" required min="1"
                                           placeholder="Enter maximum number of uses">
                                </div>

                                <div class="form-group">
                                    <label>Min Booking Amount ($)</label>
                                    <input type="number" name="minBookingAmount" step="0.01" min="0"
                                           placeholder="Enter minimum booking amount" value="0">
                                </div>
                            </div>

                            <div class="form-group">
                                <label>Status</label>
                                <select name="status">
                                    <option value="ACTIVE">Active</option>
                                    <option value="INACTIVE">Inactive</option>
                                </select>
                            </div>

                            <div class="form-actions">
                                <button type="submit" class="btn btn-success">Create Voucher</button>
                                <a href="${pageContext.request.contextPath}/staff/vouchers?action=list"
                                   class="btn btn-secondary">Cancel</a>
                            </div>
                        </form>
                    </c:if>

                    <!-- ================= DETAIL/UPDATE VIEW ================= -->
                    <c:if test="${param.action == 'detail' && not empty voucher}">
                        <h2 style="margin-bottom: 20px;">Voucher Details</h2>

                        <!-- Hiển thị thông tin voucher (cả staff và admin đều thấy) -->
                        <div class="voucher-detail-info">
                            <p><strong>Voucher ID:</strong> ${voucher.voucherId}</p>
                            <p><strong>Code:</strong> ${voucher.code}</p>
                            <p><strong>Discount:</strong> 
                                <fmt:formatNumber value="${voucher.discount}" type="number" minFractionDigits="2"/> 
                                ${voucher.type == 'PERCENT' ? '%' : '$'}
                            </p>
                            <p><strong>Type:</strong> ${voucher.type}</p>
                            <p><strong>Expire Date:</strong> <fmt:formatDate value="${voucher.expireDate}" pattern="dd/MM/yyyy"/></p>
                            <p><strong>Max Uses:</strong> ${voucher.maxUses}</p>
                            <p><strong>Used Count:</strong> ${voucher.usedCount}</p>
                            <p><strong>Min Booking Amount:</strong> $<fmt:formatNumber value="${voucher.minBookingAmount}" type="number" minFractionDigits="2"/></p>
                            <p><strong>Created Date:</strong> <fmt:formatDate value="${voucher.createdDate}" pattern="dd/MM/yyyy HH:mm"/></p>
                            <p><strong>Status:</strong> 
                                <span class="status-badge ${voucher.status ? 'active' : 'inactive'}">
                                    ${voucher.status ? 'Active' : 'Inactive'}
                                </span>
                            </p>
                        </div>

                        <!-- Chỉ admin mới thấy form update -->
                        <c:if test="${isAdmin}">
                            <hr/>
                            <h3>Edit Voucher</h3>
                            <form method="POST" action="${pageContext.request.contextPath}/staff/vouchers?action=update">
                                <input type="hidden" name="voucherId" value="${voucher.voucherId}">
                                <div class="form-group">
                                    <label>Code *</label>
                                    <input type="text" name="code" value="${voucher.code}" required>
                                </div>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label>Discount Type *</label>
                                        <select name="type" required>
                                            <option value="PERCENT" ${voucher.type == 'PERCENT' ? 'selected' : ''}>Percentage (%)</option>
                                            <option value="AMOUNT" ${voucher.type == 'AMOUNT' ? 'selected' : ''}>Fixed Amount ($)</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label>Discount Value *</label>
                                        <input type="number" name="discount" value="${voucher.discount}" step="0.01" required>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label>Expire Date *</label>
                                    <input type="date" name="expireDate" value="${voucher.expireDate}" required>
                                </div>
                                <div class="form-group">
                                    <label>Status</label>
                                    <select name="status">
                                        <option value="ACTIVE" ${voucher.status ? 'selected' : ''}>Active</option>
                                        <option value="INACTIVE" ${not voucher.status ? 'selected' : ''}>Inactive</option>
                                    </select>
                                </div>
                                <div class="form-actions">
                                    <button type="submit" class="btn btn-success">Update Voucher</button>
                                    <a href="${pageContext.request.contextPath}/staff/vouchers?action=list"
                                       class="btn btn-secondary">Back to List</a>
                                </div>
                            </form>
                        </c:if>

                        <c:if test="${not isAdmin}">
                            <div class="form-actions">
                                <a href="${pageContext.request.contextPath}/staff/vouchers?action=list"
                                   class="btn btn-secondary">Back to List</a>
                            </div>
                        </c:if>
                    </c:if>
                </div>
            </div>
        </div>
    </body>
</html>