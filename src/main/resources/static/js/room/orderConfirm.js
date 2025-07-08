 // 列印功能優化
 window.addEventListener('beforeprint', function () {
    // 隱藏不需要列印的元素
    const elementsToHide = document.querySelectorAll('.btn, .confirm-actions, .payment-section button');
    elementsToHide.forEach(el => el.style.display = 'none');
});

window.addEventListener('afterprint', function () {
    // 恢復隱藏的元素
    const elementsToShow = document.querySelectorAll('.btn, .confirm-actions, .payment-section button');
    elementsToShow.forEach(el => el.style.display = '');
});