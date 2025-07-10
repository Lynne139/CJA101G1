document.addEventListener('DOMContentLoaded', () => {
	
	
	// ----- 右側摘要同步 -----
	const fields = [
	  ["orderGuestName",   "#summary_guestName"],
	  ["orderGuestPhone",  "#summary_guestPhoneName"],
	  ["orderGuestEmail",  "#summary_guestEmail"],
	  ["highChairs",       "#summary_highChairs"],
	  ["regiReq",          "#summary_regiReq"]
	];

	fields.forEach(([inputId, targetSel]) => {
	  const input  = document.getElementById(inputId);
	  const target = document.querySelector(targetSel);

	  if (!input || !target) return;

	  // 初始填
	  target.textContent = input.value || "—";

	  // keyup / change 即時更新
	  input.addEventListener("input", () => {
	    target.textContent = input.value || "—";
	  });
	});
	
	
	const form      = document.getElementById('booking');
	const submitBtn = document.getElementById('confirmBtn');
	
	form.addEventListener('submit', () => {
	    showBtnOverlay(submitBtn);
	  });
	


	
	
	
	
	
	
	
	
	
	




});   // DOMContentLoaded




