document.addEventListener('DOMContentLoaded', () => {

  /* ---------- 元件快取 ---------- */
  const seatSelect   = document.getElementById('regiSeats');
  const dateInput    = document.getElementById('regiDate');
  const hint         = document.getElementById('slot-hint');
  const timeslotBtns = Array.from(document.querySelectorAll('.time-slot'));

  /* ---------- 事件綁定 ---------- */
  seatSelect.addEventListener('change', updateSlots);
  dateInput .addEventListener('change', updateSlots);

  // 單選 / hidden input
  timeslotBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      if (btn.classList.contains('disabled')) return;
      document.querySelectorAll('.time-slot.active')
              .forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      document.getElementById('timeslotId').value = btn.dataset.slotId;
    });
  });

  /* ---------- 主函式：更新可用時段 ---------- */
  function updateSlots() {
    const seats = seatSelect.value;
    const date  = dateInput.value;
    const resto = document.body.dataset.restoId;   // 在 <body data-resto-id="1"> 之類

    // (A) 兩欄都還沒選 → 全禁用 + 顯示提示
    if (!seats || !date) {
      hint.style.display = '';
      timeslotBtns.forEach(b => b.classList.add('disabled'));
      return;
    }
    hint.style.display = 'none';   // 隱藏提示

    // (B) Ajax 取可用 map
    fetch(`/restaurants/${resto}/available?seats=${seats}&date=${date}`)
      .then(r => r.ok ? r.json() : Promise.reject(r.status))
      .then(lockMap  => {
        // fullMap: { "3":dtrue, "5":true } => true 表示「已滿，要 disable」
        timeslotBtns.forEach(btn => {
          const slotId = btn.dataset.slotId;
          // 超過今日時間也要鎖：用 btn.dataset.afterNow 之類先塞好
          const shouldLock = lockMap[slotId];
          btn.classList.toggle('disabled', shouldLock);
		  
          // 若這顆被鎖且原本是 active，就同步取消 active & hidden value
          if (shouldLock && btn.classList.contains('active')) {
            btn.classList.remove('active');
            document.getElementById('timeslotId').value = '';
          }
        });
      })
      .catch(err => console.error('取可用時段失敗', err));
  }

  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
});   // DOMContentLoaded
