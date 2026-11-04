# SSAID Changer Next ⚡

<p align="center">
  <img src="app/src/main/res/drawable/ic_app_logo.xml" width="96" height="96" alt="SSAID Changer Next Logo" />
</p>

<p align="center">
  <b>Modern, Lightweight, Root-Powered Android SSAID (Android ID) Manager &amp; Spoofing Tool</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%208%20--%2016-00E5FF?style=for-the-badge&logo=android" alt="Android" />
  <img src="https://img.shields.io/badge/Root-KernelSU%20%7C%20Magisk%20%7C%20APatch-7C4DFF?style=for-the-badge" alt="Root" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-3DDC84?style=for-the-badge&logo=jetpackcompose" alt="Compose" />
  <img src="https://img.shields.io/badge/License-MIT-white?style=for-the-badge" alt="License" />
</p>

---

## 🌍 Languages / Diller
- [English](#-english)
- [Türkçe](#-türkçe)

---

## 🇬🇧 English

### What is SSAID Changer Next?
**SSAID Changer Next** is a modern native Android application written in Kotlin and Jetpack Compose. It allows rooted users (KernelSU, Magisk, APatch) to inspect, randomize, modify, and restore the **SSAID** (Settings Secure Android ID) assigned to each installed application.

### Why Do Bank Apps Blacklist Devices via SSAID?
Modern anti-tamper SDKs (such as Codevo Octopus used by Turkish banking apps like Yapı Kredi, Enpara, Garanti, etc.) record your app-specific **SSAID** to their backend servers the moment any suspicious or rooted environment is detected. 

Even after you hide root completely, reinstall the app, or flash clean ROMs, the app receives the same persistent SSAID from `/data/system/users/0/settings_ssaid.xml` and the server continues serving the "device security compromised" error screen. **Changing the application's SSAID assigns your device a completely fresh identity, bypassing persistent server-side device bans.**

### ✨ Features
- 🚀 **Instant, Zero-Lag App Picker:** Loads 300+ applications in under 20ms using on-demand asynchronous icon rendering.
- 📌 **Smart Modified Priority:** Applications whose SSAID has been modified are automatically pinned to the very top of the list with a neon `MODIFIED` badge.
- 🌐 **Interactive In-App Language Selector:** Easily switch between System Default, Turkish, and English anytime.
- 🛡️ **Two-Tier Backup &amp; Restore:**
  - **Per-App Restore:** Stores the original factory SSAID of any modified app; revert with a single tap.
  - **Full System Restore:** Automatically creates an initial backup (`.ssaidchanger.bak`); restore the entire database whenever needed.
- ⚠️ **Critical Restart Warning:** Prevents accidental blacklisting of newly assigned SSAIDs by enforcing device restart prior to launching target apps.
- ⚡ **Android 12/14/15/16 ABX Compatibility:** Seamlessly handles Android Binary XML (`abx2xml` / `xml2abx`) with fallback XML parsing.
- 🔒 **Auto Entry Insertion:** Automatically creates valid XML nodes if an application has never been opened before (fixing the infamous `NO_ENTRY` crash in older tools).
- 🧹 **Optional Cache Wipe:** Includes an option to trigger `pm clear` simultaneously to flush local identity tokens.

---

## 🇹🇷 Türkçe

### SSAID Changer Next Nedir?
**SSAID Changer Next**, Kotlin ve Jetpack Compose ile modern Android standartlarında sıfırdan geliştirilmiş bir SSAID (Android ID) yöneticisi ve kimlik değiştirme aracıdır. Root yetkisine sahip cihazlarda (KernelSU, Magisk, APatch) yüklü uygulamaların benzersiz cihaz kimliklerini görüntülemeyi, rastgele değiştirmeyi ve orijinal haline döndürmeyi sağlar.

### Banka Uygulamaları Neden SSAID Üzerinden Cihazı Engeller?
Yerli bankacılık uygulamalarının (Yapı Kredi, Enpara vb.) kullandığı Codevo Octopus gibi güvenlik SDK'ları, cihazda ilk kez root veya şüpheli bir iz tespit ettiğinde o uygulamaya ait **SSAID** numarasını doğrudan sunucularına kaydeder.

Sonradan rootu gizleseniz, hatta uygulamayı silip baştan yükleseniz bile Android işletim sistemi uygulamaya aynı SSAID'yi verir (`/data/system/users/0/settings_ssaid.xml`). Sunucu aynı kimliği gördüğü için "güvenlik gerekçesiyle hizmet durduruldu" uyarısını göstermeye devam eder. **SSAID Changer Next ile o uygulamanın SSAID değeri değiştirildiğinde; banka sunucusu telefonunuzu yepyeni, ilk defa açılan tertemiz bir cihaz olarak tanır.**

### ✨ Öne Çıkan Özellikler
- 🚀 **Işık Hızında Uygulama Listesi:** 300'den fazla uygulamayı 20 milisaniyede donma olmadan açar; ikonları sadece ekranda göründükçe yükler (Lazy Loading).
- 📌 **Değiştirilen Uygulamalar En Üstte:** SSAID kimliği değiştirilmiş uygulamalar listenin en tepesine `DEĞİŞTİRİLDİ` rozetiyle sabitlenir.
- 🌐 **Uygulama İçi Dil Değiştirici:** İster sistem diliyle otomatik, ister Türkçe veya İngilizce olarak anında geçiş yapabilirsiniz.
- 🛡️ **İki Kademeli Güvenli Yedekleme:**
  - **Uygulama Bazlı Orijinale Dönüş:** Her uygulamanın ilk fabrika kimliği saklanır; tek dokunuşla geri alınabilir.
  - **Tam Sistem Yedeği:** İlk çalıştırmada tüm dosyanın yedeğini alır (`.ssaidchanger.bak`); istenirse tek tıkla ilk güne döner.
- ⚠️ **Kritik Yeniden Başlatma Koruması:** Yeni SSAID verildikten sonra telefon yeniden başlatılmadan banka uygulamasının açılmasını engelleyecek hayati uyarıyı görüntüler (yeni kimliğin de banlanmasını önler).

---

## 🛠️ Building & Installation / Derleme ve Kurulum

### Requirements / Gereksinimler
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
`app/build/outputs/apk/release/SSAID-Changer-Next-v1.3.0.apk`

---

## 📜 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

Developed with ❤️ by [Hengen](https://github.com/Hengen42).
