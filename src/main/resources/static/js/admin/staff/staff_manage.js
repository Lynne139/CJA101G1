document.addEventListener("DOMContentLoaded", function () {
    // 載入部門/角色
    fetch("/employees/roles").then(res => res.json()).then(data => {
        const roleSelect = document.getElementById("roleSelect");
        if (roleSelect) {
            roleSelect.innerHTML = data.map(r => `<option value="${r.roleId}">${r.roleName}</option>`).join("");
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
    document.getElementById("addJobTitleForm")?.addEventListener("submit", function (e) {
        e.preventDefault();
        const jobTitleName = this.jobTitleName.value;
        const description = this.description.value;
        fetch("/employees/job-titles", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ jobTitleName, description, isActive: true })
        }).then(() => location.reload());
    });

    // 新增部門
    document.getElementById("addRoleForm")?.addEventListener("submit", function (e) {
        e.preventDefault();
        const roleName = this.roleName.value;
        const remark = this.remark.value;
        fetch("/employees/roles", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ roleName, remark, isActive: true })
        }).then(() => location.reload());
    });

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
}); 