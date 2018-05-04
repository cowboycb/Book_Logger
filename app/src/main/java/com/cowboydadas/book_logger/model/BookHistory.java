package com.cowboydadas.book_logger.model;

/**
 * Created by admin on 8.01.2018.
 */

public class BookHistory {

    private Long id;
    private Long bookId;
    private Integer readPage;
    private Integer remainingPage;
    private Integer iday;
    private Integer imonth;
    private Integer iyear;

    public BookHistory() {
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Integer getReadPage() {
        return readPage;
    }

    public void setReadPage(Integer readPage) {
        this.readPage = readPage;
    }

    public Integer getRemainingPage() {
        return remainingPage;
    }

    public void setRemainingPage(Integer remainingPage) {
        this.remainingPage = remainingPage;
    }

    public Integer getIday() {
        return iday;
    }

    public void setIday(Integer iday) {
        this.iday = iday;
    }

    public Integer getImonth() {
        return imonth;
    }

    public void setImonth(Integer imonth) {
        this.imonth = imonth;
    }

    public Integer getIyear() {
        return iyear;
    }

    public void setIyear(Integer iyear) {
        this.iyear = iyear;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
