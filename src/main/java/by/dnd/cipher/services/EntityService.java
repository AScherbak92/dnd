package by.dnd.cipher.services;

import by.dnd.cipher.repository.entity.CypheredEntity;
import by.dnd.cipher.repository.entity.CypheredEntityDTO;
import by.dnd.cipher.repository.entity.iCypheredEntityRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class EntityService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final iCypheredEntityRepository cypheredEntityRepository;
    private final SessionFactory sessionFactory;

    public EntityService(iCypheredEntityRepository cypheredEntityRepository, SessionFactory sessionFactory) {
        this.cypheredEntityRepository = cypheredEntityRepository;
        this.sessionFactory = sessionFactory;
    }

    //Method for creating a CypheredEntity and saving it to DB
    public boolean createCypheredEntity(CypheredEntityDTO dto){
        try {
            CypheredEntity entity = new CypheredEntity();

            if (entityExistsCheck(dto.getEntityKey())){
                throw new EntityExistsException("Entity with this key already exists");
            }
            entity.setEntityKey(dto.getEntityKey());
            entity.setEntityValue(dto.getEntityValue());

            cypheredEntityRepository.save(entity);
            LOGGER.info("Новая запись была добавлена. Ключ - " + dto.getEntityKey());

            return true;
        } catch (EntityExistsException e) {
            LOGGER.error("Error while creating a CypheredEntity: " + e.getMessage());
            return false;
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurs during cyphering: " + e.getMessage());
            return false;
        }
    }

    //Method for searching a CypheredEntity in DB
    public CypheredEntityDTO searchCypheredEntity(CypheredEntityDTO dto) {
        String hql;

        try (Session session = sessionFactory.openSession()){
            hql = "FROM CypheredEntity v WHERE v.entityKey = :entityKey";

            //Query creating
            Query<CypheredEntity> query = session.createQuery(hql, CypheredEntity.class);
            query.setParameter("entityKey", dto.getEntityKey());
            //Executing query and creating an instance of CypheredEntity
            CypheredEntity entity = query.getSingleResult();

            //Checking if such entity exists in DB
            if(entity == null) {
                throw new EntityNotFoundException("Failed to find an entity with key: " + dto.getEntityKey());
            }

            //Setting a new EntityValue to our dto and return back in controller
            dto.setEntityValue(entity.getEntityValue());
            return dto;

        } catch (EntityNotFoundException e) {
            LOGGER.error("Error while trying to find a CypheredEntity in DB: " + e.getMessage());
            return dto;
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurs during research: " + e.getMessage());
            return dto;
        }
    }

    //Method for getting all entities from DB
    public List<CypheredEntityDTO> findAllEntities() {
        List<CypheredEntityDTO> entities;
        String hql;

        try (Session session = sessionFactory.openSession()){
            hql = "SELECT new by.dnd.cipher.repository.entity.CypheredEntityDTO(v.entityKey, v.entityValue) FROM CypheredEntity v";

            //Query creating
            Query<CypheredEntityDTO> query = session.createQuery(hql, CypheredEntityDTO.class);
            entities = query.getResultList();
        } catch (Exception e) {
            LOGGER.error("Unexpected error occurs during getting all records: " + e.getMessage());
            return new LinkedList<>();
        }

        return entities;
    }

    //Method for deleting a record from DB
    public void deleteEntity(String entityKey){
        String hql;
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            if (!entityExistsCheck(entityKey)) {
                throw new EntityNotFoundException();
            }

            transaction = session.beginTransaction();

            hql = "DELETE FROM CypheredEntity v WHERE v.entityKey = :entityKey";

            Query<?> query = session.createQuery(hql);
            query.setParameter("entityKey", entityKey);

            int deletedCount = query.executeUpdate();

            transaction.commit();

            if (deletedCount > 0) {
                LOGGER.info("Successfully deleted record with key - {}", entityKey);
            } else {
                LOGGER.info("No records found with key - {}", entityKey);
            }

        } catch (EntityNotFoundException e) {
            LOGGER.error("Entity with key {} wasn't found", entityKey);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            LOGGER.error("Error while deleting a record with key - {}", entityKey);
        }
    }

    //Method for checking if a CypheredEntity exists in DB
    public boolean entityExistsCheck(String entityKey){
        return cypheredEntityRepository.existsByEntityKey(entityKey);
    }
}
