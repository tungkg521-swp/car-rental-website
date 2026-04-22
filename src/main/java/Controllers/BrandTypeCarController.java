package Controllers;

import DALs.BrandDAO;
import DALs.CarTypeDAO;
import models.BrandModel;
import models.CarTypeModel;
import Utils.RoleConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import models.AccountModel;

@WebServlet(name = "BrandTypeCarController", urlPatterns = {"/admin/brand-type-cars"})
public class BrandTypeCarController extends HttpServlet {

    private final BrandDAO brandDAO = new BrandDAO();
    private final CarTypeDAO carTypeDAO = new CarTypeDAO();

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        AccountModel account = (AccountModel) session.getAttribute("ACCOUNT");
        return account != null && account.getRoleId() == RoleConstants.ADMIN;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {
            loadManagementPage(request, response);
            return;
        }

        if (action.equals("search")) {
            searchData(request, response);
            return;
        }

        loadManagementPage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        String action = request.getParameter("action");

        if ("createBrand".equals(action)) {
            createBrand(request, response);
            return;
        }

        if ("updateBrand".equals(action)) {
            updateBrand(request, response);
            return;
        }

        if ("deleteBrand".equals(action)) {
            deleteBrand(request, response);
            return;
        }

        if ("createType".equals(action)) {
            createType(request, response);
            return;
        }

        if ("updateType".equals(action)) {
            updateType(request, response);
            return;
        }

        if ("deleteType".equals(action)) {
            deleteType(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/brand-type-cars?action=list");
    }

    private void loadManagementPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<BrandModel> brandList = brandDAO.getAllBrands();
        List<CarTypeModel> typeList = carTypeDAO.getAllTypes();

        request.setAttribute("brandList", brandList);
        request.setAttribute("typeList", typeList);

        request.setAttribute("totalBrands", brandDAO.countAllBrands());
        request.setAttribute("totalTypes", carTypeDAO.countAllTypes());
        request.setAttribute("activeCatalogItems",
                brandDAO.countActiveBrands() + carTypeDAO.countActiveTypes());

        request.getRequestDispatcher("/views/brand-type-car.jsp").forward(request, response);
    }

    private void searchData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String brandKeyword = request.getParameter("brandKeyword");
        String typeKeyword = request.getParameter("typeKeyword");

        List<BrandModel> brandList = (brandKeyword != null && !brandKeyword.trim().isEmpty())
                ? brandDAO.searchBrands(brandKeyword.trim())
                : brandDAO.getAllBrands();

        List<CarTypeModel> typeList = (typeKeyword != null && !typeKeyword.trim().isEmpty())
                ? carTypeDAO.searchTypes(typeKeyword.trim())
                : carTypeDAO.getAllTypes();

        request.setAttribute("brandList", brandList);
        request.setAttribute("typeList", typeList);

        request.setAttribute("brandKeyword", brandKeyword);
        request.setAttribute("typeKeyword", typeKeyword);

        request.setAttribute("totalBrands", brandDAO.countAllBrands());
        request.setAttribute("totalTypes", carTypeDAO.countAllTypes());
        request.setAttribute("activeCatalogItems",
                brandDAO.countActiveBrands() + carTypeDAO.countActiveTypes());

