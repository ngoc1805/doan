package card;

import javacard.framework.APDU;
import javacard.framework.ISOException;
import javacard.framework.ISO7816;
import javacard.framework.Util;

public class Avatar {
	
	private final byte[] data;
	private short size = 0;
	
	public Avatar(short maxSize){
		data = new byte[maxSize];
	}
	
	public void setData(byte[] buffer, short bufOffset, short chunkOffset, short chunkLength) {
		if ((short)(chunkOffset + chunkLength) > (short) data.length) {
			ISOException.throwIt(ISO7816.SW_DATA_INVALID);
		}
		Util.arrayCopy(buffer, bufOffset, data, chunkOffset, chunkLength);
		short newEnd = (short) (chunkOffset + chunkLength);
		if (newEnd > size)
			size = newEnd;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public short getSize() {
		return size;
	}
	
	public void setSize(short newSize) {
		size = newSize;
	}
	
	public void clear() {
		Util.arrayFillNonAtomic(data, (short)0, (short)data.length, (byte)0x00);
		size = 0;
	}
}