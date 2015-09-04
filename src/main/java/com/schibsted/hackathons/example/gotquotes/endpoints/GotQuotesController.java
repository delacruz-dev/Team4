package com.schibsted.hackathons.example.gotquotes.endpoints;

import com.google.inject.Singleton;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.channel.StringTransformer;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import scmspain.karyon.restrouter.annotation.Endpoint;
import scmspain.karyon.restrouter.annotation.Path;
import scmspain.karyon.restrouter.annotation.PathParam;

import javax.ws.rs.HttpMethod;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
@Endpoint
public class GotQuotesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GotQuotesController.class);

    private static final int QUOTE_NOT_FOUND = -1;
    private static final int QUOTE_TOP = -2;

    private static final Random rand = new Random();
    private static List<String> quotes = new ArrayList<String>();
    private static List<AtomicInteger> quotesAccess = null;

    static {
        quotes.add("Winter is coming.");
        quotes.add("You know nothing, Jon Snow.");
        quotes.add("A Lannister always pays his debts.");
        quotes.add("We do not sow.");
        quotes.add("The First Sword of Bravos does not run.");
        quotes.add("Valar Morghulis.");
        quotes.add("Hodor!");
        quotes.add("The King in the North!");
        quotes.add("When you play a game of thrones you win or you die.");
        quotes.add("The things I do for love.");
        quotes.add("A very small man can cast a very large shadow.");

        quotesAccess = Stream.generate(AtomicInteger::new)
                .limit(quotes.size())
                .collect(Collectors.toList());
    }

    public GotQuotesController() {
    }

    // This endpoint shouldn't be removed since will be always needed
    // Healthcheck endpoint needed by Asgard to validate the service is working
    @Path(value = "/healthcheck", method = HttpMethod.GET)
    public Observable<Void> healthcheck(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        response.setStatus(HttpResponseStatus.OK);
        response.write("{\"status\":\"ok\"}", StringTransformer.DEFAULT_INSTANCE);
        return response.close();
    }

    // This endpoint shouldn't be removed since will be always needed
    // Healthcheck endpoint needed by Prana to validate the service is working
    @Path(value = "/Status", method = HttpMethod.GET)
    public Observable<Void> status(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        response.setStatus(HttpResponseStatus.OK);
        response.write("Eureka!", StringTransformer.DEFAULT_INSTANCE);
        return response.close();
    }


    @Path(value = "/api/quote/{action}", method = HttpMethod.GET)
    public Observable<Void> getQuote(HttpServerResponse<ByteBuf> response, @PathParam("action") String action) {
        LOGGER.debug("Quote 'action' received: {}", action);
        JSONObject content = new JSONObject();

        try {
            int quoteIdx = getQuoteIdxFromAction(action);
            if (quoteIdx == QUOTE_TOP) {
                fillTopQuoteInfo(content);
            } else if (quoteIdx == QUOTE_NOT_FOUND) {
                response.setStatus(HttpResponseStatus.NOT_FOUND);
                fillNotFoundInfo(content);
            } else {
                fillQuoteInfo(content, quoteIdx);
            }

        } catch (JSONException e) {
            LOGGER.error("Error creating json response.", e);
            return Observable.error(e);
        }

        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        response.write(content.toString(), StringTransformer.DEFAULT_INSTANCE);
        return response.close();
    }


    private void fillNotFoundInfo(JSONObject content) throws JSONException {
        content.put("status", 404);
        content.put("message", "Quote not found!");
    }

    private void fillQuoteInfo(JSONObject content, int quoteIdx) throws JSONException {
        String quote = quotes.get(quoteIdx);
        int counter = counter = quotesAccess.get(quoteIdx).incrementAndGet();

        LOGGER.debug("Counters: {}", quotesAccess.toString());
        content.put("quote", quote);
        content.put("counter", counter);
    }

    private void fillTopQuoteInfo(JSONObject content) throws JSONException {
        // maybe the less optimal solution ever, but dude, it works...
        int maxIdx = 0;
        int maxValue = 0;
        for (int i = 0; i < quotes.size(); ++i) {
            int counter = quotesAccess.get(i).get();
            if (counter > maxValue) {
                maxValue = counter;
                maxIdx = i;
            }
        }

        content.put("quote", quotes.get(maxIdx));
        content.put("counter", maxValue);
    }

    private int getQuoteIdxFromAction(String action) throws NumberFormatException {

        int quoteIdx = 0;
        if ("random".equalsIgnoreCase(action)) {
            quoteIdx = rand.nextInt(quotes.size());
        } else if ("top".equalsIgnoreCase(action)) {
            quoteIdx = QUOTE_TOP;
        } else {
            try {
                quoteIdx = Integer.parseInt(action);
                if (quoteIdx < 0 || quoteIdx >= quotes.size())
                    quoteIdx = QUOTE_NOT_FOUND;
            } catch (NumberFormatException e) {
                quoteIdx = QUOTE_NOT_FOUND;
            }
        }

        return quoteIdx;
    }
}