# Tính năng Luyện viết (Writing Practice)

## Tổng quan
Đã tạo màn hình Luyện viết cho phép người dùng luyện tập viết lại câu tiếng Anh dựa trên nghĩa tiếng Việt.

## Thiết kế
Màn hình được thiết kế dựa trên Figma design với các thành phần:
- Header với nút back và tiêu đề "Luyện viết"
- Progress bar hiển thị tiến độ (32% = 1/3 câu)
- Tiêu đề "Viết lại câu có nghĩa như sau"
- Mascot (con chim xanh) với speech bubble hiển thị câu tiếng Việt
- Text input area lớn để người dùng nhập câu trả lời
- Nút "KIỂM TRA" ở dưới cùng (disabled khi chưa nhập)

## Files Created/Modified

### 1. WritingPracticeScreen.kt (NEW)
**Path**: `PotagoApp/app/src/main/java/com/example/potago/presentation/screen/writingpracticescreen/WritingPracticeScreen.kt`

**Chức năng:**
- Hiển thị giao diện luyện viết
- Progress bar với animation
- Mascot với speech bubble
- Text input area với placeholder
- Nút KIỂM TRA (enabled/disabled based on input)

**Props:**
- `navController: NavController` - Điều hướng
- `patternId: Int` - ID của sentence pattern

**Mock Data (sẽ thay bằng API):**
- `currentSentence` - Câu tiếng Việt cần dịch
- `progress` - Tiến độ hoàn thành (0.0 - 1.0)

### 2. Navigation.kt (UPDATED)
**Thay đổi:**
- Thêm `Screen.WritingPractice` route
- Thêm import `WritingPracticeScreen`
- Đăng ký composable route với patternId parameter

### 3. DetailSentencePatternScreen.kt (UPDATED)
**Thay đổi:**
- Cập nhật `StudyModeItem` để nhận `onClick` callback
- Thêm onClick cho "Luyện viết" button
- Navigate đến `Screen.WritingPractice(patternId)`

## Navigation Flow

```
Library Screen
    ↓
Sentence Pattern List
    ↓
Detail Sentence Pattern Screen
    ↓ (Click "Luyện viết")
Writing Practice Screen
```

## UI Components

### Header
- Back button (ic_back)
- Title: "Luyện viết" (32sp, ExtraBold)
- Shadow elevation: 2dp

### Progress Bar
- Background: #E5E7EB (gray)
- Fill: #58CC02 (green)
- Height: 13dp
- Rounded corners (9999.dp)
- Progress: 32% (mock data)

### Instruction Text
- "Viết lại câu có nghĩa như sau"
- Font: 18sp, ExtraBold
- Color: rgba(0,0,0,0.8)
- Center aligned

### Mascot & Speech Bubble
- Mascot: ic_teaching_mascot (86x121dp)
- Speech bubble:
  - Background: White
  - Border: 1dp #E5E7EB
  - Rounded corners (0, 16, 16, 16)
  - Shadow: 2dp
  - Text: Vietnamese sentence (16sp, Bold, #696969)

### Text Input Area
- Size: 246dp height
- Border: 2dp rgba(0,0,0,0.1)
- Rounded corners: 15dp
- Placeholder: "Nhập ở đây ..." (rgba(0,0,0,0.2))
- Text style: 16sp, Bold, Black

### Check Button
- Text: "KIỂM TRA" (18sp, ExtraBold, White)
- Background: #58CC02 (enabled) / rgba(88,204,2,0.5) (disabled)
- Border: 3dp #46A302 (enabled) / rgba(70,163,2,0.5) (disabled)
- Height: 56dp
- Rounded corners: 16dp
- Position: Bottom center with padding

## State Management

### Current State (Mock)
```kotlin
val currentSentence = "Ga gần nhất ở đâu?"
val progress = 0.32f // 32%
val (userAnswer, setUserAnswer) = remember { mutableStateOf("") }
```

### Future Implementation (với API)
Sẽ cần tạo ViewModel với:
- Load sentences từ pattern
- Track progress
- Validate answer
- Update progress
- Navigate to next sentence

## API Integration (TODO)

### Endpoints cần dùng:
1. **GET /api/sentences?pattern_id={id}**
   - Lấy danh sách câu trong pattern
   - Filter: status = "unknown" (chỉ lấy câu chưa thuộc)

2. **PUT /api/sentences/{id}**
   - Cập nhật status khi trả lời đúng
   - Update mistakes count

### Data Flow:
```
1. Load sentences by pattern_id
2. Filter sentences with status = "unknown"
3. Show first sentence (definition field)
4. User types answer
5. Click "KIỂM TRA"
6. Compare with term field
7. If correct:
   - Update status to "known"
   - Show next sentence
   - Update progress
8. If incorrect:
   - Increment mistakes
   - Show hint or correct answer
   - Allow retry
```

## Validation Logic (TODO)

### Answer Checking:
```kotlin
fun checkAnswer(userAnswer: String, correctAnswer: String): Boolean {
    // Normalize strings
    val normalized1 = userAnswer.trim().lowercase()
    val normalized2 = correctAnswer.trim().lowercase()
    
    // Exact match
    if (normalized1 == normalized2) return true
    
    // Allow minor differences (punctuation, etc)
    val cleaned1 = normalized1.replace(Regex("[^a-z0-9\\s]"), "")
    val cleaned2 = normalized2.replace(Regex("[^a-z0-9\\s]"), "")
    
    return cleaned1 == cleaned2
}
```

## Future Enhancements

### 1. Hint System
- Show first letter
- Show word count
- Show partial answer

### 2. Feedback
- Correct answer animation (green checkmark)
- Incorrect answer animation (red X)
- Show correct answer when wrong

### 3. Progress Tracking
- Save progress to database
- Resume from last position
- Show completion percentage

### 4. Gamification
- XP points for correct answers
- Streak counter
- Leaderboard

### 5. Multiple Choice Mode
- Show 4 options
- Easier than typing
- Good for beginners

## Testing

### Manual Test Steps:
1. Navigate to Library → Sentence Patterns
2. Select a pattern
3. Click "Luyện viết" button
4. Verify screen loads with:
   - Progress bar
   - Mascot with Vietnamese sentence
   - Empty text input
   - Disabled "KIỂM TRA" button
5. Type some text
6. Verify "KIỂM TRA" button becomes enabled
7. Click back button
8. Verify returns to Detail screen

### Test Cases (với API):
- [ ] Load sentences correctly
- [ ] Show only "unknown" sentences
- [ ] Progress bar updates correctly
- [ ] Answer validation works
- [ ] Status updates on correct answer
- [ ] Mistakes increment on wrong answer
- [ ] Navigate to next sentence
- [ ] Complete all sentences
- [ ] Handle empty sentence list

## Build Status
✅ **BUILD SUCCESSFUL**

## Screenshots
Màn hình bao gồm:
- Header với back button và title
- Progress bar (green)
- Instruction text
- Mascot với speech bubble
- Large text input area
- "KIỂM TRA" button ở dưới

## Next Steps
1. Tạo `WritingPracticeViewModel`
2. Implement API integration
3. Add answer validation logic
4. Add feedback animations
5. Add progress tracking
6. Test với real data
