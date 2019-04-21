package mini.chip8;

import java.util.Arrays;
import java.util.Random;

public class Chip8 {
	private static final int MEMORY_SIZE = 0x1000;
	private static final int LOAD_INDEX = 0x200;
	private static final int NUM_REGISTERS = 16;
	private static final int STACK_SIZE = 16;
	private static final int NUM_KEYS = 16;
	private static final int VF = 0xF;

	private static final int[] FONT_SET = {
	        0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
	        0x20, 0x60, 0x20, 0x20, 0x70, // 1
	        0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
	        0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
	        0x90, 0x90, 0xF0, 0x10, 0x10, // 4
	        0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
	        0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
	        0xF0, 0x10, 0x20, 0x40, 0x40, // 7
	        0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
	        0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
	        0xF0, 0x90, 0xF0, 0x90, 0x90, // A
	        0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
	        0xF0, 0x80, 0x80, 0x80, 0xF0, // C
	        0xE0, 0x90, 0x90, 0x90, 0xE0, // D
	        0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
	        0xF0, 0x80, 0xF0, 0x80, 0x80, // F
	};

	private Random rand;
	private Screen screen;

	/* State */
	// TODO change to proper size
	private int[] memory; // u8 * 4K
	private int[] regs; // u8 * 16
	private int opcode; // u16
	private int I; // u16
	private int pc; // u16
	private boolean drawFlag;

	private int sp; // u16 could be u8
	private int[] stack; // u16 * 16

	private int delayTimer; // u8
	private int soundTimer; // u8

	private boolean[] keys;

	public Chip8(Screen screen) {
		rand = new Random(System.nanoTime());
		this.screen = screen;
		reset();
	}

	public void reset() {
		memory = new int[MEMORY_SIZE];
		System.arraycopy(FONT_SET, 0, memory, 0, FONT_SET.length);

		regs = new int[NUM_REGISTERS];

		opcode = 0;
		I = 0;
		pc = LOAD_INDEX;
		drawFlag = false;

		sp = 0;
		stack = new int[STACK_SIZE];

		delayTimer = 0;
		soundTimer = 0;

		keys = new boolean[NUM_KEYS];
	}

	public void load(byte[] src) throws IndexOutOfBoundsException {
		if (src.length > MEMORY_SIZE - LOAD_INDEX) {
			throw new IndexOutOfBoundsException(String.format("File too big. (expected <=%d Bytes got %d Bytes)",
			        MEMORY_SIZE - LOAD_INDEX, src.length));
		}

		for (int i = 0; i < src.length; i++) {
			memory[i + LOAD_INDEX] = src[i] & 0xFF;
		}
	}

