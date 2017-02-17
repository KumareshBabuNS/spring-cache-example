package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The QuotesController class...
 *
 * @author John Blum
 * @since 1.0.0
 */
@RestController
@RequestMapping("quotes")
@SuppressWarnings("unused")
public class QuotesController {

  @Autowired
  private QuoteService quoteService;

  @RequestMapping("/gem")
  public Iterable<Quote> getAllQuotes() {
    return quoteService.findAllQuotes();
  }

  @RequestMapping("/")
  public Quote getQuote() {
    long startTime = System.currentTimeMillis();
    Quote quote = quoteService.requestRandomQuote();
    long elapsedTime = System.currentTimeMillis();

    System.err.printf("\"%1$s\"%nCache Miss [%2$s] - Elapsed Time [%3$s ms]%n", quote,
      quoteService.isCacheMiss(), (elapsedTime - startTime));

    return quote;
  }

  @RequestMapping("/{id}")
  public Quote getQuote(@PathVariable("id") long id) {
    long startTime = System.currentTimeMillis();
    Quote quote = quoteService.requestQuote(id);
    long elapsedTime = System.currentTimeMillis();

    System.err.printf("\"%1$s\"%nCache Miss [%2$s] - Elapsed Time [%3$s ms]%n", quote,
      quoteService.isCacheMiss(), (elapsedTime - startTime));

    return quote;
  }
}
