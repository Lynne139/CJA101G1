// ===== View Modal（沿用你現有邏輯，不用改） =====

// ===== 狀態切換 =====
document.addEventListener("click", async (e) => {

  // 完成／取消
  const btnDone   = e.target.closest(".btn_done");
  const btnCancel = e.target.closest(".btn_cancel");
  
  if (btnDone || btnCancel) {
    const btn   = btnDone || btnCancel;
    const id    = btn.getAttribute("data-id");
    const newSt = btnDone ? "DONE" : "CANCELED";

    try {
      const res = await fetch(`/admin/resto_order/${id}/status`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ status: newSt })
      });
      const json = await res.json();

      // ===== 更新 UI =====
      const card = btn.closest(".order-card");
      // 換 badge
      card.querySelector(".badge")
          .textContent = json.label;
      card.querySelector(".badge")
          .className  = `badge fs-6 bg-${json.cssClass}`;
      // 換色條
      card.querySelector(".status-bar")
          .className = `status-bar bg-${json.cssClass}`;

      // ===== 捲動到下一張未完成卡片 =====
      const next = [...document.querySelectorAll(".order-card")]
                      .find(c => c.querySelector(".badge")
                                   .textContent.trim() === "已成立"   // 未完成
                               ||  c.querySelector(".badge")
                                   .textContent.trim() === "保留");
      if (next) next.scrollIntoView({ behavior: "smooth", block: "start" });

    } catch (err) {
      alert("更新失敗：" + err.message);
    }
  }
});
