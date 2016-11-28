package net.streamok.lib.conf

import org.apache.commons.configuration2.CompositeConfiguration
import org.apache.commons.configuration2.Configuration
import org.apache.commons.configuration2.EnvironmentConfiguration
import org.apache.commons.configuration2.SystemConfiguration

class Conf {

    private static Conf conf = new Conf()

    private final Configuration configuration

    Conf() {
        configuration = new CompositeConfiguration()
        configuration.addConfiguration(new SystemConfiguration())
        configuration.addConfiguration(new EnvironmentConfiguration())
    }

    static configuration() {
        conf
    }

    Configuration instance() {
        configuration
    }

}