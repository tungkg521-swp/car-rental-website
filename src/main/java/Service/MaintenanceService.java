/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
        return maintenanceDAO.add(m);
    }

    public boolean updateMaintenance(MaintenanceModel m) {
        return maintenanceDAO.update(m);
    }

}