package aula2603.repository;

import java.util.regex.Pattern;

public class ValidCrm {

    // Regex para validar CRM no formato CRM-XXXXX (até 5 dígitos numéricos)
    private static final Pattern CRM_PATTERN = Pattern.compile("CRM-\\d{1,5}");

    /**
     * Valida se o CRM informado está no formato correto.
     *
     * @param crm O CRM a ser validado
     * @return true se válido, false caso contrário
     */
    public static boolean isValid(String crm) {
        if (crm == null) {
            return false;
        }
        return CRM_PATTERN.matcher(crm).matches();
    }

    // Método principal para teste rápido
    public static void main(String[] args) {
        String[] testes = {"CRM-1", "CRM-12", "CRM-123", "CRM-1234", "CRM-12345",
                "CRM-123456", "CRM-ABC", "crm-123", "CRM123"};

        for (String crm : testes) {
            System.out.println(crm + " -> " + (isValid(crm) ? "Válido" : "Inválido"));
        }
    }
}
