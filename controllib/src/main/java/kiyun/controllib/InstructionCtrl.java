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

public class InstructionCtrl extends LinearLayout {
    private String strText;
    private int m_instruction;

    public String getStrText() {
        return strText;
    }

    public void setStrText(String strText) {
        this.strText = strText;

        TextView txtView= (TextView)findViewById(R.id.textView);
        txtView.setText(strText);
    }

    public void setM_instruction(int minstruction) {
        m_instruction = minstruction;
        ImageView m_ImageView=(ImageView)findViewById(R.id.ImageView);

        if(m_instruction ==0)
        {
            m_ImageView.setImageResource(R.drawable.instruction_del);
        }
        else if(m_instruction ==1)
        {
            m_ImageView.setImageResource(R.drawable.instruction_add);
        }
        else if(m_instruction ==2)
        {
            m_ImageView.setImageResource(R.drawable.instruction_no);
        }
    }

    public InstructionCtrl(Context context, AttributeSet attrs) {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.instruction, this);
        TypedArray ta=getContext().obtainStyledAttributes(attrs,R.styleable.control);
        int count=ta.getIndexCount();
        for(int a=0;a<count;a++) {
            int itemID = ta.getIndex(a);
            if (itemID == R.styleable.control_txtText) {
                String str = ta.getString(itemID);
                this.strText = str;
                setStrText(str);
            }

            if (itemID == R.styleable.control_inStruction) {
                m_instruction = ta.getInt(itemID, 0);
                setM_instruction(m_instruction);
            }
        }


        invalidate();
        ta.recycle();

    }
}
