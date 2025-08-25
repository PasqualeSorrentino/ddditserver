package it.unisa.ddditserver.subsystems.ai;

import it.unisa.ddditserver.subsystems.versioning.dto.version.VersionDTO;
import java.util.ArrayList;

public class MockAI {
    public static ArrayList<String> mockClassification(VersionDTO versionDTO) {
        return new ArrayList<>();
    }
}
