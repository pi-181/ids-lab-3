package com.demkom58.ids_lab_2.client;

import com.demkom58.ids_lab_2.compute.Compute;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.rmi.Naming;

public class ComputePi {

    public static void main(String[] args) {
        applySecurityManager();

        try {
            String name = "rmi://localhost/Compute";
            Compute comp = (Compute) Naming.lookup(name);
            Pi task = new Pi(Integer.parseInt(args[0]));
            BigDecimal pi = (BigDecimal) comp.executeTask(task);
            System.out.println(pi);
        } catch(Exception e) {
            System.err.println("ComputePi exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void applySecurityManager() {
        System.setProperty("java.security.policy", Paths.get(".", "rmi.policy").toAbsolutePath().toString());
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
    }
}
