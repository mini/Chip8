package mini.chip8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App {

	public static void main(String[] args) {
		System.out.println("test print plz ignore");
		new Chip8().load(readFile("../roms/BLINKY"));
	}
	

	public static byte[] readFile(String romPath){
		try {
			return Files.readAllBytes(Paths.get(romPath));
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
}
