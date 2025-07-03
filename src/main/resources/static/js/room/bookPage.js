    // 驗證碼刷新功能
    function refreshCaptcha() {
        const captchaElement = document.querySelector('.captcha-code');
        const randomCode = Math.floor(1000 + Math.random() * 9000);
        captchaElement.textContent = randomCode;
    }

    // 表單提交處理
    document.getElementById('bookingForm').addEventListener('submit', function(e) {
        e.preventDefault();
        
        // 檢查必填欄位
        const requiredFields = this.querySelectorAll('[required]');
        let isValid = true;
        
        requiredFields.forEach(field => {
            if (!field.value.trim()) {
                isValid = false;
                field.classList.add('is-invalid');
            } else {
                field.classList.remove('is-invalid');
            }
        });
        
        // 檢查條款同意
        const terms1 = document.getElementById('terms1');
        const terms2 = document.getElementById('terms2');
        
        if (!terms1.checked || !terms2.checked) {
            isValid = false;
            alert('請同意服務條款後再進行預訂');
            return;
        }
        
        if (isValid) {
            // 模擬提交成功
            alert('預訂資料已送出，即將進入付款頁面...');
            // 這裡可以加入實際的表單提交邏輯
        } else {
            alert('請檢查並填寫所有必填欄位');
        }
    });

    // 移除無效樣式當用戶開始輸入
    document.querySelectorAll('.form-control, .form-select').forEach(field => {
        field.addEventListener('input', function() {
            this.classList.remove('is-invalid');
        });
    });
