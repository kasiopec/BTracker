package dtu.dk.budgetracker;

/**
 * Created by Kasio on 09.11.2016.
 */

public class Expenses {
    int id;
    String date;
    int amount;
    String shop;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    String address;

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Expenses(){

    }

    public Expenses(int id, String date, int amount){
        this.id= id;
        this.date= date;
        this.amount = amount;
    }

    public Expenses (String date, int amount, String shop, String address){
        this.date = date;
        this.amount = amount;
        this.shop = shop;
        this.address = address;
    }


}
