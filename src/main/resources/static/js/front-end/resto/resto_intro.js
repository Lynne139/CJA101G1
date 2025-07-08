// -------- 全域函式，供多處調用 --------
function updateSlots() {
  const seatSelect   = document.getElementById('regiSeats');
  const dateInput    = document.getElementById('regiDate');
  const hint         = document.getElementById('slot-hint');
  const timeslotBtns = Array.from(document.querySelectorAll('.time-slot'));

  const seats = seatSelect?.value;
  const date  = dateInput?.value;
  const resto = document.body.dataset.restoId;

  if (!seats || !date) {
    hint.style.display = '';
    timeslotBtns.forEach(b => b.classList.add('disabled'));
    return;
  }

  hint.style.display = 'none';

  fetch(`/restaurants/${resto}/available?seats=${seats}&date=${date}`)
    .then(r => r.ok ? r.json() : Promise.reject(r.status))
    .then(lockMap  => {
      timeslotBtns.forEach(btn => {
        const slotId = btn.dataset.slotId;
        const shouldLock = lockMap[slotId];

        btn.classList.toggle('disabled', shouldLock);

        if (shouldLock && btn.classList.contains('active')) {
          btn.classList.remove('active');
          document.getElementById('timeslotId').value = '';
        }
      });

      // ==== 回填按鈕選擇狀態 ====
      const selectedId = document.getElementById("selectedTimeslotId")?.value;
      if (selectedId) {
        const targetBtn = document.querySelector(`.time-slot[data-slot-id="${selectedId}"]:not(.disabled)`);
        if (targetBtn) {
          targetBtn.click();
        } else {
          const warning = document.querySelector("#timeslotWarning");
          if (warning) {
            warning.classList.remove("d-none");
            warning.textContent = "您原先選擇的時段已無效，請重新選擇。";
          }
        }
      }
    })
    .catch(err => console.error('取可用時段失敗', err));
}

// -------- 初次 DOM Ready --------
document.addEventListener('DOMContentLoaded', () => {

  const seatSelect   = document.getElementById('regiSeats');
  const dateInput    = document.getElementById('regiDate');
  const timeslotBtns = Array.from(document.querySelectorAll('.time-slot'));

  // 綁定事件
  seatSelect?.addEventListener('change', updateSlots);
  dateInput ?.addEventListener('change', updateSlots);

  timeslotBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      if (btn.classList.contains('disabled')) return;
      document.querySelectorAll('.time-slot.active')
              .forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      document.getElementById('timeslotId').value = btn.dataset.slotId;
    });
  });

  // 頁面一進來若帶值就自動刷新
  const seat = seatSelect?.value;
  const date = dateInput ?.value;
  if (seat && date) updateSlots();

  // 驗證錯誤自動捲動
  const firstError = document.querySelector(".text-danger:has(span)");
  if (firstError) {
    firstError.scrollIntoView({ behavior: "smooth", block: "center" });
  }
});


// -------- 返回頁面自動刷新 --------
window.addEventListener("pageshow", () => {
  const seat = document.getElementById("regiSeats")?.value;
  const date = document.getElementById("regiDate") ?.value;
  if (seat && date) updateSlots();
});


