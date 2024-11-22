package hexlet.code.controller;

import hexlet.code.dto.BasePage;
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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlController {
    public static void add(Context ctx) throws SQLException, IllegalArgumentException {
        var input = ctx.formParam("url");
        try {
            URL absoluteUrl = new URI(input).toURL();
            String schema = absoluteUrl.toURI().getScheme();
            String authority = absoluteUrl.toURI().getAuthority();
            Url url = new Url(schema + "://" + authority);
            Optional<Url> foundedUrl = UrlRepository.findByName(url.getName());
            if (foundedUrl.isEmpty()) {
                UrlRepository.save(url);
                setFlashMessage(ctx, "Страница успешно добавлена", "alert-success");
            } else {
                setFlashMessage(ctx, "Страница уже существует", "alert-info");
            }
            ctx.redirect(NamedRoutes.urlsPath());
        } catch (Exception e) {
            BasePage page = new BasePage();
            page.setFlash("Некорректный формат URL");
            page.setFlashType("alert-danger");
            ctx.render("index.jte", model("page", page));
        }
    }

    public static void show(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        List<UrlCheck> latestChecks = UrlCheckRepository.findLatestChecks();
        var page = new UrlsPage(urls, latestChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void showCheck(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Url № " + id + " not found"));
        var page = new UrlPage(url);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));
        ctx.render("urls/show.jte", model("page", page));
    }

    public static void doCheck(Context ctx) throws SQLException, UnirestException {
        var input = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.findById(input)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));
        try {
            URL urlForConnection = new URI(url.getName()).toURL();
            HttpResponse<String> httpResponse = Unirest.get(urlForConnection.toString()).asString();
            int statusCode = httpResponse.getStatus();
            String responseBody = httpResponse.getBody();
            Document document = Jsoup.parse(responseBody);
            String title = document.title();
            String firstH1 = document.select("h1").isEmpty() ? "" : document.select("h1").text();
            String description = document.select("meta[name=description]").isEmpty() ? ""
                    : document.select("meta[name=description]").attr("content");

            UrlCheck urlCheck = new UrlCheck();
            urlCheck.setStatusCode(statusCode);
            urlCheck.setH1(firstH1);
            urlCheck.setTitle(title);
            urlCheck.setDescription(description);
            UrlCheckRepository.saveCheck(urlCheck, url);
            setFlashMessage(ctx, "Страница успешно проверена", "alert-success");
            ctx.redirect(NamedRoutes.urlPath(input));
        } catch (URISyntaxException e) {
            setFlashMessage(ctx, "Некорректный адрес: Неверный синтаксис URL", "alert-danger");
            ctx.redirect(NamedRoutes.urlPath(url.getId()));
        } catch (MalformedURLException e) {
            setFlashMessage(ctx, "Некорректный адрес: Некорректный формат URL", "alert-danger");
            ctx.redirect(NamedRoutes.urlPath(url.getId()));
        } catch (UnirestException e) {
            setFlashMessage(ctx, "Ошибка при выполнении запроса", "alert-danger");
            ctx.redirect(NamedRoutes.urlPath(url.getId()));
        } catch (Exception e) {
            setFlashMessage(ctx, "Неизвестная ошибка: " + e.getMessage(), "alert-danger");
            ctx.redirect(NamedRoutes.urlPath(url.getId()));
        }
    }

    private static void setFlashMessage(Context ctx, String message, String type) {
        ctx.sessionAttribute("flash", message);
        ctx.sessionAttribute("flashType", type);
    }

}
