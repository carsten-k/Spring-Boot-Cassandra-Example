package de.corschdi.cassandra.model.entity;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.corschdi.cassandra.model.enums.CartStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Table("shopping_cart")
@Getter
@Setter
@ToString
public class ShoppingCart {

    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED, ordinal = 0, ordering = Ordering.DESCENDING)
    private UUID uuid;

    @Column
    private CartStatus status;

    @Column
    private String owner;

    @Column("changed_on")
    private LocalDateTime changedOn;

    @JsonIgnore
    public LocalDateTime getCreatedOn() {
        return Instant.ofEpochMilli(Uuids.unixTimestamp(getUuid())).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
