package guru.springframework.spring6restmvc.mappers;

import org.mapstruct.Mapper;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.model.BeerDTO;

@Mapper(componentModel = "spring")
public interface BeerMapper {
  Beer beerDtoToBeer(BeerDTO beerDTO);

  BeerDTO beerToBeerDTO(Beer beer);
}
