package com.photoalbum.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.*;

@Service
public class EncryptionService {

    @Autowired
    private SecretKey fileEncryptionKey;

    public byte[] encryptFile(byte[] fileData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, fileEncryptionKey);
        return cipher.doFinal(fileData);
    }

    public byte[] decryptFile(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, fileEncryptionKey);
        return cipher.doFinal(encryptedData);
    }
} 