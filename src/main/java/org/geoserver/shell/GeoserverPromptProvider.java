package org.geoserver.shell;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultPromptProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GeoserverPromptProvider extends DefaultPromptProvider {

    @Override
    public String getPrompt() {
        return "gs-shell>";
    }

    @Override
    public String name() {
        return "geoserver shell prompt provider";
    }
}
