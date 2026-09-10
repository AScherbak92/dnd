package by.dnd.cipher.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminPanelService {
    @Value("${jwt.access.secret}")
    private String accessKey;

    public boolean keyCheck(String key) {
        if (key.equals(accessKey)) {
            return true;
        }

        return false;
    }
}
