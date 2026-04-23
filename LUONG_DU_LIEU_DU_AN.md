# Luồng dữ liệu dự án Potago - Giải thích cho Newbie 🌱

## 📱 Tổng quan đơn giản

Dự án Potago có 2 phần:
1. **Backend (Python/Flask)** - Máy chủ lưu trữ dữ liệu
2. **Frontend (Android/Kotlin)** - Ứng dụng trên điện thoại

```
┌─────────────┐         Internet/WiFi        ┌─────────────┐
│   Android   │ ◄─────────────────────────► │   Backend   │
│     App     │      HTTP Requests/JSON      │   (Flask)   │
└─────────────┘                              └─────────────┘
                                                     │
                                                     ▼
                                              ┌─────────────┐
                                              │  Database   │
                                              │ (PostgreSQL)│
                                              └─────────────┘
```

---

## 🔄 Luồng dữ liệu chi tiết (từ màn hình đến database)

### Ví dụ: User muốn xem danh sách câu mẫu

```
📱 USER NHẤN NÚT
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 1. PRESENTATION LAYER (Màn hình)                            │
│    File: ListOfDetail.kt                                    │
│    - Hiển thị giao diện                                     │
│    - User nhấn nút "Xem câu mẫu"                            │
└─────────────────────────────────────────────────────────────┘
    │
    │ viewModel.loadSentences(patternId)
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. VIEW MODEL (Xử lý logic màn hình)                       │
│    File: ListOfDetailViewModel.kt                          │
│    - Nhận yêu cầu từ màn hình                              │
│    - Gọi UseCase để lấy dữ liệu                            │
└─────────────────────────────────────────────────────────────┘
    │
    │ getSentencesByPatternUseCase(patternId)
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. USE CASE (Business Logic)                               │
│    File: GetSentencesByPatternUseCase.kt                   │
│    - Chứa logic nghiệp vụ                                  │
│    - Gọi Repository để lấy dữ liệu                         │
└─────────────────────────────────────────────────────────────┘
    │
    │ repository.getSentencesByPattern(patternId)
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. REPOSITORY (Quản lý nguồn dữ liệu)                      │
│    File: SentenceRepositoryImpl.kt                         │
│    - Quyết định lấy từ đâu: API, Cache, hoặc Database      │
│    - Gọi API Service                                       │
└─────────────────────────────────────────────────────────────┘
    │
    │ apiService.getSentences(patternId)
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 5. API SERVICE (Gọi HTTP Request)                          │
│    File: SentenceApiService.kt                             │
│    - Tạo HTTP request                                      │
│    - Gửi đến Backend qua Retrofit                          │
└─────────────────────────────────────────────────────────────┘
    │
    │ GET http://192.168.0.101:5000/api/sentences?pattern_id=1
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 6. BACKEND API (Python/Flask)                              │
│    File: sentence_routes.py                                │
│    - Nhận HTTP request                                     │
│    - Gọi Service để xử lý                                  │
└─────────────────────────────────────────────────────────────┘
    │
    │ sentence_service.get_sentences_by_pattern(pattern_id)
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 7. BACKEND SERVICE (Business Logic)                        │
│    File: sentence_service.py                               │
│    - Xử lý logic nghiệp vụ                                 │
│    - Gọi Repository để truy vấn database                   │
└─────────────────────────────────────────────────────────────┘
    │
    │ sentence_repository.find_by_pattern_id(pattern_id)
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 8. BACKEND REPOSITORY (Truy vấn Database)                  │
│    File: sentence_repository.py                            │
│    - Tạo SQL query                                         │
│    - Lấy dữ liệu từ PostgreSQL                             │
└─────────────────────────────────────────────────────────────┘
    │
    │ SELECT * FROM sentences WHERE pattern_id = 1
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 9. DATABASE (PostgreSQL)                                    │
│    - Lưu trữ tất cả dữ liệu                                │
│    - Trả về kết quả                                        │
└─────────────────────────────────────────────────────────────┘
    │
    │ Trả về JSON: [{"id": 1, "term": "Hello", ...}, ...]
    ▼
┌─────────────────────────────────────────────────────────────┐
│ 10. DỮ LIỆU QUAY NGƯỢC LẠI                                 │
│     Backend → API → Repository → UseCase → ViewModel       │
│     → Screen → Hiển thị cho User                           │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
📱 USER THẤY DANH SÁCH CÂU MẪU
```

---

## 🏗️ Kiến trúc Android App (Clean Architecture)

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  ┌──────────────┐         ┌──────────────┐                 │
│  │   Screen     │ ◄─────► │  ViewModel   │                 │
│  │  (UI/View)   │         │   (State)    │                 │
│  └──────────────┘         └──────────────┘                 │
└─────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                             │
│  ┌──────────────┐         ┌──────────────┐                 │
│  │   UseCase    │ ◄─────► │    Model     │                 │
│  │ (Logic nghiệp vụ)      │  (Entities)  │                 │
│  └──────────────┘         └──────────────┘                 │
└─────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────┐
│                      DATA LAYER                              │
│  ┌──────────────┐         ┌──────────────┐                 │
│  │  Repository  │ ◄─────► │ API Service  │                 │
│  │  (Impl)      │         │  (Retrofit)  │                 │
│  └──────────────┘         └──────────────┘                 │
└─────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
                            ┌──────────────┐
                            │   Backend    │
                            │   (Flask)    │
                            └──────────────┘
