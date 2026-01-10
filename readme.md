# Charnego Internatiolaff Soccer
También conocido con el "working title" de Laff Soccer, es el nuevo megaproyecto balompédico de Charnego Translations. ¡En construcsión!

## ¿Quién ha hecho esta mierda?

Charnego Internatiolaff Soccer es una producción de Charnego Translations Ltd, todos los izquierdos reservaos. El juego tiene licencia GPL2, y la música tiene todos sus derechos reservados y está usada con permiso de su autor, [Anton Sapristi](https://antonsapristi.bandcamp.com/). El juego es un fork de [YSoccer](https://ysoccer.sourceforge.io/), de Massimo Modica.

## ¿Cuándo estará listo?

¡Nunca jamás! Pero intentaremos publicar versiones cuando nos pique

## Versión lista para jugar

Ve a https://github.com/Charnego-Translations/laffsoccer/releases para ver las versiones lanzadas listas para jugar, sin tener que andarte con zarandajas de compilar cosas. No debería hacer falta Java, porque ya viene. Léete el [leeme.txt](java/assets/docs/leeme.txt) pa más info.

## Para jugar a la última versión (versión en desarrollo, inestable)

Tienes que tener Java 17 instalado como mínimo ya que Gradle lo requiere. Recomendamos la distro Amazon Correto, disponible en https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html. Funciona en Güindos, Kleenux y MacacOSX. Entra en la consola y haz lo siguiente:

(Windows)
```
C:\COSAS\LAFFSOCCER\> cd java
C:\COSAS\LAFFSOCCER\JAVA\> gradlew lwjgl3:run
```

(Leenucs, MacdonaldsOS X, UN*X, etc)
```
$ cd java/
$ ./gradlew lwjgl3:run
```

Como verás nos centramos en la versión de Java. Las otras están más abandonadas que el niño de Solo en Casa.