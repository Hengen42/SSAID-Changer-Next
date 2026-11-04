# SSAID Changer Next ⚡ (Türkçe Rehber)

<p align="center">
  <img src="assets/logo.svg" width="96" height="96" alt="SSAID Changer Next Logo" />
</p>

<p align="center">
  <b>Modern, Hafif ve Root Destekli Android SSAID (Android ID) Yöneticisi &amp; Değiştirici</b>
</p>

<p align="center">
  <a href="README.md"><b>English Documentation</b></a> • <a href="README_TR.md"><b>Türkçe Dokümantasyon</b></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%208%20--%2016-00E5FF?style=for-the-badge&logo=android" alt="Android" />
  <img src="https://img.shields.io/badge/Root-KernelSU%20%7C%20Magisk%20%7C%20APatch-7C4DFF?style=for-the-badge" alt="Root" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-3DDC84?style=for-the-badge&logo=jetpackcompose" alt="Compose" />
  <img src="https://img.shields.io/badge/Lisans-MIT-white?style=for-the-badge" alt="Lisans" />
</p>

---

## 📖 Genel Bakış

**SSAID Changer Next**, root yetkisine sahip Android cihazlarda (KernelSU, Magisk, APatch) yüklü uygulamaların kendine özel **SSAID** (Settings Secure Android ID) değerini görüntülemeyi, rastgele değiştirmeyi ve dilediğiniz an orijinal fabrika haline geri döndürmeyi sağlayan açık kaynaklı bir Android aracıdır.

### ❓ Bazı Uygulamalar Cihazı Neden Engeller?
Bazı uygulamalar (özellikle root veya sistem modifikasyonu tespiti yapıp girişi engelleyen ve Codevo Octopus gibi güvenlik SDK'ları kullanan uygulamalar), cihazda şüpheli bir ortam algıladıkları anda o uygulamaya ait benzersiz **SSAID** kimliğini kendi sunucularına "engelli/güvensiz" olarak kaydeder.

Sonradan root gizlense veya uygulama yeniden yüklense dahi, Android işletim sistemi uygulamaya aynı dosyadaki (`/data/system/users/0/settings_ssaid.xml`) sabit kimliği vermeye devam eder. Sunucu bu kimliği tanıdığı için engelleme sürer.

**SSAID Changer Next ile o uygulamanın SSAID değeri değiştirildiğinde; sunucu cihazı ilk defa açılan tertemiz bir cihaz olarak algılar ve engeli kaldırır.**

---

## ✨ Temel Özellikler

- ⚡ **Işık Hızında Uygulama Seçimi:** 300'den fazla uygulamayı 20 milisaniyede donma olmadan listeler (Asenkron LruCache ile bellek optimizasyonu).
- 📌 **Değiştirilen Uygulamalar En Üstte:** SSAID kimliği değiştirilmiş uygulamalar listenin en tepesine `DEĞİŞTİRİLDİ` rozetiyle sabitlenir.
- 🌐 **Uygulama İçi Dil Değiştirici:** İster sistem diliyle otomatik, ister Türkçe veya İngilizce olarak anında geçiş yapabilirsiniz.
- 🛡️ **İki Kademeli Güvenli Yedekleme:**
  - **Uygulama Bazlı Orijinale Dönüş:** Her uygulamanın ilk fabrika kimliği saklanır; tek tıkla geri alınabilir.
  - **Tam Sistem Geri Yükleme:** İlk açılışta `.ssaidchanger.bak` olarak tam sistem yedeği alınır.
- 🔄 **Android 12 / 14 / 15 / 16 (ABX) Desteği:** `abx2xml` ve `xml2abx` araçlarıyla tam uyumlu çalışır.
- 🔒 **Otomatik Girdi Ekleme:** Uygulama daha önce hiç açılmamışsa bile dosyaya geçerli XML düğümünü otomatik ekler (eski araçlardaki `NO_ENTRY` çökmesini önler).
- 🧹 **İsteğe Bağlı Veri Temizliği:** Eski kimlik önbelleğini sıfırlamak için tek tıkla `pm clear` çalıştırabilir.

---

## ⚠️ Önemli Kullanım Uyarısı (Test Edilmiş Kritik Adım)

> **DİKKAT:** Bir uygulamanın SSAID kimliğini değiştirdikten sonra **TELEFONU YENİDEN BAŞLATMADAN ÖNCE HEDEF UYGULAMAYI KESİNLİKLE AÇMAYIN!**  
> Telefonu yeniden başlatmadan uygulamayı açarsanız, sistem henüz belleği sıfırlamadığı için yeni atadığınız SSAID de sunucu tarafından hemen tespit edilip kalıcı olarak banlanacaktır. Değişiklikten sonra önce telefonu yeniden başlatın (veya Hızlı Soft Reboot yapın), açıldıktan sonra hedef uygulamaya giriş yapın.

---

## 🙏 Teşekkürler & İlham Kaynağı
Bu proje, [A7ALABS/ssaid-changer](https://github.com/A7ALABS/ssaid-changer) projesinin orijinal fikrinden esinlenerek geliştirilmiştir.

Orijinal Flutter uygulaması SSAID düzenleme fikrini ilk kez ortaya koymuş olsa da; ABX dönüştürme hataları, eksik kayıt durumunda çökmesi (`NO_ENTRY`) ve çok sayıda uygulama yüklü cihazlarda ciddi arayüz donmaları gibi sorunlar barındırıyordu.

**SSAID Changer Next**, tüm bilinen hataları ortadan kaldıran, modern yerel Android (Kotlin + Jetpack Compose) mimarisiyle sıfırdan kodlanmış, iki kademeli yedekleme sistemine sahip ve Android 12–16 sürümleriyle tam uyumlu yeni nesil bir çözümdür.

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
`app/build/outputs/apk/release/SSAID-Changer-Next-v1.4.0.apk`

---

## 📜 Lisans
Bu proje **MIT Lisansı** ile lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına göz atabilirsiniz.

Geliştirici: [Hengen](https://github.com/Hengen42)
