/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DALs.CarDAO;
import DALs.WishlistDAO;

import models.WishlistModel;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import models.CarModel;
import models.CustomerModel;

/**
 *
 * @author Admin
 */
public class WishlistController extends HttpServlet {

    private final WishlistDAO wishlistDAO = new WishlistDAO();
    private final CarDAO carDAO = new CarDAO();

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

            removeWishlist(request, response);

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
    }

    private void viewWishlist(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        CustomerModel customer = (CustomerModel) session.getAttribute("CUSTOMER");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int customerId = customer.getCustomerId();

        List<WishlistModel> list = wishlistDAO.findByCustomerId(customerId);

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

        String message;

        CarModel car = carDAO.findById(carId);
        if (car == null) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;

        } else if (wishlistDAO.exists(customerId, carId)) {
            message = "Đã tồn tại trong danh sách yêu thích!";
        } else {
            boolean success = wishlistDAO.create(customerId, carId);
            message = success ? "Thêm thành công!" : "có lỗi khi thêm!";
        }

        if (message.equals("Thêm thành công!")) {
            request.setAttribute("SUCCESS", message);
        } else {
            request.setAttribute("ERROR", message);
        }

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

        String message;
        if (!wishlistDAO.exists(customerId, carId)) {
            message = "Không thấy xe này trong danh sách yêu thích!";
        } else {
            boolean success = wishlistDAO.delete(customerId, carId);
            message = success ? "Đã bỏ yêu thích!" : "Bỏ yêu thích thất bại!";
        }
        if ("Đã bỏ yêu thích!".equals(message)) {
            request.setAttribute("success", message);
        } else {
            request.setAttribute("error", message);
        }

        List<WishlistModel> list = wishlistDAO.findByCustomerId(customerId);

        request.setAttribute("wishlist", list);
        request.getRequestDispatcher("/views/wishlist.jsp").forward(request, response);
    }

}
