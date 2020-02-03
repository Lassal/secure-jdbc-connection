package br.lassal.security.rsa;

import org.bouncycastle.util.io.pem.PemReader;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Classe demonstração de como criptografar dados utilizando chave pública.
 * IMPORTANTE: este processo é demorado, chega a levar 2 segundos para criptografar
 * e descriptograr durantes os teste locais
 */
public class PubKeyCriptor {


    private PublicKey publicKey;

    public PubKeyCriptor(String publicKeyPath){
        try {
            this.publicKey = this.loadPublicKey(publicKeyPath);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * Carrega a chave publica especificada na criação do PubKeyCriptor
     * Esta é uma chave pública qualquer que tenha sido passado pelo mantenedor da chave privada
     * @param publicKeyPath
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private PublicKey loadPublicKey(String publicKeyPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        FileReader file = new FileReader(publicKeyPath);
        PemReader reader = new PemReader(file);
        X509EncodedKeySpec caKeySpec = new X509EncodedKeySpec(reader.readPemObject().getContent());
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pubKey = kf.generatePublic(caKeySpec);

        return pubKey;
    }

    /**
     * Recebe uma mensagem no formato string e criptografa esta mensagem utilizando a chave pública informada para o
     * PubKeyCriptor.
     * Esta mensagem só poderá ser descriptografada pela chave privada correspondente a esta chave pública.
     *
     * @param data Mensagem a ser criptografada
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public String encryptString(String data) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);

        cipher.update(data.getBytes());

        return Base64.getEncoder().encodeToString(cipher.doFinal());
    }
}
