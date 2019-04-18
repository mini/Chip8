package mini.chip8;

public class Chip8 {
	public static final int MEMORY_SIZE = 0x1000;
	public static final int LOAD_INDEX = 0x200;
	public static final int NUM_REGISTERS = 16;
	public static final int GFX_WIDTH = 64;
	public static final int GFX_HEIGHT = 32;

	// TODO change to proper size
	private int[] memory; // u8
	private int[] registers; // u8
	private int opcode; // u16
	private int I; // u16
	private int pc; // u16
	private boolean drawFlag;

	private int sp; // u16
	private int[] stack; // u16

	private byte[] gfx;

	private int delayTimer; // u16
	private int soundTimer; // u16

	public Chip8() {
		memory = new int[MEMORY_SIZE];
		registers = new int[NUM_REGISTERS];

		opcode = 0;
		I = 0;
		pc = 0x200;
		sp = 0;

		gfx = new byte[GFX_WIDTH * GFX_HEIGHT];

		delayTimer = 0;
		soundTimer = 0;
	}

	public void load(byte[] src) {
		if (src.length > MEMORY_SIZE - LOAD_INDEX) {
			System.err.println("File too big");
			System.exit(-1);
		}

		for (int i = 0; i < src.length; i++) {
			memory[i + LOAD_INDEX] = src[i] & 0xFF;
		}
	}

	private void step() {
		opcode = memory[pc] << 8 | memory[pc + 1];

		switch (opcode & 0xF000) {
		case 0x0000:
			switch (opcode & 0xF) {
			case 0x0: // 00E0 - CLS
				break;
			case 0xE: // 00EE - RET
				break;
			default: // 0NNN - IGNORED
				NOP();
			}
			break;
		case 0x1000: // 1NNN - JMP
			TODO();
			break;
		case 0x2000: // 2NNN - CALL
			TODO();
			break;
		case 0x3000: // 3XNN - SE Vx, byte
			TODO();
			break;
		case 0x4000: // 4XNN - SNE Vx, byte
			TODO();
			break;
		case 0x5000: // 5XY0 - SE Vx, Vy
			TODO();
			break;
		case 0x6000: // 6xNN - LD Vx, byte
			TODO();
			break;
		case 0x7000: // 7XNN - ADD Vx, byte
			TODO();
			break;
		case 0x8000:
			switch (opcode & 0xF) {
			case 0x0: // 8XY0 - LD Vx, Vy
				TODO();
				break;
			case 0x1: // 8XY1 - OR Vx, Vy
				TODO();
				break;
			case 0x2: // 8XY2 - AND Vx, Vy
				TODO();
				break;
			case 0x3: // 8XY3 - XOR Vx, Vy
				TODO();
				break;
			case 0x4: // 8XY4 - ADD Vx, Vy
				TODO();
				break;
			case 0x5: // 8XY5 - SUB Vx, Vy
				TODO();
				break;
			case 0x6: // 8XY6 - SHR Vx
				TODO();
				break;
			case 0x7: // 8XY7 - SUBN Vx, Vy
				TODO();
				break;
			case 0xE: // 8XYE - SHL Vx
				TODO();
				break;
			default:
				NOP();
			}
			break;
		case 0x9000: // 9XY0 - SNE Vx, Vy
			TODO();
			break;
		case 0xA000: // ANNN - LDI, addr
			TODO();
			break;
		case 0xB000: // BNNN - JP0, addr
			TODO();
			break;
		case 0xC000: // CXNN - RND Vx, byte
			TODO();
			break;
		case 0xD000: // DXYN - DRW Vx, Vy, nibble
			TODO();
			break;
		case 0xE000:
			switch (opcode & 0xF) {
			case 0xE: // EX9E - SKP Vx
				TODO();
				break;
			case 0x1: // EXA1 - SKNP Vx
				TODO();
				break;
			default:
				NOP();
			}
			break;
		case 0xF000:
			switch (opcode & 0xFF) {
			case 0x07: // FX07 - LD Vx, DT 
				TODO();
				break;
			case 0x0A: // FX0A - LD Vx, Key
				TODO();
				break;
			case 0x15: // FX15 - LD DT, Vx
				TODO();
				break;
			case 0x18: // FX18 - LD ST, Vx
				TODO();
				break;
			case 0x1E: // FX1E - ADD I, Vx
				TODO();
				break;
			case 0x29: // FX29 - LD F, Vx
				TODO();
				break;
			case 0x33: // FX33 - LD B, Vx
				TODO();
				break;
			case 0x55: // FX55 - LD [I], Vx
				TODO();
				break;
			case 0x65: // FX65 - LD Vx, [I]
				TODO();
				break;
			}
			break;
		}

		pc += 2;
	}

	private void NOP() {
		System.out.printf("0x%X unknown opcode", opcode);
	}

	private void TODO() {
		System.out.printf("0x%X not implemented", opcode);
		System.exit(1);
	}

}
