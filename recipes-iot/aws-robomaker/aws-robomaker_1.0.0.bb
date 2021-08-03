LICENSE = "CLOSED"

DEPENDS = "greengrass"

# Greengrass downloads the application in /home/ggc_user if the diretory exists and /tmp if not.
# Since Robomaker applications are heavy and /tmp is emptied at every boot, it is not convenient to download them at every boot.

# Note that this will not be sufficient and you will need to add permissions to the ggc_user in a different recipe to start your ROS application. Determine which common groups the user should be added to (for example: i2c, usb, etc) and add them.

do_install() {
    install -d ${D}/home/ggc_user
    chown -R ggc_user:ggc_group ${D}/home/ggc_user
}

FILES:${PN} = " \
    /home \
"
