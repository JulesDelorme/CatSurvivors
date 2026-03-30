package io.github.some_example_name.ui;

import com.badlogic.gdx.math.Rectangle;

public class UiButton {
    public final Rectangle bounds = new Rectangle();
    public final String label;
    public final String subLabel;
    public final boolean enabled;

    public UiButton(float x, float y, float width, float height, String label, String subLabel, boolean enabled) {
        bounds.set(x, y, width, height);
        this.label = label;
        this.subLabel = subLabel;
        this.enabled = enabled;
    }

    public boolean contains(float x, float y) {
        return enabled && bounds.contains(x, y);
    }
}
