package de.thebotdev.rulesbot.commands.listeners;

import com.sun.management.OperatingSystemMXBean;
import de.thebotdev.rulesbot.util.RegisterListener;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.lang.management.ManagementFactory;

import static de.thebotdev.rulesbot.Main.shardManager;
import static de.thebotdev.rulesbot.Main.stats_db;

@RegisterListener
public class Grafana extends ListenerAdapter {
    public int counter = 0;
    @Override
    public void onReady(ReadyEvent event) {
        if (counter != shardManager.getShards().size() - 1) {
            counter++;
            System.out.println(counter);
        } else {
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    stats_db.setDiscordData(shardManager.getGuilds().size(), shardManager.getUsers().size(), shardManager.getAveragePing());
                    OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
                    long cpu = Math.round(operatingSystemMXBean.getSystemCpuLoad() * 100);
                    String RAM_USED_GB = humanReadableByteCount(operatingSystemMXBean.getTotalPhysicalMemorySize() - operatingSystemMXBean.getFreePhysicalMemorySize());
                    //Runtime run = Runtime.getRuntime();
                    //double usage = ((run.totalMemory() - run.freeMemory())*100.0)/run.totalMemory();
                    double ram = Double.parseDouble(RAM_USED_GB.replace("GB", ""));
                    stats_db.setServerData(cpu, ram);
                }
            }).start();
        }
    }

    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + "B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp - 1) + ("");
        return String.format("%.1f%sB", bytes / Math.pow(unit, exp), pre).replace(",", ".");
    }
}

