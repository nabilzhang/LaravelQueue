### Laravel Queue

基于Redis实现Java平台类似Php框架中Laravel中Queue的功能

#### 依赖

1. Spring Boot
2. Spring Data Jpa

#### 实现的功能

1. 任务生产者便捷的发起任务，放入队列，包括立即执行任务、延时任务
2. 任务处理消费者，只需要获取数据进行任务处理，重试、调度逻辑都无需关心。若需要自定义重试延时等，都能快速完成。
