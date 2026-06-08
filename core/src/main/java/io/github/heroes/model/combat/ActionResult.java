package io.github.heroes.model.combat;

import java.util.ArrayList;
import java.util.List;

public record ActionResult(
    boolean successful,
    List<BattleEvent> events
) {
    public ActionResult {
        events = List.copyOf(events);
    }

    public static ActionResult success(List<BattleEvent> events) {
        return new ActionResult(true, events);
    }

    public static ActionResult failure()  {return new ActionResult(false, List.of() );}
}
