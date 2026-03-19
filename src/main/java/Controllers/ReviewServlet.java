package Controllers;

import DALs.ReviewDAO;
import DALs.BookingDAO;
import DALs.CarDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String contractParam = request.getParameter("contractId");
        int carId;
        List<ReviewModel> reviews;
        if (contractParam != null) {
            int contractId = Integer.parseInt(contractParam);
            carId = reviewService.getCarIdFromContract(contractId);
            reviews = reviewService.getReviewsByContract(contractId);
        } else {
            carId = Integer.parseInt(request.getParameter("carId"));
            reviews = reviewService.getReviewsByCar(carId);
        }
        CarModel car = carDAO.findById(carId);
        request.setAttribute("car", car);
        request.setAttribute("reviews", reviews);
        request.setAttribute("carId", carId);
        System.out.println("IMAGE = " + car.getImageUrl());
        request.getRequestDispatcher("views/review.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        // Customer chưa thuê xe 
        if (bookingId == -1) {
            session.setAttribute("error", "You must rent this car before writing a review.");
            response.sendRedirect("cars?action=detail&carId=" + carId);
            return;
        }
// Insert review 
        ReviewModel review = new ReviewModel(customerId, carId, bookingId, rating, comment);
        reviewService.addReview(review);
        response.sendRedirect("cars?action=detail&carId=" + carId);
    }
}
