package card; 

import javacard.security.MessageDigest; 
import javacard.framework.Util; 
import javacard.framework.JCSystem; 

public class PBKDF2 {     
    private static final short BLOCK_SIZE = 64;  
    private static final short HASH_SIZE = 32;   
    private static final short AES_128_KEY_SIZE = 16;          
    private static final short SALT_LEN_DEFAULT = 16; 

    private final MessageDigest sha256;          
    
    private final byte[] ipad; 
    private final byte[] opad; 
    private final byte[] innerHash; 
    
    private final byte[] tempU;         
    private final byte[] tempResult;    
    private final byte[] tempSaltCounter; 

    public PBKDF2() {         
        sha256 = MessageDigest.getInstance(MessageDigest.ALG_SHA_256, false);                  
        
        ipad = JCSystem.makeTransientByteArray(BLOCK_SIZE, JCSystem.CLEAR_ON_RESET);
        opad = JCSystem.makeTransientByteArray(BLOCK_SIZE, JCSystem.CLEAR_ON_RESET);
        innerHash = JCSystem.makeTransientByteArray(HASH_SIZE, JCSystem.CLEAR_ON_RESET);
        
        tempU = JCSystem.makeTransientByteArray(HASH_SIZE, JCSystem.CLEAR_ON_RESET);
        tempResult = JCSystem.makeTransientByteArray(HASH_SIZE, JCSystem.CLEAR_ON_RESET);
        // Salt (16) + Counter (4) = 20 bytes
        tempSaltCounter = JCSystem.makeTransientByteArray((short)(SALT_LEN_DEFAULT + 4), JCSystem.CLEAR_ON_RESET);
    }          

    private void hmac(byte[] key, short keyOff, short keyLen,                      
                      byte[] data, short dataOff, short dataLen,                      
                      byte[] output, short outOff) {                  
        // lam sach ipad, opad
        sha256.reset();         
        Util.arrayFillNonAtomic(ipad, (short)0, BLOCK_SIZE, (byte)0x00);         
        Util.arrayFillNonAtomic(opad, (short)0, BLOCK_SIZE, (byte)0x00);         
        Util.arrayFillNonAtomic(innerHash, (short)0, HASH_SIZE, (byte)0x00);                  
        
        if (keyLen <= BLOCK_SIZE) {          
            Util.arrayCopyNonAtomic(key, keyOff, ipad, (short)0, keyLen);             
            Util.arrayCopyNonAtomic(key, keyOff, opad, (short)0, keyLen);         
        } else {          
            sha256.reset();             
            sha256.doFinal(key, keyOff, keyLen, ipad, (short)0);             
            Util.arrayCopyNonAtomic(ipad, (short)0, opad, (short)0, HASH_SIZE);         
        }                  
        // XOR key voi hang so ipad(0x36) và opad(0x5c)
        for (short i = 0; i < BLOCK_SIZE; i++) {             
            ipad[i] ^= (byte)0x36;             
            opad[i] ^= (byte)0x5c;         
        }         
        // tinh Inner Hash + Du lieu(salt hoac ket qua vong truoc)
        sha256.reset();         
        sha256.update(ipad, (short)0, BLOCK_SIZE);         
        sha256.doFinal(data, dataOff, dataLen, innerHash, (short)0);    
        // tinh Outer Hash + ket qua bam lan 1              
        sha256.reset();         
        sha256.update(opad, (short)0, BLOCK_SIZE);         
        sha256.doFinal(innerHash, (short)0, HASH_SIZE, output, outOff);     
    }          

// khi goi deriveKey(pin, salt, 1000, keyOutput):
// - the tao vung nho dem chua salt + 00 00 00 01
// - chay ham hmac voi pin va vung nho dem cho ra u1
// - luu u1 vao Result
// - chay vong lap 999 lan con lai: lay U cu chay qua hmac voi pin ra U moi, lay Result XOR voi U moi
// - cat 16 byte dau cua Result tra ve lam KEK de giai ma MasterKey
    public void deriveKey(byte[] password, short passwordOff, short passwordLen,                          
                          byte[] salt, short saltOff, short saltLen,                          
                          short iterations,                          
                          byte[] output, short outOff) {                              
        
        // lam sach bo nho dem
        Util.arrayFillNonAtomic(tempU, (short)0, HASH_SIZE, (byte)0x00);
        Util.arrayFillNonAtomic(tempResult, (short)0, HASH_SIZE, (byte)0x00);
        Util.arrayFillNonAtomic(tempSaltCounter, (short)0, (short)tempSaltCounter.length, (byte)0x00);
        
        // Copy Salt vao buffer
        Util.arrayCopyNonAtomic(salt, saltOff, tempSaltCounter, (short)0, saltLen); 
        // gan counter = 1(00 00 00 01) vao cuoi salt        
        tempSaltCounter[saltLen] = 0;         
        tempSaltCounter[(short)(saltLen + 1)] = 0;         
        tempSaltCounter[(short)(saltLen + 2)] = 0;         
        tempSaltCounter[(short)(saltLen + 3)] = 1;                  
        
        short saltCounterLen = (short)(saltLen + 4);

        // tinh U1 = HMAC(...)
        hmac(password, passwordOff, passwordLen,              
             tempSaltCounter, (short)0, saltCounterLen,              
             tempU, (short)0);                  
        
        // result = U1         
        Util.arrayCopyNonAtomic(tempU, (short)0, tempResult, (short)0, HASH_SIZE);                  
        
        // vong lap tu  U2 -> Uc
        // tinh ui = HMAC(password, ui-1)
        for (short i = 1; i < iterations; i++) {             
            hmac(password, passwordOff, passwordLen,                  
                 tempU, (short)0, HASH_SIZE,                  
                 tempU, (short)0);                          
            
            // XOR
            for (short j = 0; j < HASH_SIZE; j++) {                 
                tempResult[j] ^= tempU[j];             
            }         
        }                  
        
       
        Util.arrayCopyNonAtomic(tempResult, (short)0, output, outOff, AES_128_KEY_SIZE);     
    } 
}