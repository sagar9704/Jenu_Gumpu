# MVP Document: Jenu-Gumpu (Honey Producer's Collective)

## 1. Introduction
The Minimum Viable Product (MVP) for **Jenu-Gumpu** aims to provide a functional tool for tribal honey hunters to digitize their harvest process and understand the value of their product. The focus is on local data persistence, quality grading education, and basic financial calculation.

## 2. Core Features (The "Must-Haves")

### A. Harvest Management (Local)
*   **Log Harvest**: A simple form to input Date, Location (text), Quantity (in kg), and Floral Source (Dropdown: Coffee, Wildflower, etc.).
*   **Harvest History**: A scrollable list (RecyclerView) displaying all previous logs stored in the local database.
*   **Collective Summary**: A dashboard counter that shows the "Total Stock" by summing all entries in the local Room DB.

### B. Educational Grading Tool
*   **Visual Guide**: A set of screens/images showing different honey colors and what they represent.
*   **Moisture Test Steps**: A step-by-step text/image guide on how to perform a manual moisture check.
*   **Grade Selection**: Allow users to assign a "Grade" (A, B, C) to their harvest based on the guide.

### C. Profit Calculator
*   **Income Estimation**: Input for Quantity and current Retail Price.
*   **Cost Deduction**: Input for filtering, bottling, and transport costs.
*   **Net Profit Display**: Real-time calculation showing the hunter's actual take-home pay.

### D. Localization
*   **Kannada Language**: Primary UI elements (Buttons, Labels, Toasts) must be available in Kannada to ensure the app is usable by the target demographic.

## 3. Target User Flow
1.  **Onboarding**: User opens the app and selects their preferred language (English/Kannada).
2.  **Dashboard**: User sees their total collected honey and buttons for "Add Harvest", "Grading Guide", and "Calculator".
3.  **Logging**: User clicks "Add Harvest", fills the form, and saves it to the local Room DB.
4.  **Grading**: User consults the "Grading Guide" to determine the quality of their batch.
5.  **History**: User reviews their list of batches to track productivity over time.

## 4. Technical Specification (MVP Level)
*   **Architecture**: Simple Model-View-Controller or MVVM.
*   **Database**: **Room Persistence Library** (to ensure data remains on the device without internet).
*   **UI Components**:
    *   `CardView` for dashboard stats.
    *   `RecyclerView` for the harvest list.
    *   `ConstraintLayout` for responsive form designs.
*   **Resources**: Strings.xml localized for Kannada (`values-kn`).

## 5. Success Metrics for MVP
*   User can successfully save and view at least 5 harvest logs.
*   The "Total Collective Stock" updates automatically upon saving a new log.
*   The Profit Calculator provides accurate results based on the formula: `(Quantity * Price) - Total Costs`.
*   A user who speaks only Kannada can navigate to the "Add Harvest" screen.

## 6. Out of Scope (Future Enhancements)
*   **Firebase Integration**: Moving data to the cloud for real-time collective tracking across multiple devices.
*   **Image Processing**: Using the camera to automatically detect honey color/grade.
*   **Market Price API**: Fetching live prices from government agriculture portals.
*   **Community Chat**: A feature for hunters to talk to each other.

## 7. Development Roadmap (MVP Phase)
1.  **Week 1**: UI Design (XML) & Kannada Localization.
2.  **Week 2**: Room DB Implementation & Harvest Logging Logic.
3.  **Week 3**: Profit Calculator & Grading Guide UI.
4.  **Week 4**: Testing, Bug Fixing, and Final Demo Preparation.
