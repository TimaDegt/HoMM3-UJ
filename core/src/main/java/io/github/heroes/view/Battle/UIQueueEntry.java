package io.github.heroes.view.Battle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import io.github.heroes.model.combat.TurnQueueEntry;
import io.github.heroes.model.state.Player;
import io.github.heroes.model.state.UnitStack;

public class UIQueueEntry extends Stack {
    private final Image icon;
    private final Image colorBar;

    public UIQueueEntry(TurnQueueEntry entry) {
        UnitStack unitStack = entry.getUnitStack();
        int cnt = unitStack.getCount();
        Player owner = unitStack.getOwner();

        Table gapContainer = new Table();
        gapContainer.pad(1.5f);

        Table outlineTable = new Table();
        outlineTable.setBackground(createSolidTexture(new Color(0.92f, 0.88f, 0.78f, 1f)));

        outlineTable.pad(2.5f, 1.0f, 2.5f, 1.0f);

        Table contentTable = new Table();

        String iconPath = "Icons/" + unitStack.getType().getName() + ".png";
        Texture iconTex = new Texture(iconPath);
        iconTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        icon = new Image(iconTex);
        icon.setScaling(Scaling.stretch);

        contentTable.add(icon).expand().fill().row();

        Image separator = new Image(createSolidTexture(new Color(0.92f, 0.88f, 0.78f, 1f)));
        contentTable.add(separator).expandX().fillX().height(2f).row();

        Stack barStack = new Stack();

        colorBar = new Image(createSolidTexture(Color.WHITE));
        colorBar.setScaling(Scaling.stretch);

        if (owner == Player.PLAYER_ONE) {
            colorBar.setColor(new Color(0.55f, 0.16f, 0.06f, 1f));
        } else {
            colorBar.setColor(new Color(0.06f, 0.16f, 0.55f, 1f));
        }

        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Label countLabel = new Label(String.valueOf(cnt), labelStyle);
        countLabel.setAlignment(Align.center);
        countLabel.setFontScale(1f);

        barStack.add(colorBar);
        barStack.add(countLabel);

        contentTable.add(barStack).expandX().fillX().height(22f);

        outlineTable.add(contentTable).expand().fill();
        gapContainer.add(outlineTable).expand().fill();
        this.add(gapContainer);
    }
    public UIQueueEntry(int roundNumber) {
        icon = new Image();
        colorBar = new Image();

        Table gapContainer = new Table();
        gapContainer.pad(1.5f);

        Table outlineTable = new Table();
        outlineTable.setBackground(createSolidTexture(new Color(0.92f, 0.88f, 0.78f, 1f)));
        outlineTable.pad(2.5f);

        Table contentTable = new Table();
        contentTable.setBackground(createSolidTexture(new Color(0.2f, 0.15f, 0.1f, 1f)));

        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Label numLabel = new Label(String.valueOf(roundNumber), labelStyle);
        Label roundLabel = new Label("round", labelStyle);

        numLabel.setAlignment(Align.center);
        roundLabel.setAlignment(Align.center);
        numLabel.setFontScale(1.8f);
        roundLabel.setFontScale(1.0f);

        contentTable.add(numLabel).expandX().center().row();
        contentTable.add(roundLabel).expandX().center();

        outlineTable.add(contentTable).expand().fill();
        gapContainer.add(outlineTable).expand().fill();
        this.add(gapContainer);
    }

    private TextureRegionDrawable createSolidTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(tex);
    }
}
