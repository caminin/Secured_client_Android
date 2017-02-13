package com.example.caminin.secured_client;

import android.os.AsyncTask;

import java.math.BigInteger;

/**
 * Created by caminin on 31/01/17.
 */
public class Secured_Client extends AsyncTask<Void,String,Void> {
    private static boolean debug=true;

    private String client_name;
    private Message message;
    private MainActivity activity;
    private String to_add;
    private String other_ip ="localhost";

    private PublicKey publicKey;   // clé publique de l'interlocuteur utilisée pour crypter les messages que l'on envoit
    private PrivateKey privateKey; // clé privé servant à décrypter les messages envoyés par l'interlocuteur


    public Secured_Client(String client_name,MainActivity activity){
        this.client_name=client_name;
        this.activity=activity;
        to_add="";
    }

    public String getClientName(){
        return client_name;
    }

    public void newClient(String ip){
        other_ip=ip;
        message=new Message(other_ip);
        message.receive(this);
    }

    public void sendMessage(String message){
        this.message.send(message);
    }

    public void sendPublicKey(){
        Security security = new Security();
        security.genKeys();
        privateKey = security.getPrivateKey();
        sendMessage(":init://"+ client_name +":"+security.getPublicKey().getN().toString()+"/"+security.getPublicKey().getE());
    }

    public void askPublicKey(){
        sendMessage(":ask://");
    }

    /**
     * Réceptionne l'ensemble des messages reçus et les traite
     * @param message, chaine de caractères contenant un message reçu
     */
    public void handleMessage(String message){
        Log.debug("je reçois un message",debug);
        /* réception du message d'initialisation contenant la clé publique de l'interlocuteur */
        if(message.contains(":init://")){
            String name_and_stringKey = message.replace(":init://","");
            String array_string[]=name_and_stringKey.split(":");
            String stringKey=array_string[1];
            String[] splitedKey = stringKey.split("/");
            activity.setPublicKey(new PublicKey(new BigInteger(splitedKey[0].trim()), new BigInteger(splitedKey[1].trim())));
        }
        else if(message.contains(":ask://")){
            sendPublicKey();
            Log.debug("Je lui envoie ma public key",debug);
        }

        /* décrypte les autres messages et les affiche */
        else{
            String name=message.substring(0,message.indexOf(":"));
            String crypted_message=message.substring(message.indexOf(":")+1);
            String uncrypted_message;
            if(PrivateKey.isEncrypted(crypted_message)){
                uncrypted_message= privateKey.decryption(PrivateKey.splitString(crypted_message));
                to_add=name+":"+uncrypted_message;
            }
            else{
                Log.debug("le message n'est pas encrypté",debug);
                sendPublicKey();
            }
        }

    }


    @Override
    protected Void doInBackground(Void... params) {
        while(true){
            if(to_add.isEmpty() == false){
                publishProgress(to_add);
                to_add="";
            }
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        ((SecuredInterface)activity).addText(values[0]);
    }


    public interface SecuredInterface {
        void addText(String message);
    }
}