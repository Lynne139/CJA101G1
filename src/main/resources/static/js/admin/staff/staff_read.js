// 中文字符驗證函數
function validateChinese(value, allowEmpty = false) {
    const chineseRegex = /^[\u4e00-\u9fff\s]*$/;
    const trimmedValue = value ? value.trim() : '';
    
    if (!allowEmpty && trimmedValue === '') {
        return false;
    }
    
    if (trimmedValue !== '' && !chineseRegex.test(trimmedValue)) {
        return false;
    }
    
    return true;
}

// 載入員工資料
function loadEmployees(searchTerm = '') {
    fetch("/employees/with-details")
        .then(res => {
            if (!res.ok) {
                throw new Error(`HTTP error! status: ${res.status}`);
            }
            return res.json();
        })
        .then(data => {
            // 如果有搜尋條件，進行前端篩選
            if (searchTerm) {
                data = data.filter(employee => 
                    employee.name && employee.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                    (employee.roleName && employee.roleName.toLowerCase().includes(searchTerm.toLowerCase())) ||
                    (employee.jobTitleName && employee.jobTitleName.toLowerCase().includes(searchTerm.toLowerCase()))
                );
            }

            const resultsDiv = document.getElementById("queryResults");
            let html = `
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">員工列表 ${searchTerm ? `(搜尋: ${searchTerm})` : ''}</h5>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>照片</th>
                                        <th>ID</th>
                                        <th>姓名</th>
                                        <th>部門</th>
                                        <th>職稱</th>
                                        <th>狀態</th>
                                        <th>建立日期</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
            `;
            
            if (data && data.length > 0) {
                data.forEach(employee => {
                    const statusText = employee.status ? '啟用' : '停用';
                    const statusClass = employee.status ? 'text-success' : 'text-danger';
                    const roleName = employee.roleName || '未設定';
                    const jobTitleName = employee.jobTitleName || '未設定';
                    
                    // 照片顯示
                    const photoUrl = employee.employeePhoto ? `/employees/${employee.employeeId}/photo` : '/images/admin/no_img.svg';
                    const photoHtml = `<img src="${photoUrl}" alt="員工照片" class="rounded-circle" width="50" height="50" style="object-fit: cover;">`;
                    
                    html += `
                        <tr>
                            <td>${photoHtml}</td>
                            <td>${employee.employeeId || ''}</td>
                            <td>${employee.name || ''}</td>
                            <td>${roleName}</td>
                            <td>${jobTitleName}</td>
                            <td><span class="${statusClass}">${statusText}</span></td>
                            <td>${formatDate(employee.createdDate)}</td>
                            <td>
                                <button class="btn btn-sm btn-warning" onclick="editEmployee(${employee.employeeId}, '${employee.name}', ${employee.roleId}, ${employee.jobTitleId}, ${employee.status})">
                                    <i class="fas fa-edit"></i> 編輯
                                </button>
                            </td>
                        </tr>
                    `;
                });
            } else {
                html += '<tr><td colspan="8" class="text-center">暫無員工資料</td></tr>';
            }
            
            html += `
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            `;
            
            resultsDiv.innerHTML = html;
        })
        .catch(error => {
            console.error('載入員工資料失敗:', error);
            document.getElementById("queryResults").innerHTML = `<div class="alert alert-danger">載入員工資料失敗: ${error.message}</div>`;
        });
}

