package com.wkyle.bankrecord.models;

import java.text.NumberFormat;
import java.util.Locale;

public class ClientModel {

	private int cid;
	private int tid;
	private double balance;
	private String balanceStr;

    /* getters & setters */
    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getTid() {
        return tid;
    }

    public Double getBalance() {
        return balance;
    }

	public void setBalance(Double balance) {
		NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
		this.balanceStr = nf.format(balance);
		this.balance = balance;
	}

	public String getBalanceStr() {
		return this.balanceStr;
	}
}
