package de.corschdi.cassandra.controller;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import de.corschdi.cassandra.exception.ResourceGoneException;
import de.corschdi.cassandra.repository.ItemRepository;
import de.corschdi.cassandra.model.entity.Item;
import de.corschdi.cassandra.model.entity.ShoppingCart;
import de.corschdi.cassandra.model.enums.CartStatus;
import de.corschdi.cassandra.model.enums.ItemStatus;
import de.corschdi.cassandra.repository.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/cart/{cartUuid}/items")
public class ItemController {

    @Autowired
    private ShoppingCartRepository cartRepository;

    @Autowired
    private ItemRepository repository;

    @PostMapping
    public ResponseEntity<Item> createItem(@PathVariable("cartUuid") UUID cartUuid, @RequestBody Item item) {
        return getShoppingCart(cartUuid).map(cart -> {
            if (cart.getStatus() == CartStatus.ORDERED) {
                throw new ResourceGoneException();
            }

            item.setCartUuid(cartUuid);
            item.setUuid(Uuids.timeBased());
            item.setStatus(ItemStatus.TRANSPORTING);
            item.setChangedOn(LocalDateTime.now());

            return ResponseEntity.ok(repository.save(item));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Item>> readCartItems(@PathVariable("cartUuid") UUID cartUuid) {
        return getShoppingCart(cartUuid).map(cart -> {
            if (cart.getStatus() == CartStatus.ORDERED) {
                throw new ResourceGoneException();
            }

            return ResponseEntity.ok(repository.findAllByCartUuid(cartUuid));
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{itemUuid}")
    public ResponseEntity<Item> readCartItem(@PathVariable("cartUuid") UUID cartUuid, @PathVariable("itemUuid") UUID itemUuid) {
        return getShoppingCart(cartUuid).map(cart -> {
            if (cart.getStatus() == CartStatus.ORDERED) {
                throw new ResourceGoneException();
            }

            return ResponseEntity.of(repository.findByCartUuidAndUuid(cartUuid, itemUuid));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{itemUuid}")
    public ResponseEntity<Item> updateCartItem(@PathVariable("cartUuid") UUID cartUuid, @PathVariable("itemUuid") UUID itemUuid, @RequestBody Item item) {
        return getShoppingCart(cartUuid).map(cart -> {
            if (cart.getStatus() == CartStatus.ORDERED) {
                throw new ResourceGoneException();
            }

            var itemOptional = repository.findByCartUuidAndUuid(cartUuid, itemUuid);

            return itemOptional.map(itemDb -> {
                itemDb.setStatus(item.getStatus());
                itemDb.setDescription(item.getDescription());
                itemDb.setChangedOn(LocalDateTime.now());
                return ResponseEntity.ok(repository.save(itemDb));
            }).orElse(ResponseEntity.notFound().build());
        }).orElse(ResponseEntity.notFound().build());
    }

    private Optional<ShoppingCart> getShoppingCart(UUID cartUuid) {
        return cartRepository.findByUuid(cartUuid);
    }

}
