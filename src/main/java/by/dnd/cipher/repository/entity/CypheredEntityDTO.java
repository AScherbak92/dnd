package by.dnd.cipher.repository.entity;

public class CypheredEntityDTO {
    private String entityKey;
    private String entityValue;

    public String getEntityKey() {
        return entityKey;
    }

    public CypheredEntityDTO() {
    }

    public CypheredEntityDTO(String entityKey, String entityValue) {
        this.entityKey = entityKey;
        this.entityValue = entityValue;
    }

    public void setEntityKey(String entityKey) {
        this.entityKey = entityKey;
    }

    public String getEntityValue() {
        return entityValue;
    }

    public void setEntityValue(String entityValue) {
        this.entityValue = entityValue;
    }
}
