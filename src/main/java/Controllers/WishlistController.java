/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import models.WishlistModel;
import service.WishlistService;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import models.CarModel;
import models.CustomerModel;
import service.CarService;

/**
 *
 * @author Admin
 */
public class WishlistController extends HttpServlet {

    WishlistService service = new WishlistService();
    CarService carService = new CarService();

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
        viewWishlist(request, response);
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
        String action = request.getParameter("action");
        if (action.equals("add")) {
            addWishlist(request, response);
        } else if (action.equals("delete")) {
removeWishlist(request,response);
        } else {
            response.sendRedirect(request.getContextPath() + "/home");
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

    private void viewWishlist(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        CustomerModel customer = (CustomerModel) session.getAttribute("CUSTOMER");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int customerId = customer.getCustomerId();

        List<WishlistModel> list = service.getWishlist(customerId);

        request.setAttribute("wishlist", list);

        request.getRequestDispatcher("/views/wishlist.jsp").forward(request, response);
    }

    private void addWishlist(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        CustomerModel customer = (CustomerModel) session.getAttribute("CUSTOMER");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
          int customerId = customer.getCustomerId();
        int carId = Integer.parseInt(request.getParameter("carId"));

        String message = service.addToWishlist(customerId, carId);

        if (message.equals("Added to wishlist successfully!")) {
            request.setAttribute("SUCCESS", message);
        } else {
            request.setAttribute("ERROR", message);
        }
        CarModel car = carService.getCarById(carId);
        request.setAttribute("car", car);

        request.setAttribute("MESSAGE", message);
        request.getRequestDispatcher("/views/car-detail.jsp")
                .forward(request, response);

    }
    
    
    private void removeWishlist(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    HttpSession session = request.getSession();
    CustomerModel customer = (CustomerModel) session.getAttribute("CUSTOMER");

    if (customer == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    int customerId = customer.getCustomerId();

    String carIdParam = request.getParameter("carId");
    if (carIdParam == null || carIdParam.isEmpty()) {
        response.sendRedirect(request.getContextPath() + "/wishlist");
        return;
    }

    int carId = Integer.parseInt(carIdParam);

    String message = service.removeFromWishlist(customerId, carId);

    if ("Removed successfully!".equals(message)) {
        session.setAttribute("SUCCESS", message);
    } else {
        session.setAttribute("ERROR", message);
    }

    response.sendRedirect(request.getContextPath() + "/wishlist");
}
}
