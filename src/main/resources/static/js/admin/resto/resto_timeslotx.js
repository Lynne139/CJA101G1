document.addEventListener("DOMContentLoaded", () => {
	
  //modal載入位置
  const container = document.getElementById("restoTimeslotModalContainer");
	
  const savedScrollY = sessionStorage.getItem("scrollY");
    if (savedScrollY !== null) {
      window.scrollTo(0, parseInt(savedScrollY, 10));
      sessionStorage.removeItem("scrollY");
    }
	
	
  // ===== 區段 ========================================================
  
  // ===== 刪除區段 =====
  document.addEventListener("click", function (e) {
      if (e.target.closest(".btn_period_del")) {
        const btn = e.target.closest(".btn_period_del");
        const periodId = btn.getAttribute("data-id");
		const restoId = document.getElementById("restoSelect").value;
		
        if (!periodId || !restoId) return;

        if (confirm("項目一旦刪除將無法復原，是否確定刪除？")) {
          fetch(`/admin/resto_timeslot/period/delete?periodId=${periodId}&restoId=${restoId}`, {
            method: 'GET'
          })
  		.then(res => {
  		  if (res.redirected) {
  			sessionStorage.setItem("toastMessage", "刪除成功！");
  		    window.location.href = res.url; // 讓DataTables因為整頁刷新而重載資料
  		    return;
  		  }
  		  return res.text(); // 若失敗沒redirect，才繼續處理
  		})
  		.catch(err => {
  		  alert("封存失敗：" + err.message);
  		});
        }
      }
    });
	
	
  // ===== 新增區段 =====
  //按下新增按鈕打開modal
  document.addEventListener("click",async function (e) {

        const addBtn = e.target.closest("#btnAddPeriod");
		const restoId = document.getElementById("restoSelect").value;
		
		if (!addBtn) return;

        if (!restoId) return;
		
	// 如果 modal 已經存在，先強制關掉並移除 DOM
	  const oldModalEl = document.getElementById("periodAddModal");
	  if (oldModalEl) {
	      const modal = bootstrap.Modal.getInstance(oldModalEl);
	      if (modal) modal.hide();
		
  		  // 等動畫結束後再清空 DOM（500ms 是 Bootstrap 預設動畫時間）
		  await new Promise(resolve => setTimeout(resolve, 500));
	
	      // 清掉 modal DOM
	      oldModalEl.remove();
	  }
	  
	  fetch(`/admin/resto_timeslot/period/add?restoId=${restoId}`)
	        .then(res => res.text())
	        .then(html => {
	  		
	          container.innerHTML = html;

	          const modalEl = document.getElementById("periodAddModal");
	          if (!modalEl) {
	            alert("無法載入modal結構");
	            return;
	          }
			  
	  		  const modal = new bootstrap.Modal(modalEl);
	  		  modal.show();
	  			
	  		  //確保modal內的textarea完全呈現(bs開modal預設有動畫可能導致延遲)
	  		  modalEl.addEventListener("shown.bs.modal", async () => {

	  			  await new Promise(resolve => setTimeout(resolve, 300)); // 等Bootstrap完成動畫
	  			  bindFormSubmitAddPeriod();
	  			  
	  			});

	  	    })
	        .catch(err => {
	          alert("載入表單失敗：" + err.message);
	        });
			
	function bindFormSubmitAddPeriod() {
	    const submitBtn = document.getElementById("btnSubmitAdd");
	    if (!submitBtn) return;

	    submitBtn.addEventListener("click", function (e) {
	    e.preventDefault();
		btn.disabled = true;
		showBtnOverlay(submitBtn); // 加入 loading 遮罩

	      const form = document.getElementById("periodAddForm");
	      const formData = new FormData(form);

	      fetch("/admin/resto_timeslot/period/insert", {
	        method: "POST",
	        body: formData
	      })
	        .then(res => {
				
				if (res.redirected) {
					
					sessionStorage.setItem("toastMessage", "新增成功！");

				    // 成功，清空並關閉modal
				    document.getElementById("periodAddForm").reset();

				    const modal = bootstrap.Modal.getInstance(document.getElementById("periodAddModal"));
				    modal.hide();
					  
					window.location.href = res.url; // 讓瀏覽器照後端redirect去重新載入頁面
					  
					} else {
				      return res.text(); // 失敗時回傳HTML
				    }
				
			})
	        .then(html => {
				
				if (!html) return; // 成功就不會拿到res(modal fragment+表單填入的內容)，也就不處理下面
				  
				  //錯誤時僅更新 modal body（避免整個 modal 重建）
				  const parser = new DOMParser();
				  const doc = parser.parseFromString(html, "text/html");

				  const newBody = doc.querySelector(".modal-body");
				  const oldBody = document.querySelector("#periodAddModal .modal-body");

				  if (newBody && oldBody) {
				    oldBody.replaceWith(newBody);
				  }

				  // 模擬動畫結束後再初始化TinyMCE（因為modal沒重新開不能用shown.bs.modal來監聽）
				    setTimeout(() => {
				      bindFormSubmitAddPeriod();
				    }, 300); // 與動畫一致延遲時間
	
	        })
	        .catch(err => {
	          alert("送出失敗：" + err.message);
			  })
			.finally(() => {
			  btn.disabled = false;
			  removeBtnOverlay(submitBtn);
			});
	    });
	  }
			
  });
		  
		  
  // ===== 編輯區段 =====
  //按下編輯icon按鈕打開modal
  document.addEventListener("click",async function (e) {
	
  if (e.target.closest(".btn_period_edit")) {
      const editBtn = e.target.closest(".btn_period_edit");
      const periodId = editBtn.getAttribute("data-id");
	  const restoId = document.getElementById("restoSelect").value;
	  
      if (!periodId) return;

	    // 如果 modal 已經存在，先強制關掉並移除 DOM
      	const oldModalEl = document.getElementById("periodEditModal");
	    if (oldModalEl) {
	      const modal = bootstrap.Modal.getInstance(oldModalEl);
	      if (modal) modal.hide();

	      // 等動畫結束後再清空 DOM（500ms 是 Bootstrap 預設動畫時間）
	      await new Promise(resolve => setTimeout(resolve, 500));

	      // 清掉 modal DOM
	      oldModalEl.remove();
		  
	    }

  
      fetch(`/admin/resto_timeslot/period/edit?periodId=${periodId}&restoId=${restoId}`)
        .then(res => res.text())
        .then(html => {
		
          container.innerHTML = html;

          const modalEl = document.getElementById("periodEditModal");
          if (!modalEl) {
            alert("無法載入modal結構");
            return;
          }

		      const modal = new bootstrap.Modal(modalEl);
		      modal.show();
			
		      modalEl.addEventListener("shown.bs.modal", async () => {
			    await new Promise(resolve => setTimeout(resolve, 300)); // 等Bootstrap完成動畫
			    bindFormSubmitEditPeriod();
				
			  });

  	  })
        .catch(err => {
          alert("載入表單失敗：" + err.message);
        });
	}
  });
  
  function bindFormSubmitEditPeriod() {
    const submitBtn = document.getElementById("btnSubmitEditSave");
    if (!submitBtn) return;

    submitBtn.addEventListener("click", function (e) {
      e.preventDefault();
	  btn.disabled = true;

	showBtnOverlay(submitBtn); // 加入 loading 遮罩
	
      const form = document.getElementById("periodEditForm");
      const formData = new FormData(form);

      fetch("/admin/resto_timeslot/period/update", {
        method: "POST",
        body: formData
      })
        .then(res => {

			if (res.redirected) {
			sessionStorage.setItem("toastMessage", "編輯成功！");				
				window.location.href = res.url; // 讓瀏覽器照後端redirect去重新載入頁面
				
			      // 成功，清空並關閉modal
			      document.getElementById("periodEditForm").reset();
			      const modal = bootstrap.Modal.getInstance(document.getElementById("periodEditModal"));
			      modal.hide();
				  
				} else {
			      return res.text(); // 失敗時回傳HTML
			    }
			
		})
        .then(html => {
			
			if (!html) return; // 成功就不會拿到res(表單填入的內容)，也就不處理下面

			  //錯誤時僅更新 modal body（避免整個 modal 重建）
			  const parser = new DOMParser();
			  const doc = parser.parseFromString(html, "text/html");

			  const newBody = doc.querySelector(".modal-body");
			  const oldBody = document.querySelector("#periodEditModal .modal-body");

			  if (newBody && oldBody) {
			    oldBody.replaceWith(newBody);
			  }

			    setTimeout(() => {
			      bindFormSubmitEditPeriod();
			    }, 300); // 與動畫一致延遲時間

				
        })
        .catch(err => {
          alert("送出失敗：" + err.message);
		})
	  .finally(() => {
		btn.disabled = false;
		removeBtnOverlay(submitBtn);
      });
    });
  }
	  
	  
	  
  // ===== 排序按鈕 =====
  // 排序
  document.addEventListener("click", async (e) => {
    const btn = e.target.closest(".btn_move_up, .btn_move_down");
    if (!btn) return;

    // 如果 disabled 就直接 return
    if (btn.disabled) return;
	
	const panelEl = document.querySelector(".panel_periodntimeslot");
	showPanelOverlay(panelEl);


    const periodId = btn.getAttribute("data-id");
    const dir      = btn.getAttribute("data-dir");

	let formData = new FormData();
    formData.append("periodId", periodId);
    formData.append("dir", dir);

    
    fetch("/admin/resto_timeslot/period/move", {
        method : "POST",
        body:new URLSearchParams(formData)
      }).then(res => res.json())
	    .then(data => {
			if (data.msg === "move success") {

		        // 前端即時交換DOM，不刷新
		        const periodBlock = btn.closest(".period_block");
		        if (!periodBlock) return;

		        if (dir === "up" && periodBlock.previousElementSibling) {
		          periodBlock.parentElement.insertBefore(periodBlock, periodBlock.previousElementSibling);
		        }
		        if (dir === "down" && periodBlock.nextElementSibling) {
		          periodBlock.parentElement.insertBefore(periodBlock.nextElementSibling, periodBlock);
		        }

		        // 重新決定上下箭頭是否要disabled
		        refreshMoveButtons();
		    }else {
	          alert("移動失敗：" + data.message);
	        }

	    })
		.catch(err => {
		    alert("移動失敗：" + err.message);
		})
		.finally(() => {
		  	removePanelOverlay(panelEl);
		});
  });


  // 讓頂/底元素的箭頭切換disabled狀態
  function refreshMoveButtons(){
    const allPeriods = document.querySelectorAll(".period_block");
    allPeriods.forEach(p => {
      p.querySelector(".btn_move_up").disabled   = false;
      p.querySelector(".btn_move_down").disabled = false;
    });
    if (allPeriods.length){
      allPeriods[0].querySelector(".btn_move_up").disabled =
        true;                                    // 第一個不能再上
      allPeriods[allPeriods.length-1]
        .querySelector(".btn_move_down").disabled = true; // 最後一個不能再下
    }
  }

  
  
  
  // ===== 時段 ========================================================

  // ===== 刪除時段 =====
  document.addEventListener("click", function (e) {

        if (e.target.closest(".btn_timeslot_del")) {
          const btn = e.target.closest(".btn_timeslot_del");
          const timeslotId = btn.getAttribute("data-id");
  		  const restoId = document.getElementById("restoSelect").value;
  		
          if (!timeslotId || !restoId) return;

          if (confirm("項目一旦刪除將無法復原，是否確定刪除？")) {
			// 存卷軸位置
			sessionStorage.setItem("scrollY", window.scrollY);
	
            fetch(`/admin/resto_timeslot/timeslot/delete?timeslotId=${timeslotId}&restoId=${restoId}`, {
              method: 'GET'
            })
    		.then(res => {
    		  if (res.redirected) {				
    			sessionStorage.setItem("toastMessage", "刪除成功！");
    		    window.location.href = res.url;
    		    return;
    		  }
    		  return res.text(); // 若失敗沒redirect，才繼續處理
    		})
    		.catch(err => {
    		  alert("封存失敗：" + err.message);
    		});
          }
        }
      });
	  
  
  // ===== 新增時段 =====
  //按下新增按鈕打開modal
  document.addEventListener("click",async function (e) {

  	const addBtn = e.target.closest(".btn_add_timeslot");
  		
  	if (!addBtn) return;
	
	const periodId = addBtn.getAttribute("data-period-id");
	if (!periodId) return;

  		
  	// 如果 modal 已經存在，先強制關掉並移除 DOM
  	const oldModalEl = document.getElementById("timeslotAddModal");
  	if (oldModalEl) {
  	  const modal = bootstrap.Modal.getInstance(oldModalEl);
  	  if (modal) modal.hide();
  	
	  // 等動畫結束後再清空 DOM（500ms 是 Bootstrap 預設動畫時間）
	  await new Promise(resolve => setTimeout(resolve, 500));
  	
  	  // 清掉 modal DOM
  	  oldModalEl.remove();
  	}
  	  
  	fetch(`/admin/resto_timeslot/timeslot/add?periodId=${periodId}`)
  	    .then(res => res.text())
  	    .then(html => {
  	  		
 	      container.innerHTML = html;

  	      const modalEl = document.getElementById("timeslotAddModal");
  	      if (!modalEl) {
  	          alert("無法載入modal結構");
  	          return;
  	      }
  			  
  		  const modal = new bootstrap.Modal(modalEl);
  		  modal.show();
  	  			
  	  	  modalEl.addEventListener("shown.bs.modal", async () => {

  	  		  await new Promise(resolve => setTimeout(resolve, 300)); // 等Bootstrap完成動畫
  	  		  bindFormSubmitAddTimeslot();
  	  			  
  	  			});

  	  	  })
  	      .catch(err => {
  	        alert("載入表單失敗：" + err.message);
  	      });
  			
//  	function bindFormSubmitAddTimeslot() {
//  	    const submitBtn = document.getElementById("btnSubmitAdd");
//  	    if (!submitBtn) return;
//
//  	    submitBtn.addEventListener("click", function (e) {
//  	      e.preventDefault();
//  		  
//  		  showBtnOverlay(submitBtn); // 加入 loading 遮罩
//
//  	      const form = document.getElementById("timeslotAddForm");
//  	      const formData = new FormData(form);
//
//  	      fetch("/admin/resto_timeslot/timeslot/insert", {
//  	        method: "POST",
//  	        body: formData
//  	      })
//  	        .then(res => {
//  				
//  				if (res.redirected) {
//  					
//  					sessionStorage.setItem("toastMessage", "新增成功！");
//
//  				    // 成功，清空並關閉modal
//  				    document.getElementById("timeslotAddForm").reset();
//
//  				    const modal = bootstrap.Modal.getInstance(document.getElementById("timeslotAddModal"));
//  				    modal.hide();
//  					  
//  					window.location.href = res.url; // 讓瀏覽器照後端redirect去重新載入頁面
//  					  
//  					} else {
//  				      return res.text(); // 失敗時回傳HTML
//  				    }
//  				
//  			})
//  	        .then(html => {
//  				
//  				if (!html) return; // 成功就不會拿到res(modal fragment+表單填入的內容)，也就不處理下面
//  				  
//  				  //錯誤時僅更新 modal body（避免整個 modal 重建）
//  				  const parser = new DOMParser();
//  				  const doc = parser.parseFromString(html, "text/html");
//
//  				  const newBody = doc.querySelector(".modal-body");
//  				  const oldBody = document.querySelector("#timeslotAddModal .modal-body");
//
//  				  if (newBody && oldBody) {
//  				    oldBody.replaceWith(newBody);
//  				  }
//
//  				  // 模擬動畫結束後再初始化TinyMCE（因為modal沒重新開不能用shown.bs.modal來監聽）
//  				    setTimeout(() => {
//  				      bindFormSubmitAddTimeslot();
//  				    }, 300); // 與動畫一致延遲時間
//  	
//  	        })
//  	        .catch(err => {
//  	          alert("送出失敗：" + err.message);
//  			  })
//  			.finally(() => {
//  			  removeBtnOverlay(submitBtn);
//  			});
//  	    });
//  	  }
//  			
//  	

function bindFormSubmitAddTimeslot() {
	const submitBtn = document.getElementById("btnSubmitAdd");
	 if (!submitBtn) return;

	 submitBtn.addEventListener("click", function (e) {
	   e.preventDefault();
	   btn.disabled = true;
	   showBtnOverlay(submitBtn);

	   const form = document.getElementById("timeslotAddForm");
	   const formData = new FormData(form);

	   fetch("/admin/resto_timeslot/timeslot/insert", {
	     method: "POST",
	     body: formData,
	     headers: { "X-Requested-With": "Fetch" }   // 告訴後端：AJAX
	   })
	     .then(res => res.text())
	     .then(html => {

	       /* ---------- ① 解析 HTML，判斷是否表單驗證失敗 ---------- */
	       const parser = new DOMParser();
	       const doc    = parser.parseFromString(html, "text/html");

	       const errorBody = doc.querySelector(".modal-body");
	       if (errorBody) {                 // (A) 失敗：換掉 modal body
	         document
	           .querySelector("#timeslotAddModal .modal-body")
	           .replaceWith(errorBody);

	         bindFormSubmitAddTimeslot();   // 重新綁事件
	         return;
	       }

	       /* ---------- ② 成功 ---------- */
	       // 先關掉 modal
	       bootstrap.Modal.getInstance(
	         document.getElementById("timeslotAddModal")
	       ).hide();

	       /* ---------- ③ 保留 & 還原捲軸位置 ---------- */
	       const panel = document.querySelector(".panel_periodntimeslot");
	       if (!panel) return;

	       const savedScroll = panel.scrollTop;     // 記住捲軸

	       // 取出回傳片段中「新的 panel 內容」
	       const newInner = doc
	         .querySelector(".panel_periodntimeslot")
	         ?.innerHTML;

	       if (newInner !== undefined) {
	         panel.innerHTML = newInner;           // 只換內容不重建元素
	         panel.scrollTop = savedScroll;        // 還原捲軸
	       }

	       showToast("新增成功！");

	       /* ---------- ④ 可選：pushState 改網址，但不刷新 ---------- */
	       const restoId = document.getElementById("restoSelect").value;
	       history.pushState(null, "", `/admin/resto_timeslot?restoId=${restoId}`);
	     })
	     .catch(err => alert("送出失敗：" + err.message))
	     .finally(() => {
			btn.disabled = false;
			removeBtnOverlay(submitBtn);
		 });
	 });
}

    });


	
	
	
  // ===== 編輯時段 =====
  //按下編輯icon按鈕打開modal
  document.addEventListener("click",async function (e) {	
  if (e.target.closest(".btn_timeslot_edit")) {
      const editBtn = e.target.closest(".btn_timeslot_edit");
      if ( !editBtn) return;
	  
      const timeslotId = editBtn.getAttribute("data-id");
	  if (!timeslotId ) return;

	  
	    // 如果 modal 已經存在，先強制關掉並移除 DOM
      	const oldModalEl = document.getElementById("timeslotEditModal");
	    if (oldModalEl) {
	      const modal = bootstrap.Modal.getInstance(oldModalEl);
	      if (modal) modal.hide();

	      // 等動畫結束後再清空 DOM（500ms 是 Bootstrap 預設動畫時間）
	      await new Promise(resolve => setTimeout(resolve, 500));

	      // 清掉 modal DOM
	      oldModalEl.remove();
		  
	    }

  
      fetch(`/admin/resto_timeslot/timeslot/edit?timeslotId=${timeslotId}`)
        .then(res => res.text())
        .then(html => {
		
          container.innerHTML = html;

          const modalEl = document.getElementById("timeslotEditModal");
          if (!modalEl) {
            alert("無法載入modal結構");
            return;
          }

		      const modal = new bootstrap.Modal(modalEl);
		      modal.show();
			
		      modalEl.addEventListener("shown.bs.modal", async () => {
			    await new Promise(resolve => setTimeout(resolve, 300)); // 等Bootstrap完成動畫
			    bindFormSubmitEditTimeslot();
				
			  });

  	  })
        .catch(err => {
          alert("載入表單失敗：" + err.message);
        });
	}
  });
  
  function bindFormSubmitEditTimeslot() {
    const submitBtn = document.getElementById("btnSubmitEditSave");
    if (!submitBtn) return;

    submitBtn.addEventListener("click", function (e) {
      e.preventDefault();
	  btn.disabled = true;
      showBtnOverlay(submitBtn); // 加入 loading 遮罩
	
      const form = document.getElementById("timeslotEditForm");
      const formData = new FormData(form);

      fetch("/admin/resto_timeslot/timeslot/update", {
        method: "POST",
        body: formData
      })
        .then(res => {

			if (res.redirected) {
			sessionStorage.setItem("toastMessage", "編輯成功！");				
				window.location.href = res.url; // 讓瀏覽器照後端redirect去重新載入頁面
				
			      // 成功，清空並關閉modal
			      document.getElementById("timeslotEditForm").reset();
			      const modal = bootstrap.Modal.getInstance(document.getElementById("timeslotEditModal"));
			      modal.hide();
				  
				} else {
			      return res.text(); // 失敗時回傳HTML
			    }
			
		})
        .then(html => {
			
			if (!html) return; // 成功就不會拿到res(表單填入的內容)，也就不處理下面

			  //錯誤時僅更新 modal body（避免整個 modal 重建）
			  const parser = new DOMParser();
			  const doc = parser.parseFromString(html, "text/html");

			  const newBody = doc.querySelector(".modal-body");
			  const oldBody = document.querySelector("#timeslotEditModal .modal-body");

			  if (newBody && oldBody) {
			    oldBody.replaceWith(newBody);
			  }

			    setTimeout(() => {
			      bindFormSubmitEditTimeslot();
			    }, 300); // 與動畫一致延遲時間
	
        })
        .catch(err => {
          alert("送出失敗：" + err.message);
		})
	  .finally(() => {
		btn.disabled = false;
		removeBtnOverlay(submitBtn);
      });
    });
  }
  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
  
  
  
  

  
});
