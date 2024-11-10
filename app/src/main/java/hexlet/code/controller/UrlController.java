package hexlet.code.controller;

import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlController {
    public static void add(Context ctx) throws SQLException, IllegalArgumentException{
        var input = ctx.formParam("url");
        try {
            URL absoluteUrl = new URI(input).toURL();
            String schema = absoluteUrl.toURI().getScheme();
            String authority = absoluteUrl.toURI().getAuthority();
            Url url = new Url(schema + "://" + authority);
            Optional<Url> foundedUrl = UrlRepository.findByName(url.getName());
            if (foundedUrl.isEmpty()) {
                UrlRepository.save(url);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("flashType", "alert-success");
            } else {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flashType", "alert-info");
            }
            ctx.redirect(NamedRoutes.urlsPath());
        } catch (URISyntaxException | IllegalArgumentException | IOException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flashType", "alert-danger");
            ctx.redirect(NamedRoutes.rootPath());
        }
    }

    public static void urlList(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        Map<Long, UrlCheck> latestChecks = UrlCheckRepository.findLatestChecks();
        var page = new UrlsPage(urls, latestChecks);
        String flash = ctx.consumeSessionAttribute("flash");
        page.setFlash(flash);
        String flashType = ctx.consumeSessionAttribute("flashType");
        page.setFlashType(flashType);
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));
        var page = new UrlPage(url);
        String flash = ctx.consumeSessionAttribute("flash");
        page.setFlash(flash);
        String flashType = ctx.consumeSessionAttribute("flashType");
        page.setFlashType(flashType);
        ctx.render("urls/show.jte", model("page", page));
    }

    public static void doCheck(Context ctx) throws SQLException, UnirestException {
        var urlId = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.findById(urlId)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));
        try {
            URL urlForConnection = new URI(url.getName()).toURL();
            HttpResponse<String> httpResponse = Unirest.get(url.getName()).asString();
            int statusCode = httpResponse.getStatus();
            String responseBody = httpResponse.getBody();
            Document document = Jsoup.parse(responseBody);
            String title = document.title();
            String firstH1 = document.select("h1").text();
            String description = document.select("meta[name=description]").attr("content");
            UrlCheck urlCheck = new UrlCheck();
            urlCheck.setStatusCode(statusCode);
            urlCheck.setH1(firstH1);
            urlCheck.setTitle(title);
            urlCheck.setDescription(description);
            UrlCheckRepository.saveCheck(urlCheck, url);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "alert-success");
            ctx.redirect(NamedRoutes.urlPath(urlId));
        } catch (UnirestException | URISyntaxException | MalformedURLException e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flashType", "alert-danger");
            ctx.redirect(NamedRoutes.urlPath(url.getId()));
        }
    }

}