// 載入部門資料
function loadRoles(searchTerm = '') {
    fetch("/employees/roles")
        .then(res => res.json())
        .then(data => {
            // 如果有搜尋條件，進行前端篩選
            if (searchTerm) {
                data = data.filter(role => 
                    role.roleName && role.roleName.toLowerCase().includes(searchTerm.toLowerCase()) ||
                    (role.remark && role.remark.toLowerCase().includes(searchTerm.toLowerCase()))
                );
            }

            const resultsDiv = document.getElementById("queryResults");
            let html = `
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">部門列表 ${searchTerm ? `(搜尋: ${searchTerm})` : ''}</h5>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>部門名稱</th>
                                        <th>備註</th>
                                        <th>狀態</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
            `;
            
            if (data && data.length > 0) {
                data.forEach(role => {
                    const statusText = role.isActive ? '啟用' : '停用';
                    const statusClass = role.isActive ? 'text-success' : 'text-danger';
                    html += `
                        <tr>
                            <td>${role.roleId}</td>
                            <td>${role.roleName}</td>
                            <td>${role.remark || '-'}</td>
                            <td><span class="${statusClass}">${statusText}</span></td>
                            <td>
                                <button class="btn btn-sm btn-warning" onclick="editRole(${role.roleId}, '${role.roleName}', '${role.remark || ''}')">
                                    <i class="fas fa-edit"></i> 編輯
                                </button>
                            </td>
                        </tr>
                    `;
                });
            } else {
                html += '<tr><td colspan="5" class="text-center">暫無部門資料</td></tr>';
            }
            
            html += `
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            `;
            
            resultsDiv.innerHTML = html;
        })
        .catch(error => {
            console.error('載入部門資料失敗:', error);
            document.getElementById("queryResults").innerHTML = '<div class="alert alert-danger">載入部門資料失敗</div>';
        });
}

// 載入職稱資料
function loadJobTitles(searchTerm = '') {
    fetch("/employees/job-titles")
        .then(res => res.json())
        .then(data => {
            // 如果有搜尋條件，進行前端篩選
            if (searchTerm) {
                data = data.filter(jobTitle => 
                    jobTitle.jobTitleName && jobTitle.jobTitleName.toLowerCase().includes(searchTerm.toLowerCase()) ||
                    (jobTitle.description && jobTitle.description.toLowerCase().includes(searchTerm.toLowerCase()))
                );
            }

            const resultsDiv = document.getElementById("queryResults");
            let html = `
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">職稱列表 ${searchTerm ? `(搜尋: ${searchTerm})` : ''}</h5>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-striped">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>職稱名稱</th>
                                        <th>職稱說明</th>
                                        <th>狀態</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
            `;
            
            if (data && data.length > 0) {
                data.forEach(jobTitle => {
                    const statusText = jobTitle.isActive ? '啟用' : '停用';
                    const statusClass = jobTitle.isActive ? 'text-success' : 'text-danger';
                    html += `
                        <tr>
                            <td>${jobTitle.jobTitleId}</td>
                            <td>${jobTitle.jobTitleName}</td>
                            <td>${jobTitle.description || '-'}</td>
                            <td><span class="${statusClass}">${statusText}</span></td>
                            <td>
                                <button class="btn btn-sm btn-warning" onclick="editJobTitle(${jobTitle.jobTitleId}, '${jobTitle.jobTitleName}', '${jobTitle.description || ''}')">
                                    <i class="fas fa-edit"></i> 編輯
                                </button>
                            </td>
                        </tr>
                    `;
                });
            } else {
                html += '<tr><td colspan="5" class="text-center">暫無職稱資料</td></tr>';
            }
            
            html += `
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            `;
            
            resultsDiv.innerHTML = html;
        })
        .catch(error => {
            console.error('載入職稱資料失敗:', error);
            document.getElementById("queryResults").innerHTML = '<div class="alert alert-danger">載入職稱資料失敗</div>';
        });
}

