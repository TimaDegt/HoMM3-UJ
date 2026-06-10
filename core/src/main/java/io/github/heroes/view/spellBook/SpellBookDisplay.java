package io.github.heroes.view.spellBook;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.heroes.model.magic.Spell;

import java.util.List;
import java.util.function.IntConsumer;

public class SpellBookDisplay {
    private final Group group;
    private final Texture bookTexture;
    private final Texture buttonBackgroundTexture;
    private final Image background;
    private final Table spellTable;
    private final Skin skin;
    private final IntConsumer onSpellSelected;

    public SpellBookDisplay(Skin skin, IntConsumer onSpellSelected) {
        if (skin == null) throw new IllegalArgumentException("Skin cannot be null");
        if (onSpellSelected == null) {
            throw new IllegalArgumentException("Spell selection handler cannot be null");
        }

        this.skin = skin;
        this.onSpellSelected = onSpellSelected;
        this.group = new Group();
        this.bookTexture = new Texture("Magic/SpellBook.png");
        this.buttonBackgroundTexture = new Texture("Menu/popup_bg.png");
        this.background = new Image(bookTexture);
        this.spellTable = new Table();

        background.setTouchable(Touchable.disabled);
        group.addActor(background);
        group.addActor(spellTable);
        group.setTouchable(Touchable.childrenOnly);
        group.setVisible(false);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void addTo(Stage stage) {
        stage.addActor(group);
    }

    public boolean isActive() {
        return group.isVisible();
    }

    public void setActive(boolean active) {
        group.setVisible(active);
        if (active) group.toFront();
    }

    public boolean isAnimating() {
        return false;
    }

    public void setSpells(List<Spell> spells) {
        spellTable.clearChildren();
        spellTable.defaults().width(380f).height(76f).padTop(10f).padBottom(10f);

        if (spells.isEmpty()) {
            spellTable.add(new Label("No known spells", skin));
            return;
        }

        for (int i = 0; i < spells.size(); i++) {
            Spell spell = spells.get(i);
            int spellIndex = i;
            TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(
                skin.get(TextButton.TextButtonStyle.class)
            );
            TextureRegionDrawable buttonBackground = new TextureRegionDrawable(
                buttonBackgroundTexture
            );
            buttonBackground.setMinWidth(0f);
            buttonBackground.setMinHeight(0f);
            style.up = buttonBackground;
            style.over = buttonBackground;
            style.down = buttonBackground;

            TextButton button = new TextButton(spell.getName(), style);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onSpellSelected.accept(spellIndex);
                }
            });
            spellTable.add(button).row();
        }
    }

    public void resize(int width, int height) {
        group.setSize(width, height);
        background.setSize(width, height);
        spellTable.setBounds(
            width * 0.1f,
            height * 0.15f,
            width * 0.45f,
            height * 0.7f
        );
    }

    public void dispose() {
        bookTexture.dispose();
        buttonBackgroundTexture.dispose();
    }
}
