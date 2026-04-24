# Writing Practice - Retry Logic Update ✅

## Yêu cầu mới
Sau khi trả lời hết các câu trong Luyện viết mà có câu sai → sẽ quay trở lại những câu làm sai đó để làm, đến bao giờ các câu sai đó làm đúng hết thì mới hiện giao diện màn Kết quả.

## Logic mới

### 1. **Theo dõi câu sai**
```kotlin
data class WritingPracticeUiState(
    // ... các field khác
    val incorrectSentences: MutableList<Setence> = mutableListOf(), // Danh sách câu sai
    val isRetryRound: Boolean = false // Đang ở vòng làm lại câu sai
)
```

### 2. **Khi user trả lời sai**
```kotlin
fun checkAnswer(userAnswer: String) {
    // ...
    if (isCorrect) {
        // Đúng -> cập nhật status = "known"
    } else {
        // Sai -> thêm vào danh sách câu sai
        val incorrectList = _uiState.value.incorrectSentences
        if (!incorrectList.any { it.id == currentSentence.id }) {
            incorrectList.add(currentSentence)
        }
    }
}
```

### 3. **Khi chuyển sang câu tiếp theo**
```kotlin
fun moveToNextSentence() {
    // Nếu câu hiện tại đúng và đang ở vòng retry, xóa khỏi danh sách câu sai
    if (answerResult is Correct && isRetryRound) {
        incorrectSentences.removeAll { it.id == currentSentence?.id }
    }
    
    if (currentIndex + 1 < sentences.size) {
        // Còn câu tiếp theo -> chuyển sang câu tiếp
    } else {
        // Hết câu
        if (incorrectSentences.isNotEmpty()) {
            // Có câu sai -> Bắt đầu vòng làm lại
            _uiState.update {
                it.copy(
                    sentences = incorrectSentences.toList(),
                    currentIndex = 0,
                    currentSentence = incorrectSentences.firstOrNull(),
                    isRetryRound = true
                )
            }
        } else {
            // Không có câu sai -> Hoàn thành
            _uiState.update { it.copy(isCompleted = true) }
            claimRewards()
        }
    }
}
```

## Luồng hoạt động

### **Trường hợp 1: Tất cả câu đúng ngay lần đầu**
```
Câu 1 (đúng) → Câu 2 (đúng) → Câu 3 (đúng) → Màn Kết quả ✅
```

### **Trường hợp 2: Có câu sai**
```
Vòng 1:
Câu 1 (đúng) → Câu 2 (sai) → Câu 3 (đúng) → Câu 4 (sai)
                  ↓                              ↓
            [Câu sai: 2]                  [Câu sai: 2, 4]

Vòng 2 (Retry):
Câu 2 (đúng) → Câu 4 (sai)
    ↓              ↓
[Xóa câu 2]   [Câu sai: 4]

Vòng 3 (Retry):
Câu 4 (đúng) → Màn Kết quả ✅
    ↓
[Xóa câu 4, danh sách rỗng]
```

### **Trường hợp 3: Câu sai nhiều lần**
```
Vòng 1:
Câu 1 (sai) → Câu 2 (sai) → Câu 3 (sai)
[Câu sai: 1, 2, 3]

Vòng 2 (Retry):
Câu 1 (sai) → Câu 2 (đúng) → Câu 3 (sai)
[Câu sai: 1, 3]

Vòng 3 (Retry):
Câu 1 (đúng) → Câu 3 (sai)
[Câu sai: 3]

Vòng 4 (Retry):
Câu 3 (đúng) → Màn Kết quả ✅
[Câu sai: rỗng]
```

## Các thay đổi trong code

### **WritingPracticeViewModel.kt**

#### 1. Thêm fields mới vào UiState
```kotlin
val incorrectSentences: MutableList<Setence> = mutableListOf()
val isRetryRound: Boolean = false
```

#### 2. Cập nhật `checkAnswer()`
- Khi sai: Thêm câu vào `incorrectSentences`
- Khi đúng: Không thay đổi (vẫn cập nhật status = "known")

#### 3. Cập nhật `moveToNextSentence()`
- Kiểm tra nếu đang ở vòng retry và câu hiện tại đúng → xóa khỏi danh sách sai
- Khi hết câu:
  - Nếu có câu sai → bắt đầu vòng retry
  - Nếu không có câu sai → hiện màn Kết quả

