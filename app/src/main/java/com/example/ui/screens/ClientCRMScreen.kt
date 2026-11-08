package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Client
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedSoft
import com.example.ui.theme.NaxiraLavender
import com.example.ui.theme.NaxiraLavenderSoft
import com.example.ui.theme.NaxiraPrimaryPurple
import com.example.ui.theme.NaxiraSecondaryPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenSoft
import com.example.ui.viewmodel.NaxiraViewModel

@Composable
fun ClientCRMScreen(
    viewModel: NaxiraViewModel,
    onNavigateToInvoicesWithClient: (String) -> Unit,
    onNavigateToKhataWithClient: (String) -> Unit
) {
    val clients by viewModel.clientsState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedClientForDetail by remember { mutableStateOf<Client?>(null) }
    var clientToEdit by remember { mutableStateOf<Client?>(null) }

    val filteredClients = clients.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.contactPerson.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("clients_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar & Filter
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("client_search_input"),
                placeholder = { Text("Search client name, phone, contact person...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            if (filteredClients.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = NaxiraLavender,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No clients match your search" else "No clients registered yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap the + button below to add your first agency client.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredClients) { client ->
                        ClientCard(
                            client = client,
                            onClick = { selectedClientForDetail = client }
                        )
                    }
                }
            }
        }

        // Add Client FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = NaxiraPrimaryPurple,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_client_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Client")
        }
    }

    // Add / Edit Client Dialog
    if (showAddDialog || clientToEdit != null) {
        AddEditClientDialog(
            existingClient = clientToEdit,
            onDismiss = {
                showAddDialog = false
                clientToEdit = null
            },
            onSave = { client ->
                if (clientToEdit != null) {
                    viewModel.updateClient(client)
                } else {
                    viewModel.addClient(client)
                }
                showAddDialog = false
                clientToEdit = null
            }
        )
    }

    // Client Dossier / Detail Dialog
    selectedClientForDetail?.let { client ->
        ClientDetailModal(
            client = client,
            onDismiss = { selectedClientForDetail = null },
            onEdit = {
                clientToEdit = client
                selectedClientForDetail = null
            },
            onDelete = {
                viewModel.deleteClient(client.id)
                selectedClientForDetail = null
            },
            onCreateInvoice = {
                selectedClientForDetail = null
                onNavigateToInvoicesWithClient(client.id)
            },
            onViewKhata = {
                selectedClientForDetail = null
                onNavigateToKhataWithClient(client.id)
            }
        )
    }
}

