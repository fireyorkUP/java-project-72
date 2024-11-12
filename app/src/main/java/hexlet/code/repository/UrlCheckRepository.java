package hexlet.code.repository;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;

public class UrlCheckRepository extends BaseRepository {

    public static void saveCheck(UrlCheck urlCheck, Url url) throws SQLException {
        String sql = "INSERT INTO url_checks (status_code, title, h1, description, url_id, created_at)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, urlCheck.getStatusCode());
            preparedStatement.setString(2, urlCheck.getTitle());
            preparedStatement.setString(3, urlCheck.getH1());
            preparedStatement.setString(4, urlCheck.getDescription());
            preparedStatement.setLong(5, url.getId());
            preparedStatement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
                urlCheck.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            } else {
                throw new SQLException("No id or createdAt");
            }
        }
    }

    public static List<UrlCheck> findByUrl(Long id) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC";
        var listOfChecks = new LinkedList<UrlCheck>();
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                var StatusCode = resultSet.getInt("status_code");
                var Title = resultSet.getString("title");
                var H1 = resultSet.getString("h1");
                var Description = resultSet.getString("description");
                var CreatedAt = resultSet.getTimestamp("created_at");
                var Id = resultSet.getLong("id");
                var UrlId = resultSet.getLong("url_id");
                UrlCheck urlCheck = new UrlCheck(StatusCode, Title, H1, Description, UrlId);
                urlCheck.setId(Id);
                urlCheck.setCreatedAt(CreatedAt);
                listOfChecks.add(urlCheck);
            }
            return listOfChecks;
        }
    }

    public static List<UrlCheck> findLatestChecks() throws SQLException {
        var result = new ArrayList<UrlCheck>();
        var sql = "SELECT * FROM url_checks";
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                var statusCode = resultSet.getInt("status_code");
                var title = resultSet.getString("title");
                var h1 = resultSet.getString("h1");
                var description = resultSet.getString("description");
                var urlId = resultSet.getLong("url_id");
                UrlCheck urlCheck = new UrlCheck(statusCode, title, h1, description, urlId);
                urlCheck.setId(resultSet.getLong("id"));
                urlCheck.setCreatedAt(resultSet.getTimestamp("created_at"));
                result.add(urlCheck);
            }
            return result;
        }
    }
}