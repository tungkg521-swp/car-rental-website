package Controllers;

import DALs.BookingDAO;
import DALs.CarDAO;
import DALs.ContractDAO;
import DALs.ReviewDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import models.CarModel;

import models.CustomerModel;
import models.ReviewModel;


public class ReviewServlet extends HttpServlet {

    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final ContractDAO contractDAO = new ContractDAO();
    private final CarDAO carDAO = new CarDAO();
    private final BookingDAO bookingDAO = new BookingDAO();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String contractParam = request.getParameter("contractId");
        String carParam = request.getParameter("carId");


        int carId;
        try {
            if (contractParam != null && !contractParam.trim().isEmpty()) {
                int contractId = Integer.parseInt(contractParam);
                carId = contractDAO.getCarIdByContractId(contractId);
            } else if (carParam != null && !carParam.trim().isEmpty()) {
                carId = Integer.parseInt(carParam);
            } else {
                request.getSession().setAttribute("error", "Missing car information.");
                response.sendRedirect("cars");
                return;
            }
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Invalid review page request.");
            response.sendRedirect("cars");
            return;
        }

        CarModel car = carDAO.findById(carId);
        List<ReviewModel> reviews = reviewDAO.getReviewByCar(carId);

        request.setAttribute("car", car);
        request.setAttribute("reviews", reviews);
        request.setAttribute("carId", carId);


        request.getRequestDispatcher("views/review.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        CustomerModel customer = (CustomerModel) session.getAttribute("CUSTOMER");

        String action = request.getParameter("action");
        String carIdStr = request.getParameter("carId");

        int carId;
        try {
            carId = Integer.parseInt(carIdStr);
        } catch (Exception e) {
            session.setAttribute("error", "Invalid car ID.");
            response.sendRedirect("cars");
            return;
        }

        if ("update".equals(action)) {

            if (customer == null) {

                response.sendRedirect("login.jsp");
                return;
            }


            String reviewIdStr = request.getParameter("reviewId");
            String ratingStr = request.getParameter("rating");
            String comment = request.getParameter("comment");

            int reviewId;
            int rating;

            try {
                reviewId = Integer.parseInt(reviewIdStr);
                rating = Integer.parseInt(ratingStr);
            } catch (Exception e) {
                session.setAttribute("error", "Invalid review data.");
                response.sendRedirect("cars?action=detail&carId=" + carId);
                return;
            }

            if (rating < 1 || rating > 5) {
                session.setAttribute("error", "Rating must be from 1 to 5.");
                response.sendRedirect("cars?action=detail&carId=" + carId);
                return;
            }

            boolean isOwner = reviewDAO.isReviewOwner(reviewId, customer.getCustomerId());
            if (!isOwner) {

                session.setAttribute("error", "You can only edit your own review.");
                response.sendRedirect("cars?action=detail&carId=" + carId);
                return;
            }


            boolean updated = reviewDAO.updateReview(reviewId, customer.getCustomerId(), rating, comment);
            if (!updated) {

                session.setAttribute("error", "Update review failed.");
                response.sendRedirect("cars?action=detail&carId=" + carId);
                return;
            }

            session.setAttribute("success", "Review updated successfully.");
            response.sendRedirect("cars?action=detail&carId=" + carId);
            return;
        }


        if (customer == null) {

            response.sendRedirect("login.jsp");
            return;
        }

        String ratingStr = request.getParameter("rating");
        String comment = request.getParameter("comment");

        int rating;
        try {
            rating = Integer.parseInt(ratingStr);
        } catch (Exception e) {
            session.setAttribute("error", "Invalid rating.");
            response.sendRedirect("cars?action=detail&carId=" + carId);
            return;
        }

        if (rating < 1 || rating > 5) {
            session.setAttribute("error", "Rating must be from 1 to 5.");
            response.sendRedirect("cars?action=detail&carId=" + carId);
            return;
        }

        int bookingId = bookingDAO.getCompletedBooking(customer.getCustomerId(), carId);
        if (bookingId == -1) {

            session.setAttribute("error", "You must rent this car before writing a review.");
            response.sendRedirect("cars?action=detail&carId=" + carId);
            return;
        }


        ReviewModel review = new ReviewModel(customer.getCustomerId(), carId, bookingId, rating, comment);
        boolean inserted = reviewDAO.insertReview(review);

        if (!inserted) {
            session.setAttribute("error", "Add review failed.");
            response.sendRedirect("cars?action=detail&carId=" + carId);
            return;
        }


        session.setAttribute("success", "Review added successfully.");
        response.sendRedirect("cars?action=detail&carId=" + carId);
    }
}
