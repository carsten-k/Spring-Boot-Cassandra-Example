package de.corschdi.cassandra.repository;

import de.corschdi.cassandra.model.entity.ShoppingCart;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, String> {

    Optional<ShoppingCart> findByUuid(UUID uuid);

}
