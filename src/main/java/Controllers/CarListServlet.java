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
import java.math.BigDecimal;
import java.util.List;
import models.CarModel;
import service.CarService;
import DALs.ReviewDAO;
import models.ReviewModel;

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
        } else if (action.equals("search")) {
            searchCar(request, response);
        } else if (action.equals("filter")) {
            filterCars(request, response);
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

        ReviewDAO reviewDAO = new ReviewDAO();
        List<ReviewModel> reviews = reviewDAO.getReviewByCar(carId);

        request.setAttribute("car", car);
        request.setAttribute("reviews", reviews);

        request.getRequestDispatcher("/views/car-detail.jsp")
                .forward(request, response);
    }

    private void searchCar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");

       


        String cleaned = (keyword == null) ? "" : keyword.trim().replaceAll("\\s+", " ");

        List<CarModel> list = cleaned.isEmpty()
                ? carService.findAllAvailableCars()
                : carService.searchCars(cleaned);
        request.setAttribute("cars", list);
        request.setAttribute("keyword", keyword);  // Để giữ giá trị search box
        request.getRequestDispatcher("/views/car-list.jsp").forward(request, response);
    }


    private void filterCars(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy tất cả param như trước
        String keyword = request.getParameter("keyword");
        boolean availableOnly = "on".equals(request.getParameter("availableOnly"));
        String[] brands = request.getParameterValues("brand");
        String[] types = request.getParameterValues("type");
        String[] fuels = request.getParameterValues("fuel");
        String seatsStr = request.getParameter("seats");
        Integer seats = (seatsStr != null && !seatsStr.isEmpty()) ? Integer.parseInt(seatsStr) : null;
        String transmission = request.getParameter("transmission");
        if ("Any".equals(transmission)) {
            transmission = null;
        }
        String yearRange = request.getParameter("yearRange");
        if ("Any".equals(yearRange)) {
            yearRange = null;
        }
        String maxPriceStr = request.getParameter("maxPrice");
        BigDecimal maxPrice = (maxPriceStr != null && !maxPriceStr.isEmpty()) ? new BigDecimal(maxPriceStr) : null;

        List<CarModel> list = carService.filterCars(keyword, availableOnly, brands, types, fuels,
                seats, transmission, yearRange, maxPrice);

        request.setAttribute("cars", list);
        // Giữ các param để hiển thị trạng thái filter và search box
        request.setAttribute("keyword", keyword);
        request.setAttribute("availableOnly", availableOnly);
        request.setAttribute("seats", seats);
        request.setAttribute("transmission", transmission);
        request.setAttribute("yearRange", yearRange);
        request.setAttribute("maxPrice", maxPrice);

        request.getRequestDispatcher("/views/car-list.jsp").forward(request, response);
    }
}

