// report.js - FULL VERSION (ĐÃ SỬA MODAL + REVENUE + BAR CHART)

let currentTripData = [];
let tripChartInstance = null;
let revenueChartInstance = null;
let utilizationPieChart = null;
let utilizationBarChart = null;

function loadReportsOverview() {
    const contentArea = document.querySelector('.staff-content');
    
    contentArea.innerHTML = `
        <h1 class="dashboard-title">Report Dashboard</h1>
        
        <div class="filter-bar">
            <label>Từ ngày: <input type="date" id="startDate"></label>
            <label>Đến ngày: <input type="date" id="endDate"></label>
            <button onclick="applyDashboardFilter()" class="btn btn-primary">Áp dụng</button>
            <button onclick="clearDashboardFilter()" class="btn btn-secondary">Xóa lọc</button>
        </div>

        <div id="kpiCards" class="dashboard-kpi"></div>

        <div class="dashboard-grid">
            <div class="card">
                <h2>💰 DOANH THU THEO THỜI GIAN</h2>
                <div class="chart-container" style="height: 280px;">
                    <canvas id="revenueLineChart"></canvas>
                </div>
            </div>

            <div class="card">
                <h2>⚙️ TỶ LỆ SỬ DỤNG XE</h2>
                <div style="display: flex; gap: 20px; flex-wrap: wrap;">
                    <div style="flex: 1; min-width: 280px;">
                        <h3 style="text-align:center; margin: 10px 0;">Phân bổ trạng thái xe</h3>
                        <div class="chart-container" style="height: 220px;">
                            <canvas id="utilPieChart"></canvas>
                        </div>
                    </div>
                    <div style="flex: 1; min-width: 280px;">
                        <h3 style="text-align:center; margin: 10px 0;">Top xe sử dụng nhiều (%)</h3>
                        <div class="chart-container" style="height: 220px;">
                            <canvas id="utilBarChart"></canvas>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="card">
            <h2 onclick="showAllTrips()" class="clickable-title">🚗 BÁO CÁO CHUYẾN THUÊ</h2>
            <div class="chart-container" style="height: 340px;">
                <canvas id="tripBarChart"></canvas>
            </div>
        </div>

        <!-- Modal Chi tiết chuyến -->
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

// ==================== KPI ====================
async function loadKPICards(startDate = '', endDate = '') {
    let url = `${window.contextPath}/admin/report-summary`;
    if (startDate || endDate) url += `?startDate=${startDate}&endDate=${endDate}`;

    try {
        const data = await (await fetch(url)).json();
        document.getElementById('kpiCards').innerHTML = `
            <div class="kpi-card"><div class="kpi-title">Tổng Doanh Thu</div><div class="kpi-value revenue">${Number(data.totalRevenue || 0).toLocaleString('vi-VN')} ₫</div></div>
            <div class="kpi-card"><div class="kpi-title">Tổng Chuyến Thuê</div><div class="kpi-value trips">${data.totalTrips || 0}</div></div>
            <div class="kpi-card"><div class="kpi-title">Tỷ lệ Sử dụng Xe</div><div class="kpi-value utilization">${parseFloat(data.utilization || 0).toFixed(1)}%</div></div>
        `;
    } catch (e) { console.error('Lỗi KPI:', e); }
}

// ==================== TRIP CHART ====================
async function loadTripBarChart(startDate = '', endDate = '') {
    let url = `${window.contextPath}/admin/trip-detail`;
    if (startDate || endDate) url += `?startDate=${startDate}&endDate=${endDate}`;

    try {
        const trips = await (await fetch(url)).json();
        const dateMap = new Map();
        trips.forEach(t => {
            const dateStr = t.startDate ? t.startDate.split('T')[0] : 'N/A';
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
            .map(([date, list]) => ({date, count: list.length, trips: list}))
            .sort((a,b) => new Date(a.date) - new Date(b.date));

        renderTripBarChart();
    } catch (e) { console.error(e); }
}

function renderTripBarChart() {
    const ctx = document.getElementById('tripBarChart');
    if (!ctx) return;
    if (tripChartInstance) tripChartInstance.destroy();

    tripChartInstance = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: currentTripData.map(item => item.date),
            datasets: [{ label: 'Số chuyến thuê', data: currentTripData.map(item => item.count), backgroundColor: '#3498db' }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            onClick: (event, elements) => {
                if (elements.length > 0) {
                    const selected = currentTripData[elements[0].index];
                    showTripModal(selected.date, selected.trips);
                }
            },
            scales: { y: { beginAtZero: true } }
        }
    });
}

// ==================== REVENUE CHART ====================
async function loadRevenueChart(startDate = '', endDate = '') {
    let url = `${window.contextPath}/admin/revenue-chart`;
    if (startDate || endDate) url += `?startDate=${startDate}&endDate=${endDate}`;

    try {
        const data = await (await fetch(url)).json();
        if (!data || data.length === 0) {
            showNoRevenueData();
            return;
        }
        renderRevenueLineChart(data);
    } catch (e) {
        console.error('Lỗi Revenue Chart:', e);
        showNoRevenueData();
    }
}

function renderRevenueLineChart(data) {
    const ctx = document.getElementById('revenueLineChart');
    if (!ctx) return;
    if (revenueChartInstance) revenueChartInstance.destroy();

    const labels = data.map(item => {
        const d = new Date(item.revenueDate || item.revenue_date);
        return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' });
    });

    const revenues = data.map(item => Number(item.totalPrice || item.daily_revenue || 0));

    revenueChartInstance = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Doanh thu (₫)',
                data: revenues,
                borderColor: '#27ae60',
                backgroundColor: 'rgba(39, 174, 96, 0.15)',
                tension: 0.4,
                fill: true,
                borderWidth: 3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: true, position: 'top' },
                tooltip: { callbacks: { label: ctx => ctx.raw.toLocaleString('vi-VN') + ' ₫' }}
            },
            scales: {
                y: { beginAtZero: true, ticks: { callback: value => (value / 1000000) + 'M' }}
            }
        }
    });
}

function showNoRevenueData() {
    const canvas = document.getElementById('revenueLineChart');
    if (!canvas) return;
    const container = canvas.parentElement;
    if (container) {
        container.innerHTML = `<div style="height:280px;display:flex;align-items:center;justify-content:center;color:#888;font-style:italic;">
            Không có dữ liệu doanh thu trong khoảng thời gian này
        </div>`;
    }
}

// ==================== VEHICLE UTILIZATION ====================
async function loadVehicleUtilization(startDate = '', endDate = '') {
    let url = `${window.contextPath}/admin/vehicle-utilization`;
    if (startDate || endDate) url += `?startDate=${startDate}&endDate=${endDate}`;

    try {
        const data = await (await fetch(url)).json();
        renderUtilizationCharts(data, startDate, endDate);
    } catch (e) { console.error(e); }
}

function renderUtilizationCharts(data, startDate = '', endDate = '') {
    const pieData = data.pieData || {};
    const barData = data.barData || [];

    // Pie Chart
    const pieCtx = document.getElementById('utilPieChart');
    if (pieCtx) {
        if (utilizationPieChart) utilizationPieChart.destroy();
        utilizationPieChart = new Chart(pieCtx, {
            type: 'pie',
            data: {
                labels: ['Đang thuê', 'Rảnh', 'Bảo trì'],
                datasets: [{ data: [pieData.rented||0, pieData.available||0, pieData.maintenance||0], backgroundColor: ['#e74c3c','#2ecc71','#f1c40f'] }]
            },
            options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } }}
        });
    }

    // Bar Chart - Chỉ %
    const barCtx = document.getElementById('utilBarChart');
    if (barCtx && barData.length > 0) {
        if (utilizationBarChart) utilizationBarChart.destroy();

        let periodDays = 30;
        if (startDate && endDate) {
            const s = new Date(startDate);
            const e = new Date(endDate);
            periodDays = Math.max(1, Math.ceil((e - s) / 86400000) + 1);
        }

        const labels = barData.map(item => item.plateNumber);
        const percentData = barData.map(item => {
            const days = Number(item.rentalDays || 0);
            return periodDays > 0 ? Math.round((days / periodDays) * 1000) / 10 : 0;
        });

        utilizationBarChart = new Chart(barCtx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{ label: 'Tỷ lệ sử dụng (%)', data: percentData, backgroundColor: '#3498db' }]
            },
            options: {
                indexAxis: 'y',
                responsive: true,
                maintainAspectRatio: false,
                scales: { x: { beginAtZero: true, max: 100, ticks: { callback: v => v + '%' } }},
                plugins: { tooltip: { callbacks: { label: ctx => ctx.raw + '%' } }}
            }
        });
    }
}

// ==================== MODAL FUNCTIONS (ĐÃ SỬA - KHÔNG COMMENT) ====================
function showTripModal(date, trips) {
    const title = (date === 'TẤT CẢ CHUYẾN') 
        ? 'TẤT CẢ CHUYẾN THUÊ' 
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

function formatVN(d) {
    if (!d) return '';
    const date = new Date(d);
    return isNaN(date.getTime()) ? d : date.toLocaleDateString('vi-VN');
}

function closeModal() {
    document.getElementById('tripModal').style.display = 'none';
}

async function showAllTrips() {
    const start = document.getElementById('startDate').value;
    const end = document.getElementById('endDate').value;
    let url = `${window.contextPath}/admin/trip-detail`;
    if (start || end) url += `?startDate=${start}&endDate=${end}`;

    try {
        const allTrips = await (await fetch(url)).json();
        const formatted = allTrips.map(t => ({
            bookingId: t.contractId || t.bookingId || 'N/A',
            customer: t.customerName || 'N/A',
            plate: t.plateNumber || 'N/A',
            startDate: t.startDate ? t.startDate.split('T')[0] : '',
            endDate: t.endDate ? t.endDate.split('T')[0] : '',
            days: t.rentalDays || 0,
            amount: Number(t.totalPrice || 0).toLocaleString('vi-VN') + ' ₫',
            status: t.status || 'N/A'
        }));
        showTripModal('TẤT CẢ CHUYẾN', formatted);
    } catch (e) {
        console.error(e);
        alert('Không thể tải dữ liệu chuyến đi!');
    }
}

// ==================== FILTER ====================
async function applyDashboardFilter() {
    const start = document.getElementById('startDate').value;
    const end = document.getElementById('endDate').value;
    
    await Promise.all([
        loadKPICards(start, end),
        loadTripBarChart(start, end),
        loadRevenueChart(start, end),
        loadVehicleUtilization(start, end)
    ]);
}

function clearDashboardFilter() {
    document.getElementById('startDate').value = '';
    document.getElementById('endDate').value = '';
    loadDefaultDashboard();
}

async function loadDefaultDashboard() {
    await Promise.all([
        loadKPICards(),
        loadTripBarChart(),
        loadRevenueChart(),
        loadVehicleUtilization()
    ]);
}