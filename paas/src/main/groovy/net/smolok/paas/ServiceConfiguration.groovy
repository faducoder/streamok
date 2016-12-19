package net.smolok.paas

class ServiceConfiguration {

    String image

    Map<String, String> environment

    ServiceConfiguration(String image, Map<String, String> environment) {
        this.image = image
        this.environment = environment
    }

    ServiceConfiguration(String image) {
        this(image, [:])
    }

    String image() {
        return image
    }

    Map<String, String> environment() {
        return environment
    }

}
