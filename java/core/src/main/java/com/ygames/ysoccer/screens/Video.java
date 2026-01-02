package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.gui.Gui;
import com.ygames.ysoccer.gui.Widget;
import org.apache.commons.lang3.SystemUtils;

import java.io.FileNotFoundException;

public class Video extends GLScreen {

    private final VideoPlayer videoPlayer = VideoPlayerCreator.createVideoPlayer();

    public Video(GLGame game) {
        super(game);
        setMainMenu();

        usesMouse = false;

        game.disableMouse();
        Gdx.input.setInputProcessor(new IntroInputProcessor());

        FileHandle intro = Gdx.files.internal("videos").child("intro1.ogg");

        try {
            videoPlayer.load(intro);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        videoPlayer.play();
        videoPlayer.setOnCompletionListener(fileHandle -> setMainMenu());

    }

    @Override
    public void render(float delta) {
        super.render(delta);

        videoPlayer.update();
        batch.begin();
        Texture frame = videoPlayer.getTexture();
        if (frame != null) {
            batch.draw(frame, 0, 0, Gui.WIDTH, Gui.HEIGHT, 0, 0, frame.getWidth(), frame.getHeight(), false, true);
        }
        batch.end();

        // videoPlayer.getVideoHeight() returns 0 when the video is not loaded
        if (widgetEvent == Widget.Event.FIRE1_UP || widgetEvent == Widget.Event.FIRE2_UP) { // || videoPlayer.getVideoHeight() == 0) {
            setMainMenu();
        }
    }

    private class IntroInputProcessor extends InputAdapter {

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            setMainMenu();
            return true;
        }

        @Override
        public boolean keyUp(int keycode) {
            setMainMenu();
            return true;
        }
    }

    private void setMainMenu() {
        // En linux no funciona el dispose, esto es una ñapa.
        if(SystemUtils.IS_OS_LINUX) {
            if (videoPlayer != null && videoPlayer.isPlaying()) {
                videoPlayer.pause();
            }
        } else {
            if (videoPlayer != null) {
                if (videoPlayer.isPlaying()) {
                    videoPlayer.stop();
                }
                videoPlayer.dispose();
            }
        }
        Gdx.input.setInputProcessor(null);
        game.enableMouse();
        game.setScreen(new Intro(game));
    }
}
