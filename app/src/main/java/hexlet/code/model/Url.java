package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Url {
    private Long id;
    private String name;
    private Timestamp createdAt;
    private List<UrlCheck> checks;

    public Url(String name) {
        this.name = name;
        this.checks = new LinkedList<>();
    }
}