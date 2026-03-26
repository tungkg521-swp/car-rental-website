// report.js - Trip Report với Popup Drill-down + KPI ngang

let currentTripData = [];

// ==================== LOAD REPORTS OVERVIEW ====================
function loadReportsOverview() {
    const contentArea = document.querySelector('.staff-content');
    
    contentArea.innerHTML = `
        <h1 class="dashboard-title">Report Dashboard</h1>
        
        <div class="filter-bar">
            <label>Từ ngày: <input type="date" id="startDate"></label>
            <label>Đến ngày: <input type="date" id="endDate"></label>
            <button onclick="applyDashboardFilter()" class="btn btn-primary">Áp dụng</button>
            <button onclick="clearDashboardFilter()" class="btn btn-secondary">Xóa</button>
        </div>

        <!-- KPI Cards -->
        <div id="kpiCards" class="dashboard-kpi"></div>

        <div class="dashboard-grid">
            <div class="card">
                <h2>⚙️ VEHICLE UTILIZATION</h2>
                <p class="card-subtitle">Tỷ lệ sử dụng xe (Toàn bộ)</p>
                <div class="chart-container"><canvas id="utilPieChart"></canvas></div>
                <div class="chart-container"><canvas id="utilBarChart"></canvas></div>
            </div>

            <div class="card">
                <h2>💰 REVENUE REPORT</h2>
                <p class="card-subtitle">Doanh thu theo thời gian</p>
                <div class="chart-container"><canvas id="revenueLineChart"></canvas></div>
            </div>
        </div>

        <div class="card">
            <h2 onclick="showAllTrips()" class="clickable-title">🚗 TRIP REPORT (Báo cáo chuyến đi)</h2>
            <div class="chart-container" style="height: 320px;">
                <canvas id="tripBarChart"></canvas>
            </div>
        </div>

        <!-- Modal -->
        <div id="tripModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2 id="modalTitle">Chi tiết chuyến đi</h2>
                    <span onclick="closeModal()" class="modal-close">×</span>
                </div>
                
                <table class="report-table booking-table">
                    <thead>
                        <tr>
                            <th>Mã Booking/HĐ</th>
                            <th>Khách hàng</th>
                            <th>Biển số</th>
                            <th>Nhận xe</th>
                            <th>Trả xe</th>
                            <th>Số ngày</th>
                            <th>Tổng tiền</th>
                            <th>Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody id="modalTableBody"></tbody>
                </table>
            </div>
        </div>
    `;

    loadDefaultDashboard();
}

// ==================== LOAD KPI CARDS ====================
async function loadKPICards(startDate = '', endDate = '') {
    let url = `${window.contextPath}/admin/report-summary`;
    
    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    if (params.toString()) url += '?' + params.toString();

    try {
        const res = await fetch(url);
        const data = await res.json();

        const kpiHTML = `
            <div class="kpi-card">
                <div class="kpi-title">Tổng Doanh Thu</div>
                <div class="kpi-value revenue">${Number(data.totalRevenue || 0).toLocaleString('vi-VN')} ₫</div>
            </div>
            <div class="kpi-card">
                <div class="kpi-title">Tổng Chuyến Thuê</div>
                <div class="kpi-value trips">${data.totalTrips || 0}</div>
            </div>
            <div class="kpi-card">
                <div class="kpi-title">Tỷ lệ Sử dụng Xe</div>
                <div class="kpi-value utilization">${parseFloat(data.utilization || 0).toFixed(2)}%</div>
            </div>
        `;

        document.getElementById('kpiCards').innerHTML = kpiHTML;
        
    } catch (e) {
        console.error('Lỗi load KPI:', e);
    }
}

// ==================== TRIP BAR CHART + DETAIL ====================
async function loadTripBarChart(startDate = '', endDate = '') {
    let url = `${window.contextPath}/admin/trip-detail`;
    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    if (params.toString()) url += '?' + params.toString();

    try {
        const res = await fetch(url);
        const trips = await res.json();

        const dateMap = new Map();

        trips.forEach(t => {
            const dateStr = t.startDate 
                ? t.startDate.split('T')[0] 
                : (t.start_date ? t.start_date.split('T')[0] : 'Không xác định');

            if (!dateMap.has(dateStr)) dateMap.set(dateStr, []);

            dateMap.get(dateStr).push({
                bookingId: t.contractId || t.bookingId || 'N/A',
                customer: t.customerName || 'N/A',
                plate: t.plateNumber || 'N/A',
                startDate: dateStr,
                endDate: t.endDate ? t.endDate.split('T')[0] : '',
                days: t.rentalDays || 0,
                amount: Number(t.totalPrice || 0).toLocaleString('vi-VN') + ' ₫',
                status: t.status || 'N/A'
            });
        });

        currentTripData = Array.from(dateMap.entries())
            .map(([date, list]) => ({ 
                date: date, 
                count: list.length, 
                trips: list 
            }))
            .sort((a, b) => new Date(a.date) - new Date(b.date));

        renderTripBarChart();
        
    } catch (e) {
        console.error('Lỗi load trip chart:', e);
    }
}

