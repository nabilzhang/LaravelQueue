package me.nabil.pa.queue.sample;

/**
 * 示例数据<br/>
 * 注意：该类的名字千万不要改，不然会出问题，任务无法解析运行
 *
 * @author nabilzhang
 */
public class DemoData {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        id = id;
    }

    @Override
    public String toString() {
        return "DemoData{" +
                "Id=" + id +
                '}';
    }
}
