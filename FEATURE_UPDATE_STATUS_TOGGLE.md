# Cập nhật: Tính năng Đánh dấu Đã thuộc/Chưa thuộc

## Tổng quan
Đã bổ sung tính năng cho phép người dùng đánh dấu câu là "Đã thuộc" hoặc "Chưa thuộc" trực tiếp từ menu tại màn hình Danh sách câu.

## Thay đổi

### 1. ListOfDetailViewModel.kt
**Thêm chức năng:**
- Import `UpdateSentenceUseCase`
- Thêm `updateSentenceUseCase` vào constructor
- Thêm hàm `updateSentenceStatus(sentenceId: Int, newStatus: String)`
  - Tìm câu cần cập nhật
  - Gọi API để cập nhật status
  - Cập nhật lại danh sách câu
  - Tự động lọc lại theo filter hiện tại

### 2. ListOfDetail.kt (UI)
**Cập nhật SentenceCard:**
- Thêm tham số `status: String` để biết trạng thái hiện tại
- Thêm callback `onToggleStatus: () -> Unit` để xử lý thay đổi status
- Cập nhật menu dropdown:
  - Tăng width từ 120.dp → 140.dp để chứa text dài hơn
  - Thêm tùy chọn đầu tiên: "Đã thuộc" hoặc "Chưa thuộc"
  - Icon: ic_check_detailed_video_screen màu xanh
  - Text hiển thị:
    - Nếu status = "unknown" → Hiển thị "Đã thuộc"
    - Nếu status = "known" → Hiển thị "Chưa thuộc"

**Cập nhật danh sách câu:**
- Truyền `status = sentence.status` vào SentenceCard
- Thêm callback `onToggleStatus` để:
  - Đóng menu
  - Toggle status (unknown ↔ known)
  - Gọi `viewModel.updateSentenceStatus()`

## Luồng hoạt động

### Khi người dùng click "Đã thuộc" (từ status "unknown"):
1. User click menu "•••" trên một câu có status = "unknown"
2. Menu hiển thị tùy chọn "Đã thuộc" ở đầu
3. User click "Đã thuộc"
4. Menu đóng lại
5. ViewModel gọi API để cập nhật status → "known"
6. Danh sách câu được cập nhật
7. Nếu đang ở filter "Chưa thuộc", câu sẽ biến mất khỏi danh sách
8. Nếu đang ở filter "Tất cả", câu vẫn hiển thị nhưng status đã thay đổi

### Khi người dùng click "Chưa thuộc" (từ status "known"):
1. User click menu "•••" trên một câu có status = "known"
2. Menu hiển thị tùy chọn "Chưa thuộc" ở đầu
3. User click "Chưa thuộc"
4. Menu đóng lại
5. ViewModel gọi API để cập nhật status → "unknown"
6. Danh sách câu được cập nhật
7. Nếu đang ở filter "Đã thuộc", câu sẽ biến mất khỏi danh sách
8. Nếu đang ở filter "Tất cả", câu vẫn hiển thị nhưng status đã thay đổi

## Giao diện Menu

### Menu cho câu "Chưa thuộc" (status = "unknown"):
```
┌─────────────────┐
│ ✓ Đã thuộc      │  ← Mới thêm (màu xanh)
│ ✏ Chỉnh sửa     │
│ 🗑 Xóa          │
└─────────────────┘
```

### Menu cho câu "Đã thuộc" (status = "known"):
```
┌─────────────────┐
│ ✓ Chưa thuộc    │  ← Mới thêm (màu xanh)
│ ✏ Chỉnh sửa     │
│ 🗑 Xóa          │
└─────────────────┘
```

## API Call

Khi thay đổi status, app gọi:
```
PUT /api/sentences/{id}
{
  "term": "Where is the nearest station?",
  "definition": "Ga gần nhất ở đâu?",
  "status": "known",  // hoặc "unknown"
  "mistakes": 0
}
```

## Tương tác với Filter

Filter tabs hoạt động như sau:
- **Tất cả**: Hiển thị tất cả câu (không phụ thuộc status)
- **Chưa thuộc**: Chỉ hiển thị câu có status = "unknown"
- **Đã thuộc**: Chỉ hiển thị câu có status = "known"

Khi thay đổi status:
- Danh sách tự động cập nhật theo filter hiện tại
- Câu có thể biến mất khỏi danh sách nếu không còn match với filter

## Testing

### Test Case 1: Đánh dấu "Đã thuộc"
1. Vào màn Danh sách câu
2. Chọn filter "Chưa thuộc"
3. Click menu "•••" trên một câu
4. Verify: Menu hiển thị "Đã thuộc" ở đầu
5. Click "Đã thuộc"
6. Verify: Câu biến mất khỏi danh sách
7. Chuyển sang filter "Đã thuộc"
8. Verify: Câu xuất hiện trong danh sách

### Test Case 2: Đánh dấu "Chưa thuộc"
1. Vào màn Danh sách câu
2. Chọn filter "Đã thuộc"
3. Click menu "•••" trên một câu
4. Verify: Menu hiển thị "Chưa thuộc" ở đầu
5. Click "Chưa thuộc"
6. Verify: Câu biến mất khỏi danh sách
7. Chuyển sang filter "Chưa thuộc"
8. Verify: Câu xuất hiện trong danh sách

### Test Case 3: Toggle trong filter "Tất cả"
1. Vào màn Danh sách câu
2. Chọn filter "Tất cả"
3. Click menu "•••" trên một câu "Chưa thuộc"
4. Click "Đã thuộc"
5. Verify: Câu vẫn hiển thị trong danh sách
6. Click menu "•••" lại trên câu đó
7. Verify: Menu hiển thị "Chưa thuộc" (đã thay đổi)

### Test Case 4: Error handling
1. Tắt backend server
2. Thử đánh dấu "Đã thuộc"
3. Verify: Hiển thị thông báo lỗi
4. Verify: Status không thay đổi

## Files Modified

### Backend: Không có thay đổi
API đã hỗ trợ cập nhật status từ trước.

### Frontend:
1. ✅ `ListOfDetailViewModel.kt` - Thêm logic cập nhật status
2. ✅ `ListOfDetail.kt` - Cập nhật UI menu

## Build Status
✅ **BUILD SUCCESSFUL**

Không có lỗi compilation, chỉ có deprecation warnings không liên quan.

## Next Steps
1. Start backend: `python run.py`
2. Install app: `./gradlew installDebug`
3. Test tính năng theo test cases ở trên
4. Verify API calls trong Android Studio logcat

## Notes
- Tính năng này giúp người dùng quản lý tiến độ học tập
- Status được lưu vào database và đồng bộ qua API
- Filter tự động cập nhật khi status thay đổi
- Menu width đã được tăng lên để chứa text dài hơn
