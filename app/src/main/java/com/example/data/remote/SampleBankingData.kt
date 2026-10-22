package com.example.data.remote

import com.example.data.model.QuestionEntity
import com.example.data.model.TestEntity

data class BankingSampleDocument(
    val filename: String,
    val title: String,
    val summary: String,
    val estimatedPages: Int,
    val sampleText: String
)

object SampleBankingData {

    val sampleDocuments = listOf(
        BankingSampleDocument(
            filename = "RBI_Master_Circular_PSL_2024.pdf",
            title = "RBI Master Direction - Priority Sector Lending (PSL) 2024-25",
            summary = "Mandatory targets for Domestic Commercial Banks, Small Finance Banks, and RRBs for Agriculture, MSME, and Weaker Sections.",
            estimatedPages = 18,
            sampleText = """
                RESERVE BANK OF INDIA - MASTER DIRECTION ON PRIORITY SECTOR LENDING (PSL)
                1. Targets / Sub-targets for Domestic Scheduled Commercial Banks:
                Total Priority Sector: 40 per cent of Adjusted Net Bank Credit (ANBC) or Credit Equivalent Amount of Off-Balance Sheet Exposure (CEOBE), whichever is higher.
                Agriculture: 18 per cent of ANBC or CEOBE, out of which a sub-target of 10 percent is prescribed for Small and Marginal Farmers (SMFs).
                Micro Enterprises: 7.5 per cent of ANBC or CEOBE.
                Advances to Weaker Sections: 12 per cent of ANBC or CEOBE.
                2. Non-achievement of PSL Targets:
                Banks having any shortfall in lending to priority sector targets shall be allocated amounts for contribution to the Rural Infrastructure Development Fund (RIDF) established with NABARD, or other funds with NABARD/NHB/SIDBI/MUDRA Ltd.
                3. MSME Definitions (as per Gazette notification 2020):
                Micro Enterprise: Investment in Plant and Machinery not exceeding Rs 1 crore and Annual Turnover not exceeding Rs 5 crore.
                Small Enterprise: Investment not exceeding Rs 10 crore and Turnover not exceeding Rs 50 crore.
                Medium Enterprise: Investment not exceeding Rs 50 crore and Turnover not exceeding Rs 250 crore.
            """.trimIndent()
        ),
        BankingSampleDocument(
            filename = "Banking_Law_and_Negotiable_Instruments_Act.pdf",
            title = "Negotiable Instruments Act 1881 & Banking Regulation Act 1949",
            summary = "Key legal provisions, Section 138 cheque dishonour, Holder in Due Course, Crossing of cheques, and Ombudsman rules.",
            estimatedPages = 24,
            sampleText = """
                BANKING LAW AND NEGOTIABLE INSTRUMENTS ACT 1881
                Section 4: Promissory Note - An instrument in writing containing an unconditional undertaking signed by the maker to pay a certain sum of money only to, or to the order of, a certain person, or to bearer.
                Section 5: Bill of Exchange - An instrument in writing containing an unconditional order signed by the maker directing a person to pay.
                Section 6: Cheque - A bill of exchange drawn on a specified banker and not expressed to be payable otherwise than on demand. It includes electronic cheques and truncated cheques.
                Section 138: Dishonour of Cheque for Insufficiency of funds in the accounts. Punishment includes imprisonment for a term which may extend to two years, or with fine which may extend to twice the amount of the cheque, or both. Notice must be sent within 30 days of receiving return memo, and payer gets 15 days to pay.
                Banking Regulation Act 1949:
                Section 21: Power of RBI to control advances by banking companies.
                Section 35A: Power of RBI to give directions in public interest or to prevent bank affairs being conducted detrimentally to depositors.
            """.trimIndent()
        ),
        BankingSampleDocument(
            filename = "Monetary_Policy_and_Reserve_Ratios_2024.pdf",
            title = "RBI Monetary Policy Framework, Liquidity & Reserve Ratios",
            summary = "Statutory Liquidity Ratio (SLR), Cash Reserve Ratio (CRR), Standing Deposit Facility (SDF), MSF, and Basel III Capital Adequacy.",
            estimatedPages = 14,
            sampleText = """
                MONETARY POLICY FRAMEWORK & RESERVE RATIOS
                1. Cash Reserve Ratio (CRR):
                Under Section 42(1) of the RBI Act, 1934, every Scheduled Commercial Bank is required to maintain with the RBI an average daily balance equivalent to a specified percentage of its Net Demand and Time Liabilities (NDTL). RBI does not pay any interest on CRR balances maintained by banks.
                2. Statutory Liquidity Ratio (SLR):
                Under Section 24 of the Banking Regulation Act, 1949, every bank is required to maintain in India specified liquid assets in gold, cash, or unencumbered approved securities. Maximum SLR ceiling is 40%.
                3. Standing Deposit Facility (SDF):
                Operates as the floor of the LAF corridor without requiring collateral securities from the RBI. SDF replaced reverse repo as the operative floor of liquidity absorption.
                4. Marginal Standing Facility (MSF):
                Penal rate at which scheduled banks can borrow additional overnight liquidity from RBI against dipping into their SLR quota up to a specified limit.
                5. Basel III Norms:
                Minimum Capital to Risk-weighted Assets Ratio (CRAR) for Scheduled Commercial Banks in India is 9% (plus Capital Conservation Buffer of 2.5%, making total regulatory requirement 11.5%).
            """.trimIndent()
        ),
        BankingSampleDocument(
            filename = "Digital_Banking_UPI_and_Cyber_Security.pdf",
            title = "Digital Payment Systems, UPI, CTS & Customer Protection Circular",
            summary = "NPCI architecture, Limit rules, Zero Customer Liability in unauthorized electronic transactions, and Ombudsman schemes.",
            estimatedPages = 16,
            sampleText = """
                DIGITAL BANKING & NPCI PAYMENT ECOSYSTEM
                1. Unified Payments Interface (UPI):
                Developed by National Payments Corporation of India (NPCI). Built over IMPS infrastructure. Standard transaction limit is Rs 1 lakh, enhanced to Rs 5 lakh for capital markets, insurance, medical/educational payments, and tax payments.
                2. Cheque Truncation System (CTS):
                Positive Pay System (PPS) introduced by RBI for cheques of Rs 50,000 and above, mandatory at banks' discretion for Rs 5 lakh and above.
                3. Customer Protection - Limiting Liability of Customers in Unauthorized Electronic Banking Transactions:
                Zero Liability: Contributory fraud/negligence on the part of the bank, or third-party breach where customer notifies the bank within 3 working days.
                Limited Liability: Where customer notifies between 4 to 7 working days, maximum liability ranges from Rs 5,000 (BSBD accounts) to Rs 25,000 (credit limits > Rs 5 lakh).
                Beyond 7 working days: Determined as per bank's board-approved policy.
            """.trimIndent()
        )
    )

