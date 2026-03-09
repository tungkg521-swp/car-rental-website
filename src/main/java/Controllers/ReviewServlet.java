package Controllers;

import DALs.ReviewDAO;
import DALs.BookingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import models.CustomerModel;
import models.ReviewModel;

public class ReviewServlet extends HttpServlet {

    ReviewDAO reviewDAO = new ReviewDAO();
    BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int carId = Integer.parseInt(request.getParameter("carId"));

        List<ReviewModel> reviews = reviewDAO.getReviewByCar(carId);

        request.setAttribute("reviews", reviews);
        request.setAttribute("carId", carId);

        request.getRequestDispatcher("views/review.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        CustomerModel customer = (CustomerModel) session.getAttribute("CUSTOMER");

        if (customer == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int customerId = customer.getCustomerId();
        int carId = Integer.parseInt(request.getParameter("carId"));
        int rating = Integer.parseInt(request.getParameter("rating"));
        String comment = request.getParameter("comment");

        int bookingId = bookingDAO.getCompletedBooking(customerId, carId);

        //  Customer chưa thuê xe
        if (bookingId == -1) {

            session.setAttribute("error", "You must rent this car before writing a review.");

            response.sendRedirect("cars?action=detail&carId=" + carId);
            return;
        }

        //  Insert review
        ReviewModel review = new ReviewModel(customerId, carId, bookingId, rating, comment);
        reviewDAO.insertReview(review);

        response.sendRedirect("cars?action=detail&carId=" + carId);
    }
}