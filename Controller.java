import java.io.*;
import java.util.*;

import mips.Instruction;
import mips.Processor;
import mips.RegisterFile;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Controller {
	private Processor processor;
	private List<Instruction> instructions;

	private volatile boolean running = false;
	private boolean hexadecimal = false;

	public Controller () {
		processor = new Processor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("Entre com o nome do arquivo texto:");
			String arquivo = reader.readLine();
			arquivo = "teste.txt";
			load(arquivo);
		} catch (IOException e) {}

        do {
			try {
        		reader.readLine();
			} catch (IOException e) {}
			step();
        } while (1==1);
	}
	private void refresh() {
		int pc = processor.getPcValue();

		int[] registerData = processor.getRegisters();
		List<Integer> changedRegisters = processor.getChangedRegisters();
		System.out.println("====================[ REGISTRADORES ]====================");
		for(int index : changedRegisters) {
			String repr = String.format(
					"%s: %s", RegisterFile.name(index), string_value(registerData[index]));
			System.out.println(repr);
		}

		int[] memoryData = processor.getMemory();
		List<Integer> changedMemory = processor.getChangedMemory();
		System.out.println("\n====================[    MEMORIA    ]====================");
		for(int index : changedMemory) {
			String repr = String.format(
					"%s: %s", string_value((short)index), string_value(memoryData[index]));
			System.out.println(repr);
		}
		System.out.println("\n--------------------------------------------------------------------------");
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
		refresh();
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
	}
}
