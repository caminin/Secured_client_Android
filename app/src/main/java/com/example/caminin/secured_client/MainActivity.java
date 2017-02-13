package com.example.caminin.secured_client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity implements Secured_Client.SecuredInterface {

    private static boolean debug=true;
    private Secured_Client client;
    private PublicKey publicKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        askClientName();
    }

    public void askClientName(){
        Log.debug("je demande le nom au client",debug);
        final String[] client_name = {""};
        final MainActivity activity=this;
        Log.debug("je lance la boite de dialog",debug);
        new LovelyTextInputDialog(this)
                .setTopColorRes(R.color.holo_pink_light)
                .setIcon(R.drawable.ic_delete_black_36dp)
                .setTitle("Entrez votre nom")
                .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {
                        client_name[0]=text;
                        if(client_name[0].equals("")){
                            askClientName();
                            Toast.makeText(getApplicationContext(), "Votre nom ne peut être vide", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            client = new Secured_Client(client_name[0],activity);

                            askIp();
                        }
                    }
                })
                .show();


    }

    public void askIp(){
        Log.debug("je demande l'ip de l'autre",debug);
        final String[] ip = {""};
        final MainActivity activity=this;
        Log.debug("je lance la boite de dialog",debug);
        new LovelyTextInputDialog(this)
                .setTopColorRes(R.color.holo_pink_light)
                .setIcon(R.drawable.ic_delete_black_36dp)
                .setTitle("Entrez l'adresse Ip de l'autre")
                .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {
                        ip[0]=text;
                        if(ip[0].equals("")){
                            askIp();
                            Toast.makeText(getApplicationContext(), "L'ip ne peut être vide", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            client.newClient(ip[0]);
                            client.sendPublicKey();
                            Toast.makeText(activity,"Mon ip est "+getLocalIpAddress(),Toast.LENGTH_SHORT).show();
                            client.execute();
                        }
                    }
                })
                .show();
    }

    public void sendMessage(View v){
        String message;
        EditText text=(EditText)findViewById(R.id.text_message);
        message=text.getText().toString();
        if(!message.isEmpty()){
            if(publicKey!=null){
                addText(message);
                message=PublicKey.BigIntergerToString(publicKey.encryption(message));
                text.setText("");
                client.sendMessage(client.getClientName() +":"+message);
            }
            else{//Si on n'a pas la clé, on la demande
                client.askPublicKey();
                Toast.makeText(this,"Je n'ai pas de clé publique pour envoyer, demande en cours. Réessayez dans quelques secondes",Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addText(String message){
        ((TextView)findViewById(R.id.list_message)).append(message+"\n");
    }

    public String getLocalIpAddress(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if(inetAddress.getHostAddress().contains("192.168")){
                            return inetAddress.getHostAddress();
                        }
                        Log.debug(inetAddress.getHostAddress().toString(),debug);
                    }
                }
            }
        } catch (Exception ex) {
        }
        return "localhost";
    }

    public void setPublicKey(PublicKey key){
        publicKey=key;
    }

}
