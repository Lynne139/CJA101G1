document.addEventListener("DOMContentLoaded", function () {
//	console.log("成功頁 JS 已執行");
	  history.pushState(null, '', location.href);
	  window.addEventListener('popstate', function () {
//	    console.log("偵測到使用者按了返回");
	    history.pushState(null, '', location.href);
	    location.replace('/restaurants');
	  });
});
