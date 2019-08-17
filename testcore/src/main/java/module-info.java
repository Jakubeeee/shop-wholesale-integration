module testcore {
    requires common;
    requires core;

    requires static junit;
    requires testcontainers.merged;

    exports com.jakubeeee.testcore.container;
}