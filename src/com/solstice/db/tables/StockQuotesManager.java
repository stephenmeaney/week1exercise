package com.solstice.db.tables;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solstice.ConnectionManager;
import com.solstice.InputHelper;
import com.solstice.db.beans.StockQuote;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


public class StockQuotesManager {

    private static Connection conn = ConnectionManager.getInstance().getConnection();

    public static boolean insert(StockQuote bean) throws SQLException {

        String sql = "INSERT into stock_quotes (symbol, price, volume, quote_date) " +
                "VALUES (?, ?, ?, ?)";
        ResultSet keys = null;
        try (
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {

            stmt.setString(1, bean.getSymbol());
            stmt.setDouble(2, bean.getPrice());
            stmt.setInt(3, bean.getVolume());
            stmt.setTimestamp(4, bean.getDate());

            int affected = stmt.executeUpdate();

            if (affected == 1) {
                keys = stmt.getGeneratedKeys();
                keys.next();
                int newKey = keys.getInt(1);
                bean.setId(newKey);
            } else {
                System.err.println("No rows affected");
                return false;
            }

        } catch (SQLException e) {
            System.err.println(e);
            return false;
        } finally{
            if (keys != null) {
                keys.close();
            }
        }
        return true;
    }


    public static boolean clearTable() throws SQLException {
//        String sqlDelete = "DELETE FROM stock_quotes";
        String sqlDelete = "TRUNCATE TABLE stock_quotes";
        String sqlSelectAll = "SELECT * FROM stock_quotes";

        ResultSet rs = null;

        try (
                Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ) {
            stmt.executeUpdate(sqlDelete);
            rs = stmt.executeQuery(sqlSelectAll);

            rs.last();

            if (rs.getRow() != 0) {
                return false;
            }

        } catch (SQLException e) {
            System.err.println(e);
            return false;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }

        return true;
    }


    public static void populateTable() throws Exception {
        clearTable();

        ObjectMapper mapper = new ObjectMapper();
        URL url = new URL("https://bootcamp-training-files.cfapps.io/week1/week1-stocks.json");

        List<StockQuote> beanList = mapper.readValue(url, new TypeReference<List<StockQuote>>(){});

        for (StockQuote bean : beanList) {
            StockQuotesManager.insert(bean);
        }

    }


    public static void displayAggragatedView() throws SQLException {
        String symbol = InputHelper.getInput("Enter symbol: ");
        String dateStr = InputHelper.getInput("Enter date (YYYY-MM-DD): ");

        String sql =
                "SELECT MIN(price), MAX(price), SUM(volume), (\n" +
                        "\tSELECT price\n" +
                        "\tFROM week1stockquotes.stock_quotes\n" +
                        "\tWHERE date(quote_date) = ? AND symbol = ? AND time(quote_date) = (\n" +
                        "\t\tSELECT MAX(time(quote_date))\n" +
                        "        FROM week1stockquotes.stock_quotes))\n" +
                        "FROM week1stockquotes.stock_quotes\n" +
                        "WHERE date(quote_date) = ? AND symbol = ?";

        ResultSet rs = null;
        try (
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setString(1, dateStr);
            stmt.setString(2, symbol);
            stmt.setString(3, dateStr);
            stmt.setString(4, symbol);

            rs = stmt.executeQuery();

            if (rs.next() && rs.getDouble(1) > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("\nMin Price: " + rs.getDouble(1));
                sb.append("\nMax Price: " + rs.getDouble(2));
                sb.append("\nClosing Price: " + rs.getDouble(4));
                sb.append("\nTotal Volume: " + rs.getInt(3));

                System.out.println(sb.toString());
            } else {
                System.err.println("No records found");
            }
        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }
}
