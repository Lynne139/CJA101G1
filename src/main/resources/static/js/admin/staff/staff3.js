// 權限編輯 Modal 處理
function openEditRightsModal(employeeId, employeeName) {
    document.getElementById('editRightsEmployeeId').value = employeeId;
    fetch(`/admin/employee/${employeeId}/rights-json`)
        .then(res => res.json())
        .then(data => {
            const allRights = data.allRights;
            const enabledRightIds = new Set(data.enabledRightIds);
            const checkboxesDiv = document.getElementById('rightsCheckboxes');
            checkboxesDiv.innerHTML = allRights.map(r =>
                `<div class='form-check'>
                    <input class='form-check-input' type='checkbox' name='rights' value='${r.accessId}' id='right_${r.accessId}' ${enabledRightIds.has(r.accessId) ? 'checked' : ''}>
                    <label class='form-check-label' for='right_${r.accessId}'>${r.accessName}</label>
                </div>`
            ).join('');
        });
    var modal = new bootstrap.Modal(document.getElementById('editRightsModal'));
    modal.show();
}

function openEditRightsModalFromBtn(btn) {
    const empId = btn.getAttribute('data-empid');
    const empName = btn.getAttribute('data-empname');
    openEditRightsModal(empId, empName);
}

document.getElementById('editRightsForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const employeeId = document.getElementById('editRightsEmployeeId').value;
    const rights = Array.from(document.querySelectorAll('#rightsCheckboxes input[name="rights"]:checked')).map(cb => cb.value);
    fetch(`/admin/employee/${employeeId}/edit-rights`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ rights })
    }).then(res => {
        if (res.redirected || res.ok) {
            bootstrap.Modal.getInstance(document.getElementById('editRightsModal')).hide();
            alert('權限已儲存！');
            // 可選：重新整理員工列表
        } else {
            alert('儲存失敗');
        }
    });
}); 