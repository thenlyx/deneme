# WhatsApp Tek Bildirim – v2

Samsung Android 15 / rootsuz kullanım için Android Studio kaynak projesi.

## Amaç
Yeni WhatsApp bildirimi geldiğinde eski WhatsApp bildirimlerini bildirim panelinden kaldırıp en yeni tekil bildirimi tutmayı dener. WhatsApp sohbetlerini açmaz; mesaj metnini, gönderen adını veya içeriğini okumaz; mesajları okundu yapmaz. Diğer uygulamalara dokunmaz.

## v2'deki iyileştirmeler
- WhatsApp grup özeti bildirimlerini kaldırmayı dener.
- Daha eski zaman damgalı bildirimlerin yeniden yayınlanıp en yeni bildirimin yerini almasını engeller.
- Servis yeniden bağlandığında mevcut WhatsApp bildirimleri arasından en yenisini seçer.
- Yalnızca standart WhatsApp `com.whatsapp` hedeflenir; WhatsApp Business dahil değildir.

## APK oluşturma
1. Android Studio'yu bilgisayara kur.
2. Bu klasörü `File > Open` ile aç.
3. Gradle senkronizasyonunu bekle.
4. `Build > Build Bundle(s) / APK(s) > Build APK(s)` seç.
5. APK genellikle `app/build/outputs/apk/debug/app-debug.apk` konumunda oluşur.
6. APK'yı Samsung telefona gönderip yükle.
7. Uygulamayı aç, **Bildirim erişimini aç** düğmesine bas.
8. Ayarlarda **WhatsApp Tek Bildirim** erişimini etkinleştir.
9. Uygulamaya dön; durum **Etkin** görünmeli.

## Sınırlar ve gizlilik
- Bildirim erişimi hassas bir Android iznidir. Uygulama teknik olarak bildirimleri görüp kaldırabilir; bu kaynak kodu mesaj içeriğini okumaz veya internete göndermez.
- Android/One UI ve WhatsApp bildirim davranışı üretici/sürüm bazında değişebilir. Özellikle WhatsApp'ın grup özetlerini yeniden oluşturması sebebiyle gerçek telefonda test zorunludur; yüzde 100 başarı garantisi verilemez.
- Bu ZIP kaynak projedir; imzalanmış/derlenmiş APK değildir.
- Android Studio ile derleme ve telefonda test gerekir.


## Android Studio olmadan APK oluşturma (GitHub Actions)
Bu projede `.github/workflows/build-apk.yml` dosyası bulunur.
1. GitHub'da yeni, boş bir repository oluştur.
2. ZIP'i bilgisayarda aç ve içindeki tüm dosyaları (`.github` klasörü dahil) repository'ye yükle.
3. GitHub repository sayfasında **Actions** sekmesine gir.
4. `Build WhatsApp Tek Bildirim APK` iş akışını seçip **Run workflow** düğmesine bas.
5. İşlem başarılı olunca workflow çalışmasının altındaki **Artifacts** bölümünden `WhatsApp-Tek-Bildirim-debug` dosyasını indir.
6. ZIP içindeki `app-debug.apk` dosyasını telefona gönderip yükle.

GitHub hesabı ve repository oluşturma gerekir. Bu yöntem APK'yı GitHub'ın bulut makinesinde derler; Android Studio kurman gerekmez.
