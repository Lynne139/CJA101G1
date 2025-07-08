// 驗證碼刷新功能
function refreshCaptcha() {
    const captchaElement = document.querySelector('.captcha-code');
    const randomCode = Math.floor(1000 + Math.random() * 9000);
    captchaElement.textContent = randomCode;
}

// 移除無效樣式當用戶開始輸入
document.querySelectorAll('.form-control, .form-select').forEach(field => {
    field.addEventListener('input', function() {
        this.classList.remove('is-invalid');
    });
});

// 折價券自動查詢與金額帶入
function queryCouponsForMember() {
    const memberId = window.memberId || '';
    const amountInput = document.querySelector('input[name="actualAmount"]');
    const totalAmount = amountInput ? amountInput.value : 0;
    const couponSelect = document.getElementById('couponSelect');
    const discountAmountInput = document.getElementById('discountAmount');
    
    if (!couponSelect || !discountAmountInput) return;

    console.log("🚀 啟動查詢折價券", { memberId, totalAmount });

    if (!memberId) {
        couponSelect.innerHTML = '<option value="">--- 請選擇 ---</option>';
        discountAmountInput.value = 0;
        return;
    }

    fetch(`/orderInfo/member_coupons?memberId=${memberId}&totalAmount=${totalAmount}`)
        .then(res => {
            if (!res.ok) {
                throw new Error('查詢折價券失敗');
            }
            return res.json();
        })
        .then(coupons => {
            couponSelect.innerHTML = '<option value="">--- 請選擇 ---</option>';
            if (coupons.length > 0) {
                coupons.forEach(coupon => {
                    const opt = document.createElement('option');
                    opt.value = coupon.couponCode;
                    opt.textContent = coupon.couponName + `（${coupon.couponCode}）`;
                    opt.setAttribute('data-discount', coupon.discountValue || 0);
                    couponSelect.appendChild(opt);
                });
                // // 預設選擇第一個折價券
                // couponSelect.selectedIndex = 1;
                // discountAmountInput.value = coupons[0].discountValue || 0;
            } else {
                discountAmountInput.value = 0;
            }
        })
        .catch(err => {
            console.error('查詢折價券失敗:', err);
            couponSelect.innerHTML = '<option value="">查詢失敗</option>';
            discountAmountInput.value = 0;
        });
}

// 折價券選擇變更處理
function handleCouponChange() {
    const couponSelect = document.getElementById('couponSelect');
    const discountAmountInput = document.getElementById('discountAmount');
    
    if (!couponSelect || !discountAmountInput) return;
    
    couponSelect.addEventListener('change', function() {
        const selected = this.selectedOptions[0];
        const discount = selected ? selected.getAttribute('data-discount') : 0;
        discountAmountInput.value = discount || 0;
    });
}

// 表單驗證函數
function validateForm() {
    const form = document.getElementById('bookingForm');
    
    // 驗證必填欄位
    const requiredFields = form.querySelectorAll('[required]');
    let isValid = true;
    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            isValid = false;
            field.classList.add('is-invalid');
        } else {
            field.classList.remove('is-invalid');
        }
    });

    // 條款同意
    const terms1 = document.getElementById('terms1');
    const terms2 = document.getElementById('terms2');
    if (!terms1.checked || !terms2.checked) {
        isValid = false;
        alert('請同意服務條款後再進行預訂');
        return false;
    }

    // 驗證碼
    const captchaInput = document.querySelector('.captcha-input');
    const captchaCode = document.querySelector('.captcha-code');
    if (captchaInput.value !== captchaCode.textContent) {
        isValid = false;
        captchaInput.classList.add('is-invalid');
        alert('驗證碼不正確，請重新輸入');
        return false;
    }

    // 檢查付款方式
    const payMethod = document.querySelector('select[name="payMethod"]').value;
    if (payMethod !== "0" && payMethod !== "1") {
        alert("請選擇付款方式");
        return false;
    }

    if (!isValid) {
        alert('請檢查並填寫所有必填欄位');
        return false;
    }

    return true;
}

