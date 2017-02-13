package com.example.caminin.secured_client;

import java.math.BigInteger;

/**
 * Created by alex on 26/01/17.
 */
public class PublicKey extends Key {

    public static boolean DEBUG = false;

    public BigInteger getE() {
        return e;
    }

    private BigInteger e;

    public PublicKey(BigInteger n, BigInteger e) {
        super(n);
        this.e = e;
    }

    public BigInteger[] encryption(String message){
        byte[] in = message.getBytes();
        BigInteger[] out = new BigInteger[in.length];
        Log.debug(message + "-> (", DEBUG);
        byte[] temp = new byte[1];
        for(int i = 0; i < in.length; i++){
            temp[0] = in[i];
            out[i] = new BigInteger(temp).pow(e.intValue()).mod(n);
            Log.debug(out[i].toString()+ ",", DEBUG);
        }
        Log.debug(")", DEBUG);
//        for(int i = 0; i < out.length; i++) {
//            System.out.print(out[i]+" ");
//        }
        return out;
    }

    public static String BigIntergerToString(BigInteger plop[]){
        String res="";
        for(int i=0;i<plop.length;i++){
            res+=plop[i].toString();
            res=res.trim();
            if(i<(plop.length-1)){
                res+="/";
            }
        }
        res=res.trim();
        return res;
    }

    public static void main(String[] args) {
        PublicKey pkey = new PublicKey(BigInteger.valueOf(5141), BigInteger.valueOf(7));
        pkey.encryption("bonjour");
    }
}
