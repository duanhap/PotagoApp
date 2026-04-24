# Logic làm lại câu sai - Writing Practice ✅

## 🎯 Yêu cầu
Sau khi làm hết các câu mà có câu sai → quay lại làm những câu sai đó → làm đúng hết mới hiện màn Kết quả.

## 🔄 Cách hoạt động

### Ví dụ đơn giản:
```
Có 5 câu: A, B, C, D, E

Vòng 1:
A (đúng) → B (sai) → C (đúng) → D (sai) → E (đúng)
           ↓                      ↓
      [Câu sai: B, D]

Vòng 2 (Làm lại):
B (đúng) → D (sai)
  ↓          ↓
[Xóa B]  [Còn D]

Vòng 3 (Làm lại):
D (đúng) → Màn Kết quả ✅
  ↓
[Xóa D, hết câu sai]
```

## 📝 Các trường hợp

### Trường hợp 1: Tất cả đúng ngay
```
Câu 1 ✅ → Câu 2 ✅ → Câu 3 ✅ → Kết quả 🎉
```

### Trường hợp 2: Có câu sai
```
Vòng 1: Câu 1 ✅ → Câu 2 ❌ → Câu 3 ✅
Vòng 2: Câu 2 ✅ → Kết quả 🎉
```

### Trường hợp 3: Sai nhiều lần
```
Vòng 1: Câu 1 ❌ → Câu 2 ❌ → Câu 3 ❌
Vòng 2: Câu 1 ❌ → Câu 2 ✅ → Câu 3 ❌
Vòng 3: Câu 1 ✅ → Câu 3 ❌
Vòng 4: Câu 3 ✅ → Kết quả 🎉
```

## 💻 Code thay đổi

### 1. Thêm theo dõi câu sai
```kotlin
data class WritingPracticeUiState(
    // ...
    val incorrectSentences: MutableList<Setence> = mutableListOf(), // Danh sách câu sai
    val isRetryRound: Boolean = false // Đang làm lại?
)
```

### 2. Khi trả lời sai → thêm vào danh sách
```kotlin
if (isCorrect) {
    // Đúng
} else {
    // Sai → thêm vào danh sách câu sai
    incorrectSentences.add(currentSentence)
}
```

### 3. Khi hết câu → kiểm tra
```kotlin
if (hết câu) {
    if (có câu sai) {
        // Bắt đầu vòng làm lại
        sentences = incorrectSentences
        currentIndex = 0
        isRetryRound = true
    } else {
        // Không có câu sai → Hoàn thành
        isCompleted = true
    }
}
```

### 4. Khi làm đúng ở vòng retry → xóa khỏi danh sách
```kotlin
if (đúng && đang retry) {
    incorrectSentences.remove(currentSentence)
}
```

## ✅ Lợi ích

1. **Học hiệu quả hơn**: Phải làm đúng tất cả mới xong
2. **Tập trung vào điểm yếu**: Chỉ làm lại những câu sai
3. **Không lặp vô hạn**: Mỗi câu đúng sẽ bị xóa khỏi danh sách
4. **Progress rõ ràng**: Biết còn bao nhiêu câu cần làm

## 🧪 Test thử

### Test 1: Tất cả đúng
- Làm 5 câu, tất cả đúng
- Kết quả: Hiện màn Kết quả ngay

### Test 2: Có 1 câu sai
- Làm 5 câu, câu 3 sai
- Sau câu 5 → quay lại làm câu 3
- Làm đúng câu 3 → Hiện màn Kết quả

### Test 3: Nhiều câu sai
- Làm 5 câu, câu 2, 4, 5 sai
- Sau câu 5 → quay lại làm câu 2, 4, 5
- Làm đúng câu 2, sai câu 4, đúng câu 5
- Quay lại làm câu 4
- Làm đúng câu 4 → Hiện màn Kết quả

## 📊 Progress Bar

### Vòng 1 (5 câu):
```
1/5 → 2/5 → 3/5 → 4/5 → 5/5
```

### Vòng 2 (2 câu sai):
```
1/2 → 2/2
```

### Vòng 3 (1 câu sai):
```
1/1 → Kết quả
```

## ⏱️ Thời gian

Thời gian tính từ **lúc bắt đầu** đến **lúc hoàn thành** (bao gồm cả vòng retry).

Ví dụ:
- Bắt đầu: 10:00:00
- Vòng 1 xong: 10:02:00
- Vòng 2 xong: 10:03:30
- Thời gian hiển thị: **3:30**

## 🎨 Có thể cải thiện thêm (tương lai)

1. **Thông báo**: "Bạn còn 3 câu cần làm lại"
2. **Hiển thị vòng**: "Vòng 2/3"
3. **Màu sắc**: Vòng retry dùng màu đỏ thay vì xanh
4. **Animation**: Hiệu ứng khi chuyển sang vòng retry
5. **Thống kê**: Số lần sai mỗi câu

## ✅ Hoàn thành

- ✅ Theo dõi câu sai
- ✅ Tự động bắt đầu vòng retry
- ✅ Xóa câu khi làm đúng
- ✅ Chỉ hiện Kết quả khi hết câu sai
- ✅ Progress bar đúng
- ✅ Thời gian tính đúng
- ✅ Build thành công

---
**Cập nhật**: 23/04/2026
**Trạng thái**: ✅ Hoàn thành
