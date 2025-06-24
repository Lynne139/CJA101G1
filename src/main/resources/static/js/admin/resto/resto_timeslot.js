document.addEventListener("DOMContentLoaded", () => {
  
  // ===== 編輯時段 =====
  const toggleBtn = document.getElementById("editToggleBtn");
  const radios = document.querySelectorAll(".period-radio");

  let isEditing = false;

  toggleBtn.addEventListener("click", async () => {
    if (!isEditing) {
      // 切換成編輯模式
      radios.forEach(radio => radio.disabled = false);
      toggleBtn.textContent = "儲存";
      isEditing = true;
    } else {
      // 收集選項並送出
      const formData = new FormData();
      radios.forEach(radio => {
        if (radio.checked) {
          formData.append(radio.name, radio.value);
        }
      });

      // 更新表單資料
      const restoId = document.getElementById("restoSelect").value;
      formData.append("restoId", restoId);

      try {
        const res = await fetch("/admin/resto_timeslot/update", {
          method: "POST",
          body: formData
        });
        const result = await res.text(); // or .json() if you return JSON

        // 儲存成功後：鎖定 radio、按鈕變回「編輯」
        radios.forEach(radio => radio.disabled = true);
        toggleBtn.textContent = "編輯";
        isEditing = false;

        // 可加上 toast 或提示訊息
        alert("儲存成功");
      } catch (e) {
        alert("儲存失敗：" + e);
      }
    }
  });
  
  
  
  
  
  
  
  
  
  
  
});
