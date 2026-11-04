# SSAID Changer Next ⚡

<p align="center">
  <img src="assets/logo.svg" width="96" height="96" alt="SSAID Changer Next Logo" />
</p>

<p align="center">
  <b>Modern, Lightweight, Root-Powered Android SSAID (Android ID) Manager &amp; Modifier</b>
</p>

<p align="center">
  <a href="README.md"><b>English Documentation</b></a> • <a href="README_TR.md"><b>Türkçe Dokümantasyon</b></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%208%20--%2016-00E5FF?style=for-the-badge&logo=android" alt="Android" />
  <img src="https://img.shields.io/badge/Root-KernelSU%20%7C%20Magisk%20%7C%20APatch-7C4DFF?style=for-the-badge" alt="Root" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-3DDC84?style=for-the-badge&logo=jetpackcompose" alt="Compose" />
  <img src="https://img.shields.io/badge/License-MIT-white?style=for-the-badge" alt="License" />
</p>

---

## What is SSAID Changer Next?
**SSAID Changer Next** is a modern native Android application built from scratch with Kotlin and Jetpack Compose. It allows rooted devices (KernelSU, Magisk, APatch) to inspect, randomize, modify, and restore the per-app **SSAID** (Settings Secure Android ID) stored in `/data/system/users/0/settings_ssaid.xml`.

### Why Do Certain Apps Block Devices via SSAID?
Certain applications (especially those performing aggressive environment checks and utilizing anti-tamper SDKs such as Codevo Octopus) record the app-specific **SSAID** to their backend servers the instant any root or system modification is observed.

Even after you hide root completely, reinstall the app, or wipe app caches, Android serves the exact same persistent SSAID from the system database. The server recognizes this blacklisted ID and continues blocking access. 

**SSAID Changer Next modifies this value directly in the system database, presenting your device to the target application with a clean, freshly minted identity.**

---

## ✨ Features
- 🚀 **Instant, Zero-Lag App Picker:** Loads 300+ applications in under 20ms using on-demand asynchronous icon rendering (`LruCache`).
- 📌 **Smart Modified Priority:** Applications whose SSAID has been modified are automatically pinned to the very top of the list with a neon `MODIFIED` badge.
- 🌐 **Interactive In-App Language Selector:** Easily switch between System Default, Turkish, and English anytime without needing to restart the app.
- 🛡️ **Two-Tier Backup &amp; Restore:**
  - **Per-App Restore:** Stores the original factory SSAID of any modified app; revert with a single tap.
  - **Full System Restore:** Automatically creates an initial backup (`.ssaidchanger.bak`); restore the entire database whenever needed.
- ⚡ **Android 12/14/15/16 ABX Compatibility:** Seamlessly handles Android Binary XML (`abx2xml` / `xml2abx`) with fallback XML parsing.
- 🔒 **Auto Entry Insertion:** Automatically creates valid XML nodes if an application has never been opened before (fixing the infamous `NO_ENTRY` crash in older tools).
- 🧹 **Optional Cache Wipe:** Includes an option to trigger `pm clear` simultaneously to flush local identity tokens.

---

## ⚠️ Important Usage Warning (Critical Tested Step)

> **WARNING:** After applying a new SSAID, **DO NOT OPEN THE TARGET APPLICATION BEFORE RESTARTING YOUR PHONE!**  
> If you launch the app before rebooting, Android will serve the stale in-memory cached state, causing the server to detect and permanently blacklist your newly assigned SSAID as well! Always reboot (or perform a soft reboot) first.

---

## 🙏 Credits & Acknowledgments
This project was inspired by the original concept of [A7ALABS/ssaid-changer](https://github.com/A7ALABS/ssaid-changer). 

While the original Flutter implementation introduced the idea of SSAID editing, it suffered from ABX conversion limitations, crashes when entries were missing (`NO_ENTRY`), unhandled multi-user environments, and severe UI freezes on devices with many installed apps. 

**SSAID Changer Next** is a ground-up native Android rewrite (Kotlin + Jetpack Compose) that eliminates all known bugs, introduces lazy asynchronous loading, adds comprehensive two-tier backup/restore features, and provides reliable Android 12–16 compatibility.

---

## 🛠️ Building & Installation

### Requirements
- Android 8.0+ (API 26+)
- Root Access (KernelSU / Magisk / APatch)
- JDK 17 & Android SDK

### Build via Gradle
```bash
# Clone the repository
git clone https://github.com/Hengen42/SSAID-Changer-Next.git
cd SSAID-Changer-Next

# Build Debug APK
./gradlew assembleDebug

# Build Release APK
./gradlew assembleRelease
```
The compiled APK will be at:
`app/build/outputs/apk/release/SSAID-Changer-Next-v1.4.0.apk`

---

## 📜 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

Developed with ❤️ by [Hengen](https://github.com/Hengen42).
