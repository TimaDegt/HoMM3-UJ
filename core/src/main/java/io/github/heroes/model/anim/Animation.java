package io.github.heroes.model.anim;

import com.badlogic.gdx.graphics.g3d.environment.AmbientCubemap;

public class Animation {
    private AnimParams params;

    private AnimParams.AnimType animType = AnimParams.AnimType.IDLE;
    private int frameIndex = 0;

    public Animation(AnimParams params) {
        this.params = params;
    }
    public void startAnimation(AnimParams.AnimType type) {
        if (animType != AnimParams.AnimType.IDLE) { return; }
        animType = type;
        frameIndex = 0;
    }
    public boolean isBusy() {
        return animType != AnimParams.AnimType.IDLE;
    }
    public SpriteCoordinate nextFrame() {
        int coordinate = params.currentCoordinate(animType);
        int length = params.currentLength(animType  );
        int size = params.getSize();
        if (animType == AnimParams.AnimType.IDLE) {
            return new SpriteCoordinate(
                0,
                coordinate,
                size
            );
        }
        if (animType == AnimParams.AnimType.DEAD) {
            return new SpriteCoordinate(
                (length - 1) * size,
                coordinate,
                size
            );
        }
        SpriteCoordinate ret = new SpriteCoordinate(0,0,0);
        ret.x = size * frameIndex;
        ret.y = coordinate;
        ret.size = size;
        frameIndex++;
        if (frameIndex >= length) {
            if (animType == AnimParams.AnimType.DEATH) {
                animType = AnimParams.AnimType.DEAD;
                frameIndex = 0;
                return ret;
            }
            animType = AnimParams.AnimType.IDLE;
            frameIndex = 0;
        }
        return ret;
    }
}
