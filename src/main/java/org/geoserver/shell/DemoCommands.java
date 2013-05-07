package org.geoserver.shell;

import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

@Component
public class DemoCommands implements CommandMarker {

    @CliCommand(value = "echo", help = "Echo a message")
    public String echo(@CliOption(key = {"","msg"}, mandatory = true, help = "The message to echo") String msg) {
       return msg;
    }
}
