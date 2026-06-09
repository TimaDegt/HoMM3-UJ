package io.github.heroes.view.Battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import io.github.heroes.model.snapshot.UnitSnapshot;

public class UnitInfoPopup {
    private static final float POPUP_WIDTH = 320f;
    private static final float POPUP_HEIGHT = 300f;

    private final Table root;
    private final Table contentTable;
    private final Skin skin;
    private final Texture popupBackground;

    public UnitInfoPopup(Skin skin) {
        this.skin = skin;
        popupBackground = new Texture(Gdx.files.internal("Menu/popup_bg.png"));

        root = new Table();
        root.setFillParent(true);
        root.center();
        root.setVisible(false);

        TextureRegionDrawable popupDrawable = new TextureRegionDrawable(popupBackground);
        popupDrawable.setMinWidth(0f);
        popupDrawable.setMinHeight(0f);

        contentTable = new Table(skin);
        contentTable.top();
        contentTable.setBackground(popupDrawable);

        root.add(contentTable)
            .width(POPUP_WIDTH)
            .height(POPUP_HEIGHT)
            .pad(24);
    }

    public void addTo(Stage stage) {
        stage.addActor(root);
    }

    public void show(UnitSnapshot unit) {
        updateContent(unit);
        root.setVisible(true);
    }

    public void hide() {
        root.setVisible(false);
    }

    public void dispose() {
        popupBackground.dispose();
    }

    private void updateContent(UnitSnapshot unit) {
        contentTable.clear();
        contentTable.padTop(56f);
        contentTable.padLeft(26f);
        contentTable.padRight(26f);
        contentTable.padBottom(28f);
        contentTable.defaults().left().pad(4);

        addInfoRow("Unit", unit.type().name());
        addInfoRow("Count", String.valueOf(unit.count()));
        addInfoRow("Attack", unit.baseAttack()+"("+unit.attack()+")");
        addInfoRow("Defense", unit.baseDefense()+"("+unit.defense()+")");
        addInfoRow("Health", String.valueOf(unit.maxHp()));
        addInfoRow("Current health", String.valueOf(unit.currentHp()));
        addInfoRow("Speed", String.valueOf(unit.speed()));
    }

    private void addInfoRow(String label, String value) {
        contentTable.add(new Label(label + ":", skin)).width(150f);
        contentTable.add(new Label(value, skin)).width(110f);
        contentTable.row();
    }
}
