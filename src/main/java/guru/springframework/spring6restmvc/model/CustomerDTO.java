package guru.springframework.spring6restmvc.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDTO {

  private UUID id;
  private Integer version;
  private String customerName;
  private LocalDateTime createdDate;
  private LocalDateTime updatedDate;
}
