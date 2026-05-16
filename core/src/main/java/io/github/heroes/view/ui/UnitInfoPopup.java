package io.github.heroes.view.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import io.github.heroes.model.UnitStack;

public class UnitInfoPopup {
    private final Table root;
    private final Label unitInfoLabel;

    public UnitInfoPopup(Skin skin) {
        root = new Table();
        root.setFillParent(true);
        root.center();
        root.setVisible(false);

        Table popupContent = new Table(skin);
        popupContent.setBackground("window");

        unitInfoLabel = new Label("", skin);
        popupContent.add(unitInfoLabel).pad(16);

        root.add(popupContent);
    }

    public void addTo(Stage stage) {
        stage.addActor(root);
    }

    public void show(UnitStack unit) {
        unitInfoLabel.setText(unit.getType().name());
        root.setVisible(true);
    }

    public void hide() {
        root.setVisible(false);
    }
}
