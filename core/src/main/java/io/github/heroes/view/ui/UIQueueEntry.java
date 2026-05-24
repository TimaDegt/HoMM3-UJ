package io.github.heroes.view.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import io.github.heroes.combat.TurnQueueEntry;
import io.github.heroes.model.BattleState;
import io.github.heroes.model.Player;
import io.github.heroes.model.UnitStack;

public class UIQueueEntry extends Stack {
    private final Image icon;
    private final Image colorBar;

    public UIQueueEntry(TurnQueueEntry entry) {
        Table gapContainer = new Table();
        gapContainer.pad(1f);
        Stack innerStack = new Stack();

        UnitStack unitStack = entry.getUnitStack();
        int cnt = unitStack.getCount();
        Player owner = unitStack.getOwner();

        String iconPath = "Icons/" + unitStack.getType().getName() + ".png";
        Texture iconTex = new Texture(iconPath);
        iconTex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        icon = new Image(iconTex);
        innerStack.add(icon);

        Table bottomTable = new Table();
        bottomTable.bottom();
        Stack barStack = new Stack();

        colorBar = new Image(createSolidTexture(Color.WHITE));
        if (owner == Player.PLAYER_ONE) {
            colorBar.setColor(Color.RED);
        } else {
            colorBar.setColor(Color.BLUE);
        }

        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Label countLabel = new Label(String.valueOf(cnt), labelStyle);
        countLabel.setAlignment(Align.center);
        countLabel.setFontScale(1.3f);

        barStack.add(colorBar);
        barStack.add(countLabel);

        bottomTable.add(barStack).expandX().fillX().height(20f);
        innerStack.add(bottomTable);

        innerStack.add(createOutline());

        gapContainer.add(innerStack).expand().fill();
        this.add(gapContainer);
    }
    public UIQueueEntry(int roundNumber) {
        icon = new Image();
        colorBar = new Image();

        Table gapContainer = new Table();
        gapContainer.pad(1.5f);
        Stack innerStack = new Stack();

        Image bg = new Image(createSolidTexture(Color.DARK_GRAY));
        innerStack.add(bg);

        Table textTable = new Table();
        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Label numLabel = new Label(String.valueOf(roundNumber), labelStyle);
        Label roundLabel = new Label("Round", labelStyle);

        numLabel.setFontScale(1.4f);
        roundLabel.setFontScale(0.9f);

        numLabel.setAlignment(Align.center);
        roundLabel.setAlignment(Align.center);

        textTable.add(numLabel).row();
        textTable.add(roundLabel);
        innerStack.add(textTable);

        innerStack.add(createOutline());

        gapContainer.add(innerStack).expand().fill();
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
    private Table createOutline() {
        TextureRegionDrawable outlineDrawable = createSolidTexture(new Color(0.45f, 0.25f, 0.1f, 1f));
        Table outlineTable = new Table();
        outlineTable.setFillParent(true);

        Image top = new Image(outlineDrawable);
        Image bottom = new Image(outlineDrawable);
        Image left = new Image(outlineDrawable);
        Image right = new Image(outlineDrawable);

        outlineTable.add(top).height(1).expandX().fillX().colspan(3).row();
        outlineTable.add(left).width(1).expandY().fillY();
        outlineTable.add().expand().fill();
        outlineTable.add(right).width(1).expandY().fillY().row();
        outlineTable.add(bottom).height(1).expandX().fillX().colspan(3);

        return outlineTable;
    }
}
