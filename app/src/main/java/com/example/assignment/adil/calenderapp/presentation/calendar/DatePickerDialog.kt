import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    selectedYearMonth: YearMonth,
    onDismiss: () -> Unit,
    onDateSelected: (YearMonth) -> Unit
) {
    val monthList = remember { java.time.Month.values().toList() }
    val yearList = remember { (selectedYearMonth.year - 50..selectedYearMonth.year + 50).toList() }

    var selectedMonthIndex by remember { mutableStateOf(selectedYearMonth.monthValue - 1) }
    var selectedYearIndex by remember { mutableStateOf(50) } // current year is at center of the list

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Date",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Month Picker Wheel
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Month", style = MaterialTheme.typography.bodyLarge)
                        ScrollablePicker(
                            items = monthList.map { it.getDisplayName(TextStyle.FULL, Locale.getDefault()) },
                            selectedIndex = selectedMonthIndex,
                            onItemSelected = { selectedMonthIndex = it },
                            modifier = Modifier.height(150.dp).width(100.dp)
                        )
                    }

                    // Year Picker Wheel
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Year", style = MaterialTheme.typography.bodyLarge)
                        ScrollablePicker(
                            items = yearList.map { it.toString() },
                            selectedIndex = selectedYearIndex,
                            onItemSelected = { selectedYearIndex = it },
                            modifier = Modifier.height(150.dp).width(100.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(
                        YearMonth.of(
                            yearList[selectedYearIndex],
                            monthList[selectedMonthIndex]
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ScrollablePicker(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(items) { index, item ->
            Text(
                text = item,
                style = if (index == selectedIndex) MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                else MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { onItemSelected(index) }
            )
        }
    }
}
