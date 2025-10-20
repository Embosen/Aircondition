package kiyun.controllib;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import kiyun.controllib.R;

/**
 * Created by ChenCe on 2017/12/1.
 */

public class DataCtrl extends LinearLayout {

    private String strText;
    private float fData;
    private int DecimalWidth;

    public String getStrText() {
        return strText;
    }

    public void setStrText(String strText) {
        this.strText = strText;

        TextView txtView= (TextView)findViewById(R.id.textView);
        txtView.setText(strText);
    }

    public void setData(float data) {
        fData = data;

        TextView editText= (TextView)findViewById(R.id.dataView);
        String str=String.valueOf(data);
        DecimalFormat df1=new DecimalFormat("0.##");
        df1.setMaximumFractionDigits(DecimalWidth);
        str=df1.format(data);
        editText.setText(str);
    }

    public DataCtrl(Context context, AttributeSet attrs) {
        super(context,attrs);
        LayoutInflater.from(context).inflate(kiyun.controllib.R.layout.datactrl,this);
        TypedArray ta=getContext().obtainStyledAttributes(attrs,R.styleable.control);
        int count=ta.getIndexCount();
        for(int a=0;a<count;a++)
        {
            int itemID=ta.getIndex(a);
            if(itemID==R.styleable.control_txtText) {
                String str=ta.getString(itemID);
                this.strText=str;
                setStrText(str);
            }

            if(itemID==R.styleable.control_fData) {
                float fd=0;
                fData=ta.getFloat(itemID,fd);
                float fhh=fData;
                setData(fhh);
            }

            if(itemID==R.styleable.control_DecimalWidth){
                DecimalWidth=ta.getInt(itemID,1);
            }

        }

        invalidate();
        ta.recycle();
    }

}
