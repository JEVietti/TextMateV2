package com.textmate.dovaj.textmate.models;

import com.textmate.dovaj.textmate.interfaces.BaseResponse;

/**
 * Created by dovaj on 1/24/2018.
 */

public class RatingResponseSenderReceiver extends BaseResponse {

    public RatingResult sender;
    public RatingResult receiver;

    public RatingResponseSenderReceiver(RatingResult sender, RatingResult receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }
}
