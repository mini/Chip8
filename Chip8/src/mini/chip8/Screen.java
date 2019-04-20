
package mini.chip8;

import java.util.Arrays;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Screen extends Canvas {

	private static final Color BG = Color.BLACK;
	private static final Color FG = Color.LIME;

	private static final int GFX_WIDTH = 64;
	private static final int GFX_HEIGHT = 32;

	private double scaleX, scaleY;

	private byte[] gfx;
	private GraphicsContext gc;

	public Screen() {
		this(0, 0);
	}

	public Screen(double width, double height) {
		super(width, height);
	}

	public void init() {
		scaleX = widthProperty().get() / GFX_WIDTH;
		scaleY = heightProperty().get() / GFX_HEIGHT;

		gc = getGraphicsContext2D();
		gfx = new byte[GFX_WIDTH * GFX_HEIGHT];

		clear();

	}

	public byte getPixel(int x, int y) {
		return gfx[x + y * GFX_WIDTH];
	}

	public byte togglePixel(int x, int y) {
		gfx[x + y * GFX_WIDTH] ^= 1;
		return gfx[x + y * GFX_WIDTH];
	}

	public void clear() {
		Arrays.fill(gfx, (byte) 0);
		gc.setFill(BG);
		gc.fillRect(0, 0, GFX_WIDTH * scaleX, GFX_HEIGHT * scaleY);
	}

	public void render() {
		for (int y = 0; y < GFX_HEIGHT; y++) {
			for (int x = 0; x < GFX_WIDTH; x++) {
				gc.setFill(gfx[x + y * GFX_WIDTH] == 1 ? FG : BG);
				gc.fillRect(x * scaleX, y * scaleY, scaleX, scaleY);
			}
		}
	}
}
