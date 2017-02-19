package me.nabil.laravel.queue.common.job;

/**
 * abstract job
 *
 * @author nabilzhang
 */
public abstract class AbstractJob<T> implements Job<T> {
    /**
     * 是否已经删除
     */
    private boolean deleted = false;

    /**
     * 是否已经释放
     */
    private boolean released = false;

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean isReleased() {
        return released;
    }

    public void setReleased(boolean released) {
        this.released = released;
    }
}
