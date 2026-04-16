package Controllers;


import DALs.ReviewDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import java.util.List;
import models.AccountModel;
import models.ReviewModel;

import service.ReviewService;

@WebServlet(name = "AdminReviewServlet", urlPatterns = {"/admin/review"})
public class AdminReviewServlet extends HttpServlet {


    private final ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        AccountModel account = (AccountModel) session.getAttribute("ACCOUNT");

        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {

            if (account == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            if (account.getRoleId() != 3) {
                session.setAttribute("error", "You are not allowed to access this function.");
                response.sendRedirect(request.getContextPath() + "/dashboard/admin");
                return;
            }

            List<ReviewModel> reviews = reviewDAO.getAllReviews();
            request.setAttribute("reviews", reviews);

            request.getRequestDispatcher("/views/review-list.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/review?action=list");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        AccountModel account = (AccountModel) session.getAttribute("ACCOUNT");

        String action = request.getParameter("action");

        if ("delete".equals(action)) {

            if (account == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            if (account.getRoleId() != 3) {
                session.setAttribute("error", "You are not allowed to access this function.");
                response.sendRedirect(request.getContextPath() + "/dashboard/admin");
                return;
            }

            String reviewIdStr = request.getParameter("reviewId");
            int reviewId;

            try {
                reviewId = Integer.parseInt(reviewIdStr);
            } catch (Exception e) {
                session.setAttribute("error", "Invalid review ID.");
                response.sendRedirect(request.getContextPath() + "/admin/review?action=list");
                return;
            }

            boolean deleted = reviewDAO.deleteReviewById(reviewId);

            if (!deleted) {
                session.setAttribute("error", "Delete review failed.");
                response.sendRedirect(request.getContextPath() + "/admin/review?action=list");

                return;
            }

            session.setAttribute("success", "Review deleted successfully.");
            response.sendRedirect(request.getContextPath() + "/admin/review?action=list");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/review?action=list");
    }

    private void handleAdminError(String message, HttpSession session,
            HttpServletResponse response, HttpServletRequest request) throws IOException {

        if ("login".equals(message)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if ("forbidden".equals(message)) {
            session.setAttribute("error", "You are not allowed to access this function.");
            response.sendRedirect(request.getContextPath() + "/dashboard/admin");
            return;
        }

        session.setAttribute("error", "Action failed.");
        response.sendRedirect(request.getContextPath() + "/dashboard/admin");
    }

}

