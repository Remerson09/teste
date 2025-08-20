package aula2603.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Classe utilitária para gerar senhas criptografadas
 * @author fagno
 */
public class GeradorSenha {

    public static void main(String[] args) {
        // Solicitando a codificação para 123
        System.out.println(new BCryptPasswordEncoder().encode("123"));

        // Gerando senhas para diferentes usuários
        System.out.println("Senha para admin: " + new BCryptPasswordEncoder().encode("admin"));
        System.out.println("Senha para user: " + new BCryptPasswordEncoder().encode("user"));

    }
}