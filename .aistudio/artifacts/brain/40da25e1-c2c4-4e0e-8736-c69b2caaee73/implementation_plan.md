# Naxira Business Manager — Complete Agency ERP & Billing Suite

A production-grade, premium Android SaaS application tailored for digital agency NAXIRA ("illuminating your digital presence"). The app combines professional Indian GST invoicing (Swipe-style), Khatabook digital ledger management, client CRM & lead pipelines, project and task tracking, expense logs, and real-time agency analytics backed by Cloud Firestore and Google Sign-In.

### User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural decisions were confirmed during initial interactive planning and govern the system foundation:

- **Confirmed Decision 1 (Persistence & Identity)**: Backed by Cloud Firestore with Google Sign-In via Jetpack Credential Manager. All business collections are isolated per authenticated agency workspace.
- **Confirmed Decision 2 (Workspace State)**: Clean slate initialization without pre-filled demo data, ready for immediate real operational records, while providing an optional "Load Naxira Service Catalog & Defaults" action in Settings.
- **Confirmed Decision 3 (Tax & Currency)**: Indian GST billing standard enabled by default (INR ₹, CGST 9% + SGST 9% for intra-state, IGST 18% for inter-state), with automated tax breakdown, HSN/SAC support, and UPI/Bank payment details on invoices and estimates.

---

### 1. Overview & Core Concept

- **What It Does**: Naxira Business Manager delivers an all-in-one digital operating system for digital agencies, freelance studios, and creative firms. It unifies client CRM, visual sales lead pipelines, itemized Indian GST invoicing with printable/shareable A4 PDF invoices, Khatabook-style double-entry customer ledgers with running debit/credit balances, expense categorization with P&L calculations, and project milestone tracking.
- **Target Audience / Persona**: Agency founders, creative directors, account managers, and business operators who require mobile-first, zero-lag command over agency financials, client balances, and deliverable deadlines.
- **Key Value**: Replaces 4–5 disconnected apps (separate invoicing, ledger, CRM, and task trackers) with a unified, real-time agency hub sporting Naxira's official brand identity and purple design system.

---

### 2. User Experience & Visual Design

#### Key User Flows

1. **Agency Onboarding & Google Auth Gate**: User launches the app to a branded Naxira welcome screen, taps "Sign in with Google", and is seamlessly authenticated into their agency cloud workspace. If business profile settings are unconfigured, a guided prompt configures agency details (Name, GSTIN, PAN, Bank/UPI, Address).
2. **Dashboard Command Center**: Real-time aggregated KPIs (Total Revenue, Collections, Outstanding Dues, Net Profit, Active Projects, Overdue Invoices) dynamically computed from live records, with quick filter pills (Today, This Week, This Month, This Year) and one-tap Quick Action shortcuts.
3. **Client CRM & Dedicated Profile View**: Add or import clients with full GSTIN, contact, and company details. Tapping a client opens their 360° dossier: total lifetime value, unpaid dues, linked invoices, digital ledger entries, active projects, communication notes, and activity timeline.
4. **Sales Lead Pipeline (Kanban / Funnel)**: Track prospects through New Lead → Contacted → Interested → Proposal Sent → Negotiation → Converted / Lost. One-tap conversion instantly promotes any won lead into a full client record without re-typing.
5. **Swipe-Style Billing & Invoicing**: Create itemized GST or Non-GST invoices with dynamic line items, auto tax calculation (CGST/SGST/IGST), discount handling, payment terms, and bank/UPI QR details. Preview, share via WhatsApp, or generate crisp, print-ready A4 PDF documents.
6. **Quotations & Estimates**: Draft itemized agency proposals and quote packages with validity periods; one tap converts an accepted quote directly into an active invoice.
7. **Digital Khata & Ledger**: Dedicated Khatabook-style ledger per client tracking debits (invoices/work done) and credits (payments received, advances, refunds). Real-time running balance calculation, transaction filters, and printable PDF ledger statements.
8. **Expenses & P&L Analytics**: Log operating expenditures (Hosting, Software, Marketing, Salaries, Office) with categories and payment modes. The analytics engine combines total received revenue with expenses to show real net margins.
9. **Project & Task Delivery**: Track client engagements across Naxira service domains (Web Development, App Development, SEO, Branding & Design, Meta/Google Ads) with milestone progress bars, checklists, and delivery deadlines.

#### Visual Identity & Theme Tokens

