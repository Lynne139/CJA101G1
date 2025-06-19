
/* 展開與active效果已透過URI在thymeleaf判斷，但首次/首頁無對應url時仍須透過此JS來提供主項收合邏輯 */
	
document.addEventListener("DOMContentLoaded", function () {	
	const sidebarLinks = document.querySelectorAll(".sidebar_link"); //所點主項

	sidebarLinks.forEach(link => {
		link.addEventListener("click", function (e) {
			e.preventDefault();
			const section = this.closest(".sidebar_section"); //所點主項區塊
			const submenu = section.querySelector(".submenu_block"); //所點主項的子項區塊
			const isActive = section.classList.contains("-active");

			// 收合其他
			document.querySelectorAll(".sidebar_section").forEach(sec => {
				if (sec !== section) {
					sec.classList.remove("-active");
					sec.querySelector(".submenu_block")?.classList.remove("show");
				}
			});

			// 切換自己
			if (isActive) {
				section.classList.remove("-active");
				submenu?.classList.remove("show");
			} else {
				section.classList.add("-active");
				submenu?.classList.add("show");
			}
		});
	});

	
	const hamburgerBtn = document.querySelector(".btn_hamburger");
	  const sidebar = document.querySelector(".sidebar");
	  const closeBtn = document.querySelector(".btn_sidebar_close");

	  if (hamburgerBtn) {
		hamburgerBtn.addEventListener("click", function () {
	    sidebar.classList.toggle("-on");
	    closeBtn.classList.add("-on");
	  	});
	  }

	  if (closeBtn) {
	    closeBtn.addEventListener("click", function () {
	      sidebar.classList.remove("-on");
	    });
	  }

	  window.addEventListener("resize", function () {
	    if (window.innerWidth >= 768) {
	      sidebar.classList.remove("-on");
	      closeBtn.classList.remove("-on");
	    }
	  });

});


