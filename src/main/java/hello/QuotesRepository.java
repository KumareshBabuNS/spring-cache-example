package hello;

/**
 * Created by cdavis on 2/14/17.
 */
import org.springframework.data.repository.CrudRepository;

public interface QuotesRepository extends CrudRepository<Quote, String> {

    Iterable<Quote> findAll();

}