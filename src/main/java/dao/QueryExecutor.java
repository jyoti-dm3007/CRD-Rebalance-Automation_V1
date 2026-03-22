package dao;

import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QueryExecutor {

    public static int getShares(String ticker) throws Exception {

        String query = "SELECT batch_id, sec_name, \"action\", share_quantity, calculated_at\r\n"
        		+ "FROM public.balance_batch_results; " ; 

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT current_database()")
            	
            		
        ) {
        	
    		ResultSet rs = ps.executeQuery();
    while (rs.next()) {
        System.out.println("DB: " + rs.getString(1));
    } 		

	/*
	 * ResultSet rs = ps.executeQuery();
	 * 
	 * if (rs.next()) { return rs.getInt("share_quantity"); } else { throw new
	 * RuntimeException("No data found for ticker: " + ticker); }
	 */
        }
        return 0 ;
    }
}