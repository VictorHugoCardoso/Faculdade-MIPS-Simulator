import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mips.Instruction;
import mips.Processor;
import mips.RegisterFile;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Controller {
	private Processor processor;
	private List<Instruction> instructions;

	private volatile boolean running = false;
	private boolean hexadecimal = false;

	public Controller() {
		processor = new Processor();
	}
	private void refresh() {
		int pc = processor.getPcValue();

		int instructionIndex = pc/4;
		
		int[] registerData = processor.getRegisters();
		List<Integer> changedRegisters = processor.getChangedRegisters();
		for(int index : changedRegisters) {
			String repr = String.format(
					"%s: %s", RegisterFile.name(index), string_value(registerData[index]));
		}

		int[] memoryData = processor.getMemory();
		List<Integer> changedMemory = processor.getChangedMemory();
		for(int index : changedMemory) {
			String repr = String.format(
					"%s: %s", string_value((short)index), string_value(memoryData[index]));
		}
	}

	private String string_value(int b) {
		if(hexadecimal) {
			return String.format("0x%x", b & 0xffffffffL);
		} else {
			return String.format("%d", b & 0xffffffffL);
		}
	}

	private synchronized void step() {
		processor.step();
	}

	private void load(String filename) {
		String line;
		BufferedReader reader = null;
		instructions = new ArrayList<Instruction>();

		try {
			reader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}

		try {
			int i = 0;
			while((line = reader.readLine()) != null){
				i++;
				if(line.length() == 0) {
					continue;
				}
				try {
					Instruction instruction = new Instruction(line);
					instructions.add(instruction);
				} catch (Exception e) {
					System.out.printf("Invalid instruction '%s' on line %d\n", line, i);
				}
			}
		} catch (IOException e) {
			System.out.printf("File reading error: %s \n", e.getMessage());
		}
		processor.setInstructionSet(instructions);
		refresh();
	}

	public static void main(String[] args) {
		new Controller();
		System.out.println("asdoi");
	}
}
