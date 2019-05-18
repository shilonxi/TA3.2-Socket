package android.shilon.server;

import java.net.*;
import java.io.*;
import java.util.*;

public class TcpServer
{
    public static ArrayList<Socket> socketArrayList=new ArrayList<>();
    //定义一个动态数组存放socket连接
    public static void main(String[] args)
            throws IOException
    {
        ServerSocket serverSocket=new ServerSocket(30000);
        while (true)
        {
            Socket socket=serverSocket.accept();
            System.out.println("new!");
            socketArrayList.add(socket);
            //添加但无删减
            new Thread(new ServerThread(socket)).start();
            //启动线程服务
        }
    }
}
