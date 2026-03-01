<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Voucher Details</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
            <style>
                .detail-container {
                    max-width: 600px;
                    margin: 20px auto;
                    padding: 20px;
                }

                .form-group {
                    margin-bottom: 20px;
                }

                .form-group label {
                    display: block;
                    margin-bottom: 5px;
                    font-weight: bold;
                    color: #333;
                }

                .form-group input,
                .form-group select,
                .form-group textarea {
                    width: 100%;
                    padding: 10px;
                    border: 1px solid #ddd;
                    border-radius: 4px;
                    box-sizing: border-box;
                    font-size: 14px;
                }

                .form-group input:focus,
                .form-group select:focus,
                .form-group textarea:focus {
                    outline: none;
                    border-color: #2196F3;
                    box-shadow: 0 0 5px rgba(33, 150, 243, 0.3);
                }

                .button-group {
                    display: flex;
                    gap: 10px;
                    margin-top: 30px;
                }

                .btn {
                    flex: 1;
                    padding: 10px 20px;
                    border: none;
                    border-radius: 4px;
                    cursor: pointer;
                    font-size: 14px;
                    font-weight: bold;
                    text-decoration: none;
                    display: inline-block;
                    text-align: center;
                }

                .btn-submit {
                    background-color: #4CAF50;
                    color: white;
                }

                .btn-submit:hover {
                    background-color: #45a049;
                }

                .btn-cancel {
                    background-color: #888;
                    color: white;
                }

                .btn-cancel:hover {
                    background-color: #777;
                }

                .error-message {
                    background-color: #f8d7da;
                    color: #721c24;
                    padding: 12px;
                    border-radius: 4px;
                    margin-bottom: 20px;
                }

                .success-message {
                    background-color: #d4edda;
                    color: #155724;
                    padding: 12px;
                    border-radius: 4px;
                    margin-bottom: 20px;
                }

                h1 {
                    text-align: center;
                    margin-bottom: 30px;
                }

                .read-only {
                    background-color: #f5f5f5;
                }
            </style>
        </head>

        <body>
            <jsp:include page="includes/header.jsp" />

            <div class="detail-container">
                <h1>Voucher Details</h1>

                <c:if test="${not empty error}">
                    <div class="error-message">${error}</div>
                </c:if>

                <c:if test="${not empty message}">
                    <div class="success-message">${message}</div>
                </c:if>

                <form method="POST" action="${pageContext.request.contextPath}/voucher?action=update">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="voucherId" value="${voucher.voucherId}">

                    <div class="form-group">
                        <label for="voucherId">Voucher ID:</label>
                        <input type="text" id="voucherId" name="voucherId" value="${voucher.voucherId}" readonly
                            class="read-only">
                    </div>

                    <div class="form-group">
                        <label for="code">Code:</label>
                        <input type="text" id="code" name="code" value="${voucher.code}" required>
                    </div>

                    <div class="form-group">
                        <label for="discount">Discount (%):</label>
                        <input type="number" id="discount" name="discount" value="${voucher.discount}" step="0.01"
                            required>
                    </div>

                    <div class="form-group">
                        <label for="startDate">Start Date:</label>
                        <input type="date" id="startDate" name="startDate" value="${voucher.startDate}" required>
                    </div>

                    <div class="form-group">
                        <label for="endDate">End Date:</label>
                        <input type="date" id="endDate" name="endDate" value="${voucher.endDate}" required>
                    </div>

                    <div class="form-group">
                        <label for="status">Status:</label>
                        <select id="status" name="status" required>
                            <option value="ACTIVE" ${voucher.status=='ACTIVE' ? 'selected' : '' }>Active</option>
                            <option value="INACTIVE" ${voucher.status=='INACTIVE' ? 'selected' : '' }>Inactive</option>
                        </select>
                    </div>

                    <div class="button-group">
                        <button type="submit" class="btn btn-submit">Update Voucher</button>
                        <a href="${pageContext.request.contextPath}/voucher?action=list" class="btn btn-cancel">Back to
                            List</a>
                    </div>
                </form>
            </div>
        </body>

        </html>