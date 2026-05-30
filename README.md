# RefillGo

### *Smart Detergent Refill Platform for University Campuses*

[![Build Status](https://img.shields.io/badge/Build-Succeeded-brightgreen.svg)](#)
[![Kotlin](https://img.shields.io/badge/Kotlin-Compose-purple.svg)](#)
[![Database](https://img.shields.io/badge/Room-Offline--First-blue.svg)](#)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](#)

---

## 🍃 Project Overview

**RefillGo** is a sustainability-focused startup MVP developed for **Inha University**. The platform enables students to purchase detergent dynamically through smart refill dispensing stations instead of standard pre-packaged retail products. By reusing standard bottles, students significantly reduce single-use plastic waste and foster environmentally responsible consumption habits within dormitory halls.

Our vision is to replace the traditional linear product-to-waste cycle with an eco-conscious circular economy that directly rewards student participation through live ecological metrics and localized gamified community efforts.

---

## ⚠️ Problem Statement

University student dorms undergo immense laundry volume daily, generating thousands of discarded high-density polyethylene (HDPE) laundry bottles annually. 
1. **Single-Use Plastic Proliferation:** Traditional laundry detergent is pre-packaged in heavy, non-biodegradable containers.
2. **Economic Inefficiency:** Students must pay premium retail pricing, which heavily embeds the packaging margins, despite utilizing the detergent in highly repeated, low-quantity batches.
3. **Lack of Circular Alternatives:** Zero-waste bulk purchase locations are miles away from urban or suburban campuses.

---

## 💡 Solution

RefillGo installs smart, localized IoT-based liquid soap dispensers in central dormitory areas. 
- **Dynamic Micro-Dispensing:** Refill containers for a fraction of store costs, purchasing only the exact volume (ml) needed.
- **Zero-Contact QR Scan flow:** Seamless QR identification connecting dispensers and mobile apps immediately.
- **Ecological Impact Tracking:** Fully transparent calculators showing carbon footprint decreases, plastic grams averted, and Korean Won (₩) saved compared to store purchases.

---

## ✨ Features

*   🗺️ **Interactive WebGL Campus Maps:** High-fidelity 3D WebGL Mapbox views showing dormitory kiosk counts, fluid coordinates, and live active state statuses.
*   📸 **QR Barcode Scanner Simulator:** Smooth native scanner simulation overlays allowing simulated hardware validation steps.
*   📈 **Environmental Impact Dashboard:** High-contrast Material 3 animated counters showing total refills, plastic bottles prevented, and CO₂ averted.
*   🕒 **Refill Activities Log & Timeline:** Interactive scrollable log history backed by an offline-first Room database.
*   🔬 **Classroom Presentation Simulator:** Integrated transaction and density simulation generators, making it easy to build mock data for professor review.
*   🤖 **Generative AI Forecasting:** Integrated Gemini API predictions that analyze historic campus detergent demand and strategy advice.

---

## 🛠️ Technology Stack

*   **Language:** Kotlin (100%)
*   **UI Framework:** Jetpack Compose (Material Design 3 styling rules)
*   **Database Solution:** Room Persistence Library (Offline-first architecture)
*   **Asynchronous Flow:** Coroutines, StateFlow, & shared Flow streams
*   **Mapping UI:** WebView-backed WebGL 3D Mapbox GL JS engine integration
*   **Data Serialization:** Moshi & Kotlinx Serialization
*   **Network Client:** Retrofit & OkHttp (REST API interface connectivity)
*   **AI Integration:** Gemini API (Direct REST model calls)
*   **Potential Cloud Backend:** Supabase (Database, Auth, and Edge Functions schemas pre-declared)

---

## 📐 Architecture

RefillGo adapts a clean, unidirectional **Model-View-ViewModel (MVVM)** design flow layered with a dedicated **Repository Pattern** to enable robust, offline-first execution.

```
                           │           UI LAYER            │
                           ▼                               
                  ┌─────────────────┐                      
                  │  Jetpack Compose│                      
                  │   Screen Views  │                      
                  └────────┬────────┘                      
                           │ Collects StateFlows           
                           ▼                               
                  ┌─────────────────┐                      
                  │   ViewModels    │                      
                  │(State management│                      
                  └────────┬────────┘                      
                           │ Invokes Operations            
                           ▼                               
                           │         REPOSITORIES          │
                           ▼                               
                  ┌─────────────────┐                      
                  │  Repositories   │                      
                  │ (Unified Cache) │                      
                  └────┬───────┬────┘                      
                       │       │                           
        Queries Locale │       │ Sync (Future)             
                       ▼       ▼                           
          ┌────────────────┐ ┌────────────────┐            
          │  Room Database │ │ Supabase Cloud │            
          │ (SQLite cache) │ │ (PostgreSQL)   │            
          └────────────────┘ └────────────────┘            
```

---

## 🖼️ Screenshots Placeholder

| Campus Maps | Scan Flow | Impact Analytics | Profile Timeline |
| :---: | :---: | :---: | :---: |
| *[Map View Placeholder]* | *[Scan QR View Placeholder]* | *[Impact Metrics Placeholder]* | *[History Activity Placeholder]* |

---

## 🚀 Future Roadmap

```
  Phase 1: Inha University Pilot (Dormitories 1-3)
    │
    ▼
  Phase 2: Additional Campus Dormitory Integrations
    │
    ▼
  Phase 3: Multi-University Expansion (Yonsei, Korea University)
    │
    ▼
  Phase 4: National Domestic Deployment (Apartments & Gyms)
    │
    ▼
  Phase 5: IoT Autonomous Core Smart Refill Stations
```

---

## 👥 Founding Team

We are a diverse group of passionate builders committed to green circular campus design.

*   **CEO / Co-founder** — Javohir
*   **CTO / Co-founder** — Jahongir
*   **CMO (Chief Marketing Officer)** — Milana
*   **CFO (Chief Financial Officer)** — Begzod
*   **COO (Chief Operating Officer)** — Mukhammadazam
*   **CIO (Chief Information Officer)** — Sherzod
*   **CHRM (Chief Human Resources Manager)** — Sara

---

## 🔑 Environment Variables

To run the application with real API capabilities, you must specify environment variables in your environment or localized configurations. 

Copy the secure template file:
```bash
cp .env.example .env
```

And update `.env` with your secure external credentials:
```properties
MAPBOX_ACCESS_TOKEN=YOUR_MAPBOX_TOKEN
SUPABASE_URL=YOUR_SUPABASE_URL
SUPABASE_ANON_KEY=YOUR_SUPABASE_ANON_KEY
GEMINI_API_KEY=YOUR_GEMINI_API_KEY
```

> **Note on Security:** The secrets Gradle plugin automatically loads these credentials from `.env` directly into `BuildConfig` variables at compile time. Keep `.env` strictly ignored and out of public repositories.

---

## 📂 Folder Structure

```
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── data/             # Room Entities, DAOs, and Repositories
│   │   │   │   ├── ui/               # M3 Themes, Screens, Component views
│   │   │   │   │   ├── components/   # Map overlays and Chart UI units
│   │   │   │   │   ├── screens/      # Tab pages (Dashboard, Map, Profile, Team)
│   │   │   │   │   └── theme/        # DarkForest custom typography & palette
│   │   │   │   ├── viewmodel/        # MVVM view controllers
│   │   │   │   └── MainActivity.kt   # Main Entrypoint containing top navigation
│   │   │   └── res/                  # App drawables, XML layouts, custom strings
│   │   └── test/                     # Local Robolectric Unit Testing
│   └── build.gradle.kts              # Application level Gradle specifications
├── gradle/                           # Version catalogs (libs.versions.toml)
├── .env.example                      # Secure variable layout rules template
├── .gitignore                        # Global ignored build artifacts
└── README.md                         # This repository blueprint documentation
```

---

## 💻 How To Run

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/your-username/RefillGo.git
    cd RefillGo
    ```
2.  **Configure Local Environment:**
    Ensure you create a `.env` file containing placeholder keys as structured in `.env.example`.
3.  **Run Build System:**
    Compile outputs using Gradle:
    ```bash
    gradle assembleDebug
    ```
4.  **Launch and Install:**
    Deploy to your active physical testing device or local system emulator.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for additional details.
