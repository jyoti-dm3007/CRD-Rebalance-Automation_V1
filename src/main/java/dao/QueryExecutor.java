package dao;

import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QueryExecutor {

    public static int getShares(String ticker) throws Exception {

        String query = "SELECT rr.share_quantity " +
                "FROM portfolio_holdings ph " +
                "JOIN rebalance_results rr ON ph.holding_id = rr.holding_id " +
                "JOIN securities sc ON ph.security_id = sc.security_id " +
                "WHERE sc.ticker = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)
        ) {

            ps.setString(1, ticker);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("share_quantity");
            } else {
                throw new RuntimeException("No data found for ticker: " + ticker);
            }
        }
    }
}