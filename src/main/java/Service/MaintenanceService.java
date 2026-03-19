package service;

import DALs.MaintenanceDAO;
import DALs.CarDAO;
import models.MaintenanceModel;
import java.util.List;

public class MaintenanceService {

    private final MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
    private final CarDAO carDAO = new CarDAO();

    public List<MaintenanceModel> getAllMaintenances() {
        return maintenanceDAO.findAll();
    }

    public MaintenanceModel getMaintenanceById(int id) {
        return maintenanceDAO.findById(id);
    }

    public boolean addMaintenance(MaintenanceModel m) {
        boolean success = maintenanceDAO.add(m);
        if (success) {
            carDAO.updateCarStatus(m.getCarId(), "MAINTENANCE");
        }
        return success;
    }

    public boolean updateMaintenance(MaintenanceModel m) {
        boolean success = maintenanceDAO.update(m);
        if (success) {
            if ("COMPLETED".equalsIgnoreCase(m.getStatus()) || "CANCELLED".equalsIgnoreCase(m.getStatus())) {
                carDAO.updateCarStatus(m.getCarId(), "AVAILABLE");
            } else {
                carDAO.updateCarStatus(m.getCarId(), "MAINTENANCE");
            }
        }
        return success;
    }

    // ===== THÊM DELETE =====
        public boolean deleteMaintenance(int maintenanceId) {
        MaintenanceModel m = maintenanceDAO.findById(maintenanceId);
        if (m == null) return false;

        boolean success = maintenanceDAO.delete(maintenanceId);
        if (success) {
            carDAO.updateCarStatus(m.getCarId(), "AVAILABLE");   // ← Trả xe về AVAILABLE khi xóa
        }
        return success;
    }
}