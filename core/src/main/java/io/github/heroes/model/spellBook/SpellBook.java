package io.github.heroes.model.spellBook;

import io.github.heroes.magic.Spell;
import io.github.heroes.model.state.Hero;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SpellBook {
    private List<Spell> spells;
    private Hero ownr;
    SpellBook(Hero hero) {
        this.spells = new ArrayList<>();
        this.ownr = hero;
    }
    public List<Spell> getSpells() {
        return this.spells;
    }
    public void learnSpell(Spell spell) {
        this.spells.add(spell);
        this.spells.sort(Comparator.comparingInt(Spell::getLvl)
            .thenComparing(Spell::getMagicSchool)
            .thenComparingInt(s -> s.getManaCost(ownr))
            .thenComparing(Spell::getName));
    }
}
