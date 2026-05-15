package io.github.heroes.view;

import com.badlogic.gdx.Game;
//lol
public class Main extends Game {

    @Override
    public void create() {
        this.setScreen(new LobbyScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
