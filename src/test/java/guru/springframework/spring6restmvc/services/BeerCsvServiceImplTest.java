package guru.springframework.spring6restmvc.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;
import org.springframework.util.ResourceUtils;

import guru.springframework.spring6restmvc.model.BeerCSVRecord;

public class BeerCsvServiceImplTest {

    BeerCsvService beerCsvService = new BeerCsvServiceImpl();

    @Test
    public void convertCSV() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");

        List<BeerCSVRecord> recs = beerCsvService.convertCSV(file);

        System.out.println(recs.size());

        assertThat(recs.size()).isGreaterThan(0);
    }
}
