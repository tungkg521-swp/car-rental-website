package Controllers;

import DALs.BookingDAO;
import DALs.CarChangeRequestDAO;
import DALs.CarDAO;
import DALs.ContractDAO;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.BookingModel;
import models.CarChangeRequestModel;
import models.CarModel;
import models.ContractModel;
import models.StaffModel;


@WebServlet("/staff/bookings")
public class StaffBookingController extends HttpServlet {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final CarChangeRequestDAO carChangeRequestDAO = new CarChangeRequestDAO();
    private final CarDAO carDAO = new CarDAO();
    private final ContractDAO contractDAO = new ContractDAO();

   
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

      
        if (action == null || action.equals("list")) {

            List<BookingModel> list = bookingDAO.findAllBookings();

            request.setAttribute("bookingList", list);

            request.getRequestDispatcher("/views/staff-booking.jsp")
                    .forward(request, response);
        } 
        else if ("detail".equals(action)) {

            try {

                int id = Integer.parseInt(request.getParameter("id"));

                BookingModel booking = bookingDAO.findById(id);

                if (booking == null) {
                    System.out.println("lỗi idbooking");
                    response.sendRedirect(
                            request.getContextPath() + "/staff/bookings");

                    return;
                }

                CarChangeRequestModel pendingRequest
                        = carChangeRequestDAO.getPendingByBookingId(id);

                List<CarModel> replacementCars = getAvailableReplacementCars(id);

                request.setAttribute("pendingCarChangeRequest", pendingRequest);
                request.setAttribute("replacementCars", replacementCars);

                request.setAttribute("booking", booking);

                request.getRequestDispatcher("/views/staff-booking-detail.jsp")
                        .forward(request, response);

            } catch (Exception e) {

                e.printStackTrace();

                response.sendRedirect(
                        request.getContextPath() + "/staff/bookings");
            }
        } 
        else {

            response.sendRedirect(
                    request.getContextPath() + "/staff/bookings");
        }
    }

    
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {

            int bookingId = Integer.parseInt(request.getParameter("bookingId"));

            
            HttpSession session = request.getSession();

            StaffModel staff = (StaffModel) session.getAttribute("STAFF");

            if (staff == null) {
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }

            int staffId = staff.getStaffId();

           
            boolean success = false;

            if ("approve".equals(action)) {

                success = approveBooking(bookingId, staffId);
            } else if ("reject".equals(action)) {
                success = rejectBooking(bookingId);

            }
            
            response.sendRedirect(
                    request.getContextPath()
                    + "/staff/bookings?action=detail&id=" + bookingId
                    + "&result=" + (success ? "success" : "fail")
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.sendRedirect(
                    request.getContextPath() + "/staff/bookings");
        }
    }

    private boolean approveBooking(int bookingId, int staffId) {
        BookingModel booking = bookingDAO.getById(bookingId);

        if (booking == null) {
            return false;
        }
        if (!"PENDING_APPROVAL".equalsIgnoreCase(booking.getStatus())) {
            return false;
        }

        if (contractDAO.existsByBookingId(bookingId)) {
            return false;
        }

        CarModel car = carDAO.findById(booking.getCarId());
        if (car == null) {
            return false;
        }
        if ("MAINTENANCE".equalsIgnoreCase(car.getStatus())) {
            return false;
        }

        if (bookingDAO.hasBookingConflictExcludeBooking(
                booking.getCarId(),
                booking.getStartDate(),
                booking.getEndDate(),
                bookingId)) {
            return false;
        }

        boolean updatedStaff = bookingDAO.updateStaffId(bookingId, staffId);
        if (!updatedStaff) {
            return false;
        }

        ContractModel contract = new ContractModel();
        contract.setBookingId(booking.getBookingId());
        contract.setCustomerId(booking.getCustomerId());
        contract.setStaffId(staffId);
        contract.setCarId(booking.getCarId());
        contract.setContractStartDate(booking.getStartDate());
        contract.setContractEndDate(booking.getEndDate());
        contract.setContractStatus("CREATED");
        contract.setDailyPrice(car.getPricePerDay().doubleValue());

        double total = booking.getTotalEstimatedPrice().doubleValue();
        double deposit = booking.getDepositAmount() != null
                ? booking.getDepositAmount().doubleValue()
                : total * 0.3;

        contract.setDepositAmount(deposit);
        contract.setTotalAmount(total);
        contract.setSignedAt(null);
        contract.setNote("Contract created after staff approval.");
        boolean created = contractDAO.createContract(contract);
        if (!created) {
            return false;
        }

        boolean updatedStatus = bookingDAO.updateStatus(bookingId, "CONFIRMED");
        if (!updatedStatus) {
            return false;
        }

        return updatedStaff && created && updatedStatus;
    }

    private boolean rejectBooking(int bookingId) {
        BookingModel booking = bookingDAO.getById(bookingId);

        if (booking == null) {
            return false;
        }

        if (!"PENDING_APPROVAL".equalsIgnoreCase(booking.getStatus())) {
            return false;
        }

        return bookingDAO.updateStatus(bookingId, "REJECTED");
    }

    private List<CarModel> getAvailableReplacementCars(int bookingId) {
        BookingModel booking = bookingDAO.getById(bookingId);
        if (booking == null) {
            return java.util.Collections.emptyList();
        }

        CarModel oldCar = carDAO.findById(booking.getCarId());
        if (oldCar == null) {
            return java.util.Collections.emptyList();
        }

        return carDAO.getAvailableReplacementCars(
                oldCar.getCarId(),
                oldCar.getTypeName(),
                oldCar.getPricePerDay(),
                booking.getStartDate(),
                booking.getEndDate()
        );
    }

}
