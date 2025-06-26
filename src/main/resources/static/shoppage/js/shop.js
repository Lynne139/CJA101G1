// 商城 JavaScript 功能

// 全局變數
let cart = JSON.parse(localStorage.getItem('shopCart')) || [];
let currentMemberId = null; // 當前會員ID
let availableCoupons = []; // 可用的折價券列表
let selectedCoupon = null; // 選擇的折價券

// 購物車數據
let products = [
  {
    id: 1,
    name: '嶼蔻特製蜂蜜',
    price: 350,
    category: 'food',
    description: '精選在地野生蜂蜜，純天然無添加',
    image: '/images/shop/product1.jpg'
  },
  {
    id: 2,
    name: '手工編織包',
    price: 1200,
    category: 'craft',
    description: '當地藝術家手工製作，獨特設計',
    image: '/images/shop/product2.jpg'
  },
  {
    id: 3,
    name: '海藻保濕面膜',
    price: 680,
    category: 'beauty',
    description: '富含海洋精華，深層保濕修護',
    image: '/images/shop/product3.jpg'
  },
  {
    id: 4,
    name: '海島香氛蠟燭',
    price: 850,
    category: 'home',
    description: '天然椰子蠟，海風清新香調',
    image: '/images/shop/product4.jpg'
  },
  {
    id: 5,
    name: '手工椰子餅乾',
    price: 420,
    category: 'food',
    description: '新鮮椰子製作，香脆可口',
    image: '/images/shop/product5.jpg'
  },
  {
    id: 6,
    name: '貝殼珍珠項鍊',
    price: 1500,
    category: 'craft',
    description: '天然貝殼與珍珠，優雅設計',
    image: '/images/shop/product6.jpg'
  }
];

// DOM 載入完成後初始化
document.addEventListener('DOMContentLoaded', function() {
  initializeShop();
});

// 初始化商城功能
function initializeShop() {
  // 初始化會員ID
  initializeMemberId();
  
  // 初始化購物車
  updateCartDisplay();
  
  // 載入可用的折價券
  loadAvailableCoupons();
  
  // 綁定事件監聽器
  bindEventListeners();
  
  // 初始化篩選功能
  initializeFilters();
}

// 初始化會員ID
function initializeMemberId() {
  const memberIdElement = document.querySelector('input[name="memberId"]');
  if (memberIdElement && memberIdElement.value) {
    currentMemberId = memberIdElement.value;
  }
}

// 綁定事件監聽器
function bindEventListeners() {
  // 購物車側邊欄
  const cartToggle = document.getElementById('cartToggle');
  const cartSidebar = document.getElementById('cartSidebar');
  const closeCart = document.getElementById('closeCart');
  
  if (cartToggle) {
    cartToggle.addEventListener('click', function(e) {
      e.preventDefault();
      cartSidebar.classList.add('active');
    });
  }
  
  if (closeCart) {
    closeCart.addEventListener('click', function() {
      cartSidebar.classList.remove('active');
    });
  }
  
  // 點擊側邊欄外部關閉
  document.addEventListener('click', function(e) {
    if (cartSidebar && cartSidebar.classList.contains('active')) {
      if (!cartSidebar.contains(e.target) && !cartToggle.contains(e.target)) {
        cartSidebar.classList.remove('active');
      }
    }
  });
  
  // 加入購物車按鈕
  document.addEventListener('click', function(e) {
    if (e.target.classList.contains('add-to-cart-btn')) {
      const productId = parseInt(e.target.dataset.productId);
      const productName = e.target.dataset.productName;
      const productPrice = parseInt(e.target.dataset.productPrice);
      
      addToCart(productId, productName, productPrice);
    }
  });
  
  // 快速查看按鈕
  document.addEventListener('click', function(e) {
    if (e.target.classList.contains('quick-view-btn')) {
      const productId = parseInt(e.target.dataset.productId);
      showProductModal(productId);
    }
  });
  
  // 搜尋功能
  const searchInput = document.getElementById('searchInput');
  if (searchInput) {
    searchInput.addEventListener('input', function() {
      filterProducts();
    });
  }
  
  // 結帳按鈕
  const checkoutBtn = document.getElementById('checkoutBtn');
  if (checkoutBtn) {
    checkoutBtn.addEventListener('click', function() {
      checkout();
    });
  }
  
  // 折價券選擇
  const couponSelect = document.getElementById('couponSelect');
  if (couponSelect) {
    couponSelect.addEventListener('change', function() {
      handleCouponSelection();
    });
  }
  
  // 選單功能
  const menuToggle = document.getElementById('menuToggle');
  const overlayMenu = document.getElementById('overlayMenu');
  const menuClose = document.getElementById('menuClose');
  
  if (menuToggle) {
    menuToggle.addEventListener('click', function(e) {
      e.preventDefault();
      overlayMenu.classList.add('active');
    });
  }
  
  if (menuClose) {
    menuClose.addEventListener('click', function() {
      overlayMenu.classList.remove('active');
    });
  }
}

