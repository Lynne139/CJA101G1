// 取消按鈕所在的 <form> 提交前，先存 scroll 位置
  document.querySelectorAll('form[action$="/cancel"]').forEach(form => {
    form.addEventListener('submit', () => {
      sessionStorage.setItem('scrollPos', window.scrollY);
    });
  });

  window.addEventListener('DOMContentLoaded', () => {
    // 讀 scrollPos，如果有就滾回去
    const y = sessionStorage.getItem('scrollPos');
    if (y !== null) {
      window.scrollTo(0, parseInt(y, 10));
      sessionStorage.removeItem('scrollPos');
    }

    // 如果後端透過 flash attribute 標了 cancelSuccess，就顯示 toast
    const cancelSuccess = /*[[${cancelSuccess}]]*/ false;
    if (cancelSuccess) {
      const toastEl = document.getElementById('cancelToast');
      const toast = new bootstrap.Toast(toastEl);
      toast.show();
    }
  });