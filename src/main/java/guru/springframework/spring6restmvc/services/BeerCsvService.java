package guru.springframework.spring6restmvc.services;

import java.io.File;
import java.util.List;

import guru.springframework.spring6restmvc.model.BeerCSVRecord;

public interface BeerCsvService {
  List<BeerCSVRecord> convertCSV(File csvFile);
}
