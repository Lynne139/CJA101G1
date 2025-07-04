let checkinPicker, checkoutPicker;
let presetGuests = {};
let presetRooms = {};

//接收單一房型預訂頁面的資料
window.addEventListener('load', () => {
	//URL參數
	const params = new URLSearchParams(window.location.search);
	//收集預設值
	document.querySelectorAll('.guest-select').forEach(select => {
	  const roomTypeId = select.dataset.id;
	  const guests = parseInt(params.get(`guests_${roomTypeId}`)) || 0;
	  if (guests > 0) {
	    presetGuests[roomTypeId] = guests;
	  }
	});
	document.querySelectorAll('.room-select').forEach(select => {
        const roomTypeId = select.dataset.id;
        const rooms = parseInt(params.get(`rooms_${roomTypeId}`)) || 0;
        if (rooms > 0) {
            presetRooms[roomTypeId] = rooms;
        }
    });
	
	// 設定日期
	const checkin = params.get('checkin');
	const checkout = params.get('checkout');
	const guests = parseInt(params.get('guests')) || 0;
	const packagePrice = parseInt(params.get('package')) || 0;

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

	if (checkin) {
		document.getElementById('checkin').value = formatDateString(checkin);
	}
	if (checkout) {
		document.getElementById('checkout').value = formatDateString(checkout);
	}
	if (guests) {
		document.getElementById('guestCount').value = guests;
	}

	// 記錄選到的加購專案
	if (packagePrice !== null && packagePrice !== undefined) {
		selectedPackagePrice = packagePrice;
		document.querySelectorAll('.package-option').forEach(option => {
			option.classList.remove('selected');
			if (parseInt(option.dataset.package) === packagePrice) {
				option.classList.add('selected');
			}
		});
	}
	fetch("/bookMulti/enabled-dates")
			.then(res => res.json())
			.then(enabledDates => {
				// 初始化 checkin
				checkinPicker = flatpickr("#checkin", {
					dateFormat: "Y-m-d",
					minDate: "today",
					enable: enabledDates,
					locale: "zh_tw",
					onChange: function(selectedDates) {
						if (selectedDates.length > 0) {
							const nextDay = new Date(selectedDates[0]);
							nextDay.setDate(nextDay.getDate() + 1);
							checkoutPicker.set("minDate", nextDay);
						}
						toggleRoomSelects();
						calculateTotal();
					}
				});

				// 初始化 checkout
				checkoutPicker = flatpickr("#checkout", {
					dateFormat: "Y-m-d",
					minDate: "today",
					enable: enabledDates,
					locale: "zh_tw",
					onChange: function() {
						toggleRoomSelects();
						calculateTotal();
					}
				});
				//在 flatpickr 初始化完成後，如果有預設日期，立即觸發 toggleRoomSelects
				if (checkin && checkout) {
					// 使用 setTimeout 確保 DOM 完全載入
					setTimeout(() => {
						toggleRoomSelects();
					}, 100);
				}				
				
			}).catch(e => console.error("載入可選日期失敗:", e));
});

// 控制房間選擇器的可用性
function toggleRoomSelects() {
	const checkin = document.getElementById('checkin').value;
	const checkout = document.getElementById('checkout').value;
	const roomSelects = document.querySelectorAll('.room-select');

	if (!checkin || checkin === '請選擇日期' || !checkout || checkout === '請選擇日期') {
		console.log("尚未選擇完整日期，不呼叫 API");

		// 重置所有房型卡
		roomSelects.forEach(select => {
			select.innerHTML = '<option selected>請先選擇日期</option>';
			select.disabled = true;

			const roomCard = select.closest('.room-card');
			const guestSelect = roomCard.querySelector('.guest-select');
			if (guestSelect) {
				guestSelect.innerHTML = '<option selected>選擇人數</option>';
				guestSelect.disabled = true;
			}
		});

		// 重置總入住人數
		document.getElementById('guestCount').value = 0;
		return; // 直接結束，不送 request
	}

	if (checkin && checkout) {
		// 只打一次 fetch
		fetch(`/bookMulti/inventory?start=${checkin}&end=${checkout}`)
			.then(res => res.json())
			.then(data => {
				console.log("庫存資料:", data);

				roomSelects.forEach(select => {
					const roomTypeId = select.dataset.id;
					const available = data[roomTypeId] || 0;
					console.log("目前處理的 roomTypeId:", roomTypeId);
									console.log("取得的 available:", data[roomTypeId]);

					select.innerHTML = ''; // 清空並重建 option

					// 先加 placeholder
					const placeholder = document.createElement("option");
					placeholder.text = "選擇房數";
					placeholder.value = "";
					placeholder.selected = true;
					select.appendChild(placeholder);

					if (available > 0) {
						for (let i = 1; i <= available; i++) {
							const option = document.createElement("option");
							option.value = i;
							option.text = `${i} 間`;
							select.appendChild(option);
						}
						select.disabled = false;
					} else {
						// 已售完的情況
						const option = document.createElement("option");
						option.value = "";
						option.text = "已售完";
						option.selected = true;
						option.disabled = true;
						select.appendChild(option);
						select.disabled = true;
					}
				});
				
				// 套用預設房間數
                roomSelects.forEach(select => {
                    const roomTypeId = select.dataset.id;
					const available = data[roomTypeId] || 0;
					// 只有在庫存充足且不是 disabled 狀態時才套用預設值
                    if (presetRooms[roomTypeId] && available > 0 && !select.disabled) {
                        select.value = presetRooms[roomTypeId];
                        // 選擇房間數後，立即更新該房型的人數選項
                        const roomCard = select.closest('.room-card');
                        updateGuestOptionsForRoom(roomCard);
				    }else if (available === 0) {
	                    // 確保已售完房型的人數選單也被正確設置
	                    const roomCard = select.closest('.room-card');
	                    const guestSelect = roomCard.querySelector('.guest-select');
	                    if (guestSelect) {
	                        guestSelect.innerHTML = '<option value="0" selected disabled>已售完</option>';
	                        guestSelect.disabled = true;
	                    }
					}
                });
				updateTotalGuests();
				calculateTotal();
			})
			.catch(e => console.error("查詢庫存錯誤", e));
	} else {
		// 尚未選擇日期，關閉所有 select
		roomSelects.forEach(select => {
			select.innerHTML = '<option selected>請先選擇日期</option>';
			select.disabled = true;
		});
	}
}

