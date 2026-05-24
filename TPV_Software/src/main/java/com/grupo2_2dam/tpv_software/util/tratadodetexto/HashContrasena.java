package com.grupo2_2dam.tpv_software.util.tratadodetexto;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class HashContrasena {

    // Recomendado por internet para PBKDF2
    private static final int ITERATIONS = 100000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * Hash de la contraseña utilizando PBKDF2 con una salt aleatoria y un número de iteraciones
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
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

    /**
     * Verificar la contraseña comparando el hash almacenado con el hash de la contraseña proporcionada, utilizando los mismos parámetros de salt e iteraciones
     * @param password
     * @param storedHash
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
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

    /**
     * Contante timeEquals para evitar ataques de timing, comparando byte a byte sin salir antes de comparar todo el array
     * @param a
     * @param b
     * @return
     */
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

/*
 * Funcionamiento de la clase util/HashContraseña
 */
//public class Main {
//    public static void main(String[] args) {
//        try {
//            // 1. Registras un usuario: generar un hash y guarda en DB
//            String password = "contraseña123";
//            String hash = PasswordHasher.hashPassword(password);
//            System.out.println("Hash que se guarda en DB: " + hash);
//
//            // 2. Validar login: comparar contraseña con el hash
//            String passwordIngresada = "contraseña123";
//            boolean esCorrecta = PasswordHasher.verifyPassword(passwordIngresada, hash);
//            System.out.println("Contraseña correcta " + esCorrecta);
//
//            // Prueba contraseña incorrecta
//            String passwordIncorrecta = "Contrasena1234";
//            boolean esIncorrecta = PasswordHasher.verifyPassword(passwordIncorrecta, hash);
//            System.out.println("Contraseña incorrecta " + esIncorrecta);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}