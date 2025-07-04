document.addEventListener("DOMContentLoaded", function () {
    
    // 中文字符驗證函數
    function validateChinese(input, allowEmpty = false) {
        const chineseRegex = /^[\u4e00-\u9fff\s]*$/;
        const value = input.value.trim();
        
        if (!allowEmpty && value === '') {
            return false;
        }
        
        if (value !== '' && !chineseRegex.test(value)) {
            return false;
        }
        
        return true;
    }
    
    // 即時驗證函數
    function setupRealTimeValidation() {
        // 姓名驗證
        const nameInput = document.querySelector('input[name="name"]');
        if (nameInput) {
            nameInput.addEventListener('input', function() {
                const isValid = validateChinese(this, false);
                if (isValid) {
                    this.classList.remove('is-invalid');
                    this.classList.add('is-valid');
                } else {
                    this.classList.remove('is-valid');
                    this.classList.add('is-invalid');
                }
            });
        }
        
        // 密碼驗證
        const passwordInput = document.querySelector('input[name="password"]');
        if (passwordInput) {
            passwordInput.addEventListener('input', function() {
                const length = this.value.length;
                if (length >= 4 && length <= 12) {
                    this.classList.remove('is-invalid');
                    this.classList.add('is-valid');
                } else {
                    this.classList.remove('is-valid');
                    this.classList.add('is-invalid');
                }
            });
        }
        
        // 建立日期驗證
        if (createdDateInput) {
            createdDateInput.addEventListener('change', function() {
                const selectedDate = new Date(this.value);
                const today = new Date();
                today.setHours(0, 0, 0, 0);
                
                if (selectedDate >= today) {
                    this.classList.remove('is-invalid');
                    this.classList.add('is-valid');
                } else {
                    this.classList.remove('is-valid');
                    this.classList.add('is-invalid');
                }
            });
        }
        
        // 職稱名稱驗證
        const jobTitleNameInput = document.querySelector('input[name="jobTitleName"]');
        if (jobTitleNameInput) {
            jobTitleNameInput.addEventListener('input', function() {
                const isValid = validateChinese(this, false);
                if (isValid) {
                    this.classList.remove('is-invalid');
                    this.classList.add('is-valid');
                } else {
                    this.classList.remove('is-valid');
                    this.classList.add('is-invalid');
                }
            });
        }
        
        // 職稱說明驗證（移除中文限制）
        const jobTitleDescInput = document.querySelector('input[name="description"]');
        if (jobTitleDescInput) {
            jobTitleDescInput.addEventListener('input', function() {
                // 移除中文限制，只要有內容就顯示為有效
                if (this.value.trim() !== '' || this.value === '') {
                    this.classList.remove('is-invalid');
                    this.classList.add('is-valid');
                }
            });
        }
        
        // 部門名稱驗證
        const roleNameInput = document.querySelector('input[name="roleName"]');
        if (roleNameInput) {
            roleNameInput.addEventListener('input', function() {
                const isValid = validateChinese(this, false);
                if (isValid) {
                    this.classList.remove('is-invalid');
                    this.classList.add('is-valid');
                } else {
                    this.classList.remove('is-valid');
                    this.classList.add('is-invalid');
                }
            });
        }
        
        // 部門備註驗證（移除中文限制）
        const roleRemarkInput = document.querySelector('input[name="remark"]');
        if (roleRemarkInput) {
            roleRemarkInput.addEventListener('input', function() {
                // 移除中文限制，只要有內容就顯示為有效
                if (this.value.trim() !== '' || this.value === '') {
                    this.classList.remove('is-invalid');
                    this.classList.add('is-valid');
                }
            });
        }
    }
    
    // 執行即時驗證設置
    setupRealTimeValidation();

    // 載入部門/角色
    fetch("/employees/roles").then(res => res.json()).then(data => {
        const roleSelect = document.getElementById("roleSelect");
        if (roleSelect) {
            roleSelect.innerHTML = '<option value="">請選擇部門</option>' + 
                data.map(r => `<option value="${r.roleId}">${r.roleName}</option>`).join("");
        }
    });

    // 載入職稱
    fetch("/employees/job-titles").then(res => res.json()).then(data => {
        const jobTitleSelect = document.getElementById("jobTitleSelect");
        if (jobTitleSelect) {
            jobTitleSelect.innerHTML = '<option value="">請選擇職稱</option>' + 
                data.map(jt => `<option value="${jt.jobTitleId}">${jt.jobTitleName}</option>`).join("");
        }
    });

    // 僅保留權限管理（新增/編輯權限）功能
    const editAccessSelect = document.getElementById("editAccessSelect");
    if (editAccessSelect) {
        fetch("/employees/permission-map").then(res => res.json()).then(data => {
            editAccessSelect.innerHTML = Object.entries(data).map(([id, name]) => 
                `<option value="${id}">${name}</option>`
            ).join("");
        });
    }

    // 新增職稱
    const addJobTitleForm = document.getElementById("addJobTitleForm");
    if (addJobTitleForm) {
        addJobTitleForm.addEventListener("submit", function(e) {
            e.preventDefault();
            
            // 驗證表單
            const jobTitleNameInput = this.querySelector('input[name="jobTitleName"]');
            const descriptionInput = this.querySelector('input[name="description"]');
            
            let isValid = true;
            
            // 驗證職稱名稱
            if (!validateChinese(jobTitleNameInput, false)) {
                jobTitleNameInput.classList.add('is-invalid');
                isValid = false;
            }
            
            // 職稱說明無需驗證（移除中文限制）
            
            if (!isValid) {
                alert('請檢查輸入格式，職稱名稱只能包含中文字符');
                return;
            }
            
            const jobTitleName = this.jobTitleName.value;
            const description = this.description.value;
            fetch("/employees/job-titles", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ jobTitleName, description, isActive: true })
            }).then(res => {
                if (res.ok) {
                    showJobTitleSuccessModal();
                    this.reset();
                    // 重新載入職稱選單
                    fetch("/employees/job-titles").then(res => res.json()).then(data => {
                        const jobTitleSelect = document.getElementById("jobTitleSelect");
                        if (jobTitleSelect) {
                            jobTitleSelect.innerHTML = '<option value="">請選擇職稱</option>' + 
                                data.map(jt => `<option value="${jt.jobTitleId}">${jt.jobTitleName}</option>`).join("");
                        }
                    });
                } else {
                    alert('新增職稱失敗');
                }
            });
        });
    }

    // 新增部門
    const addRoleForm = document.getElementById("addRoleForm");
    if (addRoleForm) {
        addRoleForm.addEventListener("submit", function(e) {
            e.preventDefault();
            
            // 驗證表單
            const roleNameInput = this.querySelector('input[name="roleName"]');
            const remarkInput = this.querySelector('input[name="remark"]');
            
            let isValid = true;
            
            // 驗證部門名稱
            if (!validateChinese(roleNameInput, false)) {
                roleNameInput.classList.add('is-invalid');
                isValid = false;
            }
            
            // 備註無需驗證（移除中文限制）
            
            if (!isValid) {
                alert('請檢查輸入格式，部門名稱只能包含中文字符');
                return;
            }
            
            const roleName = this.roleName.value;
            const remark = this.remark.value;
            fetch("/employees/roles", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ roleName, remark, isActive: true })
            }).then(res => {
                if (res.ok) {
                    showRoleSuccessModal();
                    this.reset();
                    // 重新載入部門選單
                    fetch("/employees/roles").then(res => res.json()).then(data => {
                        const roleSelect = document.getElementById("roleSelect");
                        if (roleSelect) {
                            roleSelect.innerHTML = '<option value="">請選擇部門</option>' + 
                                data.map(r => `<option value="${r.roleId}">${r.roleName}</option>`).join("");
                        }
                    });
                } else {
                    alert('新增部門失敗');
                }
            });
        });
    }

    // 新增權限
    document.getElementById("addAccessRightForm")?.addEventListener("submit", function (e) {
        e.preventDefault();
        const accessName = this.accessName.value;
        fetch("/employees/access-rights", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ accessName })
        }).then(() => location.reload());
    });

    // 編輯權限
    document.getElementById("editAccessRightForm")?.addEventListener("submit", function (e) {
        e.preventDefault();
        const accessId = this.accessId.value;
        const newAccessName = this.newAccessName.value;
        fetch(`/employees/access-rights/${accessId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ accessId, accessName: newAccessName })
        }).then(() => location.reload());
    });

    // 攔截新增員工表單送出，成功時顯示 Bootstrap Modal
    const addEmployeeForm = document.getElementById("addEmployeeForm");
    if (addEmployeeForm) {
        addEmployeeForm.addEventListener("submit", function(e) {
            e.preventDefault();
            
            // 驗證表單
            const nameInput = this.querySelector('input[name="name"]');
            const passwordInput = this.querySelector('input[name="password"]');
            const createdDateInput = this.querySelector('input[name="createdDate"]');
            
            let isValid = true;
            
            // 驗證姓名
            if (!validateChinese(nameInput, false)) {
                nameInput.classList.add('is-invalid');
                isValid = false;
            }
            
            // 驗證密碼
            const passwordLength = passwordInput.value.length;
            if (passwordLength < 4 || passwordLength > 12) {
                passwordInput.classList.add('is-invalid');
                isValid = false;
            }
            
            // 驗證建立日期
            const selectedDate = new Date(createdDateInput.value);
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            if (selectedDate < today) {
                createdDateInput.classList.add('is-invalid');
                isValid = false;
            }
            
            if (!isValid) {
                alert('請檢查輸入格式：\n- 姓名只能包含中文字符\n- 密碼長度須為4-12個字符\n- 建立日期不能早於今日');
                return;
            }
            
            const formData = new FormData(this);
            fetch("/employees/form", {
                method: "POST",
                body: formData
            })
            .then(res => {
                if (res.redirected || res.ok) {
                    showSuccessModal();
                    this.reset();
                    // 清除驗證樣式
                    this.querySelectorAll('.is-valid, .is-invalid').forEach(input => {
                        input.classList.remove('is-valid', 'is-invalid');
                    });
                } else {
                    alert('新增失敗');
                }
            });
        });
    }
});

function showSuccessModal() {
    var modal = new bootstrap.Modal(document.getElementById('successModal'));
    modal.show();
}

function showJobTitleSuccessModal() {
    var modal = new bootstrap.Modal(document.getElementById('jobTitleSuccessModal'));
    modal.show();
}

function showRoleSuccessModal() {
    var modal = new bootstrap.Modal(document.getElementById('roleSuccessModal'));
    modal.show();
} 