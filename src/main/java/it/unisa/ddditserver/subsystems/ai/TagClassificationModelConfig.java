package it.unisa.ddditserver.subsystems.ai;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class TagClassificationModelConfig {
    @Value("${MODELS_FOLDER_PATH}")
    private String modelsFolderPath;

    @Value("${FROM_EMAIL}")
    private String fromEmail;

    @Value("${TO_EMAIL}")
    private String toEmail;

    @Value("${APP_PASSWORD}")
    private String appPassword;
}
