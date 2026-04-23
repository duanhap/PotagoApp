# Writing Practice - Continue Feature ✅

## Yêu cầu
Khi chọn vào một mẫu câu trước đó đang làm dở trong Luyện viết, sẽ hiện popup "Bạn có muốn làm tiếp không?":
- **Đồng ý** → Làm tiếp từ vị trí đã lưu
- **Không** → Làm lại từ đầu

## Implementation

### 1. **WritingPracticeDataStore**
Tạo DataStore mới để lưu tiến độ:

```kotlin
data class WritingPracticeProgress(
    val patternId: Int,
    val currentIndex: Int,
    val completedSentenceIds: List<Int>, // Câu đã làm đúng
    val incorrectSentenceIds: List<Int>, // Câu sai
    val isRetryRound: Boolean,
    val startTime: Long,
    val lastUpdated: Long
)
```

**Methods:**
- `saveProgress(progress)` - Lưu tiến độ
- `getProgress(patternId)` - Lấy tiến độ theo patternId
- `clearProgress(patternId)` - Xóa tiến độ
- `clearAllProgress()` - Xóa tất cả tiến độ

### 2. **WritingPracticeViewModel Updates**

#### Thêm fields vào UiState:
```kotlin
val completedSentenceIds: MutableList<Int> = mutableListOf()
val showContinueDialog: Boolean = false
val savedProgress: WritingPracticeProgress? = null
```

#### Luồng hoạt động:

**A. Khi load sentences:**
```kotlin
fun loadSentences(patternId: Int) {
    // 1. Kiểm tra có progress đã lưu không
    val savedProgress = dataStore.getProgress(patternId).firstOrNull()
    
    if (savedProgress != null && savedProgress.currentIndex > 0) {
        // 2. Có progress -> hiện dialog
        showContinueDialog = true
    } else {
        // 3. Không có progress -> load mới
        loadNewSession(patternId)
    }
}
```

**B. User chọn "Làm tiếp":**
```kotlin
fun continueFromSaved() {
    // 1. Load tất cả câu
    val allSentences = getSentencesByPatternUseCase(patternId)
    
    // 2. Lọc câu dựa trên progress
    val remainingSentences = if (savedProgress.isRetryRound) {
        // Đang retry -> chỉ lấy câu sai
        allSentences.filter { it.id in savedProgress.incorrectSentenceIds }
    } else {
        // Vòng đầu -> lấy tất cả
        allSentences
    }
    
    // 3. Restore state
    currentIndex = savedProgress.currentIndex
    incorrectSentences = savedProgress.incorrectSentenceIds
    completedSentenceIds = savedProgress.completedSentenceIds
    isRetryRound = savedProgress.isRetryRound
    startTime = savedProgress.startTime
}
```

**C. User chọn "Làm lại từ đầu":**
```kotlin
fun startNewSession(patternId: Int) {
    // 1. Xóa progress cũ
    dataStore.clearProgress(patternId)
    
    // 2. Load mới
    loadNewSession(patternId)
}
```

**D. Lưu progress tự động:**
```kotlin
// Sau mỗi câu trả lời
fun checkAnswer(userAnswer: String) {
    // ... logic check answer
    saveProgress() // Lưu ngay
}

// Khi chuyển câu
fun moveToNextSentence() {
    // ... logic move
    saveProgress() // Lưu ngay
}

// Khi hoàn thành
fun moveToNextSentence() {
    if (isCompleted) {
        clearProgress() // Xóa progress
    }
}
```

### 3. **WritingPracticeScreen Updates**

#### Thêm Dialog:
```kotlin
if (uiState.showContinueDialog) {
    AlertDialog(
        onDismissRequest = { /* Không cho dismiss */ },
        title = { Text("Làm tiếp?") },
        text = { Text("Bạn có muốn tiếp tục từ lần trước không?") },
        confirmButton = {
            Button(onClick = { viewModel.continueFromSaved() }) {
                Text("Làm tiếp")
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.startNewSession(patternId) }) {
                Text("Làm lại từ đầu")
            }
        }
    )
}
```

## Luồng hoạt động chi tiết

### Trường hợp 1: Lần đầu làm
```
User mở Writing Practice
    ↓
loadSentences(patternId)
    ↓
Kiểm tra progress → Không có
    ↓
loadNewSession()
    ↓
Bắt đầu làm từ câu 1
```

### Trường hợp 2: Làm dở, quay lại làm tiếp
```
User mở Writing Practice (đã làm dở trước đó)
    ↓
loadSentences(patternId)
    ↓
Kiểm tra progress → Có (currentIndex = 3)
    ↓
Hiện dialog "Làm tiếp?"
    ↓
User chọn "Làm tiếp"
    ↓
continueFromSaved()
    ↓
Restore state: currentIndex = 3, incorrectSentences, etc.
    ↓
Tiếp tục từ câu 4
```

### Trường hợp 3: Làm dở, chọn làm lại
```
User mở Writing Practice (đã làm dở trước đó)
    ↓
loadSentences(patternId)
    ↓
Kiểm tra progress → Có
    ↓
Hiện dialog "Làm tiếp?"
    ↓
User chọn "Làm lại từ đầu"
    ↓
startNewSession()
    ↓
Xóa progress cũ
    ↓
loadNewSession()
    ↓
Bắt đầu từ câu 1
```

