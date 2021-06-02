package commande;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.StringJoiner;

import decision.Instructions;

public class CommandeParams extends Commande {

	private float arguments;

	public static byte[] floatToByteArray(float value) {
		int intBits = Float.floatToIntBits(value);
		return new byte[] { (byte) (intBits >> 24), (byte) (intBits >> 16), (byte) (intBits >> 8), (byte) (intBits) };
	}

	public CommandeParams(Instructions instruction, float arguments) {
		super(instruction);
		this.arguments = arguments;
	}

	@Override
	public String toString() {
		byte[] bytes = floatToByteArray(arguments);
		String str = new String(bytes, StandardCharsets.ISO_8859_1);
		return super.toString() + str;
	}
	
	@Override
	public byte[] getBytes() {
		byte[] bytes = floatToByteArray(arguments);
		byte commandCharCode = (byte) instruction.getCode();
		return new byte[] {commandCharCode, bytes[0], bytes[1], bytes[2], bytes[3] } ;
	}

	public static void main(String[] args) {
		float a = 142.85332f;
		byte[] bytes = floatToByteArray(a);
		System.out.println(Arrays.toString(bytes));
		System.out.println(ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getFloat());
		String str = new CommandeTurn(a).toString();
		System.out.println(str);
		System.out.println(Arrays.toString(str.getBytes()));
		System.out.println();
	}

}
