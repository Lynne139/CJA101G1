function goHome() {
    window.location.href = './homepage.html';
  }

  $(".slider").slick({
            arrow:true, // 是否顯示左右箭頭
            dots:true, // 是否顯示圓點導航
            centerMode:true, // 是否居中顯示
            centerPadding:"60px", // 居中顯示時的邊距
            autoplay:true, // 是否自動播放
            autoplaySpeed:2000, // 自動播放的速度
            speed:500, // 切換圖片的速度
            fade:true, // 是否淡入淡出切換
            slidesToShow: 1,            
            responsive:[
                {
                    breakpoint: 768, // 當視窗寬度小於768px時
                    settings: {
                        slidesToShow: 1, // 顯示1個圖片
                        centerMode:false, // 不居中顯示
                        centerPadding:"0px" // 無邊距
                    }
                }
            ]   
        });
  
  // let banners = [
  //   "./image/roomtype_1.png",
  // "./image/roomtype_2.png",
  // "./image/roomtype_3.png"
  // ];
  
  // let currentBanner = 0;
  
  // function updateBanner() {
  //   document.getElementById("bannerImage").src = banners[currentBanner];
  // }
  
  // function prevSlide() {
  //   currentBanner = (currentBanner - 1 + banners.length) % banners.length;
  //   updateBanner();
  // }
  
  // function nextSlide() {
  //   currentBanner = (currentBanner + 1) % banners.length;
  //   updateBanner();
  // }
  
 // // 自動輪播（可選）
  // setInterval(nextSlide, 5000); // 每 5 秒換一張
  
  // updateBanner(); // 初始顯示

  function toggleContent(type) {
    const display = document.getElementById("contentDisplay");
    if (type === "news") {
      display.innerHTML = "<h2>消息標題</h2><p>這裡是最新消息的內容。</p>";
    } else if (type === "media") {
      display.innerHTML = "<h2>媒體報導</h2><p>這裡是媒體報導的內容。</p>";
    }
  }
  
 
  