- **Brand Name**: NAXIRA
- **Tagline**: *illuminating your digital presence*
- **Primary Purple**: `#30086C` (Dominant brand purple, app bar headers, primary buttons)
- **Secondary Purple**: `#8147C9` (Active indicators, floating accents, progress highlights)
- **Lavender**: `#C09AE3` (Subtle tint surfaces, chip selections, soft borders)
- **Dark Canvas**: `#12053A` (Deep dark-mode surface, high-contrast text rendering)
- **Light Canvas**: `#F8F3FC` / `#F2E3F9` (Clean, modern SaaS background for daylight readability)
- **Typography**: Clean, geometric modern typography hierarchy with high-contrast numerical readouts for financial metrics.
- **Edge-to-Edge & Navigation**: Support for top notch and bottom navigation bar insets, with a responsive bottom navigation bar and an expandable modal sheet for secondary modules.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Cloud Firestore with Scoped Workspace Collections**
  - *Chosen Approach*: Multi-collection architecture partitioned under authenticated user workspace (`/users/{uid}/clients`, `/users/{uid}/invoices`, etc.) with strict security rules enforcing owner-only read/writes.
  - *Why*: Guarantees instant cloud synchronization across devices while keeping agency financials strictly private and resilient to device loss.
  - *Alternatives Considered*: Pure local Room DB was evaluated, but user explicitly selected Cloud Firestore with Google Sign-In for multi-device availability.
- **Decision 2: Strict Double-Entry Ledger Consistency**
  - *Chosen Approach*: When a payment or invoice is recorded, Firestore batch writes update both the document and the client's aggregated balance in a single atomic operation. Financial transaction entries are append-only to preserve an immutable audit trail.
  - *Why*: Eliminates calculation discrepancies between invoices and ledger statements.
  - *Alternatives Considered*: Dynamically scanning every invoice on every screen load causes excessive reads and sluggish response times.
- **Decision 3: Native Android Canvas & PrintDocumentAdapter PDF Engine**
  - *Chosen Approach*: Generate A4 PDF invoices and Khata statements using Android's native `android.graphics.pdf.PdfDocument` with Naxira branding, clear typography, and GST itemization.
  - *Why*: Zero external heavyweight dependencies, runs 100% locally and fast, produces standard vector-sharp PDF files compatible with system print and WhatsApp sharing.

---

### 4. Technical Architecture & Data Strategy *(Technical Reference)*

#### System Architecture & Flow

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI Layer                        │
├────────────────────────────────────────────────────────────────────────┤
│  AuthScreen  │  DashboardScreen  │  ClientCRMScreen  │  InvoicesScreen │
│  KhataScreen │  LeadsScreen      │  ProjectsScreen   │  ReportsScreen  │
└───────┬────────────────────────────────────────┬───────────────────────┘
        │                                        │
        ▼                                        ▼
┌─────────────────────────────┐        ┌─────────────────────────────────┐
│     ViewModel Layer         │        │     Document & PDF Service      │
│  (StateFlow & UiStates)     │        │  (Native PdfDocument Engine,    │
│  - DashboardViewModel       │        │   A4 Template, WhatsApp Intent) │
│  - InvoicingViewModel       │        └─────────────────────────────────┘
│  - ClientKhataViewModel     │
│  - LeadsProjectsViewModel   │
└──────────────┬──────────────┘
               │
               ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        Data & Repository Layer                         │
│  - AuthRepository (Jetpack CredentialManager + FirebaseAuth)           │
│  - BusinessSettingsRepository (GSTIN, Bank, UPI, Prefix, Branding)     │
│  - InvoiceRepository (Invoices, Quotations, Items, Batch Balances)     │
│  - ClientLedgerRepository (Clients, LedgerEntries, Running Balance)    │
│  - LeadsProjectsRepository (CRM Stages, Project Milestones, Tasks)     │
│  - ExpenseReportsRepository (Categorized Expenses, P&L Queries)        │
└──────────────┬─────────────────────────────────────────────────────────┘
               │
               ▼
