package service;

import DALs.BookingDAO;
import DALs.CarChangeRequestDAO;
import DALs.CarDAO;
import DALs.ContractDAO;
import models.BookingModel;
import models.CarChangeRequestModel;
import models.CarModel;
import models.ContractModel;
import java.util.List;
import models.BookingModel;
import models.CarChangeRequestModel;
import models.CarModel;

public class CarChangeRequestService {

    private final BookingDAO bookingDAO;
    private final CarDAO carDAO;
    private final ContractDAO contractDAO;
    private final CarChangeRequestDAO requestDAO;

    public CarChangeRequestService() {
        bookingDAO = new BookingDAO();
        carDAO = new CarDAO();
        contractDAO = new ContractDAO();
        requestDAO = new CarChangeRequestDAO();
    }

    public List<CarModel> getAvailableReplacementCars(int bookingId) {
        BookingModel booking = bookingDAO.getById(bookingId);
        if (booking == null) {
            return java.util.Collections.emptyList();
        }

        CarModel oldCar = carDAO.findById(booking.getCarId());
        if (oldCar == null) {
            return java.util.Collections.emptyList();
        }

        return carDAO.getAvailableReplacementCars(
                oldCar.getCarId(),
                oldCar.getTypeName(),
                oldCar.getPricePerDay(),
                booking.getStartDate(),
                booking.getEndDate()
        );
    }

    public boolean createStaffRequest(int bookingId, int staffId, int newCarId, String reason) {
        BookingModel booking = bookingDAO.getById(bookingId);
        if (booking == null) {
            return false;
        }

        if (!"AWAITING_PAYMENT".equalsIgnoreCase(booking.getStatus())
                && !"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            return false;
        }

        if (requestDAO.existsPendingRequest(bookingId)) {
            return false;
        }

        CarModel oldCar = carDAO.findById(booking.getCarId());
        if (oldCar == null) {
            return false;
        }

        List<CarModel> replacementCars = carDAO.getAvailableReplacementCars(
                oldCar.getCarId(),
                oldCar.getTypeName(),
                oldCar.getPricePerDay(),
                booking.getStartDate(),
                booking.getEndDate()
        );

        boolean found = false;
        for (CarModel car : replacementCars) {
            if (car.getCarId() == newCarId) {
                found = true;
                break;
            }
        }

        if (!found) {
            return false;
        }

        CarChangeRequestModel request = new CarChangeRequestModel();
        request.setBookingId(bookingId);
        request.setOldCarId(oldCar.getCarId());
        request.setNewCarId(newCarId);
        request.setRequestedBy("STAFF");
        request.setStatus("PENDING");
        request.setReason(reason);

        return requestDAO.createStaffRequest(request) > 0;
    }

    public boolean customerRespond(int requestId, int customerId, boolean accept) {
        CarChangeRequestModel request = requestDAO.getById(requestId);
        if (request == null) {
            return false;
        }

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            return false;
        }

        BookingModel booking = bookingDAO.findByIdForCarChange(request.getBookingId(), customerId);
        if (booking == null) {
            return false;
        }

        if (!accept) {
            boolean requestUpdated = requestDAO.updateStatus(requestId, "REJECTED");
            if (!requestUpdated) {
                return false;
            }

            if ("CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
                ContractModel contract = contractDAO.getContractByBookingId(booking.getBookingId());

                if (contract != null && "CREATED".equalsIgnoreCase(contract.getContractStatus())) {
                    boolean contractCancelled = contractDAO.updateContractStatus(contract.getContractId(), "CANCELLED");
                    if (!contractCancelled) {
                        return false;
                    }
                }

                boolean bookingUpdated = bookingDAO.updateStatus(booking.getBookingId(), "REFUND_PENDING");
                if (!bookingUpdated) {
                    return false;
                }
            } else if ("AWAITING_PAYMENT".equalsIgnoreCase(booking.getStatus())) {
                boolean bookingUpdated = bookingDAO.updateStatus(booking.getBookingId(), "CANCELLED");
                if (!bookingUpdated) {
                    return false;
                }
            }

            return true;
        }

