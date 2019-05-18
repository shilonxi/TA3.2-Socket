package android.shilon.server;

import java.net.*;
import java.io.*;
import java.util.*;

public class ServerThread implements Runnable
{
    public static ArrayList<Socket> socketDelList=new ArrayList<>();
    //定义一个动态数组存放socket连接
    Socket socket;
    BufferedReader reader;
    public ServerThread(Socket socket)
            throws IOException
    {
        this.socket=socket;
        //传参
        reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //读入数据
    }
    public void run()
    {
        try
        {
            String content;
            while ((content=reader.readLine())!=null)
            {
                System.out.println("receive:");
                System.out.println(content);
                for (Socket socket:TcpServer.socketArrayList)
                {
                    try
                    {
                        socket.sendUrgentData(0xFF);
                        //测试客户端连接是否断开
                    } catch (IOException exc)
                    {
                        socketDelList.add(socket);
                        //废弃的socket加入删除列表
                    }
                    PrintStream printStream=new PrintStream(socket.getOutputStream());
                    printStream.println(content);
                }
                //foreach遍历发送
                TcpServer.socketArrayList.removeAll(socketDelList);
                //更新列表，此操作不能在遍历过程中完成
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
