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
	member_status TINYINT(1) default 0,
	member_email VARCHAR(50) NOT NULL,
	member_points INT NOT NULL,
	member_accumulative_consumption INT NOT NULL,
	FOREIGN KEY (member_level) REFERENCES member_level_type(member_level)
);


INSERT INTO MEMBER_LEVEL_TYPE (member_level, level_rank)
VALUES 
('白金卡會員', 1),
('金卡會員', 2),
('銀卡會員', 3),
('普通會員', 4);


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
('白金卡會員', 'Pw123456', '林冠宇', '1990-05-20', '0987654321', '台北市大安區和平東路三段45號', NULL, 1, 'guanyu.lin@example.com', 4500, 75000),
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
VALUES('A2505AAA', '第一款測試折價券', 1, 100, 0, '2025-05-16', '2025-05-31', '2025-07-31'),
	('B2505AAA', '第二款測試折價券', 2, 150, 0, '2025-05-16', '2025-05-31', '2025-07-31'),
	('C2505AAA', '第三款測試折價券', 3, 200, 0, '2025-05-16', '2025-05-31', '2025-07-31');

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
VALUES (1, '測試通知標題一', '測試通知內容一'),
	(2, '測試通知標題二', '測試通知內容二'),
	(3, '測試通知標題三', '測試通知內容三');

-- 職稱表 
create table role_list (
Role_id int auto_increment primary key not null,
Role_name Varchar(50) not null,
Remark Varchar(100),
Is_active boolean not null default true
);

INSERT INTO ROLE_LIST (ROLE_NAME, REMARK)
VALUES 
('系統管理員', '擁有全站存取權限'),
('客服專員', '可回覆顧客留言與查詢訂單');

-- 員工 
Create table employee (
Employee_id int auto_increment primary key not null,
Role_id int not null,
Name Varchar(50) not null,
Status boolean default true not null,
Created_date date not null,
Password Varchar(50) not null,
foreign key (Role_id) references Role_list (Role_id)
);

INSERT INTO EMPLOYEE (ROLE_ID, NAME, CREATED_DATE, PASSWORD)
VALUES 
(1, '吳永志', '2025-01-01', '1234'),
(2, '吳冠宏', '2025-02-15', '1234');


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
VALUES(1, '林冠宇', 'guanyu.lin@example.com', '訂房有哪些優惠?'),
	(2, NULL, 'shuhan.chang@example.com', '早上幾點check out?'),
	(NULL, '杜宥瑄', 'yousyuan.tu@example.com', '住宿有公共洗衣機嗎?');
    
-- 功能權限
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
    
-- 角色權限
 CREATE TABLE role_access_right (
    Role_id INT NOT NULL,
    Access_id INT NOT NULL,
    PRIMARY KEY (Role_id, Access_id),
    FOREIGN KEY (Role_id) REFERENCES Role_list (Role_id),
    FOREIGN KEY (Access_id) REFERENCES Function_access_right (Access_id)
);

INSERT INTO Role_access_right (Role_id, Access_id) VALUES
(1, 1), (1, 2), (1, 3);


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
  
  PRODUCT_NAME VARCHAR(30) NOT NULL,
  QUANTITY INT NOT NULL,
  
  foreign key (MEMBER_ID) references MEMBER(MEMBER_ID),
  foreign key (PRODUCT_ID) references PRODUCT(PRODUCT_ID)
  
);


-- 商城訂單 
create table shop_order(

  PRODUCT_ORDER_ID INT PRIMARY KEY NOT NULL,
  MEMBER_ID INT NOT NULL,
  PRODUCT_ORDER_DATE DATE NOT NULL,
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

  PRODUCT_PHOTO_ID INT PRIMARY KEY NOT NULL,
  PRODUCT_ID INT NOT NULL,
  PRODUCT_PHOTO BLOB,
  
  foreign key (PRODUCT_ID) references PRODUCT(PRODUCT_ID)
);


-- 

INSERT INTO product_category (PRODUCT_CATEGORY_NAME, PRODUCT_CATEGORY_DESC)
VALUES 
  ('家電', '各類家庭電器'),
  ('書籍', '各式書籍、雜誌'),
  ('3C產品', '電腦與周邊設備');

