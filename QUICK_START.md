# Quick Start Guide - Sentence Management Feature

## Prerequisites
- Android Studio with Kotlin support
- Python 3.8+
- MySQL database (already configured in .env)
- Firebase credentials (already configured)

## Starting the Backend

1. Navigate to backend folder:
```bash
cd Potagp_BE
```

2. Install dependencies (if not already installed):
```bash
pip install -r requirements.txt
```

3. Start the server:
```bash
python run.py
```

Expected output:
```
 * Running on http://0.0.0.0:5000
 * Debug mode: on
```

The backend will be available at `http://localhost:5000`

## Building the Frontend

1. Navigate to frontend folder:
```bash
cd PotagoApp
```

2. Build the project:
```bash
./gradlew build
```

Or for faster builds (debug only):
```bash
./gradlew assembleDebug
```

3. Run on emulator/device:
```bash
./gradlew installDebug
```

## Testing the Feature

### Manual Testing Flow:

1. **Open the app** and navigate to Library → Sentence Patterns
2. **Select a sentence pattern** to view its sentences
3. **Add a new sentence**:
   - Click the green "+" button at the bottom
   - Enter sentence in English
   - Enter meaning in Vietnamese
   - Click "Done"
   - Verify sentence appears in the list

4. **Edit a sentence**:
   - Click "•••" menu on any sentence
   - Select "Chỉnh sửa" (Edit)
   - Modify the sentence or meaning
   - Click "Done"
   - Verify changes are saved

5. **Delete a sentence**:
   - Click "•••" menu on any sentence
   - Select "Xóa" (Delete)
   - Verify sentence is removed from list

6. **Filter sentences**:
   - Click "Tất cả" (All) tab - shows all sentences
   - Click "Chưa thuộc" (Unknown) tab - shows unknown sentences
   - Click "Đã thuộc" (Known) tab - shows known sentences

### API Testing (Optional):

Test backend endpoints directly:

```bash
# Get sentences for pattern ID 1
curl -H "Authorization: Bearer <firebase_token>" \
  http://localhost:5000/api/sentences?pattern_id=1

# Create a sentence
curl -X POST http://localhost:5000/api/sentences \
  -H "Authorization: Bearer <firebase_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "pattern_id": 1,
    "term": "Hello world",
    "definition": "Xin chào thế giới",
    "status": "unknown"
  }'

# Update a sentence
curl -X PUT http://localhost:5000/api/sentences/1 \
  -H "Authorization: Bearer <firebase_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "term": "Hello world",
    "definition": "Xin chào thế giới",
    "status": "known"
  }'

# Delete a sentence
curl -X DELETE http://localhost:5000/api/sentences/1 \
  -H "Authorization: Bearer <firebase_token>"
```

## Troubleshooting

### Backend Issues:

**Error: "Connection refused"**
- Make sure backend is running: `python run.py`
- Check if port 5000 is available

**Error: "User not found"**
- Verify Firebase token is valid
- Check user exists in database

**Error: "Pattern not found"**
- Verify pattern_id exists in database
- Check user has permission to access pattern

### Frontend Issues:

**Error: "Unresolved reference"**
- Run `./gradlew clean build` to rebuild
- Check all imports are correct

**Error: "Network error"**
- Verify backend is running
- Check API endpoint URLs in SentenceApiService
- Verify Firebase token is valid

**Sentences not appearing in list**
- Check network tab in Android Studio
- Verify API response format matches SentenceDto
- Check database has sentences for the pattern

## Key Files to Know

### Backend:
- `Potagp_BE/app/controllers/sentence_controller.py` - API endpoints
- `Potagp_BE/app/services/sentence_service.py` - Business logic
- `Potagp_BE/app/models/sentence.py` - Data model

### Frontend:
- `PotagoApp/app/src/main/java/com/example/potago/presentation/screen/detailsentencepatternscreen/ListOfDetailScreen.kt` - Sentence list UI
- `PotagoApp/app/src/main/java/com/example/potago/presentation/screen/detailsentencepatternscreen/AddSentenceScreen.kt` - Add sentence UI
- `PotagoApp/app/src/main/java/com/example/potago/presentation/screen/detailsentencepatternscreen/EditSentenceScreen.kt` - Edit sentence UI
- `PotagoApp/app/src/main/java/com/example/potago/data/repository/SentenceRepositoryImpl.kt` - API calls

## Environment Variables

Backend uses these environment variables (in `.env`):
```
DB_USER=avnadmin
DB_PASSWORD=AVNS_6PArOANuuzltVGbJ8L8
DB_HOST=mysql-31a68954-congduan2554-95bb.b.aivencloud.com
DB_PORT=17919
DB_NAME=potago
FIREBASE_SERVICE_ACCOUNT_KEY=firebase-service-account.json
```

## Performance Tips

1. **Reduce API calls**: Implement pagination for large sentence lists
2. **Cache data**: Add local caching for recently viewed sentences
3. **Optimize images**: Compress speaker icon and other assets
4. **Lazy loading**: Load sentences on demand instead of all at once

## Next Features to Implement

1. **Search functionality**: Implement search in ListOfDetailScreen
2. **Language selection**: Add language dropdowns to EditSentenceScreen
3. **Pronunciation**: Add text-to-speech for sentences
4. **Batch operations**: Allow bulk delete/update of sentences
5. **Offline support**: Cache sentences locally for offline access
6. **Sync**: Sync local changes when connection is restored

## Support

For issues or questions:
1. Check the IMPLEMENTATION_SUMMARY.md for detailed architecture
2. Review API documentation in Potagp_BE/API_DOCS.md
3. Check Android Studio logcat for error messages
4. Verify database connection and Firebase auth
