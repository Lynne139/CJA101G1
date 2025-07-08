// 驗證碼刷新功能
function refreshCaptcha() {
    const captchaElement = document.querySelector('.captcha-code');
    const randomCode = Math.floor(1000 + Math.random() * 9000);
    captchaElement.textContent = randomCode;
    // 如果需要將驗證碼存到隱藏欄位或發送到後端，可以在這裡處理
    // 例如：document.getElementById('expectedCaptcha').value = randomCode;
}

// 表單提交處理
// 傳統表單提交方式（臨時解決方案）
// document.getElementById('bookingForm').addEventListener('submit', function(e) {
//     e.preventDefault();
    
//     // 檢查必填欄位
//     const requiredFields = this.querySelectorAll('[required]');
//     let isValid = true;
    
//     requiredFields.forEach(field => {
//         if (!field.value.trim()) {
//             isValid = false;
//             field.classList.add('is-invalid');
//         } else {
//             field.classList.remove('is-invalid');
//         }
//     });
    
//     // 檢查條款同意
//     const terms1 = document.getElementById('terms1');
//     const terms2 = document.getElementById('terms2');
    
//     if (!terms1.checked || !terms2.checked) {
//         isValid = false;
//         alert('請同意服務條款後再進行預訂');
//         return;
//     }
    
//     // 驗證碼檢查
//     const captchaInput = document.querySelector('.captcha-input');
//     const captchaCode = document.querySelector('.captcha-code');
    
//     if (captchaInput.value !== captchaCode.textContent) {
//         isValid = false;
//         captchaInput.classList.add('is-invalid');
//         alert('驗證碼不正確，請重新輸入');
//         return;
//     }
    
//     if (isValid) {
//         // 顯示彈窗，等使用者按下確定才送出
//         alert('表單已送出！');
//         this.submit(); // 這裡的 this 指 form
//     } else {
//         alert('請檢查並填寫所有必填欄位');
//     }
// });

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
                // 預設選擇第一個折價券
                couponSelect.selectedIndex = 1;
                discountAmountInput.value = coupons[0].discountValue || 0;
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

// 頁面載入完成後執行
window.addEventListener('DOMContentLoaded', function() {
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

    var errorMsg = document.getElementById('serverErrorMsg');
    if (errorMsg && errorMsg.textContent.trim() !== "") {
        alert(errorMsg.textContent);
    }
});

// 專為 bookPage.html 的「確認預訂」按鈕設計
// 參考 bookSingle.js 的寫法

document.getElementById('bookBtn').addEventListener('click', function(e) {
    e.preventDefault();
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
        return;
    }

    // 驗證碼
    const captchaInput = document.querySelector('.captcha-input');
    const captchaCode = document.querySelector('.captcha-code');
    if (captchaInput.value !== captchaCode.textContent) {
        isValid = false;
        captchaInput.classList.add('is-invalid');
        alert('驗證碼不正確，請重新輸入');
        return;
    }

    if (!isValid) {
        alert('請檢查並填寫所有必填欄位');
        return;
    }

    // ====== 動態補齊/修正 input ======
    // 1. 確保所有房型明細 input 都在 form 內（多房型已由 Thymeleaf 產生）
    // 2. 補齊付款方式（select）
    const payMethod = $("select[name='payMethod']").val();
    if (payMethod !== "0" && payMethod !== "1") {
        alert("請選擇付款方式");
        return;
    }

    // 3. 補齊 couponCode
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

    // 4. 補齊 discountAmount
    const discountInput = form.querySelector('input[name="discountAmount"]');
    if (discountInput) {
        discountInput.value = discountInput.value || 0;
    }

    // 5. 補齊 roomAmount
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

    // 6. 確保 method 為 POST
    form.method = "POST";
    form.action = "/orderInfo/confirm";

    // 7. 補齊 totalAmount
    const totalAmount = document.getElementById('totalAmountText').textContent.trim().replace(/[^0-9]/g, '');
    const totalAmountInput = form.querySelector('input[name="totalAmount"]');
    if (totalAmountInput) {
        totalAmountInput.value = totalAmount;
    }

    // 送出前
    console.log("送出總金額：", document.getElementById('totalAmountInput').value);

    form.submit();
});