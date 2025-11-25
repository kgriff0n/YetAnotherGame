package dev.kgriffon.simplegame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import dev.kgriffon.simplegame.screen.MenuScreen;

public class MainGame extends Game {
    @Override
    public void create() {
        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }
}
