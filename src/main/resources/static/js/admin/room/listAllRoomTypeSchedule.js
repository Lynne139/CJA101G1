
document.addEventListener("DOMContentLoaded", function() {



	initRoomTypeScheduleTable(); // 初次載入表格

	// ===== DataTables =====
	function initRoomTypeScheduleTable() {
		const table = $('#roomTypeScheduleTable');

		if ($.fn.DataTable.isDataTable(table)) {
			table.DataTable().clear().destroy(); // 清除舊實例
		}

		table.DataTable({
			language: zhHANTLang,
			paging: true,
			pageLength: 5,
			lengthMenu: [5, 10],
			order: [[0, 'asc']],
			autoWidth: false,
			columnDefs: [
//				{ targets: [0], width: "15%" },
				{ targets: [0], width: "25%" },
				{ targets: [1], width: "30%", orderable: false },
				{ targets: [2], width: "15%" },
				{ targets: [3], width: "15%" },
				{ targets: [4], width: "15%" }
			],
			searching: false,
			ordering: true,
			info: true
		});
	}


	// ===== 清空複合查詢欄位 =====
	const clearBtn = document.querySelector(".btn_search_clear");
	const form = document.querySelector(".filter");

	clearBtn.addEventListener("click", () => {
		// 清空欄位值
		form.querySelector("select[name='roomTypeId']").value = "";
		form.querySelector("input[name='minDate']").value = "";
		form.querySelector("input[name='maxDate']").value = "";
		form.querySelector("input[name='minAmount']").value = "";
		form.querySelector("input[name='maxAmount']").value = "";

		// 自動提交清空後的查詢表單
		form.submit();
	});
	// ===== 送出複合查詢欄位 =====
	document.querySelector('.filter').addEventListener('submit', function(e) {
		e.preventDefault();// 攔截 form 的預設 full-page submit
		// 把所有 form 欄位包成 query string
		const params = new URLSearchParams(new FormData(this)).toString();
		// fetch 帶上 XMLHttpRequest header 讓後端判定為 AJAX
		fetch(`/admin/listAllRoomTypeSchedule?${params}`, {
			headers: { 'X-Requested-With': 'XMLHttpRequest' }
		})
			.then(res => res.text())
			.then(html => {
				// 把回傳的 fragment 注入到 roomScheduleResult
				document.getElementById('roomTypeScheduleResult').innerHTML = html; // 渲染結果
				// 重新啟用 DataTables
				initRoomTypeScheduleTable();
			})
			.catch(err => {
				console.error('複合查詢失敗：', err);
			});
	});





	document.addEventListener("click", function(e) {
		const btn = e.target.closest("#btnInitAllSchedules");
		
		if(!btn)return;
	    if (!confirm("確定要為所有房型批次產生未來的排程嗎？")) return;

	    btn.classList.add("loading");
	    btn.innerHTML = `<span class="spinner"></span> 處理中...`;

	    fetch("/admin/listAllRoomTypeSchedule/initAll")
	        .then(res => {
	            if (!res.ok) throw new Error("伺服器錯誤");
	            return res.text();
	        })
	        .then(() => {
	            alert("已成功為所有房型批次產生排程！");
	            location.reload();
	        })
	        .catch(err => {
	            console.error(err);
	            alert("批次產生失敗，請稍後再試");
	        })
	        .finally(() => {
	            btn.classList.remove("loading");
	            btn.innerHTML = `批次產生所有房型排程`;
	        });
	});






});