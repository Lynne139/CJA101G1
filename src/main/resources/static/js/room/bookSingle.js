
let currentDate = new Date();
let selectedCheckIn = null;
let selectedCheckOut = null;
let selectedPackagePrice = 0;
let inventoryMap = {};
const roomBasePrice = parseInt(document.querySelector("meta[name='roomTypePrice']").content);
const roomTypeId = document.querySelector("meta[name='roomTypeId']").content;

//日曆顯示庫存
document.addEventListener("DOMContentLoaded", function() {
	loadInventoryMap();
	// 初始化時不調用 updateGuestOptions()，讓下拉選單保持初始狀態
	initializeDropdowns();
});

function initializeDropdowns() {
	// 初始化間數下拉選單
	const roomSelect = document.getElementById("roomCount");
	roomSelect.innerHTML = '<option disabled selected>請選擇入住日期</option>';
	
	// 初始化人數下拉選單
	const guestSelect = document.getElementById("guestCount");
	guestSelect.innerHTML = '<option disabled selected>請先選擇房間數量</option>';
}

function loadInventoryMap() {
	// 取得當月庫存

	const startDate = new Date();
	const end = `2099-12-31`;
	//	const endDate = new Date();
	//	endDate.setDate(startDate.getDate() + 30); // 往後30天

	const start = `${startDate.getFullYear()}-${String(startDate.getMonth() + 1).padStart(2, '0')}-${String(startDate.getDate()).padStart(2, '0')}`;
	//	const end = `${endDate.getFullYear()}-${String(endDate.getMonth() + 1).padStart(2, '0')}-${String(endDate.getDate()).padStart(2, '0')}`;

	fetch(`/roomtype/${roomTypeId}/inventory-map?start=${start}&end=${end}`)
		.then(res => res.json())
		.then(data => {
			//			console.log("取得庫存地圖:", data);
			inventoryMap = data;
			// 找出最大日期
			//			const dates = Object.keys(inventoryMap).sort();
			//			if (dates.length > 0) {
			//				const maxDate = new Date(dates[dates.length - 1]);
			//				currentDate = new Date(maxDate.getFullYear(), maxDate.getMonth(), 1); // 把日曆移到最大月份
			//			}
			generateCalendar(currentDate.getFullYear(), currentDate.getMonth());
		})
		.catch(e => console.error("取庫存失敗", e));
}


function generateCalendar(year, month) {
	const firstDay = new Date(year, month, 1);
	const lastDay = new Date(year, month + 1, 0);
	const today = new Date();

	const daysInMonth = lastDay.getDate();
	const startingDayOfWeek = firstDay.getDay();

	let html = '';

	// 星期標題
	const weekdays = ['日', '一', '二', '三', '四', '五', '六'];
	weekdays.forEach(day => {
		html += `<div class="calendar-header-cell">${day}</div>`;
	});

	// 前一個月的尾巴日期
	const prevMonth = new Date(year, month - 1, 0);
	const prevMonthDays = prevMonth.getDate();
	for (let i = startingDayOfWeek - 1; i >= 0; i--) {
		const day = prevMonthDays - i;
		html += `<div class="calendar-day other-month">${day}</div>`;
	}

	// 當前月份的日期
	for (let day = 1; day <= daysInMonth; day++) {
		const date = new Date(year, month, day);
		const isPast = date < today.setHours(0, 0, 0, 0);
		const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;

		const remaining = inventoryMap[dateStr] ?? '-';
		const hasData = inventoryMap.hasOwnProperty(dateStr);

		let classes = ['calendar-day'];
		if (isPast || !hasData) classes.push('disabled'); // 新增：沒資料就禁用


		if (selectedCheckIn && dateStr === selectedCheckIn) {
			classes.push('selected');
		} else if (selectedCheckOut && dateStr === selectedCheckOut) {
			classes.push('selected');
		} else if (selectedCheckIn && selectedCheckOut && dateStr > selectedCheckIn && dateStr < selectedCheckOut) {
			classes.push('in-range');
		}

		let infoText = '';
		let infoColor = '#7E4E24';

		if (!isPast) { // 未來日期才顯示狀態
			if (!hasData) {
				infoText = '未開放';
				infoColor = '#ccc';
			} else if (remaining === 0) {
				infoText = '已售完';
				infoColor = 'red';
				classes.push('disabled');
			} else {
				infoText = `剩 ${remaining} 間`;
				infoColor = '#7E4E24';
			}
		}

		html += `<div class="${classes.join(' ')}" ${hasData && !isPast && remaining !== 0 ? `onclick="selectDate('${dateStr}')"` : ''}>
			                <div>${day}</div>
			                <div style="font-size:0.8em; color:${infoColor};">${infoText}</div>
			            </div>`;
	}

	// 下一個月的開頭日期
	const remainingCells = 42 - (startingDayOfWeek + daysInMonth);
	for (let day = 1; day <= remainingCells; day++) {
		html += `<div class="calendar-day other-month">${day}</div>`;
	}

	document.getElementById('calendar').innerHTML = html;

	// 更新月份顯示
	const monthNames = ['一月', '二月', '三月', '四月', '五月', '六月',
		'七月', '八月', '九月', '十月', '十一月', '十二月'];
	document.getElementById('currentMonth').textContent =
		`${year} 年 ${monthNames[month]}`;
}

