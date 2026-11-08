package com.example.ui.screens

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
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Invoice
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedSoft
import com.example.ui.theme.NaxiraDarkCard
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
fun DashboardScreen(
    viewModel: NaxiraViewModel,
    onNavigateToInvoices: () -> Unit,
    onNavigateToKhata: () -> Unit,
    onNavigateToClients: () -> Unit,
    onNavigateToLeads: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onOpenCreateInvoice: () -> Unit,
    onOpenAddExpense: () -> Unit,
    onOpenAddClient: () -> Unit
) {
    val metrics by viewModel.dashboardMetrics.collectAsState()
    val invoices by viewModel.invoicesState.collectAsState()
    val dateFilter by viewModel.dateFilter.collectAsState()

    val dateOptions = listOf("All", "Today", "This Week", "This Month", "This Year")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header & Date Filter
        item {
            Column {
                Text(
                    text = "Business Overview",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Real-time metrics from actual saved agency records",
                    fontSize = 12.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Date Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dateOptions) { option ->
                        FilterChip(
                            selected = (dateFilter == option),
                            onClick = { viewModel.dateFilter.value = option },
                            label = { Text(option, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NaxiraSecondaryPurple,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Hero KPI Cards: Revenue & Net Profit
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Revenue Card
                HeroMetricCard(
                    title = "Total Revenue",
                    amount = metrics.totalRevenue,
                    subtitle = "Billed across all invoices",
                    icon = Icons.Default.ArrowUpward,
                    iconBg = SuccessGreenSoft,
                    iconTint = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )

                // Net Profit Card
                HeroMetricCard(
                    title = "Net Profit",
                    amount = metrics.netProfit,
                    subtitle = "Received minus expenses",
                    icon = if (metrics.netProfit >= 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    iconBg = if (metrics.netProfit >= 0) NaxiraLavenderSoft else DangerRedSoft,
                    iconTint = if (metrics.netProfit >= 0) NaxiraPrimaryPurple else DangerRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Cash Flow Breakdown Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MiniMetricCard(
                    title = "Total Received",
                    amount = metrics.totalReceived,
                    color = SuccessGreen,
                    bgColor = SuccessGreenSoft,
                    modifier = Modifier.weight(1f)
                )
                MiniMetricCard(
                    title = "Outstanding",
                    amount = metrics.totalOutstanding,
                    color = DangerRed,
                    bgColor = DangerRedSoft,
                    modifier = Modifier.weight(1f)
                )
                MiniMetricCard(
                    title = "Expenses",
                    amount = metrics.totalExpenses,
                    color = WarningAmber,
                    bgColor = WarningAmberSoft,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Operational Pipeline Counts
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    PipelineItem(
                        label = "Active Clients",
                        count = metrics.activeClients,
                        icon = Icons.Default.Group,
                        onClick = onNavigateToClients
                    )
                    PipelineItem(
                        label = "New Leads",
                        count = metrics.newLeads,
                        icon = Icons.Default.BusinessCenter,
                        onClick = onNavigateToLeads
                    )
                    PipelineItem(
                        label = "Active Projects",
                        count = metrics.activeProjects,
                        icon = Icons.Default.Receipt,
                        onClick = {}
                    )
                    PipelineItem(
                        label = "Overdue",
                        count = metrics.overdueInvoices,
                        icon = Icons.Default.Warning,
                        isAlert = metrics.overdueInvoices > 0,
                        onClick = onNavigateToInvoices
                    )
                }
            }
        }

        // Quick Action Shortcuts Bar
        item {
            Column {
                Text(
                    text = "Quick Actions",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        QuickActionButton(
                            label = "+ Invoice",
                            icon = Icons.Default.Receipt,
                            containerColor = NaxiraPrimaryPurple,
                            onClick = onOpenCreateInvoice
                        )
                    }
                    item {
                        QuickActionButton(
                            label = "+ Payment",
                            icon = Icons.Default.Payment,
                            containerColor = NaxiraSecondaryPurple,
                            onClick = onNavigateToKhata
                        )
                    }
                    item {
                        QuickActionButton(
                            label = "+ Client",
                            icon = Icons.Default.Group,
                            containerColor = Color(0xFF5A2A9A),
                            onClick = onOpenAddClient
                        )
                    }
                    item {
                        QuickActionButton(
                            label = "+ Expense",
                            icon = Icons.Default.MoneyOff,
                            containerColor = Color(0xFF7A3BB8),
                            onClick = onOpenAddExpense
                        )
                    }
                }
            }
        }

        // Recent Invoices Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Invoices",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "View All",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NaxiraSecondaryPurple,
                    modifier = Modifier.clickable { onNavigateToInvoices() }
                )
            }
        }

        if (invoices.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = NaxiraLavender,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No invoices generated yet",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Tap '+ Invoice' above to issue your first Indian GST invoice.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(invoices.take(5)) { invoice ->
                RecentInvoiceRow(invoice = invoice, onClick = onNavigateToInvoices)
            }
        }
    }
}

@Composable
private fun HeroMetricCard(
    title: String,
    amount: Double,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(iconBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "₹ ${String.format("%,.0f", amount)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun MiniMetricCard(
    title: String,
    amount: Double,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "₹ ${String.format("%,.0f", amount)}",
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun PipelineItem(
    label: String,
    count: Int,
    icon: ImageVector,
    isAlert: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    if (isAlert) DangerRedSoft else NaxiraLavenderSoft.copy(alpha = 0.7f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isAlert) DangerRed else NaxiraPrimaryPurple,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$count",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (isAlert) DangerRed else MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            fontSize = 10.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: ImageVector,
    containerColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier
            .clickable { onClick() }
            .testTag("quick_action_${label.lowercase().replace(" ", "_")}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RecentInvoiceRow(
    invoice: Invoice,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = invoice.clientName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${invoice.invoiceNumber} • ${invoice.invoiceDate}",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹ ${String.format("%,.2f", invoice.grandTotal)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                val statusColor = when (invoice.status) {
                    "Paid" -> SuccessGreen
                    "Partially Paid" -> WarningAmber
                    "Overdue" -> DangerRed
                    else -> NaxiraSecondaryPurple
                }
                Text(
                    text = invoice.status,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }
        }
    }
}
