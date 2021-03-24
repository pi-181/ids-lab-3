package com.demkom58.ids_lab_2.client;

import com.demkom58.ids_lab_2.compute.Compute;
import com.demkom58.ids_lab_2.compute.util.StringUtil;

import java.math.BigDecimal;
import java.net.URL;
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
        final URL policyUrl = ComputePi.class.getResource("/rmi.policy");
        System.setProperty("java.security.policy", StringUtil.toPathString(policyUrl));

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
    }
}
