/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import java.io.IOException;
import jakarta.servlet.ServletException;
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
        } else if ("search".equals(action)) {
            searchCar(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("update".equals(action)) {
            handleUpdate(request, response);
        } else {
            doGet(request, response);
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

  private void searchCar(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    String keyword = request.getParameter("keyword");
    List<CarModel> list;
    if (keyword == null || keyword.trim().isEmpty()) {
        list = carService.findAllAvailableCars();
    } else {
        list = carService.searchCars(keyword);
    }
    request.setAttribute("cars", list);  // SỬA: "cars" thay vì "carList"
    request.getRequestDispatcher("/views/car-list.jsp").forward(request, response);
}

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int carId = Integer.parseInt(request.getParameter("carId"));
            String modelName = request.getParameter("modelName");
            int modelYear = Integer.parseInt(request.getParameter("modelYear"));
            java.math.BigDecimal pricePerDay = new java.math.BigDecimal(request.getParameter("pricePerDay"));
            int seatCount = Integer.parseInt(request.getParameter("seatCount"));
            String fuelType = request.getParameter("fuelType");
            String transmission = request.getParameter("transmission");
            String imageFolder = request.getParameter("imageFolder");
            String description = request.getParameter("description");
            String status = request.getParameter("status");

            models.CarModel car = new models.CarModel(
                    carId,
                    modelName,
                    modelYear,
                    pricePerDay,
                    seatCount,
                    fuelType,
                    transmission,
                    null, // brandName (unchanged)
                    null, // typeName (unchanged)
                    null, // imageUrl (not used here)
                    imageFolder,
                    description,
                    status
            );

            boolean ok = carService.updateCar(car);
            if (ok) {
                response.sendRedirect(request.getContextPath() + "/cars?action=detail&carId=" + carId);
            } else {
                request.setAttribute("error", "Failed to update car");
                showCarDetail(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Invalid input for update");
            showCarDetail(request, response);
        }
    }

}
