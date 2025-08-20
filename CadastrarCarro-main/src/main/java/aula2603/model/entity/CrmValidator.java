package aula2603.model.entity;

import aula2603.repository.ValidCrm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Validador customizado para o CRM.
 * Agora aceita apenas números (ex: 12345).
 */
public class CrmValidator implements ConstraintValidator<ValidCrm, String> {

    // Padrão regex para validar apenas números com 5 dígitos
    private static final String CRM_PATTERN = "^\\d{5}$";

    private static final Pattern pattern = Pattern.compile(CRM_PATTERN);

    @Override
    public void initialize(ValidCrm constraintAnnotation) {
        // Inicialização se necessário
    }

    @Override
    public boolean isValid(String crm, ConstraintValidatorContext context) {
        if (crm == null) {
            return true; // @NotNull/@NotBlank vai cuidar disso
        }

        crm = crm.trim();
        if (crm.isEmpty()) {
            return true; // @NotBlank cuida
        }

        boolean isValid = pattern.matcher(crm).matches();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Digite apenas números (ex: 12345)")
                    .addConstraintViolation();
        }

        return isValid;
    }

    /**
     * Método utilitário para validar CRM programaticamente.
     */
    public static boolean isValidCrmFormat(String crm) {
        if (crm == null || crm.trim().isEmpty()) {
            return false;
        }
        return pattern.matcher(crm.trim()).matches();
    }

    /**
     * Método utilitário para retornar o CRM já validado.
     */
    public static String extractCrmNumber(String crm) {
        if (isValidCrmFormat(crm)) {
            return crm.trim();
        }
        return null;
    }
}
