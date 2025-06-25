document.addEventListener("DOMContentLoaded", () => {
	
	//modal載入位置
	const container = document.getElementById("restoTimeslotModalContainer");
	
  
  // ===== 刪除區段 =====
  document.addEventListener("click", function (e) {
      if (e.target.closest(".btn_delete")) {
        const btn = e.target.closest(".btn_delete");
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
		    e.preventDefault();

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
	  			  //保險先清掉原本的tinymce以及重新初始化以下功能
	  			  //必須這樣是因為每次modal開啟都是fetch後動態載入插入的新dom
	  			  //addEventListener()是一次性的綁定，綁舊dom上的會失效，必須重新綁新渲染的dom
	  			  //timymce須清除，因為舊的tinymce編輯器有實體殘留

	  			  bindFormSubmitAdd();
	  			  
	  			});

	  	    })
	        .catch(err => {
	          alert("載入表單失敗：" + err.message);
	        });
			
	function bindFormSubmitAdd() {
	    const submitBtn = document.getElementById("btnSubmitAdd");
	    if (!submitBtn) return;

	    submitBtn.addEventListener("click", function (e) {
	    e.preventDefault();
		  
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
				      bindFormSubmitAdd();
				    }, 300); // 與動畫一致延遲時間
	
	        })
	        .catch(err => {
	          alert("送出失敗：" + err.message);
			  })
			.finally(() => {
			  removeBtnOverlay(submitBtn);
			});
	    });
	  }
			
			
			
			
			
			
			
			
			
	
  });
		  
		  
  // ===== Modal - Edit =====
      //按下編輯icon按鈕打開modal
      document.addEventListener("click",async function (e) {
  		
  	  if (e.target.closest(".btn_edit")) {
          const editBtn = e.target.closest(".btn_edit");
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
    			    bindFormSubmitEdit();
					
    			  });

      	  })
            .catch(err => {
              alert("載入表單失敗：" + err.message);
            });
  		}
      });
      
      function bindFormSubmitEdit() {
        const submitBtn = document.getElementById("btnSubmitEditSave");
        if (!submitBtn) return;

        submitBtn.addEventListener("click", function (e) {
          e.preventDefault();

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
    			      bindFormSubmitEdit();
    			    }, 300); // 與動畫一致延遲時間

    				
            })
            .catch(err => {
              alert("送出失敗：" + err.message);
  			})
  		  .finally(() => {
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

  
  
  
  

  
});
