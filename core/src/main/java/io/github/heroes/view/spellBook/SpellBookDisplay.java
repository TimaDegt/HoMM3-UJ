package io.github.heroes.view.spellBook;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.graphics.g2d.Batch;

public class SpellBookDisplay {
    private final Group group;
    private final Texture bookTexture;
    private boolean active=false;
    private boolean isAnimating=false;

    public SpellBookDisplay() {
        this.group = new Group();
        this.bookTexture = new Texture("Magic/SpellBook.png");

        Image background=new Image(bookTexture);
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        background.setPosition(0, 0);

        group.addActor(background);
    }

    public void render(Batch batch){
        group.draw(batch,1.0f);
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public boolean isAnimating() {
        return isAnimating;
    }

    public void toggle() {
        this.active = !this.active;
    }
}
