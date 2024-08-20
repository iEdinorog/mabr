package org.mabr.messengerservice.serivce;

import org.mabr.messengerservice.dto.ForwardMessageDto;
import org.mabr.messengerservice.dto.MessageDto;
import org.mabr.messengerservice.dto.ReplyMessageDto;
import org.mabr.messengerservice.dto.UpdateMessageDto;
import org.mabr.messengerservice.entity.Message;

import java.util.List;

public interface MessageService {

    void sendMessage(MessageDto messageDto);

    List<Message> getMessages(String chatId, int page, int size);

    void updateMessage(UpdateMessageDto messageDto);

    Message getMessageById(int id);

    Message replyToMessage(ReplyMessageDto dto);

    Message forwardMessage(ForwardMessageDto dto);

    void deleteMessage(int messageId);
}
