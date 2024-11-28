package guru.springframework.spring6restmvc.services;

import java.util.List;
import java.io.File;
import guru.springframework.spring6restmvc.model.BeerCSVRecord;

public interface BeerCsvService {
    List<BeerCSVRecord> convertCSV(File file);
}
