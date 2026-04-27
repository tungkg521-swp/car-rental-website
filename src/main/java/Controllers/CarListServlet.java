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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import models.CarModel;

import DALs.ReviewDAO;
import models.ReviewModel;
import java.util.ArrayList;
import java.util.Arrays;

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
        String startHourRaw = request.getParameter("startHour");
        String endDateRaw = request.getParameter("endDate");
        String endHourRaw = request.getParameter("endHour");

        List<CarModel> cars;

        if (startDateRaw != null && endDateRaw != null
                && !startDateRaw.isBlank() && !endDateRaw.isBlank()) {

            try {
                Timestamp startTime = resolveStartTime(request);
                Timestamp endTime = resolveEndTime(request);
                Timestamp now = new Timestamp(System.currentTimeMillis());

                if (startTime == null || endTime == null
                        || startTime.before(now) || !endTime.after(startTime)) {
                    request.setAttribute("dateError", "Ngày giờ thuê không hợp lệ.");
                    cars = carDAO.findAllAvailableCars();
                } else {
                    cars = carDAO.findAvailableCarsByDateRange(startTime, endTime);

                    request.setAttribute("startDate", startDateRaw);
                    request.setAttribute("startHour", startHourRaw);
                    request.setAttribute("endDate", endDateRaw);
                    request.setAttribute("endHour", endHourRaw);
                }
            } catch (Exception e) {
                request.setAttribute("dateError", "Ngày giờ thuê không hợp lệ.");
                cars = carDAO.findAllAvailableCars();
            }

        } else {
            cars = carDAO.findAllAvailableCars();

        }

        List<String> listBrand = carDAO.getAllBrandNames();
        List<String> listType = carDAO.getAllTypeNames();
        String keyword = request.getParameter("keyword");
        request.setAttribute("cars", cars);
        request.setAttribute("typeList", listType);
        request.setAttribute("brandList", listBrand);
        request.setAttribute("keyword", keyword);
        request.setAttribute("startDate", startDateRaw);
        request.setAttribute("startHour", startHourRaw);
        request.setAttribute("endDate", endDateRaw);
        request.setAttribute("endHour", endHourRaw);
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

        List<Timestamp[]> busyRanges = bookingDAO.getBusyDateRangesByCarId(carId);

        List<String> busyDates = new ArrayList<>();
        StringBuilder busyTimeRangesJson = new StringBuilder("[");

        for (int i = 0; i < busyRanges.size(); i++) {
            Timestamp[] range = busyRanges.get(i);

            LocalDateTime start = range[0].toLocalDateTime();
            LocalDateTime end = range[1].toLocalDateTime();

            LocalDateTime current = start;
            while (!current.toLocalDate().isAfter(end.toLocalDate())) {
                busyDates.add(current.toLocalDate().toString());
                current = current.plusDays(1);
            }

            busyTimeRangesJson.append("{")
                    .append("\"start\":\"").append(start.toString().replace(" ", "T")).append("\",")
                    .append("\"end\":\"").append(end.toString().replace(" ", "T")).append("\",")
                    .append("\"type\":\"BOOKING\"")
                    .append("}");

            if (i < busyRanges.size() - 1) {
                busyTimeRangesJson.append(",");
            }
        }
        busyTimeRangesJson.append("]");

        StringBuilder busyDatesJson = new StringBuilder("[");
        for (int i = 0; i < busyDates.size(); i++) {
            busyDatesJson.append("\"").append(busyDates.get(i)).append("\"");
            if (i < busyDates.size() - 1) {
                busyDatesJson.append(",");
            }
        }
        busyDatesJson.append("]");

        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        ReviewDAO reviewDAO = new ReviewDAO();
        List<ReviewModel> reviews = reviewDAO.getReviewByCar(carId);

        request.setAttribute("car", car);
        request.setAttribute("reviews", reviews);
        request.setAttribute("dailyKmLimit", 400);
        request.setAttribute("lateFeePerHour", 100000);
        request.setAttribute("lateFeeFlatDayThreshold", 4);
        request.setAttribute("extraKmFee", getExtraKmFee(car.getPricePerDay()));
        request.setAttribute("cleaningFee", getCleaningFee(car.getPricePerDay()));
        request.setAttribute("deodorizingFee", getDeodorizingFee(car.getPricePerDay()));
        String startHour = request.getParameter("startHour");
        String endHour = request.getParameter("endHour");

        request.setAttribute("startDate", startDate);
        request.setAttribute("startHour", startHour);
        request.setAttribute("endDate", endDate);
        request.setAttribute("endHour", endHour);
        request.setAttribute("busyDatesJson", busyDatesJson.toString());
        request.setAttribute("busyTimeRangesJson", busyTimeRangesJson.toString());
        request.getRequestDispatcher("/views/car-detail.jsp").forward(request, response);
    }

    private void searchCar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");
        String startDateRaw = request.getParameter("startDate");
        String startHourRaw = request.getParameter("startHour");
        String endDateRaw = request.getParameter("endDate");
        String endHourRaw = request.getParameter("endHour");

        String cleaned = (keyword == null) ? "" : keyword.trim().replaceAll("\\s+", " ");

        List<CarModel> list = cleaned.isEmpty()
                ? carDAO.findAllAvailableCars()
                : carDAO.searchCars(cleaned);

        list = keepCarsAvailableInDateRange(request, list);

         List<String> listBrand = carDAO.getAllBrandNames();
        List<String> listType = carDAO.getAllTypeNames();
        request.setAttribute("cars", list);
         request.setAttribute("typeList", listType);
        request.setAttribute("brandList", listBrand);
        request.setAttribute("keyword", keyword);
        request.setAttribute("startDate", startDateRaw);
        request.setAttribute("startHour", startHourRaw);
        request.setAttribute("endDate", endDateRaw);
        request.setAttribute("endHour", endHourRaw);

        request.getRequestDispatcher("/views/car-list.jsp").forward(request, response);
    }

    private void filterCars(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");
        String startDateRaw = request.getParameter("startDate");
        String startHourRaw = request.getParameter("startHour");
        String endDateRaw = request.getParameter("endDate");
        String endHourRaw = request.getParameter("endHour");

        boolean availableOnly = "on".equals(request.getParameter("availableOnly"));
        String brands = request.getParameter("brand");

        String types = request.getParameter("type");
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
        List<String> brandList = carDAO.getAllBrandNames();
        List<String> typeList = carDAO.getAllTypeNames();
        list = keepCarsAvailableInDateRange(request, list);

        request.setAttribute("cars", list);
        request.setAttribute("brandList", brandList);
        request.setAttribute("typeList", typeList);
        request.setAttribute("keyword", keyword);
        request.setAttribute("availableOnly", availableOnly);
        request.setAttribute("seats", seats);
        request.setAttribute("transmission", transmission);
        request.setAttribute("yearRange", yearRange);
        request.setAttribute("maxPrice", maxPrice);
        request.setAttribute("startDate", startDateRaw);
        request.setAttribute("startHour", startHourRaw);
        request.setAttribute("endDate", endDateRaw);
        request.setAttribute("endHour", endHourRaw);

        request.getRequestDispatcher("/views/car-list.jsp").forward(request, response);
    }

    private int getExtraKmFee(BigDecimal pricePerDay) {
        if (pricePerDay == null) {
            return 3000;
        }
        if (pricePerDay.compareTo(new BigDecimal("700000")) < 0) {
            return 3000;
        }
        if (pricePerDay.compareTo(new BigDecimal("1200000")) < 0) {
            return 5000;
        }
        return 7000;
    }

    private int getCleaningFee(BigDecimal pricePerDay) {
        if (pricePerDay == null) {
            return 70000;
        }
        if (pricePerDay.compareTo(new BigDecimal("700000")) < 0) {
            return 70000;
        }
        if (pricePerDay.compareTo(new BigDecimal("1200000")) < 0) {
            return 90000;
        }
        return 100000;
    }

    private int getDeodorizingFee(BigDecimal pricePerDay) {
        if (pricePerDay == null) {
            return 300000;
        }
        if (pricePerDay.compareTo(new BigDecimal("700000")) < 0) {
            return 300000;
        }
        if (pricePerDay.compareTo(new BigDecimal("1200000")) < 0) {
            return 400000;
        }
        return 500000;
    }

    private boolean isValidDateRange(HttpServletRequest request) {
        try {
            Timestamp startTime = resolveStartTime(request);
            Timestamp endTime = resolveEndTime(request);
            Timestamp now = new Timestamp(System.currentTimeMillis());

            return startTime != null
                    && endTime != null
                    && !startTime.before(now)
                    && endTime.after(startTime);
        } catch (Exception e) {
            return false;
        }
    }

    private List<CarModel> keepCarsAvailableInDateRange(HttpServletRequest request, List<CarModel> cars) {
        if (!isValidDateRange(request)) {
            return cars;
        }

        Timestamp startTime = resolveStartTime(request);
        Timestamp endTime = resolveEndTime(request);

        List<CarModel> filtered = new ArrayList<>();

        for (CarModel car : cars) {
            if (!carDAO.isCarBookedInRange(car.getCarId(), startTime, endTime)) {
                filtered.add(car);
            }
        }

        return filtered;
    }

    private Timestamp parseDateTimeLocal(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }

        try {
            String normalized = raw.trim().replace("T", " ");
            if (normalized.length() == 16) {
                normalized += ":00";
            }
            return Timestamp.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Timestamp parseDateTimeFromRequest(HttpServletRequest request, String dateParam, String timeParam) {
        String dateValue = request.getParameter(dateParam);
        String timeValue = request.getParameter(timeParam);

        if (dateValue == null || dateValue.trim().isEmpty()
                || timeValue == null || timeValue.trim().isEmpty()) {
            return null;
        }

        try {
            String normalized = dateValue.trim() + " " + timeValue.trim() + ":00";
            return Timestamp.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Timestamp resolveStartTime(HttpServletRequest request) {
        Timestamp direct = parseDateTimeLocal(request.getParameter("startDate"));
        if (direct != null) {
            return direct;
        }
        return parseDateTimeFromRequest(request, "startDate", "startHour");
    }

    private Timestamp resolveEndTime(HttpServletRequest request) {
        Timestamp direct = parseDateTimeLocal(request.getParameter("endDate"));
        if (direct != null) {
            return direct;
        }
        return parseDateTimeFromRequest(request, "endDate", "endHour");
    }
}
