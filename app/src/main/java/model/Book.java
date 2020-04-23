package model;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Restaurant POJO.
 */
@IgnoreExtraProperties
public class Book {

    public static final String FIELD_NAME = "bookName";
    public static final String FIELD_BRANCH = "branch";
    public static final String FIELD_YEAR = "courseYear";
    public static final String FIELD_SUBJECT = "subject";
    public static final String FIELD_PUBLISHER="publisher";
    public static final String FIELD_SELLER="sellerName";
    public static final String FIELD_SELLER_CONTACT="sellerContact";
    public static final String FIELD_PRICE="price";
    public static final String FIELD_PHOTO="photoURL";
    public static final String FIELD_SORT_HTL="htl";
    public static final String FIELD_SORT_LTH="lth";
    public static final String FIELD_COMMENT="comment";

    private String bookName;
    private String branch;
    private String courseYear;
    private String photo;
    private int price;
    private int discount;
    private float actualPrice;
    private String subject;
    private String publisher;
    private String sellerName;
    private String sellerContact;
    private String comment;
    private String uid;
    private boolean sold=false;

    public Book(String bookName, String branch, String courseYear, String photo, int price, int discount, float actualPrice, String subject, String publisher, String sellerName, String sellerContact, String comment,String uid,boolean sold) {
        this.bookName = bookName;
        this.branch = branch;
        this.courseYear = courseYear;
        this.photo = photo;
        this.price = price;
        this.discount = discount;
        this.actualPrice = actualPrice;
        this.subject = subject;
        this.publisher = publisher;
        this.sellerName = sellerName;
        this.sellerContact = sellerContact;
        this.comment = comment;
        this.uid=uid;
        this.sold=sold;
    }

    public String getUid() {
        return uid;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public float getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(float actualPrice) {
        this.actualPrice = actualPrice;
    }

    public Book() {}


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCourseYear() {
        return courseYear;
    }

    public void setCourseYear(String courseYear) {
        this.courseYear = courseYear;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerContact() {
        return sellerContact;
    }

    public void setSellerContact(String sellerContact) {
        this.sellerContact = sellerContact;
    }
}
