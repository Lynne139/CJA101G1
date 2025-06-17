function goHome() {
  window.location.href = './homepage.html';
}

$(".roomtype-slider").slick({
            arrow:true, // 是否顯示左右箭頭
            dots:true, // 是否顯示圓點導航
            centerPadding:"60px", // 居中顯示時的邊距
            // responsive:[
            //     {
            //         breakpoint: 768, // 當視窗寬度小於768px時
            //         settings: {
            //             slidesToShow: 1, // 顯示1個圖片
            //             centerMode:false, // 不居中顯示
            //             centerPadding:"0px" // 無邊距
            //         }
            //     }
            // ]   
        });

// let roomImages = [
//   "./image/roomtype_1.png",
//   "./image/roomtype_2.png",
//   "./image/roomtype_3.png"
// ];
// let currentImage = 0;

// function updateRoomImage() {
//   document.getElementById("roomImage").src = roomImages[currentImage];
// }

// function prevRoomImage() {
//   currentImage = (currentImage - 1 + roomImages.length) % roomImages.length;
//   updateRoomImage();
// }

// function nextRoomImage() {
//   currentImage = (currentImage + 1) % roomImages.length;
//   updateRoomImage();
// }

// updateRoomImage(); // 初始化顯示第一張