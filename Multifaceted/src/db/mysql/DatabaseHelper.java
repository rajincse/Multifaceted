package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;

public class DatabaseHelper {
	protected String url;
	protected String classPath;
	protected String user;
	protected String password;
	public DatabaseHelper(String url,  String classPath,String user,String password )
	{
		this.url = url;
		this.classPath = classPath;
		this.user = user;
		this.password = password;
	}
	public boolean isValidConnection()
	{
		boolean result = false;
		try {
			Class.forName(this.classPath);
			Connection con = DriverManager.getConnection(this.url,this.user,this.password);
			con.close();
			result = true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	protected boolean execute(String command)
	{
		boolean result = false;
		try {
			Class.forName(this.classPath);
			Connection con = DriverManager.getConnection(this.url,this.user,this.password);
			Statement s = con.createStatement();
			s.execute(command);
			
			s.close();
			con.close();
			result = true;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public DefaultTableModel getData(String command)
	{
		DefaultTableModel table = new DefaultTableModel();
		try {
			Class.forName(this.classPath);
			Connection con = DriverManager.getConnection(this.url,this.user,this.password);
			Statement s = con.createStatement();
			s.execute(command);
			ResultSet set = s.getResultSet();
			ResultSetMetaData metaData = set.getMetaData();
			int totalColumn = metaData.getColumnCount();
			Object[] dataRow = new Object[totalColumn];
			if(set!= null)
			{
				for(int i=1;i<=totalColumn;i++)
				{
					table.addColumn(metaData.getColumnName(i));
				}
				while(set.next())
				{
					for(int i=1;i<=totalColumn;i++)
					{
						dataRow[i-1] = set.getObject(i);
					}
					table.addRow(dataRow);
				}
				
			}
			s.close();
			con.close();
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return table;
	}
}
