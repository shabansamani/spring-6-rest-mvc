package guru.springframework.spring6restmvc.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import guru.springframework.spring6restmvc.entities.Beer;

@SpringBootTest
public class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BeerRepository beerRepository;
    Beer testBeer;

    @BeforeEach
    void setUp() {
        testBeer = beerRepository.findAll().get(0);
    }

    // @Transactional
    // @Test
    // void testAddCategory() {
    //     Category savedCategory = categoryRepository.save(Category.builder().description("Ales").build());
    //     testBeer.addCategory(savedCategory);
    //     Beer savedBeer = beerRepository.save(testBeer);

    //     System.out.println(savedBeer.getBeerName());
    // }

    
}
