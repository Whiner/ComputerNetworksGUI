package nodeGenerator.drawer.other;

import java.awt.*;

public class ColorUse {
    private Color color;
    private boolean used;

    public ColorUse(Color color) {
        this.color = color;
        this.used = false;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
