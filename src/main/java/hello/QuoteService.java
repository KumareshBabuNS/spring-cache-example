package hello;

import java.util.Collections;
import java.util.Map;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * Created by cdavis on 2/13/17.
 */
@SuppressWarnings("unused")
@Service
public class QuoteService {

    protected static final String ID_BASED_QUOTE_SERVICE_URL = "http://gturnquist-quoters.cfapps.io/api/{id}";
    protected static final String RANDOM_QUOTE_SERVICE_URL = "http://gturnquist-quoters.cfapps.io/api/random";

    private volatile boolean cacheMiss = false;

    private final QuoteRepository quoteRepository;

    public QuoteService(QuoteRepository quoteRepository) {
        Assert.notNull(quoteRepository, "QuoteRepository must not be null");
        this.quoteRepository = quoteRepository;
    }

    private RestTemplate quoteServiceTemplate = new RestTemplate();

    /**
     * Determines whether the previous service method invocation resulted in a cache miss.
     *
     * @return a boolean value indicating whether the previous service method invocation resulted in a cache miss.
     */
    public boolean isCacheMiss() {
        boolean cacheMiss = this.cacheMiss;
        this.cacheMiss = false;
        return cacheMiss;
    }

    protected void setCacheMiss() {
        this.cacheMiss = true;
    }

    public Iterable<Quote> findAllQuotes() {
        return quoteRepository.findAll();
    }

    /**
     * Requests a quote with the given identifier.
     *
     * @param id the identifier of the {@link Quote} to request.
     * @return a {@link Quote} with the given ID.
     */
    @Cacheable("Quotes")
    public Quote requestQuote(Long id) {
        setCacheMiss();
        return doRequestQuote(ID_BASED_QUOTE_SERVICE_URL, Collections.singletonMap("id", id));
    }

    /**
     * Requests a random quote.
     *
     * @return a random {@link Quote}.
     */
    @CachePut(cacheNames = "Quotes", key = "#result.id")
    public Quote requestRandomQuote() {
        setCacheMiss();
        return doRequestQuote(RANDOM_QUOTE_SERVICE_URL);
    }

    protected Quote doRequestQuote(String URL) {
        return doRequestQuote(URL, Collections.emptyMap());
    }

    protected Quote doRequestQuote(String URL, Map<String, Object> urlVariables) {
        QuoteResponse quoteResponse = quoteServiceTemplate.getForObject(URL, QuoteResponse.class, urlVariables);
        return quoteResponse.getQuote();
    }
}
