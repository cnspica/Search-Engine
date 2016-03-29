/**
 * Created by Hanaa Mohamed on 3/26/16.
 */

import java.sql.*;
public class DBManager {




        private static final String DBClassName="com.mysql.jdbc.Driver";
        private Connection conn;



        public DBManager(String url,String userName,String Password){

            this.connectToDatabase(url, userName, Password);


        }

        private void connectToDatabase(String url,String userName,String Password){

            try{

                Class.forName(DBClassName).newInstance();
                this.conn=DriverManager.getConnection(url,userName,Password);


            }
            catch(Exception exc){
                System.out.println("Connection to Database failed for the following : "+exc.getMessage());
                exc.printStackTrace();
            }


        }

        //for INSERT,UPDATE,DELETE
        public boolean ExecuteNonQuery(String NonQuery){

            try{
                PreparedStatement stmt=this.conn.prepareStatement(NonQuery);
                stmt.executeUpdate();
                return true;
            }
            catch(Exception exc){
                System.out.println("Updating Search index failed for the following :"+exc.getMessage());
                return false;
            }

        }

        //for SELECT
        public ResultSet ExecuteQuery(String Query){
            try{
                PreparedStatement stmt=this.conn.prepareStatement(Query);

                return stmt.executeQuery();

            }
            catch(Exception exc){
                System.out.println("Can not View records from the DB for the following :"+exc.getMessage());
                return null;
            }

        }

        public void TerminateConnection(){
            try{
                this.conn.close();
            }
            catch(Exception exc){
                System.out.println("Terminating Connection failed for the following"+exc.getMessage());
            }

        }

    }