	void step() {
		opcode = memory[pc] << 8 | memory[pc + 1];
		System.out.printf("pc: 0x%X ", pc);
		pc += 2;

		int vx = (opcode & 0x0F00) >> 8; 	// 0X00
		int vy = (opcode & 0x00F0) >> 4; 	// 00Y0
		int addr = opcode & 0x0FFF; 		// 0NNN
		int val = opcode & 0x00FF; 			// 00NN
		int nib = opcode & 0x000F; 			// 000N

		switch (opcode & 0xF000) {
			case 0x0000:
				switch (opcode & 0x00FF) {
					case 0xE0: // 00E0 - CLS
						screen.clear();
						drawFlag = true;
						break;
					case 0xEE: // 00EE - RET
						pc = stack[sp--];
						break;
					default: // 0NNN - IGNORED
						NOP();
				}
				break;
			case 0x1000: // 1NNN - JMP
				pc = addr;
				break;
			case 0x2000: // 2NNN - CALL
				stack[++sp] = pc;
				pc = addr;
				break;
			case 0x3000: // 3XNN - SE Vx, byte
				if (regs[vx] == val) {
					pc += 2;
				}
				break;
			case 0x4000: // 4XNN - SNE Vx, byte
				if (regs[vx] != val) {
					pc += 2;
				}
				break;
			case 0x5000: // 5XY0 - SE Vx, Vy
				if (regs[vx] == regs[vy]) {
					pc += 2;
				}
				break;
			case 0x6000: // 6xNN - LD Vx, byte
				regs[vx] = val;
				break;
			case 0x7000: // 7XNN - ADD Vx, byte
				regs[vx] += val;
				regs[vx] &= 0xFF;
				break;
			case 0x8000:
				switch (opcode & 0x000F) {
					case 0x0: // 8XY0 - LD Vx, Vy
						regs[vx] = regs[vy];
						break;
					case 0x1: // 8XY1 - OR Vx, Vy
						regs[vx] |= regs[vy];
						break;
					case 0x2: // 8XY2 - AND Vx, Vy
						regs[vx] &= regs[vy];
						break;
					case 0x3: // 8XY3 - XOR Vx, Vy
						regs[vx] ^= regs[vy];
						break;
					case 0x4: // 8XY4 - ADD Vx, Vy
						regs[vx] += regs[vy];
						regs[VF] = regs[vx] > 0xFF ? 1 : 0; // carry
						regs[vx] &= 0xFF;
						break;
					case 0x5: // 8XY5 - SUB Vx, Vy
						regs[VF] = regs[vx] > regs[vy] ? 1 : 0;
						regs[vx] -= regs[vy];
						regs[vx] &= 0xFF;
						break;
					case 0x6: // 8XY6 - SHR Vx
						regs[VF] = regs[vx] & 0x01;
						regs[vx] >>= 1;
						break;
					case 0x7: // 8XY7 - SUBN Vx, Vy
						regs[VF] = regs[vy] > regs[vx] ? 1 : 0;
						regs[vx] = regs[vy] - regs[vx];
						regs[vx] &= 0xFF;
						break;
					case 0xE: // 8XYE - SHL Vx
						regs[VF] = (regs[vx] & 0x80) >> 7;
						regs[vx] <<= 1;
						regs[vx] &= 0xFF;
						break;
					default:
						NOP();
				}
				break;
			case 0x9000: // 9XY0 - SNE Vx, Vy
				if (regs[vx] != regs[vy] && nib == 0) {
					pc += 2;
				}
				break;
			case 0xA000: // ANNN - LDI, addr
				I = addr;
				break;
			case 0xB000: // BNNN - JP0, addr
				pc = regs[0] + addr;
				pc &= 0xFFF;
				break;
			case 0xC000: // CXNN - RND Vx, byte
				regs[vx] = rand.nextInt(256) & val;
				break;
			case 0xD000: // DXYN - DRW Vx, Vy, nibble
				drawFlag = true;
				regs[VF] = 0;
				for (int y = 0; y < nib; y++) {
					int spriteRow = memory[I + y];
					for (int x = 0; x < 8; x++) {
						if ((spriteRow & (0x80 >> x)) > 0) { // Sprite Pixel set
							if (screen.togglePixel(regs[vx] + x, regs[vy] + y) == 0) {
								regs[VF] = 1;
							}
						}
					}
				}

				break;
			case 0xE000:
				switch (opcode & 0x00FF) {
					case 0x9E: // EX9E - SKP Vx
						if (keys[regs[vx]]) {
							pc += 2;
						}
						break;
					case 0xA1: // EXA1 - SKNP Vx
						if (!keys[regs[vx]]) {
							pc += 2;
						}
						break;
					default:
						NOP();
				}
				break;
			case 0xF000:
				switch (opcode & 0x00FF) {
					case 0x07: // FX07 - LD Vx, DT
						regs[vx] = delayTimer;
						break;
					case 0x0A: // FX0A - LD Vx, Key
						pc -= 2;
						for (int i = 0; i < NUM_KEYS; i++) {
							if (keys[i]) {
								regs[vx] = i;
								pc += 2;
								break;
							}
						}
						break;
					case 0x15: // FX15 - LD DT, Vx
						delayTimer = regs[vx];
						break;
					case 0x18: // FX18 - LD ST, Vx
						soundTimer = regs[vx];
						break;
					case 0x1E: // FX1E - ADD I, Vx
						I += regs[vx];
						regs[VF] = I > 0xFFF ? 1 : 0;
						I &= 0xFFF;
						break;
					case 0x29: // FX29 - LD F, Vx
						I = regs[vx] * 5;
						break;
					case 0x33: // FX33 - LD B, Vx
						val = regs[vx];
						memory[I + 2] = val % 10; // ones
						val /= 10;
						memory[I + 1] = val % 10; // tens
						memory[I] = val / 10;// hundreds
						break;
					case 0x55: // FX55 - LD [I], Vx
						for (int v = 0; v <= vx; v++) {
							memory[I++] = regs[v];
						}
						break;
					case 0x65: // FX65 - LD Vx, [I]
						for (int v = 0; v <= vx; v++) {
							regs[v] = memory[I++];
						}
						break;
					default:
						NOP();
				}
				break;
			default:
				NOP();
		}

		System.out.printf("op: 0x%X %b %s\n", opcode, drawFlag, Arrays.toString(regs));
	}

	public void updateTimer() {
		if (delayTimer > 0) {
			delayTimer--;
		}

		if (soundTimer > 0) {
			soundTimer--;
			if (soundTimer == 0) {
				System.out.println("Beep");
			}
		}
	}

	boolean shouldDraw() {
		return drawFlag;
	}

	void resetDrawFlag() {
		drawFlag = false;
	}

	void setKey(int n, boolean state) {
		keys[n] = state;
	}

	private void NOP() {
//		System.out.printf("0x%X unknown opcode", opcode);
	}
}
