package service;

import DALs.BookingDAO;
import DALs.CarDAO;
import DALs.ContractDAO;
import DALs.ReviewDAO;
import java.util.List;
import models.AccountModel;
import models.CarModel;
import models.CustomerModel;
import models.ReviewModel;

public class ReviewService {

    private ReviewDAO reviewDAO = new ReviewDAO();
    private ContractDAO contractDAO = new ContractDAO();
    private CarDAO carDAO = new CarDAO();

    public class ReviewPageData {
        public int carId;
        public List<ReviewModel> reviews;
        public CarModel car;
    }

    public class ServiceResult {
        private boolean success;
        private String message;
        private Object data;

        public ServiceResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public ServiceResult(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }
    }

    public ReviewPageData getReviewPage(String contractParam, String carParam) {
        ReviewPageData data = new ReviewPageData();

        int carId;

        if (contractParam != null) {
            int contractId = Integer.parseInt(contractParam);
            carId = contractDAO.getCarIdByContractId(contractId);
            data.reviews = reviewDAO.getReviewByCar(carId);
        } else {
            carId = Integer.parseInt(carParam);
            data.reviews = reviewDAO.getReviewByCar(carId);
        }

        data.carId = carId;
        data.car = carDAO.findById(carId);

        return data;
    }

    public String handleAddReview(CustomerModel customer, String carIdStr, String ratingStr, String comment) {
        if (customer == null) {
            return "login";
        }

        int customerId = customer.getCustomerId();
        int carId = Integer.parseInt(carIdStr);
        int rating = Integer.parseInt(ratingStr);

        BookingDAO bookingDAO = new BookingDAO();
        int bookingId = bookingDAO.getCompletedBooking(customerId, carId);

        if (bookingId == -1) {
            return "error";
        }

        ReviewModel review = new ReviewModel(customerId, carId, bookingId, rating, comment);
        reviewDAO.insertReview(review);

        return "success";
    }

    public String handleUpdateReview(CustomerModel customer, String reviewIdStr, String ratingStr, String comment) {
        if (customer == null) {
            return "login";
        }

        try {
            int reviewId = Integer.parseInt(reviewIdStr);
            int rating = Integer.parseInt(ratingStr);
            int customerId = customer.getCustomerId();

            if (rating < 1 || rating > 5) {
                return "invalid";
            }

            boolean isOwner = reviewDAO.isReviewOwner(reviewId, customerId);
            if (!isOwner) {
                return "forbidden";
            }

            boolean updated = reviewDAO.updateReview(reviewId, customerId, rating, comment);
            return updated ? "success" : "failed";

        } catch (Exception e) {
            e.printStackTrace();
            return "failed";
        }
    }

    public boolean isAdmin(AccountModel account) {
        return account != null && account.getRoleId() == 3;
    }

    public ServiceResult getAdminReviewList(AccountModel account) {
        if (account == null) {
            return new ServiceResult(false, "login");
        }

        if (!isAdmin(account)) {
            return new ServiceResult(false, "forbidden");
        }

        List<ReviewModel> reviews = reviewDAO.getAllReviews();
        return new ServiceResult(true, "success", reviews);
    }

    public ServiceResult deleteReviewByAdmin(AccountModel account, String reviewIdStr) {
        if (account == null) {
            return new ServiceResult(false, "login");
        }

        if (!isAdmin(account)) {
            return new ServiceResult(false, "forbidden");
        }

        try {
            int reviewId = Integer.parseInt(reviewIdStr);

            boolean deleted = reviewDAO.deleteReviewById(reviewId);
            return deleted
                    ? new ServiceResult(true, "success")
                    : new ServiceResult(false, "failed");

        } catch (Exception e) {
            e.printStackTrace();
            return new ServiceResult(false, "failed");
        }
    }

    public ServiceResult getEditReviewPageForCustomer(CustomerModel customer, String reviewIdStr, String carIdStr) {
        if (customer == null) {
            return new ServiceResult(false, "login");
        }

        try {
            int reviewId = Integer.parseInt(reviewIdStr);
            int carId = Integer.parseInt(carIdStr);

            ReviewModel review = reviewDAO.getReviewById(reviewId);

            if (review == null) {
                return new ServiceResult(false, "not_found");
            }

            if (review.getCustomerId() != customer.getCustomerId()) {
                return new ServiceResult(false, "forbidden");
            }

            CarModel car = carDAO.findById(carId);

            ReviewPageData data = new ReviewPageData();
            data.carId = carId;
            data.car = car;
            data.reviews = null;

            return new ServiceResult(true, "success", new Object[]{review, car, carId});

        } catch (Exception e) {
            e.printStackTrace();
            return new ServiceResult(false, "failed");
        }
    }
}