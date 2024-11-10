package hexlet.code.repository;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class UrlCheckRepository extends BaseRepository {

    public static void saveCheck(UrlCheck checkUrl, Url url) throws SQLException {
        String sqlCheck = "INSERT INTO url_checks (url_id, status_code,"
                + " h1, title, description, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sqlCheck, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, checkUrl.getUrlId());
            preparedStatement.setInt(2, checkUrl.getStatusCode());
            preparedStatement.setString(3, checkUrl.getH1());
            preparedStatement.setString(4, checkUrl.getTitle());
            preparedStatement.setString(5, checkUrl.getDescription());
            var createdAt = LocalDateTime.now();
            preparedStatement.setTimestamp(6, Timestamp.valueOf(createdAt));
            preparedStatement.executeUpdate();
        }
    }

    public static List<UrlCheck> find(Long urlId) throws SQLException {
        var sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            var resultSet = stmt.executeQuery();
            List<UrlCheck> checks = new ArrayList<>();
            while (resultSet.next()) {
                long checkId = resultSet.getLong("id");
                int statusCode = resultSet.getInt("status_code");
                String h1 = resultSet.getString("h1");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                var urlCheck = new UrlCheck(urlId, statusCode, title, h1, description);
                urlCheck.setCreatedAt(createdAt);
                urlCheck.setId(checkId);
                checks.add(urlCheck);
            }
            return checks;
        }
    }

    public static Map<Long, UrlCheck> findLatestChecks() throws SQLException {
        var map = new HashMap<Long, UrlCheck>();
        var sql = "SELECT * FROM url_checks";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                long urlId = resultSet.getLong("url_id");
                long checkId = resultSet.getLong("id");
                int statusCode = resultSet.getInt("status_code");
                String h1 = resultSet.getString("h1");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                Timestamp createdAt = resultSet.getTimestamp("created_at");
                var urlCheck = new UrlCheck(urlId, statusCode, title, h1, description);
                urlCheck.setCreatedAt(createdAt);
                urlCheck.setId(checkId);
                map.put(urlId, urlCheck);
            }
            return map;
        }
    }
}