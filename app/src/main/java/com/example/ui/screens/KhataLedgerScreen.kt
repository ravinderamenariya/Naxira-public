package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.Client
import com.example.data.model.LedgerEntry
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedSoft
import com.example.ui.theme.NaxiraLavender
import com.example.ui.theme.NaxiraLavenderSoft
import com.example.ui.theme.NaxiraPrimaryPurple
import com.example.ui.theme.NaxiraSecondaryPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenSoft
import com.example.ui.viewmodel.NaxiraViewModel
import com.example.util.pdf.PdfInvoiceService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun KhataLedgerScreen(
    viewModel: NaxiraViewModel,
    initialClientId: String? = null
) {
    val context = LocalContext.current
    val clients by viewModel.clientsState.collectAsState()
    val ledgerEntries by viewModel.ledgerState.collectAsState()
    val profile by viewModel.profileState.collectAsState()

    var selectedClientId by remember { mutableStateOf(initialClientId ?: "") }
    var showAddEntryDialog by remember { mutableStateOf(false) }
    var entryTypeToAdd by remember { mutableStateOf("CREDIT") } // DEBIT (You Gave) vs CREDIT (You Got)

    val selectedClient = clients.find { it.id == selectedClientId }
    val displayEntries = if (selectedClientId.isNotBlank()) {
        ledgerEntries.filter { it.clientId == selectedClientId }
    } else {
        ledgerEntries
    }

    val totalReceivables = clients.sumOf { it.balanceDue.coerceAtLeast(0.0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("khata_screen")
    ) {
        // Receivables Summary Banner
        ElevatedCard(
            shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = NaxiraPrimaryPurple),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = if (selectedClient != null) "${selectedClient.name}'s Ledger" else "Total Agency Khata",
                    color = NaxiraLavender,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val dueAmount = selectedClient?.balanceDue ?: totalReceivables
                        Text(
                            text = "₹ ${String.format("%,.2f", dueAmount)}",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (dueAmount > 0) "Net Receivable (You'll Get)" else "Settled / No Dues",
                            color = if (dueAmount > 0) DangerRedSoft else SuccessGreenSoft,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (selectedClient != null) {
                        OutlinedButton(
                            onClick = {
                                try {
                                    val pdf = PdfInvoiceService.generateKhataStatementPdf(
                                        context,
                                        profile,
                                        selectedClient,
                                        displayEntries
                                    )
                                    PdfInvoiceService.sharePdf(context, pdf, "Khata Statement - ${selectedClient.name}")
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PDF", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Client Selector Horizontal Filter
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text("Select Client Ledger", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    FilterChip(
                        selected = selectedClientId.isBlank(),
                        onClick = { selectedClientId = "" },
                        label = { Text("All Clients (${clients.size})", fontSize = 11.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NaxiraSecondaryPurple,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(clients) { client ->
                    FilterChip(
                        selected = (selectedClientId == client.id),
                        onClick = { selectedClientId = client.id },
                        label = {
                            Text(
                                "${client.name} ${if (client.balanceDue > 0) "• ₹${client.balanceDue.toInt()}" else ""}",
                                fontSize = 11.5.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NaxiraSecondaryPurple,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Khatabook GAVE / GOT Dual Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    entryTypeToAdd = "DEBIT"
                    showAddEntryDialog = true
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("YOU GAVE (DEBIT)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    entryTypeToAdd = "CREDIT"
                    showAddEntryDialog = true
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("YOU GOT (CREDIT)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Ledger Transactions Timeline
        if (displayEntries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = NaxiraLavender,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("No ledger transactions recorded", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        text = "Entries are created automatically when generating invoices and recording payments, or manually via the buttons above.",
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
                items(displayEntries) { entry ->
                    LedgerEntryCard(entry = entry)
                }
            }
        }
    }

    // Add Manual Ledger Entry Modal
    if (showAddEntryDialog) {
        AddLedgerEntryModal(
            entryType = entryTypeToAdd,
            clients = clients,
            preselectedClientId = selectedClientId,
            onDismiss = { showAddEntryDialog = false },
            onSave = { entry ->
                viewModel.addManualLedgerEntry(entry)
                showAddEntryDialog = false
                Toast.makeText(context, "Ledger updated successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun LedgerEntryCard(entry: LedgerEntry) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isDebit = entry.entryType == "DEBIT"
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(if (isDebit) DangerRedSoft else SuccessGreenSoft, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isDebit) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (isDebit) DangerRed else SuccessGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.description.ifBlank { if (isDebit) "Work Delivered" else "Payment Received" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${entry.clientName} • ${entry.entryDate} ${if (entry.paymentMethod.isNotBlank()) "(${entry.paymentMethod})" else ""}",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isDebit) "+" else "-"} ₹ ${String.format("%,.2f", entry.amount)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDebit) DangerRed else SuccessGreen
                )
                Text(
                    text = if (isDebit) "You Gave" else "You Got",
                    fontSize = 10.5.sp,
                    color = if (isDebit) DangerRed else SuccessGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun AddLedgerEntryModal(
    entryType: String,
    clients: List<Client>,
    preselectedClientId: String,
    onDismiss: () -> Unit,
    onSave: (LedgerEntry) -> Unit
) {
    var selectedClientId by remember { mutableStateOf(preselectedClientId.ifBlank { clients.firstOrNull()?.id ?: "" }) }
    var amountInput by remember { mutableStateOf("") }
    var description by remember { mutableStateOf(if (entryType == "DEBIT") "Project Milestone Deliverable" else "Payment Received via UPI") }
    var paymentMode by remember { mutableStateOf(if (entryType == "CREDIT") "UPI" else "Services") }
    var referenceNumber by remember { mutableStateOf("") }
    val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    val isDebit = entryType == "DEBIT"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isDebit) "Record Work / Debit (+ You Gave)" else "Record Payment / Credit (- You Got)",
                fontWeight = FontWeight.Bold,
                color = if (isDebit) DangerRed else SuccessGreen
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select Client", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(clients) { c ->
                        FilterChip(
                            selected = (selectedClientId == c.id),
                            onClick = { selectedClientId = c.id },
                            label = { Text(c.name, fontSize = 11.5.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Amount (₹) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Reason") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = paymentMode,
                    onValueChange = { paymentMode = it },
                    label = { Text("Payment Mode (UPI, Bank, Cash, etc.)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = referenceNumber,
                    onValueChange = { referenceNumber = it },
                    label = { Text("Reference / Bill #") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountInput.toDoubleOrNull() ?: 0.0
                    val client = clients.find { it.id == selectedClientId }
                    if (amount > 0 && client != null) {
                        val entry = LedgerEntry(
                            clientId = client.id,
                            clientName = client.name,
                            entryType = entryType,
                            amount = amount,
                            paymentMethod = paymentMode,
                            referenceNumber = referenceNumber.trim(),
                            description = description.trim(),
                            entryDate = today
                        )
                        onSave(entry)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (isDebit) DangerRed else SuccessGreen)
            ) {
                Text("Save Entry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
