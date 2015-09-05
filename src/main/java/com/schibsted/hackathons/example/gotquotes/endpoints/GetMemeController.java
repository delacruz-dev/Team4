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
import rx.schedulers.Schedulers;
import scmspain.karyon.restrouter.annotation.Endpoint;
import scmspain.karyon.restrouter.annotation.Path;
import scmspain.karyon.restrouter.annotation.PathParam;
import scmspain.karyon.restrouter.annotation.QueryParam;

import javax.imageio.ImageIO;
import javax.ws.rs.HttpMethod;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

@Singleton
@Endpoint
public class GetMemeController {
    public GetMemeController() {

    }
    
    @Path(value = "/api/meme", method = HttpMethod.GET)
    public Observable<Void> getMeme(
            HttpServerRequest<ByteBuf> request,
            HttpServerResponse<ByteBuf> response,
            @QueryParam(value = "name", defaultValue = "", required = true) String name,
            @QueryParam(value = "top", defaultValue = "", required = true) String top,
            @QueryParam(value = "bottom", defaultValue = "", required = true) String bottom) {

        String replacedName = name.replaceAll("\\s", "-");
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("blank_memes/" + replacedName + ".jpg");

        return Observable
                .<BufferedImage>create(subscriber -> {
                    try {
                        subscriber.onNext(ImageIO.read(url));
                        subscriber.onCompleted();
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                })
                .observeOn(Schedulers.io())
                .map(image -> {
                    try {
                        return writeTextOnImage(image, top, bottom);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(ByteArrayOutputStream::toByteArray)
                .flatMap(imageInByte -> {
                    response.getHeaders().add("Content-Type", "image/jpeg");
                    response.setStatus(HttpResponseStatus.OK);
                    response.writeBytes(imageInByte);
                    return response.close();
                })
                .observeOn(Schedulers.computation());
    }

    private ByteArrayOutputStream writeTextOnImage(BufferedImage originalImage, String top, String bottom) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Graphics g = originalImage.getGraphics();
        Font font = new Font("IMPACT", Font.BOLD, 36);
        g.setFont(font);
        g.drawString(top.toUpperCase(), 0, 50);
        g.drawString(bottom.toUpperCase(), 0, 230);
        g.dispose();
        ImageIO.write(originalImage, "jpg", baos);
        return baos;
    }
}