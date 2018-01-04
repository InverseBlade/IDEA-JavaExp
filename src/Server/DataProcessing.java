package Server;
import fileManage.Administrator;
import fileManage.Browser;
import fileManage.Operator;
import fileManage.User;

import java.util.Enumeration;
import java.util.Hashtable;
import java.sql.*;



public  class DataProcessing {
	private static Connection con;
	private static Statement st;
	private static ResultSet rs;
	private static int numberofRows;
	private static boolean connectToDB=false;

	static private Hashtable<String, User> users = new Hashtable<String, User>();  //注意:先getAll之后,再获取users或docs的size
	static private Hashtable<String, Doc> docs = new Hashtable<String, Doc>();

	public static int getDocsSize(){
	    return docs.size();
    }

    public static int getUsersSize(){
	    return users.size();
    }

	static {
		Init();
		
	}
	
	public static  void Init(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			// connect to database
			con = DriverManager.getConnection(
			        "jdbc:mysql://localhost:3306/java_db?useUnicode=true&characterEncoding=utf8",
                    "root",
                    "" );

			// create Statement to query database
			//st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY );
			st = con.createStatement();

			// update database connection status
			DataProcessing.connectToDB = true;
		}catch(Exception e){
		    e.printStackTrace();
			DataProcessing.connectToDB = false;
		}
	}

	private static void checkDbConect() throws SQLException {
        if(con.isClosed()){
            Init();
        }else if(st.isClosed()){
            st = con.createStatement();
        }else
            return;
    }
	
	public static Doc searchDoc(String ID) throws SQLException,IllegalStateException {
        if ( !connectToDB )
            throw new IllegalStateException( "Not Connected to Database" );

        Doc temp;
        rs = st.executeQuery("SELECT * FROM exp_docs WHERE doc_key = '" + ID +"'");
        if(rs.next()){
            temp = new Doc(
                    rs.getString("doc_key"),
                    rs.getString("creater"),
                    rs.getTimestamp("time"),
                    rs.getString("descr"),
                    rs.getString("file_name")
            );
        }else{
            temp = null;
        }
        return temp;
	}
	
	public static Enumeration<Doc> getAllDocs() throws SQLException,IllegalStateException{
        if ( !connectToDB )
            throw new IllegalStateException( "Not Connected to Database" );

        docs.clear();
        rs = st.executeQuery("SELECT * FROM exp_docs ORDER BY time ");
        while(rs.next()){
            docs.put(
                    rs.getString("doc_key"),
                    new Doc(
                            rs.getString("doc_key"),
                            rs.getString("creater"),
                            rs.getTimestamp("time"),
                            rs.getString("descr"),
                            rs.getString("file_name")
                    )
            );
        }
        return docs.elements();
	} 
	
	public static boolean insertDoc(String creator, String description, String filename) throws SQLException,IllegalStateException{
        if ( !connectToDB )
            throw new IllegalStateException( "Not Connected to Database" );

		String command;

		rs = st.executeQuery("SELECT * FROM exp_docs WHERE file_name = '"+filename+"'");
        if(!rs.next()){
            command = "INSERT INTO exp_docs(creater,descr,file_name)" +
                    "VALUES ('#','#','#')";
            command = command.replaceFirst(
                    "#",creator
            ).replaceFirst(
                    "#",description
            ).replaceFirst(
                    "#",filename
            );

            if(st.executeUpdate(command)!=0){
                rs = st.executeQuery("SELECT * FROM exp_docs WHERE doc_key IS NULL ");

                String ID, key;
                if(rs.next()){
                    key = String.valueOf(rs.getInt("id"));
                    ID = key;
                    for( ;ID.length()<4;ID = "0" + ID);
                    st.executeUpdate(
                            "UPDATE exp_docs SET doc_key = '#' WHERE id = #".replaceFirst(
                                    "#", ID).replaceFirst(
                                            "#", key
                            )
                            );
                }
                return true;
            }
            return false;
        }
		return false;
	} 
	
	
	public static User searchUser(String name, String password) throws SQLException,IllegalStateException {
        checkDbConect();
        if ( !connectToDB )
            throw new IllegalStateException( "Not Connected to Database" );

		try{
            //防SQL注入
            name=name.trim();
            password=password.trim();
            if(
                    name.indexOf("'")!=-1 ||
                            password.indexOf("'")!=-1
                    ){
                return null;
            }

            rs = st.executeQuery("SELECT * FROM exp_users WHERE name = '"+name+"'"+" AND password = '"+password+"'");
            User temp;
            if(rs.next()){
                String role = rs.getString("role");
                switch (role){
                    default:
                        temp = new Browser(name, password, role);
                        break;
                    case "operator":
                        temp = new Operator(name, password, role);
                        break;
                    case "administrator":
                        temp = new Administrator(name, password, role);
                        break;
                }
                return temp;
            }else{
                return null;
            }
        }catch (Exception e){
		    Init();
        }
        return null;
	}
	
	public static Enumeration<User> getAllUser() throws SQLException,IllegalStateException{
        if ( !connectToDB )
            throw new IllegalStateException( "Not Connected to Database" );

		String role, name, password;
		User temp;
        users.clear();
		Enumeration<User> e;
        rs = st.executeQuery("SELECT * FROM exp_users");
        while(rs.next()){
            name = rs.getString("name");
            password = rs.getString("password");
            role = rs.getString("role");
            switch (role) {
                default:
                    temp = new Browser(name, password, role);
                    break;
                case "operator":
                    temp = new Operator(name, password, role);
                    break;
                case "administrator":
                    temp = new Administrator(name, password, role);
                    break;
            }
            users.put(name,temp);
        }
        e = users.elements();
		return e;
	}
	
	
	
	public static boolean updateUser(String name, String password, String role) throws SQLException,IllegalStateException{
        if ( !connectToDB )
            throw new IllegalStateException( "Not Connected to Database" );

        String command;

        rs = st.executeQuery("SELECT * FROM exp_users WHERE name = '"+name+"'");
        if(rs.next()){
            command = "UPDATE exp_users SET password = '#', role = '#' WHERE name = '#'";
            command = command.replaceFirst(
                    "#",password
            ).replaceFirst(
                    "#",role
            ).replaceFirst(
                    "#",name
            );

            if(st.executeUpdate(command)!=0){
                return true;
            }else{
                return false;
            }
        }
        return false;
	}
	
	public static boolean insertUser(String name, String password, String role) throws SQLException,IllegalStateException{
        if ( !connectToDB )
            throw new IllegalStateException( "Not Connected to Database" );

        String command;

        rs = st.executeQuery("SELECT * FROM exp_users WHERE name = '"+name+"'");
        if(!rs.next()){
            command = "INSERT INTO exp_users(name, password, role)" +
                    "VALUES ('#','#','#')";
            command = command.replaceFirst(
                    "#",name
            ).replaceFirst(
                    "#",password
            ).replaceFirst(
                    "#",role
            );

            if(st.executeUpdate(command)!=0){
                return true;
            }else{
                return false;
            }
        }
        return false;
	}
	
	public static boolean deleteUser(String name) throws SQLException,IllegalStateException{
        if ( !connectToDB )
            throw new IllegalStateException( "Not Connected to Database" );
		
		if(st.executeUpdate("DELETE FROM exp_users WHERE name = '" + name + "'")!=0){
		    return true;
        }else{
		    System.out.println("删除失败!");
		    return false;
        }
		
	}	
            
	public void disconnectFromDB() {
		if ( connectToDB ){
			// close Statement and Connection            
			try{
				rs.close();                        
			    st.close();                        
			    con.close();                       
			}catch ( SQLException sqlException ){                                            
			    sqlException.printStackTrace();           
			}finally{                                            
				connectToDB = false;              
			}                             
		} 
   }           

	
	public static void main(String[] args) {
	    System.out.println("This is not a entrance of the programme!! Please choose another one...");
	    System.exit(0);
	}
	
}
