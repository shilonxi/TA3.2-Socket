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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class TCP_Activity extends AppCompatActivity
{
    private EditText send_text;
    private EditText receive_text;
    private Button fasong;
    private EditText ip_text;
    private Socket socket;
    private String line;
    MeTask task=new MeTask();
    //建立变量

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tcp_layout);
        send_text=findViewById(R.id.send_edit);
        receive_text=findViewById(R.id.receive_edit);
        fasong=findViewById(R.id.send_bt);
        ip_text=findViewById(R.id.ip_edit);
        //获取实例

        ip_text.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v,int actionId,KeyEvent event)
            {
                if (actionId==EditorInfo.IME_ACTION_SEND||actionId==EditorInfo.IME_ACTION_DONE||(event!=null&&KeyEvent.KEYCODE_ENTER==event.getKeyCode()&&KeyEvent.ACTION_DOWN==event.getAction()))
                {
                    clientReceive();
                    //启动线程，用于接收
                }
                return false;
            }
        });
        //用回车触发，确定目的IP地址

        fasong.setOnClickListener(new View.OnClickListener()
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

    public class MeTask extends AsyncTask<Void,Void,String>
    {
        @Override
        protected void onPreExecute()
        {
            line=ip_text.getText().toString();
            //在后台工作前，在UI线程中得到地址信息
        }

        @Override
        protected String doInBackground(Void...param)
        {
            try
            {
                socket=new Socket(line,30000);
                //建立连接
                InputStream in=socket.getInputStream();
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                //获取输入内容
                while (true)
                {
                    if (isCancelled())
                        break;
                    //判断后台是否结束，结束则退出
                    StringBuilder response=new StringBuilder();
                    response.append(reader.readLine());
                    //读取
                    show(response.toString());
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
                receive_text.append(response+'\n');
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
                PrintStream printStream;
                //建立变量
                try
                {
                    printStream=new PrintStream(socket.getOutputStream());
                    printStream.println(send_text.getText().toString());
                    //输出
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
            socket.close();
            //关闭
        } catch (IOException io)
        {
            io.printStackTrace();
        }
    }
}

