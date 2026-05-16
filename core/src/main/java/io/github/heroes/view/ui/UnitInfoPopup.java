package io.github.heroes.view.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.heroes.model.UnitStack;

public class UnitInfoPopup {
    private final Table root;
    private final Table contentTable;
    private final Skin skin;

    public UnitInfoPopup(Skin skin) {
        this.skin = skin;
        root = new Table();
        root.setFillParent(true);
        root.center();
        root.setVisible(false);

        contentTable = new Table(skin);
        contentTable.setBackground("window");

        root.add(contentTable).width(320f).pad(24);
    }

    public void addTo(Stage stage) {
        stage.addActor(root);
    }

    public void show(UnitStack unit) {
        updateContent(unit);
        root.setVisible(true);
    }

    public void hide() {
        root.setVisible(false);
    }

    private void updateContent(UnitStack unit) {
        contentTable.clear();
        contentTable.pad(18);
        contentTable.defaults().left().pad(4);

        addInfoRow("Unit", unit.getType().name());
        addInfoRow("Count", String.valueOf(unit.getCount()));
        addInfoRow("Attack", String.valueOf(unit.getType().attack));
        addInfoRow("Defense", String.valueOf(unit.getType().defense));
        addInfoRow("Health", String.valueOf(unit.getType().maxHp));
        addInfoRow("Current health", String.valueOf(unit.getCurrentHp()));
        addInfoRow("Speed", String.valueOf(unit.getType().speed));
    }

    private void addInfoRow(String label, String value) {
        contentTable.add(new Label(label + ":", skin)).width(150f);
        contentTable.add(new Label(value, skin)).width(110f);
        contentTable.row();
    }
}
