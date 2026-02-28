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
import java.util.List;
import models.CarModel;
import service.CarService;

/**
 *
 * @author ADMIN
 */
public class CarListServlet extends HttpServlet {

    private CarService carService = new CarService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {
            listCars(request, response);
        } else if (action.equals("detail")) {
            showCarDetail(request, response);
        }
    }

    private void listCars(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<CarModel> cars = carService.findAllAvailableCars();
        request.setAttribute("cars", cars);
        request.getRequestDispatcher("/views/car-list.jsp").forward(request, response);
    }

    private void showCarDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String carIdRaw = request.getParameter("carId");
        if (carIdRaw == null) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        int carId;
        try {
            carId = Integer.parseInt(carIdRaw);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        CarModel car = carService.getCarById(carId);
        if (car == null) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        request.setAttribute("car", car);
        request.getRequestDispatcher("/views/car-detail.jsp")
                .forward(request, response);
    }

}
