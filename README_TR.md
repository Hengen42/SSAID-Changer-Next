# SSAID Changer Next ⚡ (Türkçe Rehber)

<p align="center">
  <img src="app/src/main/res/drawable/ic_app_logo.xml" width="96" height="96" alt="SSAID Changer Next Logo" />
</p>

<p align="center">
  <b>Modern, Hafif ve Root Destekli Android SSAID (Android ID) Yöneticisi &amp; Kimlik Değiştirme Aracı</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%208%20--%2016-00E5FF?style=for-the-badge&logo=android" alt="Android" />
  <img src="https://img.shields.io/badge/Root-KernelSU%20%7C%20Magisk%20%7C%20APatch-7C4DFF?style=for-the-badge" alt="Root" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-3DDC84?style=for-the-badge&logo=jetpackcompose" alt="Compose" />
  <img src="https://img.shields.io/badge/Lisans-MIT-white?style=for-the-badge" alt="Lisans" />
</p>

---

## 📖 Genel Bakış

**SSAID Changer Next**, root yetkisine sahip Android cihazlarda (KernelSU, Magisk, APatch) her bir uygulamanın kendine özel **SSAID** (Settings Secure Android ID) değerini görüntülemeyi, rastgele değiştirmeyi (spoof) ve dilediğiniz an orijinal fabrika haline geri döndürmeyi sağlayan açık kaynaklı bir Android uygulamasıdır.

### ❓ Banka Uygulamaları Cihazı Nasıl Tanır ve Engeller?
Türkiye'deki yerli bankacılık uygulamalarının (Yapı Kredi, Enpara, Garanti vb.) kullandığı **Codevo Octopus** gibi anti-tamper güvenlik sistemleri, cihazda root veya modifiye edilmiş bir sistem tespit ettiği anda o uygulamaya atanmış olan benzersiz **SSAID** kimliğini kendi sunucularına "Güvensiz Cihaz" olarak kaydeder.

Sonradan root'u gizleseniz, hatta uygulamayı silip baştan yükleseniz bile Android işletim sistemi uygulamaya aynı dosyadaki (`/data/system/users/0/settings_ssaid.xml`) sabit kimliği verir. Banka sunucusu bu kimliği tanıdığı için "güvenlik gerekçesiyle hizmet durduruldu" ekranını göstermeye devam eder.

**SSAID Changer Next ile o uygulamanın SSAID değeri değiştirildiğinde; banka sunucusu telefonunuzu yepyeni, ilk defa açılan tertemiz bir cihaz olarak algılar ve engeli kaldırır.**

---

## ✨ Temel Özellikler

- ⚡ **Işık Hızında Uygulama Seçimi:** 300'den fazla uygulamayı 20 milisaniyede donma olmadan listeler (Asenkron LruCache ile bellek optimizasyonu).
- 📌 **Değiştirilen Uygulamalar En Üstte:** SSAID kimliği değiştirilmiş uygulamalar listenin en tepesine `DEĞİŞTİRİLDİ` rozetiyle sabitlenir.
- 🌐 **Uygulama İçi Dil Değiştirici:** İster sistem diliyle otomatik, ister Türkçe veya İngilizce olarak anında geçiş yapabilirsiniz.
- 🛡️ **İki Kademeli Güvenli Yedekleme:**
  - **Uygulama Bazlı Orijinale Dönüş:** Her uygulamanın ilk fabrika kimliği saklanır; tek tıkla geri alınabilir.
  - **Tam Sistem Geri Yükleme:** İlk açılışta `.ssaidchanger.bak` olarak tam sistem yedeği alınır.
- ⚠️ **Kritik Yeniden Başlatma Uyarısı:** Yeni SSAID verildikten sonra telefon yeniden başlatılmadan banka uygulamasının açılmasını engelleyen güvenlik uyarısı içerir.
- 🔄 **Android 12 / 14 / 15 / 16 (ABX) Desteği:** `abx2xml` ve `xml2abx` araçlarıyla tam uyumlu çalışır.

---

## ⚠️ Önemli Kullanım Uyarısı (Test Edilmiş Kritik Adım)

> **DİKKAT:** Bir uygulamanın SSAID kimliğini değiştirdikten sonra **TELEFONU YENİDEN BAŞLATMADAN ÖNCE HEDEF BANKA UYGULAMASINI KESİNLİKLE AÇMAYIN!**  
> Telefonu yeniden başlatmadan uygulamayı açarsanız, sistem henüz belleği sıfırlamadığı için yeni atadığınız SSAID de banka sunucusu tarafından hemen tespit edilip banlanacaktır. Değişiklikten sonra önce telefonu yeniden başlatın, açıldıktan sonra banka uygulamasına giriş yapın.

---

## 🛠️ Yerel Derleme

```bash
# Projeyi klonlayın
git clone https://github.com/Hengen42/SSAID-Changer-Next.git
cd SSAID-Changer-Next

# Debug APK derleyin
./gradlew assembleDebug

# Release APK derleyin
./gradlew assembleRelease
```
Üretilen APK konumu:
`app/build/outputs/apk/release/SSAID-Changer-Next-v1.3.0.apk`

---

## 📜 Lisans
Bu proje **MIT Lisansı** ile lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına göz atabilirsiniz.

Geliştirici: [Hengen](https://github.com/Hengen42)
