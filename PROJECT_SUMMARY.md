# Tóm Tắt Dự Án Potago Backend

## Tổng Quan
Potago Backend là một API REST được xây dựng bằng Flask, phục vụ cho ứng dụng học ngôn ngữ thông qua YouTube subtitles, flashcards, và các tính năng tương tác khác.

## Cấu Trúc Dự Án
- **Ngôn ngữ:** Python 3.11
- **Framework:** Flask 3.0.3
- **Database:** MySQL (Aiven Cloud)
- **Authentication:** Firebase Admin SDK
- **Cloud Storage:** Cloudinary
- **Documentation:** Flasgger (Swagger UI)

## Cấu Hình Cơ Sở Dữ Liệu
- **Host:** mysql-31a68954-congduan2554-95bb.b.aivencloud.com
- **Port:** 17919
- **Database:** potago
- **User:** avnadmin
- **Password:** AVNS_6PArOANuuzltVGbJ8L8

## Firebase Configuration
- Sử dụng file `firebase-service-account.json` để xác thực
- Token required cho hầu hết các endpoint
- Header: `Authorization: Bearer <Firebase_ID_Token>`

## Base URL
- **Local Development:** `http://127.0.0.1:5000`
- **API Prefix:** `/api`
- **Swagger Docs:** `/swagger/`

## Các Module Chính

### 1. User Management (`/api/users`)
- **GET /ranking/top:** Lấy top 1000 users theo ExperiencePoints
- **GET /ranking/me:** Lấy rank của user hiện tại
- **POST /avatar:** Upload avatar lên Cloudinary
- **GET /settings:** Lấy cài đặt user

### 2. Word Sets (`/api/word-sets`)
- Quản lý bộ từ vựng

### 3. Words (`/api/words`)
- Quản lý từ vựng

### 4. Videos (`/api/videos`)
- Quản lý video YouTube

### 5. Subtitles (`/api/subtitles`)
- Xử lý phụ đề video

### 6. Sentence Patterns (`/api/sentence-patterns`)
- Quản lý mẫu câu

### 7. Sentences (`/api/sentences`)
- Quản lý câu

### 8. Flashcards (`/api/flashcards`)
- Quản lý flashcards

### 9. Items (`/api/users/items`)
- Quản lý items của user

### 10. Match Games (`/api/match-games`)
- Game ghép đôi

### 11. Rewards (`/api/rewards`)
- Hệ thống phần thưởng

### 12. Streaks (`/api/streaks`)
- Theo dõi chuỗi học tập

## Dependencies Chính
```
Flask==3.0.3
Flask-Cors==4.0.1
PyMySQL==1.1.1
firebase-admin==6.5.0
python-dotenv==1.0.1
gunicorn==22.0.0
flasgger==0.9.7.1
cloudinary==1.40.0
```

## Chạy Dự Án
1. Cài đặt dependencies: `pip install -r requirements.txt`
2. Thiết lập biến môi trường trong `.env`
3. Chạy: `python run.py`
4. Truy cập: `http://127.0.0.1:5000`
5. API Docs: `http://127.0.0.1:5000/swagger/`

## Deployment
- Sử dụng Render với Docker
- File cấu hình: `render.yaml`
- Environment variables cần thiết cho DB và Firebase

## Lưu Ý Bảo Mật
- Không commit file `.env` và `firebase-service-account.json`
- Sử dụng biến môi trường cho production
- Token Firebase cần được verify trước khi xử lý request