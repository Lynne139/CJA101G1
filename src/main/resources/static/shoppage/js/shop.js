// 商城 JavaScript 功能

// 購物車數據
let cart = JSON.parse(localStorage.getItem('shopCart')) || [];
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
  // 初始化購物車
  updateCartDisplay();
  
  // 綁定事件監聽器
  bindEventListeners();
  
  // 初始化篩選功能
  initializeFilters();
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
  if (cart.length === 0) {
    showNotification('購物車是空的', 'warning');
    return;
  }
  
  // 這裡可以導向結帳頁面或顯示結帳表單
  showNotification('即將導向結帳頁面...', 'info');
  
  // 模擬結帳流程
  setTimeout(() => {
    // 清空購物車
    cart = [];
    localStorage.removeItem('shopCart');
    updateCartDisplay();
    
    // 關閉購物車側邊欄
    const cartSidebar = document.getElementById('cartSidebar');
    if (cartSidebar) {
      cartSidebar.classList.remove('active');
    }
    
    showNotification('訂單已成功提交！', 'success');
  }, 2000);
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