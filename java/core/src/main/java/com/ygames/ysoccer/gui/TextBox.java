package com.ygames.ysoccer.gui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;
import com.ygames.ysoccer.framework.GLShapeRenderer;
import com.ygames.ysoccer.framework.GLSpriteBatch;

import java.util.List;

public class TextBox extends Widget {

    private BitmapFont font;
    public List<String> lines;
    private int top;
    private int centerX;
    private int targetWidth;

    public TextBox(BitmapFont font, List<String> lines, int centerX, int top, int targetWidth) {
        this.font = font;
        this.lines = lines;
        this.top = top;
        this.centerX = centerX;
        this.targetWidth = targetWidth;
    }

    @Override
    public void render(GLSpriteBatch batch, GLShapeRenderer shapeRenderer) {
        batch.begin();
        int y = top;
        for (String line : lines) {
            font.draw(batch, line, centerX, y, targetWidth, Align.center, true);
            y += 22;
        }
        batch.end();
    }
}

