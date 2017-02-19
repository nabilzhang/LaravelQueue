package me.nabil.pa.queue.common.job;

/**
 * 异步任务
 *
 * @author nabilzhang
 */
public class JobMessage<T> {
    private String job;
    private T data;
    private String id;
    private int attempts;


    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    @Override
    public String toString() {
        return "JobMessage{" +
                "job='" + job + '\'' +
                ", data=" + data +
                ", id='" + id + '\'' +
                ", attempts=" + attempts +
                '}';
    }
}
