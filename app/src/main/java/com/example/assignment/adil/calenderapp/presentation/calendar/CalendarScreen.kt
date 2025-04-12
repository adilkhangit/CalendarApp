package com.example.assignment.adil.calenderapp.presentation.calendar

import CustomDatePickerDialog
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.assignment.adil.calenderapp.R
import com.example.assignment.adil.calenderapp.data.api.TaskDetail
import com.example.assignment.adil.calenderapp.data.api.TaskModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun TopBarTitle() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Event,
            contentDescription = "Calendar Icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Calendar",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val userId = 1 // Hardcoded for demo purposes
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var isTaskListMaximized by remember { mutableStateOf(false) }
    val today = remember { LocalDate.now() }
    
    // Track if a swipe is in progress to prevent multiple month changes
    var isSwipeInProgress by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Memoize the tasks for the selected date
    val tasksForSelectedDate by remember(state.tasks, selectedDate) {
        derivedStateOf {
            state.tasks.filter { LocalDate.parse(it.task.date) == selectedDate }
        }
    }

    // Memoize the task map for the calendar grid
    val taskMap by remember(state.tasks) {
        derivedStateOf {
            state.tasks.groupBy { LocalDate.parse(it.task.date) }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onIntent(CalendarIntent.LoadTasks(userId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TopBarTitle()
                },
                actions = {
                    // Sync button
                    OutlinedButton(
                        onClick = {
                            viewModel.onIntent(CalendarIntent.SyncTasks(userId))
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Sync,
                            contentDescription = "Sync",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Sync")
                    }
                    // Add task button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(5.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .clickable { showAddTaskDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Task",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Sync status
            if (state.lastSyncTime != null) {
                Text(
                    text = "Last synced: ${formatSyncTime(state.lastSyncTime?:System.currentTimeMillis())}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Month selector
            MonthSelector(
                selectedYearMonth = state.selectedYearMonth,
                onPreviousMonth = {
                    viewModel.onIntent(
                        CalendarIntent.ChangeMonth(state.selectedYearMonth.minusMonths(1))
                    )
                },
                onNextMonth = {
                    viewModel.onIntent(
                        CalendarIntent.ChangeMonth(state.selectedYearMonth.plusMonths(1))
                    )
                },
                onMonthSelected = { newYearMonth ->
                    viewModel.onIntent(CalendarIntent.ChangeMonth(newYearMonth))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calendar grid with swipe gesture
            if (!isTaskListMaximized) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    isSwipeInProgress = false
                                },
                                onDragCancel = {
                                    isSwipeInProgress = false
                                }
                            ) { _, dragAmount ->
                                if (!isSwipeInProgress) {
                                    when {
                                        dragAmount > 50 -> {
                                            isSwipeInProgress = true
                                            viewModel.onIntent(
                                                CalendarIntent.ChangeMonth(state.selectedYearMonth.minusMonths(1))
                                            )
                                        }
                                        dragAmount < -50 -> {
                                            isSwipeInProgress = true
                                            viewModel.onIntent(
                                                CalendarIntent.ChangeMonth(state.selectedYearMonth.plusMonths(1))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                ) {
                    CalendarGrid(
                        yearMonth = state.selectedYearMonth,
                        taskMap = taskMap,
                        today = today,
                        selectedDate = selectedDate,
                        onDateClick = { date ->
                            selectedDate = date
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Task list section
            TaskListSection(
                tasks = tasksForSelectedDate,
                selectedDate = selectedDate,
                isMaximized = isTaskListMaximized,
                onMaximizeToggle = { isTaskListMaximized = !isTaskListMaximized },
                onDeleteTask = { taskId ->
                    viewModel.onIntent(CalendarIntent.DeleteTask(userId, taskId))
                }
            )
        }

        // Show loading indicator
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Show error message
        state.error?.let { error ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.onIntent(CalendarIntent.LoadTasks(userId))
                                }
                            ) {
                                Text("Retry")
                            }
                            OutlinedButton(
                                onClick = {
                                    viewModel.onIntent(CalendarIntent.DismissError)
                                }
                            ) {
                                Text("Dismiss")
                            }
                        }
                    }
                }
            }
        }

        // Add task dialog
        if (showAddTaskDialog) {
            AddTaskDialog(
                date = selectedDate,
                onDismiss = { showAddTaskDialog = false },
                onTaskAdded = { task ->
                    viewModel.onIntent(CalendarIntent.AddTask(userId, task))
                    showAddTaskDialog = false
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthSelector(
    selectedYearMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMonthSelected: (YearMonth) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Previous Month"
            )
        }

        Text(
            text = selectedYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { showDatePicker = true }
        )

        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Next Month"
            )
        }
    }

    if (showDatePicker) {
        CustomDatePickerDialog(
            selectedYearMonth = selectedYearMonth,
            onDismiss = { showDatePicker = false },
            onDateSelected = { newYearMonth ->
                onMonthSelected(newYearMonth)
                showDatePicker = false
            }
        )
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")

@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    taskMap: Map<LocalDate, List<TaskDetail>>,
    today: LocalDate,
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit
) {
    Column {
        // Weekday headers using BoxWithConstraints
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val cellWidth = maxWidth / 7

            Row {
                // Display days in order: Sunday, Monday, ..., Saturday
                val daysOrdered = DayOfWeek.values().let { days ->
                    // Reorder to have Sunday first (US calendar standard)
                    listOf(days[6]) + days.take(6)
                }

                for (dayOfWeek in daysOrdered) {
                    Box(
                        modifier = Modifier
                            .width(cellWidth)
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar days using fixed width approach
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val cellWidth = maxWidth / 7

            // Calendar calculations - fixed to match US calendar (Sunday first)
            val firstDayOfMonth = yearMonth.atDay(1)

            // Calculate days to subtract based on day of week (Sunday = 0, Monday = 1, etc.)
            // Convert from ISO (Monday = 1) to Sunday = 0 format
            val dayOfWeekValue = when (firstDayOfMonth.dayOfWeek) {
                DayOfWeek.SUNDAY -> 0
                DayOfWeek.MONDAY -> 1
                DayOfWeek.TUESDAY -> 2
                DayOfWeek.WEDNESDAY -> 3
                DayOfWeek.THURSDAY -> 4
                DayOfWeek.FRIDAY -> 5
                DayOfWeek.SATURDAY -> 6
                else -> 0
            }

            val firstDayOfCalendar = firstDayOfMonth.minusDays(dayOfWeekValue.toLong())

            var currentDay = firstDayOfCalendar
            val daysToShow = 42 // 6 weeks * 7 days

            Column {
                repeat(6) { // Always show 6 weeks for consistency
                    Row {
                        repeat(7) { // 7 days per week
                            val date = currentDay
                            val isCurrentMonth = date.month == yearMonth.month
                            val isToday = date.equals(today)
                            val isSelected = date.equals(selectedDate)
                            val hasTask = taskMap.containsKey(date)

                            DayCell(
                                date = date,
                                width = cellWidth,
                                isCurrentMonth = isCurrentMonth,
                                isToday = isToday,
                                isSelected = isSelected,
                                hasTask = hasTask,
                                onDateClick = { onDateClick(date) }
                            )

                            currentDay = currentDay.plusDays(1)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DayCell(
    date: LocalDate,
    width: Dp,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    hasTask: Boolean,
    onDateClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width)
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onDateClick)
            .then(
                if (isSelected) {
                    Modifier.border(
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        RoundedCornerShape(4.dp)
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .then(
                        if (isToday) {
                            Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        isToday -> MaterialTheme.colorScheme.onPrimary
                        !isCurrentMonth -> Color.Gray
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            if (hasTask) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCurrentMonth) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                        )
                )
            }
        }
    }
}


@Composable
fun TaskListSection(
    tasks: List<TaskDetail>,
    selectedDate: LocalDate,
    isMaximized: Boolean,
    onMaximizeToggle: () -> Unit,
    onDeleteTask: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isMaximized) {
                    Modifier.fillMaxHeight()
                } else {
                    Modifier
                }
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tasks for ${selectedDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = onMaximizeToggle) {
                Icon(
                    imageVector = if (isMaximized) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = if (isMaximized) "Minimize" else "Maximize"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tasks for this day",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Text(
                text = "${tasks.size} task${if (tasks.size > 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isMaximized) {
                            Modifier.fillMaxHeight()
                        } else {
                            Modifier
                        }
                    )
            ) {
                items(tasks) { taskDetail ->
                    TaskItem(
                        task = taskDetail.task,
                        onDelete = { onDeleteTask(taskDetail.task_id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: TaskModel,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Due: ${task.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun formatSyncTime(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault())
    return formatter.format(date)
}