// 更新單一房型的人數選項
function updateGuestOptionsForRoom(roomCard) {
	const roomSelect = roomCard.querySelector('.room-select');
	const guestSelect = roomCard.querySelector('.guest-select');

	if (!roomSelect || !guestSelect) return;

	const roomCount = parseInt(roomSelect.value) || 0;
	const maxGuestsPerRoom = parseInt(guestSelect.dataset.maxGuests) || 4; // 預設最大4人

	// 如果沒有選擇房間數，禁用人數選擇
	if (roomCount === 0) {
		guestSelect.disabled = true;
		guestSelect.innerHTML = '<option value="0" selected>選擇人數</option>';
		return;
	}

	// 總最大人數 = 房間數量 × 每間房最大人數
	const totalMaxGuests = roomCount * maxGuestsPerRoom;
	const roomTypeId = roomSelect.dataset.id;
	
	// 記錄目前選擇的人數
	const currentGuests = parseInt(guestSelect.value) || 0;

	// 清空後重建選項
	guestSelect.innerHTML = "";
	// 添加預設選項
	const defaultOption = document.createElement("option");
	defaultOption.value = "0";
	defaultOption.text = "選擇人數";
	defaultOption.selected = true;
	guestSelect.appendChild(defaultOption);
	
	// 添加人數選項
	for (let i = 1; i <= totalMaxGuests; i++) {
		const option = document.createElement("option");
		option.value = i;
		option.text = `${i} 位`;
		guestSelect.appendChild(option);
	}
	
	// 智能選擇人數邏輯
	let selectedValue = "0";
	// 套用預設值（首次載入時）
    if (presetGuests[roomTypeId]) {
        selectedValue = presetGuests[roomTypeId];
        delete presetGuests[roomTypeId]; // 套用後移除，避免重複
    } 		
	// 如果不是首次載入，處理間數變更的邏輯
	else if (currentGuests > 0) {
		// 如果目前人數在新的最大值範圍內，保持原有人數
		if (currentGuests <= totalMaxGuests) {
			selectedValue = currentGuests;
		}
		// 如果目前人數超出新的最大值，設為最大值
		else {
			selectedValue = totalMaxGuests;
		}
	}
	// 如果目前沒有選擇人數，但有選擇房間，設為最大值
	else if (currentGuests === 0 && roomCount > 0) {
		selectedValue = totalMaxGuests;
	}
	guestSelect.value = selectedValue;

	// 啟用人數選擇
	guestSelect.disabled = false;
}

// 計算總金額
function calculateTotal() {
	const checkin = document.getElementById('checkin').value;
	const checkout = document.getElementById('checkout').value;
	const guests = parseInt(document.getElementById('guestCount').value) || 0;
	if (!checkin || !checkout) {
		updateSummary(0, [], null);
		return;
	}

	// 計算住宿天數
	const nights = Math.ceil((new Date(checkout) - new Date(checkin)) / (1000 * 60 * 60 * 24));

	// 計算房間費用
	const roomSelects = document.querySelectorAll('.room-select');
	let roomTotal = 0;
	let selectedRooms = [];

	roomSelects.forEach(select => {
		const rooms = parseInt(select.value) || 0;
		if (rooms > 0) {
			const price = parseInt(select.dataset.price);
			const roomName = select.dataset.room;
			const roomSubtotal = price * rooms * nights;
			roomTotal += roomSubtotal;
			selectedRooms.push({
				name: roomName,
				rooms: rooms,
				nights: nights,
				price: price,
				subtotal: roomSubtotal
			});
		}
	});

	let packageTotal = 0;
	let selectedPackage = null;

	if (selectedPackagePrice > 0) {
		packageTotal = guests * selectedPackagePrice * nights;
		selectedPackage = {
			name: document.querySelector('.package-option.selected .package-name').textContent,
			guests: guests,
			price: selectedPackagePrice,
			nights: nights,
			subtotal: packageTotal
		};
	}


	const total = roomTotal + packageTotal;
	updateSummary(total, selectedRooms, selectedPackage);
}


