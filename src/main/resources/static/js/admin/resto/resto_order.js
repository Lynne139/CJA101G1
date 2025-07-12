document.addEventListener("DOMContentLoaded", function () {

	
  //modal載入位置
  const container = document.getElementById("restoOrderModalContainer");

  const savedScrollY = sessionStorage.getItem("scrollY");
    if (savedScrollY !== null) {
      window.scrollTo(0, parseInt(savedScrollY, 10));
      sessionStorage.removeItem("scrollY");
    }

  initRestoOrderTable(); // 初次載入表格

  // ===== DataTables =====
  function initRestoOrderTable() {
    const table = $('#restoOrderTable');

	
    if ($.fn.DataTable.isDataTable(table)) {
      table.DataTable().clear().destroy(); // 清除舊實例
    }

    table.DataTable({
      language: zhHANTLang,
      paging: true,
      pageLength: 10,
      lengthMenu: [5,10, 20, 50],
      order: [12, 'desc'],
      autoWidth: false,
      columnDefs: [
        { targets: [0], width: "5%"}, //ID
        { targets: [1], width: "10%"}, //訂位日期
        { targets: [2], width: "10%"}, //餐廳
        { targets: [3], width: "7%"}, //區段
        { targets: [4], width: "7%"}, //時段
        { targets: [5], width: "4%"}, //人數
        { targets: [6], width: "6%", orderable: false}, //狀態
        { targets: [7], width: "7%"}, //姓名
        { targets: [8], width: "12%"}, //信箱
        { targets: [9], width: "5%"}, //來源
        { targets: [10], width: "5%"}, //會員ID
        { targets: [11], width: "5%"}, //住宿ID
        { targets: [12], width: "10%"}, //下單時間
        { targets: [13], width: "7%", orderable: false} //操作
      ],
	  scrollX:true,
	  fixedColumns: {
	      rightColumns: 1,   // 固定最右邊 1 欄
		  leftColumns: 0
	    },
      searching: false,
      ordering: true,
      info: true,
    });
	
  }
  $(window).on('resize', function () {
              $('#restoOrderTable').DataTable().columns.adjust();
    });


  // ===== 清空複合查詢欄位 =====
  const clearBtn = document.querySelector(".btn_search_clear");
  const form = document.querySelector(".filter");

  clearBtn.addEventListener("click", () => {
	// 直接回到基礎路徑，URL乾淨，flatpickr / select 自然重置
	  location.href = location.pathname;
  });

  
  // ===== Flatpickr =====
  flatpickr("#regiDateRange", {
    mode: "range",
    dateFormat: "Y-m-d",
    onChange: function(selectedDates, dateStr) {
      const [from, to] = dateStr.split(" to ");
      document.getElementById("regiDateFrom").value = from || "";
      document.getElementById("regiDateTo").value = to || "";
    }
  });

  flatpickr("#orderTimeRange", {
    mode: "range",
    enableTime: true,
    dateFormat: "Y-m-d\\TH:i",
    onChange: function(selectedDates, dateStr) {
      const [from, to] = dateStr.split(" to ");
      document.getElementById("orderTimeFrom").value = from || "";
      document.getElementById("orderTimeTo").value = to || "";
    }
  });
  
  
  // ===== Modal - View =====
    document.addEventListener("click", function (e) {
      if (e.target.closest(".btn_view")) {
        const btn = e.target.closest(".btn_view");
        const restoOrderId = btn.getAttribute("data-id");

        if (!container) return;

        fetch(`/admin/resto_order/view?restoOrderId=${restoOrderId}`)
          .then(res => res.text())
          .then(html => {

            container.innerHTML = html;

            const modalEl = document.getElementById("restoOrderViewModal");
            if (!modalEl) {
              alert("無法載入 modal 結構");
              return;
            }
            const modal = new bootstrap.Modal(modalEl);
            modal.show();
          })
          .catch(err => {
            alert("載入資料失敗：" + err.message);
          });
      }
    });
	
	
	// ===== 刪除訂單 =====
	  document.addEventListener("click", function (e) {
	    if (e.target.closest(".btn_delete")) {
	      const btn = e.target.closest(".btn_delete");
	      const restoOrderId = btn.getAttribute("data-id");
		  
		  if (!btn) return;
	      if (!restoOrderId) return;

	      if (confirm("確定要永久刪除這筆訂單？")) {
			
			const params = new URLSearchParams();
		    params.append("restoOrderId",  restoOrderId);
			
	        // 存卷軸位置
	        sessionStorage.setItem("scrollY", window.scrollY);
	        
			fetch(`/admin/resto_order/delete`, {
	          method: 'POST',
			  headers: { "Content-Type": "application/x-www-form-urlencoded" },
			  body: params
	        })
	          .then(res => {
	            if (res.redirected) {
	              sessionStorage.setItem("toastMessage", "刪除成功！");
	              window.location.href = res.url; // 讓DataTables因為整頁刷新而重載資料
	              return;
	            }
	            return res.text(); // 若失敗沒redirect，才繼續處理
	          })
	          .catch(err => {
	            alert("封存失敗：" + err.message);
	          });
	      }
	    }
	  });
	
	
	  
	
	// === Add modal中，要先選好resto才顯示對應timeslot選擇欄位 ===
		  function bindRestoTimeslotSelect() {
		    const restoSelect    = document.getElementById('restoId');
		    const timeslotSelect = document.getElementById('timeslotId');
		    const dateInput      = document.getElementById('regiDate');

		    if (!restoSelect || !timeslotSelect || !dateInput) return;
			
			// 判斷是否為編輯模式（有 hidden id）
			  const isEditMode = !!document.getElementById('restoOrderId');

		    restoSelect.addEventListener('change', () => {
		      filterTimeslot();
		      filterTimeslotByDate(); // 餐廳變了，再跑一次時間判斷
		    });
			
			  dateInput.addEventListener('change', filterTimeslotByDate);
			  
		    // 首次載入（含回填）
		    filterTimeslot();
		    filterTimeslotByDate();

		    // 只顯示所選餐廳的時段
			function filterTimeslot () {
			  const selectedRestoId = restoSelect.value;            // "" 代表未選
			  const orderSource     = document.getElementById('orderSource')?.value;
			  const periodCode      = document.querySelector('[name="snapshotPeriodCode"]')?.value;

			  timeslotSelect.querySelectorAll('option').forEach(opt => {
			    const restoId  = opt.dataset.restoId || '';         // placeholder 會是 ""
			    const slotCode = opt.dataset.periodCode || '';

			    /* === 1. 未選餐廳：只顯示 placeholder === */
			    if (!selectedRestoId) {
			      const isPlaceholder   = !restoId;  // 沒有 data-resto-id
			      opt.hidden   = !isPlaceholder;
			      opt.disabled = !isPlaceholder;
			      return;                             // 跳過後續判斷
			    }

			    /* === 2. 已選餐廳：決定是否顯示 === */
			    let show;
			    if (orderSource === 'ROOM' && periodCode) {
			      // 房務過來，強制比對 periodCode
			      show = (restoId === selectedRestoId) && (slotCode === periodCode);
			    } else {
			      show = (restoId === selectedRestoId);
			    }

			    opt.hidden   = !show;
			    opt.disabled = !show;
			  });

			  /* === 3. 若目前選到的 option 被隱藏，重設選擇 === */
			  if (timeslotSelect.selectedOptions.length &&
			      timeslotSelect.selectedOptions[0].hidden) {
			    timeslotSelect.selectedIndex = 0; // 回到 placeholder
			  }
			}

		    //灰掉今天已過時段
			function filterTimeslotByDate () {
			  const selectedDate = dateInput.value;
			  if (!selectedDate) return;                           // 還沒選日期直接跳過

			  /* === 1. 取得「今天」的本地 yyyy-MM-dd === */
			  const todayStr = new Date().toLocaleDateString('en-CA'); // en-CA → 2025-07-09

			  /* === 2. 現在時間的「分鐘數」 === */
			  const now     = new Date();
			  const currMin = now.getHours() * 60 + now.getMinutes();

			  timeslotSelect.querySelectorAll('option').forEach(opt => {
			    /* 2-1. 跳過已被餐廳篩掉的 option（hidden=true） */
			    if (opt.hidden) return;

			    const timeStr = opt.dataset.time;   // HH:mm
			    const label   = opt.dataset.label;
			    if (!timeStr) return;               // placeholder

			    /* 2-2. 將 HH:mm 轉成「分鐘數」 */
			    const [h, m] = timeStr.split(':').map(Number);
			    const optMin = h * 60 + m;

			    /* 2-3. 判斷是否為過去時段 */
			    const isPast = (selectedDate === todayStr && optMin <= currMin);

			    if (isPast) {
			      opt.textContent = `${label}（已過）`;
			      opt.disabled    = !isEditMode;    // Add: true；Edit: false
			    } else {
			      opt.textContent = label;
			      opt.disabled    = false;
			    }
			  });

			  /* 3. 若目前選到已被 disable 的 option，就回到 placeholder */
			  if (timeslotSelect.selectedOptions.length &&
			      timeslotSelect.selectedOptions[0].disabled) {
			    timeslotSelect.selectedIndex = 0;
			  }
			}

			
			
			
		  }

		  
		  // === 餐廳＋日期＋時段 選好後顯示剩餘名額 ===
		  async function refreshRemaining () {
		    const restoId    = document.getElementById('restoId').value;
		    const timeslotId = document.getElementById('timeslotId').value;
		    const date       = document.getElementById('regiDate').value;
		    const seatsInput = document.getElementById('regiSeats');

		    if (!seatsInput) return;           // modal 還沒 render 完就先離開

		    // 條件還沒填齊時：恢復預設
		    if (!restoId || !timeslotId || !date) {
		      seatsInput.placeholder = '請輸入人數';
		      seatsInput.max = '';            // 取消 max 限制
		      return;
		    }

		    try {
		      const res   = await fetch(`/admin/api/reservation/remaining?restoId=${restoId}&timeslotId=${timeslotId}&date=${date}`);
		      const { remaining } = await res.json();   // { "remaining": 18 }
			  
			  // 依 新增/編輯 決定 placeholder
			  const initialSeats = parseInt(seatsInput.dataset.original || 0, 10);
			  // 判斷是否是編輯模式（有初始值且不為空）
			  const isEditMode = !!document.getElementById("btnSubmitEditSave");
								 
			  if (isEditMode) {
			        seatsInput.placeholder =
			          `請輸入人數 (扣除您已預定的 ${initialSeats} 位，剩餘 ${remaining} 位)`;
			  } else {
			        seatsInput.placeholder = `請輸入人數 (剩餘 ${remaining} 位)`;
			  }
			  seatsInput.max = remaining;
			  

		      // 如果使用者已經填人數
			  // editmodal情況:初始值 vs 現在值不同，才代表使用者有改過
		      const currentSeats = parseInt(seatsInput.value || 0, 10);
			  if (seatsInput.value !== initialSeats && currentSeats > remaining &&!isEditMode) {
			    seatsInput.value = '';
			    alert(`選擇人數超過剩餘名額 (僅剩 ${remaining} 位)，請重新輸入！`);
			  }

		    } catch (err) {
		      seatsInput.placeholder = '剩餘名額載入失敗';
		      seatsInput.max = '';
		    }
		  }

		  // 綁定三個欄位的 change & modal 開啟後立即跑一次
		  function bindRemainingListener () {
			const initialSeats = document.getElementById('regiSeats')?.value || 0;
			document.getElementById('regiSeats')?.setAttribute('data-initial', document.getElementById('regiSeats')?.value || '');

					    ['restoId', 'timeslotId', 'regiDate'].forEach(id => {
		      document.getElementById(id)?.addEventListener('change', refreshRemaining);
		    });
			

		    // 初次顯示 modal 時也跑一次（處理回填情況）
		    refreshRemaining();
		  }
	
	
	// ===== Add Modal =====
	  //按下新增按鈕打開modal
	  document.addEventListener("click", async function (e) {

	    const addBtn = e.target.closest("#btnAddRestoOrder");
	    if (!addBtn) return;

	    // 如果 modal 已經存在，先強制關掉並移除 DOM
	    const oldModalEl = document.getElementById("restoOrderAddModal");
	    if (oldModalEl) {
	      const modal = bootstrap.Modal.getInstance(oldModalEl);
	      if (modal) modal.hide();

	      // 等動畫結束後再清空 DOM（500ms 是 Bootstrap 預設動畫時間）
	      await new Promise(resolve => setTimeout(resolve, 500));

	      // 清掉 modal DOM
	      oldModalEl.remove();
	    }

	    fetch(`/admin/resto_order/add`)
	      .then(res => res.text())
	      .then(html => {

	        container.innerHTML = html;

	        const modalEl = document.getElementById("restoOrderAddModal");
	        if (!modalEl) {
	          alert("無法載入modal結構");
	          return;
	        }

	        const modal = new bootstrap.Modal(modalEl);
	        modal.show();

	        modalEl.addEventListener("shown.bs.modal", async () => {

	          await new Promise(resolve => setTimeout(resolve, 300)); // 等Bootstrap完成動畫
			  bindFormSubmitAdd();
			  bindRestoTimeslotSelect();
			  bindRemainingListener();
	        });

	      })
	      .catch(err => {
	        alert("載入表單失敗：" + err.message);
	      });

	  });
	  function bindFormSubmitAdd() {
	    const submitBtn = document.getElementById("btnSubmitAdd");
	    if (!submitBtn) return;
	    // 防止重複綁定
	    submitBtn.removeEventListener("click", handleAddSubmit);
	    submitBtn.addEventListener("click", handleAddSubmit);
	  }

	  function handleAddSubmit(e) {
	    e.preventDefault();

	    const submitBtn = e.currentTarget;
	    if (submitBtn.disabled) return;
	    submitBtn.disabled = true;
	    showBtnOverlay(submitBtn); // 加入 loading 遮罩

	    const form = document.getElementById("restoOrderAddForm");
	    const formData = new FormData(form);

	    // 存卷軸位置
	    sessionStorage.setItem("scrollY", window.scrollY);

	    fetch("/admin/resto_order/insert", {
	      method: "POST",
	      body: formData
	    })
	      .then(res => {
	        if (res.redirected) {
	          sessionStorage.setItem("toastMessage", "新增成功！");
	          // 成功，清空並關閉modal
	          document.getElementById("restoOrderAddForm").reset();
	          const modal = bootstrap.Modal.getInstance(document.getElementById("restoOrderAddModal"));
	          modal.hide();

	          window.location.href = res.url; // 讓瀏覽器照後端redirect去重新載入頁面
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
	        const oldBody = document.querySelector("#restoOrderAddModal .modal-body");

	        if (newBody && oldBody) {
	          oldBody.replaceWith(newBody);
	        }

	          bindFormSubmitAdd();
			  bindRestoTimeslotSelect();
			  bindRemainingListener();

	      })
	      .catch(err => {
	        alert("送出失敗：" + err.message);
	      })
	      .finally(() => {
	        submitBtn.disabled = false;
	        removeBtnOverlay(submitBtn);
	      });
	  }
	  
	
	  
	  
	  // ===== Edit Modal =====
	    //按下編輯icon按鈕打開modal
	    document.addEventListener("click", async function (e) {

	      if (e.target.closest(".btn_edit")) {
	        const editBtn = e.target.closest(".btn_edit");
	        const restoOrderId = editBtn.getAttribute("data-id");

	        if (!restoOrderId) return;

	        // 如果 modal 已經存在，先強制關掉並移除 DOM
	        const oldModalEl = document.getElementById("restoOrderEditModal");
	        if (oldModalEl) {
	          const modal = bootstrap.Modal.getInstance(oldModalEl);
	          if (modal) modal.hide();

	          // 等動畫結束後再清空 DOM（500ms 是 Bootstrap 預設動畫時間）
	          await new Promise(resolve => setTimeout(resolve, 500));

	          // 清掉 modal DOM
	          oldModalEl.remove();

	        }

	        fetch(`/admin/resto_order/edit?restoOrderId=${restoOrderId}`)
	          .then(res => res.text())
	          .then(html => {

	            container.innerHTML = html;

	            const modalEl = document.getElementById("restoOrderEditModal");
	            if (!modalEl) {
	              alert("無法載入modal結構");
	              return;
	            }

	            const modal = new bootstrap.Modal(modalEl);
	            modal.show();

	            modalEl.addEventListener("shown.bs.modal", async () => {
	              await new Promise(resolve => setTimeout(resolve, 300)); // 等Bootstrap完成動畫
				  bindFormSubmitEdit();
				  bindRestoTimeslotSelect();
				  bindRemainingListener();
	            });

	          })
	          .catch(err => {
	            alert("載入表單失敗：" + err.message);
	          });
	      }

	    });

	    function bindFormSubmitEdit() {
	      const submitBtn = document.getElementById("btnSubmitEditSave");
	      if (!submitBtn) return;

	      // 防止重複綁定
	      submitBtn.removeEventListener("click", handleEditSubmit);
	      submitBtn.addEventListener("click", handleEditSubmit);
	    }

	    function handleEditSubmit(e) {
	      e.preventDefault();

	      const submitBtn = e.currentTarget;
	      if (submitBtn.disabled) return;
	      submitBtn.disabled = true;
	      showBtnOverlay(submitBtn); // 加入 loading 遮罩

	      const form = document.getElementById("restoOrderEditForm");
	      const formData = new FormData(form);

	      // 存卷軸位置
	      sessionStorage.setItem("scrollY", window.scrollY);

	      fetch("/admin/resto_order/update", {
	        method: "POST",
	        body: formData
	      })
	        .then(res => {

	          if (res.redirected) {
	            sessionStorage.setItem("toastMessage", "編輯成功！");
	            window.location.href = res.url; // 讓瀏覽器照後端redirect去重新載入頁面

	            // 成功，清空並關閉modal
	            document.getElementById("restoOrderEditForm").reset();
	            const modal = bootstrap.Modal.getInstance(document.getElementById("restoOrderEditModal"));
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
	          const oldBody = document.querySelector("#restoOrderEditModal .modal-body");

	          if (newBody && oldBody) {
	            oldBody.replaceWith(newBody);
	          }

			  bindFormSubmitEdit();
			  bindRestoTimeslotSelect();
			  bindRemainingListener();

	        })
	        .catch(err => {
	          alert("送出失敗：" + err.message);
	        })
	        .finally(() => {
	          submitBtn.disabled = false;
	          removeBtnOverlay(submitBtn);
	        });

	    }
		



		
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
});