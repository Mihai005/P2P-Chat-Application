package com.example;

import com.example.command.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private final Map<String, ICommand> commands = new HashMap<>();
    private final ChatNode node;

    public Main(int port) {
        this.node = new ChatNode(port);
        registerCommands();
    }

    private void registerCommands() {
        commands.put("!hello", new HelloCommand());
        commands.put("!send", new SendCommand());
        commands.put("!bye", new ByeCommand());
        commands.put("!byebye", new ShutdownCommand());
    }

    public void start() {
        node.startServer();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Interactive Shell. Available Commands:\n"
                + commands.values().stream().map(Object::toString).collect(Collectors.joining("\n")));

        while (scanner.hasNextLine()) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String key = line.split(" ")[0];
            ICommand cmd = commands.get(key);

            if (cmd != null) {
                cmd.execute(line, node);
            } else {
                System.out.println("Unknown command");
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Wrong usage! Need to pass the port number as an argument");
            return;
        }
        try {
            int port = Integer.parseInt(args[0]);
            new Main(port).start();
        } catch (NumberFormatException e) {
            System.out.println("Port number has to be an integer");
        }
    }
}
