package com.grupo2_2dam.tpv_software;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class HashContraseña {

    // Recomendado por internet para PBKDF2
    private static final int ITERATIONS = 100000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    public static String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {

        // Generar una salt aleatoria de 16 bytes
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        // Para que se implemente el PBKDF2
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = factory.generateSecret(spec).getEncoded();

        // Codificar en Base64 para almacenar el texto
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashBase64 = Base64.getEncoder().encodeToString(hash);

        return ITERATIONS + ":" + saltBase64 + ":" + hashBase64;
    }

    public static boolean verifyPassword(String password, String storedHash)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        // Separar los componentes del hash almacenado
        String[] parts = storedHash.split(":");
        if (parts.length != 3) {
            return false;
        }

        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] originalHash = Base64.getDecoder().decode(parts[2]);

        // Calcular el hash de la contraseña proporcionada con los mismos parámetros
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] newHash = factory.generateSecret(spec).getEncoded();

        // Comparar los hashes de forma segura (tiempo constante)
        return constantTimeEquals(originalHash, newHash);
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}