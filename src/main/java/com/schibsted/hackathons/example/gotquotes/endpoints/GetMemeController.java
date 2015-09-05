package com.schibsted.hackathons.example.gotquotes.endpoints;


import com.google.inject.Singleton;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import rx.Observable;
import scmspain.karyon.restrouter.annotation.Endpoint;
import scmspain.karyon.restrouter.annotation.Path;
import scmspain.karyon.restrouter.annotation.QueryParam;

import javax.imageio.ImageIO;
import javax.ws.rs.HttpMethod;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
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
            @QueryParam(value = "text", defaultValue = "", required = true) String text) {
        try {
            BufferedImage originalImage = ImageIO.read(new URL("http://www.cs.cmu.edu/~chuck/lennapg/len_std.jpg"));
            response.getHeaders().add("Content-Type", "image/jpeg");
            response.setStatus(HttpResponseStatus.OK);
            response.writeBytes(getBytes(text, originalImage));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.close();
    }

    private byte[] getBytes(String text, BufferedImage originalImage) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        text = text.toUpperCase();

        Graphics graphics = originalImage.getGraphics();
        FontRenderContext fontRenderContext = ((Graphics2D)graphics).getFontRenderContext();

        Font font = getFont(text, fontRenderContext, originalImage.getWidth(), 36);
        graphics.setFont(font);

        int textLeftMargin = getTextLeftMargin(getOriginalImageCenterPoint(originalImage), getOriginalTextCenterPoint(text, fontRenderContext, font));

        graphics.drawString(text, textLeftMargin, getTextHeight(text, fontRenderContext, font));
        graphics.dispose();

        ImageIO.write(originalImage, "jpg", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private int getTextLeftMargin(int imageCenterWith, int fontCenterWith) {
        return imageCenterWith - fontCenterWith;
    }

    private int getOriginalTextCenterPoint(String text, FontRenderContext fontRenderContext, Font font) {
        return (int) Math.round(getTextWidth(text, fontRenderContext, font) / 2);
    }

    private int getOriginalImageCenterPoint(BufferedImage originalImage) {
        return originalImage.getWidth() / 2;
    }

    private Font getFont(String text, FontRenderContext fontRenderContext, int maxWidth, int fontSize) {
        Font font = new Font("IMPACT", Font.BOLD, fontSize);

        if(getTextWidth(text, fontRenderContext, font) >= maxWidth)
            return getFont(text, fontRenderContext, maxWidth, fontSize - 1);
        else
            return font;
    }

    private int getTextWidth(String text, FontRenderContext fontRenderContext, Font font) {
        return (int) font.getStringBounds(text, fontRenderContext).getWidth();
    }

    private int getTextHeight(String text, FontRenderContext fontRenderContext, Font font) {
        return (int) font.getStringBounds(text, fontRenderContext).getHeight();
    }
}