function selectDate(dateStr) {
	const clickedDate = new Date(dateStr);
	const today = new Date();

	if (clickedDate < today.setHours(0, 0, 0, 0)) {
		return; // 不能選擇過去的日期
	}

	if (!selectedCheckIn || (selectedCheckIn && selectedCheckOut)) {
		// 選擇入住日期
		selectedCheckIn = dateStr;
		selectedCheckOut = null;
		onDateSelected(selectedCheckIn); // 呼叫單日庫存
	} else if (selectedCheckIn && !selectedCheckOut) {
		// 選擇退房日期
		if (dateStr > selectedCheckIn) {
			selectedCheckOut = dateStr;
			checkInventoryRange(); // 呼叫多日庫存
		} else {
			// 如果選擇的日期早於入住日期，重新選擇
			selectedCheckIn = dateStr;
			selectedCheckOut = null;
			onDateSelected(selectedCheckIn); // 重新選入住日時也呼叫 AJAX
		}
	}

	updateDateDisplay();// 更新入住/退房顯示
	generateCalendar(currentDate.getFullYear(), currentDate.getMonth());// 更新右側摘要
	updateSummary();// 重繪日曆
}

function updateDateDisplay() {
	if (selectedCheckIn) {
		document.getElementById('checkInDate').textContent = formatDate(selectedCheckIn);
		document.getElementById('dateDisplay').style.display = 'block';
	}

	if (selectedCheckOut) {
		document.getElementById('checkOutDate').textContent = formatDate(selectedCheckOut);

		// 計算住宿天數
		const checkIn = new Date(selectedCheckIn);
		const checkOut = new Date(selectedCheckOut);
		const nights = Math.ceil((checkOut - checkIn) / (1000 * 60 * 60 * 24));

		document.getElementById('nightsCount').textContent = nights;
		document.getElementById('nightsInfo').style.display = 'block';
	} else {
		document.getElementById('checkOutDate').textContent = '請選擇日期';
		document.getElementById('nightsInfo').style.display = 'none';
	}
}

function formatDate(dateStr) {
	const date = new Date(dateStr);
	const year = date.getFullYear();
	const month = date.getMonth() + 1;
	const day = date.getDate();
	return `${year}/${month}/${day}`;
}

function previousMonth() {
	currentDate.setMonth(currentDate.getMonth() - 1);
	generateCalendar(currentDate.getFullYear(), currentDate.getMonth());
}

function nextMonth() {
	currentDate.setMonth(currentDate.getMonth() + 1);
	generateCalendar(currentDate.getFullYear(), currentDate.getMonth());
}

function selectPackage(element, price) {
	// 移除其他選項的選中狀態
	document.querySelectorAll('.package-option').forEach(option => {
		option.classList.remove('selected');
	});

	// 選中當前選項
	element.classList.add('selected');
	selectedPackagePrice = price;

	updateSummary();
}

function updateGuestOptions() {
	const roomCount = parseInt(document.getElementById("roomCount").value) || 1;
	const guestSelect = document.getElementById("guestCount");
	const maxGuestsPerRoom = parseInt(guestSelect.dataset.maxGuests) ||
		parseInt(document.querySelector("meta[name='roomTypeGuestNum']")?.content) || 4;

	console.log("間數:", roomCount, "最大可住人數:", maxGuestsPerRoom);

	// 如果沒有選擇房間數，重置人數選單
	if (roomCount === 0) {
		guestSelect.innerHTML = '<option disabled selected>請先選擇房間數量</option>';
		updateSummary();
		return;
	}
	// 總最大人數= 房間數量 × 每間房最大人數
	const totalMaxGuests = roomCount * maxGuestsPerRoom;

	// 保存目前選擇的人數
	const currentGuestCount = parseInt(guestSelect.value) || totalMaxGuests;

	// 清空後重建
	guestSelect.innerHTML = "";
	// 添加預設選項
	const defaultOption = document.createElement("option");
	defaultOption.value = "0";
	defaultOption.text = "選擇人數";
	defaultOption.disabled = true;
	defaultOption.selected = true;
	guestSelect.appendChild(defaultOption);
	
	for (let i = 1; i <= totalMaxGuests; i++) {
		const option = document.createElement("option");
		option.value = i;
		option.text = `${i} 人`;
		guestSelect.appendChild(option);
	}

	// 保持原本的選擇，如果超過新的最大值則設為最大值
	if (currentGuestCount <= totalMaxGuests) {
		guestSelect.value = currentGuestCount;
	} else {
		guestSelect.value = totalMaxGuests; // 重置為預設值
	}

	// 更新摘要
	updateSummary();
}

