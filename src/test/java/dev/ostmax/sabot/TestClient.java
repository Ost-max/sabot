package dev.ostmax.sabot;

import dev.ostmax.sabot.client.MessageClient;
import lombok.Data;

@Data
public class TestClient implements MessageClient {

    interface SendMessageToChat {
        void perform(long chatId, String text);
    }

    interface SendMessageWithParams {
        void perform(long chatId, String text, Object params);
    }

    private SendMessageToChat sendMessage;
    private SendMessageWithParams sendMessageWithParams;


    @Override
    public void sendMessage(long chatId, String text) {
        System.out.println("to chatId: " + chatId + " text: " + text);
        sendMessage.perform(chatId, text);
    }

    @Override
    public void sendMessage(long chatId, String text, Object params) {
        System.out.println("to chatId: " + chatId + " text: " + text + " params: " + params);
        sendMessageWithParams.perform(chatId, text, params);
    }
}
