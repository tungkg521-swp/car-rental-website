package Controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import models.AccountModel;
import service.ReviewService;

@WebServlet(name = "AdminReviewServlet", urlPatterns = {"/admin/review"})
public class AdminReviewServlet extends HttpServlet {

    private final ReviewService reviewService = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        AccountModel account = (AccountModel) session.getAttribute("ACCOUNT");

        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {
            ReviewService.ServiceResult result = reviewService.getAdminReviewList(account);

            if (!result.isSuccess()) {
                handleAdminError(result.getMessage(), session, response, request);
                return;
            }

            request.setAttribute("reviews", result.getData());
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
            ReviewService.ServiceResult result = reviewService.deleteReviewByAdmin(
                    account,
                    request.getParameter("reviewId")
            );

            if (!result.isSuccess()) {
                handleAdminError(result.getMessage(), session, response, request);
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