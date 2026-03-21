package Controllers;

import DALs.ReviewDAO;
import DALs.BookingDAO;
import DALs.CarDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;

import models.CarModel;
import models.CustomerModel;
import models.ReviewModel;
import service.ReviewService;

public class ReviewServlet extends HttpServlet {

    ReviewService reviewService = new ReviewService();
    ReviewDAO reviewDAO = new ReviewDAO();
    BookingDAO bookingDAO = new BookingDAO();
    CarDAO carDAO = new CarDAO();

    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    HttpSession session = request.getSession();
    CustomerModel customer = (CustomerModel) session.getAttribute("CUSTOMER");

    String action = request.getParameter("action");

    if ("edit".equals(action)) {
        String reviewIdRaw = request.getParameter("reviewId");
        String carIdRaw = request.getParameter("carId");

        if (customer == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            int reviewId = Integer.parseInt(reviewIdRaw);
            int carId = Integer.parseInt(carIdRaw);

            ReviewModel review = reviewDAO.getReviewById(reviewId);

            if (review == null) {
                session.setAttribute("error", "Review not found.");
                response.sendRedirect("cars?action=detail&carId=" + carId);
                return;
            }

            if (review.getCustomerId() != customer.getCustomerId()) {
                session.setAttribute("error", "You can only edit your own review.");
                response.sendRedirect("cars?action=detail&carId=" + carId);
                return;
            }

            CarModel car = carDAO.findById(carId);

            request.setAttribute("review", review);
            request.setAttribute("car", car);
            request.setAttribute("carId", carId);

            request.getRequestDispatcher("views/edit-review.jsp").forward(request, response);
            return;

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("cars");
            return;
        }
    }

    String contractParam = request.getParameter("contractId");
    String carParam = request.getParameter("carId");

    ReviewService.ReviewPageData data = reviewService.getReviewPage(contractParam, carParam);

    request.setAttribute("car", data.car);
    request.setAttribute("reviews", data.reviews);
    request.setAttribute("carId", data.carId);

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
    int carId = Integer.parseInt(carIdStr);

    if ("update".equals(action)) {

        String result = reviewService.handleUpdateReview(
                customer,
                request.getParameter("reviewId"),
                request.getParameter("rating"),
                request.getParameter("comment")
        );

        if ("login".equals(result)) {
            response.sendRedirect("login.jsp");
            return;
        }

        if ("forbidden".equals(result)) {
            session.setAttribute("error", "You can only edit your own review.");
            response.sendRedirect("cars?action=detail&carId=" + carId);
            return;
        }

        if ("invalid".equals(result)) {
            session.setAttribute("error", "Rating must be from 1 to 5.");
            response.sendRedirect("cars?action=detail&carId=" + carId);
            return;
        }

        if ("failed".equals(result)) {
            session.setAttribute("error", "Update review failed.");
            response.sendRedirect("cars?action=detail&carId=" + carId);
            return;
        }

        session.setAttribute("success", "Review updated successfully.");
        response.sendRedirect("cars?action=detail&carId=" + carId);
        return;
    }

    String result = reviewService.handleAddReview(
            customer,
            carIdStr,
            request.getParameter("rating"),
            request.getParameter("comment")
    );

    if ("login".equals(result)) {
        response.sendRedirect("login.jsp");
        return;
    }

    if ("error".equals(result)) {
        session.setAttribute("error", "You must rent this car before writing a review.");
        response.sendRedirect("cars?action=detail&carId=" + carId);
        return;
    }

    session.setAttribute("success", "Review added successfully.");
    response.sendRedirect("cars?action=detail&carId=" + carId);
}
}