@Composable
private fun ClientCard(
    client: Client,
    onClick: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(NaxiraLavenderSoft, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = client.name.take(1).uppercase(),
                    color = NaxiraPrimaryPurple,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = client.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (client.contactPerson.isNotBlank()) {
                    Text(
                        text = "${client.contactPerson} • ${client.phone}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (client.gstin.isNotBlank()) {
                    Text(
                        text = "GSTIN: ${client.gstin}",
                        fontSize = 10.5.sp,
                        color = NaxiraSecondaryPurple,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                val hasDues = client.balanceDue > 0.01
                Box(
                    modifier = Modifier
                        .background(
                            if (hasDues) DangerRedSoft else SuccessGreenSoft,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (hasDues) "Due: ₹ ${String.format("%,.0f", client.balanceDue)}" else "Clear",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasDues) DangerRed else SuccessGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun ClientDetailModal(
    client: Client,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCreateInvoice: () -> Unit,
    onViewKhata: () -> Unit
) {
    val context = LocalContext.current
    var showDeleteConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = client.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Client", tint = NaxiraSecondaryPurple)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Lifetime Financial Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(NaxiraLavenderSoft.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Text("Total Billed", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹ ${String.format("%,.0f", client.totalBilled)}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(SuccessGreenSoft, RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Text("Total Paid", fontSize = 10.5.sp, color = SuccessGreen)
                        Text("₹ ${String.format("%,.0f", client.totalPaid)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (client.balanceDue > 0) DangerRedSoft else SuccessGreenSoft, RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Text("Balance Due", fontSize = 10.5.sp, color = if (client.balanceDue > 0) DangerRed else SuccessGreen)
                        Text("₹ ${String.format("%,.0f", client.balanceDue)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (client.balanceDue > 0) DangerRed else SuccessGreen)
                    }
                }

                // Info Rows
                if (client.contactPerson.isNotBlank()) Text("Contact: ${client.contactPerson}", fontSize = 12.5.sp)
                if (client.phone.isNotBlank()) Text("Phone: ${client.phone}", fontSize = 12.5.sp)
                if (client.email.isNotBlank()) Text("Email: ${client.email}", fontSize = 12.5.sp)
                if (client.gstin.isNotBlank()) Text("GSTIN: ${client.gstin}", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                if (client.address.isNotBlank()) Text("Address: ${client.address}, ${client.city} ${client.state}", fontSize = 12.sp)
                if (client.notes.isNotBlank()) Text("Notes: ${client.notes}", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(4.dp))

                // Communication shortcuts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (client.phone.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${client.phone}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val cleanNumber = client.whatsapp.ifBlank { client.phone }.replace("+", "").replace(" ", "")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$cleanNumber"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("WhatsApp", fontSize = 12.sp)
                        }
                    }
                }

                // Action Buttons
                Button(
                    onClick = onCreateInvoice,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NaxiraPrimaryPurple),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create Invoice")
                }

                OutlinedButton(
                    onClick = onViewKhata,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("View Client Khata Statement")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        dismissButton = {
            TextButton(onClick = { showDeleteConfirm = true }) {
                Text("Delete Client", color = DangerRed)
            }
        }
    )

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Client?") },
            text = { Text("Are you sure you want to delete ${client.name}? Historical invoices will remain in your records.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AddEditClientDialog(
    existingClient: Client? = null,
    onDismiss: () -> Unit,
    onSave: (Client) -> Unit
) {
    var name by remember { mutableStateOf(existingClient?.name ?: "") }
    var contactPerson by remember { mutableStateOf(existingClient?.contactPerson ?: "") }
    var phone by remember { mutableStateOf(existingClient?.phone ?: "") }
    var whatsapp by remember { mutableStateOf(existingClient?.whatsapp ?: "") }
    var email by remember { mutableStateOf(existingClient?.email ?: "") }
    var address by remember { mutableStateOf(existingClient?.address ?: "") }
    var city by remember { mutableStateOf(existingClient?.city ?: "") }
    var state by remember { mutableStateOf(existingClient?.state ?: "") }
    var pinCode by remember { mutableStateOf(existingClient?.pinCode ?: "") }
    var gstin by remember { mutableStateOf(existingClient?.gstin ?: "") }
    var notes by remember { mutableStateOf(existingClient?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existingClient != null) "Edit Client Details" else "Add New Agency Client",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Company / Client Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = contactPerson,
                        onValueChange = { contactPerson = it },
                        label = { Text("Contact Person") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = whatsapp,
                            onValueChange = { whatsapp = it },
                            label = { Text("WhatsApp") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = gstin,
                        onValueChange = { gstin = it.uppercase() },
                        label = { Text("GSTIN (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Billing Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = state,
                            onValueChange = { state = it },
                            label = { Text("State") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes / Requirements") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val client = (existingClient ?: Client()).copy(
                            name = name.trim(),
                            contactPerson = contactPerson.trim(),
                            phone = phone.trim(),
                            whatsapp = whatsapp.trim(),
                            email = email.trim(),
                            address = address.trim(),
                            city = city.trim(),
                            state = state.trim(),
                            pinCode = pinCode.trim(),
                            gstin = gstin.trim(),
                            notes = notes.trim()
                        )
                        onSave(client)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NaxiraPrimaryPurple)
            ) {
                Text("Save Client")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
