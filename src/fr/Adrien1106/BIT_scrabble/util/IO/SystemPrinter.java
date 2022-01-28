package fr.Adrien1106.BIT_scrabble.util.IO;

public class SystemPrinter implements Printer {

	@Override
	public void println(String msg) {
		System.out.println(msg);
	}

	@Override
	public void print(String msg) {
		System.out.print(msg);
	}

}
