package com.example.caminin.secured_client;

import java.math.BigInteger;

/**
 * Created by alex on 26/01/17.
 */
public class Key {
    public BigInteger getN() {
        return n;
    }

    BigInteger n;


    public Key(BigInteger n) {
        this.n = n;
    }
}
