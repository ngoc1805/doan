package card;

import javacard.security.MessageDigest;
import javacard.security.RandomData;
import javacard.framework.ISOException;
import javacard.framework.ISO7816;
import javacard.framework.JCSystem;
import javacard.framework.Util;

public class PIN {
    private static final short SALT_LENGTH = 16;
    private static final short KEY_LENGTH = 16;  
    private static final short ITERATIONS = 50; 
    
    private final byte[] pinHash;       
    private final byte[] salt;          
    
    // khoa duoc sinh ra tu pin + salt duoc luu trong ram
    private final byte[] derivedKey;   
    // bo dem tam thoi de tinh hash
    private final byte[] tempHashBuffer;  
    
    private byte tryLimit;
    private byte triesRemaining;
    private boolean isValidated;
    
    private final MessageDigest messageDigest;
    private final PBKDF2 pbkdf2; //  Dung instance duoc truyen vao
    private final RandomData random;
    
    // DA SUA: Them tham so pbkdf2Engine vao constructor
    public PIN(byte[] pinBuffer, short pinOffset, short pinLength, byte tryLimit, PBKDF2 pbkdf2Engine) {
        this.pinHash = new byte[32];
        this.salt = new byte[SALT_LENGTH];
        
        this.derivedKey = JCSystem.makeTransientByteArray(KEY_LENGTH, JCSystem.CLEAR_ON_RESET);
        this.tempHashBuffer = JCSystem.makeTransientByteArray((short)32, JCSystem.CLEAR_ON_RESET);
        
        this.tryLimit = tryLimit;
        this.triesRemaining = tryLimit;
        this.isValidated = false;
        
        this.messageDigest = MessageDigest.getInstance(MessageDigest.ALG_SHA_256, false);
        this.pbkdf2 = pbkdf2Engine; // Gan instance dung chung
        this.random = RandomData.getInstance(RandomData.ALG_SECURE_RANDOM);
        
        // 1. tao salt ngau nhien duy nhat
        random.generateData(salt, (short)0, SALT_LENGTH);
        
        // 2. Hash PIN luu vao pinhash
        messageDigest.doFinal(pinBuffer, pinOffset, pinLength, pinHash, (short)0);
        
        // 3. Tinh KEK (Encryption Key)
        computeKEK(pinBuffer, pinOffset, pinLength);
    }
    
    private void computeKEK(byte[] pin, short offset, short length) {
        pbkdf2.deriveKey(
            pin, offset, length,
            salt, (short)0, SALT_LENGTH,
            ITERATIONS,
            derivedKey, (short)0 
        );
    }
    
    public boolean match(byte[] buffer, short offset, short length) {
        if (triesRemaining == (byte)0x00) return false;
        
        // 1. Check Hash : xac thuc pin mà nguoi dung vua nhap
        messageDigest.reset();
        messageDigest.doFinal(buffer, offset, length, tempHashBuffer, (short)0);
        // cam di so sanh xem giông khong, sai thi tru luot nhap
        if (Util.arrayCompare(pinHash, (short)0, tempHashBuffer, (short)0, (short)pinHash.length) != 0) {
            triesRemaining--;
            return false;
        }

        // 2. tính KEK
        triesRemaining = tryLimit;
        isValidated = true;
        computeKEK(buffer, offset, length); // tinh l i khoa KEK luu vao RAM cho phien lam viec
            
        return true;
    }
    
    public void update(byte[] buffer, short offset, short length) {
        if (length < 1) ISOException.throwIt(ISO7816.SW_DATA_INVALID);
        // bam pin moi va ghi de vao pin cu
        messageDigest.reset();
        messageDigest.doFinal(buffer, offset, length, pinHash, (short)0);
        // tinh lai KEK tu pin moi, giu nguyen salt
        computeKEK(buffer, offset, length);
        
        triesRemaining = tryLimit;
    }
    
    public byte[] getKEK() {
        return derivedKey;
    }
    
    public byte getTriesRemaining() {
        return triesRemaining;
    }
    
    public void resetPin() {
        triesRemaining = tryLimit;
        isValidated = false;
        Util.arrayFillNonAtomic(derivedKey, (short)0, KEY_LENGTH, (byte)0x00);
    }
    // Chi xoa phien dang nhap, KHONG duoc reset triesRemaining
    public void logout() {
        isValidated = false;
        Util.arrayFillNonAtomic(derivedKey, (short)0, KEY_LENGTH, (byte)0x00);
    }
    
    public byte[] getSalt() {
        return salt;
    }


    public void reset() {
        resetPin();
    }

    public boolean isValidated() {
        return isValidated;
    }
    // dung de xem xac thuc pin cu truoc khi nhap pin moi, pin moi kh trung pin cu,....
    public boolean check(byte[] buffer, short offset, short length) {
        // 1. Hash PIN nhap vao lui vao tempHashBuffer
        messageDigest.reset();
        messageDigest.doFinal(buffer, offset, length, tempHashBuffer, (short)0);
        
        // so sanh hash pin voi pin hien tai
        if (Util.arrayCompare(pinHash, (short)0, tempHashBuffer, (short)0, (short)pinHash.length) == 0) {
            return true; // Trùng kh p
        }
        
        return false; // Khác nhau
    }
}