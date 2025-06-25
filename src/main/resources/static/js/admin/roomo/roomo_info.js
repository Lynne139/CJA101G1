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
            columnDefs: [
                { targets: [0], width: "20%" },
                { targets: [1], width: "25%" },
                { targets: [2], width: "25%" },
                { targets: [3], width: "10%", orderable: false },
                { targets: [4], width: "5%", orderable: false },
                { targets: [5], width: "15%", orderable: false }
            ],
            searching: false,
            ordering: true,
            info: true
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


    // ===== 刪除 =====
    document.addEventListener("click", function (e) {
        if (e.target.closest(".btn_delete")) {
            const btn = e.target.closest(".btn_delete");
            const roomoId = btn.getAttribute("data-id");

            if (!roomoId) return;

            if (confirm("項目一旦封存將無法復原，僅作為訂單紀錄對應，是否確定封存？")) {
                fetch(`/admin/roomo_info/delete?roomoId=${roomoId}`, {
                    method: 'GET'
                })
                    .then(res => {
                        if (res.redirected) {
                            window.location.href = res.url; // 讓DataTables因為整頁刷新而重載資料
                            return;
                        }
                        return res.text(); // 若失敗沒redirect，才繼續處理
                    })
                    .catch(err => {
                        alert("刪除失敗：" + err.message);
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
            table_grid:true,
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



// 圖片清除按鈕收合
    function clearBtnToggle(pic, clearBtn) {
        if (!pic || pic.includes("no_img.svg")) {
            clearBtn.classList.add("d-none");
        } else {
            clearBtn.classList.remove("d-none");
        }
    }

    function bindImagePreview() {
        const input = document.getElementById("uploadImg");
        const preview = document.getElementById("imgPreview");
        const clearFlag = document.getElementById("clearImgFlag");
        let clearBtn = document.getElementById("btnClearImage");

        if (!input || !preview || !clearFlag || !clearBtn) return;

        // 抓資料庫原始圖片作為fallback
        const originalSrc = preview.src;


        // 初始以及表單被還原時，input是或被清空，都得判定預覽圖與按鈕狀態
        if (input.files.length == 0) {

            if (originalSrc.includes("no_img.svg")) {
                // 新增或資料庫沒圖，顯示預設圖
                preview.src = "/images/admin/no_img.svg";
                clearFlag.value = "false";
                clearBtnToggle(preview.src, clearBtn);

            } else {
                // 有資料庫圖，預設顯示它
                preview.src = originalSrc;
                console.log(preview.src);
                clearFlag.value = "false";
                clearBtnToggle(preview.src, clearBtn);
            }
        }

        if (input && preview) {

            input.addEventListener("change", function () {

                const file = this.files[0];

                if (!file) {
                    preview.src = originalSrc;
                    clearBtnToggle(preview.src, clearBtn);
                    clearFlag.value = "false";
                    return;
                }

                const validTypes = ["image/png", "image/jpeg", "image/gif"];
                if (!validTypes.includes(file.type)) {
                    alert("只接受 PNG / JPEG / GIF 圖片");
                    this.value = "";
                    preview.src = originalSrc;
                    clearBtnToggle(preview.src, clearBtn);
                    clearFlag.value = "false";
                    return;
                }

                if (file.size > 16 * 1024 * 1024) {
                    alert("圖片不得超過 16MB！");
                    this.value = "";
                    preview.src = originalSrc;
                    clearBtnToggle(preview.src, clearBtn);
                    clearFlag.value = "false";
                    return;
                }

                const reader = new FileReader();
                reader.onload = e => {
                    preview.src = e.target.result;
                    clearBtnToggle(preview.src, clearBtn);
                    clearFlag.value = "false";
                };
                reader.readAsDataURL(file);
            });
        }

        clearBtn.addEventListener("click", () => {
            input.value = "";
            preview.src = "/images/admin/no_img.svg";
            clearFlag.value = "true";
            clearBtn.classList.add("d-none");
        });
    }


    // ===== Modal - Add =====
    //按下新增按鈕打開modal
    document.addEventListener("click",async function (e) {
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
                    bindImagePreview();
                    bindFormSubmitAdd();
                    bindMemberIdAutoFill();
                    bindRoomTypeRoomCascade();

                });

            })
            .catch(err => {
                alert("載入表單失敗：" + err.message);
            });
    });


    function bindFormSubmitAdd() {
        const submitBtn = document.getElementById("btnSubmitAdd");
        if (!submitBtn) return;

        submitBtn.addEventListener("click", function (e) {
            e.preventDefault();

            showBtnOverlay(submitBtn); // 加入 loading 遮罩

            const form = document.getElementById("roomoAddForm");
            const formData = new FormData(form);

            // 將 TinyMCE 的內容寫回 textarea
            const content = tinymce.get("roomoContent")?.getContent();
            formData.set("roomoContent", content || "");

            fetch("/admin/roomo_info/insert", {
                method: "POST",
                body: formData
            })
                .then(res => {

                    if (res.redirected) {
                        window.location.href = res.url; // 讓瀏覽器照後端redirect去重新載入頁面

                        // 成功，清空並關閉modal
                        document.getElementById("roomoAddForm").reset();
                        tinymce.get("roomoContent")?.setContent("");
                        document.getElementById("imgPreview").src = "";

                        const modal = bootstrap.Modal.getInstance(document.getElementById("roomoAddModal"));
                        modal.hide();

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
                        bindImagePreview();
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


    // ===== Modal - Edit =====
    //按下編輯icon按鈕打開modal
    document.addEventListener("click",async function (e) {

        if (e.target.closest(".btn_edit")) {
            const editBtn = e.target.closest(".btn_edit");
            const roomoId = editBtn.getAttribute("data-id");
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

                    //確保modal內的textarea完全呈現(bs開modal預設有動畫可能導致延遲)
                    modalEl.addEventListener("shown.bs.modal", async () => {

                        await new Promise(resolve => setTimeout(resolve, 300)); // 等Bootstrap完成動畫
                        //保險先清掉原本的tinymce以及重新初始化以下功能
                        //必須這樣是因為每次modal開啟都是fetch後動態載入插入的新dom
                        //addEventListener()是一次性的綁定，綁舊dom上的會失效，必須重新綁新渲染的dom
                        //timymce須清除，因為舊的tinymce編輯器有實體殘留
                        tinymce.remove();
                        initTinyMCE();
                        bindImagePreview();
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

            const form = document.getElementById("roomoEditForm");
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
                        document.getElementById("imgPreview").src = "";

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
                        bindImagePreview();
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
function bindMemberIdAutoFill() {
    const memberIdInput = document.getElementById("memberId");
    const memberNameInput = document.getElementById("memberName");
    if (!memberIdInput || !memberNameInput) return;

    let lastQuery = "";
    let timer = null;

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




});