package by.dnd.cipher.repository.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cyphered_entities")
public class CypheredEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false, unique = true, length = 100)
    public String entityKey;
    @Column(nullable = false, length = 5000)
    public String entityValue;

    public CypheredEntity() {
    }

    public String getEntityKey() {
        return entityKey;
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
