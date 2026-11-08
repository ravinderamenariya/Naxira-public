package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

@IgnoreExtraProperties
data class BusinessProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val agencyName: String = "NAXIRA",
    val tagline: String = "illuminating your digital presence",
    val phone: String = "+91 88907 70739",
    val email: String = "contact@naxira.in",
    val website: String = "www.naxira.in",
    val address: String = "Udaipur, Rajasthan, India",
    val gstin: String = "",
    val pan: String = "",
    val bankName: String = "HDFC Bank",
    val bankAccount: String = "",
    val bankIfsc: String = "",
    val upiId: String = "naxira@upi",
    val invoicePrefix: String = "NAX-",
    val defaultTaxRate: Double = 18.0,
    val currencySymbol: String = "₹",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Client(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val contactPerson: String = "",
    val phone: String = "",
    val whatsapp: String = "",
    val email: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val pinCode: String = "",
    val gstin: String = "",
    val website: String = "",
    val category: String = "Small Business", // Small Business, Startup, E-commerce, Personal Brand, Enterprise
    val notes: String = "",
    val totalBilled: Double = 0.0,
    val totalPaid: Double = 0.0,
    val balanceDue: Double = 0.0,
    val isArchived: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Lead(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val clientName: String = "",
    val contactPhone: String = "",
    val email: String = "",
    val source: String = "Website", // Website, Referral, Social Media, Google, Cold Call
    val dealValue: Double = 0.0,
    val status: String = "New Lead", // New Lead, Contacted, Interested, Proposal Sent, Negotiation, Converted, Lost
    val requirements: String = "",
    val followUpDate: String = "",
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class InvoiceItem(
    val id: String = "",
    val description: String = "",
    val hsnSac: String = "998311", // Standard IT/Digital services SAC
    val quantity: Double = 1.0,
    val unitPrice: Double = 0.0,
    val discountPercent: Double = 0.0,
    val taxRate: Double = 18.0, // 18% standard
    val totalAmount: Double = 0.0
)

@IgnoreExtraProperties
data class Invoice(
    @DocumentId val id: String = "",
    val userId: String = "",
    val invoiceNumber: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val clientPhone: String = "",
    val clientEmail: String = "",
    val clientGstin: String = "",
    val clientAddress: String = "",
    val invoiceDate: String = "",
    val dueDate: String = "",
    val items: List<InvoiceItem> = emptyList(),
    val subtotal: Double = 0.0,
    val discountAmount: Double = 0.0,
    val taxType: String = "CGST_SGST", // CGST_SGST (Intra-state: 9%+9%) or IGST (Inter-state: 18%) or NONE
    val cgstAmount: Double = 0.0,
    val sgstAmount: Double = 0.0,
    val igstAmount: Double = 0.0,
    val totalTax: Double = 0.0,
    val grandTotal: Double = 0.0,
    val amountPaid: Double = 0.0,
    val balanceDue: Double = 0.0,
    val status: String = "Draft", // Draft, Sent, Partially Paid, Paid, Overdue, Cancelled
    val paymentTerms: String = "Payment due within 15 days",
    val paymentNotes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Quotation(
    @DocumentId val id: String = "",
    val userId: String = "",
    val quoteNumber: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val clientPhone: String = "",
    val clientEmail: String = "",
    val items: List<InvoiceItem> = emptyList(),
    val subtotal: Double = 0.0,
    val taxAmount: Double = 0.0,
    val grandTotal: Double = 0.0,
    val validUntil: String = "",
    val terms: String = "Proposal valid for 30 days. 50% advance to start work.",
    val status: String = "Draft", // Draft, Sent, Accepted, Declined, Converted
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Payment(
    @DocumentId val id: String = "",
    val userId: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val invoiceId: String = "",
    val invoiceNumber: String = "",
    val amount: Double = 0.0,
    val paymentMethod: String = "UPI", // UPI, Bank Transfer, Cash, Cheque, NEFT/IMPS, Other
    val referenceNumber: String = "",
    val paymentDate: String = "",
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class LedgerEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val invoiceId: String = "",
    val invoiceNumber: String = "",
    val entryType: String = "CREDIT", // DEBIT (You Gave / Work done / Invoice), CREDIT (You Got / Payment received)
    val amount: Double = 0.0,
    val paymentMethod: String = "",
    val referenceNumber: String = "",
    val description: String = "",
    val runningBalance: Double = 0.0, // Net receivable after this transaction
    val entryDate: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Expense(
    @DocumentId val id: String = "",
    val userId: String = "",
    val category: String = "Software Subscriptions", // Hosting, Domain, Software Subscriptions, Advertising, Marketing, Salaries, Office, Internet, Travel, Other
    val title: String = "",
    val amount: Double = 0.0,
    val paymentMethod: String = "Bank Transfer",
    val vendor: String = "",
    val notes: String = "",
    val expenseDate: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ProjectTask(
    val id: String = "",
    val title: String = "",
    val isCompleted: Boolean = false
)

@IgnoreExtraProperties
data class Project(
    @DocumentId val id: String = "",
    val userId: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val projectName: String = "",
    val category: String = "Website Development", // Website Development, SEO, Social Media Management, Branding, Graphic Design, Digital Marketing, Custom Projects
    val budget: Double = 0.0,
    val startDate: String = "",
    val deadline: String = "",
    val status: String = "In Progress", // Not Started, In Progress, Review, Completed, On Hold
    val progressPercentage: Int = 0,
    val tasks: List<ProjectTask> = emptyList(),
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ServiceItem(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val category: String = "Development", // Development, Design & Branding, Marketing & SEO, Advertising & Video
    val description: String = "",
    val basePrice: Double = 0.0,
    val defaultTaxRate: Double = 18.0,
    val billingCycle: String = "One-Time", // One-Time, Monthly, Yearly
    @ServerTimestamp val createdAt: Timestamp? = null
)

data class DashboardMetrics(
    val totalRevenue: Double = 0.0,
    val totalReceived: Double = 0.0,
    val totalOutstanding: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val netProfit: Double = 0.0,
    val totalClients: Int = 0,
    val activeClients: Int = 0,
    val newLeads: Int = 0,
    val activeProjects: Int = 0,
    val overdueInvoices: Int = 0
)