// 初始化篩選功能
function initializeFilters() {
  const categoryFilter = document.getElementById('categoryFilter');
  const priceFilter = document.getElementById('priceFilter');
  const sortFilter = document.getElementById('sortFilter');
  
  if (categoryFilter) {
    categoryFilter.addEventListener('change', filterProducts);
  }
  
  if (priceFilter) {
    priceFilter.addEventListener('change', filterProducts);
  }
  
  if (sortFilter) {
    sortFilter.addEventListener('change', filterProducts);
  }
}

// 篩選商品
function filterProducts() {
  const searchTerm = document.getElementById('searchInput').value.toLowerCase();
  const categoryFilter = document.getElementById('categoryFilter').value;
  const priceFilter = document.getElementById('priceFilter').value;
  const sortFilter = document.getElementById('sortFilter').value;
  
  const productCards = document.querySelectorAll('.product-card');
  
  productCards.forEach(card => {
    const title = card.querySelector('.product-title').textContent.toLowerCase();
    const category = card.dataset.category;
    const price = parseInt(card.dataset.price);
    
    let show = true;
    
    // 搜尋篩選
    if (searchTerm && !title.includes(searchTerm)) {
      show = false;
    }
    
    // 分類篩選
    if (categoryFilter && category !== categoryFilter) {
      show = false;
    }
    
    // 價格篩選
    if (priceFilter) {
      const [min, max] = priceFilter.split('-').map(p => p === '+' ? Infinity : parseInt(p));
      if (price < min || (max !== Infinity && price > max)) {
        show = false;
      }
    }
    
    // 顯示或隱藏商品卡片
    card.style.display = show ? 'block' : 'none';
  });
  
  // 排序商品
  sortProducts(sortFilter);
}

// 排序商品
function sortProducts(sortType) {
  const productsGrid = document.getElementById('productsGrid');
  const productCards = Array.from(productsGrid.children);
  
  productCards.sort((a, b) => {
    const priceA = parseInt(a.dataset.price);
    const priceB = parseInt(b.dataset.price);
    const nameA = a.querySelector('.product-title').textContent;
    const nameB = b.querySelector('.product-title').textContent;
    
    switch (sortType) {
      case 'price-low':
        return priceA - priceB;
      case 'price-high':
        return priceB - priceA;
      case 'name':
        return nameA.localeCompare(nameB);
      case 'popular':
        // 這裡可以根據實際需求實現熱門排序
        return 0;
      default:
        return 0;
    }
  });
  
  // 重新排列商品卡片
  productCards.forEach(card => {
    productsGrid.appendChild(card);
  });
}

// 加入購物車
function addToCart(productId, productName, productPrice) {
  const existingItem = cart.find(item => item.id === productId);
  
  if (existingItem) {
    existingItem.quantity += 1;
  } else {
    cart.push({
      id: productId,
      name: productName,
      price: productPrice,
      quantity: 1
    });
  }
  
  // 保存到本地存儲
  localStorage.setItem('shopCart', JSON.stringify(cart));
  
  // 更新購物車顯示
  updateCartDisplay();
  
  // 顯示成功訊息
  showNotification('商品已加入購物車！', 'success');
}