function updateSummary() {
	const roomCount = parseInt(document.getElementById('roomCount').value) || 1;
	const guestCount = parseInt(document.getElementById('guestCount').value) || 1;

	document.getElementById('summaryRooms').textContent = `${roomCount} 間`;

	if (selectedCheckIn && selectedCheckOut) {
		const checkIn = new Date(selectedCheckIn);
		const checkOut = new Date(selectedCheckOut);
		const nights = Math.ceil((checkOut - checkIn) / (1000 * 60 * 60 * 24));

		document.getElementById('summaryNights').textContent = `${nights} 晚`;

		const roomCost = roomBasePrice * roomCount * nights;
		const packageCost = selectedPackagePrice * guestCount * nights;
		const total = roomCost + packageCost;

		document.getElementById('summaryRoomCost').textContent = `NT$ ${roomCost.toLocaleString()}`;
		document.getElementById('summaryPackageCost').textContent = `NT$ ${packageCost.toLocaleString()}`;
		document.getElementById('summaryTotal').textContent = `NT$ ${total.toLocaleString()}`;
	} else {
		document.getElementById('summaryNights').textContent = '- 晚';
		document.getElementById('summaryRoomCost').textContent = 'NT$ -';
		document.getElementById('summaryPackageCost').textContent = `NT$ 0`;
		document.getElementById('summaryTotal').textContent = 'NT$ -';
	}
}


// 初始化日曆
generateCalendar(currentDate.getFullYear(), currentDate.getMonth());

// 預設選擇第一個套餐（無加購）
document.querySelector('.package-option').classList.add('selected');

//間數下拉式選單
function onDateSelected(dateStr) {
	console.log("選擇日期:", dateStr);
	const roomTypeId = document.querySelector("meta[name='roomTypeId']").content;

	fetch(`/roomtype/${roomTypeId}/inventory?date=${dateStr}`)
		.then(response => response.json())
		.then(remaining => {
			console.log("該日剩餘房間數:", remaining);
			const select = document.getElementById("roomCount");
			// 記錄當前選擇的房間數
			const currentRoomCount = parseInt(select.value) || 0;
			select.innerHTML = "";

			if (remaining <= 0) {
				const option = document.createElement("option");
				option.text = "已售完";
				option.disabled = true;
				select.appendChild(option);
				// 重置人數選單
				const guestSelect = document.getElementById("guestCount");
				guestSelect.innerHTML = '<option disabled selected>請先選擇房間數量</option>';
			} else {
				// 添加預設選項
				const defaultOption = document.createElement("option");
				defaultOption.value = "0";
				defaultOption.text = "選擇間數";
				defaultOption.disabled = true;
				defaultOption.selected = true;
				select.appendChild(defaultOption);
				
				for (let i = 1; i <= remaining; i++) {
					const option = document.createElement("option");
					option.value = i;
					option.text = `${i} 間房`;
					select.appendChild(option);
				}
				// 重新選擇日期時，重置為1間房
//				select.value = 1;
							

				// 保持原本的選擇，如果超過可用數量則設為最大值
				if (currentRoomCount > 0 && currentRoomCount <= remaining) {
					select.value = currentRoomCount;
				} else {
					select.value = 1;
				}
			}

			// 更新人數選項
			updateGuestOptions();
		})
		.catch(error => {
			console.error("單日查庫存錯誤", error);
		});
}

