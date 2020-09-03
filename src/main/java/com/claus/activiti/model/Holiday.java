package com.claus.activiti.model;

import java.io.Serializable;
import java.util.Date;

public class Holiday implements Serializable {

    private Integer Id;
    private String requestName;
    private Date fromDate;
    private Date toDate;
    private Double holidayLength;
    private String reason;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Double getHolidayLength() {
        return holidayLength;
    }

    public void setHolidayLength(Double holidayLength) {
        this.holidayLength = holidayLength;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
