# Edit Card Screen Implementation

## Overview
This screen allows users to edit flashcard details including term, definition, and description.

## Files Created
1. **EditCardScreen.kt** - Main composable UI implementation
2. **EditCardViewModel.kt** - ViewModel for state management
3. **ic_check.xml** - Checkmark icon for the save button

## Design Implementation
The implementation follows the Figma design specifications:
- **Top Bar**: White background with shadow, back button, and "Chỉnh sửa thẻ" title
- **Text Fields**: Three underlined text fields for:
  - Thuật ngữ (Term) - single line
  - Định nghĩa (Definition) - single line
  - Mô tả chi tiết (Description) - multi-line
- **Bottom Bar**: Floating save button with blue circular background and checkmark icon

## Typography
- **Title**: Nunito ExtraBold, 32sp, line height 24sp
- **Field Text**: Nunito Bold, 16sp, line height 24sp
- **Field Labels**: Nunito ExtraBold, 12sp, line height 20sp

## Colors
- Background: White (#FFFFFF)
- Top Bar: White with 90% opacity (#E6FFFFFF)
- Text: Black with 70% opacity (#B3000000)
- Dividers: Black (#000000)
- Save Button: Blue (#3B82F6)

## Architecture
- **MVVM Pattern**: Follows the project's architecture
- **Hilt Dependency Injection**: ViewModel is annotated with @HiltViewModel
- **State Management**: Uses StateFlow for reactive UI updates
- **Navigation**: Integrates with NavController for screen navigation

## TODO
The ViewModel contains placeholder code for repository integration:
1. Inject the appropriate repository (WordSetRepository or similar)
2. Implement `loadCard()` to fetch card data from the backend
3. Implement `saveCard()` to persist changes to the backend
4. Add proper error handling and loading states

## Usage
To navigate to this screen, add the route to your navigation graph:
```kotlin
composable(
    route = "editCard/{cardId}",
    arguments = listOf(navArgument("cardId") { type = NavType.LongType })
) { backStackEntry ->
    val cardId = backStackEntry.arguments?.getLong("cardId") ?: 0L
    EditCardScreen(
        navController = navController,
        cardId = cardId
    )
}
```

## Preview
A preview composable is included for development and testing purposes.
