package io.github.heroes.view.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.heroes.combat.BattleController;
import io.github.heroes.combat.DefendAction;
import io.github.heroes.combat.TurnQueueEntry;
import io.github.heroes.combat.WaitAction;
import io.github.heroes.model.UnitStack;
import io.github.heroes.view.BattleViewConfig;

import java.util.ArrayList;
import java.util.List;

public class BattleActionPanel {
    private final Table table;
    private final Skin skin;
    private final BattleController battleController;
    private final List<TextButton> queueButtons;

    private enum ActionButtonType {
        SURRENDER,
        RETREAT,
        OPTIONS,
        AUTO,
        SPELL_BOOK,
        NONE,
        DEFENCE,
        WAIT
    }

    public BattleActionPanel(Skin skin, BattleController battleController) {
        this.skin = skin;
        this.battleController = battleController;
        this.table = new Table();
        this.queueButtons = new ArrayList<>();

        setupTable();
        setupButtons();
        updateQueueButtons();
    }

    public void addTo(Stage stage) {
        stage.addActor(table);
    }

    public void updateQueueButtons() {
        List<TurnQueueEntry> queue = battleController.getTurnQueueOrder();
        List<String> queueText = new ArrayList<>();
        int curr_index=0;
        while(queueText.size()<BattleViewConfig.ACTION_PANEL_QUEUE_BUTTON_COUNT){
            if (curr_index >= queue.size()){
                queueText.add("");
                continue;
            }

            queueText.add(queue.get(curr_index).getUnitStack().getType().name());

            if (curr_index<queue.size()-1 && queue.get(curr_index).getRound()!=queue.get(curr_index+1).getRound()){
                queueText.add(String.valueOf(queue.get(curr_index).getRound()+1));
            }
            curr_index++;
        }

        for (int i =0;i<BattleViewConfig.ACTION_PANEL_QUEUE_BUTTON_COUNT;i++){
            queueButtons.get(i).setText(queueText.get(i));
        }
    }

    private void setupTable() {
        table.setFillParent(true);
        table.bottom().pad(0);
    }

    private void setupButtons() {
        Table leftActionBlock = createActionButtonBlock(
            "Surrender", ActionButtonType.SURRENDER,
            "Retreat", ActionButtonType.RETREAT,
            "Options", ActionButtonType.OPTIONS,
            "Auto", ActionButtonType.AUTO
        );
        Table queueBlock = new Table();
        Table rightActionBlock = createActionButtonBlock(
            "Spell Book", ActionButtonType.SPELL_BOOK,
            "Non", ActionButtonType.NONE,
            "Defence", ActionButtonType.DEFENCE,
            "Wait", ActionButtonType.WAIT
        );

        for (int i = 1; i <= BattleViewConfig.ACTION_PANEL_QUEUE_BUTTON_COUNT; i++) {
            addQueueButton(queueBlock, "");
        }

        table.add(leftActionBlock).top();
        table.add(queueBlock).top();
        table.add(rightActionBlock).top();
    }

    private Table createActionButtonBlock(
        String topLeft,
        ActionButtonType topLeftType,
        String topRight,
        ActionButtonType topRightType,
        String bottomLeft,
        ActionButtonType bottomLeftType,
        String bottomRight,
        ActionButtonType bottomRightType
    ) {
        Table buttonBlock = new Table();
        addActionButton(buttonBlock, topLeft, topLeftType);
        addActionButton(buttonBlock, topRight, topRightType);
        buttonBlock.row();
        addActionButton(buttonBlock, bottomLeft, bottomLeftType);
        addActionButton(buttonBlock, bottomRight, bottomRightType);
        return buttonBlock;
    }



    private void addActionButton(Table targetTable, String text, ActionButtonType type) {
        TextButton button = new TextButton(text, skin);
        addActionButtonListener(button, type);
        targetTable.add(button)
            .width(BattleViewConfig.ACTION_BUTTON_SIZE)
            .height(BattleViewConfig.ACTION_BUTTON_SIZE)
            .top();
    }

    private void addActionButtonListener(TextButton button, ActionButtonType type) {
        if (type == ActionButtonType.DEFENCE) {
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    UnitStack activeUnit = battleController.getActiveUnit();
                    if (activeUnit == null) return;

                    battleController.performAction(new DefendAction(activeUnit));
                    updateQueueButtons();
                }
            });
        }

        if (type == ActionButtonType.WAIT) {
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    UnitStack activeUnit = battleController.getActiveUnit();
                    if (activeUnit == null) return;

                    battleController.performAction(new WaitAction(activeUnit));
                    updateQueueButtons();
                }
            });
        }
    }

    private void addQueueButton(Table targetTable, String text) {
        TextButton button = new TextButton(text, skin);
        queueButtons.add(button);
        targetTable.add(button)
            .width(BattleViewConfig.QUEUE_BUTTON_WIDTH)
            .height(BattleViewConfig.QUEUE_BUTTON_HEIGHT)
            .top();
    }
}
