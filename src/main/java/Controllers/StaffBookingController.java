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
import service.BookingService;

@WebServlet("/staff/bookings")
public class StaffBookingController extends HttpServlet {

    private BookingService service = new BookingService();

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
        }

        // ===== 2. VIEW DETAIL =====
        else if ("detail".equals(action)) {

            try {

                int id = Integer.parseInt(request.getParameter("id"));

                BookingModel booking = service.getBookingById(id);

                if (booking == null) {

                    response.sendRedirect(
                            request.getContextPath() + "/staff/bookings");

                    return;
                }

                request.setAttribute("booking", booking);

                request.getRequestDispatcher("/views/staff-booking-detail.jsp")
                        .forward(request, response);

            } catch (Exception e) {

                e.printStackTrace();

                response.sendRedirect(
                        request.getContextPath() + "/staff/bookings");
            }
        }

        // ===== DEFAULT =====
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

            Integer staffId = (Integer) session.getAttribute("staffId");

            // nếu chưa có login staff thì tạm thời gán = 1
            if (staffId == null) {
                staffId = 1;
            }

            // ===== APPROVE BOOKING =====
            if ("approve".equals(action)) {

                service.approveBooking(bookingId, staffId);

            }

            // ===== REJECT BOOKING =====
            else if ("reject".equals(action)) {

                service.rejectBooking(bookingId);

            }

            // quay lại detail
            response.sendRedirect(
                    request.getContextPath()
                    + "/staff/bookings?action=detail&id=" + bookingId
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.sendRedirect(
                    request.getContextPath() + "/staff/bookings");
        }
    }
}