//避免表單送出按鈕連續點擊
// 顯示 loading 遮罩在按鈕上
function showBtnOverlay(btnEl) {
  if (!btnEl) return;

  // 建立 overlay
  const overlay = document.createElement("div");
  overlay.className = "btn-overlay";
  overlay.innerHTML = `<span><i class="fas fa-spinner fa-spin fa-lg"></i></span>`;

  // 確保定位相對
  if (getComputedStyle(btnEl).position === "static") {
    btnEl.style.position = "relative";
  }  btnEl.disabled = true;
  btnEl.appendChild(overlay);
}

// 移除 loading 遮罩
function removeBtnOverlay(btnEl) {
  if (!btnEl) return;

  const overlay = btnEl.querySelector(".btn-overlay");
  if (overlay) {
    btnEl.removeChild(overlay);
  }

  btnEl.disabled = false;
}