function renderTripBarChart() {
    const ctx = document.getElementById('tripBarChart');
    if (!ctx) return;

    if (window.tripChartInstance) window.tripChartInstance.destroy();

    window.tripChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: currentTripData.map(item => item.date),
            datasets: [{
                label: 'Số chuyến',
                data: currentTripData.map(item => item.count),
                backgroundColor: '#3498db',
                borderColor: '#2980b9',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            onClick: (event, elements) => {
                if (elements.length > 0) {
                    const index = elements[0].index;
                    const selected = currentTripData[index];
                    showTripModal(selected.date, selected.trips);
                }
            },
            scales: {
                y: { beginAtZero: true, ticks: { stepSize: 1 } }
            }
        }
    });
}

// ==================== MODAL ====================
function showTripModal(date, trips) {
    const title = (date === 'TẤT CẢ CHUYẾN') 
        ? 'TẤT CẢ CHUYẾN' 
        : `Chi tiết chuyến ngày ${formatVN(date)}`;

    document.getElementById('modalTitle').textContent = title;
    
    const tbody = document.getElementById('modalTableBody');
    tbody.innerHTML = trips.map(t => `
        <tr>
            <td>${t.bookingId}</td>
            <td>${t.customer}</td>
            <td>${t.plate}</td>
            <td>${formatVN(t.startDate)}</td>
            <td>${formatVN(t.endDate)}</td>
            <td>${t.days}</td>
            <td>${t.amount}</td>
            <td>${t.status}</td>
        </tr>
    `).join('');

    document.getElementById('tripModal').style.display = 'flex';
}

// Hàm format ngày sang dd/MM/yyyy (rất ngắn)
function formatVN(d) {
    if (!d) return '';
    const date = new Date(d);
    return isNaN(date.getTime()) ? d : date.toLocaleDateString('vi-VN');
}

function closeModal() {
    document.getElementById('tripModal').style.display = 'none';
}

// ==================== SHOW ALL TRIPS ====================
async function showAllTrips() {
    const start = document.getElementById('startDate').value;
    const end = document.getElementById('endDate').value;
    
    let url = `${window.contextPath}/admin/trip-detail`;
    const params = new URLSearchParams();
    if (start) params.append('startDate', start);
    if (end) params.append('endDate', end);
    if (params.toString()) url += '?' + params.toString();

    try {
        const res = await fetch(url);
        const allTrips = await res.json();

        const formattedTrips = allTrips.map(t => ({
            bookingId: t.contractId || t.bookingId || 'N/A',
            customer: t.customerName || 'N/A',
            plate: t.plateNumber || 'N/A',
            startDate: t.startDate ? t.startDate.split('T')[0] : '',
            endDate: t.endDate ? t.endDate.split('T')[0] : '',
            days: t.rentalDays || 0,
            amount: Number(t.totalPrice || 0).toLocaleString('vi-VN') + ' ₫',
            status: t.status || 'N/A'
        }));

        showTripModal('TẤT CẢ CHUYẾN', formattedTrips);
    } catch (e) {
        console.error(e);
        alert('Không thể tải dữ liệu chuyến đi. Vui lòng thử lại!');
    }
    }

// ==================== FILTER ====================
async function applyDashboardFilter() {
    const start = document.getElementById('startDate').value;
    const end = document.getElementById('endDate').value;
    
    await Promise.all([
        loadKPICards(start, end),
        loadTripBarChart(start, end)
    ]);
}

function clearDashboardFilter() {
    document.getElementById('startDate').value = '';
    document.getElementById('endDate').value = '';
    loadDefaultDashboard();
}

async function loadDefaultDashboard() {
    await Promise.all([
        loadKPICards('', ''),
        loadTripBarChart('', '')
    ]);
}