let selectedPackagePrice = 0;
function selectPackage(el, price) {
	document.querySelectorAll('.package-option').forEach(option => {
		option.classList.remove('selected');
	});
	el.classList.add('selected');

	selectedPackagePrice = price;
	calculateTotal();
}




// 更新摘要顯示
function updateSummary(total, rooms, selectedPackage) {
	const roomSummary = document.getElementById('room-summary');
	const packageSummary = document.getElementById('package-summary');
	const totalPrice = document.getElementById('total-price');
	const bookBtn = document.getElementById('bookBtn');

	// 房間摘要
	roomSummary.innerHTML = '';
	rooms.forEach(room => {
		const div = document.createElement('div');
		div.className = 'summary-item';
		div.innerHTML = `
                <span>${room.name} × ${room.rooms}間 × ${room.nights}晚</span>
                <span>NT$ ${room.subtotal.toLocaleString()}</span>
            `;
		roomSummary.appendChild(div);
	});

	// 加購專案摘要
	packageSummary.innerHTML = '';
	if (selectedPackage) {
		const div = document.createElement('div');
		div.className = 'summary-item';
		div.innerHTML = `
		        <span>${selectedPackage.name} × ${selectedPackage.guests}人 × ${selectedPackage.nights}晚</span>
		        <span>NT$ ${selectedPackage.subtotal.toLocaleString()}</span>
		    `;
		packageSummary.appendChild(div);
	}


	// 總金額
	totalPrice.textContent = `NT$ ${total.toLocaleString()}`;

	// 預訂按鈕狀態
	bookBtn.disabled = total === 0;
}

// 事件監聽器
document.querySelectorAll('.room-select').forEach(select => {
	select.addEventListener('change', function() {
		const roomCard = this.closest('.room-card');
		// 更新該房型的人數選項
		updateGuestOptionsForRoom(roomCard);
		// 重新計算總人數和總金額
		updateTotalGuests();
		calculateTotal();
	});
});

document.querySelectorAll('.guest-select').forEach(select => {
	select.addEventListener('change', updateTotalGuests);
});

document.getElementById('guestCount').addEventListener('input', calculateTotal);




//總人數加總
function updateTotalGuests() {
	let total = 0;
	document.querySelectorAll('.guest-select').forEach(select => {
		//  先檢查房間數
		let roomCard = select.closest('.room-card');
		let roomSelect = roomCard.querySelector('.room-select');
		if (!roomSelect || parseInt(roomSelect.value) === 0 || isNaN(parseInt(roomSelect.value))) {
			console.log('房型沒選房間數，跳過這張卡');
			return; // 跳過
		}
		let value = parseInt(select.value);
		console.log('抓到的 value:', select.value, '轉成:', value);
		if (!isNaN(value) && value > 0) {
			total += value;
		}
	});
	console.log('總入住人數:', total);
	const guestInput = document.getElementById('guestCount');
	if (!isNaN(total) && total > 0) {
		guestInput.value = total;
	} else {
		guestInput.value = isNaN(total) ? 0 : total;
	}
	//重新計算專案金額
	calculateTotal();
}

document.getElementById('bookBtn').addEventListener('click', function() {
	const selectedCheckIn = document.getElementById('checkin').value;
	const selectedCheckOut = document.getElementById('checkout').value;
	if (!selectedCheckIn || !selectedCheckOut) {
		alert('請選擇入住和退房日期');
		return;
	}

	alert('接下來將導向填寫入住資料頁面。');

	const form = document.getElementById('confirmForm');

	// 設定日期與總人數
	form.querySelector('input[name="checkin"]').value = document.getElementById('checkin').value;
	form.querySelector('input[name="checkout"]').value = document.getElementById('checkout').value;
	form.querySelector('input[name="guests"]').value = document.getElementById('guestCount').value;

	// 多房型 - 房數
	document.querySelectorAll('.room-select').forEach(select => {
		const id = select.dataset.id;
		let input = document.createElement("input");
		input.type = "hidden";
		input.name = `rooms_${id}`;
		input.value = select.value;
		form.appendChild(input);
	});

	// 多房型 - 各別人數
	document.querySelectorAll('.guest-select').forEach(select => {
		const id = select.dataset.id;
		let input = document.createElement("input");
		input.type = "hidden";
		input.name = `guests_${id}`;
		input.value = select.value;
		form.appendChild(input);
	});

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