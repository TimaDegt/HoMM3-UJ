package io.github.heroes.model.combat.user.action;

public class SelectSpellUserAction implements UserAction {
    private final int spellIndex;

    public SelectSpellUserAction(int spellIndex) {
        this.spellIndex = spellIndex;
    }

    public int getSpellIndex() {
        return spellIndex;
    }
}