// 更新購物車顯示
function updateCartDisplay() {
  const cartItems = document.getElementById('cartItems');
  const cartTotal = document.getElementById('cartTotal');
  const cartIcon = document.querySelector('.cart-icon');
  
  if (cartItems) {
    if (cart.length === 0) {
      cartItems.innerHTML = `
        <div class="cart-empty">
          <i class="fas fa-shopping-cart"></i>
          <p>購物車是空的</p>
        </div>
      `;
    } else {
      cartItems.innerHTML = cart.map(item => `
        <div class="cart-item">
          <div class="cart-item-image">
            <img src="/images/shop/product${item.id}.jpg" alt="${item.name}" onerror="this.src='/images/admin/no_img.svg'">
          </div>
          <div class="cart-item-info">
            <div class="cart-item-title">${item.name}</div>
            <div class="cart-item-price">NT$ ${item.price}</div>
            <div class="cart-item-quantity">
              <button class="quantity-btn" onclick="updateQuantity(${item.id}, -1)">-</button>
              <span>${item.quantity}</span>
              <button class="quantity-btn" onclick="updateQuantity(${item.id}, 1)">+</button>
            </div>
          </div>
          <button class="cart-item-remove" onclick="removeFromCart(${item.id})">
            <i class="fas fa-trash"></i>
          </button>
        </div>
      `).join('');
    }
  }
  
  // 更新總計
  const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  if (cartTotal) {
    cartTotal.textContent = `NT$ ${total.toLocaleString()}`;
  }
  
  // 更新購物車圖標
  if (cartIcon) {
    const itemCount = cart.reduce((sum, item) => sum + item.quantity, 0);
    cartIcon.setAttribute('data-count', itemCount);
    cartIcon.classList.toggle('has-items', itemCount > 0);
  }
}

// 更新商品數量
function updateQuantity(productId, change) {
  const item = cart.find(item => item.id === productId);
  
  if (item) {
    item.quantity += change;
    
    if (item.quantity <= 0) {
      removeFromCart(productId);
    } else {
      localStorage.setItem('shopCart', JSON.stringify(cart));
      updateCartDisplay();
    }
  }
}

// 從購物車移除商品
function removeFromCart(productId) {
  cart = cart.filter(item => item.id !== productId);
  localStorage.setItem('shopCart', JSON.stringify(cart));
  updateCartDisplay();
  showNotification('商品已從購物車移除', 'info');
}

// 顯示商品詳情 Modal
function showProductModal(productId) {
  // 從頁面中獲取商品資訊
  const productCard = document.querySelector(`[data-product-id="${productId}"]`);
  
  if (productCard) {
    const productName = productCard.querySelector('.product-title').textContent;
    const productDescription = productCard.querySelector('.product-description').textContent;
    const productPrice = productCard.querySelector('.product-price').textContent;
    const productImage = productCard.querySelector('.product-image img').src;
    
    const modalTitle = document.getElementById('productModalTitle');
    const modalBody = document.getElementById('productModalBody');
    
    if (modalTitle) {
      modalTitle.textContent = productName;
    }
    
    if (modalBody) {
      modalBody.innerHTML = `
        <div class="row">
          <div class="col-md-6">
            <img src="${productImage}" alt="${productName}" class="img-fluid rounded" onerror="this.src='/images/admin/no_img.svg'">
          </div>
          <div class="col-md-6">
            <h4 class="text-brown">${productName}</h4>
            <p class="text-muted">${productDescription}</p>
            <div class="h3 text-brown mb-3">${productPrice}</div>
            <button class="btn btn-primary add-to-cart-btn" 
                    data-product-id="${productId}" 
                    data-product-name="${productName}" 
                    data-product-price="${productCard.dataset.price}">
              <i class="fas fa-shopping-cart"></i> 加入購物車
            </button>
          </div>
        </div>
      `;
    }
    
    // 顯示 Modal
    const modal = new bootstrap.Modal(document.getElementById('productModal'));
    modal.show();
  } else {
    showNotification('找不到商品資訊', 'error');
  }
}

// 結帳功能
function checkout() {
  // 檢查會員是否登入
  const memberId = getMemberIdFromSession();
  if (!memberId) {
    showNotification('請先登入會員', 'error');
    return;
  }
  
  // 檢查購物車是否有商品
  const cartItems = document.querySelectorAll('#cartItems .cart-item');
  if (cartItems.length === 0) {
    showNotification('購物車是空的', 'warning');
    return;
  }
  
  // 檢查折價券使用條件
  if (selectedCoupon) {
    const subtotalElement = document.getElementById('subtotal');
    const subtotalText = subtotalElement.textContent;
    const subtotal = parseInt(subtotalText.replace(/[^0-9]/g, '')) || 0;
    
    if (selectedCoupon.minPurchase > 0 && subtotal < selectedCoupon.minPurchase) {
      showNotification(`訂單金額未達折價券最低消費 NT$ ${selectedCoupon.minPurchase}`, 'error');
      return;
    }
  }
  
  // 獲取付款方式
  const paymentMethod = document.getElementById('paymentMethod').value === 'true';
  
  // 顯示結帳確認對話框
  const paymentText = paymentMethod ? 'LINE Pay' : '現金';
  const confirmCheckout = confirm(`確定要使用${paymentText}結帳嗎？`);
  if (!confirmCheckout) {
    return;
  }
  
  if (paymentMethod) {
    // 使用 LINE Pay 結帳
    checkoutWithLinePay(memberId);
  } else {
    // 使用現金結帳（原本的流程）
    checkoutWithCash(memberId);
  }
}

