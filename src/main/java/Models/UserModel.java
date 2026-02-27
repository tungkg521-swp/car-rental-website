package models;

public class UserModel {

    private int accountId;
    private Integer customerId; // có thể null (staff/admin)
    private String role;         // CUSTOMER / STAFF / ADMIN
    private String status;
    private String email;

    public UserModel() {
    }

    // ===== GETTERS & SETTERS =====

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ===== HELPER METHODS (RẤT TIỆN) =====

    public boolean isCustomer() {
        return "CUSTOMER".equalsIgnoreCase(role);
    }

    public boolean isStaff() {
        return "STAFF".equalsIgnoreCase(role);
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status);
    }
}