INSERT INTO PRODUCT (PRODUCT_CATEGORY_ID, PRODUCT_PRICE, PRODUCT_NAME, PRODUCT_STATUS)
VALUES 
  (1, 12000, 'Dyson 吸塵器', TRUE),
  (2, 450, 'Java 程式設計入門', TRUE),
  (3, 30000, 'MacBook Air M2', TRUE);


INSERT INTO PROD_CART_ITEM (MEMBER_ID, PRODUCT_ID, PRODUCT_NAME, QUANTITY)
VALUES
  (1, 1, 'Dyson 吸塵器', 1),
  (1, 2, 'Java 程式設計入門', 2),
  (2, 3, 'MacBook Air M2', 1);

INSERT INTO SHOP_ORDER (
  PRODUCT_ORDER_ID, MEMBER_ID, PRODUCT_ORDER_DATE, PRODUCT_AMOUNT,
  PAYMENT_METHOD, ORDER_STATUS, DISCOUNT_AMOUNT, ACTUAL_PAYMENT_AMOUNT
)
VALUES
  (1001, 1, '2025-05-15', 12450, TRUE, 1, 450, 12000),
  (1002, 2, '2025-05-16', 30000, FALSE, 0, 0, 30000);

INSERT INTO SHOP_ORDER_DETAIL (PRODUCT_ORDER_ID, PRODUCT_ID, PURCHASE_PRICE, PRODUCT_QUANTITY)
VALUES
  (1001, 1, 12000, 1),
  (1001, 2, 450, 1),
  (1002, 3, 30000, 1);

INSERT INTO PRODUCT_PHOTO (PRODUCT_PHOTO_ID, PRODUCT_ID, PRODUCT_PHOTO)
VALUES 
  (1, 1, NULL),
  (2, 2, NULL),
  (3, 3, NULL);
  
-- 新聞報導 
create table news_list (
News_no int auto_increment primary key not null,
Title varchar(100) not null,
Content Varchar(1000) not null,
Published_date date not null,
Is_display boolean default true not null,
Promo_photo longblob);

INSERT INTO NEWS_LIST (TITLE, CONTENT, PUBLISHED_DATE, IS_DISPLAY, PROMO_PHOTO)
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
News_photo longblob);

INSERT INTO HOT_NEWS_LIST (TITLE, CONTENT, CREATED_DATE, IS_DISPLAY, NEWS_PHOTO)
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
    ROOM_ORDER_STATUS TINYINT(1) NOT NULL,  -- 0:取消, 1:成立, 2:完成
    ROOM_AMOUNT INT NOT NULL,
    CHECK_IN_DATE DATETIME NOT NULL,
    CHECK_OUT_DATE DATETIME NOT NULL,
    COUPON_CODE CHAR(8),
    DISCOUNT_AMOUNT INT,
    ACTUAL_AMOUNT INT NOT NULL,
    PROJECT_ADD_ON BOOLEAN NOT NULL DEFAULT 0,
    FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER(MEMBER_ID),
    FOREIGN KEY (COUPON_CODE) REFERENCES COUPON(COUPON_CODE)
);
-- 訂單編號從1000開始
ALTER TABLE room_order AUTO_INCREMENT = 1000;


INSERT INTO ROOM_ORDER (MEMBER_ID, ROOM_ORDER_STATUS, ROOM_AMOUNT, CHECK_IN_DATE, CHECK_OUT_DATE, COUPON_CODE, DISCOUNT_AMOUNT, ACTUAL_AMOUNT, PROJECT_ADD_ON)
VALUES 
(1, 1, 50000, '2025-06-01 15:00:00', '2025-06-03 11:00:00', 'A2505AAA', 100, 49900, 0),
(2, 2, 60000, '2025-06-05 14:00:00', '2025-06-07 12:00:00', 'B2505AAA', 150, 59850, 1);


-- 房型 
CREATE TABLE room_type (
	room_type_id	INT AUTO_INCREMENT NOT NULL,
	room_type_name	  VARCHAR(50) NOT NULL,
	room_type_amount	INT NOT NULL,	  
    room_type_content	VARCHAR(1000) NOT NULL,
    room_sale_status	BOOLEAN,
    room_type_pic	MEDIUMBLOB,
    room_type_price	INT NOT NULL,
	CONSTRAINT room_type_pk PRIMARY KEY (room_type_id)
) AUTO_INCREMENT = 1;

