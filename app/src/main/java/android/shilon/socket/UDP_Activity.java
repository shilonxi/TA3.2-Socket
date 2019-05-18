package android.shilon.socket;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDP_Activity extends AppCompatActivity
{
    public static final int PORT=30000;
    //定义多点广播目的端口
    private static final int LEN=4096;
    //定义数据报的最大大小
    private MulticastSocket socket=null;
    //定义要使用的MulticastSocket
    private InetAddress mAddress=null;
    //定义多点广播地址
    byte[] inBuff=new byte[LEN];
    //定义接受字节数组
    private DatagramPacket inPacket=new DatagramPacket(inBuff,inBuff.length);
    //以指定字节数组创建接受用的DatagramPacket
    private DatagramPacket outPacket=null;
    //定义发送用的DatagramPacket
    private EditText fa_text;
    private EditText shou_text;
    private Button faya;
    private EditText dip_text;
    private String line;
    MyTask task=new MyTask();
    //建立变量

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.udp_layout);
        fa_text=findViewById(R.id.fa_edit);
        shou_text=findViewById(R.id.shou_edit);
        faya=findViewById(R.id.fa_bt);
        dip_text=findViewById(R.id.dip_edit);
        //获取实例

        dip_text.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v,int actionId,KeyEvent event)
            {
                if(actionId== EditorInfo.IME_ACTION_SEND||actionId==EditorInfo.IME_ACTION_DONE||(event!=null&&KeyEvent.KEYCODE_ENTER==event.getKeyCode()&&KeyEvent.ACTION_DOWN==event.getAction()))
                {
                    clientReceive();
                    //启动线程，用于接收
                }
                return false;
            }
        });
        //用回车触发，确定多点广播地址

        faya.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clientSend();
                //启动线程，用于发送
            }
        });
        //监听
    }

    private void clientReceive()
    {
        task.execute();
        //启动后台线程
    }

    public class MyTask extends AsyncTask<Void,Void,String>
    {
        @Override
        protected void onPreExecute()
        {
            line=dip_text.getText().toString();
            //在后台工作前，在UI线程中得到地址信息
        }

        @Override
        protected String doInBackground(Void...param)
        {
            try
            {
                socket=new MulticastSocket(PORT);
                //创建MulticastSocket，因为该对象要同时用于发送和接受，所以使用指定端口
                mAddress=InetAddress.getByName(line);
                //初始化多点广播地址
                socket.joinGroup(mAddress);
                //加入指定的多点广播地址
                socket.setLoopbackMode(false);
                //设置发送的内容会被回送到自身
                while (true)
                {
                    if (isCancelled())
                        break;
                    //判断后台是否结束，结束则退出
                    socket.receive(inPacket);
                    //读取并放入
                    String string=new String(inBuff);
                    inBuff=new byte[LEN];
                    inPacket=new DatagramPacket(inBuff,inBuff.length);
                    show(string);
                    //显示
                }
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
            return "";
            //要有返回值，无实际意义
        }
        //后台工作
    }
    //定义后台线程

    private void show(final String response)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                shou_text.append(response+'\n');
                //更新显示
            }
        });
    }

    private void clientSend()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    byte[] buff=fa_text.getText().toString().getBytes();
                    //确定输出内容
                    outPacket=new DatagramPacket(new byte[0],0,mAddress,PORT);
                    //初始化发送用的DatagramPacket
                    outPacket.setData(buff);
                    //设置发送用的DatagramPacket的内容
                    socket.send(outPacket);
                    //发送
                } catch (IOException exc)
                {
                    exc.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        task.cancel(true);
        //关闭后台线程
        try
        {
            socket.leaveGroup(mAddress);
            //离开指定的多点广播地址
            socket.close();
            //关闭
        } catch (IOException io)
        {
            io.printStackTrace();
        }
    }
}

