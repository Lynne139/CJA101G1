function bindEditButton(editBtnSelector, modalContainerSelector, editUrlPrefix) {
  $(document).on('click', editBtnSelector, function() {
    var orderId = $(this).data('id');
    $(modalContainerSelector).load(editUrlPrefix + orderId, function() {
      $('#roomoEditModal').modal('show');
      // 綁定表單驗證
      const form = document.getElementById('roomOrderEditForm');
      if (form) {
        const checkIn = document.getElementById('editCheckInDate');
        const checkOut = document.getElementById('editCheckOutDate');
        form.addEventListener('submit', function (e) {
          if (checkIn.value && checkOut.value && checkIn.value > checkOut.value) {
            e.preventDefault();
            alert('入住日期不可大於退房日期！');
            checkOut.focus();
          }
        });
      }
    });
  });
}
// 前台初始化
if (window.location.pathname.includes('/member/room/roomOrderView')) {
  bindEditButton('.btn_edit', '#modalContainer', '/member/room/edit?roomOrderId=');
  // 新增檢視功能
  $(document).on('click', '.btn_view', function() {
    var orderId = $(this).data('id');
    $('#modalContainer').load('/member/room/view?roomOrderId=' + orderId, function() {
      $('#roomoViewModal').modal('show');
    });
  });
}
// 後台初始化
if (window.location.pathname.includes('/admin/roomo_info')) {
  bindEditButton('.btn_edit', '#roomoInfoModalContainer', '/admin/roomo_info/edit?roomOrderId=');
} 

// ===== DataTables =====
function initRoomoTable() {
  const table = $('#roomoTable');

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
      scrollX: true,
      fixedColumns: {
          leftColumns: 0,
          rightColumns: 1
      },
      columnDefs: [
          { targets: [0], width: "20%" },
          { targets: [1], width: "25%" },
          { targets: [2], width: "25%", orderable: false },
          { targets: [3], width: "10%", orderable: false },
          { targets: [4], width: "5%", orderable: false },
          { targets: [5], width: "15%", orderable: false },
          { targets: [6], width: "15%" },
          { targets: [7], width: "15%" },
          { targets: [8], width: "15%" },
          { targets: [9], width: "15%", orderable: false }

      ],
      searching: false,
      ordering: true,
      info: true,
      // 預設隱藏已取消的訂單
      initComplete: function () {
          // 檢查是否有訂單狀態篩選條件
          const urlParams = new URLSearchParams(window.location.search);
          const isEnabled = urlParams.get('isEnabled');

          // 如果沒有明確指定要顯示已取消的訂單，則隱藏它們
          if (isEnabled !== '0') {
              this.api().column(3).search('^(?!.*取消).*$', true, false).draw();
          }
      }
  });
}