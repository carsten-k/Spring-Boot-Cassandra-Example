package de.corschdi.cassandra.repository;

import de.corschdi.cassandra.model.entity.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends CrudRepository<Item, String> {

    List<Item> findAllByCartUuid(UUID cartUuid);

    Optional<Item> findByCartUuidAndUuid(UUID cartUuid, UUID itemUuid);

}
