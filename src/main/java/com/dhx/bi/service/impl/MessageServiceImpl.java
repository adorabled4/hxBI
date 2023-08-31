package com.dhx.bi.service.impl;

import cn.hutool.core.date.DateUtil;
import com.dhx.bi.common.constant.UserConstant;
import com.dhx.bi.config.MailConfig;
import com.dhx.bi.model.enums.MailEnum;
import com.dhx.bi.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author adorabled4
 * @className MessageServiceImpl
 * @date : 2023/08/26/ 12:38
 **/
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    MailConfig mailConfig;

    @Override
    public boolean sendCode(String receiver, String code, MailEnum mailEnum) {
        // 拼接内容
        String content = mailEnum.getPrefix() + code + mailEnum.getSuffix();
        // 设置信息
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailConfig.getUsername());
        message.setTo(receiver);
        message.setSubject(mailEnum.getTitle());
        message.setText(content);
        // 发送邮件
        javaMailSender.send(message);
        log.info("邮件发送成功！to：【{}】 -- 验证码：【{}】 -- {}", receiver, code, DateUtil.now());
        return true;
    }

}
