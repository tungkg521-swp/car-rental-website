package DALs;

import Utils.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.ReviewModel;

public class ReviewDAO extends DBContext {

    public List<ReviewModel> getReviewByCar(int carId) {
        List<ReviewModel> list = new ArrayList<>();

        String sql = """
                     SELECT r.*, c.full_name
                     FROM reviews r
                     JOIN customer c
                     ON r.customer_id = c.customer_id
                     WHERE r.car_id = ?
                     ORDER BY r.created_at DESC
                     """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, carId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReviewModel r = new ReviewModel();

                r.setReviewId(rs.getInt("review_id"));
                r.setCustomerId(rs.getInt("customer_id"));
                r.setCarId(rs.getInt("car_id"));
                r.setBookingId(rs.getInt("booking_id"));
                r.setRating(rs.getInt("rating"));
                r.setComment(rs.getString("comment"));
                r.setCreatedAt(rs.getTimestamp("created_at"));
                r.setCustomerName(rs.getString("full_name"));

                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insertReview(ReviewModel review) {
        String sql = "INSERT INTO reviews(customer_id,car_id,booking_id,rating,comment) VALUES(?,?,?,?,?)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, review.getCustomerId());
            ps.setInt(2, review.getCarId());
            ps.setInt(3, review.getBookingId());
            ps.setInt(4, review.getRating());
            ps.setString(5, review.getComment());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public ReviewModel getReviewById(int reviewId) {
        String sql = """
                     SELECT r.*, c.full_name
                     FROM reviews r
                     JOIN customer c ON r.customer_id = c.customer_id
                     WHERE r.review_id = ?
                     """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, reviewId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ReviewModel r = new ReviewModel();
                r.setReviewId(rs.getInt("review_id"));
                r.setCustomerId(rs.getInt("customer_id"));
                r.setCarId(rs.getInt("car_id"));
                r.setBookingId(rs.getInt("booking_id"));
                r.setRating(rs.getInt("rating"));
                r.setComment(rs.getString("comment"));
                r.setCreatedAt(rs.getTimestamp("created_at"));
                r.setCustomerName(rs.getString("full_name"));
                return r;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isReviewOwner(int reviewId, int customerId) {
        String sql = "SELECT 1 FROM reviews WHERE review_id = ? AND customer_id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, reviewId);
            ps.setInt(2, customerId);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateReview(int reviewId, int customerId, int rating, String comment) {
        String sql = """
                 UPDATE reviews
                 SET rating = ?, comment = ?, created_at = GETDATE()
                 WHERE review_id = ? AND customer_id = ?
                 """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, rating);
            ps.setString(2, comment);
            ps.setInt(3, reviewId);
            ps.setInt(4, customerId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public List<ReviewModel> getAllReviews() {
    List<ReviewModel> list = new ArrayList<>();

    String sql = """
                 SELECT r.*, c.full_name
                 FROM reviews r
                 JOIN customer c ON r.customer_id = c.customer_id
                 ORDER BY r.created_at DESC
                 """;

    try {
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            ReviewModel r = new ReviewModel();
            r.setReviewId(rs.getInt("review_id"));
            r.setCustomerId(rs.getInt("customer_id"));
            r.setCarId(rs.getInt("car_id"));
            r.setBookingId(rs.getInt("booking_id"));
            r.setRating(rs.getInt("rating"));
            r.setComment(rs.getString("comment"));
            r.setCreatedAt(rs.getTimestamp("created_at"));
            r.setCustomerName(rs.getString("full_name"));
            list.add(r);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

public boolean deleteReviewById(int reviewId) {
    String sql = "DELETE FROM reviews WHERE review_id = ?";

    try {
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, reviewId);
        return ps.executeUpdate() > 0;
    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
    
}
