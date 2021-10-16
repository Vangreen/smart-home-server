package com.smarthome.server.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.smarthome.server.dao.UnassignedDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ScheduleDelayTask {

    @Autowired
    private ObjectMapper mapper;

    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private UnassignedDeviceRepository unassignedDeviceRepository;
    private SimpMessagingTemplate simpMessagingTemplate;

    public ScheduleDelayTask(UnassignedDeviceRepository unassignedDeviceRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.unassignedDeviceRepository = unassignedDeviceRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    private void delay5minutes(Runnable task) {
        executorService.schedule(task, 5, TimeUnit.MINUTES);
    }

    public void deleteUnassignedDevices(int serial) {
        Runnable task = () -> {
            System.out.println("run");
            unassignedDeviceRepository.findById(serial).ifPresent(device -> {
                        System.out.println("wykonano");
                        unassignedDeviceRepository.deleteById(device.getSerial());
                        simpMessagingTemplate.convertAndSend("/device/device/" + serial, responseObject("doesnt exists"));
                    }
            );

        };
        delay5minutes(task);
    }

    public ObjectNode responseObject(String response) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("response", response);
        return objectNode;
    }
}
