package dev.kgriffon.simplegame;

import com.badlogic.gdx.Game;
import dev.kgriffon.simplegame.screen.MenuScreen;

public class MainGame extends Game {
    @Override
    public void create() {
        setScreen(new MenuScreen(this));
    }
}
