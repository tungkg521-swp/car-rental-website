package Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;

import models.CustomerModel;
import service.ReviewService;

public class ReviewServlet extends HttpServlet {

    private final ReviewService reviewService = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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

        int carId;
        try {
            carId = Integer.parseInt(carIdStr);
        } catch (Exception e) {
            session.setAttribute("error", "Invalid car ID.");
            response.sendRedirect("cars");
            return;
        }

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
