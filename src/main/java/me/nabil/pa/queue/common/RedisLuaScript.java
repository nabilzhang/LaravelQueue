package me.nabil.pa.queue.common;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * Redis Lua脚本
 * 用Lua脚本实现多个队列在同时操作的时候的事务性
 *
 * @author nabilzhang
 */
public class RedisLuaScript {

    /**
     * Lua脚本所在根路径
     */
    private static final String SCRIPT_ROOT = "/queue_lua_script/";

    /**
     * 合并
     * <p/>
     * Get the Lua script to migrate expired jobs back onto the queue.
     * <p/>
     * KEYS[1] - The queue we are removing jobs from, for example: queues:foo:reserved
     * KEYS[2] - The queue we are moving jobs to, for example: queues:foo
     * ARGV[1] - The current UNIX timestamp
     *
     * @return
     */
    public static RedisScript<Long> migrateExpiredJobsScript() {
        DefaultRedisScript defaultRedisScript = new DefaultRedisScript<Long>();
        defaultRedisScript.setScriptSource(new ResourceScriptSource(
                new ClassPathResource(SCRIPT_ROOT + "migrateExpiredJobsScript.lua")));
        defaultRedisScript.setResultType(Long.TYPE);
        return defaultRedisScript;
    }

    /**
     * pop script
     * <p>
     * Get the Lua script for popping the next job off of the queue.
     * <p>
     * KEYS[1] - The queue to pop jobs from, for example: queues:foo
     * KEYS[2] - The queue to place reserved jobs on, for example: queues:foo:reserved
     * ARGV[1] - The time at which the reserved job will expire
     *
     * @return 返回attempts已经加了1的job
     */
    public static RedisScript<String> popScript() {
        DefaultRedisScript<String> defaultRedisScript = new DefaultRedisScript<String>();
        defaultRedisScript.setScriptSource(new ResourceScriptSource(
                new ClassPathResource(SCRIPT_ROOT + "pop.lua")));
        defaultRedisScript.setResultType(String.class);
        return defaultRedisScript;
    }

    /**
     * Get the Lua script for releasing reserved jobs.
     * <p>
     * KEYS[1] - The "delayed" queue we release jobs onto, for example: queues:foo:delayed
     * KEYS[2] - The queue the jobs are currently on, for example: queues:foo:reserved
     * ARGV[1] - The raw payload of the job to add to the "delayed" queue
     * ARGV[2] - The UNIX timestamp at which the job should become available
     *
     * @return
     */
    public static RedisScript<Boolean> releaseScript() {
        DefaultRedisScript<Boolean> defaultRedisScript = new DefaultRedisScript<Boolean>();
        defaultRedisScript.setScriptSource(new ResourceScriptSource(
                new ClassPathResource(SCRIPT_ROOT + "release.lua")));
        defaultRedisScript.setResultType(Boolean.class);
        return defaultRedisScript;
    }
}
