# ExamCrafter AI - PDF to MCQ Test Generator & Performance Analytics

An educational mobile application that transforms study PDFs into structured multiple-choice question tests, offers timed testing with instant reviews, and tracks student mastery through a visual analytics dashboard.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following key decisions were confirmed during the interactive interview and will guide the build:

- **MCQ Generation Configuration**: Users select test name, target question count (e.g., 5, 10, 15, or up to daily limit), and difficulty level (*Easy*, *Medium*, *Hard*, or *Mixed*) before Gemini parses the uploaded PDF.
- **Payment & Pro Activation**: Users can pay Rs 499 via UPI to `XXXXXXXXX@UPI` to unlock unlimited daily generations and priority support. Pro tier unlocks instantly via an activation code, with direct WhatsApp link to `+91 9512808883`.
- **Exam Experience & Analytics**: Students take tests in a focused timed exam mode with countdown timer, flag/bookmarking, and submit confirmation, followed by detailed performance scoring, question explanations, and trend analytics.

---

## 1. Overview & Core Concept

- **What It Does**: Enables students and educators to upload any syllabus, notes, or chapter PDF (`application/pdf` only) and name their exam. The app submits the document to Gemini AI to generate high-quality MCQs with 4 distinct options, correct answers, and thorough explanations. Users attempt tests in a realistic exam simulator and analyze results on an intuitive performance dashboard.
- **Target Audience**: High school, college, and competitive exam aspirants (e.g., JEE, NEET, UPSC, SAT, GRE) who want fast, interactive revision from lecture notes and textbook PDFs.
- **Daily Quotas & Monetization**:
  - **Free Tier**: 20 questions generated per calendar day with automatic midnight resets and an interactive quota gauge.
  - **Pro Tier (Rs 499)**: Unlimited question generation, unlimited saved tests, detailed mastery metrics, and priority WhatsApp assistance (`9512808883`).

---

## 2. User Experience & Visual Design

### Key User Flows

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│ PDF Upload &    │  ───> │ Question Count  │  ───> │ AI Processing   │
│ Test Naming     │       │ & Difficulty    │       │ & Generation    │
└─────────────────┘       └─────────────────┘       └─────────────────┘
                                                             │
                                                             ▼
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│ Analytics       │  <─── │ Score Breakdown │  <─── │ Timed Exam      │
│ Dashboard       │       │ & Explanations  │       │ Simulator       │
└─────────────────┘       └─────────────────┘       └─────────────────┘
```

1. **Dashboard (Home)**:
   - Daily quota tracker (e.g., "15 / 20 questions used today" with a circular progress gauge).
   - "Create New Test" hero action button.
   - Recent test attempts summary cards showing date, score percentage, and accuracy badges.
   - Overall mastery metrics: Tests Attempted, Average Score %, Total Questions Solved, and Strongest/Weakest areas.
2. **Upload & Test Setup Sheet**:
   - Strict PDF file picker (`application/pdf`) with file size validation, selected file chip, and sample PDF option for instant testing.
   - Mandatory test title input (e.g., "Organic Chemistry Chapter 4").
   - Question count selector (5, 10, 15, or 20 questions) and difficulty chips (Easy, Medium, Hard, Mixed).
   - Quota check feedback warning if requested questions exceed daily quota.
3. **Timed Exam Screen**:
   - Active countdown timer with warning colors when under 2 minutes.
   - Question number pagination pill bar with answered/flagged indicators.
   - Clean, readable typography for question text and 4 selectable option cards with subtle ripple and elevation.
   - Action bar: "Flag for Review", "Previous", "Next", and "Submit Exam".
4. **Result & Explanation Screen**:
   - Circular score summary with letter grade, time spent, and percentage.
   - Interactive review list with color-coded answers (green for correct, red for incorrect) and detailed explanations.
   - Options to retake test, review mistakes, or return to Dashboard.
5. **Pro Upgrade & Support Screen**:
   - Benefit cards: Unlimited daily questions, lifetime access, priority test generation.
   - UPI payment details card featuring UPI ID `XXXXXXXXX@UPI`, one-tap copy button, and QR code representation.
   - Instant activation code entry field with instant unlock confirmation.
   - Direct "Chat on WhatsApp" action button targeting phone number `+91 9512808883`.

### Visual Identity & Theme

- **Aesthetic Direction**: Academic tech elegance — clean, distraction-free surfaces with purposeful accents that keep focus on reading and test taking.
- **Color Palette**:
  - Primary: Deep Indigo (`#1A237E` / `#3949AB`) representing trust, intellect, and educational focus.
  - Accent / Pro: Royal Amber Gold (`#FFA000` / `#FFC107`) for Pro tier perks and high-score achievements.
  - Surface & Background: Crisp soft pearl (`#F8F9FA` in light, `#121212` in dark) with clean container borders (`#E0E0E0`).
  - Feedback: Emerald Success (`#2E7D32`), Crimson Error (`#C62828`), Amber Caution (`#ED6C02`).
