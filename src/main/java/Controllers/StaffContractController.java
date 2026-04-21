package Controllers;

import DALs.BookingDAO;
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
import java.time.Duration;
import java.util.ArrayList;
import models.CarCheckModel;
import models.CarModel;
import models.ContractModel;
import models.CustomerModel;
import models.StaffModel;

@WebServlet("/staff/contracts")
public class StaffContractController extends HttpServlet {

    private static final int DEFAULT_ALLOWED_KM_PER_DAY = 100;
    private static final double DEFAULT_EXTRA_KM_FEE_PER_KM = 3000;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final CarDAO carDAO = new CarDAO();
    private final ContractDAO contractDAO = new ContractDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final CarCheckDAO carCheckDAO = new CarCheckDAO();

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

                boolean maintenanceBlocked = "MAINTENANCE".equalsIgnoreCase(car.getStatus());

                boolean scheduleConflict = bookingDAO.hasBookingConflictExcludeBooking(
                        contract.getCarId(),
                        contract.getContractStartTime(),
                        contract.getContractEndTime(),
                        contract.getBookingId()
                );

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

                CarCheckModel preDeliveryCheck
                        = carCheckDAO.getLatestPreDeliveryCheckByContractId(contract.getContractId());

                CarCheckModel returnCheck
                        = carCheckDAO.getLatestReturnCheckByContractId(contract.getContractId());

                boolean hasReturnCheck = returnCheck != null;

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
                double finalAmountDue = remainingRentalAmount + extraChargeTotal + extraKmFee;

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

                ContractModel contract = contractDAO.getContractById(id);

                if (contract == null) {
                    response.sendRedirect(request.getContextPath() + "/staff/contracts");
                    return;
                }

