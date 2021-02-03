package com.barszcz.server.scheduler;

import com.barszcz.server.dao.UnassignedDeviceDao;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ScheduleDelayTask {
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private UnassignedDeviceDao unassignedDeviceDao;

    public ScheduleDelayTask(UnassignedDeviceDao unassignedDeviceDao) {
        this.unassignedDeviceDao = unassignedDeviceDao;
    }

    private void delay5minutes(Runnable task) {
        executorService.schedule(task, 5, TimeUnit.MINUTES);
    }

    public void deleteUnassignedDevices(int serial) {
        Runnable task = new Runnable() {
            public void run() {
                System.out.println("run");
                unassignedDeviceDao.findAllBySerialLike(serial).ifPresent(device ->
                        unassignedDeviceDao.deleteBySerialLike(device.getSerial())
                );
            }
        };
        delay5minutes(task);
    }
}
