package com.nowcoder.async;

import java.util.List;

public interface EventHandle {
//    处理函数
    void doHandle(EventModel event);

//    监控的事件类型
    List<EventType> getSupportEventTypes();
}
