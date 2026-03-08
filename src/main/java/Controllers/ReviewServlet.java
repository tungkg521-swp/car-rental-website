package Controllers;

import DALs.ReviewDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import models.CustomerModel;
import models.ReviewModel;

public class ReviewServlet extends HttpServlet {

    ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int carId = Integer.parseInt(request.getParameter("carId"));

        List<ReviewModel> reviews = reviewDAO.getReviewByCar(carId);

        request.setAttribute("reviews", reviews);

        request.getRequestDispatcher("views/review.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        CustomerModel customer = (CustomerModel) session.getAttribute("CUSTOMER");

        int customerId = customer.getCustomerId();

        int carId = Integer.parseInt(request.getParameter("carId"));
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        int rating = Integer.parseInt(request.getParameter("rating"));
        String comment = request.getParameter("comment");

        ReviewModel review = new ReviewModel(customerId, carId, bookingId, rating, comment);

        reviewDAO.insertReview(review);

        response.sendRedirect("review?carId=" + carId);
    }
}