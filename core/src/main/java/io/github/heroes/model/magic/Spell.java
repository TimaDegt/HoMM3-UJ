package io.github.heroes.model.magic;

import io.github.heroes.model.combat.ActionResult;
import io.github.heroes.model.combat.BattleEvent;
import io.github.heroes.model.snapshot.UnitSnapshot;
import io.github.heroes.model.state.Hero;
import io.github.heroes.model.state.unit.stack.UnitStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Spell {
    private final int lvl;
    public final MagicSchool school;
    private final String name;
    private final int manaCost;

    protected Spell(String name, int manaCost, int lvl, MagicSchool school) {
        if (manaCost < 0) {
            throw new IllegalArgumentException("Mana cost cannot be negative");
        }
        if (lvl <= 0 || lvl > 5) {
            throw new IllegalArgumentException("Level must be between 1 and 5");
        }
        this.school = school;
        this.lvl = lvl;
        this.name = name;
        this.manaCost = manaCost;
    }
    public int getLvl() {
        return lvl;
    }
    public MagicSchool getMagicSchool() {
        return school;
    }

    public String getName() {
        return name;
    }

    public int getManaCost(Hero caster) {
        return manaCost - lvl * (caster.getMagicSchoolLevel(school)>0?1:0);
    }

    public boolean canCast(Hero caster) {
        return caster.getMana() >= getManaCost(caster);
    }

    public final ActionResult cast(Hero caster, UnitStack castingUnit, UnitStack target) {
        if (caster == null) throw new IllegalArgumentException("Caster cannot be null");
        if (castingUnit == null) throw new IllegalArgumentException("Casting unit cannot be null");
        if (target == null) throw new IllegalArgumentException("Target cannot be null");
        if (!canCast(caster)) return ActionResult.failure();

        int hpBefore = totalHp(target);
        applyEffect(caster, target);
        caster.spendMana(getManaCost(caster));
        int damage = hpBefore - totalHp(target);

        List<BattleEvent> events = new ArrayList<>();
        events.add(new BattleEvent.UnitAttacked(
            UnitSnapshot.from(castingUnit),
            UnitSnapshot.from(target),
            false
        ));
        events.add(new BattleEvent.UnitDamaged(
            UnitSnapshot.from(target),
            damage,
            target.getCount(),
            target.getCurrentHp()
        ));
        if (!target.isAlive()) {
            events.add(new BattleEvent.UnitDied(
                UnitSnapshot.from(target),
                target.getPosition()
            ));
        }
        return ActionResult.success(events);
    }

    protected abstract void applyEffect(Hero caster, UnitStack target);

    private int totalHp(UnitStack unit) {
        if (!unit.isAlive()) return 0;
        return (unit.getCount() - 1) * unit.getMaxHp() + unit.getCurrentHp();
    }
}
