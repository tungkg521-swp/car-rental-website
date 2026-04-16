/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;


import DALs.BookingDAO;
import DALs.CarDAO;

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
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import service.BookingService;

/**
 *
 * @author ADMIN
 */
public class CarListServlet extends HttpServlet {


    private final CarDAO carDAO = new CarDAO(); 
    private BookingDAO bookingDAO = new BookingDAO();


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

        String startDateRaw = request.getParameter("startDate");
        String endDateRaw = request.getParameter("endDate");

        List<CarModel> cars;

        if (startDateRaw != null && endDateRaw != null
                && !startDateRaw.isBlank() && !endDateRaw.isBlank()) {

            try {
                Date startDate = Date.valueOf(startDateRaw);
                Date endDate = Date.valueOf(endDateRaw);
                Date today = Date.valueOf(LocalDate.now());

                if (startDate.before(today) || !endDate.after(startDate)) {
                    request.setAttribute("dateError", "Ngày thuê không hợp lệ.");

                    cars = carDAO.findAllAvailableCars();
                } else {
                    cars = carDAO.findAvailableCarsByDateRange(startDate, endDate);

                    request.setAttribute("startDate", startDateRaw);
                    request.setAttribute("endDate", endDateRaw);
                }
            } catch (Exception e) {
                request.setAttribute("dateError", "Ngày thuê không hợp lệ.");

                cars = carDAO.findAllAvailableCars();
            }

        } else {
            cars = carDAO.findAllAvailableCars();

        }

        String keyword = request.getParameter("keyword");
        request.setAttribute("cars", cars);
        request.setAttribute("keyword", keyword);
        request.setAttribute("startDate", startDateRaw);
        request.setAttribute("endDate", endDateRaw);
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


        CarModel car = carDAO.findById(carId);

        if (car == null) {
            response.sendRedirect(request.getContextPath() + "/cars");
            return;
        }

        List<Date[]> busyRanges = bookingDAO.getBusyDateRangesByCarId(carId);


        System.out.println("=== BUSY RANGES OF CAR " + carId + " ===");
        for (Date[] range : busyRanges) {
            System.out.println("Start: " + range[0] + " | End: " + range[1]);
        }
    
        List<String> busyDates = new ArrayList<>();
        for (Date[] range : busyRanges) {
            LocalDate start = range[0].toLocalDate();
            LocalDate end = range[1].toLocalDate();

            while (!start.isAfter(end)) {
                busyDates.add(start.toString());
                start = start.plusDays(1);
            }
        }

        StringBuilder busyDatesJson = new StringBuilder("[");
        for (int i = 0; i < busyDates.size(); i++) {
            busyDatesJson.append("\"").append(busyDates.get(i)).append("\"");
            if (i < busyDates.size() - 1) {
                busyDatesJson.append(",");
            }
        }
        busyDatesJson.append("]");
           System.out.println("busyDatesJson = " + busyDatesJson.toString());

        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        ReviewDAO reviewDAO = new ReviewDAO();
        List<ReviewModel> reviews = reviewDAO.getReviewByCar(carId);

        request.setAttribute("car", car);
        request.setAttribute("reviews", reviews);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("busyDatesJson", busyDatesJson.toString());
        request.getRequestDispatcher("/views/car-detail.jsp").forward(request, response);
    }

    private void searchCar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");
        String startDateRaw = request.getParameter("startDate");
        String endDateRaw = request.getParameter("endDate");

        String cleaned = (keyword == null) ? "" : keyword.trim().replaceAll("\\s+", " ");

        List<CarModel> list = cleaned.isEmpty()

                ? carDAO.findAllAvailableCars()
                : carDAO.searchCars(cleaned);


        list = keepCarsAvailableInDateRange(list, startDateRaw, endDateRaw);

        request.setAttribute("cars", list);
        request.setAttribute("keyword", keyword);
        request.setAttribute("startDate", startDateRaw);
        request.setAttribute("endDate", endDateRaw);

        request.getRequestDispatcher("/views/car-list.jsp").forward(request, response);
    }

    private void filterCars(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");
        String startDateRaw = request.getParameter("startDate");
        String endDateRaw = request.getParameter("endDate");

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
        BigDecimal maxPrice = (maxPriceStr != null && !maxPriceStr.isEmpty())
                ? new BigDecimal(maxPriceStr)
                : null;


        List<CarModel> list = carDAO.filterCars(

                keyword,
                availableOnly,
                brands,
                types,
                fuels,
                seats,
                transmission,
                yearRange,
                maxPrice
        );

        list = keepCarsAvailableInDateRange(list, startDateRaw, endDateRaw);

        request.setAttribute("cars", list);
        request.setAttribute("keyword", keyword);
        request.setAttribute("availableOnly", availableOnly);
        request.setAttribute("seats", seats);
        request.setAttribute("transmission", transmission);
        request.setAttribute("yearRange", yearRange);
        request.setAttribute("maxPrice", maxPrice);
        request.setAttribute("startDate", startDateRaw);
        request.setAttribute("endDate", endDateRaw);

        request.getRequestDispatcher("/views/car-list.jsp").forward(request, response);
    }

    private boolean isValidDateRange(String startDateRaw, String endDateRaw) {
        if (startDateRaw == null || endDateRaw == null
                || startDateRaw.isBlank() || endDateRaw.isBlank()) {
            return false;
        }

        try {
            Date startDate = Date.valueOf(startDateRaw);
            Date endDate = Date.valueOf(endDateRaw);
            Date today = Date.valueOf(LocalDate.now());

            return !startDate.before(today) && endDate.after(startDate);
        } catch (Exception e) {
            return false;
        }
    }

    private List<CarModel> keepCarsAvailableInDateRange(List<CarModel> cars, String startDateRaw, String endDateRaw) {
        if (!isValidDateRange(startDateRaw, endDateRaw)) {
            return cars;
        }

        Date startDate = Date.valueOf(startDateRaw);
        Date endDate = Date.valueOf(endDateRaw);

        List<CarModel> filtered = new java.util.ArrayList<>();

        for (CarModel car : cars) {

            if (!carDAO.isCarBookedInRange(car.getCarId(), startDate, endDate)) {

                filtered.add(car);
            }
        }

        return filtered;
    }
}
