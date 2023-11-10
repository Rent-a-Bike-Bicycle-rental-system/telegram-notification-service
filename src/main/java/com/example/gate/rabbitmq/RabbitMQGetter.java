package com.example.gate.rabbitmq;

import com.example.data.data.Application;
import com.example.data.data.DialogText;
import com.example.data.services.BikeService;
import com.example.data.services.CityService;
import com.example.data.services.TimeService;
import com.example.gate.telegram.AdminNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMQGetter {
    private final AdminNotification notification;
    private final DialogText dialogText;
    private final TimeService timeService;
    private final BikeService bikeService;
    private final CityService cityService;

    @Autowired
    public RabbitMQGetter(AdminNotification notification, DialogText dialogText, TimeService timeService, BikeService bikeService, CityService cityService) {
        this.notification = notification;
        this.dialogText = dialogText;
        this.timeService = timeService;
        this.bikeService = bikeService;
        this.cityService = cityService;
    }

    @RabbitListener(queues = "${rabbitmq.queues.telegram}")
    public void receiveJsonMessage(String jsonMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Application application = objectMapper.readValue(jsonMessage, Application.class);

            var sb = new StringBuilder();
            String city = cityService.getCityById(application.getCity()).getCity();
            var bike = bikeService.getBikeById(application.getBikeId());

            sb.append(dialogText.getText("AdminHello"));
            sb.append("ID: ").append(application.getId()).append("\n");
            sb.append("Name: ").append(application.getName()).append("\n");
            sb.append("Phone: ").append(application.getPhone()).append("\n");
            sb.append("Email: ").append(application.getEmail()).append("\n");
            sb.append("City: ").append(city).append("\n");
            sb.append("Bike: \n").append(bike.toString("en")).append("\n");
            sb.append("Comment: ").append(application.getComment()).append("\n");
            sb.append("Application time: ").append(timeService.formatTimestampToString(application.getTimestamp())).append("\n");

            notification.sendMessageToAllAdmins(sb.toString());
        } catch (JsonProcessingException e) {
            log.error("Exception", e);
        }
    }

//    @RabbitListener(queues = "${rabbitmq.queues.telegram}")
//    public void receiveApplication(Application application) {
//        var sb = new StringBuilder();
//        String city = cityService.getCityById(application.getCity()).getCity();
//        var bike = bikeService.getBikeById(application.getBikeId());
//
//        sb.append(dialogText.getText("AdminHello"));
//        sb.append("ID: ").append(application.getId()).append("\n");
//        sb.append("Name: ").append(application.getName()).append("\n");
//        sb.append("Phone: ").append(application.getPhone()).append("\n");
//        sb.append("Email: ").append(application.getEmail()).append("\n");
//        sb.append("City: ").append(city).append("\n");
//        sb.append("Bike: \n").append(bike.toString("en")).append("\n\n");
//        sb.append("Comment: ").append(application.getComment()).append("\n");
//        sb.append("Application time: ").append(timeService.formatTimestampToString(application.getTimestamp())).append("\n");
//
//        notification.sendMessageToAllAdmins(sb.toString());
//    }
}