// LINE Pay 結帳
function checkoutWithLinePay(memberId) {
  // 準備 LINE Pay 結帳數據
  const cartTotalElement = document.getElementById('cartTotal');
  const cartTotalText = cartTotalElement.textContent;
  const amount = parseInt(cartTotalText.replace(/[^0-9]/g, '')) || 0;
  
  // 生成訂單編號
  const orderId = 'SHOP_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
  
  const linePayData = {
    linepayBody: {
      amount: amount,
      currency: "TWD",
      orderId: orderId,
      packages: [
        {
          id: "package-1",
          amount: amount,
          products: [
            {
              name: "嶼蔻商城商品",
              quantity: 1,
              price: amount
            }
          ]
        }
      ],
      redirectUrls: {
        confirmUrl: "http://localhost:8080/api/confirmpayment/" + orderId + "/false",
        cancelUrl: "http://localhost:8080/front-end/shop"
      }
    },
    linepayOrder: {
      memberId: parseInt(memberId),
      couponCode: selectedCoupon ? selectedCoupon.couponCode : null,
      paymentMethod: true,
      orderId: orderId,
      amount: amount
    }
  };
  
  // 發送到 LINE Pay API
  $.ajax({
    url: '/api/linepay/false',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(linePayData),
    success: function(response) {
      if (response.status === 'success' && response.data) {
        // 跳轉到 LINE Pay 付款頁面
        window.location.href = response.data;
      } else {
        showNotification('LINE Pay 付款初始化失敗', 'error');
      }
    },
    error: function(xhr, status, error) {
      console.error('LINE Pay 結帳錯誤:', error);
      showNotification('LINE Pay 結帳失敗，請稍後再試', 'error');
    }
  });
}

// 現金結帳（原本的流程）
function checkoutWithCash(memberId) {
  // 準備結帳數據
  const checkoutData = {
    memberId: parseInt(memberId), // 確保發送為數字類型
    paymentMethod: false, // 現金付款
    couponCode: selectedCoupon ? selectedCoupon.couponCode : null // 包含選擇的折價券
  };
  
  // 發送到後端建立訂單
  $.ajax({
    url: '/shopOrd/checkout',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(checkoutData),
    success: function(response) {
      if (response.success) {
        showNotification('訂單建立成功！', 'success');
        
        // 重新載入購物車（清空）
        loadCartFromBackend();
        
        // 關閉購物車側邊欄
        const cartSidebar = document.getElementById('cartSidebar');
        if (cartSidebar) {
          cartSidebar.classList.remove('active');
        }
        
        // 可以選擇跳轉到訂單確認頁面或留在當前頁面
        setTimeout(() => {
          if (confirm('訂單建立成功！是否要查看訂單詳情？')) {
            window.location.href = '/admin/shopOrd/select_page';
          }
        }, 2000);
      } else {
        showNotification(response.message || '訂單建立失敗', 'error');
      }
    },
    error: function(xhr, status, error) {
      console.error('結帳錯誤:', error);
      showNotification('結帳失敗，請稍後再試', 'error');
    }
  });
}

// 從session獲取會員ID的輔助函數
function getMemberIdFromSession() {
  // 使用全局會員ID變數
  if (currentMemberId) {
    return currentMemberId;
  }
  
  // 如果全局變數沒有，嘗試從頁面元素獲取
  const memberIdElement = document.querySelector('input[name="memberId"]');
  if (memberIdElement && memberIdElement.value) {
    currentMemberId = memberIdElement.value;
    return currentMemberId;
  }
  
  // 如果都沒有找到，返回null表示需要登入
  return null;
}

