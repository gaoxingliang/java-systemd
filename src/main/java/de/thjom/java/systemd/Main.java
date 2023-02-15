package de.thjom.java.systemd;

import de.thjom.java.systemd.types.*;

import java.util.*;

// systemctl show docker
public class Main {
    // args: [list|service serviceName1 serviceName2...]
    public static void main(String[] args) throws Exception {
        // list out all services on current machine
        Systemd systemd = Systemd.get();
        Manager manager = systemd.getManager();
        if (args == null || args.length == 0 || args[0].equals("list")) {
            List<UnitType> units = manager.listUnits();
            for (UnitType unit : units) {
                System.out.println("Found unit " + unit.toFormattedString());
            }
        } else if (args[0].equals("service")) {
            for (int i = 1; i < args.length; i++) {
                Service service = manager.getService(args[i]);
                System.out.println(service + " \n" + service.getActiveEnterTimestamp());
            }
        }

        Systemd.disconnectAll();
    }
}
