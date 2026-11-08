package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.BusinessProfile
import com.example.data.model.Client
import com.example.data.model.Expense
import com.example.data.model.Invoice
import com.example.data.model.Lead
import com.example.data.model.LedgerEntry
import com.example.data.model.Payment
import com.example.data.model.Project
import com.example.data.model.Quotation
import com.example.data.model.ServiceItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NaxiraRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google to access business data.")
    }

    private fun userRef() = db.collection("users").document(requireUserId())

    // --- BUSINESS PROFILE ---

    fun observeBusinessProfile(): Flow<BusinessProfile> = flow {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("settings").document("business_profile")
        emitAll(
            docRef.snapshots().map { snapshot ->
                snapshot.toObject(BusinessProfile::class.java) ?: BusinessProfile(userId = uid)
            }
        )
    }

    suspend fun saveBusinessProfile(profile: BusinessProfile) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("settings").document("business_profile")
        val payload = mapOf(
            "userId" to uid,
            "agencyName" to profile.agencyName,
            "tagline" to profile.tagline,
            "phone" to profile.phone,
            "email" to profile.email,
            "website" to profile.website,
            "address" to profile.address,
            "gstin" to profile.gstin,
            "pan" to profile.pan,
            "bankName" to profile.bankName,
            "bankAccount" to profile.bankAccount,
            "bankIfsc" to profile.bankIfsc,
            "upiId" to profile.upiId,
            "invoicePrefix" to profile.invoicePrefix,
            "defaultTaxRate" to profile.defaultTaxRate,
            "currencySymbol" to profile.currencySymbol,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    // --- CLIENTS ---

    fun observeClients(): Flow<List<Client>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("users").document(uid).collection("clients")
                .snapshots()
                .map { snapshot -> snapshot.toObjects(Client::class.java) }
        )
    }

    suspend fun addClient(client: Client): String {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("clients").document()
        val payload = mapOf(
            "userId" to uid,
            "name" to client.name,
            "contactPerson" to client.contactPerson,
            "phone" to client.phone,
            "whatsapp" to client.whatsapp,
            "email" to client.email,
            "address" to client.address,
            "city" to client.city,
            "state" to client.state,
            "pinCode" to client.pinCode,
            "gstin" to client.gstin,
            "website" to client.website,
            "category" to client.category,
            "notes" to client.notes,
            "totalBilled" to client.totalBilled,
            "totalPaid" to client.totalPaid,
            "balanceDue" to client.balanceDue,
            "isArchived" to client.isArchived,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
        return docRef.id
    }

    suspend fun updateClient(client: Client) {
        val uid = requireUserId()
        require(client.id.isNotBlank())
        val docRef = db.collection("users").document(uid).collection("clients").document(client.id)
        val payload = mapOf(
            "userId" to uid,
            "name" to client.name,
            "contactPerson" to client.contactPerson,
            "phone" to client.phone,
            "whatsapp" to client.whatsapp,
            "email" to client.email,
            "address" to client.address,
            "city" to client.city,
            "state" to client.state,
            "pinCode" to client.pinCode,
            "gstin" to client.gstin,
            "website" to client.website,
            "category" to client.category,
            "notes" to client.notes,
            "totalBilled" to client.totalBilled,
            "totalPaid" to client.totalPaid,
            "balanceDue" to client.balanceDue,
            "isArchived" to client.isArchived,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.update(payload).await()
    }

    suspend fun deleteClient(clientId: String) {
        val uid = requireUserId()
        db.collection("users").document(uid).collection("clients").document(clientId).delete().await()
    }

    // --- LEADS ---

    fun observeLeads(): Flow<List<Lead>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("users").document(uid).collection("leads")
                .snapshots()
                .map { snapshot -> snapshot.toObjects(Lead::class.java) }
        )
    }

    suspend fun addLead(lead: Lead): String {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("leads").document()
        val payload = mapOf(
            "userId" to uid,
            "title" to lead.title,
            "clientName" to lead.clientName,
            "contactPhone" to lead.contactPhone,
            "email" to lead.email,
            "source" to lead.source,
            "dealValue" to lead.dealValue,
            "status" to lead.status,
            "requirements" to lead.requirements,
            "followUpDate" to lead.followUpDate,
            "notes" to lead.notes,
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
        return docRef.id
    }

    suspend fun updateLead(lead: Lead) {
        val uid = requireUserId()
        require(lead.id.isNotBlank())
        val docRef = db.collection("users").document(uid).collection("leads").document(lead.id)
        val payload = mapOf(
            "title" to lead.title,
            "clientName" to lead.clientName,
            "contactPhone" to lead.contactPhone,
            "email" to lead.email,
            "source" to lead.source,
            "dealValue" to lead.dealValue,
            "status" to lead.status,
            "requirements" to lead.requirements,
            "followUpDate" to lead.followUpDate,
            "notes" to lead.notes
        )
        docRef.update(payload).await()
    }

    suspend fun convertLeadToClient(lead: Lead): String {
        val client = Client(
            name = lead.clientName,
            contactPerson = lead.clientName,
            phone = lead.contactPhone,
            email = lead.email,
            notes = "Converted from Lead: ${lead.title}. Requirements: ${lead.requirements}"
        )
        val clientId = addClient(client)
        updateLead(lead.copy(status = "Converted"))
        return clientId
    }

    // --- INVOICES & LEDGER INTEGRATION ---

    fun observeInvoices(): Flow<List<Invoice>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("users").document(uid).collection("invoices")
                .snapshots()
                .map { snapshot -> snapshot.toObjects(Invoice::class.java) }
        )
    }

    suspend fun createInvoice(invoice: Invoice): String {
        val uid = requireUserId()
        val invoiceRef = db.collection("users").document(uid).collection("invoices").document()
        val invoiceId = invoiceRef.id

        val itemsData = invoice.items.map { item ->
            mapOf(
                "id" to item.id,
                "description" to item.description,
                "hsnSac" to item.hsnSac,
                "quantity" to item.quantity,
                "unitPrice" to item.unitPrice,
                "discountPercent" to item.discountPercent,
                "taxRate" to item.taxRate,
                "totalAmount" to item.totalAmount
            )
        }

        val invoicePayload = mapOf(
            "userId" to uid,
            "invoiceNumber" to invoice.invoiceNumber,
            "clientId" to invoice.clientId,
            "clientName" to invoice.clientName,
            "clientPhone" to invoice.clientPhone,
            "clientEmail" to invoice.clientEmail,
            "clientGstin" to invoice.clientGstin,
            "clientAddress" to invoice.clientAddress,
            "invoiceDate" to invoice.invoiceDate,
            "dueDate" to invoice.dueDate,
            "items" to itemsData,
            "subtotal" to invoice.subtotal,
            "discountAmount" to invoice.discountAmount,
            "taxType" to invoice.taxType,
            "cgstAmount" to invoice.cgstAmount,
            "sgstAmount" to invoice.sgstAmount,
            "igstAmount" to invoice.igstAmount,
            "totalTax" to invoice.totalTax,
            "grandTotal" to invoice.grandTotal,
            "amountPaid" to invoice.amountPaid,
            "balanceDue" to invoice.balanceDue,
            "status" to invoice.status,
            "paymentTerms" to invoice.paymentTerms,
            "paymentNotes" to invoice.paymentNotes,
            "createdAt" to FieldValue.serverTimestamp()
        )

        // Atomic batch write: save invoice, record ledger debit, and update client billed balance
        val batch = db.batch()
        batch.set(invoiceRef, invoicePayload)

        if (invoice.clientId.isNotBlank()) {
            val clientRef = db.collection("users").document(uid).collection("clients").document(invoice.clientId)
            batch.update(
                clientRef,
                mapOf(
                    "totalBilled" to FieldValue.increment(invoice.grandTotal),
                    "balanceDue" to FieldValue.increment(invoice.balanceDue),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )

            // Auto-create Khatabook Debit entry (Work delivered / Invoice billed)
            val ledgerRef = db.collection("users").document(uid).collection("ledger").document()
            val ledgerPayload = mapOf(
                "userId" to uid,
                "clientId" to invoice.clientId,
                "clientName" to invoice.clientName,
                "invoiceId" to invoiceId,
                "invoiceNumber" to invoice.invoiceNumber,
                "entryType" to "DEBIT",
                "amount" to invoice.grandTotal,
                "paymentMethod" to "Invoice",
                "referenceNumber" to invoice.invoiceNumber,
                "description" to "Invoice #${invoice.invoiceNumber} billed",
                "runningBalance" to invoice.balanceDue,
                "entryDate" to invoice.invoiceDate,
                "createdAt" to FieldValue.serverTimestamp()
            )
            batch.set(ledgerRef, ledgerPayload)
        }

        batch.commit().await()
        return invoiceId
    }

    suspend fun recordPayment(payment: Payment) {
        val uid = requireUserId()
        val paymentRef = db.collection("users").document(uid).collection("payments").document()

        val paymentPayload = mapOf(
            "userId" to uid,
            "clientId" to payment.clientId,
            "clientName" to payment.clientName,
            "invoiceId" to payment.invoiceId,
            "invoiceNumber" to payment.invoiceNumber,
            "amount" to payment.amount,
            "paymentMethod" to payment.paymentMethod,
            "referenceNumber" to payment.referenceNumber,
            "paymentDate" to payment.paymentDate,
            "notes" to payment.notes,
            "createdAt" to FieldValue.serverTimestamp()
        )

        val batch = db.batch()
        batch.set(paymentRef, paymentPayload)

        // Update Client totalPaid and balanceDue
        if (payment.clientId.isNotBlank()) {
            val clientRef = db.collection("users").document(uid).collection("clients").document(payment.clientId)
            batch.update(
                clientRef,
                mapOf(
                    "totalPaid" to FieldValue.increment(payment.amount),
                    "balanceDue" to FieldValue.increment(-payment.amount),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )

            // Auto-create Khatabook Credit entry (Payment Received)
            val ledgerRef = db.collection("users").document(uid).collection("ledger").document()
            val ledgerPayload = mapOf(
                "userId" to uid,
                "clientId" to payment.clientId,
                "clientName" to payment.clientName,
                "invoiceId" to payment.invoiceId,
                "invoiceNumber" to payment.invoiceNumber,
                "entryType" to "CREDIT",
                "amount" to payment.amount,
                "paymentMethod" to payment.paymentMethod,
                "referenceNumber" to payment.referenceNumber,
                "description" to "Payment received via ${payment.paymentMethod} (${payment.referenceNumber.ifBlank { "Ref#" }})",
                "runningBalance" to 0.0,
                "entryDate" to payment.paymentDate,
                "createdAt" to FieldValue.serverTimestamp()
            )
            batch.set(ledgerRef, ledgerPayload)
        }

        // Update Invoice balance if linked
        if (payment.invoiceId.isNotBlank()) {
            val invoiceRef = db.collection("users").document(uid).collection("invoices").document(payment.invoiceId)
            val invoiceSnapshot = invoiceRef.get().await()
            if (invoiceSnapshot.exists()) {
                val currentPaid = invoiceSnapshot.getDouble("amountPaid") ?: 0.0
                val grandTotal = invoiceSnapshot.getDouble("grandTotal") ?: 0.0
                val newPaid = currentPaid + payment.amount
                val newBalance = (grandTotal - newPaid).coerceAtLeast(0.0)
                val newStatus = if (newBalance <= 0.001) "Paid" else "Partially Paid"

                batch.update(
                    invoiceRef,
                    mapOf(
                        "amountPaid" to newPaid,
                        "balanceDue" to newBalance,
                        "status" to newStatus
                    )
                )
            }
        }

        batch.commit().await()
    }

    // --- KHATABOOK / LEDGER ---

    fun observeLedger(clientId: String? = null): Flow<List<LedgerEntry>> = flow {
        val uid = requireUserId()
        val query = if (clientId.isNullOrBlank()) {
            db.collection("users").document(uid).collection("ledger")
        } else {
            db.collection("users").document(uid).collection("ledger").whereEqualTo("clientId", clientId)
        }
        emitAll(
            query.snapshots().map { snapshot -> snapshot.toObjects(LedgerEntry::class.java) }
        )
    }

    suspend fun addManualLedgerEntry(entry: LedgerEntry) {
        val uid = requireUserId()
        val ledgerRef = db.collection("users").document(uid).collection("ledger").document()
        val batch = db.batch()

        val payload = mapOf(
            "userId" to uid,
            "clientId" to entry.clientId,
            "clientName" to entry.clientName,
            "invoiceId" to entry.invoiceId,
            "invoiceNumber" to entry.invoiceNumber,
            "entryType" to entry.entryType,
            "amount" to entry.amount,
            "paymentMethod" to entry.paymentMethod,
            "referenceNumber" to entry.referenceNumber,
            "description" to entry.description,
            "runningBalance" to entry.runningBalance,
            "entryDate" to entry.entryDate,
            "createdAt" to FieldValue.serverTimestamp()
        )
        batch.set(ledgerRef, payload)

        if (entry.clientId.isNotBlank()) {
            val clientRef = db.collection("users").document(uid).collection("clients").document(entry.clientId)
            val delta = if (entry.entryType == "DEBIT") entry.amount else -entry.amount
            val paidDelta = if (entry.entryType == "CREDIT") entry.amount else 0.0
            val billedDelta = if (entry.entryType == "DEBIT") entry.amount else 0.0

            batch.update(
                clientRef,
                mapOf(
                    "balanceDue" to FieldValue.increment(delta),
                    "totalPaid" to FieldValue.increment(paidDelta),
                    "totalBilled" to FieldValue.increment(billedDelta),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
        }

        batch.commit().await()
    }

    // --- QUOTATIONS ---

    fun observeQuotations(): Flow<List<Quotation>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("users").document(uid).collection("quotations")
                .snapshots()
                .map { snapshot -> snapshot.toObjects(Quotation::class.java) }
        )
    }

    suspend fun createQuotation(quotation: Quotation): String {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("quotations").document()
        val itemsData = quotation.items.map { item ->
            mapOf(
                "id" to item.id,
                "description" to item.description,
                "hsnSac" to item.hsnSac,
                "quantity" to item.quantity,
                "unitPrice" to item.unitPrice,
                "discountPercent" to item.discountPercent,
                "taxRate" to item.taxRate,
                "totalAmount" to item.totalAmount
            )
        }

        val payload = mapOf(
            "userId" to uid,
            "quoteNumber" to quotation.quoteNumber,
            "clientId" to quotation.clientId,
            "clientName" to quotation.clientName,
            "clientPhone" to quotation.clientPhone,
            "clientEmail" to quotation.clientEmail,
            "items" to itemsData,
            "subtotal" to quotation.subtotal,
            "taxAmount" to quotation.taxAmount,
            "grandTotal" to quotation.grandTotal,
            "validUntil" to quotation.validUntil,
            "terms" to quotation.terms,
            "status" to quotation.status,
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
        return docRef.id
    }

    suspend fun convertQuotationToInvoice(quotation: Quotation, nextInvoiceNumber: String): String {
        val uid = requireUserId()
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val invoice = Invoice(
            invoiceNumber = nextInvoiceNumber,
            clientId = quotation.clientId,
            clientName = quotation.clientName,
            clientPhone = quotation.clientPhone,
            clientEmail = quotation.clientEmail,
            invoiceDate = today,
            dueDate = quotation.validUntil.ifBlank { today },
            items = quotation.items,
            subtotal = quotation.subtotal,
            taxType = "CGST_SGST",
            cgstAmount = quotation.taxAmount / 2,
            sgstAmount = quotation.taxAmount / 2,
            totalTax = quotation.taxAmount,
            grandTotal = quotation.grandTotal,
            balanceDue = quotation.grandTotal,
            status = "Sent"
        )
        val invoiceId = createInvoice(invoice)

        // Mark quote as converted
        if (quotation.id.isNotBlank()) {
            db.collection("users").document(uid).collection("quotations").document(quotation.id)
                .update("status", "Converted").await()
        }
        return invoiceId
    }

    // --- EXPENSES ---

    fun observeExpenses(): Flow<List<Expense>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("users").document(uid).collection("expenses")
                .snapshots()
                .map { snapshot -> snapshot.toObjects(Expense::class.java) }
        )
    }

    suspend fun addExpense(expense: Expense): String {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("expenses").document()
        val payload = mapOf(
            "userId" to uid,
            "category" to expense.category,
            "title" to expense.title,
            "amount" to expense.amount,
            "paymentMethod" to expense.paymentMethod,
            "vendor" to expense.vendor,
            "notes" to expense.notes,
            "expenseDate" to expense.expenseDate,
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
        return docRef.id
    }

    suspend fun deleteExpense(expenseId: String) {
        val uid = requireUserId()
        db.collection("users").document(uid).collection("expenses").document(expenseId).delete().await()
    }

    // --- PROJECTS ---

    fun observeProjects(): Flow<List<Project>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("users").document(uid).collection("projects")
                .snapshots()
                .map { snapshot -> snapshot.toObjects(Project::class.java) }
        )
    }

    suspend fun addProject(project: Project): String {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("projects").document()
        val tasksData = project.tasks.map { mapOf("id" to it.id, "title" to it.title, "isCompleted" to it.isCompleted) }
        val payload = mapOf(
            "userId" to uid,
            "clientId" to project.clientId,
            "clientName" to project.clientName,
            "projectName" to project.projectName,
            "category" to project.category,
            "budget" to project.budget,
            "startDate" to project.startDate,
            "deadline" to project.deadline,
            "status" to project.status,
            "progressPercentage" to project.progressPercentage,
            "tasks" to tasksData,
            "notes" to project.notes,
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
        return docRef.id
    }

    suspend fun updateProject(project: Project) {
        val uid = requireUserId()
        require(project.id.isNotBlank())
        val docRef = db.collection("users").document(uid).collection("projects").document(project.id)
        val tasksData = project.tasks.map { mapOf("id" to it.id, "title" to it.title, "isCompleted" to it.isCompleted) }
        val payload = mapOf(
            "clientName" to project.clientName,
            "projectName" to project.projectName,
            "category" to project.category,
            "budget" to project.budget,
            "startDate" to project.startDate,
            "deadline" to project.deadline,
            "status" to project.status,
            "progressPercentage" to project.progressPercentage,
            "tasks" to tasksData,
            "notes" to project.notes
        )
        docRef.update(payload).await()
    }

    // --- SERVICES CATALOG ---

    fun observeServices(): Flow<List<ServiceItem>> = flow {
        val uid = requireUserId()
        emitAll(
            db.collection("users").document(uid).collection("services")
                .snapshots()
                .map { snapshot -> snapshot.toObjects(ServiceItem::class.java) }
        )
    }

    suspend fun addService(service: ServiceItem): String {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("services").document()
        val payload = mapOf(
            "userId" to uid,
            "title" to service.title,
            "category" to service.category,
            "description" to service.description,
            "basePrice" to service.basePrice,
            "defaultTaxRate" to service.defaultTaxRate,
            "billingCycle" to service.billingCycle,
            "createdAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
        return docRef.id
    }

    suspend fun loadDefaultNaxiraServices() {
        val defaultServices = listOf(
            ServiceItem(title = "Business Website", category = "Development", description = "Responsive modern business site with CMS and SEO", basePrice = 14999.0),
            ServiceItem(title = "E-Commerce Store", category = "Development", description = "Complete online store with payment gateway and cart", basePrice = 24999.0),
            ServiceItem(title = "Mobile App (Android/iOS)", category = "Development", description = "Cross-platform mobile application development", basePrice = 39999.0),
            ServiceItem(title = "SEO Optimization Package", category = "Marketing & SEO", description = "On-page, technical and local SEO ranking boost", basePrice = 9999.0, billingCycle = "Monthly"),
            ServiceItem(title = "Meta & Google Ads Campaign", category = "Advertising & Video", description = "Targeted PPC lead generation and sales conversion", basePrice = 14999.0, billingCycle = "Monthly"),
            ServiceItem(title = "Brand Identity & Strategy", category = "Design & Branding", description = "Official logo, brand guidelines, typography & palette", basePrice = 19999.0),
            ServiceItem(title = "UI/UX & Graphic Design", category = "Design & Branding", description = "High-fidelity wireframes, app UI and social creatives", basePrice = 12999.0),
            ServiceItem(title = "Hosting & Domain Maintenance", category = "Development", description = "Cloud hosting setup, SSL certificate and security updates", basePrice = 4999.0, billingCycle = "Yearly")
        )
        for (item in defaultServices) {
            addService(item)
        }
    }
}
