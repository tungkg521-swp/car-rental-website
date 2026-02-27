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
@WebServlet("/staff/cars")
public class StaffCarController extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        CarService carService = new CarService();

        if (action == null || action.equals("list")) {

            List<CarModel> carList = carService.findAllCars();
            request.setAttribute("carList", carList);

            request.getRequestDispatcher("/views/staff-cars.jsp")
                   .forward(request, response);

        } else if (action.equals("detail")) {

            int carId = Integer.parseInt(request.getParameter("id"));

            CarModel car = carService.getCarById(carId);

            if (car == null) {
                response.sendRedirect(request.getContextPath() + "/staff/cars");
                return;
            }

            request.setAttribute("car", car);

            request.getRequestDispatcher("/views/staff-car-detail.jsp")
                   .forward(request, response);
        }
    }
}
