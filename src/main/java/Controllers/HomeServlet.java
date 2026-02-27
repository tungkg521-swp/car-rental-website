/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.UserModel;

/**
 *
 * @author ADMIN
 */

public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        UserModel user = null;

        if (session != null) {
            user = (UserModel) session.getAttribute("USER");
        }

        if (user != null && "CUSTOMER".equals(user.getRole())) {
            request.getRequestDispatcher("/views/home.jsp")
                   .forward(request, response);
        } else {
            request.getRequestDispatcher("/views/home.jsp")
                   .forward(request, response);
        }
    }

}
