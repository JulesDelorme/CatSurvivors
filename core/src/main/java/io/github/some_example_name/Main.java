package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import io.github.some_example_name.context.GameContext;

/** Root game object that owns shared services and screen navigation. */
public class Main extends Game {
    private GameContext context;

    @Override
    public void create() {
        context = new GameContext(this);
        context.flow().showMenu();
    }

    public void replaceScreen(Screen screen) {
        Screen previous = getScreen();
        setScreen(screen);
        if (previous != null) {
            previous.dispose();
        }
    }

    @Override
    public void dispose() {
        Screen current = getScreen();
        if (current != null) {
            current.dispose();
            setScreen(null);
        }
        if (context != null) {
            context.dispose();
        }
        super.dispose();
    }
}
