package Controllers;

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

    private BookingService service = new BookingService();
    private CarChangeRequestService carChangeService = new CarChangeRequestService();

    // ================= GET =================
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        // ===== 1. VIEW LIST =====
        if (action == null || action.equals("list")) {

            List<BookingModel> list = service.findAllBookings();

            request.setAttribute("bookingList", list);

            request.getRequestDispatcher("/views/staff-booking.jsp")
                    .forward(request, response);
        } // ===== 2. VIEW DETAIL =====
        else if ("detail".equals(action)) {

            try {

                int id = Integer.parseInt(request.getParameter("id"));

                BookingModel booking = service.getBookingById(id);

                if (booking == null) {
                    System.out.println("lỗi idbooking");
                    response.sendRedirect(
                            request.getContextPath() + "/staff/bookings");

                    return;
                }

                CarChangeRequestModel pendingRequest
                        = carChangeService.getPendingRequestByBookingId(id);

                List<CarModel> replacementCars
                        = carChangeService.getAvailableReplacementCars(id);

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
                success = service.approveBooking(bookingId, staffId);
            } else if ("reject".equals(action)) {
                success = service.rejectBooking(bookingId);
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
}
