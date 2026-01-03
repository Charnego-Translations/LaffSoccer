package com.ygames.ysoccer.match;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ygames.ysoccer.framework.GLGraphics;

import java.util.Comparator;

abstract class Sprite {

    GLGraphics glGraphics;

    TextureRegion textureRegion;
    int x;
    int y;
    int z;
    int priority = 0;
    boolean alive = true;

    Sprite(GLGraphics glGraphics) {
        this.glGraphics = glGraphics;
    }

    public void draw(int subframe) {
        glGraphics.batch.draw(textureRegion, x, y - z);
    }

    public int getY() {
        return y;
    }

    static class SpriteComparator implements Comparator<Sprite> {

        @Override
        public int compare(Sprite sprite1, Sprite sprite2) {

            if (sprite1.priority != sprite2.priority) {
                return sprite1.priority - sprite2.priority;
            }

            return sprite1.getY() - sprite2.getY();
        }
    }
}
