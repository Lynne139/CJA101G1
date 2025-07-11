
DROP DATABASE IF EXISTS maison;
CREATE DATABASE IF NOT EXISTS maison;
USE maison;

-- 會員等級種類
CREATE TABLE member_level_type (
	member_level VARCHAR(30) NOT NULL PRIMARY KEY,
	level_rank INT NOT NULL
);


-- 會員資料表
CREATE TABLE member (
	member_id INT AUTO_INCREMENT PRIMARY KEY,
	member_level VARCHAR(30) NOT NULL,
	member_password VARCHAR(50) NOT NULL,
	member_name VARCHAR(50) NOT NULL,
	member_birthday Date NOT NULL,
    member_phone VARCHAR(10) NOT NULL,
	member_address VARCHAR(255) NOT NULL,
	member_pic LONGBLOB,
	member_status TINYINT(1) default 1,
	member_email VARCHAR(50) NOT NULL,
	member_points INT NOT NULL,
	member_accumulative_consumption INT NOT NULL,
	FOREIGN KEY (member_level) REFERENCES member_level_type(member_level)
);


INSERT INTO MEMBER_LEVEL_TYPE (member_level, level_rank)
VALUES 
('白金卡會員', 4),
('金卡會員', 3),
('銀卡會員', 2),
('普通會員', 1);


INSERT INTO MEMBER (
    member_level,
    member_password,
    member_name,
    member_birthday,
    member_phone,
    member_address,
    member_pic,
    member_status,
    member_email,
    member_points,
    member_accumulative_consumption
) VALUES 
('金卡會員', '1234', '郭惠民', '1990-05-20', '0987654321', '桃園市', NULL, 1, 'test@gmail.com', 6000, 60000),
('金卡會員', 'Pw223456', '張書涵', '1987-02-09', '0912345678', '新北市新莊區中華路一段320號', NULL, 1, 'shuhan.chang@example.com', 2600, 34000),
('銀卡會員', 'Pw323456', '周柏睿', '1993-06-21', '0933444555', '桃園市平鎮區中豐路168號', NULL, 1, 'boerh.chou@example.com', 1000, 12000),
('普通會員', 'Pw423456', '黃思妍', '1996-09-10', '0966123456', '台中市南屯區公益路二段28號', NULL, 1, 'siyan.huang@example.com', 0, 0),
('白金卡會員', 'Pw523456', '賴承翰', '1984-11-12', '0955666777', '高雄市鳳山區中山路370號', NULL, 1, 'chenghan.lai@example.com', 5200, 91000),
('金卡會員', 'Pw623456', '曾巧琳', '1991-03-28', '0911444888', '台南市永康區中正南路120號', NULL, 1, 'chiaolin.tseng@example.com', 2400, 36000),
('銀卡會員', 'Pw723456', '許家豪', '1994-08-17', '0977555987', '彰化縣和美鎮彰新路三段22號', NULL, 1, 'chiahao.hsu@example.com', 1600, 10500),
('普通會員', 'Pw823456', '吳育慈', '1998-01-25', '0922111987', '新竹市北區東大路二段88號', NULL, 1, 'yuci.wu@example.com', 0, 0),
('銀卡會員', 'Pw923456', '蔡昱辰', '1989-05-14', '0966888777', '宜蘭市復興路一段15號', NULL, 1, 'yuchen.tsai@example.com', 1300, 14000),
('普通會員', 'Pw023456', '杜宥瑄', '1997-10-03', '0933888999', '基隆市信一路8號', NULL, 1, 'yousyuan.tu@example.com', 0, 0);

