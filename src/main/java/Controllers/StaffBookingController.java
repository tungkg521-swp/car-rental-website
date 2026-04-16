package Controllers;

import DALs.BookingDAO;
import DALs.CarChangeRequestDAO;
import DALs.CarDAO;

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
import models.StaffModel;
import service.BookingService;
import service.CarChangeRequestService;

@WebServlet("/staff/bookings")
public class StaffBookingController extends HttpServlet {


    private final BookingDAO bookingDAO = new BookingDAO();
    private final CarChangeRequestDAO carChangeRequestDAO = new CarChangeRequestDAO();
    private final CarDAO carDAO = new CarDAO();

    // ================= GET =================
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        // ===== 1. VIEW LIST =====
        if (action == null || action.equals("list")) {


            List<BookingModel> list = bookingDAO.findAllBookings();


            request.setAttribute("bookingList", list);

            request.getRequestDispatcher("/views/staff-booking.jsp")
                    .forward(request, response);
        } // ===== 2. VIEW DETAIL =====
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
        } // ===== DEFAULT =====
        else {

            response.sendRedirect(
                    request.getContextPath() + "/staff/bookings");
        }
    }

    // ================= POST =================
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {

            int bookingId = Integer.parseInt(request.getParameter("bookingId"));

            // ===== lấy staffId từ session =====
            HttpSession session = request.getSession();

            StaffModel staff = (StaffModel) session.getAttribute("STAFF");

            if (staff == null) {
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }

            int staffId = staff.getStaffId();

            // ===== APPROVE BOOKING =====
            boolean success = false;

            if ("approve".equals(action)) {

                success = approveBooking(bookingId, staffId);
            } else if ("reject".equals(action)) {
                success = rejectBooking(bookingId);

            }
            // quay lại detail
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

        boolean updatedStaff = bookingDAO.updateStaffId(bookingId, staffId);
        boolean updatedDeadline = bookingDAO.updatePaymentDeadline(bookingId, 24);
        boolean updatedStatus = bookingDAO.updateStatus(bookingId, "AWAITING_PAYMENT");

        return updatedStaff && updatedDeadline && updatedStatus;
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
