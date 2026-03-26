package service;

import DALs.ReportDAO;
import models.ReportModel;
import java.util.List;
import java.util.Map;

public class ReportService {

    private ReportDAO reportDAO = new ReportDAO();

    public List<ReportModel> getAllRentalReports(String startDate, String endDate) {
        return reportDAO.findAllRentalReports(startDate, endDate);
    }

    public List<ReportModel> getVehicleUsageReports(String startDate, String endDate) {
        return reportDAO.findVehicleUsageReports(startDate, endDate);
    }

    public List<ReportModel> getRevenueReports(String startDate, String endDate) {
        return reportDAO.findRevenueReports(startDate, endDate);
    }

    public Map<String, Object> getReportSummary(String startDate, String endDate) {
        return reportDAO.getReportSummary(startDate, endDate);
    }
}