//多日區間剩餘間數
function checkInventoryRange() {
	if (!selectedCheckIn || !selectedCheckOut) {
		console.log("尚未選擇完整區間");
		return;
	}

	const roomTypeId = document.querySelector("meta[name='roomTypeId']").content;
	fetch(`/roomtype/${roomTypeId}/inventory-range?start=${selectedCheckIn}&end=${selectedCheckOut}`)
		.then(response => response.json())
		.then(minRemaining => {
			console.log("多日最少庫存:", minRemaining);
			const select = document.getElementById("roomCount");
			// 記錄當前選擇的房間數
			const currentRoomCount = parseInt(select.value) || 0;
			select.innerHTML = "";

			if (minRemaining <= 0) {
				const option = document.createElement("option");
				option.text = "已售完";
				option.disabled = true;
				select.appendChild(option);
				// 重置人數選單
				const guestSelect = document.getElementById("guestCount");
				guestSelect.innerHTML = '<option disabled selected>請先選擇房間數量</option>';
			} else {
				// 添加預設選項
				const defaultOption = document.createElement("option");
				defaultOption.value = "0";
				defaultOption.text = "選擇間數";
				defaultOption.disabled = true;
				select.appendChild(defaultOption);
				
				for (let i = 1; i <= minRemaining; i++) {
					const option = document.createElement("option");
					option.value = i;
					option.text = `${i} 間房`;
					select.appendChild(option);
				}
				// 保持原本的選擇，如果超過可用數量則設為最大值
				if (currentRoomCount > 0 && currentRoomCount <= minRemaining) {
					select.value = currentRoomCount;
				} else {
					select.value = "1"; // 預設選擇1間房
				}
			}

			// 更新人數選項
			updateGuestOptions();
		})
		.catch(error => {
			console.error("多日查庫存錯誤", error);
		});
}

//跳轉到多房型頁面
document.querySelectorAll('.multi-book-btn').forEach(btn => {
	btn.addEventListener('click', function() {
		goToMultiBooking(roomTypeId);
	});
});
function goToMultiBooking(roomTypeId) {
	const checkinEl = document.getElementById('checkInDate');
	const checkoutEl = document.getElementById('checkOutDate');
	const guests = parseInt(document.getElementById('guestCount').value) || 1;
	const rooms = parseInt(document.getElementById('roomCount').value) || 1; // 單一房型頁的房間數 input
	const packagePrice = selectedPackagePrice || 0;

	const checkin = checkinEl ? checkinEl.textContent.trim() : "";
	const checkout = checkoutEl ? checkoutEl.textContent.trim() : "";

	let url = `/bookMulti?checkin=${checkin}&checkout=${checkout}&guests=${guests}&package=${packagePrice}&rooms_${roomTypeId}=${rooms}&guests_${roomTypeId}=${guests}`;
	window.location.href = url;
}

document.getElementById('bookBtn').addEventListener('click', function() {
	if (!selectedCheckIn || !selectedCheckOut) {
		alert('請選擇入住和退房日期');
		return;
	}
	
	const roomCount = parseInt(document.getElementById('roomCount').value) || 0;
	const guestCount = parseInt(document.getElementById('guestCount').value) || 0;
	
	if (roomCount === 0) {
		alert('請選擇房間數量');
		return;
	}
	
	if (guestCount === 0) {
		alert('請選擇入住人數');
		return;
	}
	alert('接下來將導向填寫入住資料頁面。');

	// 抓到原本 input 元素
	const checkinText = document.getElementById('checkInDate').textContent.trim();
	const checkoutText = document.getElementById('checkOutDate').textContent.trim();

	// 格式轉 yyyy-MM-dd
	function formatDateString(str) {
		if (!str.includes('/')) return str;
		const parts = str.split('/');
		if (parts.length !== 3) return "";
		let [year, month, day] = parts;
		month = month.padStart(2, '0');
		day = day.padStart(2, '0');
		return `${year}-${month}-${day}`;
	}


	const form = document.getElementById('singleRoomForm');
	const roomTypeId = document.querySelector("meta[name='roomTypeId']").content;
	// 日期是從日曆顯示的文字
	form.querySelector('input[name="checkin"]').value = formatDateString(checkinText);
	form.querySelector('input[name="checkout"]').value = formatDateString(checkoutText);
	form.querySelector('input[name="guests"]').value = document.getElementById('guestCount').value;

	// 房數
	let inputRoom = document.createElement("input");
	inputRoom.type = "hidden";
	inputRoom.name = `rooms_${roomTypeId}`;
	inputRoom.value = document.getElementById('roomCount').value;
	form.appendChild(inputRoom);

	// 人數
	let inputGuests = document.createElement("input");
	inputGuests.type = "hidden";
	inputGuests.name = `guests_${roomTypeId}`;
	inputGuests.value = document.getElementById('guestCount').value;
	form.appendChild(inputGuests);

	// 加購專案
	if (selectedPackagePrice > 0) {
		let input = document.createElement("input");
		input.type = "hidden";
		input.name = "package";
		input.value = selectedPackagePrice;
		form.appendChild(input);
	}

	form.submit();
});