// 顯示通知訊息
function showNotification(message, type = 'info') {
  // 創建通知元素
  const notification = document.createElement('div');
  notification.className = `notification notification-${type}`;
  notification.innerHTML = `
    <div class="notification-content">
      <span>${message}</span>
      <button class="notification-close">&times;</button>
    </div>
  `;
  
  // 添加樣式
  notification.style.cssText = `
    position: fixed;
    top: 20px;
    right: 20px;
    background: ${type === 'success' ? '#28a745' : type === 'warning' ? '#ffc107' : type === 'error' ? '#dc3545' : '#17a2b8'};
    color: white;
    padding: 15px 20px;
    border-radius: 5px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    z-index: 10001;
    transform: translateX(100%);
    transition: transform 0.3s ease;
  `;
  
  // 添加到頁面
  document.body.appendChild(notification);
  
  // 顯示動畫
  setTimeout(() => {
    notification.style.transform = 'translateX(0)';
  }, 100);
  
  // 關閉按鈕事件
  const closeBtn = notification.querySelector('.notification-close');
  closeBtn.addEventListener('click', () => {
    hideNotification(notification);
  });
  
  // 自動關閉
  setTimeout(() => {
    hideNotification(notification);
  }, 3000);
}

// 隱藏通知
function hideNotification(notification) {
  notification.style.transform = 'translateX(100%)';
  setTimeout(() => {
    if (notification.parentNode) {
      notification.parentNode.removeChild(notification);
    }
  }, 300);
}

// 添加通知樣式
const notificationStyles = `
  .notification-content {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .notification-close {
    background: none;
    border: none;
    color: white;
    font-size: 1.2rem;
    cursor: pointer;
    padding: 0;
    width: 20px;
    height: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
`;

// 添加樣式到頁面
if (!document.getElementById('notification-styles')) {
  const styleSheet = document.createElement('style');
  styleSheet.id = 'notification-styles';
  styleSheet.textContent = notificationStyles;
  document.head.appendChild(styleSheet);
}

// 取代本地 cart，從後端載入會員購物車
function loadCartFromBackend() {
  $.get("/prodCart/api/member-cart", function(cartItems) {
    // cartItems 是後端回傳的購物車陣列
    let html = "";
    let total = 0;
    let itemCount = 0;
    if (cartItems.length === 0) {
      html = `<div class="cart-empty">
        <i class="fas fa-shopping-cart"></i>
        <p>購物車是空的</p>
      </div>`;
    } else {
      html = cartItems.map(item => {
        itemCount += item.quantity;
        total += item.quantity * item.prodVO.productPrice;
        return `<div class="cart-item">
          <div class="cart-item-image">
            <img src="/front-end/shop/product-image/${item.prodVO.productId}" alt="${item.prodVO.productName}" onerror="this.src='/shoppage/image/icon/bag.svg'">
          </div>
          <div class="cart-item-info">
            <div class="cart-item-title">${item.prodVO.productName}</div>
            <div class="cart-item-price">NT$ ${item.prodVO.productPrice}</div>
            <div class="cart-item-quantity">
              <span>${item.quantity}</span>
            </div>
          </div>
        </div>`;
      }).join('');
    }
    $("#cartItems").html(html);
    
    // 更新商品總計
    const subtotalElement = document.getElementById('subtotal');
    if (subtotalElement) {
      subtotalElement.textContent = `NT$ ${total.toLocaleString()}`;
    }
    
    // 更新應付金額（考慮折價券）
    updatePriceBreakdown();
    
    const cartIcon = document.querySelector('.cart-icon');
    if (cartIcon) {
      cartIcon.setAttribute('data-count', itemCount);
      cartIcon.classList.toggle('has-items', itemCount > 0);
    }
  });
}

// 頁面載入時自動載入購物車
$(function() {
  loadCartFromBackend();
});

