package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BusinessProfile
import com.example.ui.theme.DangerRed
import com.example.ui.theme.NaxiraLavender
import com.example.ui.theme.NaxiraPrimaryPurple
import com.example.ui.theme.NaxiraSecondaryPurple
import com.example.ui.viewmodel.NaxiraViewModel

@Composable
fun SettingsScreen(
    viewModel: NaxiraViewModel,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val currentProfile by viewModel.profileState.collectAsState()

    var agencyName by remember(currentProfile) { mutableStateOf(currentProfile.agencyName) }
    var tagline by remember(currentProfile) { mutableStateOf(currentProfile.tagline) }
    var phone by remember(currentProfile) { mutableStateOf(currentProfile.phone) }
    var email by remember(currentProfile) { mutableStateOf(currentProfile.email) }
    var website by remember(currentProfile) { mutableStateOf(currentProfile.website) }
    var address by remember(currentProfile) { mutableStateOf(currentProfile.address) }
    var gstin by remember(currentProfile) { mutableStateOf(currentProfile.gstin) }
    var pan by remember(currentProfile) { mutableStateOf(currentProfile.pan) }
    var bankName by remember(currentProfile) { mutableStateOf(currentProfile.bankName) }
    var bankAccount by remember(currentProfile) { mutableStateOf(currentProfile.bankAccount) }
    var bankIfsc by remember(currentProfile) { mutableStateOf(currentProfile.bankIfsc) }
    var upiId by remember(currentProfile) { mutableStateOf(currentProfile.upiId) }
    var invoicePrefix by remember(currentProfile) { mutableStateOf(currentProfile.invoicePrefix) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Agency Brand Header
        item {
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.size(60.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.naxira_logo),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = agencyName.ifBlank { "NAXIRA" },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NaxiraPrimaryPurple
                        )
                        Text(
                            text = tagline.ifBlank { "illuminating your digital presence" },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Cloud Backend: Active • Firestore Synced",
                            fontSize = 10.5.sp,
                            color = NaxiraSecondaryPurple,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Business Profile Form
        item {
            Text("Business & Invoicing Profile", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        item {
            OutlinedTextField(
                value = agencyName,
                onValueChange = { agencyName = it },
                label = { Text("Agency Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = tagline,
                onValueChange = { tagline = it },
                label = { Text("Brand Tagline") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Business Phone") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Official Email") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Website") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = invoicePrefix,
                    onValueChange = { invoicePrefix = it },
                    label = { Text("Invoice Prefix") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }

        item {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Office Address (Printed on Invoices)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = gstin,
                    onValueChange = { gstin = it.uppercase() },
                    label = { Text("Agency GSTIN") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = pan,
                    onValueChange = { pan = it.uppercase() },
                    label = { Text("PAN Number") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }

        // Bank & UPI Settings
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text("Bank & UPI Details for Invoice Payments", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("Bank Name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = bankIfsc,
                    onValueChange = { bankIfsc = it.uppercase() },
                    label = { Text("IFSC Code") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = bankAccount,
                    onValueChange = { bankAccount = it },
                    label = { Text("Bank Account No") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("UPI ID (e.g. naxira@upi)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }

        // Save Settings Action
        item {
            Button(
                onClick = {
                    val updated = currentProfile.copy(
                        agencyName = agencyName.trim(),
                        tagline = tagline.trim(),
                        phone = phone.trim(),
                        email = email.trim(),
                        website = website.trim(),
                        address = address.trim(),
                        gstin = gstin.trim(),
                        pan = pan.trim(),
                        bankName = bankName.trim(),
                        bankAccount = bankAccount.trim(),
                        bankIfsc = bankIfsc.trim(),
                        upiId = upiId.trim(),
                        invoicePrefix = invoicePrefix.trim()
                    )
                    viewModel.saveBusinessProfile(updated)
                    Toast.makeText(context, "Agency settings saved to cloud!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaxiraPrimaryPurple)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Profile Settings", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Preload Proposal Catalog action
        item {
            OutlinedButton(
                onClick = {
                    viewModel.loadDefaultNaxiraServices()
                    Toast.makeText(context, "Official Naxira packages loaded into Catalog!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NaxiraSecondaryPurple)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Preload Naxira Proposal Packages to Catalog", color = NaxiraSecondaryPurple)
            }
        }

        // Sign Out
        item {
            Button(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sign Out of Agency Account")
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "NAXIRA Business Manager v1.0 • Built for Naxira Digital Agency",
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