INSERT INTO room_type (room_type_id, room_type_name, room_type_amount, room_type_content, room_sale_status, room_type_pic, room_type_price) VALUES (null, '月影行館', 10, '房間介紹一', 1, null, 50000);
INSERT INTO room_type (room_type_id, room_type_name, room_type_amount, room_type_content, room_sale_status, room_type_pic, room_type_price) VALUES (null, '晨曦之庭', 10, '房間介紹二', 1, null, 30000);
INSERT INTO room_type (room_type_id, room_type_name, room_type_amount, room_type_content, room_sale_status, room_type_pic, room_type_price) VALUES (null, '海音居', 10, '房間介紹三', 1, null, 20000);
INSERT INTO room_type (room_type_id, room_type_name, room_type_amount, room_type_content, room_sale_status, room_type_pic, room_type_price) VALUES (null, '蔻香居', 10, '房間介紹四', 1, null, 15000);

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
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (2101, 2, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (3101, 3, null, 1, 0);
INSERT INTO room (room_id, room_type_id, room_guest_name, room_sale_status, room_status)  VALUES (4101, 4, null, 1, 0);


-- 住宿訂單明細 
CREATE TABLE ROOM_ORDER_LIST (
    ROOM_ORDER_LIST_ID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ROOM_TYPE_ID INT NOT NULL,
    ROOM_ID INT,
    ROOM_ORDER_ID INT NOT NULL,
    NUMBER_OF_PEOPLE INT NOT NULL,
    SPECIAL_REQ VARCHAR(50),
    ROOM_PRICE INT NOT NULL,
    ROOM_AMOUNT INT NOT NULL,
    FOREIGN KEY (ROOM_ORDER_ID) REFERENCES ROOM_ORDER(ROOM_ORDER_ID),
    FOREIGN KEY (ROOM_ID) REFERENCES ROOM(ROOM_ID),
    FOREIGN KEY (ROOM_TYPE_ID) REFERENCES ROOM_TYPE(ROOM_TYPE_ID)
);


INSERT INTO ROOM_ORDER_LIST (ROOM_TYPE_ID, ROOM_ID, ROOM_ORDER_ID, NUMBER_OF_PEOPLE, SPECIAL_REQ, ROOM_PRICE, ROOM_AMOUNT)
VALUES
(1, 1101, 1000, 2, 'No smoking', 50000, 1),
(3, 3101, 1001, 6, 'Extra bed', 30000, 2);

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


INSERT INTO room_type_schedule (room_type_schedule_id, room_type_id, room_amount, room_rsv_booked, room_order_date)  VALUES (null, 1, 10, 1, '2025-05-18');
INSERT INTO room_type_schedule (room_type_schedule_id, room_type_id, room_amount, room_rsv_booked, room_order_date)  VALUES (null, 2, 10, 2, '2025-05-17');


-- 餐廳 
CREATE TABLE resto (
	resto_id INT AUTO_INCREMENT NOT NULL, -- PK用流水號
    resto_name VARCHAR(40) NOT NULL,
    resto_name_en VARCHAR(40),
    resto_seats_total INT NOT NULL,
    resto_info VARCHAR(150),
    resto_type VARCHAR(30),
    resto_content LONGTEXT,
    resto_phone VARCHAR(16),
    resto_loc VARCHAR(30),
    resto_img MEDIUMBLOB,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE, -- 上下架控制
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,  -- 軟刪除使用

    
	CONSTRAINT resto_pk PRIMARY KEY (resto_id)
);
ALTER TABLE resto ADD COLUMN version INT NOT NULL DEFAULT 0;


-- PK編號從1開始
ALTER TABLE resto AUTO_INCREMENT = 1;

INSERT INTO resto (resto_name, resto_name_EN, resto_seats_total, resto_info,
	resto_type, resto_content, resto_phone, resto_loc, resto_img) 
VALUES 
	('沐光餐廳', 'Luma Buffet', 250, '餐廳簡介A', '海島風味、中西式自助餐', '餐廳文案A', '091234567 #123', '1F 中央庭園左側', NULL),
	('嶼間餐館', 'Islespace Bistro', 200, '餐廳簡介B', '精緻套餐', '餐廳文案B', '091234567 #456', '1F 大廳左側', NULL);


-- 區段
CREATE TABLE resto_period (
  period_id INT AUTO_INCREMENT NOT NULL, -- PK用流水號
  period_name VARCHAR(20) NOT NULL,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,  -- 軟刪除使用
  
  CONSTRAINT resto_period_pk PRIMARY KEY (period_id)
);
-- PK編號從1開始
ALTER TABLE resto AUTO_INCREMENT = 1;

INSERT INTO resto_period (period_name)
VALUES 
  ('早餐'),
  ('午餐'),
  ('午茶'),
  ('晚餐'),
  ('宵夜');
  
  
  -- 時段
 CREATE TABLE resto_timeslot (
  timeslot_id INT AUTO_INCREMENT NOT NULL, -- PK用流水號
  period_id INT NOT NULL,
  display_name VARCHAR(20) NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  is_deleted BOOLEAN NOT NULL DEFAULT FALSE,  -- 軟刪除使用
  
  CONSTRAINT resto_timeslot_pk PRIMARY KEY (timeslot_id),
  CONSTRAINT resto_timeslot_period_fk FOREIGN KEY (period_id) REFERENCES resto_period(period_id) -- 方便前端依區段渲染
);
-- PK編號從1開始
ALTER TABLE resto AUTO_INCREMENT = 1;

INSERT INTO resto_timeslot (period_id, display_name, start_time, end_time)
VALUES 
  (1, '07:00', '07:00:00', '08:30:00'), -- 早餐1
  (1, '08:30', '08:30:00', '10:00:00'),
  (2, '11:30', '11:30:00', '13:00:00'), -- 午餐2
  (3, '13:00', '13:00:00', '14:30:00'),
  (3, '15:00', '15:00:00', '16:30:00'), -- 午茶3
  (4, '17:30', '17:30:00', '19:00:00'), -- 晚餐4
  (4, '19:00', '19:00:00', '20:30:00');
  
  
 -- 餐廳＋區段＋時段（中介）
CREATE TABLE resto_period_timeslot (
  resto_id INT NOT NULL,
  period_id INT NOT NULL,
  timeslot_id INT NOT NULL,
  is_enabled BOOLEAN NOT NULL DEFAULT TRUE, -- 上下架控制
  
  CONSTRAINT resto_period_timeslot_pk PRIMARY KEY (resto_id, period_id, timeslot_id),
  CONSTRAINT resto_period_timeslot_resto_fk FOREIGN KEY (resto_id) REFERENCES resto(resto_id),
  CONSTRAINT resto_period_timeslot_period_fk FOREIGN KEY (period_id) REFERENCES resto_period(period_id),
  CONSTRAINT resto_period_timeslot_timeslot_fk FOREIGN KEY (timeslot_id) REFERENCES resto_timeslot(timeslot_id)
);

INSERT INTO resto_period_timeslot (resto_id, period_id, timeslot_id, is_enabled)
VALUES 
  -- 1餐廳A  1早(2)、2中(2)、3午茶(1)、4晚(2)
	(1, 1, 1, TRUE), (1, 1, 2, TRUE),
	(1, 2, 3, TRUE), (1, 2, 4, TRUE),
	(1, 3, 5, TRUE),
	(1, 4, 6, TRUE), (1, 4, 7, TRUE),

  -- 2餐廳B  2中(2)、4晚(2)
	(2, 1, 1, FALSE), (2, 1, 2, FALSE),
	(2, 2, 3, TRUE), (2, 2, 4, TRUE),
	(2, 3, 5, FALSE),
	(2, 4, 6, TRUE), (2, 4, 7, TRUE);


-- 訂位訂單
CREATE TABLE resto_order (
	resto_order_id INT AUTO_INCREMENT NOT NULL ,
    
    member_id INT NOT NULL,
	room_order_id INT,
    resto_id INT NOT NULL,
    
    regi_date DATE NOT NULL,
    regi_period_id INT NOT NULL,
    regi_timeslot_id INT NOT NULL,
    regi_seats INT NOT NULL,
    high_chairs INT DEFAULT 0,
    regi_req VARCHAR(200),
    
    -- 歷史訂單快照
    -- resto
    snapshot_resto_name VARCHAR(40),
	snapshot_resto_name_en VARCHAR(40),
    -- period
	snapshot_period_name VARCHAR(20),
    -- timeslot
	snapshot_timeslot_display_name VARCHAR(20),

    
     -- 訂單客戶資料
    order_guest_name VARCHAR(30) NOT NULL,
    order_guest_phone VARCHAR(10) NOT NULL,
    order_guest_email VARCHAR(60) NOT NULL,
	order_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 自動抓訂單進入時間
    
	-- 狀態與時效欄位
	order_status TINYINT NOT NULL DEFAULT 1,  -- 0:取消, 1:成立, 2:保留, 3:完成, 4:逾時
	reserve_expire_time DATETIME DEFAULT NULL, -- 自動逾時時間（regi_date + timeslot.start_time + 等待分鐘）

    
	CONSTRAINT resto_order_pk PRIMARY KEY (resto_order_id),
    CONSTRAINT resto_order_member_fk FOREIGN KEY (member_id) REFERENCES member (member_id),
    CONSTRAINT resto_order_room_order_fk FOREIGN KEY (room_order_id) REFERENCES room_order (room_order_id),
	CONSTRAINT resto_order_resto_fk FOREIGN KEY (resto_id) REFERENCES resto (resto_id),
    CONSTRAINT resto_order_period_fk FOREIGN KEY (regi_period_id) REFERENCES resto_period(period_id),
    CONSTRAINT resto_order_timeslot_fk FOREIGN KEY (regi_timeslot_id) REFERENCES resto_timeslot(timeslot_id)
);
-- 訂單編號從200開始
ALTER TABLE resto_order AUTO_INCREMENT = 200;

INSERT INTO resto_order ( member_id, room_order_id, resto_id, 
regi_date, regi_period_id, regi_timeslot_id, regi_seats, high_chairs, regi_req,
  snapshot_resto_name, snapshot_resto_name_en, snapshot_period_name, snapshot_timeslot_display_name,
  order_guest_name, order_guest_phone, order_guest_email, 
  order_status
)
VALUES 
	(2,1001,1,'2025-06-05',4,6,6,1,NULL, '沐光餐廳', 'Luma Buffet', '晚餐', '17:30', '張書涵','0912345678','shuhan.chang@example.com',1),
	(2,1001,1,'2025-06-06',1,2,6,1,NULL, '沐光餐廳', 'Luma Buffet', '早餐', '08:30', '張書涵','0912345678','shuhan.chang@example.com',1),
	(2,1001,1,'2025-06-06',2,3,6,1,NULL, '沐光餐廳', 'Luma Buffet', '午餐', '11:30', '張書涵','0912345678','shuhan.chang@example.com',1),
	(2,1001,1,'2025-06-06',4,7,6,1,NULL, '沐光餐廳', 'Luma Buffet', '晚餐', '19:00', '張書涵','0912345678','shuhan.chang@example.com',1),
	(2,1001,1,'2025-06-07',1,1,6,1,NULL, '沐光餐廳', 'Luma Buffet', '早餐', '07:00', '張書涵','0912345678','shuhan.chang@example.com',1),
	(3,NULL,2,'2025-06-10',2,3,3,0,NULL, '嶼間餐館', 'Islespace Bistro', '晚餐', '11:30', '周柏睿','0933444555','boerh.chou@example.com',1);
    

-- 餐廳預訂表
CREATE TABLE resto_reservation (
	resto_reserve_id INT AUTO_INCREMENT NOT NULL ,
    resto_id INT NOT NULL,
    reserve_date DATE NOT NULL,
    reserve_period_id INT NOT NULL,
    reserve_timeslot_id INT NOT NULL,
    resto_seats_total INT NOT NULL, -- 後端抓餐廳設定寫入
    reserve_seats_total INT DEFAULT 0,
    
	CONSTRAINT resto_reservation_pk PRIMARY KEY (resto_reserve_id),
    CONSTRAINT resto_reservation_resto_fk FOREIGN KEY (resto_id) REFERENCES resto (resto_id),
	CONSTRAINT resto_reservation_period_fk FOREIGN KEY (reserve_period_id) REFERENCES resto_period(period_id),
	CONSTRAINT resto_reservation_timeslot_fk FOREIGN KEY (reserve_timeslot_id) REFERENCES resto_timeslot(timeslot_id)
);

INSERT INTO resto_reservation (resto_id,reserve_date,
reserve_period_id,reserve_timeslot_id,resto_seats_total,reserve_seats_total) 
VALUES 
	(1,'2025-06-05',4,6,250,6),
	(1,'2025-06-06',1,2,250,6),
	(1,'2025-06-06',2,3,250,6),
	(1,'2025-06-06',4,7,250,6),
	(1,'2025-06-07',1,1,250,6),
	(2,'2025-06-10',2,3,200,3);