        request.getRequestDispatcher("/views/brand-type-car.jsp").forward(request, response);
    }

    private void createBrand(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String brandName = request.getParameter("brandName");
        String status = request.getParameter("brandStatus");

        if (brandName == null || brandName.trim().isEmpty()) {
            request.getSession().setAttribute("error", "Brand name is required.");
            response.sendRedirect(request.getContextPath() + "/admin/brand-type-cars?action=list");
            return;
        }

        if (status == null || status.trim().isEmpty()) {
            status = "ACTIVE";
        }

        if (brandDAO.existsBrandName(brandName.trim())) {
            request.getSession().setAttribute("error", "Brand name already exists.");
            response.sendRedirect(request.getContextPath() + "/admin/brand-type-cars?action=list");
            return;
        }

        boolean success = brandDAO.insert(brandName.trim(), status.trim().toUpperCase());

        if (success) {
            request.getSession().setAttribute("success", "Brand added successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to add brand.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/brand-type-cars?action=list");
    }

    private void updateBrand(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int brandId = Integer.parseInt(request.getParameter("brandId"));
            String brandName = request.getParameter("brandName");
            String status = request.getParameter("brandStatus");

            if (brandName == null || brandName.trim().isEmpty()) {
                request.getSession().setAttribute("error", "Brand name is required.");
                response.sendRedirect(request.getContextPath() + "/admin/brand-type-cars?action=list");
                return;
            }

            if (status == null || status.trim().isEmpty()) {
                status = "ACTIVE";
            }

            if (brandDAO.existsBrandNameExceptId(brandName.trim(), brandId)) {
                request.getSession().setAttribute("error", "Brand name already exists.");
                response.sendRedirect(request.getContextPath() + "/admin/brand-type-cars?action=list");
                return;
            }
            boolean success = brandDAO.update(brandName.trim(), status.trim().toUpperCase(), brandId);

            if (success) {
                request.getSession().setAttribute("success", "Brand updated successfully.");
            } else {
                request.getSession().setAttribute("error", "Failed to update brand.");
            }

        } catch (Exception e) {
            request.getSession().setAttribute("error", "Invalid brand data.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/brand-type-cars?action=list");
    }

    private void deleteBrand(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int brandId = Integer.parseInt(request.getParameter("brandId"));
            boolean success = brandDAO.delete(brandId);

            if (success) {
                request.getSession().setAttribute("success", "Brand deleted successfully.");
            } else {
                request.getSession().setAttribute("error", "Failed to delete brand.");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Invalid brand id.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/brand-type-cars?action=list");
    }

    private void createType(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String typeName = request.getParameter("typeName");
        String status = request.getParameter("typeStatus");

        if (typeName == null || typeName.trim().isEmpty()) {
            request.getSession().setAttribute("error", "Type name is required.");
            response.sendRedirect(request.getContextPath() + "/admin/brand-type-cars?action=list");
            return;
        }

        if (status == null || status.trim().isEmpty()) {
            status = "ACTIVE";
        }

        if (carTypeDAO.existsTypeName(typeName.trim())) {
            request.getSession().setAttribute("error", "Type car name already exists.");
            response.sendRedirect(request.getContextPath() + "/admin/brand-type-cars?action=list");
            return;
        }

        boolean success = carTypeDAO.insert(typeName, status);

        if (success) {
            request.getSession().setAttribute("success", "Type car added successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to add type car.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/brand-type-cars?action=list");
    }

    private void updateType(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int typeId = Integer.parseInt(request.getParameter("typeId"));
            String typeName = request.getParameter("typeName");
            String status = request.getParameter("typeStatus");

            if (typeName == null || typeName.trim().isEmpty()) {
                request.getSession().setAttribute("error", "Type name is required.");
                response.sendRedirect(request.getContextPath() + "/admin/brand-type-cars?action=list");
                return;
            }

            if (status == null || status.trim().isEmpty()) {
                status = "ACTIVE";
            }

            if (carTypeDAO.existsTypeNameExceptId(typeName.trim(), typeId)) {
                request.getSession().setAttribute("error", "Type car name already exists.");
                response.sendRedirect(request.getContextPath() + "/admin/brand-type-cars?action=list");
                return;
            }

            CarTypeModel type = new CarTypeModel();
            type.setTypeId(typeId);
            type.setTypeName(typeName.trim());
            type.setStatus(status.trim().toUpperCase());

            boolean success = carTypeDAO.update(typeName, status, typeId);

            if (success) {
                request.getSession().setAttribute("success", "Type car updated successfully.");
            } else {
                request.getSession().setAttribute("error", "Failed to update type car.");
            }

        } catch (Exception e) {
            request.getSession().setAttribute("error", "Invalid type car data.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/brand-type-cars?action=list");
    }

    private void deleteType(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int typeId = Integer.parseInt(request.getParameter("typeId"));
            boolean success = carTypeDAO.delete(typeId);

            if (success) {
                request.getSession().setAttribute("success", "Type car deleted successfully.");
            } else {
                request.getSession().setAttribute("error", "Failed to delete type car.");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Invalid type car id.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/brand-type-cars?action=list");
    }
}
