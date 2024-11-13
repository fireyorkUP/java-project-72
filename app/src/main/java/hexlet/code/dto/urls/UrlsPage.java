package hexlet.code.dto.urls;

import hexlet.code.dto.BasePage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@AllArgsConstructor
@Getter
public class UrlsPage extends BasePage {
    private List<Url> urls;
    private List<UrlCheck> checks;
}
