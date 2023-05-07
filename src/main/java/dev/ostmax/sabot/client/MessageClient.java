package dev.ostmax.sabot.client;

public interface MessageClient {

    void sendMessage(long chatId, String text);

    void sendMessage(long chatId, String text, Object params);

}
