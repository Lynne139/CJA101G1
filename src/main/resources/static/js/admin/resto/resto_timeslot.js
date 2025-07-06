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
		
		const params = new URLSearchParams();
		params.append("periodId",  periodId);
		params.append("restoId",  restoId);
		
        // 存卷軸位置
        sessionStorage.setItem("scrollY", window.scrollY);
        
		fetch(`/admin/resto_timeslot/period/delete`, {
			method: 'POST',
			headers: { "Content-Type": "application/x-www-form-urlencoded" },
			body: params
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
  document.addEventListener("click", async function (e) {

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

  });
  function bindFormSubmitAddPeriod() {
    const submitBtn = document.getElementById("btnSubmitAdd");
    if (!submitBtn) return;
    // 防止重複綁定
    submitBtn.removeEventListener("click", handleAddPeriodSubmit);
    submitBtn.addEventListener("click", handleAddPeriodSubmit);
  }

  function handleAddPeriodSubmit(e) {
    e.preventDefault();

    const submitBtn = e.currentTarget;
    if (submitBtn.disabled) return;
    submitBtn.disabled = true;
    showBtnOverlay(submitBtn); // 加入 loading 遮罩

    const form = document.getElementById("periodAddForm");
    const formData = new FormData(form);

    // 存卷軸位置
    sessionStorage.setItem("scrollY", window.scrollY);

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

        setTimeout(() => {
          bindFormSubmitAddPeriod();
        }, 300); // 與動畫一致延遲時間

      })
      .catch(err => {
        alert("送出失敗：" + err.message);
      })
      .finally(() => {
        submitBtn.disabled = false;
        removeBtnOverlay(submitBtn);
      });
  }



  // ===== 編輯區段 =====
  //按下編輯icon按鈕打開modal
  document.addEventListener("click", async function (e) {

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

    // 防止重複綁定
    submitBtn.removeEventListener("click", handleEditPeriodSubmit);
    submitBtn.addEventListener("click", handleEditPeriodSubmit);
  }

  function handleEditPeriodSubmit(e) {
    e.preventDefault();

    const submitBtn = e.currentTarget;
    if (submitBtn.disabled) return;
    submitBtn.disabled = true;
    showBtnOverlay(submitBtn); // 加入 loading 遮罩

    const form = document.getElementById("periodEditForm");
    const formData = new FormData(form);

    // 存卷軸位置
    sessionStorage.setItem("scrollY", window.scrollY);

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
        submitBtn.disabled = false;
        removeBtnOverlay(submitBtn);
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
    const dir = btn.getAttribute("data-dir");

    let formData = new FormData();
    formData.append("periodId", periodId);
    formData.append("dir", dir);


    fetch("/admin/resto_timeslot/period/move", {
      method: "POST",
      body: new URLSearchParams(formData)
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
        } else {
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


  // 讓頂、底元素的箭頭切換disabled狀態
  function refreshMoveButtons() {
    const allPeriods = document.querySelectorAll(".period_block");
    allPeriods.forEach(p => {
      p.querySelector(".btn_move_up").disabled = false;
      p.querySelector(".btn_move_down").disabled = false;
    });
    if (allPeriods.length) {
      allPeriods[0].querySelector(".btn_move_up").disabled =
        true;                                    // 第一個不能再上
      allPeriods[allPeriods.length - 1]
        .querySelector(".btn_move_down").disabled = true; // 最後一個不能再下
    }
  }

  // timeslot拖曳變動時都要再次更新period的刪除disable
  function refreshPeriodDeleteButtons() {
	document.querySelectorAll('.period_block').forEach(block => {

	    // 只算真正的時段 wrapper（排除 + 按鈕
	    const hasTimeslot = block.querySelector('.timeslot_wrapper') !== null;

	    const delBtn = block.querySelector('.btn_period_del');
	    if (!delBtn) return;

	    if (hasTimeslot) {
	      delBtn.disabled = true;
	      delBtn.title = '仍有時段綁定此類別，無法刪除';
	    } else {
	      delBtn.disabled = false;
	      delBtn.title = '刪除類別';
	    }
	  });
	}

	
	
	// ===== 設置住宿方案用code =====
	document.querySelectorAll('.period-code-select').forEach(sel => {
	  
	  // 使select 的 value 一開始不是空字串，就記成 prevId
	  sel.dataset.prevPeriodId = sel.value || "";
		
	  sel.addEventListener('change', async (e) => {
	    const periodId = e.target.value;
	    const code     = e.target.dataset.code;
	    const restoId  = document.getElementById('restoSelect').value;
		const prevId   = sel.dataset.prevPeriodId;     // 可能 undefined

//		console.table({ periodId, code, restoId, prevId });

		if (!restoId) return;                        // 防呆

		try{
			let success = false;

			
		// 有選period 
		if (periodId !== '') {                     // 選了新 period
		        await fetch('/admin/resto_timeslot/period/setCode', {
		          method : 'POST',
		          headers: {'Content-Type':'application/x-www-form-urlencoded'},
		          body   : new URLSearchParams({ periodId, code, restoId })
		        });
		        sel.dataset.prevPeriodId = periodId;     // 更新備份
				success = true;

		      }
			  
		// 不開放
		else if (prevId !== '') {   // 必須先有舊綁定
			await fetch('/admin/resto_timeslot/period/clearCode', {
		          method : 'POST',
		          headers: {'Content-Type':'application/x-www-form-urlencoded'},
		          body   : new URLSearchParams({ periodId: prevId, restoId })
		        });

		        sel.dataset.prevPeriodId = '';           // 清掉備份
				success = true;

		      }

			  location.reload();  
			  sessionStorage.setItem("toastMessage", "編輯成功！");

			  
//				if (success) {
//				  showToast("操作成功！");
//				}
			  
			                         // 重新載入頁面
			      } catch (err) {
			        console.error(err);
			        alert('操作失敗，請稍後再試');
			      }
		
	  });
	});


	
	
	
	
	
	
	
	
	
	


  // ===== 時段 ========================================================

  // ===== (軟)刪除時段 =====
  document.addEventListener("click", function (e) {

    if (e.target.closest(".btn_timeslot_del")) {
      const btn = e.target.closest(".btn_timeslot_del");
      const timeslotId = btn.getAttribute("data-id");
      const restoId = document.getElementById("restoSelect").value;

      if (!timeslotId || !restoId) return;

      if (confirm("項目一旦刪除將無法復原，是否確定刪除？")) {
		
		const params = new URLSearchParams();
		params.append("timeslotId",  timeslotId);
		params.append("restoId",  restoId);
		
        // 存卷軸位置
        sessionStorage.setItem("scrollY", window.scrollY);

        fetch(`/admin/resto_timeslot/timeslot/delete`, {
			method: 'POST',
			headers: { "Content-Type": "application/x-www-form-urlencoded" },
			body: params
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
  
  
  // ===== 組時段格式HH:MM =====  
  function bindTimeSelectListeners() {
    const hourSelect = document.getElementById("hourSelect");
    const minuteSelect = document.getElementById("minuteSelect");
    const hiddenInput = document.getElementById("timeslotHiddenInput");

    function updateTime() {
      const h = hourSelect.value;
      const m = minuteSelect.value;
      if (h && m) {
        hiddenInput.value = `${h}:${m}`;
      } else {
        hiddenInput.value = "";
      }
    }

    hourSelect.addEventListener("change", updateTime);
    minuteSelect.addEventListener("change", updateTime);
  }
  
  // ===== 編輯modal打開要填入資料庫存的值 =====
  function prefillTimeSelect(modalEl) {
	const timeslotName = modalEl.querySelector('input[name="timeslotName"]')?.value ?? "";

    if (!modalEl) return;

    const [hour, minute] = timeslotName.split(":");
	modalEl.querySelector("#hourSelect").value = hour;
	modalEl.querySelector("#minuteSelect").value = minute;
  }
  


  // ===== 新增時段 =====
  //按下新增按鈕打開modal
  document.addEventListener("click", async function (e) {

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
		  bindTimeSelectListeners();
		  bindFormSubmitAddTimeslot();
        })

      })
      .catch(err => {
        alert("載入表單失敗：" + err.message);
      });

  });

  function bindFormSubmitAddTimeslot() {
    const submitBtn = document.getElementById("btnSubmitAdd");
    if (!submitBtn) return;

    // 防止重複綁定
    submitBtn.removeEventListener("click", handleAddTimeslotSubmit);
    submitBtn.addEventListener("click", handleAddTimeslotSubmit);
  }

  function handleAddTimeslotSubmit(e) {
    e.preventDefault();

    const submitBtn = e.currentTarget;
    if (submitBtn.disabled) return;
    submitBtn.disabled = true;
    showBtnOverlay(submitBtn); // 加入 loading 遮罩

    const form = document.getElementById("timeslotAddForm");
    const formData = new FormData(form);

    // 存卷軸位置
    sessionStorage.setItem("scrollY", window.scrollY);

    fetch("/admin/resto_timeslot/timeslot/insert", {
      method: "POST",
      body: formData
    })
      .then(res => {

        if (res.redirected) {

          sessionStorage.setItem("toastMessage", "新增成功！");

          // 成功，清空並關閉modal
          document.getElementById("timeslotAddForm").reset();

          const modal = bootstrap.Modal.getInstance(document.getElementById("timeslotAddModal"));
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
        const oldBody = document.querySelector("#timeslotAddModal .modal-body");

        if (newBody && oldBody) {
          oldBody.replaceWith(newBody);
        }

        // 模擬動畫結束後再初始化TinyMCE（因為modal沒重新開不能用shown.bs.modal來監聽）
        setTimeout(() => {
		  bindTimeSelectListeners();
          bindFormSubmitAddTimeslot();
        }, 300); // 與動畫一致延遲時間

      })
      .catch(err => {
        alert("送出失敗：" + err.message);
      })
      .finally(() => {
        submitBtn.disabled = false;
        removeBtnOverlay(submitBtn);
      });
  }





  // ===== 編輯時段 =====
  //按下編輯icon按鈕打開modal
  document.addEventListener("click", async function (e) {
    if (e.target.closest(".btn_timeslot_edit")) {
      const editBtn = e.target.closest(".btn_timeslot_edit");
      if (!editBtn) return;

      const timeslotId = editBtn.getAttribute("data-id");
      if (!timeslotId) return;


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
			bindTimeSelectListeners();
			prefillTimeSelect(modalEl);
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

    // 防止重複綁定
    submitBtn.removeEventListener("click", handleEditTimeslotSubmit);
    submitBtn.addEventListener("click", handleEditTimeslotSubmit);
  }

  function handleEditTimeslotSubmit(e) {
    e.preventDefault();

    const submitBtn = e.currentTarget;
    if (submitBtn.disabled) return;
    submitBtn.disabled = true;
    showBtnOverlay(submitBtn); // 加入 loading 遮罩

    const form = document.getElementById("timeslotEditForm");
    const formData = new FormData(form);

    // 存卷軸位置
    sessionStorage.setItem("scrollY", window.scrollY);

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
		  bindTimeSelectListeners();
		  prefillTimeSelect(modalEl);
          bindFormSubmitEditTimeslot();
        }, 300); // 與動畫一致延遲時間

      })
      .catch(err => {
        alert("送出失敗：" + err.message);
      })
      .finally(() => {
        submitBtn.disabled = false;
        removeBtnOverlay(submitBtn);
      });
  }
  
  
  // ===== 拖曳時段改變所屬區段 =====
  
  function initTimeslotDnD() {
	
	if (window.readonly) {
	    console.log("只讀模式，不可拖動");
	    return; // 或根本不要初始化拖動元件
	}
	
    // 把每個 period 裡的 .timeslot_group 都變成 Sortable 清單
    document.querySelectorAll('.timeslot_group').forEach(groupEl => {

      new Sortable(groupEl, {
        group:         'timeslot',          // 允許跨清單
        animation:     150,
        draggable:     '.timeslot_wrapper', // 只有 wrapper 可以拖
		handle: '.timeslot_block',
        filter:        '.btn_add_timeslot', // 「＋」鈕不可拖
		ghostClass: 'sortable-ghost', // 「＋」鈕永遠不會被插入/覆蓋
		fallbackTolerance: 3,
        onEnd(evt) {
          const elWrapper = evt.item;                       // 被拖動的 wrapper
          const fromId    = evt.from.dataset.id;            // 原 periodId
          const toId      = evt.to.dataset.id;              // 目標 periodId
          if (fromId === toId) return;                      // 沒搬家

          const timeslotId = elWrapper.dataset.id;          // wrapper 的 data-id = timeslotId
          moveTimeslot(timeslotId, toId, fromId, elWrapper);        // 更新後端
		  // 重新依 data-time 排序
		  sortGroupByTime(evt.to);
		}
      });

    });
  }
  
  function sortGroupByTime(groupEl) {
    const wrappers = [...groupEl.querySelectorAll('.timeslot_wrapper')];

    wrappers
      .sort((a, b) => {
        const t1 = a.dataset.time;
        const t2 = b.dataset.time;
        return t1.localeCompare(t2);
      })
      .forEach(el => groupEl.appendChild(el)); // 排完再 append，順序就變了
  }



  // 呼叫後端同步 periodId
  function moveTimeslot(timeslotId, newPeriodId, fromPeriodId, elWrapper) {

    const panel = document.querySelector('.panel_periodntimeslot');
    showPanelOverlay(panel);

    fetch('/admin/resto_timeslot/timeslot/transfer', {
      method : 'POST',
      headers: { 'X-Requested-With': 'Fetch',
                 'Content-Type':      'application/x-www-form-urlencoded' },
      body   : new URLSearchParams({ timeslotId, newPeriodId })
    })
    .then(r => r.json())
    .then(j => {
        if (j.msg === 'transfer success') {
            // 成功就改掉 wrapper 自己的 periodId（給未來用）
            elWrapper.dataset.periodId = newPeriodId;
			
			//刷新period排序與可刪按鈕的狀態
            refreshMoveButtons();
			refreshPeriodDeleteButtons();
            showToast('移動成功！');
        } else {
            throw new Error(j.message || '未知錯誤');
        }
    })
    .catch(err => {
        // 失敗就把元素搬回原清單
        document
          .querySelector(`.timeslot_group[data-id="${fromPeriodId}"]`)
          .appendChild(elWrapper);
		  refreshPeriodDeleteButtons();       // 失敗也要重算
        alert('移動失敗：' + err.message);
    })
    .finally(() => {
		removePanelOverlay(panel);	
	});
  }


  // 第一次與每次重新插入 fragment 後都要重新啟動
  initTimeslotDnD();
  refreshPeriodDeleteButtons();
 

















});