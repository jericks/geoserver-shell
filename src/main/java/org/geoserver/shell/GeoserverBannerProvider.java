package org.geoserver.shell;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GeoserverBannerProvider extends DefaultBannerProvider implements CommandMarker {

    @Override
    public String getBanner() {
        StringBuilder builder = new StringBuilder();
        builder.append("   ____                                           ____  _          _ _  " + OsUtils.LINE_SEPARATOR);
        builder.append("  / ___| ___  ___  ___  ___ _ ____   _____ _ __  / ___|| |__   ___| | | " + OsUtils.LINE_SEPARATOR);
        builder.append(" | |  _ / _ \\/ _ \\/ __|/ _ \\ '__\\ \\ / / _ \\ '__| \\___ \\| '_ \\ / _ \\ | | " + OsUtils.LINE_SEPARATOR);
        builder.append(" | |_| |  __/ (_) \\__ \\  __/ |   \\ V /  __/ |     ___) | | | |  __/ | | " + OsUtils.LINE_SEPARATOR);
        builder.append("  \\____|\\___|\\___/|___/\\___|_|    \\_/ \\___|_|    |____/|_| |_|\\___|_|_| " + OsUtils.LINE_SEPARATOR);
        return builder.toString();
    }

    @Override
    public String getVersion() {
        return "0.1.0";
    }

    @Override
    public String getWelcomeMessage() {
        return "Welcome to the Geoserver Shell!";
    }

    @Override
    public String name() {
        return "geoserver shell";
    }
}
