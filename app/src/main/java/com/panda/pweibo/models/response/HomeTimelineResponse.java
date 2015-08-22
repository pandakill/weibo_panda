package com.panda.pweibo.models.response;

import com.panda.pweibo.models.Status;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/22:17:14.
 */
public class HomeTimelineResponse {

    private ArrayList<Status> statuses;
    private int total_number;

    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }
}