// 編輯員工
function editEmployee(employeeId, name, roleId, jobTitleId, status) {
    // 載入部門選項
    fetch("/employees/roles")
        .then(res => res.json())
        .then(roles => {
            const roleSelect = document.getElementById("editEmployeeRoleId");
            roleSelect.innerHTML = '<option value="">請選擇部門</option>';
            roles.forEach(role => {
                const selected = (roleId && role.roleId === roleId) ? 'selected' : '';
                roleSelect.innerHTML += `<option value="${role.roleId}" ${selected}>${role.roleName}</option>`;
            });
        });

    // 載入職稱選項
    fetch("/employees/job-titles")
        .then(res => res.json())
        .then(jobTitles => {
            const jobTitleSelect = document.getElementById("editEmployeeJobTitleId");
            jobTitleSelect.innerHTML = '<option value="">請選擇職稱</option>';
            jobTitles.forEach(jobTitle => {
                const selected = (jobTitleId && jobTitle.jobTitleId === jobTitleId) ? 'selected' : '';
                jobTitleSelect.innerHTML += `<option value="${jobTitle.jobTitleId}" ${selected}>${jobTitle.jobTitleName}</option>`;
            });
        });

    // 設定表單值
    document.getElementById("editEmployeeId").value = employeeId;
    document.getElementById("editEmployeeName").value = name;
    document.getElementById("editEmployeeStatus").value = status.toString();
    
    // 載入現有照片
    const photoPreview = document.getElementById("editEmployeePhotoPreview");
    if (photoPreview) {
        // 先嘗試載入員工照片
        const photoUrl = `/employees/${employeeId}/photo`;
        photoPreview.src = photoUrl;
        photoPreview.onerror = function() {
            // 如果載入失敗，顯示預設圖片
            this.src = '/images/admin/no_img.svg';
        };
        // 設置點擊事件
        photoPreview.onclick = function() {
            previewEmployeePhoto(this.src);
        };
    }
    
    // 顯示編輯模態框
    const modal = new bootstrap.Modal(document.getElementById('editEmployeeModal'));
    modal.show();
}

