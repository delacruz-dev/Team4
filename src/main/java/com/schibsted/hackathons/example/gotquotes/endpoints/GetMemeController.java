package com.schibsted.hackathons.example.gotquotes.endpoints;


import com.google.inject.Singleton;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.channel.StringTransformer;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import rx.Observable;
import scmspain.karyon.restrouter.annotation.Endpoint;
import scmspain.karyon.restrouter.annotation.Path;
import scmspain.karyon.restrouter.annotation.PathParam;

import javax.ws.rs.HttpMethod;

@Singleton
@Endpoint
public class GetMemeController {
    public GetMemeController() {

    }


    @Path(value = "/api/meme/", method = HttpMethod.GET)
    public Observable<Void> getQuote(HttpServerResponse<ByteBuf> response) {

        JSONObject content = new JSONObject();
        try {
            content.put("Hello", "World");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        response.write(content.toString(), StringTransformer.DEFAULT_INSTANCE);
        return response.close();
    }
}