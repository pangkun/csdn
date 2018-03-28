package controller;

import javabean.Article;
import javabean.Author;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Dao {
    public static void saveAuthor(Author author) {
        Connection connection = DBUtil.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into author1 values(0,?)");
            preparedStatement.setString(1, author.getUrl());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveArticle(Article article) {
        Connection connection = DBUtil.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("insert into article1 values(0,?,?,?,?,?)");
            preparedStatement.setString(1, article.getTitle());
            preparedStatement.setString(2, article.getAuthor());
            preparedStatement.setString(3, article.getDate());
            preparedStatement.setString(4, article.getTime());
            preparedStatement.setString(5, article.getUrl());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Author findAuthor(String authorName) {
        Author author = null;
        Connection connection = DBUtil.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("select * from author1 where url = ?");
            preparedStatement.setString(1, authorName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String url = resultSet.getString("url");
                int id = resultSet.getInt("id");
                author.setUrl(url);
                author.setId(id);
                return author;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return author;
    }

    public static Author findNextAuthor(String authorName) {
        Author author = null;
        Connection connection = DBUtil.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("select * from author1 where url = ?");
            preparedStatement.setString(1, authorName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                preparedStatement.execute("select * from author where id = " + (id + 1));
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String url = resultSet.getString("url");
                    author.setId(id+1);
                    author.setUrl(url);
                    return author;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return author;
    }
}