                if (!"CREATED".equalsIgnoreCase(contract.getContractStatus())) {
                    request.getSession().setAttribute("error", "Only contracts with CREATED status can be checked.");
                    response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + id);
                    return;
                }

                CustomerModel customer = customerDAO.findById(contract.getCustomerId());
                CarModel car = carDAO.findById(contract.getCarId());

                if (car == null) {
                    request.getSession().setAttribute("error", "Car not found.");
                    response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + id);
                    return;
                }

                boolean maintenanceBlocked = "MAINTENANCE".equalsIgnoreCase(car.getStatus());

                boolean scheduleConflict = bookingDAO.hasBookingConflictExcludeBooking(
                        contract.getCarId(),
                        contract.getContractStartTime(),
                        contract.getContractEndTime(),
                        contract.getBookingId()
                );

                request.setAttribute("maintenanceBlocked", maintenanceBlocked);
                request.setAttribute("scheduleConflict", scheduleConflict);

                CarCheckModel latestCarCheck
                        = carCheckDAO.getLatestCheckByContractId(contract.getContractId());

                List<CarCheckModel> carCheckList
                        = carCheckDAO.getChecksByContractId(contract.getContractId());

                request.setAttribute("contract", contract);
                request.setAttribute("customer", customer);
                request.setAttribute("car", car);
                request.setAttribute("latestCarCheck", latestCarCheck);
                request.setAttribute("carCheckList", carCheckList);

                request.getRequestDispatcher("/views/staff-car-check.jsp")
                        .forward(request, response);

            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/staff/contracts");
            }
        } else {

            response.sendRedirect(
                    request.getContextPath() + "/staff/contracts");
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
                            "Please inspect the vehicle first before completing the return.");
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

            if (success) {
                request.getSession().setAttribute("message", "Contract updated successfully.");
            } else {
                request.getSession().setAttribute("error", "Failed to update contract status.");
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
            bookingDAO.updateStatus(contract.getBookingId(), "WAITING_CUSTOMER_CONFIRM");

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

        boolean carKmUpdated = carDAO.updateCurrentOdometerKm(contract.getCarId(), returnCheck.getOdometerKm());
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

            boolean maintenanceBlocked = "MAINTENANCE".equalsIgnoreCase(car.getStatus());

            boolean scheduleConflict = bookingDAO.hasBookingConflictExcludeBooking(
                    contract.getCarId(),
                    contract.getContractStartTime(),
                    contract.getContractEndTime(),
                    contract.getBookingId()
            );

            String finalResult = "OK";

            if ("NOT_OK".equalsIgnoreCase(physicalStatus)
                    || maintenanceBlocked
                    || scheduleConflict) {
                finalResult = "NOT_OK";
            }

            StringBuilder finalNote = new StringBuilder();

            if (userNote != null && !userNote.trim().isEmpty()) {
                finalNote.append(userNote.trim());
            }

            if (maintenanceBlocked) {
                if (finalNote.length() > 0) {
                    finalNote.append(" | ");
                }
                finalNote.append("System detected: car is currently under maintenance.");
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
                request.getSession().setAttribute("error", "Contract not found.");
                response.sendRedirect(request.getContextPath() + "/staff/contracts");
                return;
            }

            if (!"ACTIVE".equalsIgnoreCase(contract.getContractStatus())) {
                request.getSession().setAttribute("error", "Only ACTIVE contracts can save return check.");
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
                request.getSession().setAttribute("error", "Please enter return odometer.");
                response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + contractId);
                return;
            }

            int returnOdometerKm = Integer.parseInt(odometerKmRaw.trim());
            if (returnOdometerKm < 0) {
                request.getSession().setAttribute("error", "Return odometer must be greater than or equal to 0.");
                response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + contractId);
                return;
            }

            CarCheckModel preDeliveryCheck = carCheckDAO.getLatestPreDeliveryCheckByContractId(contractId);
            if (preDeliveryCheck == null || preDeliveryCheck.getOdometerKm() == null) {
                request.getSession().setAttribute("error", "Pre-delivery odometer not found.");
                response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + contractId);
                return;
            }

            int startOdometerKm = preDeliveryCheck.getOdometerKm();
            if (returnOdometerKm < startOdometerKm) {
                request.getSession().setAttribute("error", "Return odometer cannot be smaller than pre-check odometer.");
                response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + contractId);
                return;
            }

            boolean isNormalReturn = "true".equalsIgnoreCase(noIssuesFound);

            if (issueTypes == null) {
                issueTypes = new String[0];
            }

            // Nếu tick No issues found thì luôn lưu theo nhánh normal return
            if (isNormalReturn) {
                issueTypes = new String[0];
            }

            // Nếu không có issue nào được chọn thì cũng hiểu là normal return
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
                        request.getSession().setAttribute("error", "Please enter amount for issue: " + issue);
                        response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + contractId);
                        return;
                    }

                    long amount = Long.parseLong(amountRaw.trim());
                    if (amount <= 0) {
                        request.getSession().setAttribute("error", "Amount must be greater than 0 for issue: " + issue);
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
                boolean mileageUpdated = updateContractMileageSummary(contractId);

                if (mileageUpdated) {
                    request.getSession().setAttribute("message", "Return check saved successfully.");
                } else {
                    request.getSession().setAttribute("error", "Return check saved but failed to update mileage summary.");
                }
            } else {
                request.getSession().setAttribute("error", "Failed to save return check.");
            }

            response.sendRedirect(request.getContextPath() + "/staff/contracts?action=detail&id=" + contractId);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Invalid number format.");
            response.sendRedirect(request.getContextPath() + "/staff/contracts");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Failed to save return check.");
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

        CarCheckModel latestCarCheck = carCheckDAO.getLatestPreDeliveryCheckByContractId(contractId);
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

        CarCheckModel preDeliveryCheck = carCheckDAO.getLatestPreDeliveryCheckByContractId(contractId);
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
        double extraKmFee = extraKm * DEFAULT_EXTRA_KM_FEE_PER_KM;

        return contractDAO.updateMileageSummary(
                contractId,
                allowedKm,
                actualKm,
                extraKm,
                extraKmFee
        );
    }

}