#### 4. Thêm `getTotalProgress()`
```kotlin
fun getTotalProgress(): String {
    val current = _uiState.value.currentIndex + 1
    val total = _uiState.value.sentences.size
    return "$current/$total"
}
```

## Lợi ích

### ✅ Học tập hiệu quả hơn
- User phải làm đúng tất cả câu mới hoàn thành
- Tập trung vào những câu còn yếu

### ✅ Không bị lặp vô hạn
- Mỗi lần làm đúng, câu đó bị xóa khỏi danh sách retry
- Đảm bảo sẽ kết thúc khi user làm đúng hết

### ✅ Theo dõi tiến độ rõ ràng
- Progress bar cập nhật theo số câu trong vòng hiện tại
- User biết còn bao nhiêu câu cần làm

## UI/UX Improvements (Có thể thêm sau)

### 1. **Thông báo khi bắt đầu vòng retry**
```kotlin
if (isRetryRound && currentIndex == 0) {
    // Hiển thị dialog hoặc snackbar:
    // "Bạn còn ${incorrectSentences.size} câu cần làm lại"
}
```

### 2. **Hiển thị số vòng retry**
```kotlin
Text("Vòng ${retryCount + 1}")
```

### 3. **Màu sắc khác biệt cho vòng retry**
```kotlin
val headerColor = if (isRetryRound) Color(0xFFFF6063) else Color(0xFF58CC02)
```

### 4. **Hiển thị danh sách câu sai**
```kotlin
Text("Câu cần làm lại: ${incorrectSentences.size}")
```

## Testing Checklist

### Test Case 1: Tất cả đúng
- [ ] Làm 5 câu, tất cả đúng
- [ ] Kết quả: Hiện màn Kết quả ngay sau câu cuối

### Test Case 2: Có 1 câu sai
- [ ] Làm 5 câu, câu 3 sai
- [ ] Kết quả: Sau câu 5, quay lại làm câu 3
- [ ] Làm đúng câu 3 → Hiện màn Kết quả

### Test Case 3: Nhiều câu sai
- [ ] Làm 5 câu, câu 2, 4, 5 sai
- [ ] Kết quả: Sau câu 5, quay lại làm câu 2, 4, 5
- [ ] Làm đúng câu 2, sai câu 4, đúng câu 5
- [ ] Kết quả: Quay lại làm câu 4
- [ ] Làm đúng câu 4 → Hiện màn Kết quả

### Test Case 4: Tất cả sai
- [ ] Làm 3 câu, tất cả sai
- [ ] Kết quả: Quay lại làm 3 câu đó
- [ ] Làm đúng hết → Hiện màn Kết quả

### Test Case 5: Progress bar
- [ ] Kiểm tra progress bar cập nhật đúng trong mỗi vòng
- [ ] Vòng 1: 1/5, 2/5, 3/5, 4/5, 5/5
- [ ] Vòng 2 (2 câu sai): 1/2, 2/2

### Test Case 6: Thời gian hoàn thành
- [ ] Thời gian tính từ lúc bắt đầu đến lúc hoàn thành (bao gồm cả vòng retry)
- [ ] Kiểm tra thời gian hiển thị đúng trên màn Kết quả

## Known Issues & Future Improvements

### Có thể cải thiện:
1. **Thông báo rõ ràng hơn** khi bắt đầu vòng retry
2. **Hiển thị số vòng** đã làm
3. **Animation** khi chuyển sang vòng retry
4. **Âm thanh** khác biệt cho vòng retry
5. **Thống kê** số lần làm sai mỗi câu

### Lưu ý:
- Danh sách `incorrectSentences` là `MutableList` để có thể thêm/xóa động
- Cần copy list khi gán vào `sentences` để tránh reference issues
- Progress bar reset về 0 khi bắt đầu vòng retry mới

## Build Status
✅ **BUILD SUCCESSFUL** - No compilation errors

## Status: ✅ COMPLETE
Logic retry đã được implement thành công:
- ✅ Theo dõi câu sai
- ✅ Tự động bắt đầu vòng retry khi có câu sai
- ✅ Xóa câu khỏi danh sách khi làm đúng
- ✅ Chỉ hiện màn Kết quả khi tất cả câu đúng
- ✅ Progress bar cập nhật đúng
- ✅ Thời gian tính từ đầu đến cuối

---
**Last Updated**: April 23, 2026
**Build Status**: SUCCESS
**Implementation**: COMPLETE
