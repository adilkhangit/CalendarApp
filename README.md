# Calendar App

A modern Android calendar application built with Jetpack Compose and following clean architecture principles. The app allows users to manage their tasks with local storage and server synchronization capabilities, implementing an offline-first approach.

## Screenshots
![Untitled design](https://github.com/user-attachments/assets/f228168d-06b6-4d69-a19a-6ef29955dbd7)

## Features

### Calendar View
- Monthly calendar view with task indicators
- Intuitive navigation between months
- Year and month picker with side-by-side dropdowns
- Current day highlighting
- Task count display for each day

### Task Management
- Add new tasks with title, description, and date
- Delete existing tasks
- View tasks for selected dates
- Task list with expandable/collapsible view
- Real-time task updates

### Offline-First Architecture
- Local-first data storage
- Automatic background synchronization
- Conflict resolution strategy
- Queue-based sync operations
- Network state awareness
- Data consistency maintenance
- Optimistic UI updates

### Sync Functionality
- Background synchronization with server
- Offline support with local storage
- Progress tracking during sync
- Ability to cancel sync process
- Visual feedback for sync status
- Automatic retry mechanism
- Sync queue management

### UI/UX
- Material Design 3 implementation
- Dark/Light theme support
- Smooth animations and transitions
- Responsive layout
- User-friendly error handling

## Technical Stack

### Architecture
- Clean Architecture
- MVVM and MVI pattern
- Repository pattern
- Single Activity architecture
- Dependency Injection with Hilt
- Offline-First design pattern

### Libraries & Technologies
- **Jetpack Compose**: UI toolkit
- **Room**: Local database
- **Kotlin Coroutines**: Asynchronous programming
- **Flow**: Reactive programming
- **Hilt**: Dependency injection
- **WorkManager**: Background tasks
- **Material3**: Design system
- **ViewModel**: UI state management

### Database Schema
- Task Entity with fields:
  - ID (Primary Key)
  - User ID
  - Title
  - Description
  - Date
  - Sync Status
  - Remote ID
  - Mark for Deletion
  - Last Modified

## Project Structure

```
app/
├── data/
│   ├── api/         # API service interfaces and models
│   ├── local/       # Room database and DAOs
│   └── repository/  # Repository implementations
├── di/              # Dependency injection modules
├── domain/
│   ├── model/       # Domain models
│   └── repository/  # Repository interfaces
├── presentation/
│   ├── calendar/    # Calendar screen and components
│   └── splash/      # Splash screen
├── ui/
│   └── theme/       # Theme configuration
└── worker/          # Background workers for sync
```

## Key Components

### Calendar Screen
- Monthly calendar grid
- Task list section
- Date selection
- Navigation controls
- Sync status indicator

### Task Management
- Task creation dialog
- Task list display
- Task deletion functionality
- Task synchronization

### Offline-First Implementation
- Local Database Operations
  - Immediate local storage
  - Optimistic UI updates
  - Conflict detection
  - Sync status tracking
- Sync Queue Management
  - Operation queuing
  - Priority-based execution
  - Retry mechanism
  - Error handling
- Network State Management
  - Connection monitoring
  - Automatic sync triggers
  - Background sync scheduling
  - Battery optimization

### Data Synchronization
- Background sync worker
- Progress tracking
- Error handling
- Network state management

## Implementation Details

### Local Storage
- Room database for task persistence
- Reactive queries with Flow
- Efficient data access patterns
- Optimistic locking
- Conflict resolution

### Offline-First Strategy
1. **Data Operations**
   - All operations first saved locally
   - Immediate UI feedback
   - Sync status tracking
   - Conflict resolution

2. **Sync Process**
   - Background sync worker
   - Queue-based operation processing
   - Automatic retry on failure
   - Network state awareness

3. **Conflict Resolution**
   - Last-write-wins strategy

### Remote Sync
- WorkManager for background sync
- Progress updates
- Error handling
- Queue management

### UI Components
- Custom calendar grid
- Date picker dialog
- Loading indicators
- Error messages
- Sync status indicators

## Future Improvements

1. ETAG Support and Pagination
2. Multi-user support
3. Task categories and tags
4. Task reminders and notifications
5. Calendar view customization
6. Task sharing capabilities
7. Advanced sync options
8. Calendar export/import
9. Widget support
10. Enhanced conflict resolution
11. Offline analytics
12. Data compression
13. Batch operations

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on emulator or device

## Requirements

- Android Studio Arctic Fox or newer
- Minimum SDK: 28 (Android 9.0)
- Target SDK: 34 (Android 14)
- Kotlin 1.9.0 or newer

## Contributing

Feel free to submit issues and enhancement requests.