### Trường hợp 4: Hoàn thành
```
User làm xong tất cả câu
    ↓
moveToNextSentence() → isCompleted = true
    ↓
clearProgress() → Xóa progress
    ↓
Hiện màn Kết quả
    ↓
Lần sau mở lại → Không có progress → Làm mới
```

## Khi nào progress được lưu?

1. **Sau mỗi câu trả lời** (đúng hoặc sai)
2. **Khi chuyển sang câu tiếp theo**
3. **Khi bắt đầu vòng retry**

## Khi nào progress bị xóa?

1. **Khi hoàn thành tất cả câu**
2. **Khi user chọn "Làm lại từ đầu"**
3. **Khi user làm xong và claim rewards**

## Data được lưu

```kotlin
WritingPracticeProgress(
    patternId = 123,                    // ID mẫu câu
    currentIndex = 3,                   // Đang ở câu thứ 4
    completedSentenceIds = [1, 2, 4],   // Đã làm đúng câu 1, 2, 4
    incorrectSentenceIds = [3, 5],      // Làm sai câu 3, 5
    isRetryRound = false,               // Đang vòng đầu
    startTime = 1714000000000,          // Thời gian bắt đầu
    lastUpdated = 1714000123000         // Lần cập nhật cuối
)
```

## UI/UX

### Dialog Design:
```
┌─────────────────────────────────┐
│         Làm tiếp?               │
├─────────────────────────────────┤
│                                 │
│  Bạn có muốn tiếp tục từ lần   │
│  trước không?                   │
│                                 │
├─────────────────────────────────┤
│  [Làm lại từ đầu]  [Làm tiếp]  │
└─────────────────────────────────┘
```

- **Màu nút "Làm tiếp"**: Xanh lá (#58CC02)
- **Màu nút "Làm lại từ đầu"**: Text xanh lá
- **Không cho dismiss** bằng cách nhấn ngoài dialog

## Testing Checklist

### Test Case 1: Lần đầu làm
- [ ] Mở Writing Practice lần đầu
- [ ] Không hiện dialog
- [ ] Bắt đầu từ câu 1

### Test Case 2: Làm dở, thoát, quay lại làm tiếp
- [ ] Làm 3/5 câu
- [ ] Thoát ra
- [ ] Mở lại → Hiện dialog
- [ ] Chọn "Làm tiếp"
- [ ] Tiếp tục từ câu 4

### Test Case 3: Làm dở, thoát, chọn làm lại
- [ ] Làm 3/5 câu
- [ ] Thoát ra
- [ ] Mở lại → Hiện dialog
- [ ] Chọn "Làm lại từ đầu"
- [ ] Bắt đầu từ câu 1

### Test Case 4: Làm xong, progress bị xóa
- [ ] Làm hết 5 câu
- [ ] Hiện màn Kết quả
- [ ] Thoát ra
- [ ] Mở lại → Không hiện dialog
- [ ] Bắt đầu từ câu 1

### Test Case 5: Làm dở ở vòng retry
- [ ] Vòng 1: Làm 5 câu, 2 câu sai
- [ ] Vòng 2: Làm 1/2 câu sai
- [ ] Thoát ra
- [ ] Mở lại → Hiện dialog
- [ ] Chọn "Làm tiếp"
- [ ] Tiếp tục vòng retry từ câu thứ 2

### Test Case 6: Nhiều pattern khác nhau
- [ ] Pattern A: Làm dở 3/5 câu
- [ ] Chuyển sang Pattern B: Làm mới
- [ ] Quay lại Pattern A → Hiện dialog
- [ ] Chọn "Làm tiếp" → Tiếp tục từ câu 4

## Files Created/Modified

### Created:
1. `WritingPracticeDataStore.kt` - DataStore để lưu progress

### Modified:
1. `WritingPracticeViewModel.kt`
   - Thêm `dataStore` injection
   - Thêm `loadSentences()` với logic kiểm tra progress
   - Thêm `continueFromSaved()`
   - Thêm `startNewSession()`
   - Thêm `saveProgress()`
   - Thêm `clearProgress()`
   - Cập nhật `checkAnswer()` để lưu progress
   - Cập nhật `moveToNextSentence()` để lưu/xóa progress

2. `WritingPracticeScreen.kt`
   - Thêm AlertDialog cho "Làm tiếp?"

3. `StorageModule.kt`
   - Thêm `provideWritingPracticeDataStore()`

## Build Status
✅ **BUILD SUCCESSFUL** - No compilation errors

## Status: ✅ COMPLETE
Feature "Làm tiếp" đã được implement thành công:
- ✅ Lưu progress tự động
- ✅ Kiểm tra progress khi mở lại
- ✅ Hiện dialog xác nhận
- ✅ Làm tiếp từ vị trí đã lưu
- ✅ Làm lại từ đầu
- ✅ Xóa progress khi hoàn thành
- ✅ Hỗ trợ cả vòng retry

---
**Last Updated**: April 23, 2026
**Build Status**: SUCCESS
**Implementation**: COMPLETE
