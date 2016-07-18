package com.sam_chordas.android.stockhawk.rest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by douglas on 15/05/2016.
 */
public class ChartParcelable implements Parcelable {

    private String date;
    private Float closePrice;

    public ChartParcelable(String date, Float closePrice) {
        this.closePrice = closePrice;
        this.date = date;
    }


    protected ChartParcelable(Parcel in) {
        date = in.readString();
        closePrice = in.readFloat();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeFloat(closePrice);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ChartParcelable> CREATOR = new Parcelable.Creator<ChartParcelable>() {
        @Override
        public ChartParcelable createFromParcel(Parcel in) {
            return new ChartParcelable(in);
        }

        @Override
        public ChartParcelable[] newArray(int size) {
            return new ChartParcelable[size];
        }
    };

    public String getDate() {
        return this.date;
    }

    public Float getClosePrice() {
        return this.closePrice;
    }



}


