<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Voucher Management</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
            <style>
                .voucher-container {
                    max-width: 1000px;
                    margin: 20px auto;
                    padding: 20px;
                }

                .voucher-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 30px;
                }

                .create-btn {
                    background-color: #4CAF50;
                    color: white;
                    padding: 10px 20px;
                    border: none;
                    border-radius: 4px;
                    cursor: pointer;
                    text-decoration: none;
                    display: inline-block;
                }

                .create-btn:hover {
                    background-color: #45a049;
                }

                .voucher-table {
                    width: 100%;
                    border-collapse: collapse;
                    background-color: #f9f9f9;
                }

                .voucher-table th {
                    background-color: #333;
                    color: white;
                    padding: 12px;
                    text-align: left;
                    font-weight: bold;
                }

                .voucher-table td {
                    border: 1px solid #ddd;
                    padding: 12px;
                }

                .voucher-table tbody tr:hover {
                    background-color: #f0f0f0;
                }

                .action-links {
                    display: flex;
                    gap: 10px;
                }

                .detail-link,
                .delete-link {
                    padding: 5px 10px;
                    border-radius: 3px;
                    text-decoration: none;
                    color: white;
                    font-size: 12px;
                }

                .detail-link {
                    background-color: #2196F3;
                }

                .detail-link:hover {
                    background-color: #0b7dda;
                }

                .delete-link {
                    background-color: #f44336;
                }

                .delete-link:hover {
                    background-color: #da190b;
                }

                .empty-message {
                    text-align: center;
                    padding: 40px;
                    color: #666;
                }

                .status-badge {
                    display: inline-block;
                    padding: 5px 10px;
                    border-radius: 3px;
                    font-size: 12px;
                    font-weight: bold;
                }

                .status-badge.active {
                    background-color: #4CAF50;
                    color: white;
                }

                .status-badge.inactive {
                    background-color: #f44336;
                    color: white;
                }
            </style>
        </head>

        <body>
            <jsp:include page="includes/header.jsp" />

            <div class="voucher-container">
                <div class="voucher-header">
                    <h1>Voucher Management</h1>
                    <a href="${pageContext.request.contextPath}/voucher?action=create" class="create-btn">+ Create
                        Voucher</a>
                </div>

                <c:if test="${empty vouchers}">
                    <div class="empty-message">
                        <p>No vouchers found. <a href="${pageContext.request.contextPath}/voucher?action=create">Create
                                one</a></p>
                    </div>
                </c:if>

                <c:if test="${not empty vouchers}">
                    <table class="voucher-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Code</th>
                                <th>Discount (%)</th>
                                <th>Start Date</th>
                                <th>End Date</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="voucher" items="${vouchers}">
                                <tr>
                                    <td>${voucher.voucherId}</td>
                                    <td>${voucher.code}</td>
                                    <td>${voucher.discount}</td>
                                    <td>${voucher.startDate}</td>
                                    <td>${voucher.endDate}</td>
                                    <td>
                                        <span
                                            class="status-badge ${voucher.status == 'ACTIVE' ? 'active' : 'inactive'}">
                                            ${voucher.status}
                                        </span>
                                    </td>
                                    <td>
                                        <div class="action-links">
                                            <a href="${pageContext.request.contextPath}/voucher?action=detail&voucherId=${voucher.voucherId}"
                                                class="detail-link">View</a>
                                            <a href="${pageContext.request.contextPath}/voucher?action=delete&voucherId=${voucher.voucherId}"
                                                class="delete-link"
                                                onclick="return confirm('Are you sure you want to delete this voucher?');">Delete</a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </div>
        </body>

        </html>