package hello;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by cdavis on 2/14/17.
 */
public interface QuoteRepository extends CrudRepository<Quote, String> {
}
