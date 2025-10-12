package dev.kgriffon.simplegame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dev.kgriffon.simplegame.MainGame;

public class MenuScreen implements Screen {
    private final MainGame game;
    private Stage stage;
    private Skin skin;

    public MenuScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Skin par défaut LibGDX
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Table pour layout propre
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label title = new Label("SimpleGame", skin);
        Label nameLabel = new Label("Pseudo :", skin);
        TextField nameField = new TextField(null, skin);
        Label ipLabel = new Label("Adresse IP :", skin);
        TextField ipField = new TextField("localhost", skin);
        TextButton connectButton = new TextButton("Se connecter", skin);

        table.add(title).colspan(2).padBottom(30).row();
        table.add(nameLabel).pad(5);
        table.add(nameField).width(200).pad(5).row();
        table.add(ipLabel).pad(5);
        table.add(ipField).width(200).pad(5).row();
        table.add(connectButton).colspan(2).padTop(20);

        connectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String username = nameField.getText().trim();
                String ip = ipField.getText().trim();
                if (username.isEmpty()) username = String.format("Player%03d", (int) (Math.random() * 999));
                game.setScreen(new GameScreen(username, ip));
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.15f, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}
