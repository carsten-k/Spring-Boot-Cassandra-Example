package de.corschdi.cassandra.controller;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import de.corschdi.cassandra.exception.ResourceGoneException;
import de.corschdi.cassandra.model.entity.ShoppingCart;
import de.corschdi.cassandra.repository.ShoppingCartRepository;
import de.corschdi.cassandra.model.enums.CartStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.UpdateOptions;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartRepository repository;

    @Autowired
    private CassandraOperations cassandraTemplate;

    @PostMapping
    public ResponseEntity<ShoppingCart> createCart(@RequestBody ShoppingCart cart) {
        cart.setUuid(Uuids.timeBased());
        cart.setChangedOn(LocalDateTime.now());
        cart.setStatus(CartStatus.OPEN);

        return ResponseEntity.ok(repository.save(cart));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ShoppingCart> readCart(@PathVariable("uuid") UUID cartUuid) {
        return ResponseEntity.of(repository.findByUuid(cartUuid));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ShoppingCart> updateCart(@PathVariable("uuid") UUID cartUuid, @RequestBody ShoppingCart cart) {
        var cartOptional = repository.findByUuid(cartUuid);

        return cartOptional.map(cartDb -> {
            if (cartDb.getStatus() == CartStatus.ORDERED) {
                throw new ResourceGoneException();
            }

            cartDb.setStatus(cart.getStatus());
            cartDb.setOwner(cart.getOwner());
            cartDb.setChangedOn(LocalDateTime.now());

            return ResponseEntity.ok(repository.save(cartDb));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{uuid}/lightweight")
    public ResponseEntity<ShoppingCart> updateCartWithLightweightTransaction(@PathVariable("uuid") UUID cartUuid, @RequestBody ShoppingCart cart) {
        var cartOptional = repository.findByUuid(cartUuid);

        return cartOptional.map(cartDb -> {
            cartDb.setStatus(cart.getStatus());
            cartDb.setOwner(cart.getOwner());
            cartDb.setChangedOn(LocalDateTime.now());

            var updateResult = cassandraTemplate.update(cart, UpdateOptions.builder().ifCondition(
                    Criteria.where("status").is(CartStatus.OPEN)
            ).build());

            if (updateResult.wasApplied()) {
                return ResponseEntity.ok(updateResult.getEntity());
            } else {
                throw new ResourceGoneException();
            }
        }).orElse(ResponseEntity.notFound().build());
    }

}
