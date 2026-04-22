package Controllers;

import DALs.CarDAO;
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

@WebServlet("/staff/cars")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class StaffCarController extends HttpServlet {

    private final CarDAO carDAO = new CarDAO();

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("getImages".equals(action)) {
            try {
                int carId = Integer.parseInt(request.getParameter("id"));

                List<String> images = carDAO.getCarImagesByCarId(carId);

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
            List<CarModel> carList = carDAO.findAllCars();
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
                carList = carDAO.searchCars(keyword);
            } else {
                carList = carDAO.findAllCars();
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
            CarModel car = carDAO.findById(carId);

            request.setAttribute("car", car);
            request.getRequestDispatcher("/views/staff-cars-manager.jsp")
                    .forward(request, response);
            return;
        }

        if (action.equals("add")) {
            if (!isAdmin(request)) {
                request.getSession().setAttribute("error", "Chỉ quản trị viên mới có thể thêm xe.");
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
                return;
            }

            request.setAttribute("brandList", carDAO.getAllBrandNames());
            request.setAttribute("typeList", carDAO.getAllTypeNames());

            request.getRequestDispatcher("/views/staff-cars-manager.jsp")
                    .forward(request, response);
            return;
        }

        if (action.equals("edit")) {
            if (!isAdmin(request)) {
                request.getSession().setAttribute("error", "Chỉ quản trị viên mới có thể cập nhật xe.");
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
                return;
            }

            int carId = Integer.parseInt(request.getParameter("id"));
            CarModel car = carDAO.findById(carId);

            request.setAttribute("car", car);
            request.setAttribute("brandList", carDAO.getAllBrandNames());
            request.setAttribute("typeList", carDAO.getAllTypeNames());

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
                request.getSession().setAttribute("error", "Chỉ quản trị viên mới có thể thêm xe.");
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
                return;
            }
            createCar(request, response);
            return;
        }

        if ("update".equals(action)) {
            if (!isAdmin(request)) {
                request.getSession().setAttribute("error", "Chỉ quản trị viên mới có thể cập nhật xe.");
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
                return;
            }
            updateCar(request, response);
            return;
        }

        if ("delete".equals(action)) {
            if (!isAdmin(request)) {
                request.getSession().setAttribute("error", "Chỉ quản trị viên mới có thể xóa xe.");
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
                return;
            }
            deleteCar(request, response);
            return;
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
            String plateNumber = request.getParameter("plateNumber");
            plateNumber = plateNumber != null ? plateNumber.trim() : "";

            if (carDAO.existsPlateNumber(plateNumber)) {
                request.getSession().setAttribute("error",
                        "Biển số xe đã tồn tại. Vui lòng nhập biển số khác.");
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=add");
                return;
            }

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
                throw new Exception("Vui lòng chọn ít nhất một ảnh!");
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
                    status,
                    plateNumber
            );

            if (car == null || imageUrls == null || imageUrls.isEmpty()) {
                request.setAttribute("error", "Vui lòng chọn ít nhất một ảnh!");
                request.setAttribute("brandList", carDAO.getAllBrandNames());
                request.setAttribute("typeList", carDAO.getAllTypeNames());
                request.getRequestDispatcher("/views/staff-cars-manager.jsp").forward(request, response);
                return;
            }

            boolean success = carDAO.addCarWithImages(car, imageUrls);

            if (success) {
                request.getSession().setAttribute("message", "Thêm xe thành công!");
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
            } else {
                request.setAttribute("error", "Thêm xe thất bại!");
                request.setAttribute("car", car);
                request.getRequestDispatcher("/views/staff-cars-manager.jsp")
                        .forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
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
            String plateNumber = request.getParameter("plateNumber");
            plateNumber = plateNumber != null ? plateNumber.trim() : "";

            if (carDAO.existsPlateNumberExceptId(plateNumber, carId)) {
                request.getSession().setAttribute("error",
                        "Biển số xe đã tồn tại. Vui lòng nhập biển số khác.");
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=edit&id=" + carId);
                return;
            }

            CarModel existingCar = carDAO.findById(carId);

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
                    status,
                    plateNumber
            );

            if (car == null) {
                request.setAttribute("error", "Dữ liệu xe không hợp lệ.");
                request.getRequestDispatcher("/views/staff-cars-manager.jsp").forward(request, response);
                return;
            }

            boolean success = carDAO.updateCarWithNewImages(car, newImageUrls);

            if (success) {
                String msg = "Cập nhật xe thành công!";
                if (!newImageUrls.isEmpty()) {
                    msg += " Đã thêm " + newImageUrls.size() + " ảnh mới.";
                }
                request.getSession().setAttribute("message", msg);
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
            } else {
                request.setAttribute("error", "Cập nhật xe thất bại.");
                request.setAttribute("car", car);
                request.getRequestDispatcher("/views/staff-cars-manager.jsp")
                        .forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            request.getRequestDispatcher("/views/staff-cars-manager.jsp")
                    .forward(request, response);
        }
    }

    private void deleteCar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String carIdRaw = request.getParameter("carId");

        try {
            int carId = Integer.parseInt(carIdRaw);

            if (carDAO.isCarInMaintenanceStatus(carId) || carDAO.hasOpenMaintenanceRecord(carId) || carDAO.isCarUnderMaintenance(carId)) {
                session.setAttribute("error",
                        "Không thể xóa xe này vì xe đang trong quá trình bảo trì.");
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
                return;
            }

            if (carDAO.hasActiveBooking(carId)) {
                session.setAttribute("error",
                        "Không thể xóa xe này vì xe đang được thuê hoặc có đơn đặt xe đang hoạt động.");
                response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
                return;
            }

            boolean deleted = carDAO.deleteCar(carId);

            if (deleted) {
                session.setAttribute("message", "Xóa xe thành công.");
            } else {
                session.setAttribute("error", "Xóa xe thất bại.");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("error", "Mã xe không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Đã xảy ra lỗi khi xóa xe.");
        }

        response.sendRedirect(request.getContextPath() + "/staff/cars?action=list");
    }
}
