/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DALs.AccountDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import service.AuthenticationService;

/**
 *
 * @author ADMIN
 */
@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/changepassword"})
public class ChangePasswordServlet extends HttpServlet {

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/views/change-password.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String email = request.getParameter("email");
            String oldPassword = request.getParameter("oldPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            AuthenticationService authentiacation = new AuthenticationService();
            AccountDAO dao = new AccountDAO();
            String checkValidate = authentiacation.validateNewPassword(newPassword);

            if (!newPassword.equals(confirmPassword)) {
                request.setAttribute("error", "Confirm password does not match!");
                request.getRequestDispatcher("/views/change-password.jsp")
                        .forward(request, response);
                return;
            }
            if (checkValidate != null) {
                request.setAttribute("error", checkValidate);

            }

            int result = dao.changePassword(email, oldPassword, newPassword);
            if (result == 1) {
                request.getSession().setAttribute("message", "Change Password Successfully!");
                response.sendRedirect(request.getContextPath() + "/login");
            } else if (result == 0) {
                request.getSession().setAttribute("error", "Email or old password is incorrect!");
                response.sendRedirect(request.getContextPath() + "/changepassword");
            }

        } catch (SQLException ex) {
            System.getLogger(ChangePasswordServlet.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
