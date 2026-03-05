package card;

import javacard.framework.Util;
import javacard.framework.Applet;
import javacard.framework.APDU;
import javacard.framework.JCSystem;
import javacard.framework.ISOException;
import javacard.framework.ISO7816;
import javacard.security.AESKey;
import javacard.security.KeyBuilder;
import javacardx.apdu.ExtendedLength;
import javacardx.crypto.Cipher;
import javacard.security.KeyPair;
import javacard.security.RSAPrivateKey;
import javacard.security.RSAPublicKey;
import javacard.security.Signature;
import javacard.security.RandomData;

public class HospitalCard extends Applet implements ExtendedLength {
    public static final byte NUMBER_RETRY = 5;
    public static final byte ROLE_ADMIN = (byte) 0x01;
    public static final short SW_PIN_SAME_AS_OLD = (short) 0x6A80;
    
    // Khoa 16 bytes dung de ma hoa Pin tren duong truyen (Transport Key)
    private static final byte[] TRANSPORT_KEY_DATA = { 
        (byte)0x4B, (byte)0x65, (byte)0x79, (byte)0x42, (byte)0x61, (byte)0x6F, (byte)0x4D, (byte)0x61,
        (byte)0x74, (byte)0x50, (byte)0x69, (byte)0x6E, (byte)0x32, (byte)0x30, (byte)0x32, (byte)0x36 
    };
    
    private PIN systemSecret; 
    private boolean isSecretValidated;
    private PBKDF2 pbkdf2Engine;
    
    static final byte INS_VERIFY_PIN = (byte) 0x00;
    static final byte INS_CREATE_PIN = (byte) 0x01;
    static final byte INS_CREATE_INFO = (byte) 0x02;
    static final byte INS_UPDATE_PIN = (byte) 0x03;
    static final byte INS_UPDATE_INFO = (byte) 0x04;
    static final byte INS_GET_INFO = (byte) 0x05;
    static final byte INS_UPDATE_MONEY = (byte) 0x06;
    static final byte INS_GET_MONEY = (byte) 0x07;
    static final byte INS_RESET_PIN = (byte) 0x09;
    static final byte INS_VERIFY_SECRET = (byte) 0x0A;
    
    static final byte INS_UPDATE_AVATAR = (byte) 0x10;
    static final byte INS_GET_AVATAR = (byte) 0x11;
    static final byte INS_CREATE_SIGNATURE = (byte) 0x12;
    static final byte INS_SET_BACKEND_PUBLIC_KEY = (byte) 0x16;
    static final byte INS_GET_CARD_ID = (byte) 0x17;
    static final byte INS_GET_DB_ID = (byte) 0x18;
    
    static final byte INS_GET_INFO_NO = (byte) 0x23;
    static final byte INS_GET_PIN_TRIES = (byte) 0x24;
    static final byte INS_UPDATE_PIN_DIRECT = (byte) 0x25;
    static final byte INS_UPDATE_MONEY_DIRECT = (byte) 0x26;
    static final byte INS_CHECK_EMPTY = (byte) 0x27;

    public static final short MAX_SIZE_AVATAR = (short) 8192;
    public static final short ENCRYPTED_INFO_SIZE = (short) 48; 
    
    private PIN pin;
    private CardModel cardModel;

    private AESKey masterKeyObj;   
    private AESKey wrappingKeyObj; 
    private AESKey transportKeyObj; 
    private RandomData rng;

    private byte[] masterKeyRAM;              
    private byte[] encryptedMasterKey_User;  
    private byte[] encryptedMasterKey_Admin; 
    private byte[] encryptedPrivateKeyExp;

    private final Cipher cipherData; 
    private final Cipher cipherWrap; 

    private final byte[] temporary;    
    private final byte[] cipherBuffer; 
    private final byte[] iv;
    
    private RSAPublicKey cachedBackendPubKey = null;
    private Signature cachedSigVerify = null;
    private Signature signature;
    private final byte[] signatureBuffer;
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
    private final byte[] tempMessage;
    private final byte[] tempSignature;
    private final byte[] tempPinData;

