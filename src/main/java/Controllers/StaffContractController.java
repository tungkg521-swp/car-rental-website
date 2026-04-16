package Controllers;


import DALs.BookingDAO;
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
import models.CarModel;
import models.ContractModel;
import models.CustomerModel;

import models.ContractModel;

import models.ContractModel;



@WebServlet("/staff/contracts")
public class StaffContractController extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final CarDAO carDAO = new CarDAO();
    private final ContractDAO contractDAO = new ContractDAO();
    private final BookingDAO bookingDAO = new BookingDAO();


    // ================= GET =================
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        // ===== LIST CONTRACT =====
        if (action == null || "list".equals(action)) {

            List<ContractModel> list = contractDAO.findAllContracts();


            request.setAttribute("contractList", list);

            request.getRequestDispatcher("/views/staff-contracts.jsp")
                    .forward(request, response);
        } // ===== CONTRACT DETAIL =====
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

                // ===== LOAD CUSTOMER =====
                CustomerModel customer
                        = customerDAO.findById(contract.getCustomerId());

                // ===== LOAD CAR =====
                CarModel car
                        = carDAO.findById(contract.getCarId());

                // ===== CALCULATE RENTAL DAYS =====
                long days = ChronoUnit.DAYS.between(
                        contract.getContractStartDate().toLocalDate(),
                        contract.getContractEndDate().toLocalDate()
                ) + 1;

                // ===== SEND DATA TO JSP =====
                request.setAttribute("contract", contract);
                request.setAttribute("customer", customer);
                request.setAttribute("car", car);
                request.setAttribute("rentalDays", days);

                request.getRequestDispatcher("/views/staff-contract-detail.jsp")
                        .forward(request, response);

            } catch (Exception e) {

                e.printStackTrace();

                response.sendRedirect(
                        request.getContextPath() + "/staff/contracts");
            }
        } else {

            response.sendRedirect(
                    request.getContextPath() + "/staff/contracts");
        }
    }

    // ================= POST =================
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {

            int contractId
                    = Integer.parseInt(request.getParameter("contractId"));

            boolean success = false;

            if ("activate".equals(action)) {
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

        if (!"ACTIVE".equalsIgnoreCase(status)
                && !"COMPLETED".equalsIgnoreCase(status)
                && !"CANCELLED".equalsIgnoreCase(status)) {
            return false;
        }

        String currentStatus = contract.getContractStatus();

        if ("ACTIVE".equalsIgnoreCase(status)
                && !"CREATED".equalsIgnoreCase(currentStatus)) {
            return false;
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

}

