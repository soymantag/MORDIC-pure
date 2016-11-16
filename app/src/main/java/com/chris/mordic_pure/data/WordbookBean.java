package com.chris.mordic_pure.data;

/**
 * Created by chris on 7/4/16.
 * Email: soymantag@163.coom
 */
public class WordbookBean {
    private String bookName;
    private int index_disordered;
    private int index_ordered;
    private int sum;


    public WordbookBean() {
    }

    public WordbookBean(String bookName, int index_disordered, int index_ordered, int sum) {
        this.bookName = bookName;
        this.index_disordered = index_disordered;
        this.index_ordered = index_ordered;
        this.sum = sum;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getIndex_disordered() {
        return index_disordered;
    }

    public void setIndex_disordered(int index_disordered) {
        this.index_disordered = index_disordered;
    }

    public int getIndex_ordered() {
        return index_ordered;
    }

    public void setIndex_ordered(int index_ordered) {
        this.index_ordered = index_ordered;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
