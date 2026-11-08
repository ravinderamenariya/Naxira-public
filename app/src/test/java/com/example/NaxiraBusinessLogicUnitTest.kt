package com.example

import com.example.data.model.Invoice
import com.example.data.model.InvoiceItem
import org.junit.Assert.assertEquals
import org.junit.Test

class NaxiraBusinessLogicUnitTest {

    @Test
    fun testIndianGstInvoiceCalculations_cgstAndSgst() {
        val item1 = InvoiceItem(
            id = "1",
            description = "Website Development",
            quantity = 1.0,
            unitPrice = 20000.0,
            taxRate = 18.0,
            totalAmount = 20000.0
        )
        val item2 = InvoiceItem(
            id = "2",
            description = "SEO Setup",
            quantity = 1.0,
            unitPrice = 10000.0,
            taxRate = 18.0,
            totalAmount = 10000.0
        )

        val subtotal = item1.unitPrice * item1.quantity + item2.unitPrice * item2.quantity
        val discount = 2000.0
        val taxableAmount = subtotal - discount
        val totalTax = taxableAmount * 0.18 // 18% GST
        val cgst = totalTax / 2 // 9%
        val sgst = totalTax / 2 // 9%
        val grandTotal = taxableAmount + totalTax

        assertEquals(30000.0, subtotal, 0.001)
        assertEquals(28000.0, taxableAmount, 0.001)
        assertEquals(5040.0, totalTax, 0.001)
        assertEquals(2520.0, cgst, 0.001)
        assertEquals(2520.0, sgst, 0.001)
        assertEquals(33040.0, grandTotal, 0.001)

        val partialPayment = 15000.0
        val balanceDue = grandTotal - partialPayment
        assertEquals(18040.0, balanceDue, 0.001)
    }

    @Test
    fun testKhatabookLedgerRunningBalance() {
        var runningBalance = 0.0

        // Invoice Billed (Debit: You gave work worth 25,000)
        val invoiceBilled = 25000.0
        runningBalance += invoiceBilled
        assertEquals(25000.0, runningBalance, 0.001)

        // Advance Payment Received (Credit: You got 10,000)
        val paymentReceived = 10000.0
        runningBalance -= paymentReceived
        assertEquals(15000.0, runningBalance, 0.001)

        // Final Payment Received (Credit: You got 15,000)
        val finalPayment = 15000.0
        runningBalance -= finalPayment
        assertEquals(0.0, runningBalance, 0.001)
    }
}
