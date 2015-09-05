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

import javax.imageio.ImageIO;
import javax.ws.rs.HttpMethod;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;

@Singleton
@Endpoint
public class GetMemeController {
    public GetMemeController() {

    }

    @Path(value = "/api/meme", method = HttpMethod.GET)
    public Observable<Void> getMeme(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        try {
            BufferedImage originalImage= ImageIO.read(new URL("http://www.cs.cmu.edu/~chuck/lennapg/len_std.jpg"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ImageIO.write(originalImage, "jpg", baos);
            byte[] imageInByte = baos.toByteArray();

            response.getHeaders().add("Content-Type", "image/jpeg");
            response.setStatus(HttpResponseStatus.OK);
            response.writeBytes(imageInByte);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.close();
    }
}