package br.com.microservices.orchestrated.productvalidationservice.config.kafka;

public class TopicSingleton {

    private TopicSingleton() {}

    private static String paymentStartTopic;
    private static String notifyEndingTopic;
    private static String productValidationFailTopic;

    public static void setPaymentStartTopic(String topic) {
        paymentStartTopic = topic;
    }

    public static void setNotifyEndingTopic(String topic) {
        notifyEndingTopic = topic;
    }

    public static void setProductValidationFailTopic(String topic) {
        productValidationFailTopic = topic;
    }

    public static String getProductValidationFailTopic() {
        return productValidationFailTopic;
    }

    public static String getPaymentStartTopic() {
        return paymentStartTopic;
    }

    public static String getNotifyEndingTopic() {
        return notifyEndingTopic;
    }
}
