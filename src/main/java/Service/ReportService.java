package service;

import DALs.ReportDAO;
import models.RentalReportModel;
import java.util.List;

public class ReportService {

    private ReportDAO reportDAO = new ReportDAO();

    public List<RentalReportModel> getAllRentalReports() {
        return reportDAO.findAllRentalReports();
    }
}