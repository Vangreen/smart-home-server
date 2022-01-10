package com.smarthome.server.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.smarthome.server.repository.UnassignedDeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Log4j2
public class RemoveUnassignedDevicesScheduler {

    private final UnassignedDeviceRepository unassignedDeviceRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper mapper;

    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private void delay5minutes(Runnable task) {
        executorService.schedule(task, 5, TimeUnit.MINUTES);
    }

    public void deleteUnassignedDevices(int serial) {
        Runnable task = () -> {
            log.info("run");
            unassignedDeviceRepository.findById(serial).ifPresent(device -> {
                        log.info("wykonano");
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
