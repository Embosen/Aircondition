package kiyun.dataprocess;

/**
 * Created by ChenCe on 2017/12/17.
 */

public class RawdataProcess{

    public float get4ByteValue( byte l1,byte l2,byte l3,byte l4,float min, float max) {
        int Int4 = l4& 0xFF;
        int Int3 = l3& 0xFF;
        int Int2 = l2& 0xFF;
        int Int1 = l1& 0xFF;

        int move4= Int4<<24;
        int move3= Int3<<16;
        int move2= Int2<<8;
        int move1= Int1<<0;

        int rltI = move4 | move3 | move2 | move1;
        float rlt = rltI;

        float x = min + (rlt) * (max - min) / 0xFFFF;
        return x;
    }

    public int get4ByteValueInt(byte l1,byte l2,byte l3,byte l4)
    {
        int Int4 = l4& 0xFF;
        int Int3 = l3& 0xFF;
        int Int2 = l2& 0xFF;
        int Int1 = l1& 0xFF;

        int move4= Int4<<24;
        int move3= Int3<<16;
        int move2= Int2<<8;
        int move1= Int1<<0;

        int rltI = move4 | move3 | move2 | move1;

        return rltI;
    }

    public int get2ByteValueInt(byte low,byte high)
    {
        int hInt = high & 0xFF;
        int lInt = low & 0xFF;
        int moved = hInt << 8;
        int rltI = moved | lInt;

        return rltI;
    }


    public float get2ByteValue( byte low,byte high,float min, float max) {
        int hInt = high & 0xFF;
        int lInt = low & 0xFF;
        int moved = hInt << 8;
        int rltI = moved | lInt;
        float rlt = moved | lInt;

        float x = min + (rlt) * (max - min) / 65535f;
        return x;
    }
}
