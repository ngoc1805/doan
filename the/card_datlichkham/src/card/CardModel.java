package card; 

import javacard.framework.Util; 
import javacard.framework.ISOException; 
import javacard.framework.ISO7816; 

public class CardModel {
    // 16 bytes (IV ngu nhiên) + 32 bytes (D liu) = 48 bytes
    private static final short ENCRYPTED_SIZE = 48; 
    
    private final byte[] id;           // ID th (lu nguyên bn)
    private final byte[] patientId;    // ID bnh nhân
    private final byte[] citizenId;    // Cn cc công dân
    private final byte[] fullName;     // H tên
    private final byte[] gender;       // Gii tính
    private final byte[] dateOfBirth;  // Ngày sinh
    private final byte[] address;      // Quê quán
    private final byte[] money;        // S d
    
    private final Avatar avatar; 
    private final byte[] backendPublicKey; 
    private short backendPublicKeyLen = 0;
    
    public CardModel() { 
        id = new byte[32]; 
        
        // Khi to b nh cho các trng mã hóa (48 bytes)
        patientId = new byte[ENCRYPTED_SIZE]; 
        citizenId = new byte[ENCRYPTED_SIZE]; 
        fullName = new byte[ENCRYPTED_SIZE]; 
        gender = new byte[ENCRYPTED_SIZE]; 
        dateOfBirth = new byte[ENCRYPTED_SIZE]; 
        address = new byte[ENCRYPTED_SIZE]; 
        money = new byte[ENCRYPTED_SIZE]; 
        
        avatar = new Avatar((short)8192); 
        backendPublicKey = new byte[200]; 
    } 

    public void setBackendPublicKey(byte[] buffer, short offset, short length){ 
        if (length > backendPublicKey.length) { 
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); 
        } 
        Util.arrayCopy(buffer, offset, backendPublicKey, (short)0, length); 
        backendPublicKeyLen = length; 
    } 

    public byte[] getBackendPublicKey(){ 
        return backendPublicKey; 
    } 

    public short getBackendPublicKeyLen() { 
        return backendPublicKeyLen; 
    } 

    public void setId(byte[] buffer, short offset, short length){ 
        if (length > id.length) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); 
        Util.arrayCopy(buffer, offset, id, (short) 0, length); 
    } 

    public byte[] getId(){ 
        return id; 
    } 

    public void setPatientId(byte[] buffer, short offset, short length){ 
        if (length > ENCRYPTED_SIZE) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); 
        Util.arrayCopy(buffer, offset, patientId, (short) 0, length); 
    } 
    public byte[] getPatientId(){ return patientId; } 

    public void setCitizenId(byte[] buffer, short offset, short length){ 
        if (length > ENCRYPTED_SIZE) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); 
        Util.arrayCopy(buffer, offset, citizenId, (short) 0, length); 
    } 
    public byte[] getCitizenId(){ return citizenId; } 

    public void setFullName(byte[] buffer, short offset, short length) { 
        if (length > ENCRYPTED_SIZE) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); 
        Util.arrayCopy(buffer, offset, fullName, (short) 0, length); 
    } 
    public byte[] getFullName() { return fullName; } 

    public void setGender(byte[] buffer, short offset, short length) { 
        if (length > ENCRYPTED_SIZE) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); 
        Util.arrayCopy(buffer, offset, gender, (short) 0, length); 
    } 
    public byte[] getGender() { return gender; } 

    public void setDateOfBirth(byte[] buffer, short offset, short length) { 
        if (length > ENCRYPTED_SIZE) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); 
        Util.arrayCopy(buffer, offset, dateOfBirth, (short) 0, length); 
    } 
    public byte[] getDateOfBirth() { return dateOfBirth; } 

    public void setAddress(byte[] buffer, short offset, short length) { 
        if (length > ENCRYPTED_SIZE) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); 
        Util.arrayCopy(buffer, offset, address, (short) 0, length); 
    } 
    public byte[] getAddress() { return address; } 

    public void setMoney(byte[] buffer, short offset, short length) { 
        if (length > ENCRYPTED_SIZE) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH); 
        Util.arrayCopy(buffer, offset, money, (short) 0, length); 
    } 
    public byte[] getMoney() { return money; } 

    public Avatar getAvatar(){ 
        return avatar; 
    } 
}