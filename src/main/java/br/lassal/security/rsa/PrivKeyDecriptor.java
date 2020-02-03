package br.lassal.security.rsa;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Base64;

public class PrivKeyDecriptor {

    private PrivateKey privateKey;

    public PrivKeyDecriptor(File keyStore, String keyEntryAlias){
        try {
            this.loadPrivateKey(keyStore, keyEntryAlias);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Carrega a chave privada a partir de uma keystore sem proteção de senha
     * @param keyStore
     * @param keyAlias
     * @throws KeyStoreException
     * @throws IOException
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     */
    private void loadPrivateKey(File keyStore, String keyAlias) throws KeyStoreException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream(keyStore), null);

        this.privateKey = (PrivateKey)ks.getKey(keyAlias ,null);
    }

    /***
     * Descriptografa a mensagem utilizando a chave privada informada na criação do PrivKeyDecriptor.
     * A chave privada precisa ser a chave privada da chave pública que criptografou a mensagem.
     * @param cryptMessage
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String decript(String cryptMessage) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, this.privateKey);

        cipher.update(Base64.getDecoder().decode(cryptMessage));

        return new String(cipher.doFinal());
    }
}
