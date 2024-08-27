package org.mabr.messengerservice.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Data
@Document(indexName = "message")
public class MessageElastic {

    @Id
    @Field(type = FieldType.Integer, name = "id")
    private int id;

    @Field(type = FieldType.Text, name = "chat_id")
    private String chatId;

    @Field(type = FieldType.Date_Nanos, name = "sent_at")
    private Instant sentAt;

    @Field(type = FieldType.Text, name = "sender_user_name")
    private String senderUsername;

    @Field(type = FieldType.Text, name = "content")
    private String content;
}
