package br.com.microservices.orchestrated.inventoryservice.config.kafka;

public class TopicSingleton {

    private TopicSingleton() {}

    private static String inventoryFailTopic;
    private static String notifyEndingTopic;
    private static String paymentFailTopic;

    public static void setInventoryFailTopic(String topic) {
        inventoryFailTopic = topic;
    }

    public static void setNotifyEndingTopic(String topic) {
        notifyEndingTopic = topic;
    }

    public static void setPaymentFailTopic(String topic) {
        paymentFailTopic = topic;
    }

    public static String getPaymentFailTopic() {
        return paymentFailTopic;
    }

    public static String getInventoryFailTopic() {
        return inventoryFailTopic;
    }

    public static String getNotifyEndingTopic() {
        return notifyEndingTopic;
    }
}
