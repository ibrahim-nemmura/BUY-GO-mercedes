package com.example.buygo;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.example.buygo.R;
import model.Book;

import com.google.firebase.firestore.Query;

/**
 * Object for passing filters around.
 */
public class Filters {




    private String branch = null;
    private String year = null;
    private String subject = null;
    private int  price = -1;
    private String sortBy = null;
    private Query.Direction sortDirection = null;


    public Filters() {}

    public static Filters getDefault() {
        Filters filters = new Filters();
        filters.setSortBy(Book.FIELD_PRICE);
        filters.setSortDirection(Query.Direction.ASCENDING);

        return filters;
    }
    public boolean hasSubject() { return !(TextUtils.isEmpty(subject));
    }

    public boolean hasBranch() {
        return !(TextUtils.isEmpty(branch));
    }

    public boolean hasYear() {
        return !(TextUtils.isEmpty(year));
    }

    public boolean hasPrice() {
        return (price>0);
    }

    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Query.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSearchDescription(Context context) {
        StringBuilder desc = new StringBuilder();

        if (branch == null && year == null && subject==null) {
            desc.append("<b>");
            desc.append(context.getString(R.string.all_books));
            desc.append("</b>");

        }

        if (branch != null) {
            desc.append("<b>");
            desc.append(branch);
            desc.append("</b>");
        }

        if (branch != null && year != null) {
            desc.append(", ");
        }

        if (year != null) {
            desc.append("<b>");
            desc.append(year);
            desc.append("</b>");
        }
        if ((year!=null || branch!=null) && subject!=null)
        {
            desc.append(", ");
        }

        if (subject != null) {
            desc.append("<b>");
            Log.d("Filters",subject);
            desc.append(subject);
            desc.append("</b>");
        }

        return desc.toString();
    }

    public String getOrderDescription(Context context) {
        if (Book.FIELD_SORT_LTH.equals(sortBy)) {
            return context.getString(R.string.sorted_by_price_lth);
        } else {
            return context.getString(R.string.sorted_by_price_htl);
        }

    }


}

