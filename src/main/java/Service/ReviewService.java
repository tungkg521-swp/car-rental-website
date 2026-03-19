package service;

import DALs.CarDAO;
import DALs.ContractDAO;
import DALs.ReviewDAO;
import java.util.List;
import models.ReviewModel;

public class ReviewService {

    private ReviewDAO reviewDAO = new ReviewDAO();
    private ContractDAO contractDAO = new ContractDAO();
    public List<ReviewModel> getReviewsByContract(int contractId) {

        int carId = contractDAO.getCarIdByContractId(contractId);

        return reviewDAO.getReviewByCar(carId);
    }

    public List<ReviewModel> getReviewsByCar(int carId) {
        return reviewDAO.getReviewByCar(carId);
    }

    public int getCarIdFromContract(int contractId) {
        return contractDAO.getCarIdByContractId(contractId);
    }

    public boolean addReview(ReviewModel review) {
        return reviewDAO.insertReview(review);
    }
}