        boolean valid = isReplacementStillValid(booking, request.getNewCarId());
        if (!valid) {
            requestDAO.updateStatus(requestId, "CANCELLED");
            return false;
        }

        boolean applied = applyCarChange(booking, request.getNewCarId());
        if (!applied) {
            return false;
        }

        return requestDAO.updateStatus(requestId, "APPROVED");
    }

    private boolean isReplacementStillValid(BookingModel booking, int newCarId) {
        CarModel oldCar = carDAO.findById(booking.getCarId());
        if (oldCar == null) {
            return false;
        }

        List<CarModel> replacementCars = carDAO.getAvailableReplacementCars(
                oldCar.getCarId(),
                oldCar.getTypeName(),
                oldCar.getPricePerDay(),
                booking.getStartDate(),
                booking.getEndDate()
        );

        for (CarModel car : replacementCars) {
            if (car.getCarId() == newCarId) {
                return true;
            }
        }

        return false;
    }

    private boolean applyCarChange(BookingModel booking, int newCarId) {
        int oldCarId = booking.getCarId();

        boolean bookingUpdated = bookingDAO.updateCarId(booking.getBookingId(), newCarId);
        if (!bookingUpdated) {
            return false;
        }

        if ("CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            boolean oldCarUpdated = carDAO.updateStatus(oldCarId, "AVAILABLE");
            if (!oldCarUpdated) {
                return false;
            }

            boolean newCarUpdated = carDAO.updateStatus(newCarId, "BOOKED");
            if (!newCarUpdated) {
                return false;
            }

            ContractModel contract = contractDAO.getContractByBookingId(booking.getBookingId());
            if (contract != null && "CREATED".equalsIgnoreCase(contract.getContractStatus())) {
                boolean contractUpdated = contractDAO.updateCarId(contract.getContractId(), newCarId);
                if (!contractUpdated) {
                    return false;
                }
            }
        }

        return true;
    }

    public CarChangeRequestModel getPendingRequestByBookingId(int bookingId) {
        return requestDAO.getPendingByBookingId(bookingId);
    }

    public boolean markRefundCompleted(int bookingId) {
        BookingModel booking = bookingDAO.getById(bookingId);
        if (booking == null) {
            return false;
        }

        if (!"REFUND_PENDING".equalsIgnoreCase(booking.getStatus())) {
            return false;
        }

        boolean bookingUpdated = bookingDAO.updateStatus(bookingId, "REFUNDED");
        if (!bookingUpdated) {
            return false;
        }

        boolean carUpdated = carDAO.updateStatus(booking.getCarId(), "AVAILABLE");
        if (!carUpdated) {
            return false;
        }

        return true;
    }

    public boolean rejectAndRefundByStaff(int bookingId) {
        BookingModel booking = bookingDAO.getById(bookingId);
        if (booking == null) {
            return false;
        }

        ContractModel contract = contractDAO.getContractByBookingId(bookingId);
        if (contract != null && "CREATED".equalsIgnoreCase(contract.getContractStatus())) {
            boolean contractCancelled = contractDAO.updateContractStatus(contract.getContractId(), "CANCELLED");
            if (!contractCancelled) {
                return false;
            }
        }

        boolean bookingUpdated = bookingDAO.updateStatus(bookingId, "REFUNDED");
        if (!bookingUpdated) {
            return false;
        }

        boolean carUpdated = carDAO.updateStatus(booking.getCarId(), "AVAILABLE");
        if (!carUpdated) {
            return false;
        }

        CarChangeRequestModel pending = requestDAO.getPendingByBookingId(bookingId);
        if (pending != null) {
            requestDAO.updateStatus(pending.getRequestId(), "REJECTED");
        }

        return true;
    }
}
