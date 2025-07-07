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
if (window.location.pathname.includes('/member/order/roomOrder')) {
  bindEditButton('.btn_edit', '#modalContainer', '/member/order/roomOrder/edit?roomOrderId=');
  // 新增檢視功能
  $(document).on('click', '.btn_view', function() {
    var orderId = $(this).data('id');
    $('#modalContainer').load('/member/order/roomOrder/view?roomOrderId=' + orderId, function() {
      $('#roomoViewModal').modal('show');
    });
  });
}

// 取消訂單
if (window.location.pathname.includes('/member/order/roomOrder')) {
  $(document).on('click', '.btn_cancel', function() {
    var orderId = $(this).data('id');
    if (confirm('確定要取消此訂單嗎？取消後不可再修改，需重新下單。')) {
      $('#modalContainer').load('/member/order/roomOrder/cancel?roomOrderId=' + orderId, function() {
        // 可在這裡加上取消成功的提示或後續處理
      });
    }
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

// ====== 新增入住/退房日與房量檢查 ======
function checkRoomInventoryAndDate(callback) {
  const checkIn = document.getElementById('editCheckInDate').value;
  const checkOut = document.getElementById('editCheckOutDate').value;
  let valid = true;
  let errorMsg = '';
  let requests = [];

  // 先檢查日期邏輯
  if (!checkIn || !checkOut) {
    callback(false, '請選擇入住與退房日期');
    return;
  }
  if (checkIn >= checkOut) {
    callback(false, '入住日期不可大於或等於退房日期！');
    return;
  }

  // 取得所有明細
  $('#roomOrderEditForm [name^="orderDetails"][name$="roomOrderListId"]').each(function() {
    const name = $(this).attr('name');
    const matchType = name.match(/orderDetails\[(\d+)\]\.roomOrderListId/);
    if (matchType) {
      const idx = matchType[1];
      let roomTypeId = $(`[name='orderDetails[${idx}].roomType.roomTypeId']`).val() ||
                       $(`[name='orderDetails[${idx}].roomTypeId']`).val();
      let roomTypeName = $(`[name='orderDetails[${idx}].roomType.roomTypeName']`).val();
      let roomAmount = $(`[name='orderDetails[${idx}].roomAmount']`).val();
      if (roomTypeId && roomAmount) {
        requests.push(
          $.get(`/member/order/roomOrder/${roomTypeId}/check_schedule`, {
            start: checkIn,
            end: checkOut
          }).then(function(available) {
            if (parseInt(available) < parseInt(roomAmount)) {
              valid = false;
              errorMsg = `房型 ${roomTypeName} 剩餘房間不足，請洽客服`;
            }
          })
        );
      }
    }
  });

  // 等所有查詢完成
  $.when.apply($, requests).then(function() {
    callback(valid, errorMsg);
  });
}

// 綁定入住/退房日 change 事件
$(document).on('change', '#editCheckInDate, #editCheckOutDate', function() {
  checkRoomInventoryAndDate(function(valid, msg) {
    if (!valid) {
      alert(msg);
      $('#roomOrderEditForm [type=submit]').prop('disabled', true);
    } else {
      $('#roomOrderEditForm [type=submit]').prop('disabled', false);
    }
  });
});

// 送出前再次檢查
$(document).on('submit', '#roomOrderEditForm', function(e) {
  let allowSubmit = false;
  e.preventDefault();
  checkRoomInventoryAndDate(function(valid, msg) {
    if (!valid) {
      alert(msg);
      allowSubmit = false;
    } else {
      allowSubmit = true;
      $('#roomOrderEditForm')[0].submit();
    }
  });
});
// ====== END ======