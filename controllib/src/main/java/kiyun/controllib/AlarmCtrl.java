package kiyun.controllib;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ChenCe on 2017/12/1.
 */

public class AlarmCtrl extends LinearLayout {
    private String strText;
    private boolean m_beAlarm;

    public String getStrText() {
        return strText;
    }

    public void setStrText(String strText) {
        this.strText = strText;

        TextView txtView= (TextView)findViewById(R.id.textView);
        txtView.setText(strText);
    }

    public void setAlarm(boolean beAlarm) {
        m_beAlarm = beAlarm;
        ImageView m_ImageView=(ImageView)findViewById(R.id.ImageView);

        if(m_beAlarm ==true )
        {
            m_ImageView.setImageResource(R.drawable.bon);
        }
        else
        {
            m_ImageView.setImageResource(R.drawable.boff);
        }
    }

    public AlarmCtrl(Context context, AttributeSet attrs) {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.alarmctrl, this);
        TypedArray ta=getContext().obtainStyledAttributes(attrs,R.styleable.control);
        int count=ta.getIndexCount();
        for(int a=0;a<count;a++) {
            int itemID = ta.getIndex(a);
            if (itemID == R.styleable.control_txtText) {
                String str = ta.getString(itemID);
                this.strText = str;
                setStrText(str);
            }

            if (itemID == R.styleable.control_beTrueOrOff) {
                m_beAlarm = ta.getBoolean(itemID, false);
                setAlarm(m_beAlarm);
            }
        }


        invalidate();
        ta.recycle();

    }
}