    fun getInitialSeedBankingTests(): List<Pair<TestEntity, List<QuestionEntity>>> {
        return listOf(
            Pair(
                TestEntity(
                    title = "Priority Sector Lending (PSL) & MSME Norms",
                    sourceDocumentName = "RBI_Master_Circular_PSL_2024.pdf",
                    difficulty = "Medium",
                    questionCount = 5,
                    targetDurationMinutes = 10
                ),
                listOf(
                    QuestionEntity(
                        testId = 0,
                        questionText = "What is the mandatory overall Priority Sector Lending (PSL) target for Domestic Scheduled Commercial Banks in India?",
                        optionA = "30% of ANBC or CEOBE",
                        optionB = "40% of ANBC or CEOBE",
                        optionC = "50% of ANBC or CEOBE",
                        optionD = "75% of ANBC or CEOBE",
                        correctOptionIndex = 1,
                        explanation = "As per RBI Master Direction on PSL, Domestic Scheduled Commercial Banks (excluding RRBs & SFBs) must achieve 40% of Adjusted Net Bank Credit (ANBC) or Credit Equivalent of Off-Balance Sheet Exposure (CEOBE), whichever is higher."
                    ),
                    QuestionEntity(
                        testId = 0,
                        questionText = "Within the overall Agriculture lending target of 18%, what is the prescribed sub-target for Small and Marginal Farmers (SMFs)?",
                        optionA = "8 percent of ANBC",
                        optionB = "10 percent of ANBC",
                        optionC = "12 percent of ANBC",
                        optionD = "15 percent of ANBC",
                        correctOptionIndex = 1,
                        explanation = "The sub-target for Small and Marginal Farmers under Agriculture lending has been set at 10% of ANBC or CEOBE by the Reserve Bank of India."
                    ),
                    QuestionEntity(
                        testId = 0,
                        questionText = "As per the revised MSME classification criteria, an enterprise is classified as a 'Micro Enterprise' if:",
                        optionA = "Investment <= Rs 25 lakh and Turnover <= Rs 1 crore",
                        optionB = "Investment <= Rs 1 crore and Turnover <= Rs 5 crore",
                        optionC = "Investment <= Rs 5 crore and Turnover <= Rs 25 crore",
                        optionD = "Investment <= Rs 10 crore and Turnover <= Rs 50 crore",
                        correctOptionIndex = 1,
                        explanation = "Under the composite criteria effective July 1, 2020: Micro: Investment <= Rs 1 Cr and Turnover <= Rs 5 Cr; Small: Investment <= Rs 10 Cr and Turnover <= Rs 50 Cr; Medium: Investment <= Rs 50 Cr and Turnover <= Rs 250 Cr."
                    ),
                    QuestionEntity(
                        testId = 0,
                        questionText = "If a Scheduled Commercial Bank experiences a shortfall in achieving its PSL targets, the shortfall amount is allocated to which of the following funds?",
                        optionA = "Consolidated Fund of India",
                        optionB = "Rural Infrastructure Development Fund (RIDF) with NABARD",
                        optionC = "Depositor Education and Awareness (DEA) Fund",
                        optionD = "Financial Stability and Development Council Fund",
                        correctOptionIndex = 1,
                        explanation = "Banks facing shortfalls in PSL lending targets are allocated contributions to RIDF (administered by NABARD) or other specified funds with NHB, SIDBI, or MUDRA."
                    ),
                    QuestionEntity(
                        testId = 0,
                        questionText = "What is the PSL target mandated for Regional Rural Banks (RRBs) and Small Finance Banks (SFBs)?",
                        optionA = "40% of ANBC",
                        optionB = "60% of ANBC",
                        optionC = "75% of ANBC",
                        optionD = "80% of ANBC",
                        correctOptionIndex = 2,
                        explanation = "Regional Rural Banks (RRBs) and Small Finance Banks (SFBs) are mandated a higher target of 75% of their ANBC to promote financial inclusion in rural and semi-urban centers."
                    )
                )
            ),
            Pair(
                TestEntity(
                    title = "Banking Regulation & Negotiable Instruments Act",
                    sourceDocumentName = "Banking_Law_and_Negotiable_Instruments_Act.pdf",
                    difficulty = "Hard",
                    questionCount = 5,
                    targetDurationMinutes = 10
                ),
                listOf(
                    QuestionEntity(
                        testId = 0,
                        questionText = "Under Section 138 of the Negotiable Instruments Act 1881, what is the maximum imprisonment term for cheque dishonour due to insufficiency of funds?",
                        optionA = "6 months",
                        optionB = "1 year",
                        optionC = "2 years",
                        optionD = "3 years",
                        correctOptionIndex = 2,
                        explanation = "Section 138 of the NI Act 1881 specifies punishment of imprisonment for a term which may extend up to two years, or with fine which may extend to twice the cheque amount, or both."
                    ),
                    QuestionEntity(
                        testId = 0,
                        questionText = "Under Section 35A of the Banking Regulation Act 1949, RBI is empowered to:",
                        optionA = "Print currency banknotes",
                        optionB = "Issue binding directions to banking companies in public and depositor interest",
                        optionC = "Approve Union Budget deficits",
                        optionD = "Fix income tax slabs for banking staff",
                        correctOptionIndex = 1,
                        explanation = "Section 35A gives RBI wide powers to issue binding directions to banks to prevent affairs detrimental to depositors or to secure proper management."
                    ),
                    QuestionEntity(
                        testId = 0,
                        questionText = "What is the maximum statutory period within which the payee must send legal demand notice after receiving the cheque return memo?",
                        optionA = "7 days",
                        optionB = "15 days",
                        optionC = "30 days",
                        optionD = "45 days",
                        correctOptionIndex = 2,
                        explanation = "The payee or holder in due course must give notice in writing demanding payment within 30 days of the receipt of information from the bank regarding cheque return."
                    ),
                    QuestionEntity(
                        testId = 0,
                        questionText = "Which section of the Negotiable Instruments Act defines a 'Cheque' as a bill of exchange drawn on a specified banker?",
                        optionA = "Section 4",
                        optionB = "Section 5",
                        optionC = "Section 6",
                        optionD = "Section 13",
                        correctOptionIndex = 2,
                        explanation = "Section 6 defines Cheque. Section 4 defines Promissory Note, Section 5 defines Bill of Exchange, and Section 13 defines Negotiable Instrument."
                    ),
                    QuestionEntity(
                        testId = 0,
                        questionText = "A cheque crossed with two parallel transverse lines bearing the words 'Not Negotiable' between them implies:",
                        optionA = "The cheque cannot be transferred to any person under any circumstances",
                        optionB = "The transferee cannot obtain a better title than that of the transferor",
                        optionC = "The cheque must be encashed at the counter in cash",
                        optionD = "The drawer is exonerated from all liabilities",
                        correctOptionIndex = 1,
                        explanation = "Under Section 130 of the NI Act, a person taking a cheque crossed 'Not Negotiable' does not acquire and cannot give a better title than the person from whom he took it (Nemo dat quod non habet)."
                    )
                )
            ),
            Pair(
                TestEntity(
                    title = "Monetary Policy & Reserve Ratios (CRR, SLR, SDF)",
                    sourceDocumentName = "Monetary_Policy_and_Reserve_Ratios_2024.pdf",
                    difficulty = "Medium",
                    questionCount = 5,
                    targetDurationMinutes = 10
                ),
                listOf(
                    QuestionEntity(
                        testId = 0,
                        questionText = "Under which Act and Section is Cash Reserve Ratio (CRR) maintained by Scheduled Commercial Banks in India?",
                        optionA = "Section 24 of Banking Regulation Act 1949",
                        optionB = "Section 42(1) of Reserve Bank of India Act 1934",
                        optionC = "Section 18 of Companies Act 2013",
                        optionD = "Section 6 of Payment and Settlement Systems Act 2007",
                        correctOptionIndex = 1,
                        explanation = "CRR is maintained under Section 42(1) of the RBI Act 1934. In contrast, SLR is maintained under Section 24 of the Banking Regulation Act 1949."
                    ),
                    QuestionEntity(
                        testId = 0,
                        questionText = "Which liquidity absorption tool was introduced by RBI as the non-collateralized floor of the Liquidity Adjustment Facility (LAF)?",
                        optionA = "Standing Deposit Facility (SDF)",
                        optionB = "Market Stabilization Scheme (MSS)",
                        optionC = "Marginal Standing Facility (MSF)",
                        optionD = "Targeted Long Term Repo Operations (TLTRO)",
                        correctOptionIndex = 0,
                        explanation = "SDF (Standing Deposit Facility) was instituted under Section 17 of the RBI Act to absorb surplus liquidity without requiring government securities collateral from the RBI."
                    ),
                    QuestionEntity(
                        testId = 0,
                        questionText = "What interest rate does the Reserve Bank of India pay to commercial banks on their mandatory CRR balances?",
                        optionA = "Repo Rate minus 1%",
                        optionB = "Bank Rate",
                        optionC = "Reverse Repo Rate",
                        optionD = "Nil (0%)",
                        correctOptionIndex = 3,
                        explanation = "Under the RBI Act amendment of 2006, RBI does not pay any interest whatsoever on CRR balances maintained by banks."
                    ),
                    QuestionEntity(
                        testId = 0,
                        questionText = "What is the minimum Capital to Risk-weighted Assets Ratio (CRAR) mandated by RBI for Indian Scheduled Commercial Banks under Basel III norms (excluding CCB)?",
                        optionA = "8.0%",
                        optionB = "9.0%",
                        optionC = "10.5%",
                        optionD = "11.5%",
                        correctOptionIndex = 1,
                        explanation = "RBI prescribes a minimum regulatory CRAR of 9.0% for Indian Scheduled Commercial Banks (higher than the Basel committee baseline of 8.0%), plus a 2.5% Capital Conservation Buffer (total 11.5%)."
                    ),
                    QuestionEntity(
                        testId = 0,
                        questionText = "An asset is classified as a 'Sub-Standard Asset' if it has remained in the Non-Performing Asset (NPA) category for a period:",
                        optionA = "Exceeding 90 days but less than or equal to 12 months",
                        optionB = "Exceeding 12 months but less than 24 months",
                        optionC = "Exceeding 3 years",
                        optionD = "Up to 30 days only",
                        correctOptionIndex = 0,
                        explanation = "An NPA is categorized as a Sub-standard Asset for up to 12 months. Once it has remained sub-standard for more than 12 months, it is downgraded to a Doubtful Asset."
                    )
                )
            )
        )
    }
}
