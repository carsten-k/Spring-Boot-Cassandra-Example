package de.corschdi.cassandra;

import de.corschdi.cassandra.model.entity.ShoppingCart;
import de.corschdi.cassandra.model.enums.CartStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class MultipleSubmitTest {

    private final RestClient restClient = new RestClient();

    private UUID cartUuid;

    @BeforeEach
    public void setup() {
        var cart = new ShoppingCart();
        cart.setOwner("Setup");

        var createdCart = restClient.executeRequest(HttpMethod.POST, "/cart", cart, ShoppingCart.class).getBody();
        assert createdCart != null;

        System.out.println("Created shopping cart with UUID " + createdCart.getUuid());

        this.cartUuid = createdCart.getUuid();
    }

    @Test
    public void runTest() throws InterruptedException {
        runTest("/cart/" + cartUuid);
    }

    @Test
    public void runTestWithLightWeightTransaction() throws InterruptedException {
        runTest("/cart/" + cartUuid + "/lightweight");
    }

    public void runTest(String path) throws InterruptedException {
        var threadPool = Executors.newCachedThreadPool();
        var callables = new ArrayList<Callable<String>>();

        for (var i = 1; i <= 10; i++) {
            final String submitterName = "Submitter " + i;

            var cart = new ShoppingCart();
            cart.setUuid(cartUuid);
            cart.setOwner(submitterName);
            cart.setStatus(CartStatus.ORDERED);

            callables.add(() -> {
                try {
                    var response = restClient.executeRequest(HttpMethod.PUT, path, cart, ShoppingCart.class);

                    return submitterName + " succeeded with HTTP " + response.getStatusCodeValue() + " and body " + response.getBody();
                } catch (HttpClientErrorException | HttpServerErrorException ex) {
                    return submitterName + " failed with HTTP " + ex.getRawStatusCode();
                }
            });
        }

        var results = threadPool.invokeAll(callables).stream();
        results.forEach(result -> {
            try {
                System.out.println(result.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

}