- **Typography & Layout**:
  - High-legibility sans-serif with ample line height for long question stems and explanations.
  - Touch-friendly 48dp option cards with generous padding and clear radio indicators.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Direct Multimodal PDF to Gemini REST API vs Local PDF Parsing**
  - *Chosen Approach*: Send PDF bytes as inline base64 data directly to Gemini (`gemini-3.5-flash`) using structured JSON output schema.
  - *Why*: Gemini natively analyzes complex layout, charts, textbook typography, and multi-column PDFs without relying on fragile on-device regex text extraction.
  - *Fallback*: Built-in curated syllabus sample tests to guarantee instantaneous offline test creation and evaluation even before uploading custom documents.
- **Decision 2: Daily Quota & Pro Activation Architecture**
  - *Chosen Approach*: Room Database tracks daily usage with automated calendar day resets, combined with an activation code validator and payment reference logger.
  - *Why*: Ensures seamless offline access to all previously created tests and attempts, prevents server dependency for student test revision, and provides instant activation without complex login hurdles.
- **Decision 3: Test State Preservation**
  - *Chosen Approach*: Exam session state is tracked in the ViewModel with automatic answer caching so users don't lose progress if they accidentally rotate their phone or switch apps.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│                      Compose UI                        │
│   Dashboard  │  UploadDialog  │  ExamScreen  │ ProPay  │
└──────────────────────────┬─────────────────────────────┘
                           │ StateFlow & Events
┌──────────────────────────▼─────────────────────────────┐
│                    MainViewModel                       │
│    - Quota Management    - Active Exam State           │
│    - Analytics Aggregator - Pro Status Verification    │
└──────────────┬───────────────────────────┬─────────────┘
               │                           │
┌──────────────▼─────────────┐ ┌───────────▼─────────────┐
│       Gemini Service       │ │     Room Local DB       │
│  - Multimodal PDF to JSON  │ │  - Tests & MCQs         │
│  - Educational Prompting   │ │  - Test Attempts        │
│  - Strict JSON Validation  │ │  - Quota & Pro Records  │
└────────────────────────────┘ └─────────────────────────┘
```

### Data Model & Entities

- **TestEntity**: Unique ID, test title, source document name, creation timestamp, question count, difficulty.
- **QuestionEntity**: Test ID, question text, 4 choices (`A`, `B`, `C`, `D`), correct choice index, explanation.
- **AttemptEntity**: Unique attempt ID, test ID, timestamp, score, total questions, time taken in seconds, user selected answers JSON.
- **UserQuotaEntity**: Date string (`YYYY-MM-DD`), questions generated today, pro status boolean, pro activation timestamp.

### Interactive State Transitions

- **Upload Flow**: Selected PDF Uri -> Reading input stream -> Gemini REST request -> Parse JSON array into Question models -> Store in Room DB -> Open newly created test.
- **Exam Flow**: Start test -> Start countdown timer -> Select option -> Next/Previous question -> Review flagged items -> Confirm submission -> Calculate score -> Save Attempt in DB -> Navigate to Results.
- **Pro Flow**: View UPI details -> Click "Copy UPI ID" -> Enter activation code or tap "Verify via WhatsApp" -> Pro status updated -> Daily limit lifted to Unlimited.
