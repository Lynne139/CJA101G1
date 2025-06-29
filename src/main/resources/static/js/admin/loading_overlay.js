// ===== btn =====

//避免表單送出按鈕連續點擊
// 顯示 loading 遮罩在按鈕上
function showBtnOverlay(btnEl) {
  if (!btnEl) return;

  // 建立 overlay
  const overlay = document.createElement("div");
  overlay.className = "btn_overlay";
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

  const overlay = btnEl.querySelector(".btn_overlay");
  if (overlay) {
    btnEl.removeChild(overlay);
  }

  btnEl.disabled = false;
}

// ===== panel =====
// 顯示 loading 遮罩在整個 panel 區塊上
function showPanelOverlay(panelEl) {
  if (!panelEl) return;

  // 避免多次套用
  if (panelEl.querySelector(".panel_overlay")) return;

  const overlay = document.createElement("div");
  overlay.className = "panel_overlay";
  overlay.innerHTML = `<span><i class="fas fa-spinner fa-spin fa-2x"></i></span>`;

  // 確保父元素定位為 relative
  if (getComputedStyle(panelEl).position === "static") {
    panelEl.style.position = "relative";
  }

  panelEl.appendChild(overlay);
}

// 移除 panel 上的 loading 遮罩
function removePanelOverlay(panelEl) {
  if (!panelEl) return;

  const overlay = panelEl.querySelector(".panel_overlay");
  if (overlay) {
    panelEl.removeChild(overlay);
  }
}
