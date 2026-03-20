// reports.js - ĐÃ SỬA HOÀN TOÀN (dùng window.contextPath)

function loadReportsOverview() {
    const contentArea = document.querySelector('.staff-content');
    if (!contentArea) return;

    contentArea.innerHTML = `
        <h1 class="dashboard-title">Reports Overview</h1>
        <div class="dashboard-cards" style="display: flex; flex-wrap: wrap; gap: 20px; margin-top: 20px;">
            <div class="card report-card" onclick="loadReport('rental')" style="cursor: pointer; transition: all 0.3s; width: 300px; height: 180px; display: flex; flex-direction: column; justify-content: center; align-items: center; text-align: center;">
                <h3>Rental Reports</h3>
                <p style="margin-top: 10px;">Xem lịch sử chuyến thuê, khách hàng, xe, trạng thái, phí phụ...</p>
            </div>

            <div class="card report-card" onclick="loadReport('revenue')" style="cursor: pointer; transition: all 0.3s; width: 300px; height: 180px; display: flex; flex-direction: column; justify-content: center; align-items: center; text-align: center;">
                <h3>Revenue Reports</h3>
                <p style="margin-top: 10px;">Doanh thu tổng, theo xe, theo thời gian, lợi nhuận ròng...</p>
            </div>

            <div class="card report-card" onclick="loadReport('usage')" style="cursor: pointer; transition: all 0.3s; width: 300px; height: 180px; display: flex; flex-direction: column; justify-content: center; align-items: center; text-align: center;">
                <h3>Vehicle Usage Reports</h3>
                <p style="margin-top: 10px;">Tỷ lệ sử dụng xe, km chạy, thời gian nghỉ, bảo dưỡng...</p>
            </div>
        </div>
    `;

    document.querySelectorAll('.sidebar-menu li').forEach(li => li.classList.remove('active'));
    const reportsLi = document.querySelector('.sidebar-menu a[onclick="loadReportsOverview(); return false;"]')?.closest('li');
    if (reportsLi) reportsLi.classList.add('active');
}

function loadReport(type) {
    const contentArea = document.querySelector('.staff-content');
    if (!contentArea) return;

    let url = '';
    if (type === 'rental') {
        url = window.contextPath + '/admin/rental-report-content';
    } else if (type === 'revenue') {
        url = window.contextPath + '/admin/revenue-report-content';
    } else if (type === 'usage') {
        url = window.contextPath + '/admin/usage-report-content';
    } else {
        contentArea.innerHTML = '<p style="color:red">Loại báo cáo không hợp lệ.</p>';
        return;
    }

    contentArea.innerHTML = '<p style="text-align:center; padding: 50px;">Đang tải báo cáo...</p>';

    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error('Lỗi tải dữ liệu: ' + response.status);
            return response.text();
        })
        .then(html => {
            contentArea.innerHTML = html;
        })
        .catch(error => {
            contentArea.innerHTML = '<p style="color:red; text-align:center; padding: 50px;">Lỗi khi tải báo cáo: ' + error.message + '</p>';
        });
}

// Hover effect
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.report-card').forEach(card => {
        card.addEventListener('mouseenter', () => {
            card.style.transform = 'scale(1.05)';
            card.style.boxShadow = '0 8px 20px rgba(0,0,0,0.2)';
        });
        card.addEventListener('mouseleave', () => {
            card.style.transform = 'scale(1)';
            card.style.boxShadow = 'none';
        });
    });
});