// 補齊表單資料
function prepareFormData() {
    const form = document.getElementById('bookingForm');
    
    // 補齊 couponCode (hidden input)
    const couponSelect = form.querySelector('select[name="couponCode"]');
    if (couponSelect) {
        let couponInput = form.querySelector('input[name="couponCode"]');
        if (!couponInput) {
            couponInput = document.createElement("input");
            couponInput.type = "hidden";
            couponInput.name = "couponCode";
            form.appendChild(couponInput);
        }
        couponInput.value = couponSelect.value;
    }

    // 補齊 discountAmount
    const discountInput = form.querySelector('input[name="discountAmount"]');
    if (discountInput) {
        discountInput.value = discountInput.value || 0;
    }

    // 補齊 roomAmount
    let totalRooms = 0;
    document.querySelectorAll('.room-amount-input').forEach(input => {
        const val = parseInt(input.value, 10);
        if (!isNaN(val)) totalRooms += val;
    });
    let roomAmountInput = form.querySelector('input[name="roomAmount"]');
    if (!roomAmountInput) {
        roomAmountInput = document.createElement("input");
        roomAmountInput.type = "hidden";
        roomAmountInput.name = "roomAmount";
        form.appendChild(roomAmountInput);
    }
    roomAmountInput.value = totalRooms;

    // 補齊 totalAmount
    const totalAmount = document.getElementById('totalAmountText').textContent.trim().replace(/[^0-9]/g, '');
    const totalAmountInput = form.querySelector('input[name="totalAmount"]');
    if (totalAmountInput) {
        totalAmountInput.value = totalAmount;
    }
}

// 處理 LINE Pay 付款
function handleLinePayment(formData) {
    const orderId = "ROOM" + Date.now() + window.memberId;
    const amount = formData.get('actualAmount');
    
    // 準備 LINE Pay 請求資料
    const linePayData = {
        linepayBody: {
            amount: parseInt(amount),
            currency: "TWD",
            orderId: orderId,
            packages: [{
                id: "room-package",
                amount: parseInt(amount),
                name: "房間訂房",
                products: [{
                    id: "room-001",
                    name: "房間預訂",
                    quantity: 1,
                    price: parseInt(amount)
                }]
            }],
            redirectUrls: {
                confirmUrl: `http://localhost:8080/api/confirmpayment/${orderId}/room`,
                cancelUrl: `http://localhost:8080/front-end/room?payment=cancelled`
            }
        },
        linepayOrder: {
            orderId: orderId,
            // 其他訂單資料可以在這裡補充
        }
    };

    // 先將訂單資料傳送到後端暫存
    fetch('/orderInfo/confirm', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('訂單準備失敗');
        }
        return response.text();
    })
    .then(result => {
        // 如果後端回傳成功，則呼叫 LINE Pay API
        return fetch('/api/linepay/room', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(linePayData)
        });
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === 'success') {
            // 跳轉到 LINE Pay 付款頁面
            window.location.href = data.data;
        } else {
            alert(data.message || 'LINE Pay 付款連線失敗');
        }
    })
    .catch(error => {
        console.error('LINE Pay 處理失敗:', error);
        alert('系統發生錯誤，請稍後再試');
    });
}

