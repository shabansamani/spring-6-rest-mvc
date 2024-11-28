package guru.springframework.spring6restmvc.events;

import org.springframework.security.core.Authentication;

import guru.springframework.spring6restmvc.entities.Beer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * BeerCreatedEvent
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class BeerCreatedEvent {
  private Beer beer;
  private Authentication authentication;
}
