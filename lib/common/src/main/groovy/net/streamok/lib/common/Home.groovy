package net.streamok.lib.common

import static org.apache.commons.lang3.SystemUtils.userHome

class Home {

    private File root

    Home(File root) {
        this.root = root
        root.mkdirs()
    }

    static Home home() {
        new Home(new File(userHome, '.streamok'))
    }

    File root() {
        root
    }

}