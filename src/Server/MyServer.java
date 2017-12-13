package Server;
import java.net.*;
import java.io.*;
import java.util.*;

import common.*;
import fileManage.*;

public class MyServer  {
    ServerSocket ss=null;
    Socket s=null;
    ObjectOutputStream oos=null;
    ObjectInputStream ois=null;

    private final String upload_path = "UploadFile\\";

    public static void main(String[] args)  {
        try{
            new MyServer();
        }catch (Exception err){
            err.printStackTrace();
        }
    }

    public MyServer() throws Exception {
        try{
            Class.forName("fileManage.DataProcessing");
            ss = new ServerSocket(2017);

            boolean ext;
            while(true){
                System.out.println("等待客户端连接中。。。");
                s = ss.accept();
                System.out.println("开始服务!");
                oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(new Response(true,null,"ready!"));

                ois = new ObjectInputStream(s.getInputStream());
                Request req;
                ext=false;
                while(!ext){
                    req = (Request)ois.readObject();

                    //控制台显示请求
                    System.out.println("action: "+req.getAction()+"\n"+req.getParam()+"   "+req.getObjReq()+"\n");

                    switch (req.getAction()){
                        case "login":
                        {
                            String[] param = req.getParam().split("[|]");
                            User user;

                            if((user=DataProcessing.searchUser(param[0], param[1]))!=null){
                                oos.writeObject(new Response(true,null,"ok",user));
                            }else{
                                oos.writeObject(new Response(false,"error info!",null));
                            }
                            break;
                        }
                        case "logout":
                        {
                            this.oos.writeObject(new Response(true,null,null));
                            this.oos.close();
                            this.ois.close();
                            this.s.close();
                            ext=true;
                            break;
                        }
                        case "getFileList":
                        {
                            Enumeration<Doc> docs = DataProcessing.getAllDocs();
                            String[][] row = new String[DataProcessing.getDocsSize()][5];
                            Doc doc;
                            int i=0;

                            while(docs.hasMoreElements()){
                                doc = docs.nextElement();
                                row[i][0] = doc.getID();
                                row[i][1] = doc.getCreator();
                                row[i][2] = doc.getTimestamp().toString().substring(0, doc.getTimestamp().toString().length()-2);
                                row[i][3] = doc.getDescription();
                                row[i][4] = doc.getFilename();
                                i++;
                            }

                            oos.writeObject(new Response(true,null,null,row));
                            break;
                        }
                        case "getUserList":
                        {
                            int i=0;
                            User user;
                            Enumeration<User> e;
                            e=DataProcessing.getAllUser();
                            String[][] data = new String[DataProcessing.getUsersSize()][4];

                            while(e.hasMoreElements()){
                                user=e.nextElement();
                                data[i][0] = String.valueOf(i+1);
                                data[i][1] = user.getName();
                                data[i][2] = user.getPassword();
                                data[i][3] = user.getRole();
                                i++;
                            }

                            oos.writeObject(new Response(true,null,null,data));
                            break;
                        }
                        case "downloadFile":
                        {
                            String file_id = req.getParam();
                            File fs;
                            Doc doc;
                            FileInputStream fis=null;
                            DataOutputStream dos=null;

                            if((doc=DataProcessing.searchDoc(file_id))!=null){
                                fs = new File(this.upload_path+doc.getFilename());
                                fis = new FileInputStream(fs);
                                dos = new DataOutputStream(s.getOutputStream());

                                //Start to copyfile
                                oos.writeObject(new Response(true,null,null));
                                System.out.println("开始发送文件...");
                                dos.writeLong(fs.length());

                                final int buffer_size = 1024 * 1024;
                                int rLength;
                                byte[] buffer = new byte[buffer_size];
                                while((rLength=fis.read(buffer))!=-1){
                                    dos.write(buffer,0,rLength);
                                    dos.flush();
                                }
                                System.out.println("Done!!");

                                oos.writeObject(new Response(true,null,"copy over"));
                                fis.close();
                            }else{
                                oos.writeObject(new Response(false,null,"cannot find file!!"));
                            }
                            break;
                        }

                        default:
                            break;
                    }
                }
            }
        }catch (Exception err){
            err.printStackTrace();
        }finally {
            if(ois!=null){
                ois.close();
            }
            if(oos!=null){
                oos.close();
            }
            if(s!=null){
                s.close();
            }
            if(ss!=null){
                ss.close();
            }

            (new DataProcessing()).disconnectFromDB();
        }
    }
}
