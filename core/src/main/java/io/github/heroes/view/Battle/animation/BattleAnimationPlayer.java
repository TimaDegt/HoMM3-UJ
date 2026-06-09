package io.github.heroes.view.Battle.animation;

import io.github.heroes.model.snapshot.UnitSnapshot;

import java.util.HashMap;
import java.util.Map;

public class BattleAnimationPlayer {
    private final Map<Long, Animation> animations = new HashMap<>();

    public Animation getAnimationEngine(UnitSnapshot unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");

        return animations.computeIfAbsent(
            unit.id(),
            key -> new Animation(UnitAnimationConfigs.get(unit.type()))
        );
    }

    public void clear() {
        animations.clear();
    }
}
