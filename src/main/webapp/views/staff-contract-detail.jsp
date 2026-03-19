<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>



<!DOCTYPE html>
<html>
    <head>
        <title>Contract Detail</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/style.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/contract-detail.css?v=1">

    </head>

    <body>

        <div class="contract-page">

            <div class="contract-container">

                <!-- HEADER -->
                <div class="contract-header">

                    <div>
                        <h1>Contract #${contract.contractId}</h1>

                        <p style="color:red">
                            DEBUG STATUS: [${contract.contractStatus}]
                        </p>

                        <span class="contract-status ${contract.contractStatus}">
                            ${contract.contractStatus}
                        </span>
                    </div>

                    <div class="contract-meta">
                        <p><b>Booking ID:</b> #${contract.bookingId}</p>
                        <p><b>Created At:</b> ${contract.createdAt}</p>
                        <p><b>Start Date:</b> ${contract.contractStartDate}</p>
                        <p><b>End Date:</b> ${contract.contractEndDate}</p>
                    </div>

                </div>

                <hr>

                <!-- CUSTOMER -->
                <div class="contract-section">

                    <h2>👤 Customer Information</h2>

                    <div class="contract-grid">

                        <div>
                            <span class="label">Name</span>
                            <p>${customer.fullName}</p>
                        </div>

                        <div>
                            <span class="label">Email</span>
                            <p>${customer.email}</p>
                        </div>

                        <div>
                            <span class="label">Phone</span>
                            <p>${customer.phone}</p>
                        </div>

                    </div>

                </div>

                <hr>

                <!-- CAR -->
                <div class="contract-section">

                    <h2>🚗 Car Information</h2>

                    <div class="contract-grid">

                        <div>
                            <span class="label">Model</span>
                            <p>${car.modelName}</p>
                        </div>

                        <div>
                            <span class="label">Daily Price</span>
                            <fmt:formatNumber value="${contract.dailyPrice}" type="number"/> VND
                        </div>

                    </div>

                </div>

                <hr>

                <!-- RENTAL -->
                <div class="contract-section">

                    <h2>📅 Rental Information</h2>

                    <div class="contract-grid">

                        <div>
                            <span class="label">Start Date</span>
                            <p>${contract.contractStartDate}</p>
                        </div>

                        <div>
                            <span class="label">End Date</span>
                            <p>${contract.contractEndDate}</p>
                        </div>

                        <div>
                            <span class="label">Rental Days</span>
                            <p>${rentalDays} days</p>
                        </div>

                    </div>

                </div>

                <hr>

                <!-- PAYMENT -->
                <div class="contract-section">

                    <h2>📄 Payment Summary</h2>

                    <div class="payment-grid">

                        <div>
                            <span class="label">Daily Price</span>
                            <fmt:formatNumber value="${contract.dailyPrice}" type="number"/> VND
                        </div>

                        <div>
                            <span class="label">Deposit (30%)</span>
                            <fmt:formatNumber value="${contract.depositAmount}" type="number"/> VND
                        </div>

                        <div class="total">
                            <span class="label">Total Amount</span>
                            <fmt:formatNumber value="${contract.totalAmount}" type="number"/> VND
                        </div>

                    </div>

                </div>

                <hr>

                <!-- NOTE -->
                <div class="contract-section">

                    <h2>📝 Note</h2>

                    <p class="note">
                        ${contract.note}
                    </p>

                </div>


                <!-- ACTION -->
                <div class="contract-actions">

                    <a class="btn btn-back"
                       href="${pageContext.request.contextPath}/staff/contracts">
                        Back
                    </a>

                    <c:choose>

                        
                        <c:when test="${contract.contractStatus eq 'CREATED'}">

                            <form method="post"
                                  action="${pageContext.request.contextPath}/staff/contracts"
                                  class="action-form">

                                <input type="hidden"
                                       name="contractId"
                                       value="${contract.contractId}"/>

                                <button class="btn btn-success"
                                        type="submit"
                                        name="action"
                                        value="activate">
                                    Activate
                                </button>

                                <button class="btn btn-danger"
                                        type="submit"
                                        name="action"
                                        value="cancel"
                                        onclick="return confirm('Cancel this contract?')">
                                    Cancel
                                </button>

                            </form>

                        </c:when>


                        
                        <c:when test="${contract.contractStatus eq 'ACTIVE'}">

                            <form method="post"
                                  action="${pageContext.request.contextPath}/staff/contracts"
                                  class="action-form">

                                <input type="hidden"
                                       name="contractId"
                                       value="${contract.contractId}"/>
                                
                                <input type="hidden"
                                       name="bookingId"
                                       value="${contract.bookingId}"/>

                                <button class="btn btn-primary"
                                        type="submit"
                                        name="action"
                                        value="complete"
                                        onclick="return confirm('Confirm return car?')">
                                    Return Car
                                </button>

                            </form>

                        </c:when>


                        <c:otherwise>

                            <span class="contract-closed">
                                Contract Closed
                            </span>

                        </c:otherwise>

                    </c:choose>

                </div>

            </div>

        </div>

    </body>
</html>