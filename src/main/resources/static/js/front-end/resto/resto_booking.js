document.addEventListener('DOMContentLoaded', () => {
	
	
	document.querySelector('#orderGuestName').addEventListener('input', (e) => {
	  document.querySelector('#summary_guestName').textContent = e.target.value;
	});

	document.querySelector('#orderGuestPhone').addEventListener('input', (e) => {
	  document.querySelector('#summary_guestPhoneName').textContent = e.target.value;
	});

	document.querySelector('#orderGuestEmail').addEventListener('input', (e) => {
	  document.querySelector('#summary_guestEmail').textContent = e.target.value;
	});

	document.querySelector('#highChairs').addEventListener('input', (e) => {
	  document.querySelector('#summary_highChairs').textContent = e.target.value;
	});

	document.querySelector('#regiReq').addEventListener('input', (e) => {
	  document.querySelector('#summary_regiReq').textContent = e.target.value;
	});




});   // DOMContentLoaded