-- 折價券 
CREATE TABLE coupon
(
	coupon_code CHAR(8) NOT NULL PRIMARY KEY,
	coupon_name VARCHAR(50) NOT NULL,
	order_type INT NOT NULL,
	discount_value INT NOT NULL,
	min_purchase INT DEFAULT 0 NOT NULL,
	claim_start_date DATE NOT NULL,
	claim_end_date DATE NOT NULL,
	expiry_date DATE NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 會員折價券
CREATE TABLE member_coupon
(
	coupon_code CHAR(8) NOT NULL,
	member_id INT NOT NULL,
	is_used BOOLEAN NOT NULL DEFAULT FALSE,
	claim_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	used_time DATETIME,
    PRIMARY KEY (coupon_code, member_id),
    FOREIGN KEY (coupon_code) REFERENCES coupon(coupon_code),
    FOREIGN KEY (member_id) REFERENCES member(member_id)
);

INSERT INTO coupon (coupon_code, coupon_name, order_type, discount_value, min_purchase, claim_start_date, claim_end_date, expiry_date)
VALUES('A2501AAA', '乙巳新春折價券A', 1, 200, 300, '2025-01-01', '2025-06-30', '2025-07-31'),
	('B2501AAA', '乙巳新春折價券B', 2, 150, 300, '2025-01-01', '2025-06-30', '2025-07-31'),
	('C2501AAA', '乙巳新春折價券C', 3, 300, 500, '2025-01-01', '2025-06-30', '2025-07-31'),
    ('A2504AAA', '乙巳夏日折價券A', 1, 200, 300, '2025-04-01', '2025-09-30', '2025-10-31'),
	('B2504AAA', '乙巳夏日折價券B', 2, 150, 300, '2025-04-01', '2025-09-30', '2025-10-31'),
	('C2504AAA', '乙巳夏日折價券C', 3, 300, 500, '2025-04-01', '2025-09-30', '2025-10-31'),
    ('A2505AAA', '乙巳端午折價券A', 1, 100, 100, '2025-05-01', '2025-06-30', '2025-07-31'),
	('B2505AAA', '乙巳端午折價券B', 2, 150, 150, '2025-05-01', '2025-06-30', '2025-07-31'),
	('C2505AAA', '乙巳端午折價券C', 3, 200, 200, '2025-05-01', '2025-06-30', '2025-07-31'),
	('A2507AAA', '乙巳中秋折價券A', 1, 200, 300, '2025-07-01', '2025-09-30', '2025-10-31'),
	('B2507AAA', '乙巳中秋折價券B', 2, 150, 300, '2025-07-01', '2025-09-30', '2025-10-31'),
	('C2507AAA', '乙巳中秋折價券C', 3, 300, 500, '2025-07-01', '2025-09-30', '2025-10-31'),
	('A2507AAB', '乙巳秋季折價券A', 1, 200, 300, '2025-07-01', '2025-12-31', '2026-01-31'),
	('B2507AAB', '乙巳秋季折價券B', 2, 150, 300, '2025-07-01', '2025-12-31', '2026-01-31'),
	('C2507AAB', '乙巳秋季折價券C', 3, 300, 500, '2025-07-01', '2025-12-31', '2026-01-31');

INSERT INTO member_coupon (coupon_code, member_id, used_time)
VALUES('A2505AAA', 1, NULL),
	('B2505AAA', 1, NULL),
	('C2505AAA', 1, NULL),
	('A2505AAA', 2, NULL),
	('B2505AAA', 2, NULL),
	('A2505AAA', 3, NULL);




-- 系統通知 
CREATE TABLE notification
(
	notification_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	member_id INT NOT NULL,
	title VARCHAR(50) NOT NULL,
	content VARCHAR(255) NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	is_read BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (member_id) REFERENCES member(member_id)
);

INSERT INTO notification (member_id, title, content)
VALUES (1, '登入成功通知', '親愛的貴賓，您已於 2025-07-07 20:57:12 成功登入。'),
	(1, '登入成功通知', '親愛的貴賓，您已於 2025-07-07 20:57:12 成功登入。'),
    (1, '登入成功通知', '親愛的貴賓，您已於 2025-07-07 20:57:12 成功登入。'),
    (1, '登入成功通知', '親愛的貴賓，您已於 2025-07-07 20:57:12 成功登入。'),
    (1, '登入成功通知', '親愛的貴賓，您已於 2025-07-07 20:57:12 成功登入。'),
    (1, '登入成功通知', '親愛的貴賓，您已於 2025-07-07 20:57:12 成功登入。'),
    (1, '登入成功通知', '親愛的貴賓，您已於 2025-07-07 20:57:12 成功登入。'),
    (1, '登入成功通知', '親愛的貴賓，您已於 2025-07-07 20:57:12 成功登入。'),
    (1, '登入成功通知', '親愛的貴賓，您已於 2025-07-07 20:57:12 成功登入。'),
    (1, '登入成功通知', '親愛的貴賓，您已於 2025-07-07 20:57:12 成功登入。'),
    (1, '登入成功通知', '親愛的貴賓，您已於 2025-07-07 20:57:12 成功登入。'),
    (2, '登入成功通知', '親愛的貴賓，您已於 2025-07-07 20:57:12 成功登入。'),
    (2, '登入成功通知', '親愛的貴賓，您已於 2025-07-07 20:57:12 成功登入。'),
    (2, '登入成功通知', '親愛的貴賓，您已於 2025-07-07 20:57:12 成功登入。'),
    (2, '登入成功通知', '親愛的貴賓，您已於 2025-07-07 20:57:12 成功登入。'),
    (2, '登入成功通知', '親愛的貴賓，您已於 2025-07-07 20:57:12 成功登入。');

-- 職稱
CREATE TABLE job_title (
    job_title_id INT AUTO_INCREMENT PRIMARY KEY,
    job_title_name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO job_title (job_title_id, job_title_name, description, is_active) VALUES
  (1, '總經理', '負責公司整體營運管理', TRUE),
  (2, '副總經理', '協助總經理管理公司業務', TRUE),
  (3, '部門經理', '負責部門日常營運管理', TRUE),
  (4, '資深專員', '具備豐富經驗的專業人員', TRUE),
  (5, '專員', '一般業務處理人員', TRUE),
  (6, '助理', '協助各項業務處理', TRUE),
  (7, '系統', '自動寫入資料', TRUE);

-- 員工
create table employee (
  Employee_id int auto_increment primary key not null,
  Role_id int not null,
  job_title_id int,
  Name varchar(50) not null,
  Status boolean not null default true,
  Created_date date not null,
  Password varchar(50) not null,
  employee_photo longblob
);

INSERT INTO employee (Employee_id, Role_id, job_title_id, Name, Status, Created_date, Password, employee_photo)
VALUES
  (1, 1, 7, 'SYSTEM', TRUE, '2025-01-01', '1234', NULL),
  (2, 1, 1, '吳永志', TRUE, '2025-01-01', '1234', NULL),
  (3, 2, 5, '吳冠宏', TRUE, '2025-02-15', '1234', NULL);

-- 權限
create table function_access_right (
  Access_id int  auto_increment primary key,
  Access_name Varchar(50) not null
);

INSERT INTO FUNCTION_ACCESS_RIGHT (ACCESS_NAME) VALUES
('會員管理權限'),
('員工管理權限'),
('住宿管理權限'),
('餐廳管理權限'),
('商店管理權限'),
('優惠管理權限'),
('客服管理權限'),
('消息管理權限');

-- 員工個別權限
CREATE TABLE employee_function_access_right (
    employee_id INT NOT NULL,
    function_access_right_id INT NOT NULL,
    start_date DATE,
    end_date DATE,
    enabled BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (employee_id, function_access_right_id),
    FOREIGN KEY (employee_id) REFERENCES employee(Employee_id),
    FOREIGN KEY (function_access_right_id) REFERENCES function_access_right(Access_id)
);

INSERT INTO employee_function_access_right (employee_id, function_access_right_id, start_date, end_date, enabled) VALUES
(1, 1, NULL, NULL, TRUE), -- SYSTEM 擁有會員管理權限
(1, 2, NULL, NULL, TRUE), -- SYSTEM 擁有員工管理權限
(1, 3, NULL, NULL, TRUE), -- SYSTEM 擁有住宿管理權限
(1, 4, NULL, NULL, TRUE), -- SYSTEM 擁有餐廳管理權限
(1, 5, NULL, NULL, TRUE), -- SYSTEM 擁有商店管理權限
(1, 6, NULL, NULL, TRUE), -- SYSTEM 擁有優惠管理權限
(1, 7, NULL, NULL, TRUE), -- SYSTEM 擁有客服管理權限
(1, 8, NULL, NULL, TRUE), -- SYSTEM 擁有消息管理權限
(2, 2, NULL, NULL, TRUE), -- 吳永志 員工管理權限
(2, 3, NULL, NULL, TRUE), -- 吳永志 住宿管理權限
(2, 4, NULL, NULL, TRUE), -- 吳永志 餐廳管理權限
(3, 7, NULL, NULL, TRUE); -- 吳冠宏 客服管理權限

-- 客服留言 
CREATE TABLE customer_service_message
(
    message_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    member_id INT,
    customer_name VARCHAR(50),
    email VARCHAR(50) NOT NULL,
    submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    message VARCHAR(1500) NOT NULL,
    processing_status INT NOT NULL DEFAULT 0,
    employee_id INT,
    responsed_at DATETIME,
    FOREIGN KEY (member_id) REFERENCES member(member_id),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

INSERT INTO customer_service_message (member_id, customer_name, email, message)
VALUES
    (1, '林冠宇', 'guanyu.lin@example.com', '請問洗衣間有提供柔軟精嗎?'),
    (2, NULL, 'shuhan.chang@example.com', '我非常想帶我的狗一同入住可以嗎?'),
    (NULL, '杜宥瑄', 'yousyuan.tu@example.com', '請問有接駁專車嗎?');

-- 商品類別 
create table product_category(

  PRODUCT_CATEGORY_ID INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  PRODUCT_CATEGORY_NAME VARCHAR(30) NOT NULL,
  PRODUCT_CATEGORY_DESC VARCHAR(1000)

)AUTO_INCREMENT = 1;

-- 商品 
create table product(

  PRODUCT_ID INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  PRODUCT_CATEGORY_ID int NOT NULL,
  PRODUCT_PRICE INT NOT NULL,
  PRODUCT_NAME VARCHAR(30) NOT NULL,
  PRODUCT_STATUS Boolean NOT NULL DEFAULT TRUE,

  foreign key (PRODUCT_CATEGORY_ID) references PRODUCT_CATEGORY(PRODUCT_CATEGORY_ID)
)AUTO_INCREMENT = 1;


-- 商品購物車項目 
create table prod_cart_item(

  MEMBER_ID INT NOT NULL,
  PRODUCT_ID INT NOT NULL,
  PRIMARY KEY(MEMBER_ID, PRODUCT_ID),
  
  QUANTITY INT NOT NULL,
  
  foreign key (MEMBER_ID) references MEMBER(MEMBER_ID),
  foreign key (PRODUCT_ID) references PRODUCT(PRODUCT_ID)
  
);


-- 商城訂單 
create table shop_order(

  PRODUCT_ORDER_ID INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  MEMBER_ID INT NOT NULL,
  PRODUCT_ORDER_DATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRODUCT_AMOUNT INT NOT NULL,
  PAYMENT_METHOD Boolean,
  ORDER_STATUS TinyInt NOT NULL,
  DISCOUNT_AMOUNT INT,
  ACTUAL_PAYMENT_AMOUNT INT NOT NULL,
  COUPON_CODE CHAR(8),
  
  foreign key (MEMBER_ID) references MEMBER(MEMBER_ID),
  foreign key (COUPON_CODE) references COUPON(COUPON_CODE)
  
);


-- 商品訂單明細 
create table shop_order_detail(

  PRODUCT_ORDER_ID INT NOT NULL,
  PRODUCT_ID INT NOT NULL,
  PRIMARY KEY(PRODUCT_ORDER_ID, PRODUCT_ID),
  
  PURCHASE_PRICE INT NOT NULL,
  PRODUCT_QUANTITY INT NOT NULL,

  foreign key (PRODUCT_ORDER_ID) references SHOP_ORDER(PRODUCT_ORDER_ID),
  foreign key (PRODUCT_ID) references PRODUCT(PRODUCT_ID)
  
);


-- 商品圖片 
create table product_photo(

  PRODUCT_PHOTO_ID INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  PRODUCT_ID INT NOT NULL,
  PRODUCT_PHOTO MEDIUMBLOB,
  
  foreign key (PRODUCT_ID) references PRODUCT(PRODUCT_ID)
);


-- 

INSERT INTO product_category (PRODUCT_CATEGORY_NAME, PRODUCT_CATEGORY_DESC)
VALUES 
  ('家電', '各類家庭電器'),
  ('書籍', '各式書籍、雜誌'),
  ('3C產品', '電腦與周邊設備'),
  ('香氛', '包含香水、室內香氛、香氛蠟燭等。'),
  ('寢具', '主要包含床墊、床單、被子、枕頭等。 這些用品可以依照材質、功能、季節性等不同面向進行分類和介紹。。');

INSERT INTO PRODUCT (PRODUCT_CATEGORY_ID, PRODUCT_PRICE, PRODUCT_NAME, PRODUCT_STATUS)
VALUES 
  (1, 12000, '吸塵器', TRUE),
  (4, 450, '香氛蠟燭', TRUE),
  (5, 5900, '枕頭', TRUE),
  (5, 450, '蠶絲被', TRUE);


INSERT INTO PROD_CART_ITEM (MEMBER_ID, PRODUCT_ID,QUANTITY)
VALUES
  (1, 1, 1),
  (1, 2, 2),
  (2, 3, 1);

INSERT INTO SHOP_ORDER (
   MEMBER_ID, PRODUCT_AMOUNT,
  PAYMENT_METHOD, ORDER_STATUS, DISCOUNT_AMOUNT, ACTUAL_PAYMENT_AMOUNT,COUPON_CODE
)
VALUES
  ( 1, 12450, TRUE, 1, 150, 12300,'A2505AAA'),
  ( 2, 30000, FALSE, 0, 0, 30000,null);

INSERT INTO SHOP_ORDER_DETAIL (PRODUCT_ORDER_ID, PRODUCT_ID, PURCHASE_PRICE, PRODUCT_QUANTITY)
VALUES
  (1, 1, 12000, 1),
  (2, 2, 450, 1),
  (2, 3, 30000, 1);

INSERT INTO PRODUCT_PHOTO (PRODUCT_ID, PRODUCT_PHOTO)
VALUES 
  ( 1, NULL),
  ( 2, NULL),
  ( 3, NULL);
  
-- 新聞報導 
create table news_list (
News_no int auto_increment primary key not null,
Title varchar(100) not null,
Content Varchar(1000) not null,
Published_date date not null,
Is_display boolean default true not null,
News_photo longblob);

INSERT INTO NEWS_LIST (TITLE, CONTENT, PUBLISHED_DATE, IS_DISPLAY, NEWS_PHOTO)
VALUES 
(
  '嶼蔻飯店榮獲2025年度最佳設計旅宿獎',
  '嶼蔻 Maison d''Yuko 憑藉獨特海島設計與高品質服務，榮獲觀光局頒發「最佳設計旅宿獎」，感謝大家的支持與肯定。',
  '2025-04-20',
  TRUE,
  NULL
),
(
  '嶼蔻攜手在地藝術家推出香氛聯名系列',
  '我們邀請台灣知名藝術家合作，推出限定香氛系列商品，歡迎至飯店精品區與官網選購，數量有限售完為止。',
  '2025-05-10',
  TRUE,
  NULL
);


-- 最新消息 
create table hot_news_list (
Hot_news_no int primary key auto_increment not null,
Title Varchar(100) not null,
Content Varchar(1000) not null,
Created_date date not null,
Is_display boolean default true not null,
Hotnews_photo longblob);

INSERT INTO HOT_NEWS_LIST (TITLE, CONTENT, CREATED_DATE, IS_DISPLAY, HOTNEWS_PHOTO)
VALUES 
(
  '嶼蔻飯店全新開幕，打造奢華海島體驗',
  '我們誠摯歡迎您蒞臨嶼蔻飯店，享受融合自然與藝術的度假體驗，立即預約享限時開幕優惠。',
  '2025-05-01',
  TRUE,
  NULL
),
(
  '端午連假住房優惠活動開跑',
  '端午節連假期間，訂房即享 85 折優惠，另加贈限量香氛禮盒，數量有限送完為止！',
  '2025-05-15',
  TRUE,
  NULL
);

-- 活動通知
 create table promotion_list (
Promo_no int auto_increment primary key not null,
Title varchar(100) not null,
Content Varchar(1000) not null,
Start_date date not null,
End_date date not null,
Is_display boolean default true not null,
Promo_photo longblob);

INSERT INTO PROMOTION_LIST (TITLE, CONTENT, START_DATE, END_DATE, IS_DISPLAY, PROMO_PHOTO)
VALUES 
(
  '夏日清涼住房優惠',
  '即日起至 7 月底，平日入住所有房型享 8 折優惠，暑期親子出遊首選！',
  '2025-06-01',
  '2025-07-31',
  TRUE,
  NULL
),
(
  '週年慶加碼回饋活動',
  '嶼蔻飯店一週年，會員訂房即享升等房型優惠，加贈迎賓香氛禮盒一份！',
  '2025-08-01',
  '2025-08-31',
  TRUE,
  NULL
);







-- 住宿訂單 
CREATE TABLE ROOM_ORDER (
    ROOM_ORDER_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    MEMBER_ID INT NOT NULL,
    ORDER_DATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 自動抓訂單進入時間
    ROOM_ORDER_STATUS TINYINT(1) NOT NULL DEFAULT 1,  -- 0:取消, 1:成立, 2:完成
    TOTAL_AMOUNT INT NOT NULL,
    ROOM_AMOUNT INT NOT NULL,
    CHECK_IN_DATE DATE NOT NULL,
    CHECK_OUT_DATE DATE NOT NULL,
    COUPON_CODE CHAR(8),
    DISCOUNT_AMOUNT INT,
    ACTUAL_AMOUNT INT NOT NULL,
    PAY_STATUS TINYINT(1) NOT NULL DEFAULT 0, -- 0:未付款 1:已付款
    PAY_METHOD TINYINT(1) NOT NULL DEFAULT 0, -- 0:現金 1:信用卡
    PROJECT_ADD_ON BOOLEAN NOT NULL DEFAULT 0,
    UPDATE_DATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, /* 新增修改都自動寫入 */
    UPDATE_EMP INT NOT NULL DEFAULT 1, -- 系統id=1
     
    FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER(MEMBER_ID),
                            FOREIGN KEY (COUPON_CODE) REFERENCES COUPON(COUPON_CODE),
                            FOREIGN KEY (UPDATE_EMP) REFERENCES EMPLOYEE(EMPLOYEE_ID)
);
-- 訂單編號從1000開始
ALTER TABLE room_order AUTO_INCREMENT = 1000;


INSERT INTO ROOM_ORDER (MEMBER_ID, ROOM_ORDER_STATUS, TOTAL_AMOUNT, ROOM_AMOUNT, CHECK_IN_DATE, CHECK_OUT_DATE, COUPON_CODE, DISCOUNT_AMOUNT, ACTUAL_AMOUNT, PROJECT_ADD_ON,UPDATE_EMP)
VALUES 
    (1, 1, 100000,1, '2025-07-11', '2025-07-13', 'A2505AAA', 100, 99900, 0 ,1),
    (2, 1, 104800,2, '2025-07-15', '2025-07-17', 'B2505AAA', 150, 104650, 1 ,1),
    (3, 1, 15000,1, '2025-07-12', '2025-07-13', null, 0, 15000, 0 ,1),
    (4, 1, 80000,2, '2025-07-18', '2025-07-20', null, 0, 80000, 0 ,1),
    (5, 1, 50000,1, '2025-07-19', '2025-07-20', null, 0, 50000, 0 ,1),
    (6, 1, 60000,2, '2025-07-12', '2025-07-13', null, 0, 60000, 0 ,1),
    (7, 1, 30000,2, '2025-07-26', '2025-07-27', null, 0, 30000, 0 ,1),
    (8, 1, 30000,1, '2025-07-25', '2025-07-27', null, 0, 30000, 0 ,1),
    (9, 1, 60000,1, '2025-07-17', '2025-07-20', null, 0, 60000, 0 ,1),
    (10, 1, 30000,1, '2025-07-12', '2025-07-13', null, 0, 30000, 0 ,1);


-- 房型 
CREATE TABLE room_type (
	room_type_id	INT AUTO_INCREMENT NOT NULL,
	room_type_name	  VARCHAR(50) NOT NULL,
		  
    room_type_content	VARCHAR(1000) NOT NULL,
    room_sale_status	BOOLEAN,
    room_type_pic	MEDIUMBLOB,
    room_type_price	INT NOT NULL,
    guest_num INT NOT NULL,
	CONSTRAINT room_type_pk PRIMARY KEY (room_type_id)
) AUTO_INCREMENT = 1;

INSERT INTO room_type (room_type_id, room_type_name, room_type_content, room_sale_status, room_type_pic, room_type_price, guest_num) VALUES (null, '月影行館', '獨棟頂級 Villa，附私人泳池與月光露台', 1, null, 50000, 6);
INSERT INTO room_type (room_type_id, room_type_name, room_type_content, room_sale_status, room_type_pic, room_type_price, guest_num) VALUES (null, '晨曦之庭', '南洋花園套房，開窗即是熱帶植物與日光灑落', 1, null, 30000, 4);
INSERT INTO room_type (room_type_id, room_type_name, room_type_content, room_sale_status, room_type_pic, room_type_price, guest_num) VALUES (null, '海音居', '靠海海景房，聽海浪入眠，私密陽台設計', 1, null, 20000, 2);
INSERT INTO room_type (room_type_id, room_type_name, room_type_content, room_sale_status, room_type_pic, room_type_price, guest_num) VALUES (null, '蔻香居', '主打香氛放鬆體驗，適合芳療與瑜伽旅人', 1, null, 15000, 2);

-- 房間 
CREATE TABLE room (
	room_id     INT NOT NULL,
	room_type_id     INT NOT NULL,
	room_guest_name       VARCHAR(50),
	room_sale_status  BOOLEAN,
	room_status       TINYINT NOT NULL,
	CONSTRAINT room_room_type_id_fk FOREIGN KEY (room_type_id) REFERENCES room_type (room_type_id),
	CONSTRAINT room_id_pk PRIMARY KEY (room_id)
);

INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (1101, 1, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (1102, 1, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (1103, 1, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (1104, 1, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (1105, 1, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (1106, 1, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (1107, 1, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (1108, 1, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (1109, 1, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (1110, 1, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (2101, 2, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (2102, 2, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (2103, 2, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (2104, 2, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (2105, 2, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (2106, 2, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (2107, 2, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (2108, 2, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (2109, 2, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (2110, 2, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3101, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3102, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3103, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3104, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3105, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3106, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3107, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3108, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3109, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3110, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3111, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3112, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3113, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3114, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3115, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3116, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3117, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3118, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3119, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3120, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4101, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4102, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4103, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4104, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4105, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4106, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4107, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4108, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4109, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4110, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4111, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4112, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4113, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4114, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4115, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4116, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4117, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4118, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4119, 4, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4120, 4, null, 1, 0);


-- 住宿訂單明細 
CREATE TABLE ROOM_ORDER_LIST (
    ROOM_ORDER_LIST_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ROOM_TYPE_ID INT NOT NULL,
    ROOM_ORDER_ID INT NOT NULL,
    NUMBER_OF_PEOPLE INT NOT NULL,
    SPECIAL_REQ VARCHAR(50),
    ROOM_PRICE INT NOT NULL,
    ROOM_AMOUNT INT NOT NULL,
	ROOM_GUEST_NAME VARCHAR(50),
	CREATE_DATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 自動抓訂單進入時間
	LIST_STATUS INT NOT NULL DEFAULT 1,
	FOREIGN KEY (ROOM_ORDER_ID) REFERENCES room_order(ROOM_ORDER_ID) ON DELETE CASCADE,
    FOREIGN KEY (ROOM_TYPE_ID) REFERENCES ROOM_TYPE(ROOM_TYPE_ID)
);


INSERT INTO ROOM_ORDER_LIST (ROOM_TYPE_ID,  ROOM_ORDER_ID, NUMBER_OF_PEOPLE, SPECIAL_REQ, ROOM_PRICE, ROOM_AMOUNT,ROOM_GUEST_NAME,LIST_STATUS)
VALUES
    (1, 1000, 6, 'No smoking', 50000, 1,'王大明',1),
    (2, 1001, 4, 'No smoking', 30000, 1,'周牛牛',1),
    (3, 1001, 2, 'Extra bed', 20000, 1,'楊大胖',1),
    (4, 1002, 2, null, 15000, 1, null,1),
    (3, 1003, 4, null, 20000, 2,'陳春',1),
    (1, 1004, 6, null, 50000, 1,'黃嘉明',1),
    (2, 1005, 8, null, 30000, 2,'李柏翰',1),
    (4, 1006, 4, null, 15000, 2,'趙元新',1),
    (4, 1007, 2, null, 15000, 1,'張君和',1),
    (3, 1008, 2, null, 20000, 1,'蔡詩穎',1),
    (2, 1009, 4, null, 30000, 1,'蔡詩穎',1);

-- 以下設定: 自增主鍵的起點值，也就是初始值，取值範圍是1 .. 655355 --
set auto_increment_offset=1;
-- 以下設定: 自增主鍵每次遞增的量，其預設值是1，取值範圍是1 .. 65535 --
set auto_increment_increment=1; 




-- 以下設定: 自增主鍵的起點值，也就是初始值，取值範圍是1 .. 655355 --
set auto_increment_offset=1;
-- 以下設定: 自增主鍵每次遞增的量，其預設值是1，取值範圍是1 .. 65535 --
set auto_increment_increment=1;

-- 房型預定表
CREATE TABLE room_type_schedule (
	room_type_schedule_id     INT AUTO_INCREMENT NOT NULL,
	room_type_id	INT NOT NULL,
	room_amount		INT NOT NULL,
	room_rsv_booked		INT NOT NULL,
	room_order_date		DATE NOT NULL,
	CONSTRAINT room_type_schedule_room_type_id_fk FOREIGN KEY (room_type_id) REFERENCES room_type (room_type_id),
	CONSTRAINT room_type_schedule_id_pk PRIMARY KEY (room_type_schedule_id)
) AUTO_INCREMENT = 1;


INSERT INTO room_type_schedule (room_type_schedule_id, room_type_id, room_amount, room_rsv_booked, room_order_date)  VALUES (null, 1, 10, 1, '2025-07-11');
INSERT INTO room_type_schedule (room_type_schedule_id, room_type_id, room_amount, room_rsv_booked, room_order_date)  VALUES (null, 1, 10, 1, '2025-07-12');
INSERT INTO room_type_schedule (room_type_schedule_id, room_type_id, room_amount, room_rsv_booked, room_order_date)  VALUES (null, 2, 10, 1, '2025-07-15');
INSERT INTO room_type_schedule (room_type_schedule_id, room_type_id, room_amount, room_rsv_booked, room_order_date)  VALUES (null, 2, 10, 1, '2025-07-16');
INSERT INTO room_type_schedule (room_type_schedule_id, room_type_id, room_amount, room_rsv_booked, room_order_date)  VALUES (null, 3, 20, 1, '2025-07-15');
INSERT INTO room_type_schedule (room_type_schedule_id, room_type_id, room_amount, room_rsv_booked, room_order_date)  VALUES (null, 3, 20, 1, '2025-07-16');
INSERT INTO room_type_schedule (room_type_schedule_id, room_type_id, room_amount, room_rsv_booked, room_order_date)  VALUES (null, 4, 20, 1, '2025-07-12');
INSERT INTO room_type_schedule (room_type_schedule_id, room_type_id, room_amount, room_rsv_booked, room_order_date)  VALUES (null, 3, 20, 3, '2025-07-18');
INSERT INTO room_type_schedule (room_type_schedule_id, room_type_id, room_amount, room_rsv_booked, room_order_date)  VALUES (null, 3, 20, 3, '2025-07-19');
INSERT INTO room_type_schedule (room_type_schedule_id, room_type_id, room_amount, room_rsv_booked, room_order_date)  VALUES (null, 1, 20, 1, '2025-07-19');
INSERT INTO room_type_schedule (room_type_schedule_id, room_type_id, room_amount, room_rsv_booked, room_order_date)  VALUES (null, 2, 20, 3, '2025-07-12');
INSERT INTO room_type_schedule (room_type_schedule_id, room_type_id, room_amount, room_rsv_booked, room_order_date)  VALUES (null, 4, 20, 3, '2025-07-26');
INSERT INTO room_type_schedule (room_type_schedule_id, room_type_id, room_amount, room_rsv_booked, room_order_date)  VALUES (null, 4, 20, 1, '2025-07-25');
INSERT INTO room_type_schedule (room_type_schedule_id, room_type_id, room_amount, room_rsv_booked, room_order_date)  VALUES (null, 3, 20, 1, '2025-07-17');



CREATE TABLE role_list (
  Role_id int auto_increment primary key not null,
  Role_name Varchar(50) not null,
  Remark Varchar(100),
  Is_active boolean not null default true
);

INSERT INTO role_list (Role_id, Role_name, Remark, Is_active) VALUES
  (1, '總經理室', '最高管理部門', TRUE),
  (2, '客服部', '處理顧客問題', TRUE);

  
  
  
  
  
  
  
  
-- 餐廳 
CREATE TABLE resto (
	resto_id INT AUTO_INCREMENT NOT NULL, -- PK用流水號
    resto_name VARCHAR(40) NOT NULL,
    resto_name_en VARCHAR(40),
    resto_seats_total INT NOT NULL,
    resto_info VARCHAR(150),
    resto_type VARCHAR(30),
    resto_content LONGTEXT,
    resto_phone VARCHAR(20),
    resto_loc VARCHAR(30),
    resto_img MEDIUMBLOB,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE, -- 上下架控制
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,  -- 軟刪除使用

    
	CONSTRAINT resto_pk PRIMARY KEY (resto_id)
);
-- PK編號從1開始
ALTER TABLE resto AUTO_INCREMENT = 1;
ALTER TABLE resto ADD COLUMN version INT NOT NULL DEFAULT 0;

INSERT INTO resto (resto_name, resto_name_EN, resto_seats_total, resto_info,
	resto_type, resto_content, resto_phone, resto_loc, resto_img) 
VALUES 
	('沐光餐廳', 'Luma Buffet', 250, '隨著一天的變幻，沐光的用餐環境與自然光影完美融合，您將享受每個時段各具特色的美食，體驗到充滿多樣與豐富選擇的自助餐盛宴。多款嶼蔻風味的經典料理與國際特色美食，從清新的熱帶水果、海鮮拼盤、到烤魚、燉飯等島嶼風味料理，每一口都帶您進入熱帶天堂。'
    , '海島風味、中西式自助餐', '<p dir="ltr">｜位置｜ 1F 中央庭園左側<br>｜開放時段｜</p>
 <ul>
 <li dir="ltr" aria-level="1">
 <p dir="ltr" role="presentation">早餐｜06:30&ndash;08:00、08:00&ndash;09:30</p>
 </li>
 <li dir="ltr" aria-level="1">
 <p dir="ltr" role="presentation">午餐｜11:00&ndash;12:30、12:30&ndash;14:00</p>
 </li>
 <li dir="ltr" aria-level="1">
 <p dir="ltr" role="presentation">午茶｜14:30&ndash;16:00</p>
 </li>
 <li dir="ltr" aria-level="1">
 <p dir="ltr" role="presentation">晚餐｜17:30&ndash;19:00、19:00&ndash;20:30<br><br></p>
 </li>
 </ul>
 <p dir="ltr">｜供餐形式｜<br>海島風味、中西式自助餐</p>
 <table style="border-collapse: collapse; width: 100.031%; height: 57.6px;" border="1"><colgroup><col style="width: 19.9081%;"><col style="width: 19.9081%;"><col style="width: 19.9081%;"><col style="width: 19.9081%;"><col style="width: 19.9081%;"></colgroup>
 <tbody>
 <tr style="height: 19.2px;">
 <td style="height: 19.2px; text-align: center;">&nbsp;</td>
 <td style="height: 19.2px; text-align: center;">早餐</td>
 <td style="height: 19.2px; text-align: center;">中餐</td>
 <td style="height: 19.2px; text-align: center;">午茶</td>
 <td style="height: 19.2px; text-align: center;">晚餐</td>
 </tr>
 <tr style="height: 19.2px;">
 <td style="height: 19.2px; text-align: left;">明日</td>
 <td style="height: 19.2px; text-align: center;">NT$650</td>
 <td style="height: 19.2px; text-align: center;">NT$1080</td>
 <td style="height: 19.2px; text-align: center;">NT$750</td>
 <td style="height: 19.2px; text-align: center;">NT$1180</td>
 </tr>
 <tr style="height: 19.2px;">
 <td style="height: 19.2px; text-align: left;">假日<span style="font-size: 10pt;">(含國定假日)</span></td>
 <td style="height: 19.2px; text-align: center;">NT$650</td>
 <td style="height: 19.2px; text-align: center;">NT$1180</td>
 <td style="height: 19.2px; text-align: center;">NT$850</td>
 <td style="height: 19.2px; text-align: center;">NT$1280</td>
 </tr>
 </tbody>
 </table>
 <p><span style="font-size: 10pt;">價格未含 10% 服務費</span></p>',
    '07-123-4567 #122', '1F 中央庭園左側', NULL),

	('嶼間餐館', 'Islespace Bistro', 200, '在島嶼與島嶼之間的靜謐時光，為您獻上精緻且富有節奏感的套餐體驗。嚴選在地與國際食材，從開胃前菜到手工甜點，佐料香氛調酒搭配，每一道菜皆展現出法式創意料理 x 南洋香料的清新格調與現代美感，是適合午時小聚或夜晚約會的理想場所。'
    , '精緻套餐',
    '<p dir="ltr">｜位置｜ 1F 大廳左側<br>｜開放時段｜</p>
 <ul>
 <li dir="ltr" aria-level="1">
 <p dir="ltr" role="presentation">午餐｜11:00&ndash;12:30、12:30&ndash;14:00</p>
 </li>
 <li dir="ltr" aria-level="1">
 <p dir="ltr" role="presentation">晚餐｜17:30&ndash;19:00、19:00&ndash;20:30<br><br></p>
 </li>
 </ul>
 <p dir="ltr">｜供餐形式｜<br>精緻套餐</p>'
    , '07-123-4567 #126', '1F 大廳左側', NULL);


-- 區段(只用在UI方便，不需軟刪)
CREATE TABLE resto_period (
  period_id INT AUTO_INCREMENT NOT NULL, -- PK用流水號
  resto_id INT NOT NULL,
  period_name VARCHAR(10) NOT NULL,
  
  sort_order INT NOT NULL DEFAULT 0,
  
  period_code TINYINT DEFAULT NULL, -- 0:早餐BREAKFAST, 1:早午餐BRUNCH, 2:午餐LUNCH, 3:午茶TEA, 4:晚餐DINNER, 5:宵夜SUPPER

  CONSTRAINT resto_period_pk PRIMARY KEY (period_id),
  CONSTRAINT resto_period_resto_fk FOREIGN KEY (resto_id) REFERENCES resto (resto_id),
  CONSTRAINT resto_period_sort_uk UNIQUE KEY (resto_id, sort_order),
  CONSTRAINT resto_period_room_uk UNIQUE KEY (resto_id, period_code)
);
-- PK編號從1開始
ALTER TABLE resto_period AUTO_INCREMENT = 1;

INSERT INTO resto_period (resto_id, period_name,sort_order,period_code)
VALUES 
  (1,'早餐',1,0), (1,'午餐',2,2), (1,'午茶',3,3), (1,'晚餐',4,4),
  (2,'早餐',1,0), (2,'午餐',2,2), (2,'午茶',3,3), (2,'晚餐',4,4), (2,'宵夜',5,NULL);
  
  
  -- 時段(與日期+餐廳去判斷滿額)
 CREATE TABLE resto_timeslot (
  timeslot_id INT AUTO_INCREMENT NOT NULL, -- PK用流水號
  resto_id INT NOT NULL,
  period_id INT,
  
  timeslot_name VARCHAR(5) NOT NULL,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,  -- 軟刪除使用
  
  CONSTRAINT resto_timeslot_pk PRIMARY KEY (timeslot_id),
  CONSTRAINT resto_timeslot_resto_fk FOREIGN KEY (resto_id) REFERENCES resto (resto_id),
  CONSTRAINT resto_timeslot_period_fk FOREIGN KEY (period_id) REFERENCES resto_period(period_id) ON DELETE SET NULL -- 方便前端依區段渲染  
  );
-- PK編號從1開始
ALTER TABLE resto_timeslot AUTO_INCREMENT = 1;

INSERT INTO resto_timeslot (resto_id, period_id, timeslot_name)
VALUES 
  (1,1, '07:00'), -- 早餐1
  (1,1, '09:30'),
  (1,2, '11:30'), -- 午餐2
  (1,2, '13:00'),
  (1,3, '15:00'), -- 午茶3
  (1,4, '17:30'), -- 晚餐4
  (1,4, '19:00'),
  
  (2,5, '07:00'), -- 早餐1
  (2,5, '09:00'),
  (2,6, '10:30'), -- 午餐2
  (2,6, '12:30'),
  (2,7, '14:30'), -- 午茶3
  (2,8, '17:00'), -- 晚餐4
  (2,8, '19:00'),
  (2,9, '22:00'); -- 宵夜5

-- 訂位訂單
CREATE TABLE resto_order (
	resto_order_id INT AUTO_INCREMENT NOT NULL ,
    
    member_id INT,
	room_order_id INT,
    resto_id INT NOT NULL,
    
    regi_date DATE NOT NULL,
    regi_timeslot_id INT NOT NULL,
    regi_seats INT NOT NULL,
    high_chairs INT DEFAULT 0,
    regi_req VARCHAR(500),
    
    -- 歷史訂單快照
    -- resto
    snapshot_resto_name VARCHAR(40) NOT NULL,
	snapshot_resto_name_en VARCHAR(40),
    -- period
	snapshot_period_name VARCHAR(10) NOT NULL,
    -- timeslot
	snapshot_timeslot_name VARCHAR(5) NOT NULL,

    
     -- 訂單客戶資料
    order_guest_name VARCHAR(30) NOT NULL,
    order_guest_phone VARCHAR(20) NOT NULL,
    order_guest_email VARCHAR(200) NOT NULL,
	order_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 自動抓訂單進入時間
    
	-- 管理+狀態與時效欄位
    admin_note VARCHAR(500), -- 管理員備註
    order_source TINYINT NOT NULL DEFAULT 1, -- 0:會員登入預約MEMBER, 1:住房訂單ROOM, 2:管理員建置ADMIN
	order_status TINYINT NOT NULL DEFAULT 1,  -- 0:取消CANCELED, 1:成立CREATED, 2:完成DONE, 3:保留WITHHOLD, 4:逾時NOSHOW
	reserve_expire_time DATETIME NOT NULL, -- 自動逾時時間（regi_date + timeslotName + 等待10min）
    
	CONSTRAINT resto_order_pk PRIMARY KEY (resto_order_id),
    CONSTRAINT resto_order_member_fk FOREIGN KEY (member_id) REFERENCES member (member_id),
    CONSTRAINT resto_order_room_order_fk FOREIGN KEY (room_order_id) REFERENCES room_order (room_order_id),
	CONSTRAINT resto_order_resto_fk FOREIGN KEY (resto_id) REFERENCES resto (resto_id),
    CONSTRAINT resto_order_timeslot_fk FOREIGN KEY (regi_timeslot_id) REFERENCES resto_timeslot(timeslot_id)	
);
-- 訂單編號從1開始
ALTER TABLE resto_order AUTO_INCREMENT = 1;
CREATE INDEX idx_resto_date_slot ON resto_order (resto_id, regi_date, regi_timeslot_id); -- 查某日某餐廳某時段是否已滿
CREATE INDEX idx_member_order ON resto_order (member_id); -- 查某會員所有訂單


INSERT INTO resto_order ( member_id, room_order_id, resto_id, 
regi_date, regi_timeslot_id, regi_seats, high_chairs, regi_req,
  snapshot_resto_name, snapshot_resto_name_en, snapshot_period_name, snapshot_timeslot_name,
  order_guest_name, order_guest_phone, order_guest_email, 
  order_source, order_status,reserve_expire_time
)
VALUES 
	(3,NULL,2,'2025-05-25',9,1,0,NULL, '嶼間餐館', 'Islespace Bistro', '早餐', '09:00', '周柏睿','0933444555','boerh.chou@example.com',0,2,'2025-05-25 11:10:00'),
	(2,1001,1,'2025-06-05',6,6,1,NULL, '沐光餐廳', 'Luma Buffet', '晚餐', '17:30', '張書涵','0912345678','shuhan.chang@example.com',1,2,'2025-06-05 17:40:00'),
	(2,1001,1,'2025-06-06',1,6,1,NULL, '沐光餐廳', 'Luma Buffet', '早餐', '07:00', '張書涵','0912345678','shuhan.chang@example.com',1,2,'2025-06-06 07:10:00'),
	(2,1001,1,'2025-06-06',4,6,1,NULL, '沐光餐廳', 'Luma Buffet', '午餐', '13:00', '張書涵','0912345678','shuhan.chang@example.com',1,2,'2025-06-06 13:10:00'),
	(2,1001,1,'2025-06-06',6,6,1,NULL, '沐光餐廳', 'Luma Buffet', '晚餐', '17:30', '張書涵','0912345678','shuhan.chang@example.com',1,2,'2025-06-06 17:40:00'),
	(2,1001,1,'2025-06-07',2,6,1,NULL, '沐光餐廳', 'Luma Buffet', '早餐', '09:30', '張書涵','0912345678','shuhan.chang@example.com',1,2,'2025-06-07 09:40:00'),
	(3,NULL,2,'2025-06-10',14,2,0,NULL, '嶼間餐館', 'Islespace Bistro', '晚餐', '19:00', '周柏睿','0933444555','boerh.chou@example.com',0,0,'2025-06-10 17:10:00'),
    (3,NULL,2,'2025-06-10',13,3,0,NULL, '嶼間餐館', 'Islespace Bistro', '晚餐', '17:00', '周柏睿','0933444555','boerh.chou@example.com',0,2,'2025-06-10 17:10:00');
    

-- 餐廳預訂表
CREATE TABLE resto_reservation (
	resto_reserve_id INT AUTO_INCREMENT NOT NULL ,
    resto_id INT NOT NULL,
    reserve_date DATE NOT NULL,
    reserve_timeslot_id INT NOT NULL,
    resto_seats_total INT NOT NULL, -- 後端抓餐廳設定寫入
    reserve_seats_total INT DEFAULT 0,
    
	CONSTRAINT resto_reservation_pk PRIMARY KEY (resto_reserve_id),
    CONSTRAINT resto_reservation_resto_fk FOREIGN KEY (resto_id) REFERENCES resto (resto_id),
	CONSTRAINT resto_reservation_timeslot_fk FOREIGN KEY (reserve_timeslot_id) REFERENCES resto_timeslot(timeslot_id)
);

INSERT INTO resto_reservation (resto_id,reserve_date,reserve_timeslot_id,resto_seats_total,reserve_seats_total) 
VALUES 
	(2,'2025-05-25',9,200,1),
	(1,'2025-06-05',6,250,6),
	(1,'2025-06-06',1,250,6),
	(1,'2025-06-06',4,250,6),
	(1,'2025-06-06',6,250,6),
	(1,'2025-06-07',2,250,6),
	(2,'2025-06-10',13,200,3);
