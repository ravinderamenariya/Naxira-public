package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedSoft
import com.example.ui.theme.NaxiraLavenderSoft
import com.example.ui.theme.NaxiraPrimaryPurple
import com.example.ui.theme.NaxiraSecondaryPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenSoft
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.NaxiraViewModel

@Composable
fun ReportsScreen(viewModel: NaxiraViewModel) {
    val context = LocalContext.current
    val metrics by viewModel.dashboardMetrics.collectAsState()
    val clients by viewModel.clientsState.collectAsState()
    val invoices by viewModel.invoicesState.collectAsState()
    val expenses by viewModel.expensesState.collectAsState()
    val leads by viewModel.leadsState.collectAsState()

    val totalDeals = leads.size
    val convertedDeals = leads.count { it.status == "Converted" }
    val conversionRate = if (totalDeals > 0) (convertedDeals * 100) / totalDeals else 0

    val topClients = clients.sortedByDescending { it.totalBilled }.take(5)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("reports_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Agency Financial P&L", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Calculated from verified transactions", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = {
                        val summary = """
                            *NAXIRA Agency Business Report*
                            --------------------------------
                            • Total Revenue Billed: ₹ ${String.format("%,.2f", metrics.totalRevenue)}
                            • Actual Received Collections: ₹ ${String.format("%,.2f", metrics.totalReceived)}
                            • Outstanding Dues: ₹ ${String.format("%,.2f", metrics.totalOutstanding)}
                            • Total Expenses: ₹ ${String.format("%,.2f", metrics.totalExpenses)}
                            • Net Operating Margin: ₹ ${String.format("%,.2f", metrics.netProfit)}
                            • Active Clients: ${metrics.activeClients}
                            • Lead Conversion: $conversionRate% ($convertedDeals/$totalDeals won)
                            --------------------------------
                            Generated via Naxira Business Manager
                        """.trimIndent()
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, summary)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Business Summary"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NaxiraSecondaryPurple),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 12.sp)
                }
            }
        }

        // Net Operating Profit Card
        item {
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (metrics.netProfit >= 0) NaxiraPrimaryPurple else DangerRed
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("NET OPERATING PROFIT", color = Color.White.copy(alpha = 0.8f), fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹ ${String.format("%,.2f", metrics.netProfit)}",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val profitMargin = if (metrics.totalReceived > 0) (metrics.netProfit / metrics.totalReceived) * 100 else 0.0
                    Text(
                        text = "Margin: ${String.format("%.1f", profitMargin)}% of cash collected",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Cash Flow Split
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = SuccessGreenSoft),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Collections (In)", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("₹ ${String.format("%,.0f", metrics.totalReceived)}", color = SuccessGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = DangerRedSoft),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Expenses (Out)", color = DangerRed, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text("₹ ${String.format("%,.0f", metrics.totalExpenses)}", color = DangerRed, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Lead Conversion Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Lead Conversion Performance", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("$conversionRate%", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = NaxiraSecondaryPurple)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { conversionRate / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = NaxiraSecondaryPurple,
                        trackColor = NaxiraLavenderSoft
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$convertedDeals won out of $totalDeals total leads generated",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Top Revenue Contributing Clients
        item {
            Text("Top Clients by Lifetime Revenue", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        if (topClients.isEmpty()) {
            item {
                Text("No client billing records found.", fontSize = 12.sp, color = Color.Gray)
            }
        } else {
            items(topClients) { client ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(client.name, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Paid: ₹ ${String.format("%,.0f", client.totalPaid)}", fontSize = 11.sp, color = SuccessGreen)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("₹ ${String.format("%,.0f", client.totalBilled)}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = NaxiraPrimaryPurple)
                            if (client.balanceDue > 0) {
                                Text("Due: ₹ ${String.format("%,.0f", client.balanceDue)}", fontSize = 11.sp, color = DangerRed)
                            }
                        }
                    }
                }
            }
        }
    }
}
