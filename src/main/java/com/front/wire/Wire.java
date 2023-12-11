package com.front.wire;

import com.front.message.Message;

public interface Wire {

    public void put(Message message);

    public boolean hasMessage();

    public Message get();
}
