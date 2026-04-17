package Controllers;

import DALs.BookingDAO;
import DALs.CarCheckDAO;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import DALs.CarDAO;
import DALs.ContractDAO;
import DALs.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.CarCheckModel;
import models.CarModel;
import models.ContractModel;
import models.CustomerModel;
import models.StaffModel;

@WebServlet("/staff/contracts")
public class StaffContractController extends HttpServlet {

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
        } 
        else if ("detail".equals(action)) {

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

              
                long days = ChronoUnit.DAYS.between(
                        contract.getContractStartDate().toLocalDate(),
                        contract.getContractEndDate().toLocalDate()
                ) + 1;

                CarCheckModel latestCarCheck = carCheckDAO.getLatestCheckByContractId(contract.getContractId());

                request.setAttribute("contract", contract);
                request.setAttribute("customer", customer);
                request.setAttribute("car", car);
                request.setAttribute("rentalDays", days);
                request.setAttribute("latestCarCheck", latestCarCheck);

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
                        contract.getContractStartDate(),
                        contract.getContractEndDate(),
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
            } else if ("complete".equals(action)) {
                String carNextStatus = request.getParameter("carNextStatus");
                success = completeContract(contractId, carNextStatus);
            } else if ("cancel".equals(action)) {
                success = updateContractStatus(contractId, "CANCELLED");
            }

            if (success) {
                request.getSession().setAttribute("message", "Contract updated successfully.");
            } else {
                request.getSession().setAttribute("error", "Failed to update contract status.");
            }

            if ("saveCheck".equals(action)) {
                response.sendRedirect(
                        request.getContextPath()
                        + "/staff/contracts?action=checkForm&id=" + contractId);
            } else {
                response.sendRedirect(
                        request.getContextPath()
                        + "/staff/contracts?action=detail&id=" + contractId);
            }

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

        if (!"ACTIVE".equalsIgnoreCase(status)
                && !"COMPLETED".equalsIgnoreCase(status)
                && !"CANCELLED".equalsIgnoreCase(status)) {
            return false;
        }

        String currentStatus = contract.getContractStatus();

        if ("ACTIVE".equalsIgnoreCase(status)) {
            if (!"CREATED".equalsIgnoreCase(currentStatus)) {
                return false;
            }

            
            if (!carCheckDAO.hasLatestCheckOk(contractId)) {
                return false;
            }
        }

        if ("COMPLETED".equalsIgnoreCase(status)
                && !"ACTIVE".equalsIgnoreCase(currentStatus)) {
            return false;
        }

        if ("CANCELLED".equalsIgnoreCase(status)
                && !("CREATED".equalsIgnoreCase(currentStatus)
                || "ACTIVE".equalsIgnoreCase(currentStatus))) {
            return false;
        }

        boolean updated = contractDAO.updateContractStatus(contractId, status);
        if (!updated) {
            return false;
        }

        if ("ACTIVE".equalsIgnoreCase(status)) {
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

            String physicalStatus = request.getParameter("physicalStatus");
            if (physicalStatus == null
                    || (!"OK".equalsIgnoreCase(physicalStatus)
                    && !"NOT_OK".equalsIgnoreCase(physicalStatus))) {
                return false;
            }

            String fuelLevel = request.getParameter("fuelLevel");
            String exteriorNote = request.getParameter("exteriorNote");
            String interiorNote = request.getParameter("interiorNote");
            String userNote = request.getParameter("note");

            boolean maintenanceBlocked = "MAINTENANCE".equalsIgnoreCase(car.getStatus());

            boolean scheduleConflict = bookingDAO.hasBookingConflictExcludeBooking(
                    contract.getCarId(),
                    contract.getContractStartDate(),
                    contract.getContractEndDate(),
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

            return carCheckDAO.addCheck(check);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
