package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import hexlet.code.repository.UrlRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public final class AppTest {
    private Javalin app;
    private static MockWebServer mockServer;

    @BeforeAll
    public static void startMock() throws Exception {
        mockServer = new MockWebServer();
        Path path = Paths.get("src/test/resources/example.html").toAbsolutePath().normalize();
        String htmlFile = Files.readString(path);
        MockResponse mockedResponse = new MockResponse().setBody(htmlFile).setResponseCode(200);
        mockServer.enqueue(mockedResponse);
    }

    @AfterAll
    public static void shutDownMock() throws IOException {
        mockServer.shutdown();
    }

    @BeforeEach
    public void startApp() throws SQLException, IOException {
        app = App.getApp();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string().contains("Анализатор страниц"));
        });
    }

    @Test
    public void testUrlList() {
        JavalinTest.test(app, (server, client) -> {
            assertThat(client.get("/urls").code()).isEqualTo(200);
        });
    }

    @Test
    public void testUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://www.github.com";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://www.github.com");
        });
    }

    @Test
    public void testTwoUrl() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            String firstRequest = "url=https://www.example.com";
            client.post(NamedRoutes.urlsPath(), firstRequest);
            String secondRequest = "url=https://www.example.com";
            client.post(NamedRoutes.urlsPath(), secondRequest);
            var firstRequestFound = client.get(NamedRoutes.urlPath("1"));
            var secondRequestFound = client.get(NamedRoutes.urlPath("2"));
            assertThat(UrlRepository.getEntities().size() == 1);
            assertThat(firstRequestFound.code()).isEqualTo(200);
            assertThat(secondRequestFound.code()).isEqualTo(404);
        });
    }

    @Test
    public void testWrongUrl() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/9999");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    public void testCheck() {
        String serverUrl = mockServer.url("/").toString();
        JavalinTest.test(app, (server, client) -> {
            Url url = new Url(serverUrl);
            UrlRepository.save(url);
            client.post("/urls/" + url.getId() + "/checks");
            List<UrlCheck> checkUrl = UrlCheckRepository.findByUrl(url.getId());
            String title = checkUrl.get(0).getTitle();
            String h1 = checkUrl.get(0).getH1();
            String description = checkUrl.get(0).getDescription();
            assertThat(title).isEqualTo("Анализатор страниц");
            assertThat(h1).isEqualTo("H1 tag content");
            assertThat(description).isEqualTo("Content of description");
        });
    }
}