// 頁面載入完成後執行
window.addEventListener('DOMContentLoaded', function() {
    const payMethodSelect = document.querySelector('select[name="payMethod"]');
    const bookBtn = document.getElementById('bookBtn');

    // 預設按鈕 disabled
    bookBtn.disabled = true;

    // 監聽付款方式選擇
    payMethodSelect.addEventListener('change', function() {
        if (this.value === "0") {
            bookBtn.textContent = "確認預訂";
            bookBtn.disabled = false;
        } else if (this.value === "1") {
            bookBtn.textContent = "LINE Pay 付款";
            bookBtn.disabled = false;
        } else {
            bookBtn.textContent = "請選擇付款方式";
            bookBtn.disabled = true;
        }
    });

    console.log('會員ID:', window.memberId);
    
    // 查詢折價券
    queryCouponsForMember();
    
    // 設定折價券選擇事件
    handleCouponChange();
    
    // 初始化驗證碼
    refreshCaptcha();

    // 計算總房間數
    let totalRooms = 0;
    document.querySelectorAll('.room-amount-input').forEach(input => {
        const val = parseInt(input.value, 10);
        if (!isNaN(val)) totalRooms += val;
    });
    document.getElementById('totalRoomsDisplay').textContent = totalRooms;

    // 寫入 hidden input
    const roomAmountInput = document.getElementById('roomAmountInput');
    if (roomAmountInput) {
        roomAmountInput.value = totalRooms;
    }

    // 頁面載入時記錄原始實付金額
    const totalAmountSpan = document.getElementById('totalAmountText');
    if (totalAmountSpan) {
        window.originalActualAmount = parseInt(totalAmountSpan.textContent.replace(/[^0-9]/g, ''), 10) || 0;
    } else {
        window.originalActualAmount = 0;
    }

    // 顯示伺服器錯誤訊息
    var errorMsg = document.getElementById('serverErrorMsg');
    if (errorMsg && errorMsg.textContent.trim() !== "") {
        alert(errorMsg.textContent);
    }
});

// 動態更新實付金額
function updateActualAmount() {
    const totalAmountSpan = document.getElementById('totalAmountText');
    const discountInput = document.getElementById('discountAmount');
    let base = window.originalActualAmount || 0;
    let discount = 0;
    if (discountInput) {
        discount = parseInt(discountInput.value, 10) || 0;
    }
    const actual = Math.max(base - discount, 0);
    if (totalAmountSpan) {
        totalAmountSpan.textContent = actual;
    }
    // 同步 hidden input
    const actualAmountInput = document.querySelector('input[name="actualAmount"]');
    if (actualAmountInput) {
        actualAmountInput.value = actual;
    }
}

// 折價券選擇變更處理
function handleCouponChange() {
    const couponSelect = document.getElementById('couponSelect');
    const discountAmountInput = document.getElementById('discountAmount');
    if (!couponSelect || !discountAmountInput) return;
    couponSelect.addEventListener('change', function() {
        const selected = this.selectedOptions[0];
        const discount = selected ? selected.getAttribute('data-discount') : 0;
        discountAmountInput.value = discount || 0;
        updateActualAmount();
    });
    // 若 discountAmount 允許手動輸入，也要監聽 input
    discountAmountInput.addEventListener('input', updateActualAmount);
}

// 統一的表單提交處理
document.addEventListener('DOMContentLoaded', function() {
    const bookBtn = document.getElementById('bookBtn');
    
    bookBtn.addEventListener('click', function(e) {
        e.preventDefault();
        
        // 驗證表單
        if (!validateForm()) {
            return;
        }

        // 準備表單資料
        prepareFormData();

        // 獲取付款方式
        const payMethod = document.querySelector('select[name="payMethod"]').value;
        const form = document.getElementById('bookingForm');
        const formData = new FormData(form);

        if (payMethod === "0") {
            // 臨櫃付款 - 直接提交表單到後端處理
            console.log("處理臨櫃付款");
            
            fetch('/orderInfo/confirm', {
                method: 'POST',
                body: formData
            })
            .then(response => {
                if (response.redirected) {
                    // 如果後端重定向，則跳轉到重定向的頁面
                    window.location.href = response.url;
                } else {
                    return response.text();
                }
            })
            .then(html => {
                if (html) {
                    // 如果返回 HTML 內容，則替換當前頁面
                    document.open();
                    document.write(html);
                    document.close();
                }
            })
            .catch(error => {
                console.error('臨櫃付款處理失敗:', error);
                alert('系統發生錯誤，請稍後再試');
            });
            
        } else if (payMethod === "1") {
            // LINE Pay 付款
            console.log("處理 LINE Pay 付款");
            handleLinePayment(formData);
        }
    });
});