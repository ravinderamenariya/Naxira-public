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
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Phone
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
import com.example.data.model.Lead
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

@Composable
fun LeadManagementScreen(
    viewModel: NaxiraViewModel,
    onLeadConverted: (String) -> Unit
) {
    val context = LocalContext.current
    val leads by viewModel.leadsState.collectAsState()
    var selectedStageFilter by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var leadToEdit by remember { mutableStateOf<Lead?>(null) }

    val stages = listOf("All", "New Lead", "Contacted", "Interested", "Proposal Sent", "Negotiation", "Converted", "Lost")

    val filteredLeads = leads.filter {
        if (selectedStageFilter == "All") true else it.status.equals(selectedStageFilter, ignoreCase = true)
    }

    val totalPipelineValue = leads.filter { it.status != "Lost" && it.status != "Converted" }.sumOf { it.dealValue }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("leads_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Pipeline Summary Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Active Pipeline Value", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("₹ ${String.format("%,.0f", totalPipelineValue)}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = NaxiraPrimaryPurple)
                    }
                    Box(
                        modifier = Modifier
                            .background(NaxiraLavenderSoft, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("${leads.count { it.status != "Lost" && it.status != "Converted" }} Active Deals", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NaxiraPrimaryPurple)
                    }
                }
            }

            // Stage Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stages) { stage ->
                    val count = if (stage == "All") leads.size else leads.count { it.status.equals(stage, ignoreCase = true) }
                    FilterChip(
                        selected = (selectedStageFilter == stage),
                        onClick = { selectedStageFilter = stage },
                        label = { Text("$stage ($count)", fontSize = 11.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NaxiraSecondaryPurple,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (filteredLeads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = NaxiraLavender, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No leads in this stage", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            "Add potential client leads to track negotiations and proposal conversions.",
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
                    items(filteredLeads) { lead ->
                        LeadCard(
                            lead = lead,
                            onEdit = { leadToEdit = lead },
                            onConvertToClient = {
                                viewModel.convertLeadToClient(lead)
                                Toast.makeText(context, "${lead.clientName} converted to Client in CRM!", Toast.LENGTH_SHORT).show()
                            }
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
                .testTag("add_lead_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Lead")
        }
    }

    if (showAddDialog || leadToEdit != null) {
        AddEditLeadDialog(
            existingLead = leadToEdit,
            onDismiss = {
                showAddDialog = false
                leadToEdit = null
            },
            onSave = { lead ->
                if (leadToEdit != null) {
                    viewModel.updateLead(lead)
                } else {
                    viewModel.addLead(lead)
                }
                showAddDialog = false
                leadToEdit = null
            }
        )
    }
}

@Composable
private fun LeadCard(
    lead: Lead,
    onEdit: () -> Unit,
    onConvertToClient: () -> Unit
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
                    text = lead.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                val (stageBg, stageFg) = when (lead.status) {
                    "Converted" -> Pair(SuccessGreenSoft, SuccessGreen)
                    "Proposal Sent", "Negotiation" -> Pair(WarningAmberSoft, WarningAmber)
                    "Lost" -> Pair(DangerRedSoft, DangerRed)
                    else -> Pair(NaxiraLavenderSoft, NaxiraPrimaryPurple)
                }
                Box(
                    modifier = Modifier
                        .background(stageBg, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = lead.status, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = stageFg)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${lead.clientName} • ${lead.contactPhone.ifBlank { "No phone" }}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (lead.requirements.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = lead.requirements,
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Deal Value", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹ ${String.format("%,.0f", lead.dealValue)}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = NaxiraPrimaryPurple)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NaxiraSecondaryPurple, modifier = Modifier.size(18.dp))
                    }

                    if (lead.status != "Converted") {
                        Button(
                            onClick = onConvertToClient,
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Convert to Client", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditLeadDialog(
    existingLead: Lead? = null,
    onDismiss: () -> Unit,
    onSave: (Lead) -> Unit
) {
    var title by remember { mutableStateOf(existingLead?.title ?: "") }
    var clientName by remember { mutableStateOf(existingLead?.clientName ?: "") }
    var phone by remember { mutableStateOf(existingLead?.contactPhone ?: "") }
    var email by remember { mutableStateOf(existingLead?.email ?: "") }
    var dealValueInput by remember { mutableStateOf(if (existingLead != null) "${existingLead.dealValue.toInt()}" else "25000") }
    var status by remember { mutableStateOf(existingLead?.status ?: "New Lead") }
    var requirements by remember { mutableStateOf(existingLead?.requirements ?: "") }

    val stages = listOf("New Lead", "Contacted", "Interested", "Proposal Sent", "Negotiation", "Converted", "Lost")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingLead != null) "Edit Lead" else "New Lead Opportunity", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Deal Title (e.g. Website Redesign) *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it },
                        label = { Text("Client / Company Name *") },
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
                            value = dealValueInput,
                            onValueChange = { dealValueInput = it },
                            label = { Text("Deal Value (₹)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
                item {
                    Text("Lead Stage", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(stages) { st ->
                            FilterChip(
                                selected = (status == st),
                                onClick = { status = st },
                                label = { Text(st, fontSize = 11.sp) }
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = requirements,
                        onValueChange = { requirements = it },
                        label = { Text("Requirements & Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && clientName.isNotBlank()) {
                        val dealVal = dealValueInput.toDoubleOrNull() ?: 0.0
                        val lead = (existingLead ?: Lead()).copy(
                            title = title.trim(),
                            clientName = clientName.trim(),
                            contactPhone = phone.trim(),
                            email = email.trim(),
                            dealValue = dealVal,
                            status = status,
                            requirements = requirements.trim()
                        )
                        onSave(lead)
                    }
                },
                enabled = title.isNotBlank() && clientName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NaxiraPrimaryPurple)
            ) {
                Text("Save Lead")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
