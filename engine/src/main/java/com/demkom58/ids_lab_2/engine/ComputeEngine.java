package com.demkom58.ids_lab_2.engine;

import com.demkom58.ids_lab_2.compute.Compute;
import com.demkom58.ids_lab_2.compute.task.Task;
import com.demkom58.ids_lab_2.compute.util.Opt;

import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ComputeEngine extends UnicastRemoteObject implements Compute {
    private static final String SERVICE_NAME = "rmi://localhost/Compute";

    public static void main(String[] args) throws Exception {
        final int id = args.length > 0 && args[0] != null ? Integer.parseInt(args[0]) : 1;
        final String name = SERVICE_NAME + id;

        applySecurityManager();

        if (id == 1)
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

        Naming.rebind(name, new ComputeEngine());
        System.out.println("ComputeEngine bound");
    }

    protected ComputeEngine() throws RemoteException {
        super();
    }

    @Override
    public Opt<String> executeTask(Task task) throws RemoteException {
        return task.execute();
    }

    public static void applySecurityManager() {
        System.setProperty("java.security.policy", Paths.get(".",  "rmi.policy").toAbsolutePath().toString());
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new SecurityManager());
    }

}
