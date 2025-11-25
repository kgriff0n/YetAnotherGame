package dev.kgriffon.simplegame.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import dev.kgriffon.simplegame.MainGame;

public class MenuScreen implements Screen {

    private final MainGame game;
    private Stage stage;
    private Skin skin;

    private SelectBox<String> typeSelectBox;
    private Image faceSprite;

    public MenuScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

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

        Label typeLabel = new Label("Face :", skin);
        typeSelectBox = new SelectBox<>(skin);
        typeSelectBox.setItems("laugh", "heart", "sleep", "jaded", "shiny");

        faceSprite = new Image(new Texture("texture/entity/face/laugh.png"));

        typeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateTypeImage(typeSelectBox.getSelected());
            }
        });

        table.add(title).colspan(2).padBottom(30).row();
        table.add(nameLabel).pad(5);
        table.add(nameField).width(200).pad(5).row();
        table.add(ipLabel).pad(5);
        table.add(ipField).width(200).pad(5).row();

        table.add(typeLabel).pad(5);
        Table typeTable = new Table();
        typeTable.add(typeSelectBox).width(100).padRight(10);
        typeTable.add(faceSprite).size(64);
        table.add(typeTable).pad(5).row();

        table.add(connectButton).colspan(2).padTop(20);

        connectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String username = nameField.getText().trim();
                String ip = ipField.getText().trim();
                if (username.isEmpty()) username = String.format("Player%03d", (int) (Math.random() * 999));
                game.setScreen(new GameScreen(username, ip, typeSelectBox.getSelected()));
            }
        });
    }

    private void updateTypeImage(String type) {
        faceSprite.setDrawable(new Image(new Texture("texture/entity/face/%s.png".formatted(type))).getDrawable());
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.3f, 0.3f, 0.3f, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }

    //TODO maybe, one day...
//    private final MainGame game;
//    private SpriteBatch batch;
//    private Button playButton;
//    private TextField usernameField;
//    private TextField ipField;
//
//    public MenuScreen(MainGame game) {
//        this.game = game;
//    }
//
//    @Override
//    public void show() {
//        batch = new SpriteBatch();
//
//        usernameField = new TextField(300, 400, 300, 50);
//        ipField = new TextField(300, 320, 300, 50);
//
//        playButton = new Button("Rejoindre", 350, 220, 200, 60, () -> {
//            String username = usernameField.getText();
//            String ip = ipField.getText();
//            if (!username.isEmpty() && !ip.isEmpty()) {
//                game.setScreen(new GameScreen(username, ip));
//            }
//        });
//    }
//
//    @Override
//    public void render(float delta) {
//        ScreenUtils.clear(1, 0, 0, 1);
//
//        usernameField.update(delta);
//        ipField.update(delta);
//        playButton.update(delta);
//
//        batch.begin();
//        usernameField.draw(batch);
//        ipField.draw(batch);
//        playButton.draw(batch);
//        batch.end();
//    }
//
//    @Override public void resize(int width, int height) {}
//    @Override public void pause() {}
//    @Override public void resume() {}
//    @Override public void hide() {}
//
//    @Override
//    public void dispose() {
//        batch.dispose();
//    }
}
