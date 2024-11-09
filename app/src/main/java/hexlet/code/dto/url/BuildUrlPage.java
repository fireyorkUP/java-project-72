package hexlet.code.dto.url;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BuildUrlPage extends BasePage {

    private String name;
    private LocalDateTime createdAt;
    private Exception errors;
}