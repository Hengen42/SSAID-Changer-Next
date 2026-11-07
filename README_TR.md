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

- 🔔 **Uygulama İçi Güncelleme Kontrolü:** Açılışta GitHub üzerinden daha yeni bir kararlı sürüm olup olmadığını otomatik kontrol eder. Güncelleme simgesinden elle kontrol de yapılabilir; yeni sürüm varsa GitHub sürüm sayfasını açmayı veya ertelemeyi seçebilirsiniz. Güncelleme kontrolü root izni gerektirmez.

- ⚡ **Işık Hızında Doğrudan Liste:** Uygulama açılır açılmaz yüklü tüm uygulamalar 20 milisaniyede donma olmadan karşınıza gelir (Asenkron LruCache ile bellek optimizasyonu).
- 📌 **Değiştirilen Uygulamalar En Üstte:** SSAID kimliği değiştirilmiş uygulamalar listenin en tepesine `DEĞİŞTİRİLDİ` rozetiyle sabitlenir.
- 🔄 **Aşağı Çekerek Yenileme (Pull-to-Refresh):** Yeni yüklenen uygulamaları hemen listelemek için listeyi aşağı kaydırmanız yeterlidir.
- 🌐 **Uygulama İçi Dil Değiştirici:** İster sistem diliyle otomatik, ister Türkçe veya İngilizce olarak anında geçiş yapabilirsiniz.
- 🛡️ **İki Kademeli Güvenli Yedekleme:**
  - **Uygulama Bazlı Orijinale Dönüş:** Her uygulamanın ilk fabrika kimliği saklanır; tek tıkla geri alınabilir.
  - **Tam Sistem Geri Yükleme:** İlk açılışta `.ssaidchanger.bak` olarak tam sistem yedeği alınır.
- 🔄 **Android 12 / 14 / 15 / 16 (ABX) Desteği:** `abx2xml` ve `xml2abx` araçlarıyla tam uyumlu çalışır.
- 🔒 **Otomatik Girdi Ekleme:** Uygulama daha önce hiç açılmamışsa bile dosyaya geçerli XML düğümünü otomatik ekler (eski araçlardaki `NO_ENTRY` çökmesini önler).
- 🧹 **İsteğe Bağlı Veri Temizliği:** Eski kimlik önbelleğini sıfırlamak için tek tıkla `pm clear` çalıştırabilir.

---

## 📥 Kurulum ve Kullanım (Son Kullanıcı İçin)

**v1.6.0 ve öncesinden geçiş:** v1.6.1 ile kalıcı release imzasına geçilmiştir.
Eski debug imzalı kurulumun bir defalığına kaldırılması gerekebilir. **Kaldırmadan
önce kayıtlı orijinal SSAID değerlerini not edin**; uygulamayı kaldırmak uygulama
bazlı geri yükleme kayıtlarını siler. Ayrıntılar: [imzalama ve geçiş rehberi](docs/RELEASE_SIGNING.md).
Kalıcı imza, Google Play Protect uyarılarının kalkacağını garanti etmez.

