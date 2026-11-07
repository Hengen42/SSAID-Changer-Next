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
- 🔔 **In-App Update Checks:** Automatically checks GitHub for a newer stable release at startup. Use the update icon to check manually; when an update is available, choose to open its GitHub release page or postpone it. No root access is required for update checks.
- 🚀 **Instant, Zero-Lag App List:** Opens directly to your installed app list in under 20ms using on-demand asynchronous icon rendering (`LruCache`).
- 📌 **Smart Modified Priority:** Applications whose SSAID has been modified are automatically pinned to the very top of the list with a neon `MODIFIED` badge.
- 🔄 **Native Pull-to-Refresh:** Swipe down on the application list to instantly scan and detect newly installed applications.
- 🌐 **Interactive In-App Language Selector:** Easily switch between System Default, Turkish, and English anytime without needing to restart the app.
- 🛡️ **Two-Tier Backup &amp; Restore:**
  - **Per-App Restore:** Stores the original factory SSAID of any modified app; revert with a single tap.
  - **Full System Restore:** Automatically creates an initial backup (`.ssaidchanger.bak`); restore the entire database whenever needed.
- ⚡ **Android 12/14/15/16 ABX Compatibility:** Seamlessly handles Android Binary XML (`abx2xml` / `xml2abx`) with fallback XML parsing.
- 🔒 **Auto Entry Insertion:** Automatically creates valid XML nodes if an application has never been opened before (fixing the infamous `NO_ENTRY` crash in older tools).
- 🧹 **Optional Cache Wipe:** Includes an option to trigger `pm clear` simultaneously to flush local identity tokens.

---

## 📥 Installation & How to Use

1. **Download:** Grab the latest `SSAID-Changer-Next-vX.X.X.apk` directly from the [Releases](https://github.com/Hengen42/SSAID-Changer-Next/releases) page.
2. **Install & Grant Root:** Open the app and grant Superuser permission when prompted by KernelSU / Magisk / APatch.
3. **Select Target App:** Tap on the application you want to modify.
4. **Randomize & Apply:** Tap **Randomize** to generate a new 16-hex SSAID, ensure "Also clear app data" is checked, and tap **APPLY SPOOFED SSAID**.
5. **Reboot Phone:** Tap **Restart Phone Now** and wait for your device to reboot. Once booted, launch your target app freely!

---

## ⚠️ Important Usage Warning (Critical Tested Step)

> **WARNING:** After applying a new SSAID, **DO NOT OPEN THE TARGET APPLICATION BEFORE RESTARTING YOUR PHONE!**  
> If you launch the app before rebooting, Android will serve the stale in-memory cached state, causing the server to detect and permanently blacklist your newly assigned SSAID as well! Always reboot your phone first.

---

## 🤝 Contributing (Pull Requests)
SSAID Changer Next is an open-source project and welcomes all community contributions! Whether you want to add a new feature, optimize performance, or fix a bug:

1. Fork this repository (click the **Fork** button at the top right).
2. Create your feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m "Add amazing feature"`
4. Push to your branch: `git push origin feature/amazing-feature`
5. Open a **Pull Request (PR)** on GitHub!

All pull requests are actively reviewed and merged.

---

## 🐛 Bug Reports & Feature Requests (GitHub Issues)
Encountered a bug or have an idea to make SSAID Changer Next even better?  
Feel free to open an issue or submit a feature request on our [GitHub Issues](https://github.com/Hengen42/SSAID-Changer-Next/issues) page! Community feedback is always welcome and actively reviewed.

---

## 🙏 Credits & Acknowledgments
This project was inspired by the original concept of [A7ALABS/ssaid-changer](https://github.com/A7ALABS/ssaid-changer). 

While the original Flutter implementation introduced the idea of SSAID editing, it suffered from ABX conversion limitations, crashes when entries were missing (`NO_ENTRY`), unhandled multi-user environments, and severe UI freezes on devices with many installed apps. 

**SSAID Changer Next** is a ground-up native Android rewrite (Kotlin + Jetpack Compose) that eliminates all known bugs, introduces lazy asynchronous loading, adds comprehensive two-tier backup/restore features, and provides reliable Android 12–16 compatibility.

---

## 🛠️ Build from Source (For Developers)

```bash
git clone https://github.com/Hengen42/SSAID-Changer-Next.git
cd SSAID-Changer-Next
./gradlew assembleRelease
```
The compiled release APK will be located at:
`app/build/outputs/apk/release/SSAID-Changer-Next-v1.6.0.apk`

---

## 📜 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

Developed with ❤️ by [Hengen](https://github.com/Hengen42).
