package service;

import DALs.ReportDAO;
import models.ReportModel;
import java.util.List;
import java.util.Map;

public class ReportService {

    private ReportDAO reportDAO = new ReportDAO();

    // Rental Report (Trip Report)
    public List<ReportModel> getAllRentalReports(String startDate, String endDate) {
        return reportDAO.findAllRentalReports(startDate, endDate);
    }

    // Vehicle Usage Report
    public List<ReportModel> getVehicleUsageReports(String startDate, String endDate) {
        return reportDAO.findVehicleUsageReports(startDate, endDate);  // Sửa tên method cho đúng
    }

    // Revenue Detail Report
    public List<ReportModel> getRevenueReports(String startDate, String endDate) {
        return reportDAO.findRevenueReports(startDate, endDate);
    }

    // Revenue Chart 
    public List<ReportModel> getRevenueByDate(String startDate, String endDate) {
        return reportDAO.findRevenueByDate(startDate, endDate);
    }

    // KPI Summary
    public Map<String, Object> getReportSummary(String startDate, String endDate) {
        return reportDAO.getReportSummary(startDate, endDate);
    }
    // Vehicle Utilization 

    public Map<String, Object> getVehicleUtilization(String startDate, String endDate) {
        return reportDAO.getVehicleUtilization(startDate, endDate);
    }
}