┌────────────────────────────────────────────────────────────────────────┐
│                  Cloud Firestore (Named Database)                      │
│  /users/{uid}/settings/business_profile                                │
│  /users/{uid}/clients/{clientId}                                       │
│  /users/{uid}/leads/{leadId}                                           │
│  /users/{uid}/invoices/{invoiceId}                                     │
│  /users/{uid}/quotations/{quoteId}                                     │
│  /users/{uid}/payments/{paymentId}                                     │
│  /users/{uid}/ledger/{ledgerId}                                        │
│  /users/{uid}/expenses/{expenseId}                                     │
│  /users/{uid}/projects/{projectId}                                     │
│  /users/{uid}/services/{serviceId}                                     │
└────────────────────────────────────────────────────────────────────────┘
```

#### Core Data Entities

- **BusinessProfile**: `agencyName`, `tagline`, `logoUri`, `phone`, `email`, `website`, `address`, `gstin`, `pan`, `bankName`, `bankAccount`, `bankIfsc`, `upiId`, `invoicePrefix`, `taxRateDefault`.
- **Client**: `id`, `name`, `contactPerson`, `phone`, `whatsapp`, `email`, `address`, `city`, `state`, `pinCode`, `gstin`, `category`, `notes`, `totalBilled`, `totalPaid`, `balanceDue`, `createdAt`.
- **Lead**: `id`, `title`, `clientName`, `contactPhone`, `email`, `source`, `dealValue`, `status` (*New, Contacted, Interested, Proposal Sent, Negotiation, Converted, Lost*), `followUpDate`, `notes`, `createdAt`.
- **Invoice**: `id`, `invoiceNumber`, `clientId`, `clientName`, `clientGstin`, `clientAddress`, `invoiceDate`, `dueDate`, `items: List<InvoiceItem>`, `subtotal`, `discountAmount`, `taxType` (*CGST_SGST / IGST / NONE*), `taxAmount`, `grandTotal`, `amountPaid`, `balanceDue`, `status` (*Draft, Sent, Partially Paid, Paid, Overdue, Cancelled*), `paymentNotes`, `createdAt`.
- **InvoiceItem**: `id`, `description`, `hsnSac`, `quantity`, `unitPrice`, `discount`, `taxRate`, `total`.
- **Quotation**: `id`, `quoteNumber`, `clientId`, `clientName`, `items`, `subtotal`, `taxAmount`, `grandTotal`, `validUntil`, `terms`, `status` (*Draft, Sent, Accepted, Declined, Converted*), `createdAt`.
- **LedgerEntry**: `id`, `clientId`, `invoiceId?`, `entryType` (*DEBIT / CREDIT*), `amount`, `paymentMethod` (*Cash, UPI, Bank Transfer, Cheque, Other*), `referenceNumber`, `description`, `runningBalance`, `entryDate`, `createdAt`.
- **Expense**: `id`, `category` (*Hosting, Domain, Software, Marketing, Salaries, Office, Internet, Travel, Other*), `title`, `amount`, `paymentMethod`, `vendor`, `notes`, `expenseDate`, `createdAt`.
- **Project**: `id`, `clientId`, `clientName`, `projectName`, `category` (*Web, App, SEO, Branding, Ads, Marketing, Custom*), `budget`, `startDate`, `deadline`, `status` (*Not Started, In Progress, Review, Completed, On Hold*), `progressPercentage`, `tasks: List<ProjectTask>`, `createdAt`.
- **ServiceItem**: `id`, `title`, `category`, `description`, `basePrice`, `defaultTaxRate`, `billingCycle` (*One-Time / Recurring*).

#### Execution Steps (Upon Approval)

1. **Phase 1: Backend Provisioning & Foundation**:
   - Provision Firebase Firestore and Authentication via `set_up_firebase` tool.
   - Configure `google-services.json`, `firebase_applet_config.xml`, `firebase.json`, and write verified `firestore.rules`.
   - Update `app/build.gradle.kts` with required Firebase dependencies and configure Android app icon using Naxira branding assets.
2. **Phase 2: Authentication & Navigation Shell**:
   - Implement CredentialManager Google Sign-In with reactive auth gate and sign-out capabilities.
   - Build Naxira M3 Design System (`Theme.kt`, `Color.kt`, purple palette tokens `#30086C`, `#8147C9`, `#12053A`).
   - Implement responsive Scaffold with Bottom Navigation + More Drawer for smooth switching across all 12 operational sections.
3. **Phase 3: Client CRM & Lead Management Pipeline**:
   - Build client management with full profile views, contact actions, search, and filtering.
   - Build interactive sales pipeline with stage columns, value counters, and 1-tap "Convert to Client" workflow.
4. **Phase 4: Indian GST Invoicing, Quotations & PDF Generation**:
   - Implement Invoice & Quote generators with dynamic item calculations (subtotal, CGST/SGST/IGST, discounts, roundoff).
   - Implement native Android PDF generator creating professional A4 Naxira invoices with printable/shareable intents.
   - Quotation to Invoice 1-tap conversion.
5. **Phase 5: Khatabook Digital Ledger & Payment Tracking**:
   - Implement client ledger with automatic credit/debit running balances and payment logs.
   - Support partial payments, payment method tracking (UPI, Bank, Cash), and ledger statement generation.
6. **Phase 6: Expenses, Projects & Business Reporting**:
   - Expense tracker with categorized outlays and monthly P&L computation.
   - Project delivery tracker with milestone percentages, tasks, and status badges.
   - Reports dashboard with revenue vs. expense charts, client-wise breakdowns, and performance metrics.
7. **Phase 7: Settings, Business Profile & Verification**:
   - Configurable agency profile (GSTIN, Bank, UPI, Address, numbering prefixes).
   - Comprehensive compilation verification via `compile_applet`.
