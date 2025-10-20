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

public class ToggleCtrl extends LinearLayout {
    private String strText;
    private boolean m_beOn;
    private int TrueImageType;

    public String getStrText() {
        return strText;
    }

    public void setStrText(String strText) {
        this.strText = strText;

        TextView txtView= (TextView)findViewById(R.id.textView);
        txtView.setText(strText);
    }

    public void setToggle(boolean beOn) {
        m_beOn = beOn;
        ImageView m_ImageView=(ImageView)findViewById(R.id.ImageView);

        if(m_beOn ==true )
        {
            if(TrueImageType==0)   m_ImageView.setImageResource(R.drawable.toggle_on);
            else if(TrueImageType==1) m_ImageView.setImageResource(R.drawable.refmodel);
        }
        else
        {
            if(TrueImageType==0)  m_ImageView.setImageResource(R.drawable.toggle_off);
            else if(TrueImageType==1) m_ImageView.setImageResource(R.drawable.funmodel);
        }
    }

    public ToggleCtrl(Context context, AttributeSet attrs) {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.togglectrl, this);
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
                m_beOn = ta.getBoolean(itemID, false);
                setToggle(m_beOn);
            }

            if (itemID == R.styleable.control_TrueImageType) {
                TrueImageType = ta.getInteger(itemID, 0);
                setToggle(m_beOn);
            }
        }


        invalidate();
        ta.recycle();

    }
}
