package Controllers;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;

import DALs.CarDAO;
import DALs.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.CarModel;
import models.ContractModel;
import models.CustomerModel;
import service.ContractService;

@WebServlet("/staff/contracts")
public class StaffContractController extends HttpServlet {

    private final ContractService contractService = new ContractService();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final CarDAO carDAO = new CarDAO();

    // ================= GET =================
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        // ===== LIST CONTRACT =====
        if (action == null || "list".equals(action)) {

            List<ContractModel> list = contractService.findAllContracts();

            request.setAttribute("contractList", list);

            request.getRequestDispatcher("/views/staff-contracts.jsp")
                    .forward(request, response);
        }

        // ===== CONTRACT DETAIL =====
        else if ("detail".equals(action)) {

            try {

                int id = Integer.parseInt(request.getParameter("id"));

                ContractModel contract =
                        contractService.getContractById(id);

                if (contract == null) {

                    response.sendRedirect(
                            request.getContextPath() + "/staff/contracts");

                    return;
                }

                // ===== LOAD CUSTOMER =====
                CustomerModel customer =
                        customerDAO.findById(contract.getCustomerId());

                // ===== LOAD CAR =====
                CarModel car =
                        carDAO.findById(contract.getCarId());

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
        }

        else {

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

            int contractId =
                    Integer.parseInt(request.getParameter("contractId"));

            if ("activate".equals(action)) {

                contractService.updateContractStatus(contractId, "ACTIVE");

            } else if ("complete".equals(action)) {

                contractService.updateContractStatus(contractId, "COMPLETED");

            } else if ("cancel".equals(action)) {

                contractService.updateContractStatus(contractId, "CANCELLED");
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
}