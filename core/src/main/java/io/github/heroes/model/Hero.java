package io.github.heroes.model;

import io.github.heroes.magic.MagicSchool;

public class Hero {
    private final String name;
    private int attack;
    private int defense;
    private int spellPower;
    private int knowledge;
    private int mana;
    private int maxMana;
    private boolean hasCastSpellThisRound = false;

    private void validatePositiveValue(int val) {
        if (val < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }
    }

    public Hero(String name, int attack, int defense, int spellPower, int knowledge) {
        this.name = name;
        this.attack = attack;
        this.defense = defense;
        this.spellPower = spellPower;
        this.knowledge = knowledge;
        this.mana = knowledge * 10;
        this.maxMana = 999;
        this.hasCastSpellThisRound = false;

    }

    public String getName() {
        return name;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpellPower() {
        return spellPower;
    }

    public int getKnowledge() {
        return knowledge;
    }

    public int getMana() {
        return mana;
    }

    public void spendMana(int amount) {
        if (amount <= 0)return;
        mana = Math.max(0, mana - amount);
    }

    public int getMagicSchoolLevel(MagicSchool school) {
        return 0;
    }

    public void increaseAttack(int val) {
        validatePositiveValue(val);
        attack += val;
    }

    public void increaseDefense(int val) {
        validatePositiveValue(val);
        defense += val;
    }

    public void increaseSpellPower(int val) {
        validatePositiveValue(val);
        spellPower += val;
    }

    public void increaseKnowledge(int val) {
        validatePositiveValue(val);
        knowledge += val;
        mana += val * 10;
    }

    public void increaseMana(int val) {
        validatePositiveValue(val);
        mana += val;
    }

    public boolean hasCastSpellThisRound() {
        return hasCastSpellThisRound;
    }

    public void setCastSpellThisRound(boolean cast) {
        this.hasCastSpellThisRound = cast;
    }

}

