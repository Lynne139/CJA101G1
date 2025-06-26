
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
				{ targets: [0], width: "15%" },
				{ targets: [1], width: "20%", orderable: false },
				{ targets: [2], width: "15%" },
				{ targets: [3], width: "15%" },
				{ targets: [4], width: "15%" },
				{ targets: [5], width: "20%" }
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




	// ===== Modal - Add =====
	//按下新增按鈕打開modal
	document.addEventListener("click", async function(e) {
		const addBtn = e.target.closest("#btnAddRoom");

		if (!addBtn) return;
		e.preventDefault();

		// 如果 modal 已經存在，先強制關掉並移除 DOM
		const oldModalEl = document.getElementById("roomTypeScheduleAddModal");
		if (oldModalEl) {
			const modal = bootstrap.Modal.getInstance(oldModalEl);
			if (modal) modal.hide();

			// 等動畫結束後再清空 DOM（500ms 是 Bootstrap 預設動畫時間）
			await new Promise(resolve => setTimeout(resolve, 500));

			// 清掉 modal DOM
			oldModalEl.remove();

		}


		fetch("/admin/listAllRoomTypeSchedule/add")
			.then(res => res.text())
			.then(html => {
				// container 一定要在這裡重新抓一次
				const container = document.getElementById("roomTypeScheduleInfoModalContainer");

				if (!container) {
					alert("找不到 modal 插入位置");
					return;
				}
				//直接插入，不清空原來內容
				//				document.body.insertAdjacentHTML("beforeend", html);

				//會有非同步問題，可以用上面那一句
				//先清空再插入，再觸發 DOM reflow
				// 等待畫面 reflow 完再抓 modal
				container.innerHTML = html;

				setTimeout(() => {
					const modalEl = document.getElementById("roomTypeScheduleAddModal");
					if (!modalEl) {
						alert("無法載入modal結構");
						return;
					}

					const modal = new bootstrap.Modal(modalEl);
					modal.show();

					// Modal 顯示後，綁定表單提交功能
					modalEl.addEventListener("shown.bs.modal", () => {
						//addEventListener()是一次性的綁定，綁舊dom上的會失效，必須重新綁新渲染的dom
						bindFormSubmitAdd();
					});
				}, 50);// 50ms 較安全
			})
			.catch(err => {
				alert("載入表單失敗：" + err.message);
			});
	});

	// 綁定「新增房間」modal 內的送出按鈕
	function bindFormSubmitAdd() {
		const submitBtn = document.getElementById("btnSubmitAdd");
		if (!submitBtn) return;

		submitBtn.addEventListener("click", function(e) {
			e.preventDefault();

			showBtnOverlay(submitBtn); // 加入 loading 遮罩

			const form = document.getElementById("roomTypeScheduleAddForm");
			const formData = new FormData(form);


			fetch("/admin/listAllRoomTypeSchedule/insert", {
				method: "POST",
				body: formData
			})
				.then(res => {

					if (res.redirected) {
						window.location.href = res.url; // 讓瀏覽器照後端redirect去重新載入頁面

						// 成功，清空並關閉modal
						document.getElementById("roomTypeScheduleAddForm").reset();

						const modal = bootstrap.Modal.getInstance(document.getElementById("roomTypeScheduleAddModal"));
						modal.hide();

					} else {
						return res.text(); // 失敗時回傳HTML
					}

				})
				.then(html => {

					if (!html) return; // 成功就不會拿到res(modal fragment+表單填入的內容)，也就不處理下面

					//錯誤時僅更新 modal body（避免整個 modal 重建）
					const parser = new DOMParser();
					const doc = parser.parseFromString(html, "text/html");

					const newBody = doc.querySelector(".modal-body");
					const oldBody = document.querySelector("#roomTypeScheduleAddModal .modal-body");

					if (newBody && oldBody) {
						oldBody.replaceWith(newBody);
					}

					// 模擬動畫結束後再初始化TinyMCE（因為modal沒重新開不能用shown.bs.modal來監聽）
					setTimeout(() => {
						bindFormSubmitAdd();
					}, 300); // 與動畫一致延遲時間


				})
				.catch(err => {
					alert("送出失敗：" + err.message);
				})
				.finally(() => {
					removeBtnOverlay(submitBtn);
				});
		});
	}


	// ===== Modal - Edit =====
	//按下編輯icon按鈕打開modal
	document.addEventListener("click", async function(e) {
		const editBtn = e.target.closest(".btn_edit");
		if (!editBtn) return;
		e.preventDefault();

		const roomId = editBtn.getAttribute("data-id");
		if (!roomId) return;

		//		// 關鍵：URL 要跟 Controller 的 GET Mapping 一致
		//		  const res = await fetch(`/admin/listAllRoom/edit?roomId=${roomId}`);
		//		  const html = await res.text();
		//		  container.innerHTML = html;
		//		  const modalEl = document.getElementById("roomEditModal");
		//		  new bootstrap.Modal(modalEl).show();
		//		  // 綁定表單驗證 & 送出
		//		  bindFormSubmitEdit();

		// 1. 移除舊的 modal
		// 如果 modal 已經存在，先強制關掉並移除 DOM
		const oldModalEl = document.getElementById("roomTypeScheduleEditModal");
		if (oldModalEl) {
			const modal = bootstrap.Modal.getInstance(oldModalEl);
			if (modal) modal.hide();

			// 等動畫結束後再清空 DOM（500ms 是 Bootstrap 預設動畫時間）
			await new Promise(resolve => setTimeout(resolve, 500));

			// 清掉 modal DOM
			oldModalEl.remove();
		}

		// 2. 不論是否有舊 modal，都要 fetch 新的
		fetch(`/admin/listAllRoomTypeSchedule/edit?roomId=${roomTypeScheduleId}`)
			.then(res => res.text())
			.then(html => {

				// container 一定要在這裡重新抓一次
				const container = document.getElementById("roomTypeScheduleInfoModalContainer");

				if (!container) {
					alert("找不到 modal 插入位置");
					return;
				}

				container.innerHTML = html;

				setTimeout(() => {
					const modalEl = document.getElementById("roomTypeScheduleEditModal");
					if (!modalEl) {
						alert("無法載入modal結構");
						return;
					}

					const modal = new bootstrap.Modal(modalEl);
					modal.show();
					// Modal 顯示後，綁定表單驗證&送出功能
					modalEl.addEventListener("shown.bs.modal", () => {
						//addEventListener()是一次性的綁定，綁舊dom上的會失效，必須重新綁新渲染的dom
						bindFormSubmitEdit();
					});
				}, 50);// 50ms 較安全
			})
			.catch(err => {
				alert("載入表單失敗：" + err.message);
			});
	});


	function bindFormSubmitEdit() {
		const submitBtn = document.getElementById("btnSubmitEditSave");
		if (!submitBtn) return;

		submitBtn.addEventListener("click", function(e) {
			e.preventDefault();

			showBtnOverlay(submitBtn); // 加入 loading 遮罩

			const form = document.getElementById("roomTypeScheduleEditForm");
			const formData = new FormData(form);


			fetch("/admin/listAllRoomTypeSchedule/update", {
				method: "POST",
				body: formData
			})
				.then(res => {

					if (res.redirected) {
						window.location.href = res.url; // 讓瀏覽器照後端redirect去重新載入頁面

						// 成功，清空並關閉modal
						document.getElementById("roomTypeScheduleEditForm").reset();

						const modal = bootstrap.Modal.getInstance(document.getElementById("roomTypeScheduleEditModal"));
						modal.hide();

					} else {
						return res.text(); // 失敗時回傳HTML
					}

				})
				.then(html => {

					if (!html) return; // 成功就不會拿到res(表單填入的內容)，也就不處理下面

					//錯誤時僅更新 modal body（避免整個 modal 重建）
					const parser = new DOMParser();
					const doc = parser.parseFromString(html, "text/html");

					const newBody = doc.querySelector(".modal-body");
					const oldBody = document.querySelector("#roomTypeScheduleEditModal .modal-body");

					if (newBody && oldBody) {
						oldBody.replaceWith(newBody);
					}

					// 模擬動畫結束後再初始化TinyMCE（因為modal沒重新開不能用shown.bs.modal來監聽）
					setTimeout(() => {
						bindFormSubmitEdit();
					}, 300); // 與動畫一致延遲時間


				})
				.catch(err => {
					alert("送出失敗：" + err.message);
				})
				.finally(() => {
					removeBtnOverlay(submitBtn);
				});
		});
	}








});