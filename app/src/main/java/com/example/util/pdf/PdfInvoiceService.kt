package com.example.util.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.BusinessProfile
import com.example.data.model.Client
import com.example.data.model.Invoice
import com.example.data.model.LedgerEntry
import java.io.File
import java.io.FileOutputStream

object PdfInvoiceService {

    fun generateInvoicePdf(
        context: Context,
        profile: BusinessProfile,
        invoice: Invoice
    ): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 points (595x842)
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val primaryPaint = Paint().apply {
            color = Color.parseColor("#30086C")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val secondaryPaint = Paint().apply {
            color = Color.parseColor("#8147C9")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.parseColor("#1D0B52")
            textSize = 10f
            isAntiAlias = true
        }

        val boldPaint = Paint().apply {
            color = Color.parseColor("#1D0B52")
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = Color.parseColor("#C09AE3")
            textSize = 9f
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.parseColor("#E0D4F0")
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }

        // Header Banner (Naxira Brand Purple)
        canvas.drawRect(0f, 0f, 595f, 90f, primaryPaint)
        canvas.drawText(profile.agencyName.ifBlank { "NAXIRA" }, 40f, 40f, titlePaint)
        canvas.drawText(profile.tagline.ifBlank { "illuminating your digital presence" }, 40f, 58f, subtitlePaint)
        canvas.drawText("TAX INVOICE", 450f, 45f, titlePaint)
        
        val statusPaint = Paint().apply {
            color = Color.parseColor("#8147C9")
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Status: ${invoice.status.uppercase()}", 450f, 65f, subtitlePaint)

        // Agency & Client Details Grid
        var y = 115f
        canvas.drawText("BILLED BY:", 40f, y, boldPaint)
        canvas.drawText("BILLED TO:", 320f, y, boldPaint)

        y += 15f
        canvas.drawText(profile.agencyName, 40f, y, textPaint)
        canvas.drawText(invoice.clientName, 320f, y, textPaint)

        y += 13f
        canvas.drawText(profile.address, 40f, y, textPaint)
        if (invoice.clientAddress.isNotBlank()) canvas.drawText(invoice.clientAddress, 320f, y, textPaint)

        y += 13f
        canvas.drawText("Phone: ${profile.phone}", 40f, y, textPaint)
        if (invoice.clientPhone.isNotBlank()) canvas.drawText("Phone: ${invoice.clientPhone}", 320f, y, textPaint)

        y += 13f
        if (profile.gstin.isNotBlank()) canvas.drawText("GSTIN: ${profile.gstin}", 40f, y, textPaint)
        if (invoice.clientGstin.isNotBlank()) canvas.drawText("GSTIN: ${invoice.clientGstin}", 320f, y, textPaint)

        y += 20f
        // Invoice metadata bar
        canvas.drawRect(40f, y, 555f, y + 26f, Paint().apply { color = Color.parseColor("#F4EDFA") })
        canvas.drawText("Invoice No: ${invoice.invoiceNumber}", 50f, y + 17f, boldPaint)
        canvas.drawText("Date: ${invoice.invoiceDate}", 240f, y + 17f, textPaint)
        canvas.drawText("Due: ${invoice.dueDate}", 420f, y + 17f, textPaint)

        y += 45f
        // Items Table Header
        canvas.drawRect(40f, y, 555f, y + 22f, secondaryPaint)
        val headerTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("ITEM / DESCRIPTION", 50f, y + 15f, headerTextPaint)
        canvas.drawText("SAC/HSN", 250f, y + 15f, headerTextPaint)
        canvas.drawText("QTY", 330f, y + 15f, headerTextPaint)
        canvas.drawText("RATE (₹)", 380f, y + 15f, headerTextPaint)
        canvas.drawText("TAX %", 450f, y + 15f, headerTextPaint)
        canvas.drawText("TOTAL (₹)", 495f, y + 15f, headerTextPaint)

        y += 22f
        // Items Table Rows
        invoice.items.forEach { item ->
            y += 18f
            canvas.drawText(item.description, 50f, y, textPaint)
            canvas.drawText(item.hsnSac, 250f, y, textPaint)
            canvas.drawText("${item.quantity}", 330f, y, textPaint)
            canvas.drawText(String.format("%.2f", item.unitPrice), 380f, y, textPaint)
            canvas.drawText("${item.taxRate}%", 450f, y, textPaint)
            canvas.drawText(String.format("%.2f", item.totalAmount), 495f, y, boldPaint)
            y += 6f
            canvas.drawLine(40f, y, 555f, y, linePaint)
        }

        // Totals Box
        y += 20f
        val totalsX = 350f
        canvas.drawText("Subtotal:", totalsX, y, textPaint)
        canvas.drawText("₹ ${String.format("%.2f", invoice.subtotal)}", 480f, y, textPaint)

        if (invoice.discountAmount > 0) {
            y += 14f
            canvas.drawText("Discount:", totalsX, y, textPaint)
            canvas.drawText("- ₹ ${String.format("%.2f", invoice.discountAmount)}", 480f, y, textPaint)
        }

        if (invoice.taxType == "CGST_SGST") {
            y += 14f
            canvas.drawText("CGST (9%):", totalsX, y, textPaint)
            canvas.drawText("₹ ${String.format("%.2f", invoice.cgstAmount)}", 480f, y, textPaint)
            y += 14f
            canvas.drawText("SGST (9%):", totalsX, y, textPaint)
            canvas.drawText("₹ ${String.format("%.2f", invoice.sgstAmount)}", 480f, y, textPaint)
        } else if (invoice.taxType == "IGST") {
            y += 14f
            canvas.drawText("IGST (18%):", totalsX, y, textPaint)
            canvas.drawText("₹ ${String.format("%.2f", invoice.igstAmount)}", 480f, y, textPaint)
        }

        y += 18f
        canvas.drawRect(totalsX - 10f, y - 12f, 555f, y + 16f, Paint().apply { color = Color.parseColor("#30086C") })
        val grandTotalTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("Grand Total:", totalsX, y + 7f, grandTotalTextPaint)
        canvas.drawText("₹ ${String.format("%.2f", invoice.grandTotal)}", 470f, y + 7f, grandTotalTextPaint)

        y += 25f
        canvas.drawText("Amount Paid: ₹ ${String.format("%.2f", invoice.amountPaid)}", totalsX, y, boldPaint)
        y += 14f
        val balancePaint = Paint().apply {
            color = if (invoice.balanceDue > 0) Color.parseColor("#DE350B") else Color.parseColor("#00875A")
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Balance Due: ₹ ${String.format("%.2f", invoice.balanceDue)}", totalsX, y, balancePaint)

        // Payment Info & Bank Details
        val bankY = y - 70f
        canvas.drawText("PAYMENT & BANK DETAILS", 40f, bankY, boldPaint)
        canvas.drawText("Bank: ${profile.bankName}", 40f, bankY + 14f, textPaint)
        if (profile.bankAccount.isNotBlank()) canvas.drawText("A/C No: ${profile.bankAccount}", 40f, bankY + 27f, textPaint)
        if (profile.bankIfsc.isNotBlank()) canvas.drawText("IFSC: ${profile.bankIfsc}", 40f, bankY + 40f, textPaint)
        if (profile.upiId.isNotBlank()) canvas.drawText("UPI ID: ${profile.upiId}", 40f, bankY + 53f, boldPaint)

        // Footer Note
        canvas.drawLine(40f, 790f, 555f, 790f, linePaint)
        val footerPaint = Paint().apply {
            color = Color.GRAY
            textSize = 8.5f
            isAntiAlias = true
        }
        canvas.drawText("Terms: ${invoice.paymentTerms}", 40f, 804f, footerPaint)
        canvas.drawText("Thank you for choosing NAXIRA • www.naxira.in", 40f, 818f, footerPaint)

        pdfDocument.finishPage(page)

        val outputDir = File(context.cacheDir, "invoices").apply { mkdirs() }
        val outputFile = File(outputDir, "Invoice_${invoice.invoiceNumber}.pdf")
        FileOutputStream(outputFile).use { pdfDocument.writeTo(it) }
        pdfDocument.close()

        return outputFile
    }

    fun generateKhataStatementPdf(
        context: Context,
        profile: BusinessProfile,
        client: Client,
        entries: List<LedgerEntry>
    ): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val primaryPaint = Paint().apply {
            color = Color.parseColor("#30086C")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.parseColor("#1D0B52")
            textSize = 9.5f
            isAntiAlias = true
        }

        val boldPaint = Paint().apply {
            color = Color.parseColor("#1D0B52")
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = Color.parseColor("#C09AE3")
            textSize = 9f
            isAntiAlias = true
        }

        // Header
        canvas.drawRect(0f, 0f, 595f, 85f, primaryPaint)
        canvas.drawText(profile.agencyName, 40f, 38f, titlePaint)
        canvas.drawText("CUSTOMER LEDGER / KHATA STATEMENT", 40f, 56f, subtitlePaint)
        canvas.drawText("NET DUE: ₹ ${String.format("%.2f", client.balanceDue)}", 400f, 45f, titlePaint)

        var y = 110f
        canvas.drawText("Client: ${client.name}", 40f, y, boldPaint)
        canvas.drawText("Contact: ${client.contactPerson} (${client.phone})", 40f, y + 14f, textPaint)
        if (client.gstin.isNotBlank()) canvas.drawText("GSTIN: ${client.gstin}", 40f, y + 27f, textPaint)

        y += 45f
        // Table Header
        canvas.drawRect(40f, y, 555f, y + 22f, Paint().apply { color = Color.parseColor("#8147C9") })
        val thPaint = Paint().apply {
            color = Color.WHITE
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("DATE", 50f, y + 15f, thPaint)
        canvas.drawText("DESCRIPTION", 130f, y + 15f, thPaint)
        canvas.drawText("MODE", 300f, y + 15f, thPaint)
        canvas.drawText("DEBIT (₹)", 370f, y + 15f, thPaint)
        canvas.drawText("CREDIT (₹)", 440f, y + 15f, thPaint)
        canvas.drawText("BALANCE (₹)", 500f, y + 15f, thPaint)

        y += 22f
        val linePaint = Paint().apply {
            color = Color.parseColor("#EAE0F5")
            strokeWidth = 0.8f
        }

        entries.take(28).forEach { entry ->
            y += 18f
            canvas.drawText(entry.entryDate, 50f, y, textPaint)
            val desc = if (entry.description.length > 25) entry.description.take(25) + "..." else entry.description
            canvas.drawText(desc, 130f, y, textPaint)
            canvas.drawText(entry.paymentMethod.ifBlank { "-" }, 300f, y, textPaint)
            
            if (entry.entryType == "DEBIT") {
                canvas.drawText(String.format("%.2f", entry.amount), 370f, y, boldPaint)
                canvas.drawText("-", 450f, y, textPaint)
            } else {
                canvas.drawText("-", 380f, y, textPaint)
                canvas.drawText(String.format("%.2f", entry.amount), 440f, y, boldPaint)
            }
            canvas.drawText(String.format("%.2f", entry.runningBalance), 500f, y, textPaint)
            y += 5f
            canvas.drawLine(40f, y, 555f, y, linePaint)
        }

        pdfDocument.finishPage(page)

        val outputDir = File(context.cacheDir, "invoices").apply { mkdirs() }
        val sanitizedClientName = client.name.replace(" ", "_").replace("/", "_")
        val outputFile = File(outputDir, "Ledger_${sanitizedClientName}.pdf")
        FileOutputStream(outputFile).use { pdfDocument.writeTo(it) }
        pdfDocument.close()

        return outputFile
    }

    fun sharePdf(context: Context, pdfFile: File, title: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share $title"))
    }
}
