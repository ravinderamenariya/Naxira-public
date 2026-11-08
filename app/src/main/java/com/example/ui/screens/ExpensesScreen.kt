package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Expense
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedSoft
import com.example.ui.theme.NaxiraLavender
import com.example.ui.theme.NaxiraLavenderSoft
import com.example.ui.theme.NaxiraPrimaryPurple
import com.example.ui.theme.NaxiraSecondaryPurple
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberSoft
import com.example.ui.viewmodel.NaxiraViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpensesScreen(
    viewModel: NaxiraViewModel,
    onOpenAddExpenseTrigger: Boolean = false
) {
    val context = LocalContext.current
    val expenses by viewModel.expensesState.collectAsState()
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(onOpenAddExpenseTrigger) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    val categories = listOf("All", "Hosting", "Domain", "Software Subscriptions", "Advertising", "Salaries", "Office", "Internet", "Travel", "Other")

    val filteredExpenses = expenses.filter {
        if (selectedCategoryFilter == "All") true else it.category.equals(selectedCategoryFilter, ignoreCase = true)
    }

    val totalExpensesAmount = expenses.sumOf { it.amount }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("expenses_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Expense Header Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WarningAmberSoft),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Recorded Outlays", fontSize = 12.sp, color = WarningAmber, fontWeight = FontWeight.SemiBold)
                        Text("₹ ${String.format("%,.2f", totalExpensesAmount)}", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = WarningAmber)
                    }
                    Box(
                        modifier = Modifier
                            .background(WarningAmber.copy(alpha = 0.2f), CircleShape)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.MoneyOff, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(24.dp))
                    }
                }
            }

            // Category Filter Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = (selectedCategoryFilter == cat),
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(cat, fontSize = 11.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NaxiraSecondaryPurple,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (filteredExpenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.MoneyOff, contentDescription = null, tint = NaxiraLavender, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No expenses recorded", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            "Track domain, hosting, salary, and advertising costs to calculate true net profit.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredExpenses) { expense ->
                        ExpenseCard(
                            expense = expense,
                            onDelete = { expenseToDelete = expense }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = NaxiraPrimaryPurple,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_expense_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Expense")
        }
    }

    if (showAddDialog) {
        AddExpenseDialog(
            onDismiss = { showAddDialog = false },
            onSave = { expense ->
                viewModel.addExpense(expense)
                showAddDialog = false
                Toast.makeText(context, "Expense logged! Net profit updated.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    expenseToDelete?.let { exp ->
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = { Text("Delete Expense?") },
            text = { Text("Are you sure you want to delete ${exp.title} (₹${exp.amount})?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteExpense(exp.id)
                        expenseToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ExpenseCard(
    expense: Expense,
    onDelete: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(WarningAmberSoft, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MoneyOff, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.title,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${expense.category} • ${expense.expenseDate} ${if (expense.vendor.isNotBlank()) "• ${expense.vendor}" else ""}",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹ ${String.format("%,.2f", expense.amount)}",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DangerRed
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onSave: (Expense) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Software Subscriptions") }
    var paymentMethod by remember { mutableStateOf("Bank Transfer") }
    var vendor by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    val categories = listOf("Hosting", "Domain", "Software Subscriptions", "Advertising", "Salaries", "Office", "Internet", "Travel", "Other")
    val methods = listOf("UPI", "Bank Transfer", "Credit Card", "Cash")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Business Expense", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Expense Title (e.g. AWS Cloud Hosting) *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Amount (₹) *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    Text("Category", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = (category == cat),
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }
                }
                item {
                    Text("Payment Method", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(methods) { m ->
                            FilterChip(
                                selected = (paymentMethod == m),
                                onClick = { paymentMethod = m },
                                label = { Text(m, fontSize = 11.sp) }
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = vendor,
                        onValueChange = { vendor = it },
                        label = { Text("Vendor / Payee (e.g. Hostinger, Adobe)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountInput.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amount > 0) {
                        val expense = Expense(
                            title = title.trim(),
                            amount = amount,
                            category = category,
                            paymentMethod = paymentMethod,
                            vendor = vendor.trim(),
                            notes = notes.trim(),
                            expenseDate = today
                        )
                        onSave(expense)
                    }
                },
                enabled = title.isNotBlank() && (amountInput.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = NaxiraPrimaryPurple)
            ) {
                Text("Log Expense")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
