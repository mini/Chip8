
package mini.chip8;

import java.util.Arrays;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Screen extends Canvas {

	private static final Color BG = Color.BLACK;
	private static final Color FG = Color.WHITE;

	private static final int GFX_WIDTH = 64;
	private static final int GFX_HEIGHT = 32;
	private static final int NUM_PIXELS = GFX_WIDTH * GFX_HEIGHT;

	private int scaleX, scaleY;

	private byte[] gfx;
	private GraphicsContext gc;

	public Screen() {
		this(0, 0);
	}

	public Screen(double width, double height) {
		super(width, height);
	}

	public void init() {
		gfx = new byte[GFX_WIDTH * GFX_HEIGHT];
		gc = getGraphicsContext2D();

		widthProperty().addListener((obs, oldV, newV) -> {
			if (oldV != null) {
				gc.clearRect(0, 0, oldV.doubleValue(), getHeight());
			}
			scaleX = (int) (newV.doubleValue() / GFX_WIDTH);
			render();
		});
		heightProperty().addListener((obs, oldV, newV) -> {
			if (oldV != null) {
				gc.clearRect(0, 0, getWidth(), oldV.doubleValue());
			}
			scaleY = (int) (newV.doubleValue() / GFX_HEIGHT);
			render();
		});

		Pane parent = (Pane) getParent();
		widthProperty().bind(parent.widthProperty());
		heightProperty().bind(parent.heightProperty());

		render();
	}

	public byte getPixel(int x, int y) {
		return gfx[(x + y * GFX_WIDTH) % NUM_PIXELS];
	}

	public byte togglePixel(int x, int y) {
		gfx[(x + y * GFX_WIDTH) % NUM_PIXELS] ^= 1;
		return gfx[(x + y * GFX_WIDTH) % NUM_PIXELS];
	}

	public void clear() {
		Arrays.fill(gfx, (byte) 0);
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
