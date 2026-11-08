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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.Invoice
import com.example.data.model.InvoiceItem
import com.example.data.model.Payment
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedSoft
import com.example.ui.theme.NaxiraLavender
import com.example.ui.theme.NaxiraLavenderSoft
import com.example.ui.theme.NaxiraPrimaryPurple
import com.example.ui.theme.NaxiraSecondaryPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenSoft
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberSoft
import com.example.ui.viewmodel.NaxiraViewModel
import com.example.util.pdf.PdfInvoiceService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvoicesScreen(
    viewModel: NaxiraViewModel,
    initialClientId: String? = null,
    onOpenCreateInvoiceTrigger: Boolean = false
) {
    val context = LocalContext.current
    val invoices by viewModel.invoicesState.collectAsState()
    val clients by viewModel.clientsState.collectAsState()
    val profile by viewModel.profileState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") }
    var showCreateDialog by remember { mutableStateOf(onOpenCreateInvoiceTrigger) }
    var invoiceForPayment by remember { mutableStateOf<Invoice?>(null) }

    val statusOptions = listOf("All", "Sent", "Partially Paid", "Paid", "Draft", "Overdue")

    val filteredInvoices = invoices.filter { inv ->
        val matchesClient = if (initialClientId.isNullOrBlank()) true else inv.clientId == initialClientId
        val matchesSearch = inv.invoiceNumber.contains(searchQuery, ignoreCase = true) ||
                inv.clientName.contains(searchQuery, ignoreCase = true)
        val matchesStatus = if (selectedStatusFilter == "All") true else inv.status.equals(selectedStatusFilter, ignoreCase = true)
        matchesClient && matchesSearch && matchesStatus
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("invoices_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("invoice_search_input"),
                placeholder = { Text("Search invoice #, client name...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            // Status Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statusOptions) { status ->
                    FilterChip(
                        selected = (selectedStatusFilter == status),
                        onClick = { selectedStatusFilter = status },
                        label = { Text(status, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NaxiraSecondaryPurple,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (filteredInvoices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = NaxiraLavender,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No invoices found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Create professional GST and Non-GST invoices using the + button.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredInvoices) { invoice ->
                        InvoiceCard(
                            invoice = invoice,
                            onSharePdf = {
                                try {
                                    val pdfFile = PdfInvoiceService.generateInvoicePdf(context, profile, invoice)
                                    PdfInvoiceService.sharePdf(context, pdfFile, "Invoice #${invoice.invoiceNumber}")
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error creating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onRecordPayment = { invoiceForPayment = invoice }
                        )
                    }
                }
            }
        }

        // Create Invoice FAB
        FloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = NaxiraPrimaryPurple,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("create_invoice_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Create Invoice")
        }
    }

    // Create Invoice Modal Dialog
    if (showCreateDialog) {
        CreateInvoiceDialog(
            viewModel = viewModel,
            onDismiss = { showCreateDialog = false },
            onCreated = { showCreateDialog = false }
        )
    }

    // Record Payment Dialog
    invoiceForPayment?.let { invoice ->
        RecordPaymentDialog(
            invoice = invoice,
            onDismiss = { invoiceForPayment = null },
            onPaymentRecorded = { payment ->
                viewModel.recordPayment(payment)
                invoiceForPayment = null
                Toast.makeText(context, "Payment recorded! Client ledger updated.", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun InvoiceCard(
    invoice: Invoice,
    onSharePdf: () -> Unit,
    onRecordPayment: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Number, Date, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = invoice.invoiceNumber,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = NaxiraPrimaryPurple
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${invoice.invoiceDate}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val (statusBg, statusFg) = when (invoice.status) {
                    "Paid" -> SuccessGreenSoft to SuccessGreen
                    "Partially Paid" -> WarningAmberSoft to WarningAmber
                    "Overdue" -> DangerRedSoft to DangerRed
                    else -> NaxiraLavenderSoft to NaxiraPrimaryPurple
                }
                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = invoice.status.uppercase(),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusFg
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Client Name & Details
            Text(
                text = invoice.clientName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (invoice.dueDate.isNotBlank()) {
                Text(
                    text = "Due: ${invoice.dueDate}",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Amounts Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total (₹)", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹ ${String.format("%,.2f", invoice.grandTotal)}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Paid (₹)", fontSize = 10.5.sp, color = SuccessGreen)
                    Text("₹ ${String.format("%,.2f", invoice.amountPaid)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Balance Due", fontSize = 10.5.sp, color = if (invoice.balanceDue > 0) DangerRed else SuccessGreen)
                    Text(
                        text = "₹ ${String.format("%,.2f", invoice.balanceDue)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (invoice.balanceDue > 0) DangerRed else SuccessGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions: PDF Share & Record Payment
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSharePdf,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp), tint = DangerRed)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PDF / Share", fontSize = 12.sp)
                }

                if (invoice.balanceDue > 0.01) {
                    Button(
                        onClick = onRecordPayment,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pay Due", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateInvoiceDialog(
    viewModel: NaxiraViewModel,
    onDismiss: () -> Unit,
    onCreated: () -> Unit
) {
    val clients by viewModel.clientsState.collectAsState()
    val profile by viewModel.profileState.collectAsState()

    var selectedClientId by remember { mutableStateOf(clients.firstOrNull()?.id ?: "") }
    var customClientName by remember { mutableStateOf("") }
    var invoiceNumber by remember { mutableStateOf(viewModel.getNextInvoiceNumber(profile.invoicePrefix)) }
    val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    var invoiceDate by remember { mutableStateOf(today) }
    var dueDate by remember { mutableStateOf(today) }
    var taxType by remember { mutableStateOf("CGST_SGST") } // CGST_SGST, IGST, NONE
    var discountInput by remember { mutableStateOf("0") }
    var paymentTerms by remember { mutableStateOf("Payment due within 15 days via UPI/NEFT") }

    val lineItems = remember {
        mutableStateListOf(
            InvoiceItem(id = "1", description = "Website Development & Setup", hsnSac = "998311", quantity = 1.0, unitPrice = 14999.0, taxRate = 18.0, totalAmount = 14999.0)
        )
    }

    // Dynamic calculations
    val subtotal = lineItems.sumOf { it.quantity * it.unitPrice }
    val discount = discountInput.toDoubleOrNull() ?: 0.0
    val taxableAmount = (subtotal - discount).coerceAtLeast(0.0)
    val totalTax = when (taxType) {
        "CGST_SGST", "IGST" -> taxableAmount * 0.18
        else -> 0.0
    }
    val grandTotal = taxableAmount + totalTax

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate Indian GST Invoice", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("Select Agency Client", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    if (clients.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(clients) { c ->
                                FilterChip(
                                    selected = (selectedClientId == c.id),
                                    onClick = { selectedClientId = c.id },
                                    label = { Text(c.name, fontSize = 11.5.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NaxiraPrimaryPurple,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = customClientName,
                            onValueChange = { customClientName = it },
                            label = { Text("Client Name *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = invoiceNumber,
                            onValueChange = { invoiceNumber = it },
                            label = { Text("Invoice No") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = invoiceDate,
                            onValueChange = { invoiceDate = it },
                            label = { Text("Date") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                item {
                    Text("GST Structure", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = taxType == "CGST_SGST", onClick = { taxType = "CGST_SGST" })
                        Text("CGST (9%) + SGST (9%)", fontSize = 11.5.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        RadioButton(selected = taxType == "IGST", onClick = { taxType = "IGST" })
                        Text("IGST (18%)", fontSize = 11.5.sp)
                    }
                }

                // Line Items Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Line Items (${lineItems.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        TextButton(onClick = {
                            lineItems.add(
                                InvoiceItem(
                                    id = System.currentTimeMillis().toString(),
                                    description = "Digital Marketing & SEO",
                                    quantity = 1.0,
                                    unitPrice = 4999.0,
                                    totalAmount = 4999.0
                                )
                            )
                        }) {
                            Text("+ Add Item", color = NaxiraSecondaryPurple)
                        }
                    }
                }

                // Line Items List
                items(lineItems.indices.toList()) { index ->
                    val item = lineItems[index]
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = item.description,
                                    onValueChange = { lineItems[index] = item.copy(description = it) },
                                    label = { Text("Service Description") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                if (lineItems.size > 1) {
                                    IconButton(onClick = { lineItems.removeAt(index) }) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = DangerRed)
                                    }
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = item.hsnSac,
                                    onValueChange = { lineItems[index] = item.copy(hsnSac = it) },
                                    label = { Text("SAC") },
                                    modifier = Modifier.weight(0.8f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = "${item.quantity}",
                                    onValueChange = {
                                        val q = it.toDoubleOrNull() ?: 1.0
                                        lineItems[index] = item.copy(quantity = q, totalAmount = q * item.unitPrice)
                                    },
                                    label = { Text("Qty") },
                                    modifier = Modifier.weight(0.7f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = "${item.unitPrice}",
                                    onValueChange = {
                                        val p = it.toDoubleOrNull() ?: 0.0
                                        lineItems[index] = item.copy(unitPrice = p, totalAmount = item.quantity * p)
                                    },
                                    label = { Text("Rate (₹)") },
                                    modifier = Modifier.weight(1.2f),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }

                // Discount and Totals Summary
                item {
                    OutlinedTextField(
                        value = discountInput,
                        onValueChange = { discountInput = it },
                        label = { Text("Special Discount (₹)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NaxiraLavenderSoft.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal:", fontSize = 12.sp)
                            Text("₹ ${String.format("%.2f", subtotal)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        if (discount > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Discount:", fontSize = 12.sp)
                                Text("- ₹ ${String.format("%.2f", discount)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("GST Amount:", fontSize = 12.sp)
                            Text("₹ ${String.format("%.2f", totalTax)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Grand Total:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = NaxiraPrimaryPurple)
                            Text("₹ ${String.format("%.2f", grandTotal)}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = NaxiraPrimaryPurple)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val client = clients.find { it.id == selectedClientId }
                    val finalClientName = client?.name ?: customClientName.ifBlank { "Client" }
                    val newInvoice = Invoice(
                        invoiceNumber = invoiceNumber,
                        clientId = client?.id ?: "",
                        clientName = finalClientName,
                        clientPhone = client?.phone ?: "",
                        clientEmail = client?.email ?: "",
                        clientGstin = client?.gstin ?: "",
                        clientAddress = client?.address ?: "",
                        invoiceDate = invoiceDate,
                        dueDate = dueDate,
                        items = lineItems.toList(),
                        subtotal = subtotal,
                        discountAmount = discount,
                        taxType = taxType,
                        cgstAmount = if (taxType == "CGST_SGST") totalTax / 2 else 0.0,
                        sgstAmount = if (taxType == "CGST_SGST") totalTax / 2 else 0.0,
                        igstAmount = if (taxType == "IGST") totalTax else 0.0,
                        totalTax = totalTax,
                        grandTotal = grandTotal,
                        balanceDue = grandTotal,
                        status = "Sent",
                        paymentTerms = paymentTerms
                    )
                    viewModel.createInvoice(newInvoice)
                    onCreated()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NaxiraPrimaryPurple)
            ) {
                Text("Generate Invoice")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun RecordPaymentDialog(
    invoice: Invoice,
    onDismiss: () -> Unit,
    onPaymentRecorded: (Payment) -> Unit
) {
    var amountInput by remember { mutableStateOf(String.format(Locale.getDefault(), "%.2f", invoice.balanceDue)) }
    var selectedMethod by remember { mutableStateOf("UPI") } // UPI, Bank Transfer, Cash, Cheque
    var referenceNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    val methods = listOf("UPI", "Bank Transfer", "Cash", "Cheque")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Invoice Payment", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Invoice: ${invoice.invoiceNumber} (${invoice.clientName})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Current Balance Due: ₹ ${String.format("%,.2f", invoice.balanceDue)}", color = DangerRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("Amount Received (₹) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Payment Mode", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(methods) { m ->
                        FilterChip(
                            selected = (selectedMethod == m),
                            onClick = { selectedMethod = m },
                            label = { Text(m, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = referenceNumber,
                    onValueChange = { referenceNumber = it },
                    label = { Text("UPI Ref / UTR / Cheque No") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountInput.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        val payment = Payment(
                            clientId = invoice.clientId,
                            clientName = invoice.clientName,
                            invoiceId = invoice.id,
                            invoiceNumber = invoice.invoiceNumber,
                            amount = amount,
                            paymentMethod = selectedMethod,
                            referenceNumber = referenceNumber.trim(),
                            paymentDate = today,
                            notes = notes.trim()
                        )
                        onPaymentRecorded(payment)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) {
                Text("Save Payment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