1. **İndirin:** En güncel `SSAID-Changer-Next-vX.X.X.apk` dosyasını doğrudan [Releases (Sürümler)](https://github.com/Hengen42/SSAID-Changer-Next/releases) sayfasından indirin.
2. **Kurun ve Root Yetkisi Verin:** APK'yı yükleyip açın; KernelSU / Magisk / APatch root uyarısı verdiğinde onaylayın.
3. **Hedef Uygulamayı Seçin:** Kimliğini değiştirmek istediğiniz uygulamaya dokunun.
4. **Rastgele Üretip Uygulayın:** **Rastgele Üret** butonuna basın, "Uygulama verilerini de temizle" kutucuğunun seçili olduğundan emin olun ve **YENİ SSAID KİMLİĞİNİ UYGULA** deyin.
5. **Telefonu Yeniden Başlatın:** Açılan pencereden **Telefonu Hemen Yeniden Başlat** butonuna basın. Telefon açıldığında hedef uygulamanıza artık sorunsuz giriş yapabilirsiniz!

---

## ⚠️ Önemli Kullanım Uyarısı (Test Edilmiş Kritik Adım)

> **DİKKAT:** Bir uygulamanın SSAID kimliğini değiştirdikten sonra **TELEFONU YENİDEN BAŞLATMADAN ÖNCE HEDEF UYGULAMAYI KESİNLİKLE AÇMAYIN!**  
> Telefonu yeniden başlatmadan uygulamayı açarsanız, sistem henüz belleği sıfırlamadığı için yeni atadığınız SSAID de sunucu tarafından hemen tespit edilip kalıcı olarak banlanacaktır. Değişiklikten sonra önce telefonu yeniden başlatın, açıldıktan sonra hedef uygulamaya giriş yapın.

---

## 🤝 Katkıda Bulunma (Pull Request - PR)
SSAID Changer Next açık kaynaklı bir topluluk projesidir. Projeye katkıda bulunmak, yeni bir özellik eklemek veya bir hatayı düzeltmek isterseniz Pull Request (PR) gönderebilirsiniz:

1. Bu depoyu (repository) Fork'layın (sağ üstteki **Fork** butonuna basın).
2. Yeni bir özellik veya düzeltme dalı oluşturun: `git checkout -b ozellik/yeni-ozellik`
3. Değişikliklerinizi yapıp commit'leyin: `git commit -m "Harika bir özellik eklendi"`
4. Dalınızı uzak sunucunuza gönderin: `git push origin ozellik/yeni-ozellik`
5. GitHub üzerinden bir **Pull Request (PR)** açın!

Tüm Pull Request'ler memnuniyetle incelenir ve projeye dahil edilir.

---

## 🐛 Hata Bildirimi & Güncelleme Talepleri (GitHub Issues)
Uygulamada bir hata ile karşılaştıysanız veya yeni bir özellik öneriniz varsa, [GitHub Issues (Sorun Bildirimi)](https://github.com/Hengen42/SSAID-Changer-Next/issues) sayfasında dilediğiniz gibi yeni bir konu açarak bize iletebilirsiniz! Topluluk geri bildirimleri memnuniyetle karşılanır ve düzenli olarak incelenir.

---

## 🙏 Teşekkürler & İlham Kaynağı
Bu proje, [A7ALABS/ssaid-changer](https://github.com/A7ALABS/ssaid-changer) projesinin orijinal fikrinden esinlenerek geliştirilmiştir.

Orijinal Flutter uygulaması SSAID düzenleme fikrini ilk kez ortaya koymuş olsa da; ABX dönüştürme hataları, eksik kayıt durumunda çökmesi (`NO_ENTRY`) ve çok sayıda uygulama yüklü cihazlarda ciddi arayüz donmaları gibi sorunlar barındırıyordu.

**SSAID Changer Next**, tüm bilinen hataları ortadan kaldıran, modern yerel Android (Kotlin + Jetpack Compose) mimarisiyle sıfırdan kodlanmış, iki kademeli yedekleme sistemine sahip ve Android 12–16 sürümleriyle tam uyumlu yeni nesil bir çözümdür.

---

## 🛠️ Kaynak Koddan Derleme (Geliştiriciler İçin)

```bash
git clone https://github.com/Hengen42/SSAID-Changer-Next.git
cd SSAID-Changer-Next
./gradlew assembleRelease
```
Derlenen Release APK konumu:
`app/build/outputs/apk/release/SSAID-Changer-Next-v1.6.1.apk`

Release derlemesi kalıcı imzalama anahtarı gerektirir; [imzalama rehberine](docs/RELEASE_SIGNING.md) bakın.
İmzalama bilgileri olmadan geliştirme için `./gradlew assembleDebug` kullanabilirsiniz.

---

## 📜 Lisans
Bu proje **MIT Lisansı** ile lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına göz atabilirsiniz.

Geliştirici: [Hengen](https://github.com/Hengen42)
