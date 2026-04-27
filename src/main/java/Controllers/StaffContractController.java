package Controllers;

import DALs.BookingDAO;
import DALs.CarChangeRequestDAO;
import DALs.CarCheckDAO;
import DALs.CarDAO;
import java.io.IOException;
import java.util.List;
import DALs.ContractDAO;
import DALs.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import models.BookingModel;
import models.CarChangeRequestModel;
import models.CarCheckModel;
import models.CarModel;
import models.ContractModel;
import models.CustomerModel;
import models.StaffModel;

@WebServlet("/staff/contracts")
public class StaffContractController extends HttpServlet {

    private static final int DEFAULT_ALLOWED_KM_PER_DAY = 400;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final CarDAO carDAO = new CarDAO();
    private final ContractDAO contractDAO = new ContractDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final CarCheckDAO carCheckDAO = new CarCheckDAO();
    private final CarChangeRequestDAO carChangeRequestDAO = new CarChangeRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || "list".equals(action)) {

            List<ContractModel> list = contractDAO.findAllContracts();

            request.setAttribute("contractList", list);

            request.getRequestDispatcher("/views/staff-contracts.jsp")
                    .forward(request, response);
        } else if ("detail".equals(action)) {

            try {

                HttpSession session = request.getSession(false);

                int id = Integer.parseInt(request.getParameter("id"));

                ContractModel contract
                        = contractDAO.getContractById(id);

                if (contract == null) {

                    response.sendRedirect(
                            request.getContextPath() + "/staff/contracts");

                    return;
                }

                CustomerModel customer
                        = customerDAO.findById(contract.getCustomerId());

                CarModel car
                        = carDAO.findById(contract.getCarId());


                boolean bookingScheduleConflict = bookingDAO.hasBookingConflictExcludeBooking(
                        contract.getCarId(),
                        contract.getContractStartTime(),
                        contract.getContractEndTime(),
                        contract.getBookingId()
                );

                boolean maintenanceScheduleConflict = bookingDAO.hasMaintenanceConflict(
                        contract.getCarId(),
                        contract.getContractStartTime(),
                        contract.getContractEndTime()
                );

                boolean scheduleConflict = bookingScheduleConflict || maintenanceScheduleConflict;
                
                boolean maintenanceBlocked = maintenanceScheduleConflict;

                Duration duration = Duration.between(
                        contract.getContractStartTime().toLocalDateTime(),
                        contract.getContractEndTime().toLocalDateTime()
                );

                long totalMinutes = duration.toMinutes();
                long totalDays = totalMinutes / (24 * 60);
                long remainingHours = (totalMinutes % (24 * 60)) / 60;
                long remainingMinutes = totalMinutes % 60;

                String rentalDurationText = "";

                if (totalDays > 0) {
                    rentalDurationText += totalDays + " day" + (totalDays > 1 ? "s" : "");
                }

                if (remainingHours > 0) {
                    if (!rentalDurationText.isEmpty()) {
                        rentalDurationText += " ";
                    }
                    rentalDurationText += remainingHours + " hour" + (remainingHours > 1 ? "s" : "");
                }

                if (remainingMinutes > 0) {
                    if (!rentalDurationText.isEmpty()) {
                        rentalDurationText += " ";
                    }
                    rentalDurationText += remainingMinutes + " minute" + (remainingMinutes > 1 ? "s" : "");
                }

                if (rentalDurationText.isEmpty()) {
                    rentalDurationText = "0 minute";
                }

                CarCheckModel preDeliveryCheck = carCheckDAO.getLatestPreDeliveryCheckByContractAndCarId(
                        contract.getContractId(),
                        contract.getCarId()
                );

                CarCheckModel returnCheck
                        = carCheckDAO.getLatestReturnCheckByContractId(contract.getContractId());

                boolean hasReturnCheck = returnCheck != null;

                Timestamp actualReturnTime = contract.getActualReturnTime();
                String returnTimingStatus = "NOT_RETURNED";
                double extraTimeFee = 0;

                if (actualReturnTime != null) {
                    returnTimingStatus = determineReturnTimingStatus(contract.getContractEndTime(), actualReturnTime);
                    extraTimeFee = calculateExtraTimeFee(contract, actualReturnTime);
                }

                String actualReturnDateValue = "";
                String actualReturnHourValue = "";

                if (actualReturnTime != null) {
                    actualReturnDateValue = actualReturnTime.toLocalDateTime().toLocalDate().toString();
                    actualReturnHourValue = String.format("%02d:00", actualReturnTime.toLocalDateTime().getHour());
                }

                List<String> savedIssueTypes = parseSavedIssueTypes(returnCheck);
                List<String> savedDescriptions = parseSavedDescriptions(returnCheck);
                List<Long> savedAmounts = parseSavedAmounts(returnCheck);

                List<String> extraChargeTypes = parseReturnFeeTypes(returnCheck);
                List<Long> extraChargeAmounts = parseReturnFeeAmounts(returnCheck);

                long extraChargeTotal = 0;
                for (Long amount : extraChargeAmounts) {
                    extraChargeTotal += amount;
                }

                long remainingRentalAmount = Math.round(contract.getTotalAmount() - contract.getDepositAmount());

                double extraKmFee = contract.getExtraKmFee() != null ? contract.getExtraKmFee() : 0;
                double extraKmFeePerKm = getExtraKmFeePerKm(contract.getDailyPrice());
                double lateHourlyFee = getLateHourlyFee(contract.getDailyPrice());
                double finalAmountDue = remainingRentalAmount + extraChargeTotal + extraKmFee + extraTimeFee;

                BookingModel booking = bookingDAO.findById(contract.getBookingId());

                CarChangeRequestModel carChangeRequest = carChangeRequestDAO.getLatestByBookingId(contract.getBookingId());

                boolean canProcessRefund = booking != null
                        && "REFUND_PENDING".equalsIgnoreCase(booking.getStatus());

                CarModel oldCarChangeCar = null;
                CarModel newCarChangeCar = null;

                if (carChangeRequest != null) {
                    oldCarChangeCar = carDAO.findById(carChangeRequest.getOldCarId());

                    if (carChangeRequest.getNewCarId() > 0) {
                        newCarChangeCar = carDAO.findById(carChangeRequest.getNewCarId());
                    }
                }

                boolean hasPendingCarChangeRequest = carChangeRequest != null
                        && "PENDING".equalsIgnoreCase(carChangeRequest.getStatus());

                boolean canRequestCarChange = booking != null
                        && "CONFIRMED".equalsIgnoreCase(booking.getStatus())
                        && "CREATED".equalsIgnoreCase(contract.getContractStatus())
                        && preDeliveryCheck != null
                        && "NOT_OK".equalsIgnoreCase(preDeliveryCheck.getCheckResult())
                        && !hasPendingCarChangeRequest;

                request.setAttribute("contract", contract);
                request.setAttribute("customer", customer);
                request.setAttribute("car", car);
                request.setAttribute("maintenanceBlocked", maintenanceBlocked);
                request.setAttribute("scheduleConflict", scheduleConflict);
                request.setAttribute("hasReturnCheck", hasReturnCheck);
                request.setAttribute("rentalDurationText", rentalDurationText);
                request.setAttribute("preDeliveryCheck", preDeliveryCheck);
                request.setAttribute("returnCheck", returnCheck);
                request.setAttribute("extraChargeTypes", extraChargeTypes);
                request.setAttribute("extraChargeAmounts", extraChargeAmounts);
                request.setAttribute("extraChargeTotal", extraChargeTotal);
                request.setAttribute("remainingRentalAmount", remainingRentalAmount);
                request.setAttribute("finalAmountDue", finalAmountDue);
                request.setAttribute("savedIssueTypes", savedIssueTypes);
                request.setAttribute("savedDescriptions", savedDescriptions);
                request.setAttribute("savedAmounts", savedAmounts);
                request.setAttribute("preCheckOdometer",
                        preDeliveryCheck != null ? preDeliveryCheck.getOdometerKm() : null);
                request.setAttribute("returnCheckOdometer",
                        returnCheck != null ? returnCheck.getOdometerKm() : null);
                request.setAttribute("contractExtraKmFee", extraKmFee);
                request.setAttribute("extraKmFeePerKm", extraKmFeePerKm);
                request.setAttribute("lateHourlyFee", lateHourlyFee);
                request.setAttribute("booking", booking);
                request.setAttribute("carChangeRequest", carChangeRequest);
                request.setAttribute("canProcessRefund", canProcessRefund);
                request.setAttribute("oldCarChangeCar", oldCarChangeCar);
                request.setAttribute("newCarChangeCar", newCarChangeCar);
                request.setAttribute("canRequestCarChange", canRequestCarChange);
                request.setAttribute("hasPendingCarChangeRequest", hasPendingCarChangeRequest);
                request.setAttribute("returnTimingStatus", returnTimingStatus);
                request.setAttribute("extraTimeFee", extraTimeFee);
                request.setAttribute("actualReturnTime", actualReturnTime);
                request.setAttribute("actualReturnTimeValue",
                        actualReturnTime != null
                                ? actualReturnTime.toLocalDateTime().toString().substring(0, 16)
                                : "");
                request.setAttribute("actualReturnDateValue", actualReturnDateValue);
                request.setAttribute("actualReturnHourValue", actualReturnHourValue);

                if (session != null) {
                    String message = (String) session.getAttribute("message");
                    if (message != null) {
                        request.setAttribute("message", message);
                        session.removeAttribute("message");
                    }

                    String error = (String) session.getAttribute("error");
                    if (error != null) {
                        request.setAttribute("error", error);
                        session.removeAttribute("error");
                    }
                }

                request.getRequestDispatcher("/views/staff-contract-detail.jsp")
                        .forward(request, response);

            } catch (Exception e) {

                e.printStackTrace();

                response.sendRedirect(
                        request.getContextPath() + "/staff/contracts");
            }
        } else if ("checkForm".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + id);
                return;
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/staff/contracts");
                return;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {

            int contractId
                    = Integer.parseInt(request.getParameter("contractId"));

            boolean success = false;

            if ("saveCheck".equals(action)) {
                success = saveCarCheck(request);
            } else if ("activate".equals(action)) {
                success = updateContractStatus(contractId, "ACTIVE");
            } else if ("sendToCustomer".equals(action)) {
                success = sendToCustomerConfirm(contractId);
            } else if ("markNoShow".equals(action)) {
                success = markCustomerNoShow(contractId);
            } else if ("complete".equals(action)) {
                String carNextStatus = request.getParameter("carNextStatus");

                if (!hasReturnCheck(contractId)) {
                    request.getSession().setAttribute("error",
                            "Vui lòng kiểm tra xe khi trả trước khi hoàn tất hợp đồng.");
                    response.sendRedirect(request.getContextPath()
                            + "/staff/contracts?action=detail&id=" + contractId);
                    return;
                }
                success = completeContract(contractId, carNextStatus);
            } else if ("cancel".equals(action)) {
                success = updateContractStatus(contractId, "CANCELLED");
            } else if ("deliverCar".equals(action)) {
                success = deliverCar(contractId);

            } else if ("saveReturnCheck".equals(action)) {
                saveReturnCheck(request, response);
                return;
            }

            if ("saveCheck".equals(action)) {
                if (success) {
                    request.getSession().setAttribute("message", "Lưu kiểm tra xe trước khi giao thành công.");
                } else {
                    request.getSession().setAttribute("error", "Lưu kiểm tra xe trước khi giao thất bại.");
                }

            } else if ("sendToCustomer".equals(action)) {
                if (success) {
                    request.getSession().setAttribute("message", "Đã gửi thông tin kiểm tra xe cho khách hàng xác nhận.");
                } else {
                    request.getSession().setAttribute("error", "Không thể gửi thông tin kiểm tra xe cho khách hàng. Vui lòng kiểm tra lại kết quả kiểm tra xe.");
                }

            } else if ("markNoShow".equals(action)) {
                if (success) {
                    request.getSession().setAttribute("message", "Đã ghi nhận khách hàng không đến nhận xe.");
                } else {
                    request.getSession().setAttribute("error", "Ghi nhận khách hàng không đến nhận xe thất bại.");
                }

            } else if ("complete".equals(action)) {
                if (success) {
                    request.getSession().setAttribute("message", "Hoàn tất trả xe thành công.");
                } else {
                    request.getSession().setAttribute("error", "Hoàn tất trả xe thất bại. Vui lòng kiểm tra lại thông tin hậu kiểm.");
                }

            } else if ("cancel".equals(action)) {
                if (success) {
                    request.getSession().setAttribute("message", "Hủy hợp đồng thành công.");
                } else {
                    request.getSession().setAttribute("error", "Hủy hợp đồng thất bại.");
                }

            } else if ("deliverCar".equals(action)) {
                if (success) {
                    request.getSession().setAttribute("message", "Giao xe thành công.");
                } else {
                    request.getSession().setAttribute("error", "Giao xe thất bại. Khách hàng có thể chưa xác nhận nhận xe.");
                }

            } else {
                if (success) {
                    request.getSession().setAttribute("message", "Xử lý thành công.");
                } else {
                    request.getSession().setAttribute("error", "Xử lý thất bại. Vui lòng thử lại.");
                }
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/staff/contracts?action=detail&id=" + contractId);

        } catch (Exception e) {

            e.printStackTrace();

            response.sendRedirect(
                    request.getContextPath() + "/staff/contracts");
        }
    }

    private boolean updateContractStatus(int contractId, String status) {
        ContractModel contract = contractDAO.getContractById(contractId);

        if (contract == null) {
            return false;
        }

        String currentStatus = contract.getContractStatus();

        if (!"ACTIVE".equalsIgnoreCase(status)
                && !"WAITING_CUSTOMER_CONFIRM".equalsIgnoreCase(status)
                && !"COMPLETED".equalsIgnoreCase(status)
                && !"CANCELLED".equalsIgnoreCase(status)) {
            return false;
        }

        if ("ACTIVE".equalsIgnoreCase(status)) {
            if (!"WAITING_CUSTOMER_CONFIRM".equalsIgnoreCase(currentStatus)) {
                return false;
            }
        }

        if ("COMPLETED".equalsIgnoreCase(status)
                && !"ACTIVE".equalsIgnoreCase(currentStatus)) {
            return false;
        }

        if ("CANCELLED".equalsIgnoreCase(status)
                && !("CREATED".equalsIgnoreCase(currentStatus)
                || "WAITING_CUSTOMER_CONFIRM".equalsIgnoreCase(currentStatus)
                || "ACTIVE".equalsIgnoreCase(currentStatus))) {
            return false;
        }

        boolean updated = contractDAO.updateContractStatus(contractId, status);
        if (!updated) {
            return false;
        }

        if ("WAITING_CUSTOMER_CONFIRM".equalsIgnoreCase(status)) {

        } else if ("ACTIVE".equalsIgnoreCase(status)) {
            carDAO.updateStatus(contract.getCarId(), "RENTING");
            bookingDAO.updateStatus(contract.getBookingId(), "ACTIVE");

        } else if ("COMPLETED".equalsIgnoreCase(status)) {
            carDAO.updateStatus(contract.getCarId(), "AVAILABLE");
            bookingDAO.updateStatus(contract.getBookingId(), "COMPLETED");

        } else if ("CANCELLED".equalsIgnoreCase(status)) {
            carDAO.updateStatus(contract.getCarId(), "AVAILABLE");
            bookingDAO.updateStatus(contract.getBookingId(), "CANCELLED");
        }

        return true;
    }

    private boolean completeContract(int contractId, String carNextStatus) {
        ContractModel contract = contractDAO.getContractById(contractId);

        if (contract == null) {
            return false;
        }

        if (!"ACTIVE".equalsIgnoreCase(contract.getContractStatus())) {
            return false;
        }

        CarCheckModel returnCheck = carCheckDAO.getLatestReturnCheckByContractId(contractId);
        if (returnCheck == null || returnCheck.getOdometerKm() == null) {
            return false;
        }

        boolean mileageUpdated = updateContractMileageSummary(contractId);
        if (!mileageUpdated) {
            return false;
        }

        contract = contractDAO.getContractById(contractId);
        if (contract == null) {
            return false;
        }

        double extraKmFee = contract.getExtraKmFee() != null
                ? contract.getExtraKmFee()
                : 0;

        double lateFee = calculateExtraTimeFee(contract, contract.getActualReturnTime());

        long returnIssueFeeTotal = calculateReturnIssueFeeTotal(returnCheck);

        double extraFeeTotal = extraKmFee + lateFee + returnIssueFeeTotal;

        double finalAmount = contract.getTotalAmount() + extraFeeTotal;

        boolean settlementUpdated = contractDAO.updateFinalSettlement(
                contractId,
                extraFeeTotal,
                finalAmount
        );

        if (!settlementUpdated) {
            return false;
        }

        boolean carKmUpdated = carDAO.updateCurrentOdometerKm(
                contract.getCarId(),
                returnCheck.getOdometerKm()
        );

        if (!carKmUpdated) {
            return false;
        }

        boolean updated = contractDAO.updateContractStatus(contractId, "COMPLETED");
        if (!updated) {
            return false;
        }

        bookingDAO.updateStatus(contract.getBookingId(), "COMPLETED");

        if ("MAINTENANCE".equalsIgnoreCase(carNextStatus)) {
            carDAO.updateStatus(contract.getCarId(), "MAINTENANCE");
        } else {
            carDAO.updateStatus(contract.getCarId(), "AVAILABLE");
        }

        return true;
    }

    private long calculateReturnIssueFeeTotal(CarCheckModel returnCheck) {
        List<Long> feeAmounts = parseReturnFeeAmounts(returnCheck);

        long total = 0;
        for (Long amount : feeAmounts) {
            if (amount != null) {
                total += amount;
            }
        }

        return total;
    }

    private boolean saveCarCheck(HttpServletRequest request) {
        try {
            int contractId = Integer.parseInt(request.getParameter("contractId"));

            ContractModel contract = contractDAO.getContractById(contractId);
            if (contract == null) {
                return false;
            }

            if (!"CREATED".equalsIgnoreCase(contract.getContractStatus())) {
                return false;
            }

            CarChangeRequestModel latestRequest = carChangeRequestDAO.getLatestByBookingId(contract.getBookingId());
            if (latestRequest != null && "PENDING".equalsIgnoreCase(latestRequest.getStatus())) {
                return false;
            }

            HttpSession session = request.getSession(false);
            if (session == null) {
                return false;
            }

            StaffModel staff = (StaffModel) session.getAttribute("STAFF");
            if (staff == null) {
                return false;
            }

            CarModel car = carDAO.findById(contract.getCarId());
            if (car == null) {
                return false;
            }

            int startOdometerKm = car.getCurrentOdometerKm();

            String physicalStatus = request.getParameter("physicalStatus");
            if (physicalStatus == null
                    || (!"OK".equalsIgnoreCase(physicalStatus)
                    && !"NOT_OK".equalsIgnoreCase(physicalStatus))) {
                return false;
            }

            String fuelLevel = request.getParameter("fuelLevel");
            String[] exteriorIssues = request.getParameterValues("exteriorIssues");
            String[] interiorIssues = request.getParameterValues("interiorIssues");
            String userNote = request.getParameter("note");

            String exteriorNote = joinIssueArray(exteriorIssues);
            String interiorNote = joinIssueArray(interiorIssues);

            

            boolean bookingScheduleConflict = bookingDAO.hasBookingConflictExcludeBooking(
                    contract.getCarId(),
                    contract.getContractStartTime(),
                    contract.getContractEndTime(),
                    contract.getBookingId()
            );

            boolean maintenanceScheduleConflict = bookingDAO.hasMaintenanceConflict(
                    contract.getCarId(),
                    contract.getContractStartTime(),
                    contract.getContractEndTime()
            );

            boolean scheduleConflict = bookingScheduleConflict || maintenanceScheduleConflict;

            String finalResult = "OK";

            if ("NOT_OK".equalsIgnoreCase(physicalStatus)
                    || scheduleConflict) {
                finalResult = "NOT_OK";
            }

            StringBuilder finalNote = new StringBuilder();

            if (userNote != null && !userNote.trim().isEmpty()) {
                finalNote.append(userNote.trim());
            }

            if (scheduleConflict) {
                if (finalNote.length() > 0) {
                    finalNote.append(" | ");
                }
                finalNote.append("System detected: car has schedule conflict in this rental period.");
            }

            CarCheckModel check = new CarCheckModel();
            check.setContractId(contract.getContractId());
            check.setCarId(contract.getCarId());
            check.setCheckedBy(staff.getStaffId());
            check.setFuelLevel(fuelLevel);
            check.setExteriorNote(exteriorNote);
            check.setInteriorNote(interiorNote);
            check.setCheckResult(finalResult);
            check.setNote(finalNote.toString());
            check.setOdometerKm(startOdometerKm);

            return carCheckDAO.addCheck(check);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void saveReturnCheck(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            int contractId = Integer.parseInt(request.getParameter("contractId"));

            ContractModel contract = contractDAO.getContractById(contractId);
            if (contract == null) {
                request.getSession().setAttribute("error", "Không tìm thấy hợp đồng.");
                response.sendRedirect(request.getContextPath() + "/staff/contracts");
                return;
            }

            if (!"ACTIVE".equalsIgnoreCase(contract.getContractStatus())) {
                request.getSession().setAttribute("error", "Chỉ hợp đồng đang thuê mới có thể lưu kiểm tra trả xe.");
                response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + contractId);
                return;
            }

            HttpSession session = request.getSession(false);
            if (session == null) {
                response.sendRedirect(request.getContextPath() + "/staff/contracts");
                return;
            }

            StaffModel staff = (StaffModel) session.getAttribute("STAFF");
            if (staff == null) {
                request.getSession().setAttribute("error", "Staff session not found.");
                response.sendRedirect(request.getContextPath() + "/staff/contracts");
                return;
            }

            String[] issueTypes = request.getParameterValues("issueTypes");
            String noIssuesFound = request.getParameter("noIssuesFound");

            String odometerKmRaw = request.getParameter("odometerKm");
            if (odometerKmRaw == null || odometerKmRaw.trim().isEmpty()) {
                request.getSession().setAttribute("error", "Vui lòng nhập số km khi trả xe.");
                response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + contractId);
                return;
            }

            int returnOdometerKm = Integer.parseInt(odometerKmRaw.trim());
            if (returnOdometerKm < 0) {
                request.getSession().setAttribute("error", "Số km khi trả xe phải lớn hơn hoặc bằng 0.");
                response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + contractId);
                return;
            }

            Timestamp actualReturnTime = parseDateTimeFromRequest(request, "actualReturnDate", "actualReturnHour");
            if (actualReturnTime == null) {
                request.getSession().setAttribute("error", "Vui lòng chọn ngày và giờ trả xe thực tế hợp lệ.");
                response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + contractId);
                return;
            }

            CarCheckModel preDeliveryCheck = carCheckDAO.getLatestPreDeliveryCheckByContractAndCarId(
                    contractId,
                    contract.getCarId()
            );
            if (preDeliveryCheck == null || preDeliveryCheck.getOdometerKm() == null) {
                request.getSession().setAttribute("error", "Không tìm thấy số km trước khi giao xe.");
                response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + contractId);
                return;
            }

            int startOdometerKm = preDeliveryCheck.getOdometerKm();
            if (returnOdometerKm < startOdometerKm) {
                request.getSession().setAttribute("error", "Số km khi trả xe không được nhỏ hơn số km trước khi giao.");
                response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + contractId);
                return;
            }

            boolean isNormalReturn = "true".equalsIgnoreCase(noIssuesFound);

            if (issueTypes == null) {
                issueTypes = new String[0];
            }

            if (isNormalReturn) {
                issueTypes = new String[0];
            }

            if (issueTypes.length == 0) {
                isNormalReturn = true;
            }

            StringBuilder exteriorNoteBuilder = new StringBuilder();
            StringBuilder noteBuilder = new StringBuilder();

            if (isNormalReturn) {
                noteBuilder.append("No issues found");
            } else {
                for (String issue : issueTypes) {
                    String safeSuffix = buildSafeFieldSuffix(issue);

                    String description = request.getParameter("description_" + safeSuffix);
                    String amountRaw = request.getParameter("amount_" + safeSuffix);

                    if (amountRaw == null || amountRaw.trim().isEmpty()) {
                        request.getSession().setAttribute("error", "Vui lòng nhập số tiền cho lỗi: " + issue);
                        response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + contractId);
                        return;
                    }

                    long amount = Long.parseLong(amountRaw.trim());
                    if (amount <= 0) {
                        request.getSession().setAttribute("error", "Số tiền phải lớn hơn 0 cho lỗi: " + issue);
                        response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + contractId);
                        return;
                    }

                    if (exteriorNoteBuilder.length() > 0) {
                        exteriorNoteBuilder.append(" | ");
                    }
                    exteriorNoteBuilder.append(issue);

                    if (noteBuilder.length() > 0) {
                        noteBuilder.append(" | ");
                    }
                    noteBuilder.append(buildDisplayFeeLine(issue, description, amount));
                }
            }

            CarCheckModel check = new CarCheckModel();
            check.setContractId(contract.getContractId());
            check.setCarId(contract.getCarId());
            check.setCheckedBy(staff.getStaffId());
            check.setFuelLevel("FULL");
            check.setExteriorNote(exteriorNoteBuilder.toString());
            check.setInteriorNote("");
            check.setCheckResult("RETURN_CHECK");
            check.setNote(noteBuilder.toString());
            check.setOdometerKm(returnOdometerKm);

            boolean saved = carCheckDAO.addCheck(check);

            if (saved) {
                boolean returnTimeUpdated = contractDAO.updateActualReturnTime(contractId, actualReturnTime);
                if (!returnTimeUpdated) {
                    request.getSession().setAttribute("error", "Đã lưu kiểm tra trả xe nhưng cập nhật thời gian trả thực tế thất bại.");
                    response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + contractId);
                    return;
                }

                boolean mileageUpdated = updateContractMileageSummary(contractId);

                if (mileageUpdated) {
                    request.getSession().setAttribute("message", "Lưu kiểm tra trả xe thành công.");
                } else {
                    request.getSession().setAttribute("error", "Đã lưu kiểm tra trả xe nhưng cập nhật tổng số km thất bại.");
                }
            } else {
                request.getSession().setAttribute("error", "Lưu kiểm tra trả xe thất bại.");
            }

            response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + contractId);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Dữ liệu số không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/staff/contracts");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Lưu kiểm tra trả xe thất bại.");
            response.sendRedirect(request.getContextPath() + "/staff/contracts");
        }
    }

    private boolean deliverCar(int contractId) {
        ContractModel contract = contractDAO.getContractById(contractId);

        if (contract == null) {
            return false;
        }

        if (!"WAITING_CUSTOMER_CONFIRM".equalsIgnoreCase(contract.getContractStatus())) {
            return false;
        }

        if (contract.getCustomerConfirmed() == null || !contract.getCustomerConfirmed()) {
            return false;
        }

        return updateContractStatus(contractId, "ACTIVE");
    }

    private String buildSafeFieldSuffix(String issue) {
        return issue.replace(" ", "_");
    }

    private String buildDisplayFeeLine(String issue, String description, long amount) {
        StringBuilder line = new StringBuilder();
        line.append(issue);

        if (description != null && !description.trim().isEmpty()) {
            line.append(": ").append(description.trim());
        }

        line.append(" - ").append(amount).append(" VND");
        return line.toString();
    }

    private boolean hasReturnCheck(int contractId) {
        CarCheckModel returnCheck = carCheckDAO.getLatestReturnCheckByContractId(contractId);
        return returnCheck != null;
    }

    private List<String> parseReturnFeeTypes(CarCheckModel latestCarCheck) {
        List<String> feeTypes = new ArrayList<>();

        if (latestCarCheck == null) {
            return feeTypes;
        }

        if (!"RETURN_CHECK".equalsIgnoreCase(latestCarCheck.getCheckResult())) {
            return feeTypes;
        }

        String note = latestCarCheck.getNote();
        if (note == null || note.trim().isEmpty()) {
            return feeTypes;
        }

        String[] parts = note.split("\\|");

        for (String rawPart : parts) {
            String part = rawPart.trim();
            if (part.isEmpty()) {
                continue;
            }

            int lastDashIndex = part.lastIndexOf(" - ");
            if (lastDashIndex == -1) {
                continue;
            }

            String leftPart = part.substring(0, lastDashIndex).trim();

            int colonIndex = leftPart.indexOf(":");
            String type;
            if (colonIndex != -1) {
                type = leftPart.substring(0, colonIndex).trim();
            } else {
                type = leftPart.trim();
            }

            feeTypes.add(type);
        }

        return feeTypes;
    }

    private List<Long> parseReturnFeeAmounts(CarCheckModel latestCarCheck) {
        List<Long> feeAmounts = new ArrayList<>();

        if (latestCarCheck == null) {
            return feeAmounts;
        }

        if (!"RETURN_CHECK".equalsIgnoreCase(latestCarCheck.getCheckResult())) {
            return feeAmounts;
        }

        String note = latestCarCheck.getNote();
        if (note == null || note.trim().isEmpty()) {
            return feeAmounts;
        }

        String[] parts = note.split("\\|");

        for (String rawPart : parts) {
            String part = rawPart.trim();
            if (part.isEmpty()) {
                continue;
            }

            try {
                int lastDashIndex = part.lastIndexOf(" - ");
                if (lastDashIndex == -1) {
                    continue;
                }

                String rightPart = part.substring(lastDashIndex + 3).trim();
                rightPart = rightPart.replace("VND", "").trim().replace(",", "");

                long amount = Long.parseLong(rightPart);
                feeAmounts.add(amount);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return feeAmounts;
    }

    private List<String> parseSavedIssueTypes(CarCheckModel latestCarCheck) {
        List<String> issueTypes = new ArrayList<>();

        if (latestCarCheck == null) {
            return issueTypes;
        }

        if (!"RETURN_CHECK".equalsIgnoreCase(latestCarCheck.getCheckResult())) {
            return issueTypes;
        }

        String exteriorNote = latestCarCheck.getExteriorNote();
        if (exteriorNote == null || exteriorNote.trim().isEmpty()) {
            return issueTypes;
        }

        String[] parts = exteriorNote.split("\\|");
        for (String rawPart : parts) {
            String part = rawPart.trim();
            if (!part.isEmpty()) {
                issueTypes.add(part);
            }
        }

        return issueTypes;
    }

    private List<String> parseSavedDescriptions(CarCheckModel latestCarCheck) {
        List<String> descriptions = new ArrayList<>();

        if (latestCarCheck == null) {
            return descriptions;
        }

        if (!"RETURN_CHECK".equalsIgnoreCase(latestCarCheck.getCheckResult())) {
            return descriptions;
        }

        String note = latestCarCheck.getNote();
        if (note == null || note.trim().isEmpty()) {
            return descriptions;
        }

        String[] parts = note.split("\\|");

        for (String rawPart : parts) {
            String part = rawPart.trim();
            if (part.isEmpty()) {
                descriptions.add("");
                continue;
            }

            int lastDashIndex = part.lastIndexOf(" - ");
            if (lastDashIndex == -1) {
                descriptions.add("");
                continue;
            }

            String leftPart = part.substring(0, lastDashIndex).trim();

            int colonIndex = leftPart.indexOf(":");
            if (colonIndex != -1) {
                descriptions.add(leftPart.substring(colonIndex + 1).trim());
            } else {
                descriptions.add("");
            }
        }

        return descriptions;
    }

    private List<Long> parseSavedAmounts(CarCheckModel latestCarCheck) {
        List<Long> amounts = new ArrayList<>();

        if (latestCarCheck == null) {
            return amounts;
        }

        if (!"RETURN_CHECK".equalsIgnoreCase(latestCarCheck.getCheckResult())) {
            return amounts;
        }

        String note = latestCarCheck.getNote();
        if (note == null || note.trim().isEmpty()) {
            return amounts;
        }

        String[] parts = note.split("\\|");

        for (String rawPart : parts) {
            String part = rawPart.trim();
            if (part.isEmpty()) {
                amounts.add(0L);
                continue;
            }

            try {
                int lastDashIndex = part.lastIndexOf(" - ");
                if (lastDashIndex == -1) {
                    amounts.add(0L);
                    continue;
                }

                String rightPart = part.substring(lastDashIndex + 3).trim();
                rightPart = rightPart.replace("VND", "").trim().replace(",", "");

                amounts.add(Long.parseLong(rightPart));
            } catch (Exception e) {
                amounts.add(0L);
            }
        }

        return amounts;
    }

    private boolean sendToCustomerConfirm(int contractId) {
        ContractModel contract = contractDAO.getContractById(contractId);

        if (contract == null) {
            return false;
        }

        if (!"CREATED".equalsIgnoreCase(contract.getContractStatus())) {
            return false;
        }

        CarChangeRequestModel latestRequest = carChangeRequestDAO.getLatestByBookingId(contract.getBookingId());
        if (latestRequest != null && "PENDING".equalsIgnoreCase(latestRequest.getStatus())) {
            return false;
        }

        CarCheckModel latestCarCheck = carCheckDAO.getLatestPreDeliveryCheckByContractAndCarId(
                contractId,
                contract.getCarId()
        );
        if (latestCarCheck == null) {
            return false;
        }

        if (!"OK".equalsIgnoreCase(latestCarCheck.getCheckResult())) {
            return false;
        }

        return contractDAO.updateContractForCustomerConfirm(
                contractId,
                "WAITING_CUSTOMER_CONFIRM",
                latestCarCheck.getCheckId()
        );
    }

    private boolean markCustomerNoShow(int contractId) {
        ContractModel contract = contractDAO.getContractById(contractId);

        if (contract == null) {
            return false;
        }

        if (!"WAITING_CUSTOMER_CONFIRM".equalsIgnoreCase(contract.getContractStatus())) {
            return false;
        }

        boolean updated = contractDAO.updateContractStatus(contractId, "CANCELLED");
        if (!updated) {
            return false;
        }

        bookingDAO.updateStatus(contract.getBookingId(), "CANCELLED");
        carDAO.updateStatus(contract.getCarId(), "AVAILABLE");

        return true;
    }

    private String joinIssueArray(String[] issues) {
        if (issues == null || issues.length == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (String issue : issues) {
            if (issue == null || issue.trim().isEmpty()) {
                continue;
            }

            if (builder.length() > 0) {
                builder.append(" | ");
            }

            builder.append(issue.trim());
        }

        return builder.toString();
    }

    private int calculateAllowedKm(ContractModel contract) {
        long minutes = Duration.between(
                contract.getContractStartTime().toLocalDateTime(),
                contract.getContractEndTime().toLocalDateTime()
        ).toMinutes();

        long rentalDays = (long) Math.ceil(minutes / 1440.0);
        if (rentalDays <= 0) {
            rentalDays = 1;
        }

        return (int) rentalDays * DEFAULT_ALLOWED_KM_PER_DAY;
    }

    private boolean updateContractMileageSummary(int contractId) {
        ContractModel contract = contractDAO.getContractById(contractId);
        if (contract == null) {
            return false;
        }

        CarCheckModel preDeliveryCheck = carCheckDAO.getLatestPreDeliveryCheckByContractAndCarId(
                contractId,
                contract.getCarId()
        );
        CarCheckModel returnCheck = carCheckDAO.getLatestReturnCheckByContractId(contractId);

        if (preDeliveryCheck == null || preDeliveryCheck.getOdometerKm() == null) {
            return false;
        }

        if (returnCheck == null || returnCheck.getOdometerKm() == null) {
            return false;
        }

        int startKm = preDeliveryCheck.getOdometerKm();
        int endKm = returnCheck.getOdometerKm();

        if (endKm < startKm) {
            return false;
        }

        int actualKm = endKm - startKm;
        int allowedKm = calculateAllowedKm(contract);
        int extraKm = Math.max(actualKm - allowedKm, 0);

        double feePerKm = getExtraKmFeePerKm(contract.getDailyPrice());
        double extraKmFee = extraKm * feePerKm;

        return contractDAO.updateMileageSummary(
                contractId,
                allowedKm,
                actualKm,
                extraKm,
                extraKmFee
        );
    }

    private String determineReturnTimingStatus(Timestamp expectedReturnTime, Timestamp actualReturnTime) {
        if (expectedReturnTime == null || actualReturnTime == null) {
            return "UNKNOWN";
        }

        if (actualReturnTime.before(expectedReturnTime)) {
            return "EARLY";
        } else if (actualReturnTime.after(expectedReturnTime)) {
            return "LATE";
        } else {
            return "ON_TIME";
        }
    }

    private double calculateExtraTimeFee(ContractModel contract, Timestamp actualReturnTime) {
        if (contract == null || actualReturnTime == null || contract.getContractEndTime() == null) {
            return 0;
        }

        Timestamp expectedReturnTime = contract.getContractEndTime();

        if (!actualReturnTime.after(expectedReturnTime)) {
            return 0;
        }

        long overdueMinutes = Duration.between(
                expectedReturnTime.toLocalDateTime(),
                actualReturnTime.toLocalDateTime()
        ).toMinutes();

        if (overdueMinutes <= 0) {
            return 0;
        }

        double dailyPrice = contract.getDailyPrice();
        if (dailyPrice < 0) {
            dailyPrice = 0;
        }

        double lateHourlyFee = getLateHourlyFee(dailyPrice);
        double overdueHours = overdueMinutes / 60.0;

        if (overdueHours < 4) {
            return lateHourlyFee * overdueHours;
        } else if (overdueHours < 8) {
            return dailyPrice / 2.0;
        } else {
            return dailyPrice;
        }
    }

    private double getExtraKmFeePerKm(double dailyPrice) {
        if (dailyPrice < 700000) {
            return 3000;
        } else if (dailyPrice < 1200000) {
            return 5000;
        } else {
            return 7000;
        }
    }

    private double getLateHourlyFee(double dailyPrice) {
        if (dailyPrice < 700000) {
            return 80000;
        } else if (dailyPrice < 1200000) {
            return 100000;
        } else {
            return 120000;
        }
    }

    private Timestamp parseDateTimeFromRequest(HttpServletRequest request, String dateParam, String timeParam) {
        String dateValue = request.getParameter(dateParam);
        String timeValue = request.getParameter(timeParam);

        if (dateValue == null || dateValue.trim().isEmpty()
                || timeValue == null || timeValue.trim().isEmpty()) {
            return null;
        }

        try {
            String normalized = dateValue.trim() + " " + timeValue.trim() + ":00";
            return Timestamp.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
