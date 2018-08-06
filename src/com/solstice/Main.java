package com.solstice;

import com.solstice.db.tables.StockQuotesManager;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) throws Exception {

        Connection conn = ConnectionManager.getInstance().getConnection();
//        StockQuotesManager.populateTable();
        StockQuotesManager.displayAggragatedView();
        conn.close();
    }
}