```

---

## 📦 Các thành phần chính

### 1. **Screen (Màn hình)**
- **Là gì?** Giao diện người dùng nhìn thấy
- **Làm gì?** Hiển thị dữ liệu, nhận input từ user
- **Ví dụ:** `ListOfDetail.kt`, `WritingPracticeScreen.kt`

### 2. **ViewModel**
- **Là gì?** Cầu nối giữa màn hình và logic
- **Làm gì?** Quản lý trạng thái (state), gọi UseCase
- **Ví dụ:** `ListOfDetailViewModel.kt`

### 3. **UseCase**
- **Là gì?** Chứa logic nghiệp vụ cụ thể
- **Làm gì?** Thực hiện 1 tác vụ duy nhất (Single Responsibility)
- **Ví dụ:** `GetSentencesByPatternUseCase.kt`, `UpdateSentenceUseCase.kt`

### 4. **Repository**
- **Là gì?** Quản lý nguồn dữ liệu
- **Làm gì?** Quyết định lấy từ API, cache, hay database local
- **Ví dụ:** `SentenceRepositoryImpl.kt`

### 5. **API Service**
- **Là gì?** Interface định nghĩa các API endpoint
- **Làm gì?** Gọi HTTP request đến Backend
- **Ví dụ:** `SentenceApiService.kt`

### 6. **Backend (Flask)**
- **Là gì?** Máy chủ xử lý logic và lưu trữ dữ liệu
- **Làm gì?** Nhận request, xử lý, trả về JSON
- **Ví dụ:** `sentence_routes.py`, `sentence_service.py`

### 7. **Database (PostgreSQL)**
- **Là gì?** Nơi lưu trữ tất cả dữ liệu
- **Làm gì?** Lưu và truy vấn dữ liệu
- **Ví dụ:** Bảng `sentences`, `users`, `sentence_patterns`

---

## 🔑 Các khái niệm quan trọng

### **Dependency Injection (DI)**
```kotlin
// Thay vì tự tạo:
val repository = SentenceRepositoryImpl()

// Hilt tự động inject:
@Inject constructor(
    private val repository: SentenceRepository
)
```
**Lợi ích:** Dễ test, dễ thay đổi implementation

### **State Management**
```kotlin
// ViewModel quản lý state
data class UiState(
    val isLoading: Boolean = false,
    val sentences: List<Sentence> = emptyList(),
    val error: String? = null
)

// Screen quan sát state
val uiState by viewModel.uiState.collectAsState()
```
**Lợi ích:** UI tự động cập nhật khi state thay đổi

### **Result Wrapper**
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```
**Lợi ích:** Xử lý success/error/loading một cách nhất quán

---

## 🎯 Ví dụ thực tế: Luyện viết (Writing Practice)

### **Bước 1: User mở màn hình Luyện viết**
```kotlin
// WritingPracticeScreen.kt
LaunchedEffect(patternId) {
    viewModel.loadSentences(patternId)  // Gọi ViewModel
}
```

### **Bước 2: ViewModel gọi UseCase**
```kotlin
// WritingPracticeViewModel.kt
fun loadSentences(patternId: Int) {
    viewModelScope.launch {
        when (val result = getSentencesByPatternUseCase(patternId)) {
            is Result.Success -> {
                _uiState.update { it.copy(sentences = result.data) }
            }
            is Result.Error -> {
                _uiState.update { it.copy(error = result.message) }
            }
        }
    }
}
```

### **Bước 3: UseCase gọi Repository**
```kotlin
// GetSentencesByPatternUseCase.kt
suspend operator fun invoke(patternId: Int): Result<List<Sentence>> {
    return repository.getSentencesByPattern(patternId)
}
```

### **Bước 4: Repository gọi API**
```kotlin
// SentenceRepositoryImpl.kt
override suspend fun getSentencesByPattern(patternId: Int): Result<List<Sentence>> {
    return try {
        val response = apiService.getSentences(patternId)
        Result.Success(response.map { it.toDomain() })
    } catch (e: Exception) {
        Result.Error(e.message ?: "Unknown error")
    }
}
```

### **Bước 5: API Service gọi Backend**
```kotlin
// SentenceApiService.kt
@GET("api/sentences")
suspend fun getSentences(
    @Query("pattern_id") patternId: Int
): List<SentenceDto>
```

### **Bước 6: Backend xử lý**
```python
# sentence_routes.py
@sentence_bp.route('/api/sentences', methods=['GET'])
def get_sentences():
    pattern_id = request.args.get('pattern_id')
    sentences = sentence_service.get_sentences_by_pattern(pattern_id)
    return jsonify([s.to_dict() for s in sentences])
```

### **Bước 7: Dữ liệu quay về**
```
Database → Backend → API → Repository → UseCase → ViewModel → Screen
```

### **Bước 8: User thấy danh sách câu**
```kotlin
// WritingPracticeScreen.kt
LazyColumn {
    items(uiState.sentences) { sentence ->
        Text(sentence.term)  // Hiển thị câu
    }
}
```

---

## 🚀 Tóm tắt siêu ngắn gọn

1. **User nhấn nút** → Screen
2. **Screen gọi** → ViewModel
3. **ViewModel gọi** → UseCase
4. **UseCase gọi** → Repository
5. **Repository gọi** → API Service
6. **API Service gọi** → Backend (Flask)
7. **Backend truy vấn** → Database (PostgreSQL)
8. **Dữ liệu quay ngược lại** → Hiển thị cho User

**Nguyên tắc vàng:** Mỗi layer chỉ biết layer ngay bên dưới nó!
- Screen chỉ biết ViewModel
- ViewModel chỉ biết UseCase
- UseCase chỉ biết Repository
- Repository chỉ biết API Service

**Lợi ích:**
- ✅ Dễ test từng phần
- ✅ Dễ thay đổi implementation
- ✅ Code sạch, dễ maintain
- ✅ Tách biệt UI và Business Logic

---

**Tạo bởi:** Kiro AI
**Ngày:** 23/04/2026
**Dành cho:** Newbie developers 🌱
