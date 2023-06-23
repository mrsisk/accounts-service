package mrsisk.github.io.accountsmanager.config

import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RabbitMQConfig {


    companion object {
        @JvmStatic
        lateinit var internalExchange: String

        @JvmStatic
        lateinit var notificationQueue: String

        @JvmStatic
        lateinit var routingKey: String

    }

    @Value("\${rabbitmq.exchange.registration}")
    fun setInternal(ex: String){
        internalExchange = ex
    }

    @Value("\${rabbitmq.queue.notification}")
    fun setQueue(queue: String){
        notificationQueue = queue
    }

    @Value("\${rabbitmq.routing-keys.registration-notification}")
    fun setKey(key: String){
        routingKey = key
    }

    @Bean
    fun internalTopicExchange(): TopicExchange{
        return TopicExchange(internalExchange)
    }

    @Bean
    fun notificationQueue(): Queue{
        return Queue(notificationQueue)
    }

    @Bean
    fun internalToNotificationBinding(): Binding{
        return BindingBuilder.bind(notificationQueue())
            .to(internalTopicExchange())
            .with(routingKey)
    }

    @Bean
    fun messageConvertor(): MessageConverter = Jackson2JsonMessageConverter()

    @Bean
    fun mqTemplate(connectionFactory: ConnectionFactory): AmqpTemplate{
        val template = RabbitTemplate(connectionFactory)
        template.messageConverter = messageConvertor()
        return template
    }


}