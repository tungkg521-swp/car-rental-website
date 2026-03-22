package service;

import DALs.ReportDAO;
import models.ReportModel;
import java.util.List;

public class ReportService {

    private ReportDAO reportDAO = new ReportDAO();

    public List<ReportModel> getAllRentalReports() {
        return reportDAO.findAllRentalReports();
    }

    public List<ReportModel> getVehicleUsageReports() {
        return reportDAO.findVehicleUsageReport();
    }

    public List<ReportModel> getRevenueReports() {
        return reportDAO.findRevenueReports();
    }
}
