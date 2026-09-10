package by.dnd.cipher.repository.entity;

import org.springframework.data.jpa.repository.JpaRepository;


public interface iCypheredEntityRepository extends JpaRepository<CypheredEntity, Integer> {
    boolean existsByEntityKey(String entityKey);
}
