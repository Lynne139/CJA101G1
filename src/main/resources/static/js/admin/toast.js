document.addEventListener("DOMContentLoaded", () => {
  const msg = sessionStorage.getItem("toastMessage");
  if (msg) {
    showToast(msg);
    sessionStorage.removeItem("toastMessage");
  }
});

// 你原本就有定義的 showToast()
function showToast(message) {
  const toastEl = document.createElement("div");
  toastEl.className = "toast align-items-center text-white bg-success border-0 position-fixed bottom-0 end-0 m-3";
  toastEl.setAttribute("role", "alert");
  toastEl.innerHTML = `
    <div class="d-flex">
      <div class="toast-body">${message}</div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
    </div>
  `;
  document.body.appendChild(toastEl);

  const toast = new bootstrap.Toast(toastEl);
  toast.show();

  toastEl.addEventListener("hidden.bs.toast", () => {
    toastEl.remove(); // 移除 DOM
  });
}