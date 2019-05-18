package android.shilon.socket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Main_Activity extends AppCompatActivity
{
    private RadioGroup radioGroup;
    private RadioButton tcp;
    private RadioButton udp;
    //建立变量

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        radioGroup=findViewById(R.id.rg);
        tcp=findViewById(R.id.tcp_bt);
        udp=findViewById(R.id.udp_bt);
        //获取实例
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                if (checkedId==tcp.getId())
                {
                    Toast.makeText(Main_Activity.this,"即将进入TCP单播界面……",Toast.LENGTH_SHORT).show();
                    //通知
                    radioGroup.clearCheck();
                    //清空
                    Intent intent=new Intent(Main_Activity.this,TCP_Activity.class);
                    startActivity(intent);
                    //切换
                }
                else if (checkedId==udp.getId())
                {
                    Toast.makeText(Main_Activity.this,"即将进入UDP多播界面……",Toast.LENGTH_SHORT).show();
                    //通知
                    radioGroup.clearCheck();
                    //清空
                    Intent intent=new Intent(Main_Activity.this,UDP_Activity.class);
                    startActivity(intent);
                    //切换
                }
            }
        });
        //监听
    }
}
