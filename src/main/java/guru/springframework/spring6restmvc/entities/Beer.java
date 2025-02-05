package guru.springframework.spring6restmvc.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import guru.springframework.spring6restmvc.model.BeerStyle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Beer {

  @Id
  @GeneratedValue(generator = "UUID")
  @UuidGenerator
  @JdbcTypeCode(SqlTypes.CHAR)
  @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
  private UUID id;

  @Version
  private Integer version;

  @NotNull
  @NotBlank
  @Size(max = 50)
  @Column(length = 50)
  private String beerName;

  @NotNull
  private BeerStyle beerStyle;

  @NotNull
  @NotBlank
  private String upc;
  private Integer quantityOnHand;

  @NotNull
  private BigDecimal price;

  @CreationTimestamp
  private LocalDateTime createdDate;

  @UpdateTimestamp
  private LocalDateTime updatedDate;

}
