package cn.example.binapi.common.model.entity;

import lombok.Data;

@Data
public class Mail {

    private String fromMail;

    private String toMail;

    private String content;
}
