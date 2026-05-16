package io.github.heroes.view.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import io.github.heroes.combat.BattleController;
import io.github.heroes.combat.TurnQueueEntry;
import io.github.heroes.view.BattleViewConfig;

import java.util.ArrayList;
import java.util.List;

public class BattleActionPanel {
    private final Table table;
    private final Skin skin;
    private final BattleController battleController;
    private final List<TextButton> queueButtons;

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
        Table leftActionBlock = createActionButtonBlock("Surrender", "Retreat", "Options", "Auto");
        Table queueBlock = new Table();
        Table rightActionBlock = createActionButtonBlock("Spell Book", "Non", "Defence", "Wait");

        for (int i = 1; i <= BattleViewConfig.ACTION_PANEL_QUEUE_BUTTON_COUNT; i++) {
            addQueueButton(queueBlock, "");
        }

        table.add(leftActionBlock).top();
        table.add(queueBlock).top();
        table.add(rightActionBlock).top();
    }

    private Table createActionButtonBlock(String topLeft, String topRight, String bottomLeft, String bottomRight) {
        Table buttonBlock = new Table();
        addActionButton(buttonBlock, topLeft);
        addActionButton(buttonBlock, topRight);
        buttonBlock.row();
        addActionButton(buttonBlock, bottomLeft);
        addActionButton(buttonBlock, bottomRight);
        return buttonBlock;
    }



    private void addActionButton(Table targetTable, String text) {
        TextButton button = new TextButton(text, skin);
        targetTable.add(button)
            .width(BattleViewConfig.ACTION_BUTTON_SIZE)
            .height(BattleViewConfig.ACTION_BUTTON_SIZE)
            .top();
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
