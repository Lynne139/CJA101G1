// ===== resto =====
document.getElementById("restoSeatsTotal").addEventListener("input", function () {
  this.value = this.value.replace(/\D/g, ""); // 移除非數字
});