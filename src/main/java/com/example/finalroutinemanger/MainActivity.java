package com.example.finalroutinemanger;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button listen, listDevices,install,startRoutine,stop,getRoutine,getApps,switchRoutine,deleteApp;
    ListView listView,listView2;
    TextView msg_box,status;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;

    SendReceive sendReceive;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;

    int REQUEST_ENABLE_BLUETOOTH=1;

    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    byte[] delimiter= {0x1E,0x1E,0x1E};//record separator
    byte[] readBuff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewByIdes();
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
        }

        implementListeners();
    }
    private void implementListeners() {

        listDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<BluetoothDevice> bt=bluetoothAdapter.getBondedDevices();
                String[] strings=new String[bt.size()];
                btArray=new BluetoothDevice[bt.size()];
                int index=0;

                if( bt.size()>0)
                {
                    for(BluetoothDevice device : bt)
                    {
                        btArray[index]= device;
                        strings[index]=device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerClass serverClass=new ServerClass();
                serverClass.start();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClientClass clientClass=new ClientClass(btArray[i]);
                clientClass.start();

                status.setText("Connecting");
            }
        });

        //Start Routine button click
        startRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] soh = new byte[1];
                soh[0] = 0x01;
                sendReceive.write(soh);
                //int a = bytes.length;
                byte[] hex2 = new byte[2];
                hex2[0] = 0x00;
                hex2[1] = 0x00;
                int command_name = hex2.length;
                int totalBytes= command_name;
                byte[] array = new byte[4];
                for (int i =0; i <4 ; i++)
                {
                    array[3-i] = (byte) (totalBytes >>> (i*8));
                }
                sendReceive.write(array);
                sendReceive.write(hex2);
            }
        });
        //stop button click
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] soh = new byte[1];
                soh[0] = 0x01;
                sendReceive.write(soh);
                //int a = bytes.length;
                byte[] hex2 = new byte[2];
                hex2[0] = 0x00;
                hex2[1] = 0x01;
                int command_name = hex2.length;
                int totalBytes= command_name;
                byte[] array = new byte[4];
                for (int i =0; i <4 ; i++)
                {
                    array[3-i] = (byte) (totalBytes >>> (i*8));
                }
                sendReceive.write(array);
                sendReceive.write(hex2);
            }
        });

        //get routine button click
        getRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] soh = new byte[1];
                soh[0] = 0x01;
                sendReceive.write(soh);
                //int a = bytes.length;
                byte[] hex2 = new byte[2];
                hex2[0] = 0x00;
                hex2[1] = 0x02;
                int command_name = hex2.length;
                int totalBytes= command_name;
                byte[] array = new byte[4];
                for (int i =0; i <4 ; i++)
                {
                    array[3-i] = (byte) (totalBytes >>> (i*8));
                }
                sendReceive.write(array);
                sendReceive.write(hex2);

            }
        });
        //get app button click
        getApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] soh = new byte[1];
                soh[0] = 0x01;
                sendReceive.write(soh);
                //int a = bytes.length;
                byte[] hex2 = new byte[2];
                hex2[0] = 0x00;
                hex2[1] = 0x04;
                int command_name = hex2.length;
                int totalBytes= command_name;
                byte[] array = new byte[4];
                for (int i =0; i <4 ; i++)
                {
                    array[3-i] = (byte) (totalBytes >>> (i*8));
                }
                sendReceive.write(array);
                sendReceive.write(hex2);
            }
        });
        //switch routine button click
        switchRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] soh = new byte[1];
                soh[0] = 0x01;
                sendReceive.write(soh);
                //int a = bytes.length;
                byte[] hex2 = new byte[2];
                hex2[0] = 0x00;
                hex2[1] = 0x03;
                int command_name = hex2.length;
                String routine_name = "fireDemo";
                int routine_name_len = routine_name.length();
                int totalBytes= command_name + routine_name_len;
                byte[] array = new byte[4];
                for (int i =0; i <4 ; i++)
                {
                    array[3-i] = (byte) (totalBytes >>> (i*8));
                }
                sendReceive.write(array);
                sendReceive.write(hex2);
                sendReceive.write(routine_name.getBytes());
            }
        });
        //delete app button click
        deleteApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] soh = new byte[1];
                soh[0] = 0x01;
                sendReceive.write(soh);
                //int a = bytes.length;
                byte[] hex2 = new byte[2];
                hex2[0] = 0x00;
                hex2[1] = 0x06;
                int command_name = hex2.length;
                String routine_name = "FIRE DEMO";
                int routine_name_len = routine_name.length();
                int totalBytes= command_name + routine_name_len;
                byte[] array = new byte[4];
                for (int i =0; i <4 ; i++)
                {
                    array[3-i] = (byte) (totalBytes >>> (i*8));
                }
                sendReceive.write(array);
                sendReceive.write(hex2);
                sendReceive.write(routine_name.getBytes());
            }
        });

        // install click
        //SOH(1 byte)  + header length + data (command name 0x0005)+ file.xml + us (0x002) + image + eof
        install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.install_app_icon);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] imageBytes = stream.toByteArray();
                int f = imageBytes.length;
              String file = "<App>\n" +
                      "  <Name>installAppTest</Name>\n" +
                      "  <Tools>\n" +
                      "    <Tool>\n" +
                      "      <Name>myLeds</Name>\n" +
                      "      <Module>leds</Module>\n" +
                      "      <Class>\n" +
                      "        <Name>LedSet</Name>\n" +
                      "        <Parameters/>\n" +
                      "      </Class>\n" +
                      "    </Tool>\n" +
                      "  </Tools>\n" +
                      "  <Tasks>\n" +
                      "    <Task>\n" +
                      "      <Name>task1</Name>\n" +
                      "      <Command>\n" +
                      "        <Name>blinkLED</Name>\n" +
                      "        <Tool>myLeds</Tool>\n" +
                      "        <Parameters>\n" +
                      "          <Param>\n" +
                      "            <Val>green</Val>\n" +
                      "            <Type>str</Type>\n" +
                      "          </Param>\n" +
                      "          <Param>\n" +
                      "            <Val>1</Val>\n" +
                      "            <Type>int</Type>\n" +
                      "          </Param>\n" +
                      "        </Parameters>\n" +
                      "      </Command>\n" +
                      "    </Task>\n" +
                      "    <Task>\n" +
                      "      <Name>task2</Name>\n" +
                      "      <Command>\n" +
                      "        <Name>blinkLED</Name>\n" +
                      "        <Tool>myLeds</Tool>\n" +
                      "        <Parameters>\n" +
                      "          <Param>\n" +
                      "            <Val>white</Val>\n" +
                      "            <Type>str</Type>\n" +
                      "          </Param>\n" +
                      "          <Param>\n" +
                      "            <Val>1</Val>\n" +
                      "            <Type>int</Type>\n" +
                      "          </Param>\n" +
                      "        </Parameters>\n" +
                      "      </Command>\n" +
                      "    </Task>\n" +
                      "    <Task>\n" +
                      "      <Name>task3</Name>\n" +
                      "      <Command>\n" +
                      "        <Name>blinkLED</Name>\n" +
                      "        <Tool>myLeds</Tool>\n" +
                      "        <Parameters>\n" +
                      "          <Param>\n" +
                      "            <Val>red</Val>\n" +
                      "            <Type>str</Type>\n" +
                      "          </Param>\n" +
                      "          <Param>\n" +
                      "            <Val>1</Val>\n" +
                      "            <Type>int</Type>\n" +
                      "          </Param>\n" +
                      "        </Parameters>\n" +
                      "      </Command>\n" +
                      "    </Task>\n" +
                      "    <Task>\n" +
                      "      <Name>task4</Name>\n" +
                      "      <Command>\n" +
                      "        <Name>blinkLED</Name>\n" +
                      "        <Tool>myLeds</Tool>\n" +
                      "        <Parameters>\n" +
                      "          <Param>\n" +
                      "            <Val>blue</Val>\n" +
                      "            <Type>str</Type>\n" +
                      "          </Param>\n" +
                      "          <Param>\n" +
                      "            <Val>1</Val>\n" +
                      "            <Type>int</Type>\n" +
                      "          </Param>\n" +
                      "        </Parameters>\n" +
                      "      </Command>\n" +
                      "    </Task>\n" +
                      "    <Task>\n" +
                      "      <Name>task5</Name>\n" +
                      "      <Command>\n" +
                      "        <Name>blinkLED</Name>\n" +
                      "        <Tool>myLeds</Tool>\n" +
                      "        <Parameters>\n" +
                      "          <Param>\n" +
                      "            <Val>yellow</Val>\n" +
                      "            <Type>str</Type>\n" +
                      "          </Param>\n" +
                      "          <Param>\n" +
                      "            <Val>1</Val>\n" +
                      "            <Type>int</Type>\n" +
                      "          </Param>\n" +
                      "        </Parameters>\n" +
                      "      </Command>\n" +
                      "    </Task>\n" +
                      "  </Tasks>\n" +
                      "</App>\n";
                int g = file.getBytes().length;
                byte[] bytes = new byte[1];
                bytes[0] = 0x01;
                //int a = bytes.length;
                byte[] hex2 = new byte[2];
                hex2[0] = 0x00;
                hex2[1] = 0x05;
                int b = hex2.length;
                byte[] hex4 = {0x1F,0x1F,0x1F};
                int c = hex4.length;
                byte[] hex5 = {0x1E,0x1E,0x1E};
                int d = hex5.length;
               String string = "installAppTest.xml";
               int e = string.length();
               String string5= "icon.jpeg";
               int h = string5.length();
               int totalBytes= b + e + c + g + d + h + c + f;
               byte[] array = new byte[4];

               for (int i =0; i <4 ; i++)
               {
                   array[3-i] = (byte) (totalBytes >>> (i*8));
               }
                sendReceive.write(bytes);//sending SOH
                //byte[] array = s.getBytes();
                sendReceive.write(array);//sending data length
                sendReceive.write(hex2);//sending command name

                sendReceive.write(string.getBytes());//sending file name
                sendReceive.write(hex4);//sending unit separator
                sendReceive.write(file.getBytes());//sending file
                //byte[] hex4 = {0x1E};
                sendReceive.write(hex5);//sending record separator

                sendReceive.write(string5.getBytes());//sending image file name
                sendReceive.write(hex4);//sending unit separator
                //sendReceive.write("image".getBytes());//for testing purpose
                sendReceive.write(imageBytes);//sending image file
            }
        });
    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what)
            {
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    readBuff= (byte[]) msg.obj;
                    List<byte[]> parsed = SplitBytesByDelimiter(delimiter,readBuff);
                    String get_routine_command = "GET_ROUTINES";
                    String get_app_command = "GET_APPS";
                    byte[] cmdEncoding = parsed.get(0);
                    String cmd = new String(cmdEncoding);
                    //msg_box.setText(cmd);
                    if(cmd.equals(get_routine_command)){
                        msg_box.setText("GET ROUTINE");
                        List<byte[]> encodedRoutines = parsed.subList(1, parsed.size());
                        getRoutineReceive(encodedRoutines);
                    }else if (cmd.equals(get_app_command)){
                        //msg_box.setText("GET");
                        List<byte[]> encodedRoutines = parsed.subList(1, parsed.size());
                        String[] apps = new String[encodedRoutines.size()-1];
                        //msg_box.setText(String.valueOf(encodedRoutines.size()+1));
                        for (int i = 0; i<encodedRoutines.size()-1; i++) {
                            apps[i] = new String(encodedRoutines.get(i+i));
                        }
                        msg_box.setText(apps[0]);
                        ArrayAdapter<String> arrayMsg=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,apps);
                        listView2.setAdapter(arrayMsg);

                       // Bitmap bitmap=BitmapFactory.decodeByteArray(encodedRoutines.get(1),0,msg.arg1);

                        //imageView.setImageBitmap(bitmap);//list it in imageview

                    }else{
                        //msg_box.setText("NOTHING");
                    }
                    break;
            }
            return true;
        }
    });

    private void getRoutineReceive(List<byte[]> encodedRoutines){
        String[] routines = new String[encodedRoutines.size()];
        for (int i = 0; i<encodedRoutines.size(); i++) {
            routines[i] = new String(encodedRoutines.get(i));
        }
        ArrayAdapter<String> arrayMsg=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,routines);
        listView2.setAdapter(arrayMsg);
    }


    public static boolean isMatch(byte[] pattern, byte[] input, int pos) {
        for(int i=0; i< pattern.length; i++) {
            if(pattern[i] != input[pos+i]) {
                return false;
            }
        }
        return true;
    }

    public static List<byte[]> SplitBytesByDelimiter(byte[] pattern, byte[] input) {
        List<byte[]> l = new LinkedList<byte[]>();
        int blockStart = 0;
        for(int i=0; i<input.length; i++) {
            if(isMatch(pattern,input,i)) {
                l.add(Arrays.copyOfRange(input, blockStart, i));
                blockStart = i+pattern.length;
                i = blockStart;
            }
        }
        l.add(Arrays.copyOfRange(input, blockStart, input.length ));
        return l;
    }

    private void findViewByIdes() {
        install = (Button)findViewById(R.id.install);
        startRoutine = (Button)findViewById(R.id.start_routine);
        stop = (Button)findViewById(R.id.stopRoutine);
        getApps = (Button)findViewById(R.id.getApps);
        msg_box = (TextView)findViewById(R.id.msgBox);
        switchRoutine = (Button)findViewById(R.id.switchRoutine);
        deleteApp = (Button)findViewById(R.id.deleteApp);
        listen=(Button) findViewById(R.id.listen);
        listView=(ListView) findViewById(R.id.listview);
        listView2 = (ListView)findViewById(R.id.listView2);
        status=(TextView) findViewById(R.id.status);
        listDevices=(Button) findViewById(R.id.listDevices);
        getRoutine = (Button)findViewById(R.id.getRoutine);
    }

    private class ServerClass extends Thread
    {
        private BluetoothServerSocket serverSocket;

        public ServerClass(){
            try {
                serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            BluetoothSocket socket=null;

            while (socket==null)
            {
                try {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket=serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if(socket!=null)
                {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive=new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread
    {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass (BluetoothDevice device1)
        {
            device=device1;

            try {
                socket=device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            try {
                socket.connect();
                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive=new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive (BluetoothSocket socket)
        {
            bluetoothSocket=socket;
            InputStream tempIn=null;
            OutputStream tempOut=null;

            try {
                tempIn=bluetoothSocket.getInputStream();
                tempOut=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=tempIn;
            outputStream=tempOut;
        }

        public void run()
        {
            byte[] buffer=new byte[11024];
            int bytes;

            while (true)
            {
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}





