package com.ygames.ysoccer.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.ygames.ysoccer.YSoccer;
import com.ygames.ysoccer.framework.FileUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Locale;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        setLocalFilesBaseForMacAppBundle();
        if (StartupHelper.startNewJvmIfRequired()) return; // This handles macOS support and helps on Windows.
        createApplication();
    }

    // On macOS, GUI apps launched via Finder/LaunchServices (which is how the construo-packaged .app is
    // normally started) get "/" as their actual working directory, not the app bundle's own folder. This is
    // fixed for the life of the JVM: java.io.File's FileSystem provider caches the real cwd at JVM bootstrap,
    // before our own code gets to run, and ignores any later System.setProperty("user.dir", ...) call (this
    // is documented File behavior, not a bug we can work around in-process). That breaks every relative asset
    // lookup done via Gdx.files.local(...), including the directory listings SelectTeam/MenuMusic use to
    // browse teams and tracks, which (unlike Gdx.files.internal single-file reads) have no classpath fallback.
    // So instead of relying on the process's working directory at all, point FileUtils.local(...) at the
    // bundle's Resources folder directly. This is a no-op for the run/laffDist*/jarMac flows, since their jar
    // isn't sitting inside a "<name>.app/Contents/MacOS/" folder.
    private static void setLocalFilesBaseForMacAppBundle() {
        if (!System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("mac")) return;
        try {
            File jarFile = new File(Lwjgl3Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File macOsDir = jarFile.getParentFile();
            if (macOsDir == null || !macOsDir.getName().equals("MacOS")) return;
            File contentsDir = macOsDir.getParentFile();
            if (contentsDir == null || !contentsDir.getName().equals("Contents")) return;
            File resourcesDir = new File(contentsDir, "Resources");
            if (resourcesDir.isDirectory()) {
                FileUtils.setLocalFilesBase(resourcesDir.getAbsolutePath());
            }
        } catch (URISyntaxException e) {
            System.err.println("Could not determine app bundle location: " + e.getMessage());
        }
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new YSoccer(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Charnego Internatiolaff Soccer");
        //// Vsync limits the frames per second to what your hardware can display, and helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line after is a safeguard.
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match the monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.

        configuration.setWindowedMode(1280, 720);
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        //// They can also be loaded from the root of assets/ .
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        //// This could improve compatibility with Windows machines with buggy OpenGL drivers, Macs
        //// with Apple Silicon that have to emulate compatibility with OpenGL anyway, and more.
        //// This uses the dependency `com.badlogicgames.gdx:gdx-lwjgl3-angle` to function.
        //// You would need to add this line to lwjgl3/build.gradle , below the dependency on `gdx-backend-lwjgl3`:
        ////     implementation "com.badlogicgames.gdx:gdx-lwjgl3-angle:$gdxVersion"
        //// You can choose to add the following line and the mentioned dependency if you want; they
        //// are not intended for games that use GL30 (which is compatibility with OpenGL ES 3.0).
        //// Know that it might not work well in some cases.
//        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20, 0, 0);

        return configuration;
    }
}
