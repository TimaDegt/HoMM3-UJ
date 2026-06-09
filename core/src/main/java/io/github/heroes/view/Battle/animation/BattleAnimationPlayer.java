package io.github.heroes.view.Battle.animation;

import io.github.heroes.model.state.UnitStack;

import java.util.IdentityHashMap;
import java.util.Map;

public class BattleAnimationPlayer {
    private final Map<UnitStack, Animation> animations = new IdentityHashMap<>();

    public Animation getAnimationEngine(UnitStack unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");

        return animations.computeIfAbsent(
            unit,
            key -> new Animation(UnitAnimationConfigs.get(key.getType()))
        );
    }

    public void clear() {
        animations.clear();
    }
}
