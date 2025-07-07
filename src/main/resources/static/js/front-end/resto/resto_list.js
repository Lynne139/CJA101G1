document.addEventListener('DOMContentLoaded', () => {

  // 1. 只能單選：加 / 移除 active class
  document.querySelectorAll('.time-slot').forEach(btn => {
    btn.addEventListener('click', () => {
      if (btn.classList.contains('disabled')) return;          // 不可選的直接忽略

      // 先全部取消 active
      document.querySelectorAll('.time-slot.active')
              .forEach(b => b.classList.remove('active'));

      // 自己加 active，並把 timeslotId 灌進 hidden input
      btn.classList.add('active');
      document.getElementById('timeslotId').value = btn.dataset.slotId;
    
	  
		console.log(document.getElementById('timeslotId'));  
	});
  });
  
  
  
  
  

});
