# GlokMC Client (Fabric, Minecraft 26.2)

ESC menüsünün en üstüne **GlokMC Ayarları** düğmesi ekler. Panelden: FPS göstergesi, tuş göstergesi, tam parlaklık, zoom (C), koşu kilidi.
Menü kısayolu: Sağ Shift. Ayarlar `config/glokmc-client.json` içinde saklanır.

## Derleme
Gereken: Java 25 ve Gradle 9.5.1+
    gradle build
Çıkan dosya: build/libs/glokmc-client-1.0.0.jar

Kolay yol: bu klasörü içeren depoyu GitHub'a yükle, `.github/workflows/release.yml` modu kendisi derler ve launcher'ın içine koyar.