// 照片預覽功能
function previewEditEmployeePhoto(input) {
    if (input.files && input.files[0]) {
        const file = input.files[0];
        
        // 檢查檔案大小 (10MB)
        if (file.size > 10 * 1024 * 1024) {
            alert('檔案大小不能超過 10MB');
            input.value = '';
            return;
        }
        
        // 檢查檔案類型
        if (!file.type.startsWith('image/')) {
            alert('只能上傳圖片檔案');
            input.value = '';
            return;
        }
        
        const reader = new FileReader();
        reader.onload = function(e) {
            const previewImg = document.getElementById('editEmployeePhotoPreview');
            previewImg.src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
}

// 儲存員工編輯
function saveEmployeeEdit() {
    const employeeId = document.getElementById("editEmployeeId").value;
    const name = document.getElementById("editEmployeeName").value;
    const roleId = document.getElementById("editEmployeeRoleId").value;
    const jobTitleId = document.getElementById("editEmployeeJobTitleId").value;
    const status = document.getElementById("editEmployeeStatus").value === 'true';
    const photoFile = document.getElementById("editEmployeePhoto").files[0];

    // 驗證姓名
    if (!validateChinese(name, false)) {
        alert('姓名只能包含中文字符且不能為空');
        document.getElementById("editEmployeeName").classList.add('is-invalid');
        return;
    }

    const employeeData = {
        name: name,
        roleId: roleId && roleId.trim() !== '' ? parseInt(roleId) : null,
        jobTitleId: jobTitleId && jobTitleId.trim() !== '' ? parseInt(jobTitleId) : null,
        status: status
    };

    // 先更新員工基本資料
    fetch(`/employees/${employeeId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(employeeData)
    })
    .then(res => {
        if (!res.ok) {
            return res.text().then(text => {
                throw new Error(`更新員工資料失敗: ${res.status} - ${text}`);
            });
        }
        return res.text(); // 現在返回字串
    })
    .then(message => {
        console.log('員工資料更新:', message);
        // 如果有選擇新照片，則上傳照片
        if (photoFile) {
            const formData = new FormData();
            formData.append('photo', photoFile);
            
            return fetch(`/employees/${employeeId}/photo`, {
                method: 'POST',
                body: formData
            });
        } else {
            return Promise.resolve();
        }
    })
    .then(res => {
        if (res && !res.ok) {
            return res.text().then(text => {
                throw new Error(`照片上傳失敗: ${res.status} - ${text}`);
            });
        }
        alert('員工更新成功！');
        loadEmployees(); // 重新載入員工列表
        const modal = bootstrap.Modal.getInstance(document.getElementById('editEmployeeModal'));
        modal.hide();
    })
    .catch(error => {
        console.error('更新員工失敗:', error);
        alert('更新員工失敗: ' + error.message);
    });
}

// 編輯部門
function editRole(roleId, roleName, remark) {
    document.getElementById("editRoleId").value = roleId;
    document.getElementById("editRoleName").value = roleName;
    document.getElementById("editRoleRemark").value = remark;
    
    const modal = new bootstrap.Modal(document.getElementById('editRoleModal'));
    modal.show();
}

// 儲存部門編輯
function saveRoleEdit() {
    const roleId = document.getElementById("editRoleId").value;
    const roleName = document.getElementById("editRoleName").value;
    const remark = document.getElementById("editRoleRemark").value;

    const roleData = {
        roleName: roleName,
        remark: remark
    };

    fetch(`/employees/roles/${roleId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(roleData)
    })
    .then(res => {
        if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
    })
    .then(data => {
        alert('部門更新成功！');
        loadRoles(); // 重新載入部門列表
        const modal = bootstrap.Modal.getInstance(document.getElementById('editRoleModal'));
        modal.hide();
    })
    .catch(error => {
        console.error('更新部門失敗:', error);
        alert('更新部門失敗: ' + error.message);
    });
}

// 編輯職稱
function editJobTitle(jobTitleId, jobTitleName, description) {
    document.getElementById("editJobTitleId").value = jobTitleId;
    document.getElementById("editJobTitleName").value = jobTitleName;
    document.getElementById("editJobTitleDescription").value = description;
    
    const modal = new bootstrap.Modal(document.getElementById('editJobTitleModal'));
    modal.show();
}

// 儲存職稱編輯
function saveJobTitleEdit() {
    const jobTitleId = document.getElementById("editJobTitleId").value;
    const jobTitleName = document.getElementById("editJobTitleName").value;
    const description = document.getElementById("editJobTitleDescription").value;

    const jobTitleData = {
        jobTitleName: jobTitleName,
        description: description
    };

    fetch(`/employees/job-titles/${jobTitleId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(jobTitleData)
    })
    .then(res => {
        if (!res.ok) {
            throw new Error(`HTTP error! status: ${res.status}`);
        }
        return res.json();
    })
    .then(data => {
        alert('職稱更新成功！');
        loadJobTitles(); // 重新載入職稱列表
        const modal = bootstrap.Modal.getInstance(document.getElementById('editJobTitleModal'));
        modal.hide();
    })
    .catch(error => {
        console.error('更新職稱失敗:', error);
        alert('更新職稱失敗: ' + error.message);
    });
}

// 格式化日期
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-TW');
}

// 照片預覽功能
function previewEmployeePhoto(photoUrl) {
    const previewImage = document.getElementById('photoPreviewImage');
    previewImage.src = photoUrl;
    
    // 如果照片載入失敗，顯示預設圖片
    previewImage.onerror = function() {
        this.src = '/images/admin/no_img.svg';
    };
    
    // 顯示預覽 Modal
    const modal = new bootstrap.Modal(document.getElementById('photoPreviewModal'));
    modal.show();
}

// 搜尋員工
function searchEmployees() {
    const searchTerm = document.getElementById('employeeSearchInput').value.trim();
    loadEmployees(searchTerm);
}

// 搜尋部門
function searchRoles() {
    const searchTerm = document.getElementById('roleSearchInput').value.trim();
    loadRoles(searchTerm);
}

// 搜尋職稱
function searchJobTitles() {
    const searchTerm = document.getElementById('jobTitleSearchInput').value.trim();
    loadJobTitles(searchTerm);
} 