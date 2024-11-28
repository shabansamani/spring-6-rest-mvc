package guru.springframework.spring6restmvc.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CustomerDTO {
  private String customerName;
  private UUID id;
  private Integer version;
  private LocalDateTime createdDate;
  private LocalDateTime lastModifiedDate;
}
