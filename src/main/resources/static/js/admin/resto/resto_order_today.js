document.addEventListener('DOMContentLoaded', () => {

  // ===== 卡片收合 / 展開 =====
  // 設定卡片點擊展開/收合
  document.querySelectorAll(".order-card").forEach(card => {
      // 同時抓 up 與 down，哪個有就用哪個
      const caret  = card.querySelector(".fa-caret-up, .fa-caret-down");
      const detail = card.querySelector(".guest_detail");

      // 若卡片少任何一塊，直接跳過這張卡，避免報錯
      if (!caret || !detail) return;

      // 預設收合
      detail.style.display = "none";

      card.addEventListener("click", e => {
		// 若點到按鈕，此事件不處理
		if (e.target.closest("button")) return;

        const isOpen = detail.style.display !== "none";
        detail.style.display = isOpen ? "none" : "block";

        // caret 方向 toggle
        caret.classList.toggle("fa-caret-up");
        caret.classList.toggle("fa-caret-down");
      });
    });



	// ===== 完成 / 取消 按鈕 AJAX =====
	// 抓統計6個欄位
	const sField = (name) => document.querySelector(`[data-field='${name}']`);

	// 點擊 DONE / CANCELED 按鈕 
	document.addEventListener("click", async (e) => {

	  const btn = e.target.closest(".btn_done, .btn_cancel");
	  if (!btn) return;

	  const card    = btn.closest(".order-card");
	  const orderId = btn.dataset.id;
	  const action  = btn.classList.contains("btn_done") ? "DONE" : "CANCELED";

	  if (!orderId || card.classList.contains("updating")) return;

		showPanelOverlay(card);
	  try {
	    const res = await fetch(`/admin/resto_order/${orderId}/status`, {
	      method: "POST",
		  headers: {
		      "Content-Type": "application/json",
		      "X-Requested-With": "fetch"
		    },
		    body: JSON.stringify({ status: action })
	    });
	    if (!res.ok) throw new Error(await res.text());

	    const json = await res.json();     // {label, cssClass, summary}

	    // ---- 1. 更新卡片顏色 & 標籤 ----
	    const statusBar = card.querySelector(".status-bar");
	    const badge     = statusBar.querySelector(".badge");
	    statusBar.className = `status-bar bg-${json.cssClass}`;
	    badge.className     = `badge fs-6 bg-${json.cssClass}`;
	    badge.textContent   = json.label;

	    // ---- 2. 收合 & 捲到下一張 ----
	    const detail = card.querySelector(".guest_detail");
	    const caret  = card.querySelector(".fa-caret-up, .fa-caret-down");
	    detail.style.display = "none";
	    caret.classList.remove("fa-caret-down");
	    caret.classList.add("fa-caret-up");

	    const next = card.nextElementSibling;
	    if (next?.classList.contains("order-card")) {
	      next.scrollIntoView({ behavior: "smooth", block: "center" });
	    }

	    // ---- 3. 刷新六個統計數字 ----
	    if (json.summary) {
	      const sum = json.summary;     // DTO 的欄位名稱
	      sField("total").textContent      = sum.total;
	      sField("done").textContent       = sum.done;
	      sField("noshow").textContent     = sum.noshow;
	      sField("ongoing").textContent    = sum.ongoing;
	      sField("canceled").textContent   = sum.canceled;
	      sField("totalSeats").textContent = sum.totalSeats;
	    }

	  } catch (err) {
	    alert("更新失敗: " + err.message);
	  } finally {
		removePanelOverlay(card);
	  }
	});


	// ===== SSE推播註冊 =====
	function setupSSE() {
	  const eventSource = new EventSource("/sse/order-status");

	  eventSource.addEventListener("order-status-update", (e) => {
	    console.log("SSE 訊息：", e.data);
	    if (e.data === "refresh") {
	      refreshSummary();      // 更新統計數字
	      refreshCardStatuses();   // 更新卡片
	    }
	  });

	  eventSource.onerror = (err) => {
	    console.error("SSE 錯誤", err);
	  };
	}

	const RESTO_ID = parseInt(
	  document.getElementById("orderTodayPage")?.dataset?.restoId || "-1", 10
	);
	
	setupSSE();
	
	
	async function refreshSummary() {
	  const r = await fetch(`/admin/resto_order/summary-json?restoId=${RESTO_ID}`);
	  if (!r.ok) return;
	  const s = await r.json();

	  sField("total").textContent      = s.total;
	  sField("done").textContent       = s.done;
	  sField("noshow").textContent     = s.noshow;
	  sField("ongoing").textContent    = s.ongoing;
	  sField("canceled").textContent   = s.canceled;
	  sField("totalSeats").textContent = s.totalSeats;
	}

	async function refreshCardStatuses() {
	  const r = await fetch(`/admin/resto_order/statuses?restoId=${RESTO_ID}`);
	  if (!r.ok) return;
	  const map = await r.json();          // { "9":{cssClass:"success",label:"已完成"}, ... }

	  Object.entries(map).forEach(([id, st]) => {
	    // 透過 data-id 找同一張卡
	    const btn     = document.querySelector(`button[data-id='${id}']`);
	    if (!btn) return;
	    const card    = btn.closest(".order-card");
	    const bar     = card.querySelector(".status-bar");
	    const badge   = bar.querySelector(".badge");

	    bar.className   = `status-bar bg-${st.cssClass}`;
	    badge.className = `badge fs-6 bg-${st.cssClass}`;
	    badge.textContent = st.label;
	  });
	}



  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
});
