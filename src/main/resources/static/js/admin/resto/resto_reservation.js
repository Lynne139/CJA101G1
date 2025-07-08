
document.addEventListener("DOMContentLoaded", function () {


  initRestoRsvtTable(); // 初次載入表格

  // ===== DataTables =====
  function initRestoRsvtTable() {
    const table = $('#restoReservationTable');
	
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
        { targets: [0], width: "20%" },
        { targets: [1], width: "25%" },
        { targets: [2], width: "11%" },
        { targets: [3], width: "11%"},
        { targets: [4], width: "11%"},
        { targets: [5], width: "11%"},
        { targets: [6], width: "11%"},
      ],
	  scrollX:true,
      searching: false,
      ordering: true,
      info: true,
    });
	
  }
  $(window).on('resize', function () {
            $('#restoReservationTable').DataTable().columns.adjust();
  });


  // ===== 清空複合查詢欄位 =====
  const clearBtn = document.querySelector(".btn_search_clear");
  const form = document.querySelector(".filter");

  clearBtn.addEventListener("click", () => {
    // 直接回到基礎路徑(清空欄位值)
    location.href = location.pathname;
  });






});
