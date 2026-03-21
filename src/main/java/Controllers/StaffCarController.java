package Controllers;

import Utils.RoleConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import models.AccountModel;
import models.CarModel;
import service.CarService;

@WebServlet("/staff/cars")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class StaffCarController extends HttpServlet {

    private final CarService carService = new CarService();

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("getImages".equals(action)) {
            try {
                int carId = Integer.parseInt(request.getParameter("id"));
                List<String> images = carService.getCarImages(carId);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < images.size(); i++) {
                    json.append("\"").append(images.get(i)).append("\"");
                    if (i < images.size() - 1) {
                        json.append(",");
                    }
                }
                json.append("]");

                response.getWriter().write(json.toString());
            } catch (Exception e) {
                e.printStackTrace();
                response.getWriter().write("[]");
            }
            return;
        }

        if (action == null || action.equals("list")) {
            List<CarModel> carList = carService.findAllCars();
            request.setAttribute("carList", carList);
            request.getRequestDispatcher("/views/staff-cars-manager.jsp")
                    .forward(request, response);
            return;
        }

        if (action.equals("search")) {
            String keyword = request.getParameter("keyword");
            String status = request.getParameter("status");

            List<CarModel> carList;

            if (keyword != null && !keyword.trim().isEmpty()) {
                carList = carService.searchCars(keyword);
            } else {
                carList = carService.findAllCars();
            }

            if (status != null && !status.trim().isEmpty()) {
                carList = carList.stream()
                        .filter(car -> car.getStatus().equals(status))
                        .toList();
            }

            request.setAttribute("carList", carList);
            request.getRequestDispatcher("/views/staff-cars-manager.jsp")
                    .forward(request, response);
            return;
        }

        if (action.equals("detail")) {
            int carId = Integer.parseInt(request.getParameter("id"));
            CarModel car = carService.getCarById(carId);

            request.setAttribute("car", car);
            request.getRequestDispatcher("/views/staff-cars-manager.jsp")
                    .forward(request, response);
            return;
        }

        if (action.equals("add")) {
            if (!isAdmin(request)) {
                request.getSession().setAttribute("error", "Only admin can add cars.");
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
                return;
            }

            request.getRequestDispatcher("/views/staff-cars-manager.jsp")
                    .forward(request, response);
            return;
        }

        if (action.equals("edit")) {
            if (!isAdmin(request)) {
                request.getSession().setAttribute("error", "Only admin can edit cars.");
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
                return;
            }

            int carId = Integer.parseInt(request.getParameter("id"));
            CarModel car = carService.getCarById(carId);

            request.setAttribute("car", car);
            request.getRequestDispatcher("/views/staff-cars-manager.jsp")
                    .forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("create".equals(action)) {
            if (!isAdmin(request)) {
                request.getSession().setAttribute("error", "Only admin can add cars.");
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
                return;
            }
            createCar(request, response);
            return;
        }

        if ("update".equals(action)) {
            if (!isAdmin(request)) {
                request.getSession().setAttribute("error", "Only admin can update cars.");
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
                return;
            }
            updateCar(request, response);
            return;
        }

        if ("delete".equals(action)) {
            deleteCar(request, response);
        }
    }

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        AccountModel account = (AccountModel) session.getAttribute("ACCOUNT");
        return account != null && account.getRoleId() == RoleConstants.ADMIN;
    }

    private void createCar(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String modelName = request.getParameter("modelName");
            int modelYear = Integer.parseInt(request.getParameter("modelYear"));
            BigDecimal pricePerDay = new BigDecimal(request.getParameter("pricePerDay"));
            int seatCount = Integer.parseInt(request.getParameter("seatCount"));
            String fuelType = request.getParameter("fuelType");
            String transmission = request.getParameter("transmission");
            String brandName = request.getParameter("brandName");
            String typeName = request.getParameter("typeName");
            String description = request.getParameter("description");
            String status = request.getParameter("status");

            String imageFolder = modelName.toLowerCase()
                    .replaceAll("\\s+", "_")
                    .replaceAll("vinfast_", "")
                    .replaceAll("[^a-z0-9_]", "");

            if (imageFolder.isEmpty()) {
                imageFolder = "car_" + System.currentTimeMillis();
            }

            String uploadPath = getServletContext().getRealPath("")
                    + File.separator + "assets"
                    + File.separator + "images"
                    + File.separator + "cars"
                    + File.separator + imageFolder;

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            Collection<Part> parts = request.getParts();
            List<String> imageUrls = new ArrayList<>();
            int imageCount = 0;

            for (Part part : parts) {
                if ("carImages".equals(part.getName()) && part.getSize() > 0) {
                    imageCount++;

                    String originalFileName = Paths.get(part.getSubmittedFileName())
                            .getFileName().toString();

                    String fileExtension = "";
                    int dotIndex = originalFileName.lastIndexOf(".");
                    if (dotIndex >= 0) {
                        fileExtension = originalFileName.substring(dotIndex);
                    }

                    String fileName = imageFolder + "_" + imageCount + fileExtension;
                    String fullFilePath = uploadPath + File.separator + fileName;
                    part.write(fullFilePath);

                    String relativeImageUrl = "assets/images/cars/" + imageFolder + "/" + fileName;
                    imageUrls.add(relativeImageUrl);
                }
            }

            if (imageUrls.isEmpty()) {
                throw new Exception("Please select at least one image!");
            }

            CarModel car = new CarModel(
                    0,
                    modelName,
                    modelYear,
                    pricePerDay,
                    seatCount,
                    fuelType,
                    transmission,
                    brandName,
                    typeName,
                    imageUrls.get(0),
                    imageFolder,
                    description,
                    status
            );

            boolean success = carService.addCarWithImages(car, imageUrls);

            if (success) {
                request.getSession().setAttribute("message", "Car added successfully!");
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
            } else {
                request.setAttribute("error", "Failed to add car!");
                request.setAttribute("car", car);
                request.getRequestDispatcher("/views/staff-cars-manager.jsp")
                        .forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/views/staff-cars-manager.jsp")
                    .forward(request, response);
        }
    }

    private void updateCar(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int carId = Integer.parseInt(request.getParameter("carId"));
            String modelName = request.getParameter("modelName");
            int modelYear = Integer.parseInt(request.getParameter("modelYear"));
            BigDecimal pricePerDay = new BigDecimal(request.getParameter("pricePerDay"));
            int seatCount = Integer.parseInt(request.getParameter("seatCount"));
            String fuelType = request.getParameter("fuelType");
            String transmission = request.getParameter("transmission");
            String brandName = request.getParameter("brandName");
            String typeName = request.getParameter("typeName");
            String description = request.getParameter("description");
            String status = request.getParameter("status");

            CarModel existingCar = carService.getCarById(carId);
            if (existingCar == null) {
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
                return;
            }

            String imageFolder = existingCar.getImageFolder();
            String imageUrl = existingCar.getImageUrl();

            Collection<Part> parts = request.getParts();
            List<String> newImageUrls = new ArrayList<>();

            String uploadPath = getServletContext().getRealPath("")
                    + File.separator + "assets"
                    + File.separator + "images"
                    + File.separator + "cars"
                    + File.separator + imageFolder;

            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            File[] existingFiles = uploadDir.listFiles();
            int startCount = (existingFiles != null) ? existingFiles.length : 0;

            for (Part part : parts) {
                if ("carImages".equals(part.getName()) && part.getSize() > 0) {
                    startCount++;

                    String originalFileName = Paths.get(part.getSubmittedFileName())
                            .getFileName().toString();

                    String fileExtension = "";
                    int dotIndex = originalFileName.lastIndexOf(".");
                    if (dotIndex >= 0) {
                        fileExtension = originalFileName.substring(dotIndex);
                    }

                    String fileName = imageFolder + "_" + startCount + fileExtension;
                    String fullFilePath = uploadPath + File.separator + fileName;
                    part.write(fullFilePath);

                    String relativeImageUrl = "assets/images/cars/" + imageFolder + "/" + fileName;
                    newImageUrls.add(relativeImageUrl);
                }
            }

            CarModel car = new CarModel(
                    carId,
                    modelName,
                    modelYear,
                    pricePerDay,
                    seatCount,
                    fuelType,
                    transmission,
                    brandName,
                    typeName,
                    imageUrl,
                    imageFolder,
                    description,
                    status
            );

            boolean success = carService.updateCarWithNewImages(car, newImageUrls);

            if (success) {
                String msg = "Car updated successfully!";
                if (!newImageUrls.isEmpty()) {
                    msg += " Added " + newImageUrls.size() + " new image(s).";
                }
                request.getSession().setAttribute("message", msg);
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
            } else {
                request.setAttribute("error", "Failed to update car.");
                request.setAttribute("car", car);
                request.getRequestDispatcher("/views/staff-cars-manager.jsp")
                        .forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("/views/staff-cars-manager.jsp")
                    .forward(request, response);
        }
    }

    private void deleteCar(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int carId = Integer.parseInt(request.getParameter("carId"));
            boolean success = carService.deleteCar(carId);

            if (success) {
                request.getSession().setAttribute("message", "Car deleted successfully!");
            } else {
                request.getSession().setAttribute("error", "Failed to delete car.");
            }

            response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Error: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
        }
    }
}