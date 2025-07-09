document.addEventListener("DOMContentLoaded", function () {

    //modal載入位置
    const container = document.getElementById("roomoInfoModalContainer");


    initRoomoTable(); // 初次載入表格


    // ===== DataTables =====
    function initRoomoTable() {
        const table = $('#roomoTable');

        if ($.fn.DataTable.isDataTable(table)) {
            table.DataTable().clear().destroy(); // 清除舊實例
        }

        table.DataTable({
            language: zhHANTLang,
            paging: true,
            pageLength: 5,
            lengthMenu: [5, 10],
            order: [[0, 'asc']],
            autoWidth: false,
            scrollX: true,
            fixedColumns: {
                leftColumns: 0,
                rightColumns: 1
            },
            columnDefs: [
                { targets: [0], width: "20%" },
                { targets: [1], width: "25%" },
                { targets: [2], width: "25%", orderable: false },
                { targets: [3], width: "10%", orderable: false },
                { targets: [4], width: "5%", orderable: false },
                { targets: [5], width: "15%", orderable: false },
                { targets: [6], width: "15%" },
                { targets: [7], width: "15%" },
                { targets: [8], width: "15%" },
                { targets: [9], width: "15%", orderable: false }

            ],
            searching: false,
            ordering: true,
            info: true,
            // 預設隱藏已取消的訂單
            initComplete: function () {
                // 檢查是否有訂單狀態篩選條件
                const urlParams = new URLSearchParams(window.location.search);
                const isEnabled = urlParams.get('isEnabled');

                // 如果沒有明確指定要顯示已取消的訂單，則隱藏它們
                if (isEnabled !== '0') {
                    this.api().column(3).search('^(?!.*取消).*$', true, false).draw();
                }
            }
        });
    }


    // ===== 清空複合查詢欄位 =====
    const clearBtn = document.querySelector(".btn_search_clear");
    const form = document.querySelector(".filter");

    clearBtn.addEventListener("click", () => {
        // 清空欄位值
        form.querySelector("select[name='isEnabled']").value = "";
        form.querySelector("input[name='keyword']").value = "";

        // 自動提交清空後的查詢表單
        form.submit();
    });


    // ===== 取消訂單 =====
    document.addEventListener("click", function (e) {
        if (e.target.closest(".btn_cancel")) {
            const btn = e.target.closest(".btn_cancel");
            const roomoId = btn.getAttribute("data-id");

            if (!roomoId) return;

            if (confirm("確定要取消此訂單嗎？")) {
                fetch('/admin/roomo_info/cancel', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: 'roomOrderId=' + encodeURIComponent(roomoId)
                })
                    .then(res => res.json())
                    .then(data => {
                        if (data.success) {
                            // 更新表格中的訂單狀態顯示
                            const row = btn.closest("tr");
                            if (row) {
                                const statusCell = row.querySelector("td:nth-child(3)"); // 訂單狀態欄位
                                if (statusCell) {
                                    statusCell.innerHTML = '<span class="badge bg-secondary">取消</span>';
                                }
                                // 讓取消按鈕變成disable
                                btn.disabled = true;
                                // 編輯按鈕也變成disable
                                const editBtn = row.querySelector(".btn_edit");
                                if (editBtn) {
                                    editBtn.disabled = true;
                                }
                            }
                            // 直接前端更新所有明細狀態顯示為取消
                            if (data.olistIds && Array.isArray(data.olistIds)) {
                                data.olistIds.forEach(olistId => {
                                    // 編輯modal select
                                    const detailSelect = document.querySelector(`.order-detail-status[data-id="${olistId}"]`);
                                    if (detailSelect) {
                                        detailSelect.value = '0';
                                        const selectContainer = detailSelect.closest('.form-group') || detailSelect.parentElement;
                                        if (selectContainer) {
                                            selectContainer.innerHTML = '<span class="badge bg-secondary">取消</span>';
                                        }
                                    }
                                    // 檢視modal
                                    const viewModal = document.getElementById('roomoViewModal');
                                    if (viewModal && viewModal.classList.contains('show')) {
                                        const detailSections = viewModal.querySelectorAll('.modal_section');
                                        detailSections.forEach(section => {
                                            const titleElement = section.previousElementSibling;
                                            if (titleElement && titleElement.textContent.includes(`#${olistId}`)) {
                                                const statusElement = section.querySelector('p:last-child strong');
                                                if (statusElement && statusElement.textContent.includes('明細狀態')) {
                                                    const statusTextElement = statusElement.nextElementSibling;
                                                    if (statusTextElement) {
                                                        statusTextElement.textContent = '已取消';
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                            alert("訂單已成功取消");
                        } else {
                            alert("取消訂單失敗：" + (data.message || "未知錯誤"));
                        }
                    })
                    .catch(err => {
                        alert("取消訂單失敗：" + err.message);
                    });
            }
        }
    });




    // ===== Modal - View =====
    document.addEventListener("click", async function (e) {
        if (e.target.closest(".btn_view")) {
            const roomOrderId = e.target.closest(".btn_view").dataset.id;
            const res = await fetch(`/admin/roomo_info/view?roomOrderId=${roomOrderId}`);
            const html = await res.text();
            container.innerHTML = html;
            const modalEl = document.getElementById("roomoViewModal");
            if (modalEl) {
                const modal = new bootstrap.Modal(modalEl);
                modal.show();
            }
        }
    });

    //===== Modal Add + Edit 共用 =====
    function initTinyMCE() {
        tinymce.init({
            selector: '#roomoContent',
            toolbar_mode: 'sliding',
            language: 'zh_TW',
            height: 300,
            readonly: false,
            menubar: false,
            branding: false,
            plugins: [
                'lists', 'table'
            ],
            toolbar: 'undo redo | bold italic underline strikethrough | fontsize lineheight | forecolor backcolor | bullist numlist | alignleft aligncenter alignright alignjustify | table tableinsertdialog',
            table_toolbar: '',
            table_appearance_options: false,
            table_grid: true,
            fontsize_formats: '12px 14px 16px 18px 20px 24px 28px 32px',
            line_height_formats: '1 1.2 1.4 1.6 2',
            content_style: `
  		  body { font-family: Helvetica, Arial, sans-serif; font-size: 16px; line-height: 1.2; },
  		  table { border-collapse: collapse; width: 100%; },
  		  td, th { border: 1px solid #ccc; padding: 2px; }`,
        });

    }

    // Prevent Bootstrap dialog from blocking focusin
    //TinyMCE官方提供避免與bs modal focus設定衝突的方法
    document.addEventListener('focusin', (e) => {
        if (e.target.closest(".tox-tinymce-aux, .moxman-window, .tam-assetmanager-root") !== null) {
            e.stopImmediatePropagation();
        }
    });

    // ===== Modal - Add =====
    //按下新增按鈕打開modal
    document.addEventListener("click", async function (e) {
        const addBtn = e.target.closest("#btnAddRoomo");

        if (!addBtn) return;
        e.preventDefault();

        // 如果 modal 已經存在，先強制關掉並移除 DOM
        const oldModalEl = document.getElementById("roomoAddModal");
        if (oldModalEl) {
            const modal = bootstrap.Modal.getInstance(oldModalEl);
            if (modal) modal.hide();

            // 等動畫結束後再清空 DOM（500ms 是 Bootstrap 預設動畫時間）
            await new Promise(resolve => setTimeout(resolve, 500));

            // 清掉 modal DOM
            oldModalEl.remove();

            // 同時清除 TinyMCE（保險起見）
            if (window.tinymce && Array.isArray(tinymce.editors) && tinymce.editors.length > 0) {
                await tinymce.remove();
            }
        }


        fetch("/admin/roomo_info/add")
            .then(res => res.text())
            .then(html => {

                container.innerHTML = html;

                const modalEl = document.getElementById("roomoAddModal");
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
                    tinymce.remove();
                    initTinyMCE();
                    // 綁定新增表單提交事件
                    bindFormSubmitAdd();

                    // 自動填入會員姓名
                    bindMemberIdAutoFill();

                    // 房型選擇後自動填入價格
                    bindRoomTypeRoomCascade();

                    // 新增房型訂購區
                    bindAddRoomDetailBlock();

                    // 房型選擇後自動填入價格
                    bindRoomDetailPriceAutoFill();


                    // 自動查詢優惠券
                    bindCouponAutoQuery();

                    // 房型選擇後自動檢查是否有空房
                    bindRoomAmountSelectUpdate();

                    // 加購專案區塊顯示/隱藏與資料載入
                    bindProjectAddOnArea();

                    // 在 modal 開啟時呼叫
                    bindRoomScheduleCheck();

                });

            })
            .catch(err => {
                alert("載入表單失敗：" + err.message);
            });
    });


    function bindFormSubmitAdd() {
        const submitBtn = document.getElementById("btnSubmitAdd");
        console.log("綁定新增表單提交事件");
        if (!submitBtn) return;

        // 防重複綁定：先移除舊的事件監聽器
        const newSubmitBtn = submitBtn.cloneNode(true);
        submitBtn.parentNode.replaceChild(newSubmitBtn, submitBtn);

        // 重新取得按鈕並綁定事件
        const freshSubmitBtn = document.getElementById("btnSubmitAdd");
        if (!freshSubmitBtn) return;

        freshSubmitBtn.addEventListener("click", function (e) {
            e.preventDefault(); // 防止表單預設送出
            e.stopPropagation(); // 防止事件冒泡

            // 防止重複點擊
            if (this.disabled) return;
            this.disabled = true;

            const form = document.getElementById("roomOrderAddForm");
            if (!form) {
                alert("找不到表單！");
                this.disabled = false;
                return;
            }
            // ====== 新增入住/退房日期驗證 ======
            const checkInDate = form.querySelector("#checkInDate").value;
            const checkOutDate = form.querySelector("#checkOutDate").value;
            if (checkInDate && checkOutDate && checkInDate > checkOutDate) {
                alert("入住日期不可大於退房日期！");
                // focus 入住日期欄位
                form.querySelector("#checkOutDate").focus();
                this.disabled = false;
                return;
            }

            // ===== 檢查所有 required 欄位 =====
            const requiredFields = form.querySelectorAll("[required]");

            let missingFields = [];
            for (const field of requiredFields) {
                if (!field.value || (field.type === "checkbox" && !field.checked)) {
                    // 取得最近的 .form-label 文字
                    let label = field.closest(".mb-3, .form-group")?.querySelector(".form-label");
                    let msg = label ? label.textContent.trim() : (field.placeholder || field.name || "有欄位");
                    missingFields.push(msg);
                }
            }
            if (missingFields.length > 0) {
                alert("請填寫以下欄位：\n" + missingFields.join("\n"));
                // focus 第一個沒填的欄位
                const firstMissing = requiredFields[Array.from(requiredFields).findIndex(field =>
                    !field.value || (field.type === "checkbox" && !field.checked)
                )];
                if (firstMissing) firstMissing.focus();
                this.disabled = false;
                return;
            }
            // ===== 檢查結束 =====

            const formData = new FormData(form);

            // 將 TinyMCE 的內容寫回 textarea
            const content = tinymce.get("roomoContent")?.getContent();
            formData.set("roomoContent", content || "");

            console.log("開始送出表單...");
            fetch("/admin/roomo_info/insert", {
                method: "POST",
                body: formData
            })
                .then(res => {
                    console.log("API 回應:", res);
                    console.log("狀態碼:", res.status);

                    // 檢查是否為重導向（302）或成功重導向
                    if (res.redirected || res.status === 302) {
                        console.log("重導向到:", res.url || "/admin/roomo_info");

                        // 成功，清空並關閉modal
                        const modal = bootstrap.Modal.getInstance(document.getElementById("roomoAddModal"));
                        if (modal) modal.hide();

                        // 重導向到指定頁面
                        window.location.href = "/admin/roomo_info";

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
                    const oldBody = document.querySelector("#roomoAddModal .modal-body");

                    if (newBody && oldBody) {
                        oldBody.replaceWith(newBody);
                    }

                    // 模擬動畫結束後再初始化TinyMCE（因為modal沒重新開不能用shown.bs.modal來監聽）
                    setTimeout(() => {
                        tinymce.remove(); //清掉舊的
                        initTinyMCE();    //重新初始化
                        bindFormSubmitAdd();
                    }, 300); // 與動畫一致延遲時間


                })
                .catch(err => {
                    console.error("送出失敗:", err);
                    alert("送出失敗：" + err.message);
                })
                .finally(() => {
                    this.disabled = false; // 重新啟用按鈕
                    removeBtnOverlay(freshSubmitBtn);
                });
        });
    }


    // ===== Modal - Edit =====
    //按下編輯icon按鈕打開modal
    document.addEventListener("click", async function (e) {

        if (e.target.closest(".btn_edit")) {
            const editBtn = e.target.closest(".btn_edit");
            const roomoId = editBtn.getAttribute("data-id");
            // console.log("編輯按鈕被點擊，roomoId:", roomoId);
            if (!roomoId) return;

            // 如果 modal 已經存在，先強制關掉並移除 DOM
            const oldModalEl = document.getElementById("roomoEditModal");
            if (oldModalEl) {
                const modal = bootstrap.Modal.getInstance(oldModalEl);
                if (modal) modal.hide();

                // 等動畫結束後再清空 DOM（500ms 是 Bootstrap 預設動畫時間）
                await new Promise(resolve => setTimeout(resolve, 500));

                // 清掉 modal DOM
                oldModalEl.remove();

                // 同時清除 TinyMCE（保險起見）
                if (window.tinymce && Array.isArray(tinymce.editors) && tinymce.editors.length > 0) {
                    await tinymce.remove();
                }
            }


            fetch(`/admin/roomo_info/edit?roomOrderId=${roomoId}`)
                .then(res => res.text())
                .then(html => {
                    container.innerHTML = html;

                    const modalEl = document.getElementById("roomoEditModal");
                    if (!modalEl) {
                        alert("無法載入modal結構");
                        return;
                    }

                    const modal = new bootstrap.Modal(modalEl);
                    modal.show();

                    let hasUnsavedChanges = false; // ✅ 用於判斷是否變更

                    // ✅ 當 modal 開啟完成後才執行初始化邏輯
                    modalEl.addEventListener("shown.bs.modal", async () => {
                        console.log("✅ modal 已開啟");
                        await new Promise(resolve => setTimeout(resolve, 300));

                        tinymce.remove();
                        initTinyMCE();
                        bindFormSubmitEdit();
                        bindRoomTypeRoomCascade();
                        bindAddRoomDetailBlock();
                        bindRoomDetailPriceAutoFill();
                        bindCouponAutoQuery();
                        // 房型選擇後自動檢查是否有空房
                        bindRoomAmountSelectUpdate();
                        // 加購專案區塊顯示/隱藏與資料載入
                        bindProjectAddOnArea();
                        // 新增：入住/退房日期變動時清空所有明細
                        bindEditDateChangeClearDetail();
                        // 在 modal 開啟時呼叫
                        bindRoomScheduleCheck();
                        // 計算總金額
                        calcActualAmount();
                        // roomOrderId 是當前這筆訂單 ID
                        bindCancelProjectAddOn(roomOrderId); 

                        console.log("綁定完成");

                        // ✅ 只要表單有變更就標記
                        const editForm = document.getElementById("roomOrderEditForm");
                        if (editForm) {
                            editForm.addEventListener("input", () => {
                                hasUnsavedChanges = true;
                                console.log("✏️ 使用者輸入內容 → 設定 hasUnsavedChanges = true");
                            });
                        }

                        // ✅ 攔截 modal 關閉
                        modalEl.addEventListener("hide.bs.modal", async function (e) {
                            if (hasUnsavedChanges) {
                                e.preventDefault();
                                if (confirm("尚未儲存變更，是否儲存？\n按「確定」儲存，按「取消」放棄變更。")) {
                                    // 按「確定」→ 儲存
                                    const success = await doSave();
                                    if (success) {
                                        hasUnsavedChanges = false;
                                        bootstrap.Modal.getInstance(modalEl).hide(); // 再次關閉
                                    }
                                } else {
                                    // 按「取消」→ 直接關閉，不儲存
                                    hasUnsavedChanges = false;
                                    bootstrap.Modal.getInstance(modalEl).hide();
                                }
                            }
                        });

                        // ✅ 如果你有儲存成功後的 callback，也可以這樣清除
                        window.resetEditChangeFlag = () => {
                            hasUnsavedChanges = false;
                            console.log("✅ 已儲存 → hasUnsavedChanges 設為 false");
                        };

                        // 在 modal 開啟時呼叫
                        bindRoomScheduleCheck();
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

            // 修正 form id
            const form = document.getElementById("roomOrderEditForm");

            // ====== 新增入住/退房日期驗證 ======
            const checkInDate = form.querySelector("[name='checkInDate']").value;
            const checkOutDate = form.querySelector("[name='checkOutDate']").value;
            if (checkInDate && checkOutDate && checkInDate > checkOutDate) {
                alert("入住日期不可大於退房日期！");
                form.querySelector("[name='checkOutDate']").focus();
                removeBtnOverlay(submitBtn);
                return;
            }
            // ===== 檢查結束 =====

            const formData = new FormData(form);

            // 將 TinyMCE 的內容寫回 textarea
            const content = tinymce.get("roomoContent")?.getContent();
            formData.set("roomoContent", content || "");

            fetch("/admin/roomo_info/update", {
                method: "POST",
                body: formData
            })
                .then(res => {

                    if (res.redirected) {
                        window.location.href = res.url; // 讓瀏覽器照後端redirect去重新載入頁面

                        // 成功，清空並關閉modal
                        document.getElementById("roomoEditForm").reset();
                        tinymce.get("roomoContent")?.setContent("");

                        const modal = bootstrap.Modal.getInstance(document.getElementById("roomoEditModal"));
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
                    const oldBody = document.querySelector("#roomoEditModal .modal-body");

                    if (newBody && oldBody) {
                        oldBody.replaceWith(newBody);
                    }

                    // 模擬動畫結束後再初始化TinyMCE（因為modal沒重新開不能用shown.bs.modal來監聽）
                    setTimeout(() => {
                        tinymce.remove(); //清掉舊的
                        initTinyMCE();    //重新初始化
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

    // ...在 modal 載入後呼叫此函數...
    // 填入MID後自動搜尋會員名字
    function bindMemberIdAutoFill() {
        const memberIdInput = document.getElementById("memberId");
        const memberNameInput = document.getElementById("memberName");
        if (!memberIdInput || !memberNameInput) return;

        let lastQuery = "";
        let timer = null;

        if (memberIdInput) {
            memberIdInput.addEventListener("input", function () {
                const memberId = memberIdInput.value.trim();
                if (!memberId) {
                    memberNameInput.value = "";
                    lastQuery = "";
                    return;
                }
                // 防止重複查詢與過快請求
                if (memberId === lastQuery) return;
                lastQuery = memberId;

                clearTimeout(timer);
                timer = setTimeout(() => {
                    fetch(`/admin/member/name?memberId=${memberId}`)
                        .then(res => res.json())
                        .then(data => {
                            if (data && data.memberName) {
                                memberNameInput.value = data.memberName;
                            } else {
                                memberNameInput.value = "查無此會員";
                            }
                        })
                        .catch(() => {
                            memberNameInput.value = "查詢失敗";
                        });
                }, 300); // 300ms debounce
            });
        }
    }

    function bindRoomTypeRoomCascade() {
        const roomTypeSelect = document.getElementById("roomType");
        const roomSelect = document.getElementById("room");
        if (!roomTypeSelect || !roomSelect) return;

        roomTypeSelect.addEventListener("change", function () {
            const roomTypeId = this.value;
            // 清空房間選單
            roomSelect.innerHTML = '<option value="">請選擇</option>';
            if (!roomTypeId) return;

            fetch(`/admin/room/byType?roomTypeId=${roomTypeId}`)
                .then(res => res.json())
                .then(list => {
                    list.forEach(room => {
                        const opt = document.createElement("option");
                        opt.value = room.roomId;
                        opt.textContent = room.roomId;
                        roomSelect.appendChild(opt);
                    });
                });
        });
    }

    function bindRoomDetailPriceAutoFill() {
        document.getElementById("orderDetailList").addEventListener("change", function (e) {
            // 房型選擇時
            if (e.target.classList.contains("roomTypeSelect") || e.target.classList.contains("roomAmountInput") || e.target.classList.contains("roomAmountSelect")) {
                const item = e.target.closest(".order-detail-item");
                const roomTypeSelect = item.querySelector(".roomTypeSelect");
                const amountInput = item.querySelector(".roomAmountInput, .roomAmountSelect");
                const priceInput = item.querySelector(".roomPriceInput");
                const numberOfPeopleSelect = item.querySelector(".numberOfPeopleSelect");
                // 取得單間最大人數
                const guestNum = roomTypeSelect?.selectedOptions[0]?.getAttribute("data-guest-num") || 4;
                // 取得房間數量
                const amount = Number(amountInput?.value) || 1;
                // 計算最大入住人數
                const maxPeople = amount * guestNum;
                // 更新入住人數選單
                if (numberOfPeopleSelect) {
                    numberOfPeopleSelect.innerHTML = '<option value="">請選擇人數</option>';
                    for (let i = 1; i <= maxPeople; i++) {
                        const opt = document.createElement("option");
                        opt.value = i;
                        opt.textContent = i;
                        numberOfPeopleSelect.appendChild(opt);
                    }
                }
                // 房型選擇時自動帶入價格
                if (e.target.classList.contains("roomTypeSelect")) {
                    const price = roomTypeSelect.selectedOptions[0].getAttribute("data-price") || 0;
                    priceInput.setAttribute("data-unit-price", price);
                    priceInput.value = price * amount;
                }
                // 房間數量變動時自動計算價格
                if (e.target.classList.contains("roomAmountInput") || e.target.classList.contains("roomAmountSelect")) {
                    let unitPrice = priceInput.getAttribute("data-unit-price");
                    if (!unitPrice) {
                        unitPrice = 0;
                    }
                    priceInput.value = unitPrice * amount;
                }
            }
            calcActualAmount(); // 每次明細變動都重新計算總價
        });
    }

    // 折扣金額變動時
    document.addEventListener("input", function (e) {
        if (e.target && e.target.id === "discountAmount") {
            calcActualAmount();
        }
    });

    // 入住/退房日期變動時重新計算
    document.addEventListener("change", function (e) {
        if (e.target && (e.target.id === "checkInDate" || e.target.id === "checkOutDate")) {
            calcActualAmount();
        }
    });

    // 若有「加入其他房型」按鈕，新增明細後也要呼叫 calcActualAmount()
    document.addEventListener("click", function (e) {
        if (e.target && e.target.id === "btnAddDetail") {
            setTimeout(calcActualAmount, 100); // 等新欄位渲染後再算
        }
    });

    function calcActualAmount() {
        // 取得所有房價 input
        const priceInputs = document.querySelectorAll("#orderDetailList .roomPriceInput");
        let totalRoomPrice = 0;
        priceInputs.forEach(input => {
            totalRoomPrice += Number(input.value) || 0;
        });
        // 填入房間總數量
        const roomAmountInput = document.getElementById("roomAmount");
        if (roomAmountInput) {
            const roomAmount = document.querySelectorAll("#orderDetailList .order-detail-item").length;
            roomAmountInput.value = roomAmount;
        }
        // 填入房間總價格
        const totalRoomPriceInput = document.getElementById("totalRoomPrice");
        if (totalRoomPriceInput) totalRoomPriceInput.value = totalRoomPrice;
        // 取得折扣金額
        const discountInput = document.getElementById("discountAmount");
        const discount = Number(discountInput?.value) || 0;
        // ===== 新增：計算專案總價 =====
        let projectTotal = 0;
        let projectPeople = 0;
        let allPeopleSelected = true;
        const restoProjectArea = document.getElementById("restoProjectArea");
        if (restoProjectArea && restoProjectArea.style.display !== "none") {
            const planSelect = document.getElementById("projectPlanSelect");
            const planOption = planSelect?.selectedOptions[0];
            if (planOption) {
                if (planOption.value == "1") projectTotal = 800;
                else if (planOption.value == "2") projectTotal = 1800;
                else if (planOption.value == "3") projectTotal = 2800;
            }
            // 取專案人數（任一區塊都一樣）
            const peopleInputs = restoProjectArea.querySelectorAll('.projectPeopleInput');
            peopleInputs.forEach(input => {
                if (!input.value) {
                    allPeopleSelected = false;
                } else {
                    projectPeople += Number(input.value);
                }
            });
            projectTotal = projectPeople * projectTotal;
        }
        // ===== 計算入住天數（退房日不算在內） =====
        let stayNights = 1;
        const checkInInput = document.getElementById("checkInDate");
        const checkOutInput = document.getElementById("checkOutDate");
        if (checkInInput && checkOutInput && checkInInput.value && checkOutInput.value) {
            const checkIn = new Date(checkInInput.value);
            const checkOut = new Date(checkOutInput.value);
            // 計算入住天數：退房日不算在內
            // 例如：7/3入住，7/5退房，實際入住天數是7/3和7/4，共2天
            const diff = (checkOut - checkIn) / (1000 * 60 * 60 * 24);
            stayNights = diff > 0 ? diff : 1;
        }

        // ===== 訂單總金額 = (總房價 + 專案總價) × 入住天數 =====
        const totalPriceInput = document.getElementById("totalPrice");
        const orderTotal = (totalRoomPrice + projectTotal) * stayNights;
        if (totalPriceInput) totalPriceInput.value = orderTotal;
        // ===== 實際支付金額 = 訂單總金額 - 折扣金額 =====
        const actualAmountInput = document.getElementById("actualAmount");
        if (actualAmountInput) actualAmountInput.value = Math.max(orderTotal - discount, 0);
    }

    // 新增明細按鈕綁定
    function bindAddRoomDetailBlock() {
        console.log("加入房型被點擊");
        const addBtn = document.getElementById("btnAddDetail");
        const detailList = document.getElementById("orderDetailList");

        if (!addBtn || !detailList) return;

        addBtn.addEventListener("click", () => {
            const currentItems = detailList.querySelectorAll(".order-detail-item");
            if (currentItems.length >= 5) {
                alert("最多只能新增五個房型");
                return;
            }

            const newIndex = currentItems.length;
            const newItem = currentItems[0].cloneNode(true);

            // 清空輸入值 & 更新 name 屬性編號
            newItem.querySelectorAll("input, select").forEach(input => {
                const name = input.getAttribute("name");
                if (name) {
                    const newName = name.replace(/\[\d+\]/, `[${newIndex}]`);
                    input.setAttribute("name", newName);
                }

                // 清除值（不清readonly 價格欄位以便自動帶入）
                if (input.type === "text" || input.type === "number") {
                    input.value = input.classList.contains("roomPriceInput") ? "" : "";
                }
                if (input.tagName === "SELECT") {
                    input.selectedIndex = 0;
                }
            });

            // 新增明細時，清空主鍵
            const hiddenId = newItem.querySelector("input[name$='.roomOrderListId']");
            if (hiddenId) hiddenId.value = "";

            // 取得最後一個房型區塊
            const lastItem = currentItems[currentItems.length - 1];
            // 插入在最後一個房型區塊的後面
            lastItem.after(newItem);
        });


        if (detailList) {
            detailList.addEventListener("click", function (e) {
                const btn = e.target.closest(".remove-detail-btn");
                if (btn) {
                    console.log("orderlistid:", btn.getAttribute("data-id"));

                    const item = btn.closest(".order-detail-item");
                    if (!item) return;

                    const roomOrderListId = btn.getAttribute("data-id");

                    if (!confirm("確定要移除這筆訂單明細嗎？")) {
                        return;
                    }

                    // 新增模式：只刪除前端區塊
                    if (detailList.querySelectorAll(".order-detail-item").length > 1) {
                        item.remove();
                        calcActualAmount();
                    } else {
                        alert("至少要保留一個房型");
                    }
                    return;
                }
            });
        }
    }



    function checkSchedule() {
        const roomTypeId = roomTypeSelect.value;
        const checkInDate = checkInInput.value;
        const checkOutDate = checkOutInput.value;
        // 先移除外層提示
        const parent = warningArea.parentElement;
        let oldMsg = parent.nextElementSibling;
        if (oldMsg && oldMsg.classList.contains('roomWarningMsg')) {
            oldMsg.remove();
        }
        // 如果有一個沒填，直接 return，不顯示任何提示
        if (!roomTypeId || !checkInDate || !checkOutDate) {
            warningArea.textContent = '';
            return;
        }
        // 呼叫API
        fetch(`/admin/roomo_info/${roomTypeId}/check_schedule?start=${checkInDate}&end=${checkOutDate}`)
            .then(res => res.json())
            .then(minAvailable => {
                // 先移除舊的
                const parent = warningArea.parentElement;
                let oldMsg = parent.nextElementSibling;
                if (oldMsg && oldMsg.classList.contains('roomWarningMsg')) {
                    oldMsg.remove();
                }

                const msgDiv = document.createElement('div');
                msgDiv.className = 'roomWarningMsg mt-1';

                if (minAvailable > 0) {
                    msgDiv.textContent = '該房型於選定日期內皆有空房';
                    msgDiv.classList.add('text-success');
                    roomAmountSelect.disabled = false;
                    roomAmountSelect.innerHTML = "";
                    for (let i = 1; i <= minAvailable; i++) {
                        const opt = document.createElement("option");
                        opt.value = i;
                        opt.textContent = i;
                        roomAmountSelect.appendChild(opt);
                    }
                } else {
                    msgDiv.textContent = '選定區間內有滿房日期';
                    msgDiv.classList.add('text-danger');
                    roomAmountSelect.innerHTML = `<option value="">無法選擇</option>`;
                    roomAmountSelect.disabled = true;
                }
                parent.after(msgDiv);
            })
            .catch(() => {
                warningArea.textContent = '查詢失敗，請稍後再試';
                warningArea.classList.remove('text-success');
                warningArea.classList.add('text-danger');
            });
    }

    // ===== 自動查詢優惠券 =====
    function bindCouponAutoQuery() {
        // 先宣告所有會用到的變數
        const memberIdInput = document.getElementById("memberId");
        const couponSelect = document.getElementById("coupon");
        const discountAmountInput = document.getElementById("discountAmount");
        const roomAmountInput = document.querySelector('input[name="orderDetails[0].roomAmount"]');
        const roomPriceInput = document.getElementById("totalRoomPrice");
        const roomTypeSelect = document.querySelector('select[name="orderDetails[0].roomTypeId"]');
        if (!memberIdInput || !couponSelect || !discountAmountInput || !roomPriceInput || !roomTypeSelect) return;

        let loaded = false;
        let timer = null;

        function queryCoupons() {
            const memberId = memberIdInput.value.trim();
            // 如果 roomAmountInput 存在才取 value
            const roomAmount = roomAmountInput ? roomAmountInput.value : 0;
            const roomPrice = roomPriceInput.value || 0;
            if (!memberId) {
                couponSelect.innerHTML = '<option value="">--- 請選擇 ---</option>';
                discountAmountInput.value = 0;
                return;
            }
            console.log("查詢優惠券", memberId, roomPrice);
            fetch(`/admin/roomo_info/member_coupons?memberId=${memberId}&totalAmount=${roomPrice}`)
                .then(res => res.json())
                .then(coupons => {
                    couponSelect.innerHTML = '<option value="">--- 請選擇 ---</option>';
                    if (coupons.length > 0) {
                        coupons.forEach(coupon => {
                            const opt = document.createElement("option");
                            opt.value = coupon.couponCode;
                            opt.textContent = coupon.couponName + `（${coupon.couponCode}）`;
                            opt.setAttribute("data-discount", coupon.discountValue || 0);
                            couponSelect.appendChild(opt);
                        });
                        couponSelect.selectedIndex = 1;
                        const firstDiscount = coupons[0].discountValue || 0;
                        discountAmountInput.value = firstDiscount;
                    } else {
                        discountAmountInput.value = 0;
                    }
                    loaded = true;
                })
                .catch(() => {
                    couponSelect.innerHTML = '<option value="">查詢失敗</option>';
                    discountAmountInput.value = 0;
                    loaded = false;
                });
        }

        function checkRoomTypeSelected(e) {
            if (!roomTypeSelect.value) {
                e.preventDefault();
                alert("請先選擇房型，再選擇優惠券！");
                couponSelect.blur();
                return false;
            }
            if (!memberIdInput.value) {
                e.preventDefault();
                alert("請先輸入會員編號，再選擇優惠券！");
                couponSelect.blur();
                return false;
            }
            if (!loaded) queryCoupons();
        }

        // 綁定事件前都要判斷元素存在
        if (memberIdInput) {
            memberIdInput.addEventListener("input", function () {
                // 折價券選單與折扣金額
                couponSelect.innerHTML = '<option value="">--- 請選擇 ---</option>';
                discountAmountInput.value = 0;
                loaded = false;

                // 會員姓名
                const memberNameInput = document.getElementById("memberName");
                if (memberNameInput) memberNameInput.value = "";

                // 房型、房間數、價格等（只重設第一組，若有多組可自行加強）
                if (roomTypeSelect) roomTypeSelect.selectedIndex = 0;
                if (roomAmountInput) roomAmountInput.value = "";
                if (roomPriceInput) roomPriceInput.value = "";

                // 其他明細欄位（如有多組，建議全部清空只留一組）
                const detailList = document.getElementById("orderDetailList");
                if (detailList) {
                    const items = detailList.querySelectorAll(".order-detail-item");
                    items.forEach((item, idx) => {
                        if (idx === 0) {
                            // 第一組清空
                            item.querySelectorAll("input, select").forEach(input => {
                                if (input.type === "text" || input.type === "number") input.value = "";
                                if (input.tagName === "SELECT") input.selectedIndex = 0;
                            });
                        } else {
                            // 其他組移除
                            item.remove();
                        }
                    });
                }

                // 房間總數、總價、實付金額
                const roomAmountInput2 = document.getElementById("roomAmount");
                if (roomAmountInput2) roomAmountInput2.value = 0;
                const totalRoomPriceInput = document.getElementById("totalRoomPrice");
                if (totalRoomPriceInput) totalRoomPriceInput.value = 0;
                const actualAmountInput = document.getElementById("actualAmount");
                if (actualAmountInput) actualAmountInput.value = 0;
            });
            memberIdInput.addEventListener("blur", function () {
                const memberId = memberIdInput.value.trim();
                const roomTypeId = roomTypeSelect.value;
                const roomPrice = roomPriceInput.value || 0;
                if (memberId && roomTypeId && roomPrice > 0) {
                    console.log("會員ID輸入完成，自動查詢優惠券");
                    queryCoupons();
                }
            });
        }
        if (roomAmountInput) {
            roomAmountInput.addEventListener("input", function () {
                loaded = false;
            });
        }
        if (roomPriceInput) {
            roomPriceInput.addEventListener("input", function () {
                loaded = false;
            });
            roomPriceInput.addEventListener("blur", function () {
                const memberId = memberIdInput.value.trim();
                const roomTypeId = roomTypeSelect.value;
                const roomPrice = roomPriceInput.value || 0;
                if (memberId && roomTypeId && roomPrice > 0) {
                    console.log("價格變動後，自動查詢優惠券");
                    queryCoupons();
                }
            });
        }
        if (roomTypeSelect) {
            roomTypeSelect.addEventListener("change", function () {
                loaded = false;
                couponSelect.innerHTML = '<option value="">--- 請選擇 ---</option>';
                discountAmountInput.value = 0;
                const memberId = memberIdInput.value.trim();
                const roomPrice = roomPriceInput.value || 0;
                if (memberId && roomPrice > 0) {
                    console.log("房型變更後，自動查詢優惠券");
                    queryCoupons();
                }
            });
        }
        if (couponSelect) {
            couponSelect.addEventListener("focus", checkRoomTypeSelected);
            couponSelect.addEventListener("click", checkRoomTypeSelected);
            couponSelect.addEventListener("blur", function () {
                calcActualAmount();
            });
            couponSelect.addEventListener("change", function () {
                const selected = couponSelect.selectedOptions[0];
                const discount = selected ? selected.getAttribute("data-discount") : 0;
                discountAmountInput.value = discount || 0;
                calcActualAmount();
            });
        }
    }

    function bindRoomAmountSelectUpdate() {
        // 檢查是否有多房型明細的情況
        const orderDetailList = document.getElementById("orderDetailList");
        if (orderDetailList) {
            // 多房型明細的情況，使用 bindRoomScheduleCheck 來處理
            bindRoomScheduleCheck();
            return;
        }

        // 單一房型的情況（如果還有的話）
        const roomTypeSelect = document.getElementById("roomTypeId");
        const checkInInput = document.getElementById("checkInDate");
        const checkOutInput = document.getElementById("checkOutDate");
        const roomAmountSelect = document.querySelector(".roomAmountSelect");
        const warningArea = document.getElementById("roomWarning");

        if (!roomTypeSelect || !checkInInput || !checkOutInput || !roomAmountSelect || !warningArea) {
            console.warn("部分元素未找到，無法綁定房間數更新事件");
            return;
        }

        function updateRoomAmountOptions() {
            const roomTypeId = roomTypeSelect.value;
            const checkIn = checkInInput.value;
            const checkOut = checkOutInput.value;

            if (!roomTypeId || !checkIn || !checkOut) {
                roomAmountSelect.innerHTML = `<option value="">請先選擇房型與日期</option>`;
                roomAmountSelect.disabled = true;
                warningArea.textContent = "";
                return;
            }

            fetch(`/admin/roomo_info/${roomTypeId}/check_schedule?start=${checkIn}&end=${checkOut}`)
                .then(res => res.json())
                .then(minAvailable => {
                    if (minAvailable > 0) {
                        warningArea.textContent = '該房型於選定日期內皆有空房';
                        warningArea.classList.remove('text-danger');
                        warningArea.classList.add('text-success');
                        roomAmountSelect.disabled = false;
                        // 產生可選數量
                        roomAmountSelect.innerHTML = "";
                        for (let i = 1; i <= minAvailable; i++) {
                            const opt = document.createElement("option");
                            opt.value = i;
                            opt.textContent = i;
                            roomAmountSelect.appendChild(opt);
                        }
                    } else {
                        warningArea.textContent = '選定區間內有滿房日期';
                        warningArea.classList.remove('text-success');
                        warningArea.classList.add('text-danger');
                        roomAmountSelect.innerHTML = `<option value="">無法選擇</option>`;
                        roomAmountSelect.disabled = true;
                    }
                })
                .catch(err => {
                    console.error("fetch 錯誤", err);
                    warningArea.textContent = "房型查詢失敗，請稍後重試";
                    roomAmountSelect.disabled = true;
                });
        }

        // 綁定事件
        roomTypeSelect.addEventListener("change", updateRoomAmountOptions);
        checkInInput.addEventListener("change", updateRoomAmountOptions);
        checkOutInput.addEventListener("change", updateRoomAmountOptions);
    }

    // ===== 加購專案區塊顯示/隱藏與資料載入 =====
    function bindProjectAddOnArea() {
        const projectAddOnRadios = document.querySelectorAll('input[name="projectAddOn"]');
        const projectPlanArea = document.getElementById("projectPlanArea");
        const projectPlanSelect = document.getElementById("projectPlanSelect");
        const restoProjectArea = document.getElementById("restoProjectArea");
        const checkInDateInput = document.getElementById("checkInDate");

        if (!projectAddOnRadios.length || !projectPlanArea || !projectPlanSelect || !restoProjectArea) return;

        // 1. 綁定「是否加購專案」radio
        projectAddOnRadios.forEach(radio => {
            radio.addEventListener("change", function () {
                if (this.value === "1") {
                    projectPlanArea.style.display = "block";
                } else {
                    projectPlanArea.style.display = "none";
                    restoProjectArea.style.display = "none";
                    restoProjectArea.innerHTML = "";
                    projectPlanSelect.value = "";
                    calcActualAmount();
                }
            });
        });

        // 2. 綁定「專案方案」select
        projectPlanSelect.addEventListener("change", function () {
            generateProjectBlocks(this.value);
            calcActualAmount(); // 立即結算訂單總金額
        });

        // 3. 動態產生專案區塊
        function generateProjectBlocks(planValue) {
            restoProjectArea.innerHTML = "";
            if (!planValue) {
                restoProjectArea.style.display = "none";
                return;
            }
            // 只產生一區
            const block = document.createElement("div");
            block.className = "project-block mb-3 p-3 border rounded";
            block.innerHTML = `
                <div class="mb-2"><strong>請選擇餐廳</strong></div>
                <div class="row g-2 align-items-end">
                  <div class="col-md-4">
                    <label class="form-label">餐廳</label>
                    <select class="form-select restoSelect" required>
                      <option value="">請選擇餐廳</option>
                    </select>
                  </div>
                  <div class="col-md-2">
                    <label class="form-label">兒童座椅數</label>
                    <input type="number" class="form-control highChairsInput" min="0" value="0">
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">餐廳需求備註</label>
                    <input type="text" class="form-control restoReqInput" maxlength="50" placeholder="如：靠窗、過敏...">
                  </div>
                  <div class="col-md-2 d-flex align-items-end">
                    <span class="autoAssignResult"></span>
                  </div>
                </div>
            `;
            restoProjectArea.appendChild(block);
            restoProjectArea.style.display = "block";
            bindRestoSelectAutoAssign(block, planValue);
            syncProjectPeopleInput();
        }

        function bindRestoSelectAutoAssign(block, planValue) {
            const restoSelect = block.querySelector(".restoSelect");
            const autoAssignResult = block.querySelector(".autoAssignResult");
            // 載入餐廳選單
            fetch("/admin/roomo_info/resto/list")
                .then(res => res.json())
                .then(restos => {
                    restoSelect.innerHTML = '<option value="">請選擇餐廳</option>';
                    restos.forEach(resto => {
                        const opt = document.createElement("option");
                        opt.value = resto.restoId;
                        opt.textContent = resto.restoName;
                        restoSelect.appendChild(opt);
                    });
                });
            restoSelect.addEventListener("change", function () {
                const restoId = Number(this.value);
                if (!restoId) {
                    autoAssignResult.textContent = "";
                    return;
                }
                // 取得入住/退房/人數/方案
                const checkInDate = document.getElementById("checkInDate").value;
                const checkOutDate = document.getElementById("checkOutDate").value;
                const plan = planValue;
                // 專案人數：動態加總所有房型明細的入住人數
                let projectPeople = 0;
                let allPeopleSelected = true;
                document.querySelectorAll('#orderDetailList select[name$=".numberOfPeople"]').forEach(select => {
                    if (!select.value) {
                        allPeopleSelected = false;
                    } else {
                        projectPeople += Number(select.value);
                    }
                });
                if (!checkInDate || !checkOutDate || !allPeopleSelected) {
                    autoAssignResult.textContent = "請先選擇入住/退房與人數";
                    autoAssignResult.className = "autoAssignResult text-danger";
                    return;
                }
                // 呼叫 API
                fetch(`/admin/roomo_info/auto_assign_meal`, {
                    method: "POST",
                    headers: { "Content-Type": "application/x-www-form-urlencoded" },
                    body: `restoId=${restoId}&numOfPeople=${projectPeople}&checkInDate=${checkInDate}&checkOutDate=${checkOutDate}&projectPlan=${plan}`
                })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        autoAssignResult.textContent = "可預約";
                        autoAssignResult.className = "autoAssignResult text-success";
                    } else {
                        autoAssignResult.textContent = data.message || "有日期/時段無法預約";
                        autoAssignResult.className = "autoAssignResult text-danger";
                    }
                })
                .catch(() => {
                    autoAssignResult.textContent = "查詢失敗";
                    autoAssignResult.className = "autoAssignResult text-danger";
                });
            });
        }

        // 5. 專案人數同步入住人數總和，且不可編輯
        function syncProjectPeopleInput() {
            function updateProjectPeople() {
                // 取得所有房型區域的入住人數 select
                const peopleSelects = document.querySelectorAll('#orderDetailList select[name$=".numberOfPeople"]');
                let totalPeople = 0;
                peopleSelects.forEach(select => {
                    totalPeople += Number(select.value) || 0;
                });
                // 將專案區塊的人數 input 設為總和且 readonly
                const projectPeopleInputs = document.querySelectorAll('.projectPeopleInput');
                projectPeopleInputs.forEach(input => {
                    input.value = totalPeople;
                    input.readOnly = true;
                });
                calcActualAmount();
            }
            // 初始同步
            updateProjectPeople();
            // 綁定房型區域入住人數 select 變動時自動同步
            document.querySelectorAll('#orderDetailList select[name$=".numberOfPeople"]').forEach(select => {
                select.addEventListener('change', updateProjectPeople);
            });
        }
    }

    function bindRoomScheduleCheck() {
        // 取得所有房型、入住、退房欄位
        // 注意：多明細時要針對每一組動態綁定
        document.querySelectorAll('.order-detail-item').forEach(function (item) {
            const roomTypeSelect = item.querySelector('.roomTypeSelect');
            const checkInInput = document.getElementById('checkInDate');
            const checkOutInput = document.getElementById('checkOutDate');
            const roomAmountSelect = item.querySelector('.roomAmountSelect');
            let warningArea = item.querySelector('.roomWarning');
            if (!warningArea) {
                // 若沒有就自動加一個
                warningArea = document.createElement('div');
                warningArea.className = 'roomWarning text-danger mt-1';
                roomTypeSelect.parentElement.insertBefore(warningArea, roomTypeSelect.nextSibling);
            }

            function checkSchedule() {
                const roomTypeId = roomTypeSelect.value;
                const checkInDate = checkInInput.value;
                const checkOutDate = checkOutInput.value;
                // 先移除外層提示
                const parent = warningArea.parentElement;
                let oldMsg = parent.nextElementSibling;
                if (oldMsg && oldMsg.classList.contains('roomWarningMsg')) {
                    oldMsg.remove();
                }
                // 如果有一個沒填，直接 return，不顯示任何提示
                if (!roomTypeId || !checkInDate || !checkOutDate) {
                    warningArea.textContent = '';
                    return;
                }
                // 呼叫API
                fetch(`/admin/roomo_info/${roomTypeId}/check_schedule?start=${checkInDate}&end=${checkOutDate}`)
                    .then(res => res.json())
                    .then(minAvailable => {
                        // 先移除舊的
                        const parent = warningArea.parentElement;
                        let oldMsg = parent.nextElementSibling;
                        if (oldMsg && oldMsg.classList.contains('roomWarningMsg')) {
                            oldMsg.remove();
                        }

                        const msgDiv = document.createElement('div');
                        msgDiv.className = 'roomWarningMsg mt-1';

                        if (minAvailable > 0) {
                            msgDiv.textContent = '該房型於選定日期內皆有空房';
                            msgDiv.classList.add('text-success');
                            roomAmountSelect.disabled = false;
                            roomAmountSelect.innerHTML = "";
                            for (let i = 1; i <= minAvailable; i++) {
                                const opt = document.createElement("option");
                                opt.value = i;
                                opt.textContent = i;
                                roomAmountSelect.appendChild(opt);
                            }
                        } else {
                            msgDiv.textContent = '選定區間內有滿房日期';
                            msgDiv.classList.add('text-danger');
                            roomAmountSelect.innerHTML = `<option value="">無法選擇</option>`;
                            roomAmountSelect.disabled = true;
                        }
                        parent.after(msgDiv);
                    })
                    .catch(() => {
                        warningArea.textContent = '查詢失敗，請稍後再試';
                        warningArea.classList.remove('text-success');
                        warningArea.classList.add('text-danger');
                    });
            }

            // 綁定事件
            roomTypeSelect.addEventListener('change', checkSchedule);
            checkInInput.addEventListener('change', checkSchedule);
            checkOutInput.addEventListener('change', checkSchedule);
        });
    }

    // 編輯模式：入住/退房日期變動時，清空所有房型與房間數量
    function bindEditDateChangeClearDetail() {
        const checkInInput = document.getElementById('checkInDate');
        const checkOutInput = document.getElementById('checkOutDate');
        if (!checkInInput || !checkOutInput) return;

        function clearDetails() {
            document.querySelectorAll('.order-detail-item').forEach(function (item) {
                // 清空房型
                const roomTypeSelect = item.querySelector('.roomTypeSelect');
                if (roomTypeSelect) {
                    roomTypeSelect.value = '';
                }
                // 清空房間數量
                const roomAmountSelect = item.querySelector('.roomAmountSelect');
                if (roomAmountSelect) {
                    roomAmountSelect.innerHTML = '<option value="">請先選擇房型與日期</option>';
                    roomAmountSelect.disabled = true;
                }
                // 清空房價
                const priceInput = item.querySelector('.roomPriceInput');
                if (priceInput) priceInput.value = '';
                // 清空入住人數
                const numberOfPeopleSelect = item.querySelector('.numberOfPeopleSelect');
                if (numberOfPeopleSelect) {
                    numberOfPeopleSelect.innerHTML = '<option value="">請先選擇房型</option>';
                    numberOfPeopleSelect.disabled = true;
                }
            });
        }

        checkInInput.addEventListener('change', clearDetails);
        checkOutInput.addEventListener('change', clearDetails);
    }

    function bindCancelProjectAddOn() {
        const cancelBtn = document.getElementById("cancelProjectAddOn");
        if (!cancelBtn) return;
      
        const roomOrderId = cancelBtn.getAttribute("data-id"); // ✅ 正確取得 ID 值
        if (!roomOrderId) return;
      
        cancelBtn.addEventListener("click", async () => {
          const confirmed = confirm("確定要取消加購專案嗎？取消後不可再修改，需重新下單。");
      
          if (!confirmed) return;
      
          try {
            const res = await fetch("/admin/roomo_info/cancel_project_add_on", {
              method: "POST",
              headers: {
                "Content-Type": "application/x-www-form-urlencoded"
              },
              body: "roomOrderId=" + encodeURIComponent(roomOrderId)
            });
      
            const data = await res.json();
      
            if (data.success) {
              alert("已成功取消加購專案");
              reloadEditModal(roomOrderId);
            } else {
              alert(data.message || "取消失敗");
            }
          } catch (err) {
            alert("取消加購專案失敗：" + err.message);
          }
        });
      }
      

      async function reloadEditModal(roomOrderId) {
        const container = document.getElementById("modalContainer"); // 或你自己的容器
      
        // 清掉舊 modal（若有）
        const oldModal = document.getElementById("roomoEditModal");
        if (oldModal) {
          const modal = bootstrap.Modal.getInstance(oldModal);
          if (modal) modal.hide();
          await new Promise((r) => setTimeout(r, 500));
          oldModal.remove();
        }
      
        // fetch 新 modal HTML
        fetch(`/admin/roomo_info/edit?roomOrderId=${roomOrderId}`)
          .then((res) => res.text())
          .then((html) => {
            container.innerHTML = html;
            const modalEl = document.getElementById("roomoEditModal");
            const modal = new bootstrap.Modal(modalEl);
            modal.show();
      
            // 重新綁定 modal 初始化邏輯
            modalEl.addEventListener("shown.bs.modal", () => {
              bindFormSubmitEdit();
              bindRoomTypeRoomCascade();
              bindAddRoomDetailBlock();
              bindRoomDetailPriceAutoFill();
              bindCouponAutoQuery();
              bindCancelProjectAddOn(roomOrderId); // ✅ 重新綁定取消按鈕
            });
          });
      }
      

});

async function doSave() {
    // 取得編輯表單
    const form = document.getElementById("roomOrderEditForm");
    if (!form) return false;

    // 準備 FormData
    const formData = new FormData(form);

    // 如果有用 TinyMCE，記得同步內容
    if (window.tinymce && tinymce.get("roomoContent")) {
        formData.set("roomoContent", tinymce.get("roomoContent").getContent() || "");
    }

    try {
        const res = await fetch("/admin/roomo_info/update", {
            method: "POST",
            body: formData
        });

        // 如果後端有 redirect，代表成功
        if (res.redirected) {
            window.location.href = res.url;
            return true;
        }

        // 如果沒 redirect，可能是表單驗證失敗，顯示錯誤
        const html = await res.text();
        // 更新 modal 內容（只更新 .modal-body）
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, "text/html");
        const newBody = doc.querySelector(".modal-body");
        const oldBody = document.querySelector("#roomoEditModal .modal-body");
        if (newBody && oldBody) {
            oldBody.replaceWith(newBody);
        }
        alert("儲存失敗，請檢查表單內容！");
        return false;
    } catch (err) {
        alert("儲存失敗：" + err.message);
        return false;
    }
}


