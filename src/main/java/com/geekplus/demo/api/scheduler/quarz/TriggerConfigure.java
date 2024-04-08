package com.geekplus.demo.api.scheduler.quarz;

import java.util.Date;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

/**
 * @author yaominhua
 * @version 1.0
 * @date 2021/10/19 13:52
 * @since athena-sprint-4c200m-4.0
 **/
@Slf4j
@Getter
@Setter
public class TriggerConfigure {

    public static final String SCHEDULE_ONCE = "SCHEDULE_ONCE";

    /**
     * trigger key
     */
    private String key;
    /**
     * trigger group
     */
    private String group = "DEFAULT";
    /**
     * corn expression
     */
    private String cron;

    private Date triggerStartTime;

    public Trigger buildTrigger() {
        if (SCHEDULE_ONCE.equals(group)) {
            if (Objects.isNull(triggerStartTime)) {
                throw new RuntimeException("triggerStartTime cannot be null when group is SCHEDULE_ONCE");
            }
            return TriggerBuilder.newTrigger()
                    .withIdentity(getTriggerKey())
                    .startAt(triggerStartTime)
                    .build();
        } else {
            return (CronTrigger)TriggerBuilder.newTrigger()
                    .withIdentity(getTriggerKey())
                    .withSchedule(CronScheduleBuilder.
                            cronSchedule(cron)
                            .withMisfireHandlingInstructionDoNothing()) //防止恢复后一股脑把之前缓存的全执行一遍
                    .build();
        }
    }

    public TriggerKey getTriggerKey() {
        return new TriggerKey(key, group);
    }

}
