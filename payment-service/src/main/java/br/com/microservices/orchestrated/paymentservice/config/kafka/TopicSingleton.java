package br.com.microservices.orchestrated.paymentservice.config.kafka;

public class TopicSingleton {

    private TopicSingleton() {}

    private static String inventoryStartTopic;
    private static String paymentFailTopic;
    private static String productValidationFailTopic;

    public static void setInventoryStartTopic(String topic) {
        inventoryStartTopic = topic;
    }

    public static void setPaymentFailTopicTopic(String topic) {
        paymentFailTopic = topic;
    }

    public static void setProductValidationFailTopic(String topic) {
        productValidationFailTopic = topic;
    }

    public static String getProductValidationFailTopic() {
        return productValidationFailTopic;
    }

    public static String getInventoryStartTopic() {
        return inventoryStartTopic;
    }

    public static String getPaymentFailTopic() {
        return paymentFailTopic;
    }
}
