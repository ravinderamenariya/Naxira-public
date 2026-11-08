package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BusinessProfile
import com.example.data.model.Client
import com.example.data.model.DashboardMetrics
import com.example.data.model.Expense
import com.example.data.model.Invoice
import com.example.data.model.Lead
import com.example.data.model.LedgerEntry
import com.example.data.model.Payment
import com.example.data.model.Project
import com.example.data.model.Quotation
import com.example.data.model.ServiceItem
import com.example.data.repository.NaxiraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NaxiraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NaxiraRepository(application)

    val dateFilter = MutableStateFlow("All") // All, Today, This Week, This Month, This Year
    val searchQuery = MutableStateFlow("")

    val profileState: StateFlow<BusinessProfile> = repository.observeBusinessProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BusinessProfile())

    val clientsState: StateFlow<List<Client>> = repository.observeClients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leadsState: StateFlow<List<Lead>> = repository.observeLeads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val invoicesState: StateFlow<List<Invoice>> = repository.observeInvoices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quotationsState: StateFlow<List<Quotation>> = repository.observeQuotations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ledgerState: StateFlow<List<LedgerEntry>> = repository.observeLedger()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expensesState: StateFlow<List<Expense>> = repository.observeExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projectsState: StateFlow<List<Project>> = repository.observeProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val servicesState: StateFlow<List<ServiceItem>> = repository.observeServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Aggregated real-time metrics computed from actual saved records
    val dashboardMetrics: StateFlow<DashboardMetrics> = combine(
        invoicesState,
        clientsState,
        leadsState,
        projectsState,
        expensesState
    ) { invoices, clients, leads, projects, expenses ->
        val totalRevenue = invoices.filter { it.status != "Cancelled" }.sumOf { it.grandTotal }
        val totalReceived = invoices.filter { it.status != "Cancelled" }.sumOf { it.amountPaid }
        val totalOutstanding = invoices.filter { it.status != "Cancelled" }.sumOf { it.balanceDue }
        val totalExpenses = expenses.sumOf { it.amount }
        val netProfit = totalReceived - totalExpenses
        val overdueCount = invoices.count { it.status == "Overdue" || (it.balanceDue > 0 && it.status == "Sent") }

        DashboardMetrics(
            totalRevenue = totalRevenue,
            totalReceived = totalReceived,
            totalOutstanding = totalOutstanding,
            totalExpenses = totalExpenses,
            netProfit = netProfit,
            totalClients = clients.size,
            activeClients = clients.count { !it.isArchived },
            newLeads = leads.count { it.status != "Converted" && it.status != "Lost" },
            activeProjects = projects.count { it.status == "In Progress" || it.status == "Review" },
            overdueInvoices = overdueCount
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardMetrics())

    fun saveBusinessProfile(profile: BusinessProfile) {
        viewModelScope.launch {
            repository.saveBusinessProfile(profile)
        }
    }

    fun addClient(client: Client, onComplete: (String) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.addClient(client)
            onComplete(id)
        }
    }

    fun updateClient(client: Client) {
        viewModelScope.launch {
            repository.updateClient(client)
        }
    }

    fun deleteClient(clientId: String) {
        viewModelScope.launch {
            repository.deleteClient(clientId)
        }
    }

    fun addLead(lead: Lead) {
        viewModelScope.launch {
            repository.addLead(lead)
        }
    }

    fun updateLead(lead: Lead) {
        viewModelScope.launch {
            repository.updateLead(lead)
        }
    }

    fun convertLeadToClient(lead: Lead) {
        viewModelScope.launch {
            repository.convertLeadToClient(lead)
        }
    }

    fun createInvoice(invoice: Invoice, onComplete: (String) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.createInvoice(invoice)
            onComplete(id)
        }
    }

    fun recordPayment(payment: Payment) {
        viewModelScope.launch {
            repository.recordPayment(payment)
        }
    }

    fun addManualLedgerEntry(entry: LedgerEntry) {
        viewModelScope.launch {
            repository.addManualLedgerEntry(entry)
        }
    }

    fun createQuotation(quotation: Quotation) {
        viewModelScope.launch {
            repository.createQuotation(quotation)
        }
    }

    fun convertQuotationToInvoice(quotation: Quotation) {
        viewModelScope.launch {
            val nextNumber = "NAX-${System.currentTimeMillis() % 10000}"
            repository.convertQuotationToInvoice(quotation, nextNumber)
        }
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repository.addExpense(expense)
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            repository.deleteExpense(expenseId)
        }
    }

    fun addProject(project: Project) {
        viewModelScope.launch {
            repository.addProject(project)
        }
    }

    fun updateProject(project: Project) {
        viewModelScope.launch {
            repository.updateProject(project)
        }
    }

    fun addService(service: ServiceItem) {
        viewModelScope.launch {
            repository.addService(service)
        }
    }

    fun loadDefaultNaxiraServices() {
        viewModelScope.launch {
            repository.loadDefaultNaxiraServices()
        }
    }

    fun getNextInvoiceNumber(prefix: String): String {
        val count = invoicesState.value.size + 1
        return "$prefix${String.format(Locale.getDefault(), "%04d", count)}"
    }

    fun getNextQuoteNumber(): String {
        val count = quotationsState.value.size + 1
        return "EST-${String.format(Locale.getDefault(), "%04d", count)}"
    }
}
