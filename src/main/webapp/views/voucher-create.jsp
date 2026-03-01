<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Create New Voucher</title>
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

                h1 {
                    text-align: center;
                    margin-bottom: 30px;
                }
            </style>
        </head>

        <body>
            <jsp:include page="includes/header.jsp" />

            <div class="detail-container">
                <h1>Create New Voucher</h1>

                <c:if test="${not empty error}">
                    <div class="error-message">${error}</div>
                </c:if>

                <form method="POST" action="${pageContext.request.contextPath}/voucher?action=create">
                    <input type="hidden" name="action" value="create">

                    <div class="form-group">
                        <label for="code">Code:</label>
                        <input type="text" id="code" name="code" placeholder="e.g., SAVE20" required>
                    </div>

                    <div class="form-group">
                        <label for="discount">Discount (%):</label>
                        <input type="number" id="discount" name="discount" placeholder="e.g., 20" step="0.01" min="0"
                            required>
                    </div>

                    <div class="form-group">
                        <label for="startDate">Start Date:</label>
                        <input type="date" id="startDate" name="startDate" required>
                    </div>

                    <div class="form-group">
                        <label for="endDate">End Date:</label>
                        <input type="date" id="endDate" name="endDate" required>
                    </div>

                    <div class="form-group">
                        <label for="status">Status:</label>
                        <select id="status" name="status" required>
                            <option value="ACTIVE">Active</option>
                            <option value="INACTIVE">Inactive</option>
                        </select>
                    </div>

                    <div class="button-group">
                        <button type="submit" class="btn btn-submit">Create Voucher</button>
                        <a href="${pageContext.request.contextPath}/voucher?action=list" class="btn btn-cancel">Back to
                            List</a>
                    </div>
                </form>
            </div>
        </body>

        </html>