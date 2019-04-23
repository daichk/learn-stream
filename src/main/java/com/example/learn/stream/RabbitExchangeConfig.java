package com.example.learn.stream;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.stream.binder.rabbit.RabbitMessageChannelBinder;
import org.springframework.cloud.stream.binder.rabbit.config.RabbitMessageChannelBinderConfiguration;
import org.springframework.cloud.stream.binder.rabbit.config.RabbitServiceAutoConfiguration;
import org.springframework.cloud.stream.binder.rabbit.properties.RabbitBinderConfigurationProperties;
import org.springframework.cloud.stream.binder.rabbit.properties.RabbitExtendedBindingProperties;
import org.springframework.cloud.stream.binder.rabbit.provisioning.RabbitExchangeQueueProvisioner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置自定义的RabbitMq 绑定器
 * 1. 支持topic dlx
 * @author zenglw
 * @date 2019/4/22
 */
@Configuration
@ConditionalOnBean(RabbitMessageChannelBinderConfiguration.class)
public class RabbitExchangeConfig extends RabbitMessageChannelBinderConfiguration {

    @Autowired
    private ConnectionFactory rabbitConnectionFactory;

    @Autowired
    private RabbitProperties rabbitProperties;

    @Autowired
    private MessagePostProcessor gZipPostProcessor;

    @Autowired
    private MessagePostProcessor deCompressingPostProcessor;

    @Autowired
    private RabbitBinderConfigurationProperties rabbitBinderConfigurationProperties;

    @Autowired
    private RabbitExtendedBindingProperties rabbitExtendedBindingProperties;

    @Bean
    RabbitExchangeQueueProvisioner provisioningProvider() {
        return new RabbitExchangeQueueTopicDlxProvisioner(this.rabbitConnectionFactory);
    }

    @Bean
    RabbitMessageChannelBinder rabbitMessageChannelBinder() throws Exception {
        RabbitMessageChannelBinder binder = new RabbitMessageChannelBinder(this.rabbitConnectionFactory,
            this.rabbitProperties, provisioningProvider());
        binder.setAdminAddresses(this.rabbitBinderConfigurationProperties.getAdminAddresses());
        binder.setCompressingPostProcessor(gZipPostProcessor);
        binder.setDecompressingPostProcessor(deCompressingPostProcessor);
        binder.setNodes(this.rabbitBinderConfigurationProperties.getNodes());
        binder.setExtendedBindingProperties(this.rabbitExtendedBindingProperties);
        return binder;
    }

}
