package guru.springframework.spring6restmvc.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import guru.springframework.spring6restmvc.events.BeerCreatedEvent;

/**
 * BeerCreatedListener
 */
@Component
public class BeerCreatedListener {

  @EventListener
  public void listen(BeerCreatedEvent beerCreatedEvent) {
    System.out.println("I heard a beer was created!");
    System.out.println(beerCreatedEvent.getBeer().getId());
  }

}
