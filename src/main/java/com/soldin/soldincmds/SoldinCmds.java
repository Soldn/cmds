package com.soldin.soldincmds;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SoldinCmds extends JavaPlugin {

    private final List<Integer> taskIds = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadCommands();
        getLogger().info("SoldinCmds успешно включен!");
    }

    @Override
    public void onDisable() {
        cancelTasks();
        getLogger().info("SoldinCmds выключен!");
    }

    private void cancelTasks() {
        for (int id : taskIds) {
            Bukkit.getScheduler().cancelTask(id);
        }
        taskIds.clear();
    }

    private void loadCommands() {
        cancelTasks();

        List<Map<?, ?>> commands = getConfig().getMapList("commands");
        for (Map<?, ?> cmdData : commands) {
            String command = (String) cmdData.get("command");
            int interval = (int) cmdData.get("interval");

            if (command == null || interval <= 0) {
                getLogger().warning("Ошибка в конфиге: пропускаю неверную команду.");
                continue;
            }

            int taskId = new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    getLogger().info("Выполнена команда: " + command);
                }
            }.runTaskTimer(this, interval * 20L, interval * 20L).getTaskId();

            taskIds.add(taskId);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            loadCommands();
            sender.sendMessage("§aSoldinCmds: конфиг успешно перезагружен!");
            return true;
        }
        sender.sendMessage("§eИспользование: /soldincmds reload");
        return true;
    }
}