// 載入可用的折價券
function loadAvailableCoupons() {
  console.log('開始載入折價券...');
  
  // 計算購物車總金額
  let cartTotal = 0;
  $('.cart-item').each(function() {
    const price = parseInt($(this).find('.item-price').text().replace(/[^\d]/g, ''));
    const quantity = parseInt($(this).find('.quantity-input').val());
    cartTotal += price * quantity;
  });
  
  console.log('購物車總金額:', cartTotal);
  
  $.ajax({
    url: '/shopOrd/api/available-coupons',
    type: 'GET',
    data: { cartTotal: cartTotal },
    success: function(response) {
      console.log('折價券API響應:', response);
      
      if (response.success) {
        availableCoupons = response.coupons || [];
        console.log('可用折價券數量:', availableCoupons.length);
        console.log('折價券列表:', availableCoupons);
        populateCouponSelect();
      } else {
        console.log('載入折價券失敗:', response.message);
        showNotification('載入折價券失敗: ' + response.message, 'error');
      }
    },
    error: function(xhr, status, error) {
      console.error('載入折價券錯誤:', error);
      console.error('HTTP狀態:', xhr.status);
      console.error('響應文本:', xhr.responseText);
      showNotification('載入折價券失敗，請檢查網路連線', 'error');
    }
  });
}

// 填充折價券下拉選單
function populateCouponSelect() {
  const couponSelect = document.getElementById('couponSelect');
  if (!couponSelect) return;
  
  // 清空現有選項（保留第一個"不使用折價券"選項）
  couponSelect.innerHTML = '<option value="">不使用折價券</option>';
  
  // 添加可用的折價券
  availableCoupons.forEach(coupon => {
    const option = document.createElement('option');
    option.value = coupon.couponCode;
    
    // 顯示詳細的折價券信息
    let couponText = `${coupon.couponName} - 折抵 NT$ ${coupon.discountValue}`;
    if (coupon.minPurchase > 0) {
      couponText += ` (最低消費 NT$ ${coupon.minPurchase})`;
    }
    
    option.textContent = couponText;
    option.dataset.coupon = JSON.stringify(coupon);
    couponSelect.appendChild(option);
  });
}

// 處理折價券選擇
function handleCouponSelection() {
  const couponSelect = document.getElementById('couponSelect');
  const couponInfo = document.getElementById('couponInfo');
  
  if (!couponSelect || !couponInfo) return;
  
  const selectedValue = couponSelect.value;
  
  if (selectedValue === '') {
    // 沒有選擇折價券
    selectedCoupon = null;
    couponInfo.style.display = 'none';
  } else {
    // 選擇了折價券
    const selectedOption = couponSelect.options[couponSelect.selectedIndex];
    selectedCoupon = JSON.parse(selectedOption.dataset.coupon);
    
    // 檢查最低消費
    const subtotalElement = document.getElementById('subtotal');
    const subtotalText = subtotalElement.textContent;
    const subtotal = parseInt(subtotalText.replace(/[^0-9]/g, '')) || 0;
    
    if (selectedCoupon.minPurchase > 0 && subtotal < selectedCoupon.minPurchase) {
      // 未達最低消費
      couponInfo.innerHTML = `<small class="text-danger">未達最低消費 NT$ ${selectedCoupon.minPurchase}</small>`;
      couponInfo.style.display = 'block';
      selectedCoupon = null; // 不允許使用此折價券
    } else {
      // 可以使用折價券
      couponInfo.innerHTML = `<small class="text-success">已選擇折價券</small>`;
      couponInfo.style.display = 'block';
    }
  }
  
  // 重新計算價格
  updatePriceBreakdown();
}

// 更新價格明細
function updatePriceBreakdown() {
  const subtotalElement = document.getElementById('subtotal');
  const discountRow = document.getElementById('discountRow');
  const discountAmount = document.getElementById('discountAmount');
  const cartTotal = document.getElementById('cartTotal');
  
  if (!subtotalElement || !discountRow || !discountAmount || !cartTotal) return;
  
  // 獲取商品總計
  const subtotalText = subtotalElement.textContent;
  const subtotal = parseInt(subtotalText.replace(/[^0-9]/g, '')) || 0;
  
  // 計算折扣
  let discount = 0;
  if (selectedCoupon) {
    discount = Math.min(selectedCoupon.discountValue, subtotal); // 折扣不能超過總金額
  }
  
  // 計算應付金額
  const total = Math.max(0, subtotal - discount);
  
  // 更新顯示
  subtotalElement.textContent = `NT$ ${subtotal.toLocaleString()}`;
  
  if (discount > 0) {
    discountRow.style.display = 'flex';
    discountAmount.textContent = `-NT$ ${discount.toLocaleString()}`;
  } else {
    discountRow.style.display = 'none';
  }
  
  cartTotal.textContent = `NT$ ${total.toLocaleString()}`;
} 