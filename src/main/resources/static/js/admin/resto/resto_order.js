document.addEventListener("DOMContentLoaded", function () {

  //modal載入位置
  const container = document.getElementById("restoOrderModalContainer");


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
        { targets: [9], width: "5%"},
        { targets: [10], width: "4%"},
        { targets: [11], width: "10%"},
        { targets: [12], width: "9%", orderable: false},
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

  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
});