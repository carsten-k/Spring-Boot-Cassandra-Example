package de.corschdi.cassandra.config;

import com.datastax.oss.driver.internal.core.type.codec.extras.enums.EnumNameCodec;
import de.corschdi.cassandra.model.enums.CartStatus;
import de.corschdi.cassandra.model.enums.ItemStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.SessionBuilderConfigurer;

import java.nio.file.Paths;

@Configuration
public class CassandraConfiguration extends AbstractCassandraConfiguration {

    @Value("${cassandra.keyspace}")
    private String keyspace;

    @Value("${cassandra.auth.bundle-location}")
    private String bundleLocation;

    @Value("${cassandra.auth.client-id}")
    private String clientId;

    @Value("${cassandra.auth.client-secret}")
    private String clientSecret;

    @Override
    protected String getKeyspaceName() {
        return keyspace;
    }

    @Override
    protected SessionBuilderConfigurer getSessionBuilderConfigurer() {
        return sessionBuilder -> sessionBuilder
                .withCloudSecureConnectBundle(Paths.get(bundleLocation))
                .withAuthCredentials(clientId, clientSecret)
                .addTypeCodecs(new EnumNameCodec<>(CartStatus.class), new EnumNameCodec<>(ItemStatus.class));
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

}