    public HospitalCard() {
        pbkdf2Engine = new PBKDF2();
        rng = RandomData.getInstance(RandomData.ALG_SECURE_RANDOM);
        masterKeyObj = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, (short) 128, false);
        wrappingKeyObj = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, (short) 128, false);
        
        transportKeyObj = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, (short) 128, false);
        transportKeyObj.setKey(TRANSPORT_KEY_DATA, (short) 0);

        cipherData = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_CBC_NOPAD, false);
        cipherWrap = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_ECB_NOPAD, false);

        masterKeyRAM = JCSystem.makeTransientByteArray((short) 16, JCSystem.CLEAR_ON_RESET);
        encryptedMasterKey_User = new byte[16];
        encryptedMasterKey_Admin = new byte[16];
        encryptedPrivateKeyExp = new byte[128];

        temporary = JCSystem.makeTransientByteArray((short) 64, JCSystem.CLEAR_ON_RESET);
        cipherBuffer = JCSystem.makeTransientByteArray((short) 64, JCSystem.CLEAR_ON_RESET);
        
        iv = new byte[16];
        Util.arrayFillNonAtomic(iv, (short)0, (short)16, (byte)0x00);

        signature = Signature.getInstance(Signature.ALG_RSA_SHA_PKCS1, false);
        signatureBuffer = JCSystem.makeTransientByteArray((short) (KeyBuilder.LENGTH_RSA_1024 / 8), JCSystem.CLEAR_ON_RESET);
        tempMessage = JCSystem.makeTransientByteArray((short) 256, JCSystem.CLEAR_ON_RESET);
        tempSignature = JCSystem.makeTransientByteArray((short) 128, JCSystem.CLEAR_ON_RESET);
        tempPinData = JCSystem.makeTransientByteArray((short) 32, JCSystem.CLEAR_ON_RESET);
        
        isSecretValidated = false;
    }

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new HospitalCard().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
    }

    public void deselect() {
        if (pin != null) pin.logout();
        if (systemSecret != null) systemSecret.reset();
        isSecretValidated = false;
        clearSession();
    }

    public void process(APDU apdu) {
        if (selectingApplet()) {
            clearSession();
            return;
        }

        byte[] buf = apdu.getBuffer();
        switch (buf[ISO7816.OFFSET_INS]) {
            case INS_VERIFY_SECRET: verifySecret(apdu); break;
            case INS_CREATE_PIN: createPIN(apdu); break;
            case INS_VERIFY_PIN: verifyPIN(apdu); break;
            case INS_UPDATE_PIN: updatePIN(apdu); break;
            case INS_RESET_PIN: resetPIN(apdu); break;
            case INS_UPDATE_PIN_DIRECT: updatePINDirect(apdu); break;
            
            case INS_CREATE_INFO: createCard(apdu); break;
            case INS_GET_INFO: 
                if ((pin == null || !pin.isValidated()) && !isSecretValidated) {
                      ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
                }
                getCard(apdu); 
                break;
                
            case INS_UPDATE_INFO: updateInfo(apdu); break;
            case INS_UPDATE_MONEY: updateMoney(apdu); break;
            case INS_GET_MONEY: getMoney(apdu); break;
            case INS_UPDATE_MONEY_DIRECT: updateMoneyDirect(apdu); break;

            case INS_GET_AVATAR: getAvatar(apdu); break;
            case INS_UPDATE_AVATAR: updateAvatar(apdu); break;

            case INS_CREATE_SIGNATURE: createSignature(apdu); break;
            case INS_SET_BACKEND_PUBLIC_KEY: setBackendPublicKey(apdu); break;
            case INS_GET_CARD_ID: getCardId(apdu); break;
            case INS_GET_DB_ID: getDbId(apdu); break;
            
            case INS_GET_INFO_NO: getCardNO(apdu); break;
            case INS_GET_PIN_TRIES: getPinTries(apdu); break;
            case INS_CHECK_EMPTY: checkEmpty(apdu); break;

            default: ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }

    private short decryptTransportData(byte[] input, short inputOff, byte[] output, short outputOff) {
        cipherWrap.init(transportKeyObj, Cipher.MODE_DECRYPT);
        cipherWrap.doFinal(input, inputOff, (short)16, temporary, (short)0);
        short realLen = (short)(temporary[0] & 0xFF);
        if (realLen <= 0 || realLen > 15) ISOException.throwIt(ISO7816.SW_DATA_INVALID);
        Util.arrayCopy(temporary, (short)1, output, outputOff, realLen);
        return realLen;
    }

    private void checkEmpty(APDU apdu) {
        byte[] buf = apdu.getBuffer();
        buf[0] = (cardModel == null) ? (byte)0x01 : (byte)0x00;
        apdu.setOutgoingAndSend((short)0, (short)1);
    }

    private void verifySecret(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        short byteRead = apdu.setIncomingAndReceive(); 
        if (byteRead != 16) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        if (systemSecret == null) ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);

        short secretLen = decryptTransportData(buffer, ISO7816.OFFSET_CDATA, tempPinData, (short)0);
        if (systemSecret.match(tempPinData, (short)0, (byte)secretLen) == false) {
            ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
        }
        
        isSecretValidated = true;
        try {
            restoreMasterKeyFromSlot(systemSecret.getKEK(), encryptedMasterKey_Admin);
        } catch (Exception e) {
            isSecretValidated = false;
            ISOException.throwIt(ISO7816.SW_DATA_INVALID);
        }
    }

    private void createPIN(APDU apdu) {
        byte[] buf = apdu.getBuffer();
        short lenReceived = apdu.setIncomingAndReceive();
        if (lenReceived != 32) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

        short offset = ISO7816.OFFSET_CDATA;
        short pinLen = decryptTransportData(buf, offset, tempPinData, (short)0);
        offset += 16; 
        
        if (pinLen > 32 || pinLen < 0) ISOException.throwIt(ISO7816.SW_DATA_INVALID);
        pin = new PIN(tempPinData, (short)0, (short)pinLen, NUMBER_RETRY, pbkdf2Engine);
        
        short secretLen = decryptTransportData(buf, offset, tempPinData, (short)0);
        offset += 16;
        
        if (secretLen > 16 || secretLen <= 0) ISOException.throwIt(ISO7816.SW_DATA_INVALID);
        systemSecret = new PIN(tempPinData, (short)0, (short)secretLen, (byte)3, pbkdf2Engine);
        generateAndBackupMasterKey(pin.getKEK());
    }

    private void updatePINDirect(APDU apdu) throws ISOException {
        byte[] buf = apdu.getBuffer();
        short lc = apdu.setIncomingAndReceive();
        if (lc != 16) ISOException.throwIt(ISO7816.SW_DATA_INVALID);
        if (!masterKeyObj.isInitialized()) ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        
        short newPinLen = decryptTransportData(buf, ISO7816.OFFSET_CDATA, tempPinData, (short)0);
        if (pin.check(tempPinData, (short)0, (byte)newPinLen)) {
            ISOException.throwIt(SW_PIN_SAME_AS_OLD);
        }

        pin.update(tempPinData, (short)0, (byte)newPinLen);
        saveMasterKeyToSlot(pin.getKEK(), encryptedMasterKey_User);
    }

    private void generateAndBackupMasterKey(byte[] userKek) {
        rng.generateData(masterKeyRAM, (short)0, (short)16);
        masterKeyObj.setKey(masterKeyRAM, (short)0);
        saveMasterKeyToSlot(userKek, encryptedMasterKey_User);
        saveMasterKeyToSlot(systemSecret.getKEK(), encryptedMasterKey_Admin);
    }

    private void saveMasterKeyToSlot(byte[] keyBytes, byte[] destBuffer) {
        wrappingKeyObj.setKey(keyBytes, (short)0);
        cipherWrap.init(wrappingKeyObj, Cipher.MODE_ENCRYPT);
        cipherWrap.doFinal(masterKeyRAM, (short)0, (short)16, destBuffer, (short)0);
    }

    private void restoreMasterKeyFromSlot(byte[] keyBytes, byte[] srcBuffer) {
        wrappingKeyObj.setKey(keyBytes, (short)0);
        cipherWrap.init(wrappingKeyObj, Cipher.MODE_DECRYPT);
        cipherWrap.doFinal(srcBuffer, (short)0, (short)16, masterKeyRAM, (short)0);
        masterKeyObj.setKey(masterKeyRAM, (short)0);
    }

    private void clearSession() {
        Util.arrayFillNonAtomic(masterKeyRAM, (short)0, (short)16, (byte)0x00);
        masterKeyObj.clearKey();
    }

    private void encryptInfo(byte[] input, short inOff, short len, byte[] output, short outOff) {
        if (!masterKeyObj.isInitialized()) ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        if (len > 32) ISOException.throwIt(ISO7816.SW_DATA_INVALID);

        rng.generateData(iv, (short)0, (short)16);
        Util.arrayCopyNonAtomic(iv, (short)0, output, outOff, (short)16);

        Util.arrayFillNonAtomic(cipherBuffer, (short)0, (short)32, (byte)0x00);
        Util.arrayCopyNonAtomic(input, inOff, cipherBuffer, (short)0, len);
        
        cipherData.init(masterKeyObj, Cipher.MODE_ENCRYPT, iv, (short) 0, (short) 16);
        cipherData.doFinal(cipherBuffer, (short) 0, (short)32, output, (short)(outOff + 16));
    }

    private short decryptInfoField(byte[] input, byte[] output, short outOffset) {
        if (!masterKeyObj.isInitialized()) return 0;
        Util.arrayCopyNonAtomic(input, (short)0, iv, (short)0, (short)16);
        Util.arrayCopyNonAtomic(input, (short)16, cipherBuffer, (short)0, (short)32);
        
        cipherData.init(masterKeyObj, Cipher.MODE_DECRYPT, iv, (short) 0, (short) 16);
        cipherData.doFinal(cipherBuffer, (short) 0, (short)32, output, outOffset);
        
        short pointer = (short) (outOffset + 31);
        while (pointer >= outOffset && output[pointer] == (byte) 0x00) pointer--;
        if (pointer < outOffset) return 0;
        return (short) (pointer - outOffset + 1);
    }

    private void decryptECBBlock(byte[] input, short inOff, short len, byte[] output, short outOff) {
        if (!masterKeyObj.isInitialized()) return;
        cipherWrap.init(masterKeyObj, Cipher.MODE_DECRYPT); 
        cipherWrap.doFinal(input, inOff, len, output, outOff);
    }

    private void createCard(APDU apdu) throws ISOException {
        if (!masterKeyObj.isInitialized()) ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);

        byte[] buf = apdu.getBuffer();
        apdu.setIncomingAndReceive();
        JCSystem.beginTransaction();
        cardModel = new CardModel();
        
        byte offset = ISO7816.OFFSET_CDATA;
        short length;

        // 1. ID th
        length = (short) (buf[offset] & 0xFF);
        cardModel.setId(buf, (short)(offset + 1), length);
        offset += (short) (length + 1);

        // 2. ID Bnh nhân
        length = (short) (buf[offset] & 0xFF);
        encryptInfo(buf, (short)(offset + 1), length, temporary, (short)0);
        cardModel.setPatientId(temporary, (short) 0, ENCRYPTED_INFO_SIZE);
        offset += (short) (length + 1);

        // 3. CCCD
        length = (short) (buf[offset] & 0xFF);
        encryptInfo(buf, (short)(offset + 1), length, temporary, (short)0);
        cardModel.setCitizenId(temporary, (short) 0, ENCRYPTED_INFO_SIZE);
        offset += (short) (length + 1);

        // 4. H tên
        length = (short) (buf[offset] & 0xFF);
        encryptInfo(buf, (short)(offset + 1), length, temporary, (short)0);
        cardModel.setFullName(temporary, (short) 0, ENCRYPTED_INFO_SIZE);
        offset += (short) (length + 1);

        // 5. Gii tính
        length = (short) (buf[offset] & 0xFF);
        encryptInfo(buf, (short)(offset + 1), length, temporary, (short)0);
        cardModel.setGender(temporary, (short) 0, ENCRYPTED_INFO_SIZE);
        offset += (short) (length + 1);

        // 6. Ngày sinh
        length = (short) (buf[offset] & 0xFF);
        encryptInfo(buf, (short)(offset + 1), length, temporary, (short)0);
        cardModel.setDateOfBirth(temporary, (short) 0, ENCRYPTED_INFO_SIZE);
        offset += (short) (length + 1);

        // 7. Quê quán
        length = (short) (buf[offset] & 0xFF);
        encryptInfo(buf, (short)(offset + 1), length, temporary, (short)0);
        cardModel.setAddress(temporary, (short) 0, ENCRYPTED_INFO_SIZE);
        offset += (short) (length + 1);

        // 8. S d khi to bng 0
        Util.arrayFillNonAtomic(temporary, (short) 0, (short) 64, (byte) 0x00);
        temporary[0] = (byte) '0';
        encryptInfo(temporary, (short) 0, (short) 1, temporary, (short)0);
        cardModel.setMoney(temporary, (short) 0, ENCRYPTED_INFO_SIZE);

        JCSystem.commitTransaction();
        
        KeyPair keyPair = generateKeyPair();
        privateKey = (RSAPrivateKey) keyPair.getPrivate();
        publicKey = (RSAPublicKey) keyPair.getPublic();
        
        short expLen = privateKey.getExponent(tempMessage, (short)0);
        cipherWrap.init(masterKeyObj, Cipher.MODE_ENCRYPT);
        cipherWrap.doFinal(tempMessage, (short)0, expLen, encryptedPrivateKeyExp, (short)0);
        
        privateKey.clearKey();

        short pubKeyLen = serializePublicKey(publicKey, buf, (short) 0);
        apdu.setOutgoingAndSend((short) 0, pubKeyLen);
    }

    private void updateInfo(APDU apdu) throws ISOException {
        byte[] buf = apdu.getBuffer();
        short lc = apdu.setIncomingAndReceive();
        if (!checkAdminForCurrentCommandFromBuffer(buf, ISO7816.OFFSET_CDATA, lc)) {
            ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
        }
        applyInfoFromBuffer(buf, ISO7816.OFFSET_CDATA, lc);
    }

    private void applyInfoFromBuffer(byte[] buffer, short startOffset, short totalLen) {
        short offset = startOffset;
        short msgLen = Util.getShort(buffer, offset); offset += (short)(2 + msgLen);
        short sigLen = Util.getShort(buffer, offset); offset += (short)(2 + sigLen);
        short length;

        length = (short) (buffer[offset++] & 0xFF);
        encryptInfo(buffer, offset, length, temporary, (short)0);
        cardModel.setCitizenId(temporary, (short)0, ENCRYPTED_INFO_SIZE);
        offset += length;

        length = (short) (buffer[offset++] & 0xFF);
        encryptInfo(buffer, offset, length, temporary, (short)0);
        cardModel.setFullName(temporary, (short)0, ENCRYPTED_INFO_SIZE);
        offset += length;
        
        length = (short) (buffer[offset++] & 0xFF);
        encryptInfo(buffer, offset, length, temporary, (short)0);
        cardModel.setGender(temporary, (short)0, ENCRYPTED_INFO_SIZE);
        offset += length;

        length = (short) (buffer[offset++] & 0xFF);
        encryptInfo(buffer, offset, length, temporary, (short)0);
        cardModel.setDateOfBirth(temporary, (short)0, ENCRYPTED_INFO_SIZE);
        offset += length;
        
        length = (short) (buffer[offset++] & 0xFF);
        encryptInfo(buffer, offset, length, temporary, (short)0);
        cardModel.setAddress(temporary, (short)0, ENCRYPTED_INFO_SIZE);
    }

    private void updateMoney(APDU apdu) throws ISOException {
        byte[] buf = apdu.getBuffer();
        short lc = apdu.setIncomingAndReceive();
        if (!checkAdminForCurrentCommandFromBuffer(buf, ISO7816.OFFSET_CDATA, lc)) {
              ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
        }
        short offset = ISO7816.OFFSET_CDATA;
        short msgLen = Util.getShort(buf, offset); offset += (short)(2 + msgLen);
        short sigLen = Util.getShort(buf, offset); offset += (short)(2 + sigLen);
        short moneyLen = (short)(lc - (offset - ISO7816.OFFSET_CDATA));
        
        encryptInfo(buf, offset, moneyLen, temporary, (short)0);
        cardModel.setMoney(temporary, (short)0, ENCRYPTED_INFO_SIZE);
    }

    private void updateMoneyDirect(APDU apdu) throws ISOException {
        byte[] buf = apdu.getBuffer();
        short lc = apdu.setIncomingAndReceive();
        
        if (lc == 0) ISOException.throwIt(ISO7816.SW_DATA_INVALID);
        if (!masterKeyObj.isInitialized()) ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);

        encryptInfo(buf, ISO7816.OFFSET_CDATA, lc, temporary, (short)0);
        cardModel.setMoney(temporary, (short)0, ENCRYPTED_INFO_SIZE);
    }

    private void updateAvatar(APDU apdu) {
        if (cardModel == null) ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED);
        byte[] buf = apdu.getBuffer();
        short length = apdu.setIncomingAndReceive();
        short chunkOffset = Util.makeShort((byte)(buf[ISO7816.OFFSET_P1] & 0xFF), (byte)(buf[ISO7816.OFFSET_P2] & 0xFF));
        if (length == 0) return;

        short numBlocks = (short)((length + 15) / 16);
        short encryptedLength = (short)(numBlocks * 16);
        
        if ((short)(chunkOffset + encryptedLength) > MAX_SIZE_AVATAR) 
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

        byte[] avatarData = cardModel.getAvatar().getData();
        short srcPos = ISO7816.OFFSET_CDATA;
        short dstPos = chunkOffset;
        short remaining = length;
        
        cipherWrap.init(masterKeyObj, Cipher.MODE_ENCRYPT);
        for (short i = 0; i < numBlocks; i++) {
            Util.arrayFillNonAtomic(temporary, (short)0, (short)16, (byte)0x00);
            short bytesToCopy = (remaining > 16) ? (short)16 : remaining;
            Util.arrayCopy(buf, srcPos, temporary, (short)0, bytesToCopy);
            cipherWrap.doFinal(temporary, (short)0, (short)16, avatarData, dstPos);
            srcPos += bytesToCopy;
            dstPos += 16;
            remaining -= bytesToCopy;
        }

        short newEnd = (short)(chunkOffset + encryptedLength);
        if (newEnd > cardModel.getAvatar().getSize()) {
            cardModel.getAvatar().setSize(newEnd);
        }
    }

    private void getAvatar(APDU apdu) {
        if (cardModel == null || cardModel.getAvatar() == null) ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        
        byte[] encryptedData = cardModel.getAvatar().getData();
        short encryptedSize = cardModel.getAvatar().getSize();
        if (encryptedSize == 0) { apdu.setOutgoingAndSend((short)0, (short)0); return; }

        short numBlocks = (short)(encryptedSize / 16);
        if (numBlocks == 0) { apdu.setOutgoingAndSend((short)0, (short)0); return; }

        decryptECBBlock(encryptedData, (short)((numBlocks - 1) * 16), (short)16, temporary, (short)0);
        
        short paddingCount = 0;
        for (short i = 15; i >= 0; i--) {
            if (temporary[i] == 0) paddingCount++; else break;
        }
        short actualSize = (short)(encryptedSize - paddingCount);

        apdu.setOutgoing();
        apdu.setOutgoingLength(actualSize);
        
        byte[] buf = apdu.getBuffer();
        short srcOffset = 0;
        short totalSent = 0;
        short bufOffset = 0;

        for (short i = 0; i < numBlocks; i++) {
             decryptECBBlock(encryptedData, srcOffset, (short)16, temporary, (short)0);
             short remaining = (short)(actualSize - totalSent);
             short bytesToCopy = remaining > 16 ? (short)16 : remaining;
             
             Util.arrayCopy(temporary, (short)0, buf, bufOffset, bytesToCopy);
             
             bufOffset += bytesToCopy;
             totalSent += bytesToCopy;
             srcOffset += 16;
             
             if (bufOffset >= 200 || totalSent >= actualSize) {
                 apdu.sendBytesLong(buf, (short)0, bufOffset);
                 bufOffset = 0;
             }
             if (totalSent >= actualSize) break;
        }
    }

    private void getCard(APDU apdu) throws ISOException {
        if (cardModel == null) return;
        byte[] buf = apdu.getBuffer();
        byte offset = 0;
        
        buf[offset] = (byte)decryptInfoField(cardModel.getPatientId(), buf, (short)(offset + 1));
        offset += (short) (buf[offset] + 1);
        buf[offset] = (byte)decryptInfoField(cardModel.getCitizenId(), buf, (short)(offset + 1));
        offset += (short) (buf[offset] + 1);
        buf[offset] = (byte)decryptInfoField(cardModel.getFullName(), buf, (short)(offset + 1));
        offset += (short) (buf[offset] + 1);
        buf[offset] = (byte)decryptInfoField(cardModel.getGender(), buf, (short)(offset + 1));
        offset += (short) (buf[offset] + 1);
        buf[offset] = (byte)decryptInfoField(cardModel.getDateOfBirth(), buf, (short)(offset + 1));
        offset += (short) (buf[offset] + 1);
        buf[offset] = (byte)decryptInfoField(cardModel.getAddress(), buf, (short)(offset + 1));
        offset += (short) (buf[offset] + 1);
        buf[offset] = (byte)decryptInfoField(cardModel.getMoney(), buf, (short)(offset + 1));
        offset += (short) (buf[offset] + 1);

        apdu.setOutgoingAndSend((short) 0, offset);
    }

    private void getMoney(APDU apdu) throws ISOException {
        byte[] buf = apdu.getBuffer();
        short length = decryptInfoField(cardModel.getMoney(), buf, (short) 0);
        apdu.setOutgoingAndSend((short) 0, length);
    }

    private void verifyPIN(APDU apdu) {
        byte[] buf = apdu.getBuffer();
        short dataLen = apdu.setIncomingAndReceive();
        byte offset = ISO7816.OFFSET_CDATA;

        if (pin == null) ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED);
        if (dataLen != 16) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        
        short pinLen = decryptTransportData(buf, offset, tempPinData, (short)0);

        if (pin.match(tempPinData, (short)0, (byte)pinLen)) {
            try {
                restoreMasterKeyFromSlot(pin.getKEK(), encryptedMasterKey_User);
            } catch (Exception e) {
                ISOException.throwIt(ISO7816.SW_DATA_INVALID);
            }
            return;
        }
        buf[ISO7816.OFFSET_CDATA] = pin.getTriesRemaining();
        apdu.setOutgoingAndSend(ISO7816.OFFSET_CDATA, (short) 1);
        ISOException.throwIt(ISO7816.SW_WRONG_DATA);
    }

    private void updatePIN(APDU apdu) throws ISOException {
        if (!masterKeyObj.isInitialized()) ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        byte[] buf = apdu.getBuffer();
        apdu.setIncomingAndReceive();
        short lc = (short)(buf[ISO7816.OFFSET_LC] & 0xFF);
        if (!checkAdminForCurrentCommandFromBuffer(buf, ISO7816.OFFSET_CDATA, lc)) {
              ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
        }
        short offset = ISO7816.OFFSET_CDATA;
        short msgLen = Util.getShort(buf, offset); offset += (short)(2 + msgLen);
        short sigLen = Util.getShort(buf, offset); offset += (short)(2 + sigLen);
        
        short pinLen = decryptTransportData(buf, offset, tempPinData, (short)0);

        pin.update(tempPinData, (short)0, (byte)pinLen);
        saveMasterKeyToSlot(pin.getKEK(), encryptedMasterKey_User);
    }

    private void resetPIN(APDU apdu) throws ISOException {
        byte[] buf = apdu.getBuffer();
        short lc = apdu.setIncomingAndReceive();
        if (!checkAdminForCurrentCommandFromBuffer(buf, ISO7816.OFFSET_CDATA, lc)) {
            ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
        }
        pin.resetPin();
    }

    private void getPinTries(APDU apdu) {
        if(pin == null) ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED);
        byte[] buf = apdu.getBuffer();
        buf[0] = pin.getTriesRemaining();
        apdu.setOutgoingAndSend((short)0, (short)1);
    }

    private void getDbId(APDU apdu) {
        if (cardModel == null) ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED);
        byte[] buf = apdu.getBuffer();
        byte[] id = cardModel.getId();
        Util.arrayCopyNonAtomic(id, (short)0, buf, (short)0, (short)id.length);
        apdu.setOutgoingAndSend((short)0, (short)id.length);
    }

    private void getCardId(APDU apdu) {
        if (cardModel == null) ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED);
        byte[] buf = apdu.getBuffer();
        short len = decryptInfoField(cardModel.getPatientId(), buf, (short)0);
        apdu.setOutgoingAndSend((short)0, len);
    }

    private void setBackendPublicKey(APDU apdu) {
        byte[] buf = apdu.getBuffer();
        short len = apdu.setIncomingAndReceive();
        if (len > 200) ISOException.throwIt(ISO7816.SW_DATA_INVALID);
        cardModel.setBackendPublicKey(buf, ISO7816.OFFSET_CDATA, len);
        cachedBackendPubKey = null;
    }

    private void createSignature(APDU apdu) {
         byte[] buf = apdu.getBuffer();
         short len = apdu.setIncomingAndReceive();
         
         if (!masterKeyObj.isInitialized()) ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
         
         cipherWrap.init(masterKeyObj, Cipher.MODE_DECRYPT);
         cipherWrap.doFinal(encryptedPrivateKeyExp, (short)0, (short)128, tempMessage, (short)0);
         
         short modLen = publicKey.getModulus(tempMessage, (short)128); 
         
         privateKey.setModulus(tempMessage, (short)128, modLen);
         privateKey.setExponent(tempMessage, (short)0, (short)128);
         
         signature.init(privateKey, Signature.MODE_SIGN);
         short sigLen = signature.sign(buf, ISO7816.OFFSET_CDATA, len, signatureBuffer, (short)0);
         
         privateKey.clearKey();
         Util.arrayFillNonAtomic(tempMessage, (short)0, (short)256, (byte)0);
         
         apdu.setOutgoing();
         apdu.setOutgoingLength(sigLen);
         apdu.sendBytesLong(signatureBuffer, (short)0, sigLen);
    }

    private void getCardNO(APDU apdu) {
        if (cardModel == null) ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        byte[] buf = apdu.getBuffer();
        short offset = 0;
        offset = copyAttr(cardModel.getPatientId(), buf, offset);
        offset = copyAttr(cardModel.getFullName(), buf, offset);
        offset = copyAttr(cardModel.getDateOfBirth(), buf, offset);
        apdu.setOutgoingAndSend((short)0, offset);
    }

    private short copyAttr(byte[] data, byte[] buf, short offset) {
        if (data == null) { buf[offset++] = 0; return offset; }
        short len = decryptInfoField(data, buf, (short)(offset + 1));
        buf[offset] = (byte)len;
        return (short)(offset + 1 + len);
    }

    private boolean checkAdminForCurrentCommandFromBuffer(byte[] buf, short startOffset, short len) {
        short offset = startOffset;
        if ((short)(offset + 2 - startOffset) > len) return false;
        short messageLen = Util.getShort(buf, offset); 
        offset += 2;
        
        if(messageLen <= 0 || messageLen > 256 || (short)(offset + messageLen - startOffset) > len) 
            return false;
        
        Util.arrayFillNonAtomic(tempMessage, (short)0, messageLen, (byte)0);
        Util.arrayCopy(buf, offset, tempMessage, (short)0, messageLen);
        offset += messageLen;
        
        if ((short)(offset + 2 - startOffset) > len) return false;
        short sigLen = Util.getShort(buf, offset); 
        offset += 2;
        
        if(sigLen <= 0 || sigLen > 128 || (short)(offset + sigLen - startOffset) > len) 
            return false;
        
        Util.arrayFillNonAtomic(tempSignature, (short)0, sigLen, (byte)0);
        Util.arrayCopy(buf, offset, tempSignature, (short)0, sigLen);
        
        return verifySignatureFromBE(tempMessage, messageLen, tempSignature, sigLen) 
               && isAdminRole(tempMessage, messageLen);
    }

    private boolean verifySignatureFromBE(byte[] message, short messageLen, byte[] signatureData, short sigLen) {
        try {
            if (cachedBackendPubKey == null) {
                if (cardModel == null) return false;
                byte[] pubKeyBytes = cardModel.getBackendPublicKey();
                short pos = 0;
                
                short modLen = (short) (pubKeyBytes[pos++] & 0xFF);
                cachedBackendPubKey = (RSAPublicKey) KeyBuilder.buildKey(
                    KeyBuilder.TYPE_RSA_PUBLIC, 
                    KeyBuilder.LENGTH_RSA_512, 
                    false
                );
                cachedBackendPubKey.setModulus(pubKeyBytes, pos, modLen);
                pos += modLen;
                
                short expLen = (short) (pubKeyBytes[pos++] & 0xFF);
                cachedBackendPubKey.setExponent(pubKeyBytes, pos, expLen);
                
                cachedSigVerify = Signature.getInstance(Signature.ALG_RSA_SHA_PKCS1, false);
            }
            
            cachedSigVerify.init(cachedBackendPubKey, Signature.MODE_VERIFY);
            return cachedSigVerify.verify(message, (short)0, messageLen, signatureData, (short)0, sigLen);
            
        } catch (Exception e) { 
            return false; 
        }
    }

    private boolean isAdminRole(byte[] message, short messageLen) {
        return messageLen >= 1 && message[(short)(messageLen - 1)] == ROLE_ADMIN;
    }

    public KeyPair generateKeyPair() {
        KeyPair keyPair = new KeyPair(KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_1024);
        keyPair.genKeyPair(); 
        return keyPair;
    }

    public short serializePublicKey(RSAPublicKey key, byte[] buffer, short offset) {
        short exponentLength = key.getExponent(buffer, (short) (offset + 2));
        short modulusLength = key.getModulus(buffer, (short) (offset + 2 + exponentLength + 2));
        
        Util.setShort(buffer, offset, exponentLength);
        Util.setShort(buffer, (short) (offset + 2 + exponentLength), modulusLength);
        
        return (short) (4 + exponentLength + modulusLength);
    }
}