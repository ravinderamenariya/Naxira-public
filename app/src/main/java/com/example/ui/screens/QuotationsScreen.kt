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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Receipt
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
import com.example.data.model.Client
import com.example.data.model.InvoiceItem
import com.example.data.model.Quotation
import com.example.ui.theme.NaxiraLavender
import com.example.ui.theme.NaxiraLavenderSoft
import com.example.ui.theme.NaxiraPrimaryPurple
import com.example.ui.theme.NaxiraSecondaryPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenSoft
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberSoft
import com.example.ui.viewmodel.NaxiraViewModel

@Composable
fun QuotationsScreen(
    viewModel: NaxiraViewModel,
    onNavigateToInvoices: () -> Unit
) {
    val context = LocalContext.current
    val quotations by viewModel.quotationsState.collectAsState()
    val clients by viewModel.clientsState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("quotations_screen")
    ) {
        if (quotations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = NaxiraLavender, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("No quotations created", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        "Draft professional service proposals with validities and convert them directly to invoices when accepted.",
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(quotations) { quote ->
                    QuotationCard(
                        quotation = quote,
                        onConvert = {
                            viewModel.convertQuotationToInvoice(quote)
                            Toast.makeText(context, "Quotation converted to live Invoice!", Toast.LENGTH_SHORT).show()
                            onNavigateToInvoices()
                        }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = NaxiraPrimaryPurple,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Quote")
        }
    }

    if (showCreateDialog) {
        CreateQuotationDialog(
            clients = clients,
            viewModel = viewModel,
            onDismiss = { showCreateDialog = false },
            onCreated = { quote ->
                viewModel.createQuotation(quote)
                showCreateDialog = false
                Toast.makeText(context, "Quotation generated!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun QuotationCard(
    quotation: Quotation,
    onConvert: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = quotation.quoteNumber,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NaxiraPrimaryPurple
                )
                val (statusBg, statusFg) = when (quotation.status) {
                    "Converted" -> SuccessGreenSoft to SuccessGreen
                    "Accepted" -> SuccessGreenSoft to SuccessGreen
                    else -> WarningAmberSoft to WarningAmber
                }
                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = quotation.status, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = statusFg)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = quotation.clientName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            if (quotation.validUntil.isNotBlank()) {
                Text("Valid until: ${quotation.validUntil}", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Quote Total (₹)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹ ${String.format("%,.2f", quotation.grandTotal)}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = NaxiraPrimaryPurple)
                }

                if (quotation.status != "Converted") {
                    Button(
                        onClick = onConvert,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Convert to Invoice", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateQuotationDialog(
    clients: List<Client>,
    viewModel: NaxiraViewModel,
    onDismiss: () -> Unit,
    onCreated: (Quotation) -> Unit
) {
    var selectedClientId by remember { mutableStateOf(clients.firstOrNull()?.id ?: "") }
    var serviceTitle by remember { mutableStateOf("Full Branding & Website Package") }
    var amountInput by remember { mutableStateOf("34999") }
    var validDays by remember { mutableStateOf("30") }
    var quoteNumber by remember { mutableStateOf(viewModel.getNextQuoteNumber()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate Proposal / Quotation", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select Client", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(clients) { c ->
                        FilterChip(
                            selected = (selectedClientId == c.id),
                            onClick = { selectedClientId = c.id },
                            label = { Text(c.name, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = serviceTitle,
                    onValueChange = { serviceTitle = it },
                    label = { Text("Package / Service Name *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Base Price (₹) *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = validDays,
                        onValueChange = { validDays = it },
                        label = { Text("Validity (Days)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val base = amountInput.toDoubleOrNull() ?: 0.0
                    val client = clients.find { it.id == selectedClientId }
                    val tax = base * 0.18
                    val total = base + tax
                    val quote = Quotation(
                        quoteNumber = quoteNumber,
                        clientId = selectedClientId,
                        clientName = client?.name ?: "Client",
                        items = listOf(
                            InvoiceItem(
                                id = "1",
                                description = serviceTitle,
                                quantity = 1.0,
                                unitPrice = base,
                                taxRate = 18.0,
                                totalAmount = base
                            )
                        ),
                        subtotal = base,
                        taxAmount = tax,
                        grandTotal = total,
                        validUntil = "30 Days from issue",
                        status = "Sent"
                    )
                    onCreated(quote)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NaxiraPrimaryPurple)
            ) {
                Text("Create Quotation")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
