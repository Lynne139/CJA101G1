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
      pageLength: 5,
      lengthMenu: [5, 10],
      order: [[0, 'asc']],
      autoWidth: false,
      columnDefs: [
        { targets: [0], width: "5%"},
        { targets: [1], width: "8%"},
        { targets: [2], width: "5%"},
        { targets: [3], width: "5%"},
        { targets: [4], width: "5%"},
        { targets: [5], width: "7%"},
        { targets: [6], width: "13%"},
        { targets: [7], width: "10%"},
        { targets: [8], width: "7%"},
        { targets: [9], width: "7%"},
        { targets: [10], width: "4%"},
        { targets: [11], width: "10%"},
        { targets: [12], width: "7%", orderable: false},
        { targets: [13], width: "7%", orderable: false}
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

	
	// ===== 新增區段 =====
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

	        //確保modal內的textarea完全呈現(bs開modal預設有動畫可能導致延遲)
	        modalEl.addEventListener("shown.bs.modal", async () => {

	          await new Promise(resolve => setTimeout(resolve, 300)); // 等Bootstrap完成動畫
			  bindRestoTimeslotSelect(); 
			  bindFormSubmitAdd();

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

	        setTimeout(() => {
	          bindFormSubmitAdd();
	        }, 300); // 與動畫一致延遲時間

	      })
	      .catch(err => {
	        alert("送出失敗：" + err.message);
	      })
	      .finally(() => {
	        submitBtn.disabled = false;
	        removeBtnOverlay(submitBtn);
	      });
	  }
	  

	  // add modal中，要先選好resto才顯示對應timeslot選擇欄位
	  function bindRestoTimeslotSelect () {
	    const restoSelect    = document.getElementById('restoId');
	    const timeslotSelect = document.getElementById('timeslotId');
	    if (!restoSelect || !timeslotSelect) return;

	    filterTimeslot(); // 初始就跑一次，處理回填

	    restoSelect.addEventListener('change', filterTimeslot);

	    function filterTimeslot () {
	      const selected = restoSelect.value;

	      timeslotSelect.querySelectorAll('option').forEach(opt => {
	        const restoId = opt.getAttribute('data-resto-id');

	        // 顯示提示用選項，或屬於當前餐廳的選項
	        if (!restoId || restoId === selected) {
	          opt.hidden = false;
	        } else {
	          opt.hidden = true;
	        }
	      });
	    }
	  }

	  const regiDateInput = document.getElementById("regiDate");
	  if (regiDateInput) {
	    regiDateInput.addEventListener("input", function () {
	      const selected = this.value;
	      if (fullDates.includes(selected)) {
	        alert("此日期已滿額，請選擇其他日期");
	        this.value = "";
	      }
	    });
	  }
	  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
});