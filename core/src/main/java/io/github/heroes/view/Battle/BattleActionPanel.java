package io.github.heroes.view.Battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.heroes.model.combat.TurnQueueEntry;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.List;

public class BattleActionPanel {
    private final Group group;
    private final Texture panelBackground;
    private final List<UIQueueEntry> queueEntries;
    private final List<ImageButton> battleActionButtons;
    private final Runnable onDefendClicked;
    private final Runnable onWaitClicked;

    private enum ActionButtonType {
        SURRENDER,
        RETREAT,
        OPTIONS,
        AUTO,
        SPELL_BOOK,
        DEFENCE,
        WAIT
    }

    public void addTo(Stage stage) {
        stage.addActor(this.group);
    }

    public BattleActionPanel(
        List<TurnQueueEntry> turnQueue,
        Runnable onDefendClicked,
        Runnable onWaitClicked
    ) {
        this.group = new Group();
        this.panelBackground = new Texture("Combat/FullPanel.png");
        this.queueEntries = new ArrayList<>();
        this.battleActionButtons = new ArrayList<>();
        this.onDefendClicked = onDefendClicked;
        this.onWaitClicked = onWaitClicked;

        Image backgroundActor = new Image(panelBackground);
        float scale = Gdx.graphics.getWidth() / 800f;
        backgroundActor.setSize(800 * scale, 42 * scale);
        backgroundActor.setPosition(0, 0);
        group.addActor(backgroundActor);

        setupButtons(scale);
        updateQueueButtons(turnQueue);
    }

    public void updateQueueButtons(List<TurnQueueEntry> queue) {
        for (UIQueueEntry entry : queueEntries) {
            entry.remove();
        }
        queueEntries.clear();

        List<Object> queueData = new ArrayList<>();
        int curr_index = 0;

        while (queueData.size() < BattleViewConfig.ACTION_PANEL_QUEUE_BUTTON_COUNT) {
            if (curr_index >= queue.size()) {
                queueData.add(null);
                continue;
            }

            queueData.add(queue.get(curr_index));

            if (curr_index < queue.size() - 1 && queue.get(curr_index).getRound() != queue.get(curr_index + 1).getRound()) {
                if (queueData.size() < BattleViewConfig.ACTION_PANEL_QUEUE_BUTTON_COUNT) {
                    queueData.add(queue.get(curr_index).getRound() + 1);
                }
            }
            curr_index++;
        }

        float scale = Gdx.graphics.getWidth() / 800f;
        float qStartX = 211 * scale;
        float btnH = 38 * scale;
        float qW = (406f / BattleViewConfig.ACTION_PANEL_QUEUE_BUTTON_COUNT) * scale;
        float y = 2 * scale;

        for (int i = 0; i < BattleViewConfig.ACTION_PANEL_QUEUE_BUTTON_COUNT; i++) {
            Object data = queueData.get(i);

            if (data == null) continue;

            UIQueueEntry entry;
            if (data instanceof Integer) {
                entry = new UIQueueEntry((Integer) data);
            } else {
                entry = new UIQueueEntry((TurnQueueEntry) data);
            }

            entry.setBounds(qStartX + (i * qW), y, qW, btnH);
            group.addActor(entry);
            queueEntries.add(entry);
        }
    }

    private void setupButtons(float scale) {
        String[] leftButtons = {"Options", "Surrender", "Retreat", "Auto"};
        float startX = 2 * scale;
        float y = 2 * scale;
        float btnW = 50 * scale;
        float btnH = 38 * scale;
        float gap = 1 * scale;

        for (int i = 0; i < leftButtons.length; i++) {
            addButton(leftButtons[i], startX + (i * (50 + 1)) * scale, y, btnW, btnH);
        }

        addStaticImage("Combat/QueueBackground.png", 211 * scale, 2 * scale, 406 * scale, 38 * scale);

        addStaticImage("Combat/LogUp.png", 623 * scale, 21 * scale, 20 * scale, 19 * scale);
        addStaticImage("Combat/LogDown.png", 623 * scale, 2 * scale, 20 * scale, 19 * scale);

        String[] rightButtons = {"Spell Book", "Wait", "Defence"};
        float rightStartX = 644 * scale;
        for (int i = 0; i < rightButtons.length; i++) {
            addButton(rightButtons[i], rightStartX + (i * (50 + 1)) * scale, y, btnW, btnH);
        }
    }

    private void addButton(String name, float x, float y, float w, float h) {
        Texture tex = new Texture("Combat/" + name + ".png");
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(new TextureRegion(tex));

        ImageButton btn = new ImageButton(style);
        btn.setBounds(x, y, w, h);
        btn.getImageCell().size(w, h);

        ActionButtonType type = null;
        if (name.equals("Surrender")) type = ActionButtonType.SURRENDER;
        else if (name.equals("Retreat")) type = ActionButtonType.RETREAT;
        else if (name.equals("Options")) type = ActionButtonType.OPTIONS;
        else if (name.equals("Auto")) type = ActionButtonType.AUTO;
        else if (name.equals("Spell Book")) type = ActionButtonType.SPELL_BOOK;
        else if (name.equals("Wait")) type = ActionButtonType.WAIT;
        else if (name.equals("Defence")) type = ActionButtonType.DEFENCE;
        assert (type != null);

        btn.setDisabled(!isEnabledActionButton(type));
        if (isEnabledActionButton(type)) {
            battleActionButtons.add(btn);
        }
        addActionButtonListener(btn, type);
        group.addActor(btn);
    }
    private void addStaticImage(String path, float x, float y, float w, float h) {
        Texture tex = new Texture(path);
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear); // <--- Add this!
        Image img = new Image(tex);
        img.setBounds(x, y, w, h);
        group.addActor(img);
    }

    private boolean isEnabledActionButton(ActionButtonType type) {
        return type == ActionButtonType.DEFENCE || type == ActionButtonType.WAIT;
    }

    public void setBattleInputEnabled(boolean enabled) {
        for (ImageButton button : battleActionButtons) {
            button.setDisabled(!enabled);
        }
    }

    private void addActionButtonListener(ImageButton button, ActionButtonType type) {
        if (type == ActionButtonType.DEFENCE) {
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onDefendClicked.run();
                }
            });
        }

        if (type == ActionButtonType.WAIT) {
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    onWaitClicked.run();
                }
            });
        }
    }
}
